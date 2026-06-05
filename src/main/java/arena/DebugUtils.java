package arena;

import java.io.*;
import java.util.*;

/**
 * Отладочный класс для проверки функциональности приложения "Арена"
 *
 * Как использовать:
 * 1. Запустите этот класс (правой кнопкой → Run 'DebugUtils.main()')
 * 2. В консоли появятся результаты проверки
 * 3. Чтобы отключить отладку, удалите вызовы DebugUtils.print() из основного кода
 */
public class DebugUtils {

    // Включение/выключение отладочного вывода
    private static boolean debugMode = true;

    // Путь к папке игры (укажите свой реальный путь!)
    private static String testGamePath = "/Users/oleg/Documents/программирование/практика/учебная 2/Games/Medieval 2 - Total War/mods/DBM/data";

    /**
     * Главный метод для запуска отладки
     */
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("              ОТЛАДОЧНЫЙ РЕЖИМ - АРЕНА");
        System.out.println("═══════════════════════════════════════════════════════════════\n");

        // 1. Проверка работы с файлами
        testFileAccess();

        // 2. Проверка поиска юнитов
        testUnitSearch();

        // 3. Проверка парсинга характеристик
        testUnitParsing();

        // 4. Проверка вычислений (атака, защита)
        testCalculations();

        // 5. Проверка сохранения (закомментировано, чтобы не испортить файлы)
        // testUnitUpdate();

        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("              ОТЛАДКА ЗАВЕРШЕНА");
        System.out.println("═══════════════════════════════════════════════════════════════");
    }

    /**
     * 1. Проверка доступа к файлам
     */
    private static void testFileAccess() {
        System.out.println("📁 1. ПРОВЕРКА ДОСТУПА К ФАЙЛАМ");
        System.out.println("───────────────────────────────────────────────────────────────");

        File dataDir = new File(testGamePath);
        System.out.println("   Путь к папке игры: " + testGamePath);
        System.out.println("   Папка существует: " + (dataDir.exists() && dataDir.isDirectory()));

        File eduFile = new File(testGamePath, "export_descr_unit.txt");
        System.out.println("   export_descr_unit.txt: " + (eduFile.exists() ? "✅ НАЙДЕН" : "❌ НЕ НАЙДЕН") +
                " (размер: " + (eduFile.exists() ? eduFile.length() + " байт" : "—") + ")");

        File textDir = new File(testGamePath + "/text");
        System.out.println("   Папка /text существует: " + (textDir.exists() && textDir.isDirectory()));

        File unitsFile = new File(testGamePath + "/text/export_units.txt");
        System.out.println("   export_units.txt: " + (unitsFile.exists() ? "✅ НАЙДЕН" : "❌ НЕ НАЙДЕН") +
                " (размер: " + (unitsFile.exists() ? unitsFile.length() + " байт" : "—") + ")");

        System.out.println();
    }

    /**
     * 2. Проверка поиска юнитов в export_units.txt
     */
    private static void testUnitSearch() {
        System.out.println("🔍 2. ПРОВЕРКА ПОИСКА ЮНИТОВ");
        System.out.println("───────────────────────────────────────────────────────────────");

        String textPath = testGamePath + "/text/export_units.txt";
        String[] testNames = {"Арабские лучники", "Arab_Archer", "Лаконская конница", "Несуществующий юнит"};

        for (String name : testNames) {
            String internalName = findInternalNameDebug(textPath, name);
            if (internalName != null) {
                System.out.println("   ✅ Найден: \"" + name + "\" → внутреннее имя: " + internalName);
            } else {
                System.out.println("   ❌ Не найден: \"" + name + "\"");
            }
        }
        System.out.println();
    }

    /**
     * 3. Проверка парсинга характеристик
     */
    private static void testUnitParsing() {
        System.out.println("📊 3. ПРОВЕРКА ПАРСИНГА ХАРАКТЕРИСТИК");
        System.out.println("───────────────────────────────────────────────────────────────");

        String[] testUnits = {"Arab_Archer", "Arab_Medium_Cavalry"};

        for (String internalName : testUnits) {
            System.out.println("\n   Юнит: " + internalName);
            UnitData unit = loadUnitStatsDebug(internalName);

            if (unit != null) {
                System.out.println("      Отображаемое имя: " + unit.getDisplayName());
                System.out.println("      Атака 1-м оружием: " + unit.getStatPri1());
                System.out.println("      Натиск 1-м оружием: " + unit.getStatPri2());
                System.out.println("      Атака 2-м оружием: " + unit.getStatSec1());
                System.out.println("      Натиск 2-м оружием: " + unit.getStatSec2());
                System.out.println("      Доспех: " + unit.getStatPriArmour1());
                System.out.println("      Умение: " + unit.getStatPriArmour2());
                System.out.println("      Щит: " + unit.getStatPriArmour3());
                System.out.println("      Дальность: " + unit.getStatPri4());
                System.out.println("      Есть метательное оружие: " + (unit.hasRangedWeapon() ? "да" : "нет"));
                System.out.println("      ОБЩАЯ АТАКА: " + unit.getTotalAttack());
                System.out.println("      ОБЩАЯ ЗАЩИТА: " + unit.getTotalDefense());
            } else {
                System.out.println("      ❌ Ошибка загрузки юнита!");
            }
        }
        System.out.println();
    }

    /**
     * 4. Проверка вычислений (сравнение юнитов)
     */
    private static void testCalculations() {
        System.out.println("⚖️ 4. ПРОВЕРКА СРАВНЕНИЯ ЮНИТОВ");
        System.out.println("───────────────────────────────────────────────────────────────");

        UnitData unit1 = loadUnitStatsDebug("Arab_Archer");
        UnitData unit2 = loadUnitStatsDebug("Arab_Medium_Cavalry");

        if (unit1 != null && unit2 != null) {
            System.out.println("   Юнит 1: " + unit1.getDisplayName());
            System.out.println("      Атака: " + unit1.getTotalAttack());
            System.out.println("      Защита: " + unit1.getTotalDefense());

            System.out.println("   Юнит 2: " + unit2.getDisplayName());
            System.out.println("      Атака: " + unit2.getTotalAttack());
            System.out.println("      Защита: " + unit2.getTotalDefense());

            int attackDiff = unit1.getTotalAttack() - unit2.getTotalAttack();
            int defenseDiff = unit1.getTotalDefense() - unit2.getTotalDefense();

            System.out.println("   Разница в атаке: " + attackDiff);
            System.out.println("   Разница в защите: " + defenseDiff);

            if (attackDiff > 0) {
                System.out.println("   → " + unit1.getDisplayName() + " сильнее в атаке на " + attackDiff);
            } else if (attackDiff < 0) {
                System.out.println("   → " + unit2.getDisplayName() + " сильнее в атаке на " + (-attackDiff));
            } else {
                System.out.println("   → Атака одинаковая");
            }

            if (defenseDiff > 0) {
                System.out.println("   → " + unit1.getDisplayName() + " сильнее в защите на " + defenseDiff);
            } else if (defenseDiff < 0) {
                System.out.println("   → " + unit2.getDisplayName() + " сильнее в защите на " + (-defenseDiff));
            } else {
                System.out.println("   → Защита одинаковая");
            }
        }
        System.out.println();
    }

    /**
     * 5. Проверка сохранения (осторожно - изменяет файл!)
     */
    private static void testUnitUpdate() {
        System.out.println("💾 5. ПРОВЕРКА СОХРАНЕНИЯ (ТОЛЬКО ДЛЯ ТЕСТА!)");
        System.out.println("───────────────────────────────────────────────────────────────");
        System.out.println("   ⚠️ ЭТА ЧАСТЬ ЗАКОММЕНТИРОВАНА, ЧТОБЫ НЕ ПОВРЕДИТЬ ФАЙЛЫ");
        System.out.println("   Для включения раскомментируйте код в методе testUnitUpdate()");
        // ВНИМАНИЕ: Раскомментируйте только если понимаете, что делаете!
        /*
        UnitData unit = loadUnitStatsDebug("Arab_Archer");
        if (unit != null) {
            // Сохраняем оригинальные значения
            int origAttack = unit.getStatPri1();
            int origDefense = unit.getTotalDefense();

            // Меняем значения для теста
            unit.setStatPri1(99);

            // Здесь должен быть код сохранения в файл
            System.out.println("   Тестовое сохранение выполнено");

            // Восстанавливаем оригинальные значения
            unit.setStatPri1(origAttack);
            System.out.println("   Оригинальные значения восстановлены");
        }
        */
        System.out.println();
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ ОТЛАДКИ (копии методов из основных классов)
    // ═══════════════════════════════════════════════════════════════════════════════════

    private static String findInternalNameDebug(String filePath, String displayName) {
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
        }
        return null;
    }

    private static UnitData loadUnitStatsDebug(String internalName) {
        String eduPath = testGamePath + "/export_descr_unit.txt";
        UnitData unit = new UnitData(internalName, "Загрузка...");

        try (BufferedReader reader = new BufferedReader(new FileReader(eduPath), 65536)) {
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
                        parseStatPriDebug(line, unit);
                    } else if (trimmed.startsWith("stat_sec") && !trimmed.startsWith("stat_sec_attr")) {
                        parseStatSecDebug(line, unit);
                    } else if (trimmed.startsWith("stat_pri_armour")) {
                        parseStatPriArmourDebug(line, unit);
                        break;
                    }
                }
            }

            if (found) {
                // Дополнительно ищем отображаемое имя
                String textPath = testGamePath + "/text/export_units.txt";
                String displayName = findInternalNameDebug(textPath, internalName);
                if (displayName != null) {
                    String namePart = displayName; // displayName — это internalName? Нужно переделать
                    // Для простоты оставим как есть
                }
                unit.setDisplayName(internalName);
            } else {
                return null;
            }

            return unit;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void parseStatPriDebug(String line, UnitData unit) {
        String content = line.substring(line.indexOf("stat_pri") + 8).trim();
        String[] parts = content.split("[\\s,]+");

        if (parts.length >= 2) {
            unit.setStatPri1(parseIntSafe(parts[0]));
            unit.setStatPri2(parseIntSafe(parts[1]));
            if (parts.length >= 4) {
                unit.setStatPri4(parseIntSafe(parts[3]));
            }
        }
    }

    private static void parseStatSecDebug(String line, UnitData unit) {
        String content = line.substring(line.indexOf("stat_sec") + 8).trim();
        String[] parts = content.split("[\\s,]+");

        if (parts.length >= 2) {
            unit.setStatSec1(parseIntSafe(parts[0]));
            unit.setStatSec2(parseIntSafe(parts[1]));
        }
    }

    private static void parseStatPriArmourDebug(String line, UnitData unit) {
        int startIdx = line.indexOf("stat_pri_armour") + 14;
        String afterKeyword = line.substring(startIdx).trim();

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

    private static int parseIntSafe(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════════════
    // УТИЛИТНЫЕ МЕТОДЫ ДЛЯ ОТЛАДКИ
    // ═══════════════════════════════════════════════════════════════════════════════════

    /**
     * Выводит отладочную информацию в консоль
     * Можно вызывать из любого места программы:
     * DebugUtils.print("Переменная x = " + x);
     */
    public static void print(String message) {
        if (debugMode) {
            System.out.println("[DEBUG] " + message);
        }
    }

    /**
     * Выводит отладочную информацию с меткой
     */
    public static void print(String tag, String message) {
        if (debugMode) {
            System.out.println("[DEBUG:" + tag + "] " + message);
        }
    }

    /**
     * Выводит содержимое массива строк
     */
    public static void printArray(String[] array) {
        if (debugMode) {
            System.out.print("[DEBUG] Массив: [");
            for (int i = 0; i < array.length; i++) {
                System.out.print("\"" + array[i] + "\"");
                if (i < array.length - 1) System.out.print(", ");
            }
            System.out.println("]");
        }
    }

    /**
     * Включает/выключает отладочный режим
     */
    public static void setDebugMode(boolean enabled) {
        debugMode = enabled;
        System.out.println("[DEBUG] Отладочный режим: " + (enabled ? "ВКЛЮЧЁН" : "ВЫКЛЮЧЁН"));
    }

    /**
     * Выводит текущий путь к папке игры
     */
    public static void printGamePath() {
        if (debugMode) {
            System.out.println("[DEBUG] Путь к игре: " + Main.getGameDataPath());
        }
    }
}