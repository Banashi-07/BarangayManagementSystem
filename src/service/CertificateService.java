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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CertificateService {

    private static final String TEMPLATES_DIR = "src/filepdf/";
    
    private static final String CLEARANCE_TEMPLATE = "ClearancePdf.pdf";
    private static final String RESIDENCY_TEMPLATE = "ResidencyPdf.pdf";
    private static final String INDIGENCY_TEMPLATE = "IndigencyPdf.pdf";

    // For scheduling automatic deletion
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // =====================================================
    // FONT STYLE SETTINGS - CHANGE THESE VALUES
    // =====================================================
    
    private static final Standard14Fonts.FontName NAME_FONT = Standard14Fonts.FontName.HELVETICA_BOLD;
    private static final float NAME_FONT_SIZE = 12;
    
    private static final Standard14Fonts.FontName AGE_FONT = Standard14Fonts.FontName.HELVETICA_BOLD;
    private static final float AGE_FONT_SIZE = 11;
    
    private static final Standard14Fonts.FontName PURPOSE_FONT = Standard14Fonts.FontName.HELVETICA_BOLD;
    private static final float PURPOSE_FONT_SIZE = 10;
    
    private static final Standard14Fonts.FontName DATE_FONT = Standard14Fonts.FontName.HELVETICA;
    private static final float DATE_FONT_SIZE = 10;
    
    private static final Standard14Fonts.FontName OR_FONT = Standard14Fonts.FontName.COURIER;
    private static final float OR_FONT_SIZE = 9;

    static {
        try {
            // Only create templates directory, no output directory needed anymore
            new File(TEMPLATES_DIR).mkdirs();
        } catch (Exception e) {
            System.err.println("Failed to create templates directory: " + e.getMessage());
        }
        
        // Clean up any leftover temp files from previous runs
        cleanupOldTempFiles();
        
        // Shutdown hook to clean up scheduler
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            scheduler.shutdown();
            cleanupOldTempFiles();
        }));
    }

    public static void generateClearance(int residentId, String purpose) {
        File tempFile = null;
        try {
            Resident resident = DatabaseManager.getResidentById(residentId);
            if (resident == null) {
                throw new IllegalArgumentException("Resident not found with ID: " + residentId);
            }

            // Create temporary file (deleted when JVM exits or manually)
            tempFile = File.createTempFile("clearance_" + sanitizeFilename(resident.getName()) + "_", ".pdf");
            
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
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(451, 553);
                    content.showText(currentDate);
                    content.endText();
                    
                    // NAME field
                    content.setFont(new PDType1Font(NAME_FONT), NAME_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(275, 487);
                    content.showText(resident.getName());
                    content.endText();
                    
                    // AGE field
                    content.setFont(new PDType1Font(AGE_FONT), AGE_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(455, 487);
                    content.showText(String.valueOf(age));
                    content.endText();
                    
                    // PURPOSE field
                    content.setFont(new PDType1Font(PURPOSE_FONT), PURPOSE_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(130, 360);
                    content.showText(purpose);
                    content.endText();
                    
                    // OFFICIAL RECEIPT NUMBER field
                    content.setFont(new PDType1Font(OR_FONT), OR_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(185, 160);
                    content.showText(String.valueOf(residentId));
                    content.endText();
                    
                    // BOTTOM DATE field
                    content.setFont(new PDType1Font(DATE_FONT), DATE_FONT_SIZE);
                    content.setNonStrokingColor(0, 0, 0);
                    content.beginText();
                    content.newLineAtOffset(95, 146);
                    content.showText(dateFormat.format(new Date()));
                    content.endText();
                }
                
                doc.save(tempFile);
            }
            
            // Open the temporary file and schedule deletion
            openAndDeleteLater(tempFile);
            
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            // Clean up on error
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
            throw new RuntimeException("Failed to generate clearance certificate: " + e.getMessage());
        }
    }

    public static void generateResidency(int residentId, String purpose) {
        File tempFile = null;
        try {
            Resident resident = DatabaseManager.getResidentById(residentId);
            if (resident == null) {
                throw new IllegalArgumentException("Resident not found with ID: " + residentId);
            }

            // Create temporary file
            tempFile = File.createTempFile("residency_" + sanitizeFilename(resident.getName()) + "_", ".pdf");
            
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
                
                doc.save(tempFile);
            }
            
            // Open and schedule deletion
            openAndDeleteLater(tempFile);
            
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
            throw new RuntimeException("Failed to generate residency certificate: " + e.getMessage());
        }
    }

    public static void generateIndigency(int residentId, String purpose) {
        File tempFile = null;
        try {
            Resident resident = DatabaseManager.getResidentById(residentId);
            if (resident == null) {
                throw new IllegalArgumentException("Resident not found with ID: " + residentId);
            }

            // Create temporary file
            tempFile = File.createTempFile("indigency_" + sanitizeFilename(resident.getName()) + "_", ".pdf");
            
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
                
                doc.save(tempFile);
            }
            
            // Open and schedule deletion
            openAndDeleteLater(tempFile);
            
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
            throw new RuntimeException("Failed to generate indigency certificate: " + e.getMessage());
        }
    }

    /**
     * Opens a PDF file and schedules it for deletion after 30 seconds
     */
    private static void openAndDeleteLater(File pdfFile) {
        try {
            // Open the PDF
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.OPEN)) {
                    desktop.open(pdfFile);
                    System.out.println("Temporary PDF opened: " + pdfFile.getName());
                } else {
                    System.out.println("PDF saved to temp location: " + pdfFile.getAbsolutePath());
                }
            } else {
                // Fallback for systems without Desktop support
                String os = System.getProperty("os.name").toLowerCase();
                ProcessBuilder pb = null;
                if (os.contains("win")) {
                    pb = new ProcessBuilder("rundll32", "url.dll,FileProtocolHandler", pdfFile.getAbsolutePath());
                } else if (os.contains("mac")) {
                    pb = new ProcessBuilder("open", pdfFile.getAbsolutePath());
                } else if (os.contains("nix") || os.contains("nux")) {
                    pb = new ProcessBuilder("xdg-open", pdfFile.getAbsolutePath());
                }
                if (pb != null) {
                    pb.start();
                    System.out.println("Temporary PDF opened: " + pdfFile.getName());
                }
            }
            
            // Schedule deletion after 30 seconds (gives time to view)
            scheduler.schedule(() -> {
                boolean deleted = pdfFile.delete();
                if (deleted) {
                    System.out.println("Temporary PDF deleted: " + pdfFile.getName());
                } else {
                    // If can't delete now, delete on JVM exit
                    pdfFile.deleteOnExit();
                    System.out.println("Scheduled PDF deletion on exit: " + pdfFile.getName());
                }
            }, 30, TimeUnit.SECONDS);
            
        } catch (IOException e) {
            System.err.println("Failed to open PDF: " + e.getMessage());
            // Delete immediately if can't open
            pdfFile.delete();
        }
    }
    
    /**
     * Cleans up any leftover temporary files from previous runs
     */
    private static void cleanupOldTempFiles() {
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            File[] tempFiles = new File(tempDir).listFiles((dir, name) -> 
                name.startsWith("clearance_") || 
                name.startsWith("residency_") || 
                name.startsWith("indigency_")
            );
            
            if (tempFiles != null) {
                for (File file : tempFiles) {
                    // Delete files older than 1 hour
                    if (System.currentTimeMillis() - file.lastModified() > 3600000) {
                        file.delete();
                        System.out.println("Cleaned up old temp file: " + file.getName());
                    }
                }
            }
        } catch (Exception e) {
            // Ignore cleanup errors
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
}