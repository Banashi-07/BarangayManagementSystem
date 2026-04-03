package UI.components;

import com.itextpdf.text.pdf.*;
import database.DatabaseManager;

import javax.swing.*;
import java.io.*;
import java.awt.Desktop;

public class PrintResidency {

    public static void print(int id) {
        generate(id, "filepdf/Residency.pdf", "residency_");
    }

    private static void generate(int id, String template, String prefix) {
        try {
            DatabaseManager.Resident r = DatabaseManager.getResidentById(id);

            if (r == null) {
                JOptionPane.showMessageDialog(null, "Resident not found!");
                return;
            }

            new File("output").mkdirs();

            String name = safe(r.getName());
            String address = safe(r.getAddress());
            String purok = safe(r.getPurok());

            String output = "output/" + prefix + name + ".pdf";

            PdfReader reader = new PdfReader(template);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(output));

            PdfContentByte canvas = stamper.getOverContent(1);
            BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, false);

            canvas.beginText();
            canvas.setFontAndSize(font, 12);

            canvas.setTextMatrix(150, 500);
            canvas.showText(name);

            canvas.setTextMatrix(150, 480);
            canvas.showText(address);

            canvas.setTextMatrix(150, 460);
            canvas.showText("Purok " + purok);

            canvas.endText();

            stamper.close();
            reader.close();

            open(output);

            JOptionPane.showMessageDialog(null, "Residency generated!");

        } catch (Exception e) {
            error(e);
        }
    }

    private static String safe(String s) {
        return (s == null) ? "" : s;
    }

    private static void open(String path) {
        try {
            Desktop.getDesktop().open(new File(path));
        } catch (Exception ignored) {}
    }

    private static void error(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error:\n" + e.getMessage());
    }
}