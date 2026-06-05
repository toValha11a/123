package arena;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    private static String gameDataPath = "";

    // Базовый путь к папке DBM (для логов)
    private static String basePath = "/Users/oleg/Documents/программирование/практика/учебная 2/Games/Medieval 2 - Total War/mods/DBM";

    public static String getGameDataPath() {
        return gameDataPath;
    }

    public static void setGameDataPath(String path) {
        gameDataPath = path;
        // При установке пути к игре обновляем базовый путь для логов
        if (!path.isEmpty()) {
            // Из пути data убираем /data в конце
            String dbmPath = path;
            if (dbmPath.endsWith("/data")) {
                dbmPath = dbmPath.substring(0, dbmPath.length() - 5);
            }
            Logger.setLogDirectory(dbmPath);
            Logger.info("Путь к игре установлен: " + path);
        }
    }

    public static String getBasePath() {
        return basePath;
    }

    @Override
    public void start(Stage primaryStage) {
        // Инициализируем логгер с базовым путём
        Logger.init(basePath);
        Logger.info("JavaFX приложение запущено");

        MainMenu mainMenu = new MainMenu();
        mainMenu.show(primaryStage);
    }

    @Override
    public void stop() {
        // Вызывается при закрытии приложения
        Logger.close();
    }

    public static void main(String[] args) {
        Logger.info("Запуск приложения \"Арена\"");
        launch(args);
    }
}