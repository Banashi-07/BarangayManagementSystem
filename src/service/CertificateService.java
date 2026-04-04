package service;

import database.DatabaseManager;
import database.DatabaseManager.Resident;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.Loader;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CertificateService {

    private static final String TEMPLATES_DIR = "src/filepdf/";
    private static final String OUTPUT_DIR = "certificates/";
    
    private static final String CLEARANCE_TEMPLATE = "ClearancePdf.pdf";
    private static final String RESIDENCY_TEMPLATE = "ResidencyPdf.pdf";
    private static final String INDIGENCY_TEMPLATE = "IndigencyPdf.pdf";

    // =====================================================
    // FONT STYLE SETTINGS - CHANGE THESE VALUES
    // =====================================================
    
    // For NAME field - Change this to control name style
    // Options: 
    //   Standard14Fonts.FontName.HELVETICA (regular)
    //   Standard14Fonts.FontName.HELVETICA_BOLD (bold)
    //   Standard14Fonts.FontName.HELVETICA_OBLIQUE (italic)
    //   Standard14Fonts.FontName.TIMES_ROMAN (regular serif)
    //   Standard14Fonts.FontName.TIMES_BOLD (bold serif)
    //   Standard14Fonts.FontName.TIMES_ITALIC (italic serif)
    //   Standard14Fonts.FontName.COURIER (monospace)
    private static final Standard14Fonts.FontName NAME_FONT = Standard14Fonts.FontName.HELVETICA_BOLD;
    private static final float NAME_FONT_SIZE = 12;  // Font size in points
    
    // For AGE field
    private static final Standard14Fonts.FontName AGE_FONT = Standard14Fonts.FontName.HELVETICA_BOLD;
    private static final float AGE_FONT_SIZE = 11;
    
    // For PURPOSE field
    private static final Standard14Fonts.FontName PURPOSE_FONT = Standard14Fonts.FontName.HELVETICA_BOLD;
    private static final float PURPOSE_FONT_SIZE = 10;
    
    // For DATE fields
    private static final Standard14Fonts.FontName DATE_FONT = Standard14Fonts.FontName.HELVETICA;
    private static final float DATE_FONT_SIZE = 10;
    
    // For OFFICIAL RECEIPT NUMBER
    private static final Standard14Fonts.FontName OR_FONT = Standard14Fonts.FontName.COURIER;
    private static final float OR_FONT_SIZE = 9;

    static {
        try {
            Files.createDirectories(Paths.get(OUTPUT_DIR));
            Files.createDirectories(Paths.get(TEMPLATES_DIR));
        } catch (IOException e) {
            System.err.println("Failed to create directories: " + e.getMessage());
        }
    }

    public static void generateClearance(int residentId, String purpose) {
        try {
            Resident resident = DatabaseManager.getResidentById(residentId);
            if (resident == null) {
                throw new IllegalArgumentException("Resident not found with ID: " + residentId);
            }

            String filename = String.format("Clearance_%s_%s.pdf", 
                sanitizeFilename(resident.getName()), 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
            
            Path outputPath = Paths.get(OUTPUT_DIR, filename);
            File templateFile = new File(TEMPLATES_DIR + CLEARANCE_TEMPLATE);
            
            if (!templateFile.exists()) {
                throw new FileNotFoundException("Template not found: " + templateFile.getAbsolutePath());
            }

            try (PDDocument doc = Loader.loadPDF(templateFile)) {
                PDPage page = doc.getPage(0);
                
                try (PDPageContentStream content = new PDPageContentStream(
                        doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                    
                    int age = calculateAge(resident.getBirthdate());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                    String currentDate = dateFormat.format(new Date());
                    
                    // DATE field (top right)
                    content.setFont(new PDType1Font(DATE_FONT), DATE_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0); // Black
                    content.beginText();
                    content.newLineAtOffset(451, 553);
                    content.showText(currentDate);
                    content.endText();
                    
                    // NAME field
                    content.setFont(new PDType1Font(NAME_FONT), NAME_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0); // Black
                    content.beginText();
                    content.newLineAtOffset(275, 487);
                    content.showText(resident.getName());
                    content.endText();
                    
                    // AGE field
                    content.setFont(new PDType1Font(AGE_FONT), AGE_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0); // Black
                    content.beginText();
                    content.newLineAtOffset(455, 487);
                    content.showText(String.valueOf(age));
                    content.endText();
                    
                    // PURPOSE field
                    content.setFont(new PDType1Font(PURPOSE_FONT), PURPOSE_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0); // Black
                    content.beginText();
                    content.newLineAtOffset(130, 360);
                    content.showText(purpose);
                    content.endText();
                    
                    // OFFICIAL RECEIPT NUMBER field
                    content.setFont(new PDType1Font(OR_FONT), OR_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0); // Black
                    content.beginText();
                    content.newLineAtOffset(185, 160);
                    content.showText(String.valueOf(residentId));
                    content.endText();
                    
                    // BOTTOM DATE field
                    content.setFont(new PDType1Font(DATE_FONT), DATE_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0); // Black
                    content.beginText();
                    content.newLineAtOffset(95, 146);
                    content.showText(dateFormat.format(new Date()));
                    content.endText();
                }
                
                doc.save(outputPath.toFile());
            }
            
            openInBrowser(outputPath.toFile());
            
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate clearance certificate: " + e.getMessage());
        }
    }

    public static void generateResidency(int residentId, String purpose) {
        try {
            Resident resident = DatabaseManager.getResidentById(residentId);
            if (resident == null) {
                throw new IllegalArgumentException("Resident not found with ID: " + residentId);
            }

            String filename = String.format("Residency_%s_%s.pdf", 
                sanitizeFilename(resident.getName()), 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
            
            Path outputPath = Paths.get(OUTPUT_DIR, filename);
            File templateFile = new File(TEMPLATES_DIR + RESIDENCY_TEMPLATE);
            
            if (!templateFile.exists()) {
                throw new FileNotFoundException("Template not found: " + templateFile.getAbsolutePath());
            }

            try (PDDocument doc = Loader.loadPDF(templateFile)) {
                PDPage page = doc.getPage(0);
                
                try (PDPageContentStream content = new PDPageContentStream(
                        doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                    
                    int age = calculateAge(resident.getBirthdate());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                    String currentDate = dateFormat.format(new Date());
                    
                    // DATE field (top right)
                    content.setFont(new PDType1Font(DATE_FONT), DATE_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(448, 553);
                    content.showText(currentDate);
                    content.endText();
                    
                    // NAME field
                    content.setFont(new PDType1Font(NAME_FONT), NAME_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(265, 487);
                    content.showText(resident.getName());
                    content.endText();
                    
                    // AGE field
                    content.setFont(new PDType1Font(AGE_FONT), AGE_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(450, 487);
                    content.showText(String.valueOf(age));
                    content.endText();
                    
                    // PURPOSE field
                    content.setFont(new PDType1Font(PURPOSE_FONT), PURPOSE_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(395, 375);
                    content.showText(purpose);
                    content.endText();
                    
                    // OFFICIAL RECEIPT NUMBER field
                    content.setFont(new PDType1Font(OR_FONT), OR_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(185, 130);
                    content.showText(String.valueOf(residentId));
                    content.endText();
                    
                    // BOTTOM DATE field
                    content.setFont(new PDType1Font(DATE_FONT), DATE_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(95, 117);
                    content.showText(dateFormat.format(new Date()));
                    content.endText();
                }
                
                doc.save(outputPath.toFile());
            }
            
            openInBrowser(outputPath.toFile());
            
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate residency certificate: " + e.getMessage());
        }
    }

    public static void generateIndigency(int residentId, String purpose) {
        try {
            Resident resident = DatabaseManager.getResidentById(residentId);
            if (resident == null) {
                throw new IllegalArgumentException("Resident not found with ID: " + residentId);
            }

            String filename = String.format("Indigency_%s_%s.pdf", 
                sanitizeFilename(resident.getName()), 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
            
            Path outputPath = Paths.get(OUTPUT_DIR, filename);
            File templateFile = new File(TEMPLATES_DIR + INDIGENCY_TEMPLATE);
            
            if (!templateFile.exists()) {
                throw new FileNotFoundException("Template not found: " + templateFile.getAbsolutePath());
            }

            try (PDDocument doc = Loader.loadPDF(templateFile)) {
                PDPage page = doc.getPage(0);
                
                try (PDPageContentStream content = new PDPageContentStream(
                        doc, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                    
                    int age = calculateAge(resident.getBirthdate());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                    String currentDate = dateFormat.format(new Date());
                    
                    // DATE field (top right)
                    content.setFont(new PDType1Font(DATE_FONT), DATE_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(430, 545);
                    content.showText(currentDate);
                    content.endText();
                    
                    // NAME field
                    content.setFont(new PDType1Font(NAME_FONT), NAME_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(275, 465);
                    content.showText(resident.getName());
                    content.endText();
                    
                    // AGE field
                    content.setFont(new PDType1Font(AGE_FONT), AGE_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(470, 465);
                    content.showText(String.valueOf(age));
                    content.endText();
                    
                    // PURPOSE field
                    content.setFont(new PDType1Font(PURPOSE_FONT), PURPOSE_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(150, 363);
                    content.showText(purpose);
                    content.endText();
                }
                
                doc.save(outputPath.toFile());
            }
            
            openInBrowser(outputPath.toFile());
            
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate indigency certificate: " + e.getMessage());
        }
    }

    private static int calculateAge(String birthdate) {
        if (birthdate == null || birthdate.trim().isEmpty()) {
            return 0;
        }
        
        try {
            DateTimeFormatter formatter;
            if (birthdate.contains("-")) {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            } else {
                formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            }
            
            LocalDate birthDate = LocalDate.parse(birthdate, formatter);
            LocalDate currentDate = LocalDate.now();
            return Period.between(birthDate, currentDate).getYears();
        } catch (Exception e) {
            System.err.println("Failed to parse birthdate: " + birthdate);
            return 0;
        }
    }

    private static String sanitizeFilename(String name) {
        return name.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private static void openInBrowser(File pdfFile) {
        if (pdfFile == null || !pdfFile.exists()) {
            System.err.println("Cannot open file - file does not exist: " + pdfFile);
            return;
        }
        
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(pdfFile.toURI());
                    System.out.println("PDF opened in browser: " + pdfFile.getName());
                    return;
                }
                
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(pdfFile);
                    System.out.println("PDF opened with default viewer: " + pdfFile.getName());
                    return;
                }
            }
            
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder processBuilder = null;
            
            if (os.contains("win")) {
                processBuilder = new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", pdfFile.getAbsolutePath());
            } else if (os.contains("mac")) {
                processBuilder = new ProcessBuilder("open", pdfFile.getAbsolutePath());
            } else if (os.contains("nix") || os.contains("nux")) {
                processBuilder = new ProcessBuilder("xdg-open", pdfFile.getAbsolutePath());
            }
            
            if (processBuilder != null) {
                processBuilder.start();
                System.out.println("PDF opened: " + pdfFile.getName());
            } else {
                System.out.println("File saved at: " + pdfFile.getAbsolutePath());
            }
            
        } catch (IOException e) {
            System.out.println("File saved at: " + pdfFile.getAbsolutePath());
        }
    }
}