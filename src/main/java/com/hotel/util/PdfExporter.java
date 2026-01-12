package com.hotel.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.util.List;
import java.util.Map;

// exports reports to pdf format
// handles revenue, occupancy, feedback, and activity logs
public class PdfExporter {
    
    // exports revenue report to pdf
    public static void exportRevenueReport(String filename, List<Map<String, Object>> data) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            try {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Revenue Report");
                contentStream.endText();
                
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                float y = 700;
                contentStream.beginText();
                contentStream.newLineAtOffset(50, y);
                contentStream.showText("Period | Reservation Count | Subtotal | Tax | Discounts | Total");
                contentStream.endText();
                
                y -= 20;
                for (Map<String, Object> row : data) {
                    if (y < 50) {
                        contentStream.endText();
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(PDType1Font.HELVETICA, 12);
                        y = 750;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, y);
                    } else if (y == 680) {
                        // first row after header
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, y);
                    }
                    
                    // get values from map
                    Object periodObj = row.get("period") != null ? row.get("period") : row.get("Period");
                    Object countObj = row.get("reservationCount") != null ? row.get("reservationCount") : row.get("Reservation Count");
                    Object subtotalObj = row.get("subtotal") != null ? row.get("subtotal") : row.get("Subtotal");
                    Object taxObj = row.get("tax") != null ? row.get("tax") : row.get("Tax");
                    Object discountsObj = row.get("discounts") != null ? row.get("discounts") : row.get("Discounts");
                    Object totalObj = row.get("total") != null ? row.get("total") : row.get("Total");
                    
                    // convert to strings/numbers
                    String period = periodObj != null ? periodObj.toString() : "N/A";
                    String count = countObj != null ? countObj.toString() : "0";
                    double subtotal = parseDouble(subtotalObj);
                    double tax = parseDouble(taxObj);
                    double discounts = parseDouble(discountsObj);
                    double total = parseDouble(totalObj);
                    
                    String line = String.format("%s | %s | %.2f | %.2f | %.2f | %.2f",
                        period, count, subtotal, tax, discounts, total);
                    
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText(line);
                    y -= 20;
                }
                if (contentStream != null) {
                    contentStream.endText();
                }
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }
            
            document.save(filename);
        }
    }
    
    // exports occupancy report to pdf
    public static void exportOccupancyReport(String filename, List<Map<String, Object>> data) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            try {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Occupancy Report");
                contentStream.endText();
                
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                float y = 700;
                contentStream.beginText();
                contentStream.newLineAtOffset(50, y);
                contentStream.showText("Date | Available | Occupied | Occupancy %");
                contentStream.endText();
                
                y -= 20;
                for (Map<String, Object> row : data) {
                    if (y < 50) {
                        contentStream.endText();
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(PDType1Font.HELVETICA, 12);
                        y = 750;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, y);
                    } else if (y == 680) {
                        // first row after header
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, y);
                    }
                    
                    // get values from map
                    Object dateObj = row.get("date") != null ? row.get("date") : row.get("Date");
                    Object availableObj = row.get("availableRooms") != null ? row.get("availableRooms") : row.get("Available");
                    Object occupiedObj = row.get("occupiedRooms") != null ? row.get("occupiedRooms") : row.get("Occupied");
                    Object occupancyObj = row.get("occupancyPercent") != null ? row.get("occupancyPercent") : row.get("Occupancy %");
                    
                    String date = dateObj != null ? dateObj.toString() : "N/A";
                    int available = parseInt(availableObj);
                    int occupied = parseInt(occupiedObj);
                    double occupancy = parseDouble(occupancyObj);
                    
                    String line = String.format("%s | %d | %d | %.2f%%",
                        date, available, occupied, occupancy);
                    
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText(line);
                    y -= 20;
                }
                if (contentStream != null) {
                    contentStream.endText();
                }
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }
            
            document.save(filename);
        }
    }
    
    // helper to parse double safely
    private static double parseDouble(Object obj) {
        if (obj == null) return 0.0;
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        try {
            return Double.parseDouble(obj.toString());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    // exports activity logs to pdf
    public static void exportActivityLogs(String filename, List<Map<String, Object>> data) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            try {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Activity Logs Report");
                contentStream.endText();
                
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                float y = 700;
                contentStream.beginText();
                contentStream.newLineAtOffset(50, y);
                contentStream.showText("Timestamp | Actor | Action | Entity Type | Entity ID | Message");
                contentStream.endText();
                
                y -= 20;
                for (Map<String, Object> row : data) {
                    if (y < 50) {
                        contentStream.endText();
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(PDType1Font.HELVETICA, 10);
                        y = 750;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, y);
                    } else if (y == 680) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, y);
                    }
                    
                    Object timestampObj = row.get("timestamp") != null ? row.get("timestamp") : row.get("Timestamp");
                    Object actorObj = row.get("actor") != null ? row.get("actor") : row.get("Actor");
                    Object actionObj = row.get("action") != null ? row.get("action") : row.get("Action");
                    Object entityTypeObj = row.get("entityType") != null ? row.get("entityType") : row.get("Entity Type");
                    Object entityIdObj = row.get("entityId") != null ? row.get("entityId") : row.get("Entity ID");
                    Object messageObj = row.get("message") != null ? row.get("message") : row.get("Message");
                    
                    String timestamp = timestampObj != null ? timestampObj.toString() : "N/A";
                    String actor = actorObj != null ? actorObj.toString() : "N/A";
                    String action = actionObj != null ? actionObj.toString() : "N/A";
                    String entityType = entityTypeObj != null ? entityTypeObj.toString() : "N/A";
                    String entityId = entityIdObj != null ? entityIdObj.toString() : "N/A";
                    String message = messageObj != null ? messageObj.toString() : "N/A";
                    
                    // truncate long messages to fit on page
                    if (message.length() > 50) {
                        message = message.substring(0, 47) + "...";
                    }
                    
                    String line = String.format("%s | %s | %s | %s | %s | %s",
                        truncate(timestamp, 20), truncate(actor, 15), truncate(action, 15), 
                        truncate(entityType, 15), truncate(entityId, 10), truncate(message, 30));
                    
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText(line);
                    y -= 20;
                }
                if (contentStream != null) {
                    contentStream.endText();
                }
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }
            
            document.save(filename);
        }
    }
    
    // exports feedback summary to pdf
    public static void exportFeedbackSummary(String filename, List<Map<String, Object>> data) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            try {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Feedback Summary Report");
                contentStream.endText();
                
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                float y = 700;
                contentStream.beginText();
                contentStream.newLineAtOffset(50, y);
                contentStream.showText("Reservation ID | Guest | Rating | Comment | Date | Sentiment Tag");
                contentStream.endText();
                
                y -= 20;
                for (Map<String, Object> row : data) {
                    if (y < 50) {
                        contentStream.endText();
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.setFont(PDType1Font.HELVETICA, 10);
                        y = 750;
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, y);
                    } else if (y == 680) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(50, y);
                    }
                    
                    Object reservationIdObj = row.get("reservationId") != null ? row.get("reservationId") : row.get("Reservation ID");
                    Object guestObj = row.get("guest") != null ? row.get("guest") : row.get("Guest");
                    Object ratingObj = row.get("rating") != null ? row.get("rating") : row.get("Rating");
                    Object commentObj = row.get("comment") != null ? row.get("comment") : row.get("Comment");
                    Object dateObj = row.get("date") != null ? row.get("date") : row.get("Date");
                    Object sentimentObj = row.get("sentimentTag") != null ? row.get("sentimentTag") : row.get("Sentiment Tag");
                    
                    String reservationId = reservationIdObj != null ? reservationIdObj.toString() : "N/A";
                    String guest = guestObj != null ? guestObj.toString() : "N/A";
                    String rating = ratingObj != null ? ratingObj.toString() : "N/A";
                    String comment = commentObj != null ? commentObj.toString() : "N/A";
                    String date = dateObj != null ? dateObj.toString() : "N/A";
                    String sentiment = sentimentObj != null ? sentimentObj.toString() : "N/A";
                    
                    // truncate long comments
                    if (comment.length() > 40) {
                        comment = comment.substring(0, 37) + "...";
                    }
                    
                    String line = String.format("%s | %s | %s | %s | %s | %s",
                        truncate(reservationId, 12), truncate(guest, 20), truncate(rating, 6),
                        truncate(comment, 30), truncate(date, 12), truncate(sentiment, 15));
                    
                    contentStream.newLineAtOffset(0, -20);
                    contentStream.showText(line);
                    y -= 20;
                }
                if (contentStream != null) {
                    contentStream.endText();
                }
            } finally {
                if (contentStream != null) {
                    contentStream.close();
                }
            }
            
            document.save(filename);
        }
    }
    
    // helper to truncate strings
    private static String truncate(String str, int maxLength) {
        if (str == null) return "N/A";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
    
    // helper to parse int safely
    private static int parseInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Number) {
            return ((Number) obj).intValue();
        }
        try {
            return Integer.parseInt(obj.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}



