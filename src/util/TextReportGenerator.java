package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.awt.Desktop;

public class TextReportGenerator {

    public static void generateMedicalRecordReport(String filePath, String title, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(title);
            writer.newLine();
            writer.write("=====================================");
            writer.newLine();
            writer.write(content);
            writer.newLine();
            writer.write("=====================================");
            writer.newLine();

            // Automatically open the file after generation
            File file = new File(filePath);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}