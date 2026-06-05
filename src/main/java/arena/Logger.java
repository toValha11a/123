package arena;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Класс для логирования действий приложения "Арена"
 *
 * Лог-файл: arena.log (перезаписывается при каждом запуске)
 */
public class Logger {

    // Имя лог-файла (фиксированное, перезаписывается при каждом запуске)
    private static final String LOG_FILE_NAME = "arena.log";

    // Путь к папке с логами
    private static String logDirectory = "";

    // Полный путь к лог-файлу
    private static String currentLogFile = "";

    // Флаг включения подробного логирования
    private static boolean debugMode = false;

    // Формат даты и времени для логов
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Инициализация логгера (вызывается при старте приложения)
     * ПРИ КАЖДОМ ЗАПУСКЕ ПРЕДЫДУЩИЙ ЛОГ УДАЛЯЕТСЯ
     * @param basePath - базовый путь к папке DBM
     */
    public static void init(String basePath) {
        try {
            // Создаём папку для логов
            logDirectory = basePath + "/logs/";
            File logDir = new File(logDirectory);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            // Полный путь к лог-файлу
            currentLogFile = logDirectory + LOG_FILE_NAME;

            // Удаляем старый лог-файл, если он существует
            File oldLog = new File(currentLogFile);
            if (oldLog.exists()) {
                oldLog.delete();
                System.out.println("[ЛОГ] Предыдущий лог-файл удалён: " + currentLogFile);
            }

            // Записываем первую запись (создаётся новый файл)
            info("═══════════════════════════════════════════════════════════════");
            info("              ЗАПУСК ПРИЛОЖЕНИЯ \"АРЕНА\"");
            info("═══════════════════════════════════════════════════════════════");
            info("Версия: 1.0");
            info("Папка логов: " + logDirectory);
            info("Лог-файл: " + currentLogFile);
            info("Путь к игре: " + (Main.getGameDataPath().isEmpty() ? "не указан" : Main.getGameDataPath()));
            info("");

        } catch (Exception e) {
            System.err.println("Ошибка при инициализации логгера: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Перезапись существующего лог-файла (принудительное создание нового)
     */
    public static void resetLog() {
        if (!currentLogFile.isEmpty()) {
            File logFile = new File(currentLogFile);
            if (logFile.exists()) {
                logFile.delete();
            }
            info("═══════════════════════════════════════════════════════════════");
            info("              ПЕРЕЗАПУСК ЛОГИРОВАНИЯ");
            info("═══════════════════════════════════════════════════════════════");
        }
    }

    /**
     * Установка пути к папке логов
     */
    public static void setLogDirectory(String path) {
        logDirectory = path;
        if (!logDirectory.endsWith("/")) {
            logDirectory += "/";
        }
        logDirectory += "logs/";

        // Создаём папку, если её нет
        File dir = new File(logDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        currentLogFile = logDirectory + LOG_FILE_NAME;
    }

    /**
     * Включение/выключение подробного режима
     */
    public static void setDebugMode(boolean enabled) {
        debugMode = enabled;
        info("Режим отладки: " + (enabled ? "ВКЛЮЧЁН" : "ВЫКЛЮЧЁН"));
    }

    /**
     * Информационное сообщение
     */
    public static void info(String message) {
        log("INFO", message);
    }

    /**
     * Информационное сообщение с указанием класса и метода
     */
    public static void info(String className, String methodName, String message) {
        log("INFO", "[" + className + "." + methodName + "] " + message);
    }

    /**
     * Предупреждение
     */
    public static void warning(String message) {
        log("WARNING", message);
    }

    /**
     * Предупреждение с указанием класса и метода
     */
    public static void warning(String className, String methodName, String message) {
        log("WARNING", "[" + className + "." + methodName + "] " + message);
    }

    /**
     * Ошибка
     */
    public static void error(String message, String compareUnits, String ошибкаЗагрузкиХарактеристик) {
        log("ERROR", message);
    }

    /**
     * Ошибка с исключением
     */
    public static void error(String message, Exception e) {
        log("ERROR", message + " - " + e.getMessage());
        if (debugMode) {
            log("ERROR", "Стек вызовов:");
            for (StackTraceElement element : e.getStackTrace()) {
                log("ERROR", "  at " + element.toString());
            }
        }
    }

    /**
     * Ошибка с указанием класса и метода
     */
    public static void error(String className, String methodName, String message, Exception e) {
        log("ERROR", "[" + className + "." + methodName + "] " + message + " - " + e.getMessage());
        if (debugMode) {
            for (StackTraceElement element : e.getStackTrace()) {
                log("ERROR", "  at " + element.toString());
            }
        }
    }

    /**
     * Отладочное сообщение (только если включён debugMode)
     */
    public static void debug(String message) {
        if (debugMode) {
            log("DEBUG", message);
        }
    }

    /**
     * Отладочное сообщение с указанием класса и метода
     */
    public static void debug(String className, String methodName, String message) {
        if (debugMode) {
            log("DEBUG", "[" + className + "." + methodName + "] " + message);
        }
    }

    /**
     * Логирование действия пользователя
     */
    public static void action(String actionName, String details) {
        log("ACTION", actionName + " - " + details);
    }

    /**
     * Логирование результата операции
     */
    public static void result(String operation, boolean success, String details) {
        if (success) {
            log("RESULT", "✅ " + operation + " - УСПЕШНО - " + details);
        } else {
            log("RESULT", "❌ " + operation + " - ОШИБКА - " + details);
        }
    }

    /**
     * Основной метод записи в лог
     */
    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(DATE_FORMAT);
        String logEntry = timestamp + " [" + level + "] " + message;

        // Выводим в консоль
        if (level.equals("ERROR")) {
            System.err.println(logEntry);
        } else if (level.equals("WARNING")) {
            System.out.println("\u001B[33m" + logEntry + "\u001B[0m"); // Жёлтый цвет
        } else {
            System.out.println(logEntry);
        }

        // Записываем в файл (добавляем в конец, но файл уже очищен при запуске)
        if (!currentLogFile.isEmpty()) {
            try (FileWriter fw = new FileWriter(currentLogFile, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(logEntry);
            } catch (IOException e) {
                System.err.println("Не удалось записать в лог-файл: " + e.getMessage());
            }
        }
    }

    /**
     * Получить путь к текущему лог-файлу
     */
    public static String getCurrentLogFile() {
        return currentLogFile;
    }

    /**
     * Закрытие логгера (вызывается при завершении приложения)
     */
    public static void close() {
        info("═══════════════════════════════════════════════════════════════");
        info("              ЗАВЕРШЕНИЕ РАБОТЫ ПРИЛОЖЕНИЯ");
        info("═══════════════════════════════════════════════════════════════");
        info("Лог-файл: " + currentLogFile);
    }

    /**
     * Замер времени выполнения операции
     */
    public static Timer startTimer() {
        return new Timer();
    }

    /**
     * Вспомогательный класс для замера времени
     */
    public static class Timer {
        private long startTime;

        public Timer() {
            startTime = System.currentTimeMillis();
        }

        public long getElapsed() {
            return System.currentTimeMillis() - startTime;
        }

        public void logElapsed(String operation) {
            long elapsed = getElapsed();
            info(operation + " выполнено за " + elapsed + " мс");
        }
    }
}