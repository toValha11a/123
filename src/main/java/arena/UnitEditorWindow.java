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
import java.io.*;
import java.nio.file.*;
import java.util.*;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Orientation;
import javafx.scene.control.Separator;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import arena.Logger;

public class UnitEditorWindow {

    private Stage stage;
    private TextField unitNameField;
    private TextField attack1Field;
    private TextField charge1Field;
    private TextField attack2Field;
    private TextField charge2Field;
    private TextField armourField;
    private TextField shieldField;
    private TextField skillField;
    private TextField rangeField;
    private String currentInternalName;
    private String currentDisplayName;

    private VBox iconBox;
    private ImageView iconView;

    public void show() {
        stage = new Stage();
        stage.setTitle("Редактирование характеристик юнита");

        // Заголовок
        Text title = new Text("Редактирование характеристик юнита");
        title.setFont(Font.font("Arial", 20));

        // Поиск юнита
        unitNameField = new TextField();
        unitNameField.setPromptText("Наименование юнита");
        unitNameField.setPrefWidth(300);

        Button btnFind = new Button("Найти");
        btnFind.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnFind.setOnAction(e -> findUnit());

        HBox searchBox = new HBox(10, unitNameField, btnFind);
        searchBox.setAlignment(Pos.CENTER);

        // Поля ввода характеристик
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        int row = 0;

        attack1Field = createNumberField(1, 100);
        charge1Field = createNumberField(1, 100);
        attack2Field = createNumberField(1, 100);
        charge2Field = createNumberField(1, 100);
        armourField = createNumberField(1, 100);
        shieldField = createNumberField(1, 100);
        skillField = createNumberField(1, 100);
        rangeField = createNumberField(1, 400);

        addLabelAndField(grid, row++, "Атака первым оружием:", attack1Field);
        addLabelAndField(grid, row++, "Натиск первым оружием:", charge1Field);
        addLabelAndField(grid, row++, "Атака вторым оружием:", attack2Field);
        addLabelAndField(grid, row++, "Натиск вторым оружием:", charge2Field);
        addLabelAndField(grid, row++, "Уровень доспеха:", armourField);
        addLabelAndField(grid, row++, "Защита (умение):", skillField);
        addLabelAndField(grid, row++, "Уровень щита:", shieldField);
        addLabelAndField(grid, row++, "Дальность полёта снаряда:", rangeField);

        // ========== ОБЛАСТЬ ДЛЯ ИКОНКИ ==========
        iconBox = new VBox(10);          // теперь iconBox - поле класса
        iconBox.setAlignment(Pos.CENTER);
        iconBox.setPadding(new Insets(20));
        iconBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-color: #f5f5f5;");
        iconBox.setPrefWidth(120);
        iconBox.setPrefHeight(120);

        Label iconLabel = new Label("Иконка юнита");
        iconLabel.setFont(Font.font("Arial", 12));

        iconView = new ImageView();      // теперь iconView - поле класса
        iconView.setFitWidth(64);
        iconView.setFitHeight(64);
        iconView.setPreserveRatio(true);

        iconBox.getChildren().addAll(iconLabel, iconView);
        // ======================================

        // Размещаем поля и иконку в одной строке
        HBox contentBox = new HBox(20);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(grid, new Separator(Orientation.VERTICAL), iconBox);

        // Кнопки
        Button btnSave = new Button("Сохранить");
        Button btnReset = new Button("Сбросить");
        Button btnExit = new Button("Выйти в меню");

        styleButton(btnSave, "#4CAF50");
        styleButton(btnReset, "#FF9800");
        styleButton(btnExit, "#f44336");

        btnSave.setOnAction(e -> saveChanges());
        btnReset.setOnAction(e -> clearFields());
        btnExit.setOnAction(e -> stage.close());

        HBox buttonsBox = new HBox(20, btnSave, btnReset, btnExit);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(title, searchBox, contentBox, buttonsBox);

        Scene scene = new Scene(layout, 850, 550);
        stage.setScene(scene);
        stage.show();
    }

    private TextField createNumberField(int min, int max) {
        TextField field = new TextField();
        field.setPromptText(min + "-" + max);
        field.setPrefWidth(120);

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
        label.setPrefWidth(180);
        grid.add(label, 0, row);
        grid.add(field, 1, row);
    }

    private void styleButton(Button button, String color) {
        button.setPrefWidth(120);
        button.setPrefHeight(35);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 13px;");
        button.setCursor(javafx.scene.Cursor.HAND);
    }

    private void findUnit() {
        Logger.Timer timer = Logger.startTimer();

        String displayName = unitNameField.getText().trim();
        Logger.action("ПОИСК_ЮНИТА", "Имя: " + displayName);

        if (displayName.isEmpty()) {
            Logger.warning("UnitEditorWindow", "findUnit", "Пустое имя юнита");
            showError("Введите наименование юнита!");
            return;
        }

        String dataPath = Main.getGameDataPath();
        if (dataPath.isEmpty()) {
            Logger.warning("UnitEditorWindow", "findUnit", "Путь к игре не установлен");
            showError("Сначала укажите путь к корневой папке игры в главном меню!");
            return;
        }

        String textPath = dataPath + "/text/export_units.txt";
        Logger.debug("UnitEditorWindow", "findUnit", "Поиск в файле: " + textPath);

        String internalName = findInternalName(textPath, displayName);
        if (internalName == null) {
            Logger.warning("UnitEditorWindow", "findUnit", "Юнит не найден: " + displayName);
            Logger.result("Поиск юнита", false, displayName);
            showError("Юнит \"" + displayName + "\" не найден в файле export_units.txt!");
            return;
        }

        currentInternalName = internalName;
        currentDisplayName = displayName;

        // ЗАГРУЖАЕМ ИКОНКУ
        loadUnitIcon(internalName);

        Logger.info("UnitEditorWindow", "findUnit", "Найден юнит: " + internalName + " (" + displayName + ")");

        String eduPath = dataPath + "/export_descr_unit.txt";
        loadUnitStats(eduPath, internalName);

        timer.logElapsed("Поиск юнита " + displayName);
        Logger.result("Поиск юнита", true, displayName + " → " + internalName);
    }

    private String findInternalName(String filePath, String displayName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath), 65536)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("{")) {
                    int start = line.indexOf('{');
                    int end = line.indexOf('}');
                    if (start >= 0 && end > start) {
                        String internal = line.substring(start + 1, end);
                        String namePart = line.substring(end + 1);
                        if (namePart.contains(displayName)) {
                            return internal;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка при чтении файла export_units.txt!");
        }
        return null;
    }

    private void loadUnitStats(String filePath, String internalName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath), 65536)) {
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                if (!found && line.trim().startsWith("dictionary") && line.contains(internalName)) {
                    found = true;
                    continue;
                }

                if (found) {
                    String trimmed = line.trim();

                    if (trimmed.startsWith("stat_pri") && !trimmed.startsWith("stat_pri_attr") && !trimmed.startsWith("stat_pri_armour")) {
                        parseStatPri(line);
                    } else if (trimmed.startsWith("stat_sec") && !trimmed.startsWith("stat_sec_attr")) {
                        parseStatSec(line);
                    } else if (trimmed.startsWith("stat_pri_armour")) {
                        parseStatPriArmour(line);
                        break;
                    }
                }
            }

            if (!found) {
                showError("Юнит не найден в файле export_descr_unit.txt!");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка при чтении файла!");
        }
    }

    private void parseStatPri(String line) {
        // Извлекаем содержимое после stat_pri
        String content = line.substring(line.indexOf("stat_pri") + 8).trim();

        // Разбиваем по пробелам и запятым
        String[] parts = content.split("[\\s,]+");

        if (parts.length >= 2) {
            attack1Field.setText(parts[0]);
            charge1Field.setText(parts[1]);
            if (parts.length >= 4) {
                rangeField.setText(parts[3]);
            }
        }
    }

    private void parseStatSec(String line) {
        String content = line.substring(line.indexOf("stat_sec") + 8).trim();
        String[] parts = content.split("[\\s,]+");

        if (parts.length >= 2) {
            attack2Field.setText(parts[0]);
            charge2Field.setText(parts[1]);
        }
    }

    private void parseStatPriArmour(String line) {
        // Находим позицию после "stat_pri_armour"
        int startIdx = line.indexOf("stat_pri_armour") + 14;
        String afterKeyword = line.substring(startIdx).trim();

        // Ищем первую цифру в строке
        int firstDigit = -1;
        for (int i = 0; i < afterKeyword.length(); i++) {
            if (Character.isDigit(afterKeyword.charAt(i))) {
                firstDigit = i;
                break;
            }
        }

        if (firstDigit >= 0) {
            String numbersPart = afterKeyword.substring(firstDigit);
            String[] parts = numbersPart.split("[\\s,]+");

            if (parts.length >= 3) {
                armourField.setText(parts[0]);
                skillField.setText(parts[1]);
                shieldField.setText(parts[2]);
            }
        }
    }

    private void saveChanges() {
        Logger.Timer timer = Logger.startTimer();
        Logger.action("СОХРАНЕНИЕ", "Юнит: " + currentDisplayName);

        if (currentInternalName == null) {
            Logger.warning("UnitEditorWindow", "saveChanges", "Попытка сохранить без найденного юнита");
            showError("Сначала найдите юнита!");
            return;
        }

        int range = getIntFromField(rangeField);
        if (range == 0) {
            Logger.warning("UnitEditorWindow", "saveChanges", "Попытка изменить дальность у юнита без метательного оружия");
            showError("Данный юнит не имеет метательного оружия!");
            return;
        }

        if (!validateFields()) {
            Logger.warning("UnitEditorWindow", "saveChanges", "Валидация полей не пройдена");
            return;
        }

        String dataPath = Main.getGameDataPath();
        String eduPath = dataPath + "/export_descr_unit.txt";

        try {
            updateUnitInFile(eduPath);
            Logger.info("UnitEditorWindow", "saveChanges", "Характеристики сохранены для юнита: " + currentInternalName);
            timer.logElapsed("Сохранение характеристик юнита " + currentDisplayName);
            Logger.result("Сохранение характеристик", true, currentInternalName);
            showSuccess("Характеристики успешно сохранены!");
        } catch (IOException e) {
            Logger.error("UnitEditorWindow", "saveChanges", "Ошибка сохранения", e);
            Logger.result("Сохранение характеристик", false, currentInternalName + " - " + e.getMessage());
            showError("Ошибка при сохранении файла!");
        }
    }

    private void updateUnitInFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        boolean inTargetUnit = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!inTargetUnit && line.trim().startsWith("dictionary") && line.contains(currentInternalName)) {
                    inTargetUnit = true;
                }

                if (inTargetUnit && line.trim().startsWith("stat_pri") && !line.trim().startsWith("stat_pri_attr") && !line.trim().startsWith("stat_pri_armour")) {
                    line = updateStatPri(line);
                } else if (inTargetUnit && line.trim().startsWith("stat_sec") && !line.trim().startsWith("stat_sec_attr")) {
                    line = updateStatSec(line);
                } else if (inTargetUnit && line.trim().startsWith("stat_pri_armour")) {
                    line = updateStatPriArmour(line);
                    inTargetUnit = false;
                }

                lines.add(line);
            }
        }

        Files.write(Paths.get(filePath), lines);
    }

    private String updateStatPri(String line) {
        String beforeStat = line.substring(0, line.indexOf("stat_pri"));
        String newValues = getIntFromField(attack1Field) + ", " +
                getIntFromField(charge1Field) + ", ShortBowArrow, " +
                getIntFromField(rangeField) + ", 25, missile, missile_mechanical, piercing, none, 30, 1";

        return beforeStat + "stat_pri                " + newValues;
    }

    private String updateStatSec(String line) {
        String beforeStat = line.substring(0, line.indexOf("stat_sec"));
        String newValues = getIntFromField(attack2Field) + ", " +
                getIntFromField(charge2Field) + ", no, 0, 0, melee, melee_simple, piercing, knife, 25, 1";

        return beforeStat + "stat_sec                " + newValues;
    }

    private String updateStatPriArmour(String line) {
        String beforeStat = line.substring(0, line.indexOf("stat_pri_armour"));
        String newValues = getIntFromField(armourField) + ", " +
                getIntFromField(skillField) + ", " +
                getIntFromField(shieldField) + ", flesh";

        return beforeStat + "stat_pri_armour         " + newValues;
    }

    private int getIntFromField(TextField field) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean validateFields() {
        return validateField(attack1Field, 1, 100, "Атака первым оружием") &&
                validateField(charge1Field, 1, 100, "Натиск первым оружием") &&
                validateField(attack2Field, 1, 100, "Атака вторым оружием") &&
                validateField(charge2Field, 1, 100, "Натиск вторым оружием") &&
                validateField(armourField, 1, 100, "Уровень доспеха") &&
                validateField(shieldField, 1, 100, "Уровень щита") &&
                validateField(skillField, 1, 100, "Защита (умение)") &&
                validateField(rangeField, 1, 400, "Дальность полёта снаряда");
    }

    private boolean validateField(TextField field, int min, int max, String fieldName) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            showError(fieldName + " не может быть пустым!");
            return false;
        }
        try {
            int value = Integer.parseInt(text);
            if (value < min || value > max) {
                showError(fieldName + " должно быть в диапазоне от " + min + " до " + max + "!");
                return false;
            }
        } catch (NumberFormatException e) {
            showError(fieldName + " должно быть числом!");
            return false;
        }
        return true;
    }

    private void clearFields() {
        attack1Field.clear();
        charge1Field.clear();
        attack2Field.clear();
        charge2Field.clear();
        armourField.clear();
        shieldField.clear();
        skillField.clear();
        rangeField.clear();
        unitNameField.clear();
        currentInternalName = null;
        currentDisplayName = null;
        // Сбрасываем иконку
        showPlaceholderIcon();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Загружает TGA иконку юнита через TwelveMonkeys ImageIO
     */
    private void loadUnitIcon(String internalName) {
        String dataPath = Main.getGameDataPath();
        if (dataPath == null || dataPath.isEmpty()) {
            Logger.debug("UnitEditorWindow", "loadUnitIcon", "Путь к игре не установлен");
            showPlaceholderIcon();
            return;
        }

        // Из пути .../data убираем /data в конце
        String basePath = dataPath;
        if (basePath.endsWith("/data")) {
            basePath = basePath.substring(0, basePath.length() - 5);
        }

        // Путь к иконке
        String iconPath = basePath + "/ui/units/mercs/#" + internalName + ".tga";

        Logger.debug("UnitEditorWindow", "loadUnitIcon", "Загрузка иконки: " + iconPath);

        File tgaFile = new File(iconPath);
        if (!tgaFile.exists()) {
            Logger.warning("UnitEditorWindow", "loadUnitIcon", "Иконка не найдена: " + iconPath);
            showPlaceholderIcon();
            return;
        }

        try {
            // TwelveMonkeys автоматически регистрирует TGA формат
            BufferedImage bufferedImage = ImageIO.read(tgaFile);

            if (bufferedImage != null) {
                // Конвертируем в JavaFX Image
                javafx.scene.image.Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);

                // Используем сохранённую ссылку на iconView
                iconView.setImage(fxImage);
                iconView.setFitWidth(64);
                iconView.setFitHeight(64);
                iconView.setPreserveRatio(true);

                Logger.info("UnitEditorWindow", "loadUnitIcon", "Иконка загружена: " + internalName);
            } else {
                Logger.warning("UnitEditorWindow", "loadUnitIcon", "Не удалось прочитать TGA файл: " + iconPath);
                showPlaceholderIcon();
            }

        } catch (Exception e) {
            Logger.error("UnitEditorWindow", "loadUnitIcon", "Ошибка загрузки иконки", e);
            showPlaceholderIcon();
        }
    }

    /**
     * Показывает заглушку (серый квадрат) если иконка не найдена
     */
    private void showPlaceholderIcon() {
        if (iconView != null) {
            iconView.setImage(null);
            iconView.setStyle("-fx-background-color: #cccccc;");
        }
    }
}