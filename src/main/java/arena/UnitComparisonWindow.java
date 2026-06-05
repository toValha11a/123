package arena;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.*;

import arena.Logger;

public class UnitComparisonWindow {

    private Stage stage;
    private TextField unit1Field;
    private TextField unit2Field;
    private TextArea resultArea;

    public void show() {
        stage = new Stage();
        stage.setTitle("Сравнение юнитов");

        // Заголовок
        Text title = new Text("Сравнение юнитов");
        title.setFont(Font.font("Arial", 20));

        // Поля ввода названий юнитов
        unit1Field = new TextField();
        unit1Field.setPromptText("Наименование юнита 1");
        unit1Field.setPrefWidth(250);

        unit2Field = new TextField();
        unit2Field.setPromptText("Наименование юнита 2");
        unit2Field.setPrefWidth(250);

        Button btnCompare = new Button("Сравнить");
        btnCompare.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnCompare.setOnAction(e -> compareUnits());

        VBox inputsBox = new VBox(10, unit1Field, unit2Field, btnCompare);
        inputsBox.setAlignment(Pos.CENTER);

        // Результат
        Label resultLabel = new Label("Результат:");
        resultLabel.setFont(Font.font("Arial", 14));

        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(150);
        resultArea.setPrefWidth(400);
        resultArea.setStyle("-fx-control-inner-background: #f5f5f5;");

        // Кнопки
        Button btnReset = new Button("Сбросить");
        Button btnExit = new Button("Выйти в меню");

        styleButton(btnReset, "#FF9800");
        styleButton(btnExit, "#f44336");

        btnReset.setOnAction(e -> clearFields());
        btnExit.setOnAction(e -> stage.close());

        HBox buttonsBox = new HBox(20, btnReset, btnExit);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox resultBox = new VBox(10, resultLabel, resultArea, buttonsBox);
        resultBox.setAlignment(Pos.CENTER);

        HBox mainLayout = new HBox(40, inputsBox, resultBox);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(30));

        VBox layout = new VBox(20, title, mainLayout);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 700, 400);
        stage.setScene(scene);
        stage.show();
    }

    private void styleButton(Button button, String color) {
        button.setPrefWidth(120);
        button.setPrefHeight(35);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 13px;");
        button.setCursor(javafx.scene.Cursor.HAND);
    }

    private void compareUnits() {
        Logger.Timer timer = Logger.startTimer();

        String name1 = unit1Field.getText().trim();
        String name2 = unit2Field.getText().trim();

        Logger.action("СРАВНЕНИЕ_ЮНИТОВ", "Юнит 1: " + name1 + ", Юнит 2: " + name2);

        if (name1.isEmpty() || name2.isEmpty()) {
            Logger.warning("UnitComparisonWindow", "compareUnits", "Одно из имён пустое");
            showError("Введите названия обоих юнитов!");
            return;
        }

        String dataPath = Main.getGameDataPath();
        if (dataPath.isEmpty()) {
            Logger.warning("UnitComparisonWindow", "compareUnits", "Путь к игре не установлен");
            showError("Сначала укажите путь к корневой папке игры в главном меню!");
            return;
        }

        String textPath = dataPath + "/text/export_units.txt";

        String[] result1 = findInternalNameExact(textPath, name1);
        String[] result2 = findInternalNameExact(textPath, name2);

        if (result1 == null) {
            Logger.warning("UnitComparisonWindow", "compareUnits", "Юнит 1 не найден: " + name1);
            showError("Юнит \"" + name1 + "\" не найден в файле export_units.txt!");
            return;
        }
        if (result2 == null) {
            Logger.warning("UnitComparisonWindow", "compareUnits", "Юнит 2 не найден: " + name2);
            showError("Юнит \"" + name2 + "\" не найден в файле export_units.txt!");
            return;
        }

        String internal1 = result1[0];
        String displayName1 = result1[1];
        String internal2 = result2[0];
        String displayName2 = result2[1];

        Logger.debug("UnitComparisonWindow", "compareUnits", "Юнит 1: " + internal1 + " (" + displayName1 + ")");
        Logger.debug("UnitComparisonWindow", "compareUnits", "Юнит 2: " + internal2 + " (" + displayName2 + ")");

        String eduPath = dataPath + "/export_descr_unit.txt";
        UnitData unit1 = loadUnitStats(eduPath, internal1, displayName1);
        UnitData unit2 = loadUnitStats(eduPath, internal2, displayName2);

        if (unit1 == null || unit2 == null) {
            Logger.error("UnitComparisonWindow", "compareUnits", "Ошибка загрузки характеристик");
            showError("Ошибка при загрузке характеристик юнитов!");
            return;
        }

        int attack1 = unit1.getStatPri1() + unit1.getStatPri2() + unit1.getStatSec1() + unit1.getStatSec2();
        int attack2 = unit2.getStatPri1() + unit2.getStatPri2() + unit2.getStatSec1() + unit2.getStatSec2();
        int defense1 = unit1.getStatPriArmour1() + unit1.getStatPriArmour2() + unit1.getStatPriArmour3();
        int defense2 = unit2.getStatPriArmour1() + unit2.getStatPriArmour2() + unit2.getStatPriArmour3();

        Logger.debug("UnitComparisonWindow", "compareUnits",
                "Атака: " + attack1 + " vs " + attack2 + ", Защита: " + defense1 + " vs " + defense2);

        int attackDiff = attack1 - attack2;
        int defenseDiff = defense1 - defense2;

        StringBuilder result = new StringBuilder();

        if (attackDiff > 0) {
            result.append("Юнит \"").append(unit1.getDisplayName())
                    .append("\" имеет лучшую атаку на ").append(attackDiff).append(" единиц\n");
        } else if (attackDiff < 0) {
            result.append("Юнит \"").append(unit2.getDisplayName())
                    .append("\" имеет лучшую атаку на ").append(-attackDiff).append(" единиц\n");
        } else {
            result.append("Юниты имеют одинаковую атаку (").append(attack1).append(")\n");
        }

        if (defenseDiff > 0) {
            result.append("Юнит \"").append(unit1.getDisplayName())
                    .append("\" имеет лучшую защиту на ").append(defenseDiff).append(" единиц");
        } else if (defenseDiff < 0) {
            result.append("Юнит \"").append(unit2.getDisplayName())
                    .append("\" имеет лучшую защиту на ").append(-defenseDiff).append(" единиц");
        } else {
            result.append("Юниты имеют одинаковую защиту (").append(defense1).append(")");
        }

        resultArea.setText(result.toString());
        timer.logElapsed("Сравнение юнитов " + name1 + " и " + name2);
    }

    // Точный поиск по полному названию
    private String[] findInternalNameExact(String filePath, String displayName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath), 65536)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("{")) {
                    int start = line.indexOf('{');
                    int end = line.indexOf('}');
                    if (start >= 0 && end > start) {
                        String internal = line.substring(start + 1, end);
                        String namePart = line.substring(end + 1);
                        // Точное совпадение: проверяем, что название целиком содержится в строке
                        if (namePart.equals(displayName) || namePart.startsWith(displayName + " ") ||
                                namePart.contains(" " + displayName + " ") || namePart.endsWith(" " + displayName) ||
                                namePart.equals(displayName)) {
                            return new String[]{internal, namePart.trim()};
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private UnitData loadUnitStats(String filePath, String internalName, String displayName) {
        UnitData unit = new UnitData(internalName, displayName);

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
                        parseStatPri(line, unit);
                    } else if (trimmed.startsWith("stat_sec") && !trimmed.startsWith("stat_sec_attr")) {
                        parseStatSec(line, unit);
                    } else if (trimmed.startsWith("stat_pri_armour")) {
                        parseStatPriArmour(line, unit);
                        break;
                    }
                }
            }

            return unit;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void parseStatPri(String line, UnitData unit) {
        // Извлекаем содержимое после stat_pri
        String content = line.substring(line.indexOf("stat_pri") + 8).trim();
        // Разбиваем по пробелам и запятым
        String[] parts = content.split("[\\s,]+");

        if (parts.length >= 2) {
            unit.setStatPri1(parseIntSafe(parts[0]));
            unit.setStatPri2(parseIntSafe(parts[1]));
        }
    }

    private void parseStatSec(String line, UnitData unit) {
        String content = line.substring(line.indexOf("stat_sec") + 8).trim();
        String[] parts = content.split("[\\s,]+");

        if (parts.length >= 2) {
            unit.setStatSec1(parseIntSafe(parts[0]));
            unit.setStatSec2(parseIntSafe(parts[1]));
        }
    }

    private void parseStatPriArmour(String line, UnitData unit) {
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
                unit.setStatPriArmour1(parseIntSafe(parts[0]));
                unit.setStatPriArmour2(parseIntSafe(parts[1]));
                unit.setStatPriArmour3(parseIntSafe(parts[2]));
            }
        }
    }

    private int parseIntSafe(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void clearFields() {
        unit1Field.clear();
        unit2Field.clear();
        resultArea.clear();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}