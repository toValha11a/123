package arena;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import arena.Logger;

public class TroopProportionsWindow {

    private Stage stage;
    private TextField realTotalField;
    private TextField realUnitField;
    private TextField gameTotalField;
    private TextField gameUnitField;
    private TextField resultField;

    public void show() {
        stage = new Stage();
        stage.setTitle("Расчёт пропорций войск");

        // Заголовок
        Text title = new Text("Расчёт пропорций войск");
        title.setFont(Font.font("Arial", 20));

        // Поля ввода
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        int row = 0;

        addLabelAndField(grid, row++, "Реальная общая численность войск:", realTotalField = createNumberField(1, 500000));
        addLabelAndField(grid, row++, "Реальная численность отдельного отряда:", realUnitField = createNumberField(1, 500000));
        addLabelAndField(grid, row++, "Игровая общая численность войск:", gameTotalField = createNumberField(1, 40000));
        addLabelAndField(grid, row++, "Игровая численность отдельного отряда:", gameUnitField = createNumberField(1, 40000));

        // Кнопки
        Button btnCalculate = new Button("Рассчитать");
        styleButton(btnCalculate, "#4CAF50");
        btnCalculate.setOnAction(e -> calculate());

        Button btnReset = new Button("Сбросить");
        Button btnExit = new Button("Выйти в меню");
        styleButton(btnReset, "#FF9800");
        styleButton(btnExit, "#f44336");

        // Результат
        Label resultLabel = new Label("Результат:");
        resultLabel.setFont(Font.font("Arial", 14));

        resultField = new TextField();
        resultField.setEditable(false);
        resultField.setPrefWidth(300);
        resultField.setStyle("-fx-control-inner-background: #f5f5f5;");

        HBox resultBox = new HBox(10, resultLabel, resultField);

        HBox buttonsBox = new HBox(20, btnCalculate, btnReset, btnExit);
        buttonsBox.setAlignment(Pos.CENTER);

        btnReset.setOnAction(e -> clearFields());
        btnExit.setOnAction(e -> stage.close());

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(title, grid, resultBox, buttonsBox);

        Scene scene = new Scene(layout, 650, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    private TextField createNumberField(int min, int max) {
        TextField field = new TextField();
        field.setPromptText(min + " - " + max);
        field.setPrefWidth(180);

        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                try {
                    int value = Integer.parseInt(newVal);
                    if (value < min || value > max) {
                        field.setStyle("-fx-border-color: red;");
                    } else {
                        field.setStyle("");
                    }
                } catch (NumberFormatException e) {
                    field.setStyle("-fx-border-color: red;");
                }
            } else {
                field.setStyle("");
            }
        });

        return field;
    }

    private void addLabelAndField(GridPane grid, int row, String labelText, TextField field) {
        Label label = new Label(labelText);
        label.setPrefWidth(250);
        grid.add(label, 0, row);
        grid.add(field, 1, row);
    }

    private void styleButton(Button button, String color) {
        button.setPrefWidth(150);
        button.setPrefHeight(35);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 13px;");
    }

    private void calculate() {
        Logger.Timer timer = Logger.startTimer();
        Logger.action("РАСЧЁТ_ПРОПОРЦИЙ",
                "Реальные: " + realTotalField.getText() + "/" + realUnitField.getText() +
                        ", Игровые: " + gameTotalField.getText() + "/" + gameUnitField.getText());
        // Проверяем, что заполнены все поля
        if (realTotalField.getText().isEmpty() || realUnitField.getText().isEmpty()) {
            showError("Заполните поля реальной численности!");
            return;
        }

        double realTotal = getDoubleFromField(realTotalField);
        double realUnit = getDoubleFromField(realUnitField);

        if (realTotal <= 0 || realUnit <= 0) {
            showError("Значения должны быть положительными числами!");
            return;
        }

        // Проверяем, что заполнено хотя бы одно из игровых полей
        boolean hasGameTotal = !gameTotalField.getText().isEmpty();
        boolean hasGameUnit = !gameUnitField.getText().isEmpty();

        if (!hasGameTotal && !hasGameUnit) {
            showError("Заполните хотя бы одно из игровых полей!");
            return;
        }

        if (hasGameTotal && hasGameUnit) {
            showError("Заполните только одно из игровых полей!");
            return;
        }

        double result;

        if (hasGameTotal) {
            double gameTotal = getDoubleFromField(gameTotalField);
            if (gameTotal <= 0) {
                showError("Игровая общая численность должна быть положительным числом!");
                return;
            }
            result = gameTotal * realUnit / realTotal;
            resultField.setText(String.format("%.0f", result));
        } else {
            double gameUnit = getDoubleFromField(gameUnitField);
            if (gameUnit <= 0) {
                showError("Игровая численность отряда должна быть положительным числом!");
                return;
            }
            result = gameUnit * realTotal / realUnit;
            resultField.setText(String.format("%.0f", result));
        }
        timer.logElapsed("Расчёт пропорций");
        Logger.result("Расчёт пропорций", true, "Результат: " + resultField.getText());
    }

    private double getDoubleFromField(TextField field) {
        try {
            return Double.parseDouble(field.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void clearFields() {
        realTotalField.clear();
        realUnitField.clear();
        gameTotalField.clear();
        gameUnitField.clear();
        resultField.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}