package arena;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import java.io.*;
import java.nio.file.*;

/**
 * Простой загрузчик TGA файлов
 * Поддерживает 24-битные и 32-битные TGA изображения
 */
public class TgaLoader {

    /**
     * Загружает TGA файл и возвращает JavaFX Image
     * @param tgaFile путь к TGA файлу
     * @return Image или null в случае ошибки
     */
    public static Image loadTga(File tgaFile) {
        if (!tgaFile.exists()) {
            System.err.println("Файл не найден: " + tgaFile.getPath());
            return null;
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(tgaFile))) {
            // Читаем заголовок TGA
            byte idLength = dis.readByte();           // длина ID поля
            byte colorMapType = dis.readByte();       // тип цветовой карты
            byte imageType = dis.readByte();          // тип изображения (2 = RGB, 3 = оттенки серого, 10 = RLE RGB)

            // Пропускаем информацию о цветовой карте (5 байт)
            dis.skipBytes(5);

            // Читаем координаты и размеры
            short xOrigin = dis.readShort();
            short yOrigin = dis.readShort();
            short width = dis.readShort();
            short height = dis.readShort();
            byte pixelDepth = dis.readByte();         // глубина цвета (24 или 32)
            byte imageDesc = dis.readByte();          // дескриптор изображения

            // Пропускаем ID поле, если оно есть
            if (idLength > 0) {
                dis.skipBytes(idLength);
            }

            // Проверяем тип изображения (только не-RGB для простоты)
            if (imageType != 2 && imageType != 3) {
                System.err.println("Неподдерживаемый тип TGA: " + imageType);
                return null;
            }

            // Определяем количество байт на пиксель
            int bytesPerPixel = pixelDepth / 8;

            // Вычисляем размер данных
            int imageSize = width * height * bytesPerPixel;
            byte[] imageData = new byte[imageSize];
            dis.readFully(imageData);

            // Создаём JavaFX Image
            return createImageFromTgaData(imageData, width, height, bytesPerPixel);

        } catch (Exception e) {
            System.err.println("Ошибка загрузки TGA: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Создаёт JavaFX Image из данных TGA
     */
    private static Image createImageFromTgaData(byte[] data, short width, short height, int bytesPerPixel) {
        int w = width;
        int h = height;

        // Для TGA данные обычно идут снизу-вверх, переворачиваем
        byte[] flippedData = new byte[data.length];

        int rowSize = w * bytesPerPixel;
        for (int y = 0; y < h; y++) {
            int srcRow = (h - 1 - y) * rowSize;
            int dstRow = y * rowSize;
            System.arraycopy(data, srcRow, flippedData, dstRow, rowSize);
        }

        if (bytesPerPixel == 3) {
            // 24-битное изображение (BGR)
            int[] pixels = new int[w * h];
            for (int i = 0; i < pixels.length; i++) {
                int b = flippedData[i * 3] & 0xFF;
                int g = flippedData[i * 3 + 1] & 0xFF;
                int r = flippedData[i * 3 + 2] & 0xFF;
                pixels[i] = (0xFF << 24) | (r << 16) | (g << 8) | b;
            }

            WritableImage image = new WritableImage(w, h);
            PixelWriter writer = image.getPixelWriter();
            writer.setPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), pixels, 0, w);
            return image;

        } else if (bytesPerPixel == 4) {
            // 32-битное изображение (BGRA)
            int[] pixels = new int[w * h];
            for (int i = 0; i < pixels.length; i++) {
                int b = flippedData[i * 4] & 0xFF;
                int g = flippedData[i * 4 + 1] & 0xFF;
                int r = flippedData[i * 4 + 2] & 0xFF;
                int a = flippedData[i * 4 + 3] & 0xFF;
                pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
            }

            WritableImage image = new WritableImage(w, h);
            PixelWriter writer = image.getPixelWriter();
            writer.setPixels(0, 0, w, h, PixelFormat.getIntArgbInstance(), pixels, 0, w);
            return image;

        } else {
            System.err.println("Неподдерживаемая глубина цвета: " + bytesPerPixel);
            return null;
        }
    }
}