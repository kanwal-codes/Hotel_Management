package com.hotel.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

// exports reports to plain text format
// currently just for activity logs
public class TxtExporter {
    
    // exports activity logs to text file
    public static void exportActivityLogs(String filename, List<Map<String, Object>> data) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.append("ACTIVITY LOGS\n");
            writer.append("=".repeat(80)).append("\n\n");
            
            for (Map<String, Object> row : data) {
                writer.append("Timestamp: ").append(String.valueOf(row.get("timestamp"))).append("\n");
                writer.append("Actor: ").append(String.valueOf(row.get("actor"))).append("\n");
                writer.append("Action: ").append(String.valueOf(row.get("action"))).append("\n");
                writer.append("Entity Type: ").append(String.valueOf(row.get("entityType"))).append("\n");
                writer.append("Entity ID: ").append(String.valueOf(row.get("entityId"))).append("\n");
                writer.append("Message: ").append(String.valueOf(row.get("message"))).append("\n");
                writer.append("-".repeat(80)).append("\n");
            }
        }
    }
}



