module com.example.educationpracticearena {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;      // для SwingFXUtils
    requires java.desktop;       // для ImageIO и BufferedImage

    opens arena to javafx.graphics, javafx.fxml;
    exports arena;
}