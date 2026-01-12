package com.hotel.util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// this class exports reports to csv files
// handles revenue, occupancy, feedback, and activity logs
public class CsvExporter {
    
    // exports revenue report to csv
    public static void exportRevenueReport(String filename, List<Map<String, Object>> data) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // header row
            writer.append("Period,Reservation Count,Subtotal,Tax,Discounts,Total\n");
            
            // data rows
            for (Map<String, Object> row : data) {
                writer.append(String.valueOf(row.get("period"))).append(",");
                writer.append(String.valueOf(row.get("reservationCount"))).append(",");
                writer.append(String.valueOf(row.get("subtotal"))).append(",");
                writer.append(String.valueOf(row.get("tax"))).append(",");
                writer.append(String.valueOf(row.get("discounts"))).append(",");
                writer.append(String.valueOf(row.get("total"))).append("\n");
            }
        }
    }
    
    // exports occupancy report to csv
    public static void exportOccupancyReport(String filename, List<Map<String, Object>> data) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // header row
            writer.append("Date,Rooms Available,Rooms Occupied,Occupancy Percentage\n");
            
            // data rows
            for (Map<String, Object> row : data) {
                writer.append(String.valueOf(row.get("date"))).append(",");
                writer.append(String.valueOf(row.get("availableRooms"))).append(",");
                writer.append(String.valueOf(row.get("occupiedRooms"))).append(",");
                writer.append(String.valueOf(row.get("occupancyPercent"))).append("\n");
            }
        }
    }
    
    // exports feedback summary to csv
    public static void exportFeedbackSummary(String filename, List<Map<String, Object>> data) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // header row
            writer.append("Reservation ID,Guest,Rating,Comment,Date,Sentiment Tag\n");
            
            // data rows
            for (Map<String, Object> row : data) {
                writer.append(String.valueOf(row.get("reservationId"))).append(",");
                writer.append(escapeCsv(String.valueOf(row.get("guest")))).append(",");
                writer.append(String.valueOf(row.get("rating"))).append(",");
                writer.append(escapeCsv(String.valueOf(row.get("comment")))).append(",");
                writer.append(String.valueOf(row.get("date"))).append(",");
                writer.append(String.valueOf(row.get("sentimentTag"))).append("\n");
            }
        }
    }
    
    // exports feedback list to csv
    public void exportFeedback(List<com.hotel.model.Feedback> feedbackList, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // header row
            writer.append("Reservation ID,Guest,Rating,Comment,Date,Sentiment Tag\n");
            
            // data rows
            for (com.hotel.model.Feedback feedback : feedbackList) {
                writer.append(String.valueOf(feedback.getReservation().getId())).append(",");
                writer.append(escapeCsv(feedback.getReservation().getGuest().getName())).append(",");
                writer.append(String.valueOf(feedback.getRating())).append(",");
                writer.append(escapeCsv(feedback.getComments() != null ? feedback.getComments() : "")).append(",");
                writer.append(String.valueOf(feedback.getCreatedAt() != null ? feedback.getCreatedAt().toLocalDate() : "")).append(",");
                writer.append(escapeCsv(feedback.getSentimentTag() != null ? feedback.getSentimentTag() : "")).append("\n");
            }
        }
    }
    
    // exports activity logs to csv
    public static void exportActivityLogs(String filename, List<Map<String, Object>> data) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            // header row
            writer.append("Timestamp,Actor,Action,Entity Type,Entity ID,Message\n");
            
            // data rows
            for (Map<String, Object> row : data) {
                writer.append(String.valueOf(row.get("timestamp"))).append(",");
                writer.append(escapeCsv(String.valueOf(row.get("actor")))).append(",");
                writer.append(escapeCsv(String.valueOf(row.get("action")))).append(",");
                writer.append(escapeCsv(String.valueOf(row.get("entityType")))).append(",");
                writer.append(String.valueOf(row.get("entityId"))).append(",");
                writer.append(escapeCsv(String.valueOf(row.get("message")))).append("\n");
            }
        }
    }
    
    // handles csv escaping for commas, quotes, and newlines
    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}



