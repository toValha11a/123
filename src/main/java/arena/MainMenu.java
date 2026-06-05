package arena;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.File;

import arena.Logger;

public class MainMenu {

    private Stage stage;
    private TextField pathField;

    public void show(Stage primaryStage) {
        stage = primaryStage;

        // Заголовок
        Text title = new Text("Арена");
        title.setFont(Font.font("Arial", 24));

        // Строка ввода пути
        pathField = new TextField();
        pathField.setPromptText("Путь к корневой папке игры");
        pathField.setPrefWidth(400);

        Button btnFind = new Button("Найти");
        btnFind.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnFind.setOnAction(e -> findGamePath());

        HBox pathBox = new HBox(10, pathField, btnFind);
        pathBox.setAlignment(Pos.CENTER);

        // Кнопки меню
        Button btnUnitEditor = createMenuButton("Изменение характеристик");
        Button btnUnitComparison = createMenuButton("Сравнение юнитов");
        Button btnTroopProportions = createMenuButton("Расчёт пропорций войск");

        btnUnitEditor.setOnAction(e -> openUnitEditor());
        btnUnitComparison.setOnAction(e -> openUnitComparison());
        btnTroopProportions.setOnAction(e -> openTroopProportions());

        HBox buttonsBox = new HBox(20, btnUnitEditor, btnUnitComparison, btnTroopProportions);
        buttonsBox.setAlignment(Pos.CENTER);

        // Основной layout
        VBox layout = new VBox(30);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(title, pathBox, buttonsBox);

        Scene scene = new Scene(layout, 800, 300);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setTitle("Арена - Главное меню");
        stage.setScene(scene);
        stage.show();
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefHeight(50);
        button.setPrefWidth(220);
        button.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;");

        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-size: 14px;")
        );
        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px;")
        );

        return button;
    }

    private void findGamePath() {
        Logger.action("ПОИСК_ПУТИ", "Введён путь: " + pathField.getText().trim());

        String path = pathField.getText().trim();
        if (path.isEmpty()) {
            Logger.warning("MainMenu", "findGamePath", "Попытка ввода пустого пути");
            showError("Введите корректный адрес корневой папки игры!");
            return;
        }

        File dataDir = new File(path);
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            Logger.warning("MainMenu", "findGamePath", "Папка не существует: " + path);
            showError("Введите корректный адрес корневой папки игры!");
            return;
        }

        File eduFile = new File(path, "export_descr_unit.txt");
        if (!eduFile.exists()) {
            Logger.warning("MainMenu", "findGamePath", "Файл export_descr_unit.txt не найден в " + path);
            showError("В папке не найден файл export_descr_unit.txt!");
            return;
        }

        Main.setGameDataPath(path);
        Logger.info("MainMenu", "findGamePath", "Путь успешно установлен: " + path);
        Logger.result("Установка пути к игре", true, path);
        showSuccess("Путь успешно установлен!");
    }

    private void openUnitEditor() {
        Logger.action("ОТКРЫТИЕ_ОКНА", "Редактирование характеристик юнита");
        if (Main.getGameDataPath().isEmpty()) {
            Logger.warning("MainMenu", "openUnitEditor", "Попытка открыть окно без указания пути к игре");
            showError("Сначала укажите путь к корневой папке игры!");
            return;
        }
        UnitEditorWindow window = new UnitEditorWindow();
        window.show();
    }

    private void openUnitComparison() {
        Logger.action("ОТКРЫТИЕ_ОКНА", "Сравнение юнитов");
        if (Main.getGameDataPath().isEmpty()) {
            Logger.warning("MainMenu", "openUnitComparison", "Попытка открыть окно без указания пути к игре");
            showError("Сначала укажите путь к корневой папке игры!");
            return;
        }
        UnitComparisonWindow window = new UnitComparisonWindow();
        window.show();
    }

    private void openTroopProportions() {
        Logger.action("ОТКРЫТИЕ_ОКНА", "Расчёт пропорций войск");
        TroopProportionsWindow window = new TroopProportionsWindow();
        window.show();
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}