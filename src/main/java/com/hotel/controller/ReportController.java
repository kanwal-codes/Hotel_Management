package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BaseController;
import com.hotel.controller.helper.AdminNavigationHelper;
import com.hotel.model.AuditLog;
import com.hotel.model.RoomType;
import com.hotel.service.ReportingService;
import com.hotel.util.CsvExporter;
import com.hotel.util.LoggerService;
import com.hotel.util.PdfExporter;
import com.hotel.util.TxtExporter;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//
 // Controller for generating and exporting reports.
 // Supports revenue, occupancy, activity logs, and feedback summary reports.
 // Allows exporting reports to CSV, PDF, or TXT formats.
//
public class ReportController extends BaseController {
    
    private ReportingService reportingService;
    private LoggerService logger;
    
    // ========== ReportsScreen.fxml ==========
    @FXML private ComboBox<String> reportTypeComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> roomTypeComboBox;
    @FXML private TableView<Map<String, Object>> reportTable;
    @FXML private Button txtExportButton;
    
    private ObservableList<Map<String, Object>> reportData = FXCollections.observableArrayList();
    
    @FXML
    private void initialize() {
        // Initialize services
        reportingService = AppConfig.createReportingService();
        logger = LoggerService.getInstance();
        
        // Initialize ComboBox items
        if (reportTypeComboBox != null) {
            reportTypeComboBox.getItems().addAll("Revenue", "Occupancy", "Activity Logs", "Feedback Summary");
            reportTypeComboBox.setValue("Revenue");
        }
        
        if (roomTypeComboBox != null) {
            roomTypeComboBox.getItems().addAll("All", "Single", "Double", "Deluxe", "Penthouse");
            roomTypeComboBox.setValue("All");
        }
        
        // Set default dates (current month)
        if (startDatePicker != null) {
            startDatePicker.setValue(LocalDate.now().withDayOfMonth(1));
        }
        if (endDatePicker != null) {
            endDatePicker.setValue(LocalDate.now());
        }
        
        // Setup table
        setupReportTable();
    }
    
    private void setupReportTable() {
        if (reportTable == null) {
            return;
        }
        
        // Clear existing columns
        reportTable.getColumns().clear();
        
        // Columns will be dynamically created based on report type
        reportTable.setItems(reportData);
    }
    
    private void populateTable(List<Map<String, Object>> data, List<String> columnNames) {
        if (reportTable == null) {
            return;
        }
        
        reportData.clear();
        reportData.addAll(data);
        
        // Clear existing columns
        reportTable.getColumns().clear();
        
        // Get current report type to set appropriate column widths
        String reportType = reportTypeComboBox != null ? reportTypeComboBox.getValue() : "";
        
        // Create columns dynamically with appropriate widths
        for (String columnName : columnNames) {
            TableColumn<Map<String, Object>, String> column = new TableColumn<>(columnName);
            
            // Set column widths based on column name and report type
            setColumnWidth(column, columnName, reportType);
            
            // Try multiple key formats to match data
            final String key1 = columnName.toLowerCase().replace(" ", "");
            final String key2 = columnName.toLowerCase().replace(" ", "").replace("id", "id");
            final String key3 = columnName.toLowerCase();
            final String key4 = columnName; // Original case
            column.setCellValueFactory(cellData -> {
                Map<String, Object> row = cellData.getValue();
                Object value = null;
                // Try multiple key formats
                if (value == null) value = row.get(key1);
                if (value == null) value = row.get(key2);
                if (value == null) value = row.get(key3);
                if (value == null) value = row.get(key4);
                // Try camelCase for Entity Type and Entity ID
                if (value == null && columnName.equals("Entity Type")) {
                    value = row.get("entityType");
                }
                if (value == null && columnName.equals("Entity ID")) {
                    value = row.get("entityId");
                }
                return new javafx.beans.property.SimpleStringProperty(
                    value != null ? value.toString() : "");
            });
            reportTable.getColumns().add(column);
        }
        
        // Apply table styling
        reportTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    private void setColumnWidth(TableColumn<Map<String, Object>, String> column, String columnName, String reportType) {
        // Set appropriate widths based on column type and report type
        if (columnName.equalsIgnoreCase("Date") || columnName.equalsIgnoreCase("From") || columnName.equalsIgnoreCase("To")) {
            // Date columns: compact width
            column.setMinWidth(100.0);
            column.setPrefWidth(110.0);
            column.setMaxWidth(120.0);
        } else if (columnName.equalsIgnoreCase("Timestamp")) {
            // Timestamp columns: wider width for date/time
            column.setMinWidth(150.0);
            column.setPrefWidth(180.0);
            column.setMaxWidth(200.0);
        } else if (columnName.equalsIgnoreCase("Actor")) {
            // Actor column: medium width
            column.setMinWidth(100.0);
            column.setPrefWidth(120.0);
            column.setMaxWidth(150.0);
        } else if (columnName.equalsIgnoreCase("Action")) {
            // Action column: medium width
            column.setMinWidth(120.0);
            column.setPrefWidth(150.0);
            column.setMaxWidth(200.0);
        } else if (columnName.equalsIgnoreCase("Entity Type")) {
            // Entity Type column: medium width
            column.setMinWidth(100.0);
            column.setPrefWidth(120.0);
            column.setMaxWidth(150.0);
        } else if (columnName.equalsIgnoreCase("Entity ID")) {
            // Entity ID column: narrow width
            column.setMinWidth(80.0);
            column.setPrefWidth(100.0);
            column.setMaxWidth(120.0);
        } else if (columnName.equalsIgnoreCase("Message")) {
            // Message column: wider width for longer text
            column.setMinWidth(200.0);
            column.setPrefWidth(300.0);
            column.setMaxWidth(400.0);
        } else if (columnName.equalsIgnoreCase("Room Type")) {
            column.setMinWidth(100.0);
            column.setPrefWidth(120.0);
            column.setMaxWidth(150.0);
        } else if (columnName.equalsIgnoreCase("Total Rooms") || columnName.equalsIgnoreCase("Occupied Rooms") || 
                   columnName.equalsIgnoreCase("Available Rooms")) {
            column.setMinWidth(100.0);
            column.setPrefWidth(120.0);
            column.setMaxWidth(140.0);
        } else if (columnName.equalsIgnoreCase("Occupancy %")) {
            column.setMinWidth(100.0);
            column.setPrefWidth(110.0);
            column.setMaxWidth(130.0);
        } else if (columnName.equalsIgnoreCase("Reservation ID") || columnName.equalsIgnoreCase("Entity ID")) {
            column.setMinWidth(100.0);
            column.setPrefWidth(120.0);
            column.setMaxWidth(150.0);
        } else if (columnName.equalsIgnoreCase("Guest") || columnName.equalsIgnoreCase("Actor")) {
            column.setMinWidth(120.0);
            column.setPrefWidth(150.0);
            column.setMaxWidth(200.0);
        } else if (columnName.equalsIgnoreCase("Rating")) {
            column.setMinWidth(70.0);
            column.setPrefWidth(80.0);
            column.setMaxWidth(100.0);
        } else if (columnName.equalsIgnoreCase("Comment") || columnName.equalsIgnoreCase("Message")) {
            // Comment/Message columns: flexible width, take remaining space
            column.setMinWidth(150.0);
            column.setPrefWidth(250.0);
            column.setMaxWidth(Double.MAX_VALUE);
        } else if (columnName.equalsIgnoreCase("Sentiment Tag") || columnName.equalsIgnoreCase("Action") || 
                   columnName.equalsIgnoreCase("Entity Type")) {
            column.setMinWidth(100.0);
            column.setPrefWidth(120.0);
            column.setMaxWidth(180.0);
        } else if (columnName.contains("Amount") || columnName.contains("Price") || columnName.contains("Total") || 
                   columnName.contains("Subtotal") || columnName.contains("Tax") || columnName.contains("Discount")) {
            // Financial columns
            column.setMinWidth(100.0);
            column.setPrefWidth(120.0);
            column.setMaxWidth(150.0);
        } else if (columnName.equalsIgnoreCase("Period") || columnName.equalsIgnoreCase("Reservation Count")) {
            column.setMinWidth(100.0);
            column.setPrefWidth(130.0);
            column.setMaxWidth(180.0);
        } else {
            // Default width for other columns
            column.setMinWidth(100.0);
            column.setPrefWidth(120.0);
            column.setMaxWidth(200.0);
        }
    }
    
    // ========== Navigation Methods ==========
    @FXML
    @Override
    protected void goBack() {
        try {
            Stage stage = getCurrentStage();
            AdminNavigationHelper.switchScene(stage, "/view/admin/Dashboard.fxml", controller -> {
                // Navigation to dashboard - user context should be maintained
            });
        } catch (Exception e) {
            logger.logError("Failed to navigate back to dashboard", e);
            showAlert("Error", "Failed to navigate back: " + e.getMessage());
        }
    }
    
    //
     // Gets the current stage for navigation.
     // Overrides base method to use admin-specific fields.
//
    @Override
    protected Stage getCurrentStage(Node... fallbackNodes) {
        // Try admin-specific fields first
        if (reportTable != null && reportTable.getScene() != null) {
            return getCurrentStageFromNode(reportTable);
        }
        if (reportTypeComboBox != null && reportTypeComboBox.getScene() != null) {
            return getCurrentStageFromNode(reportTypeComboBox);
        }
        // Try fallback nodes
        return super.getCurrentStage(fallbackNodes);
    }
    
    // ========== Report Type Methods ==========
    @FXML
    private void showRevenueReports() {
        if (reportTypeComboBox != null) {
            reportTypeComboBox.setValue("Revenue");
        }
        generateReport();
    }
    
    @FXML
    private void showOccupancyReports() {
        if (reportTypeComboBox != null) {
            reportTypeComboBox.setValue("Occupancy");
        }
        generateReport();
    }
    
    @FXML
    private void showActivityLogs() {
        if (reportTypeComboBox != null) {
            reportTypeComboBox.setValue("Activity Logs");
        }
        // Show TXT export button for Activity Logs
        if (txtExportButton != null) {
            txtExportButton.setVisible(true);
            txtExportButton.setManaged(true);
        }
        generateReport();
    }
    
    @FXML
    private void showFeedbackSummary() {
        if (reportTypeComboBox != null) {
            reportTypeComboBox.setValue("Feedback Summary");
        }
        generateReport();
    }
    
    // ========== Report Generation Methods ==========
    @FXML
    private void generateReport() {
        if (reportTypeComboBox == null) return;
        
        String reportType = reportTypeComboBox.getValue();
        if (reportType == null) return;
        
        try {
            switch (reportType) {
                case "Revenue":
                    // Hide TXT export button for other reports
                    if (txtExportButton != null) {
                        txtExportButton.setVisible(false);
                        txtExportButton.setManaged(false);
                    }
                    generateRevenueReport();
                    break;
                case "Occupancy":
                    // Hide TXT export button for other reports
                    if (txtExportButton != null) {
                        txtExportButton.setVisible(false);
                        txtExportButton.setManaged(false);
                    }
                    generateOccupancyReport();
                    break;
                case "Activity Logs":
                    // Show TXT export button for Activity Logs
                    if (txtExportButton != null) {
                        txtExportButton.setVisible(true);
                        txtExportButton.setManaged(true);
                    }
                    generateActivityLogs();
                    break;
                case "Feedback Summary":
                    // Hide TXT export button for other reports
                    if (txtExportButton != null) {
                        txtExportButton.setVisible(false);
                        txtExportButton.setManaged(false);
                    }
                    generateFeedbackSummary();
                    break;
            }
        } catch (Exception e) {
            logger.logError("Failed to generate report: " + reportType, e);
            showAlert("Error", "Failed to generate report: " + e.getMessage());
        }
    }
    
    private void generateRevenueReport() {
        LocalDate start = startDatePicker != null ? startDatePicker.getValue() : null;
        LocalDate end = endDatePicker != null ? endDatePicker.getValue() : null;
        
        if (start == null || end == null) {
            showAlert("Error", "Please select both start and end dates");
            return;
        }
        
        if (start.isAfter(end)) {
            showAlert("Error", "Start date must be before or equal to end date");
            return;
        }
        
        // Get room type filter
        RoomType roomTypeFilter = null;
        if (roomTypeComboBox != null && roomTypeComboBox.getValue() != null) {
            String roomTypeStr = roomTypeComboBox.getValue();
            if (!roomTypeStr.equals("All")) {
                try {
                    roomTypeFilter = RoomType.valueOf(roomTypeStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.logWarning("Invalid room type filter: " + roomTypeStr);
                }
            }
        }
        
        // Generate report for the date range with optional room type filter
        ReportingService.RevenueReport report = reportingService.generateRevenueReport(start, end, "Custom", roomTypeFilter);
        
        // Prepare data for table
        Map<String, Object> row = new HashMap<>();
        String period = "Custom";
        int reservationCount = report.getReservationCount();
        String subtotal = String.format("%.2f", report.getSubtotal());
        String tax = String.format("%.2f", report.getTax());
        String discounts = String.format("%.2f", report.getDiscounts());
        String total = String.format("%.2f", report.getTotal());
        
        // Add both lowercase and original case keys for proper column mapping
        row.put("period", period);
        row.put("Period", period);
        row.put("reservationcount", reservationCount);
        row.put("Reservation Count", reservationCount);
        row.put("subtotal", subtotal);
        row.put("Subtotal", subtotal);
        row.put("tax", tax);
        row.put("Tax", tax);
        row.put("discounts", discounts);
        row.put("Discounts", discounts);
        row.put("total", total);
        row.put("Total", total);
        
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(row);
        
        List<String> columns = new ArrayList<>();
        columns.add("Period");
        columns.add("Reservation Count");
        columns.add("Subtotal");
        columns.add("Tax");
        columns.add("Discounts");
        columns.add("Total");
        
        populateTable(data, columns);
        
        logger.logInfo("Revenue Report: " + report.getReservationCount() + " reservations, Total: $" + report.getTotal());
    }
    
    private void generateOccupancyReport() {
        LocalDate startDate = startDatePicker != null ? startDatePicker.getValue() : LocalDate.now();
        LocalDate endDate = endDatePicker != null ? endDatePicker.getValue() : LocalDate.now();
        
        if (startDate == null || endDate == null) {
            showAlert("Error", "Please select both start and end dates");
            return;
        }
        
        if (startDate.isAfter(endDate)) {
            showAlert("Error", "Start date must be before or equal to end date");
            return;
        }
        
        // Get room type filter
        RoomType roomTypeFilter = null;
        if (roomTypeComboBox != null && roomTypeComboBox.getValue() != null) {
            String roomTypeStr = roomTypeComboBox.getValue();
            if (!roomTypeStr.equals("All")) {
                try {
                    roomTypeFilter = RoomType.valueOf(roomTypeStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    logger.logWarning("Invalid room type filter: " + roomTypeStr);
                }
            }
        }
        
        // Generate occupancy report by room type for the date range with optional filter
        List<ReportingService.RoomTypeOccupancyReport> reports = 
            reportingService.generateOccupancyReportByRoomType(startDate, endDate, roomTypeFilter);
        
        // Prepare data for table
        List<Map<String, Object>> data = new ArrayList<>();
        
        for (ReportingService.RoomTypeOccupancyReport report : reports) {
            Map<String, Object> row = new HashMap<>();
            row.put("fromdate", startDate.toString());
            row.put("From", startDate.toString());
            row.put("todate", endDate.toString());
            row.put("To", endDate.toString());
            row.put("roomtype", report.getRoomType().toString());
            row.put("Room Type", report.getRoomType().toString());
            row.put("totalrooms", report.getTotalRooms());
            row.put("Total Rooms", report.getTotalRooms());
            row.put("occupiedrooms", report.getOccupiedRooms());
            row.put("Occupied Rooms", report.getOccupiedRooms());
            row.put("availablerooms", report.getAvailableRooms());
            row.put("Available Rooms", report.getAvailableRooms());
            row.put("occupancypercent", String.format("%.2f", report.getOccupancyPercent()));
            row.put("occupancypercentage", String.format("%.2f", report.getOccupancyPercent()));
            row.put("Occupancy Percentage", String.format("%.2f", report.getOccupancyPercent()));
            data.add(row);
        }
        
        List<String> columns = new ArrayList<>();
        columns.add("From");
        columns.add("To");
        columns.add("Room Type");
        columns.add("Total Rooms");
        columns.add("Occupied Rooms");
        columns.add("Available Rooms");
        columns.add("Occupancy Percentage");
        
        populateTable(data, columns);
        
        int totalRooms = reports.stream().mapToInt(ReportingService.RoomTypeOccupancyReport::getTotalRooms).sum();
        int totalOccupied = reports.stream().mapToInt(ReportingService.RoomTypeOccupancyReport::getOccupiedRooms).sum();
        String dateRange = startDate.toString() + " to " + endDate.toString();
        logger.logInfo("Occupancy Report for " + dateRange + ": " + totalOccupied + " occupied out of " + totalRooms + " total rooms");
    }
    
    private void generateActivityLogs() {
        try {
            LocalDate start = startDatePicker != null ? startDatePicker.getValue() : null;
            LocalDate end = endDatePicker != null ? endDatePicker.getValue() : null;
            
            LocalDateTime startDateTime = start != null ? start.atStartOfDay() : null;
            LocalDateTime endDateTime = end != null ? end.atTime(23, 59, 59) : null;
            
            List<AuditLog> logs = reportingService.getActivityLogs(startDateTime, endDateTime);
            
            // Prepare data for table
            List<Map<String, Object>> data = new ArrayList<>();
            for (AuditLog log : logs) {
                Map<String, Object> row = new HashMap<>();
                String timestamp = log.getTimestamp() != null ? log.getTimestamp().toString() : "";
                String actor = log.getActor() != null ? log.getActor() : "";
                String action = log.getAction() != null ? log.getAction() : "";
                String entityType = log.getEntityType() != null ? log.getEntityType() : "";
                String entityId = log.getEntityId() != null ? log.getEntityId().toString() : "";
                String message = log.getMessage() != null ? log.getMessage() : "";
                
                // Add both lowercase and original case keys for flexible matching
                row.put("timestamp", timestamp);
                row.put("Timestamp", timestamp);
                row.put("actor", actor);
                row.put("Actor", actor);
                row.put("action", action);
                row.put("Action", action);
                row.put("entitytype", entityType);
                row.put("entityType", entityType);
                row.put("Entity Type", entityType);
                row.put("entityid", entityId);
                row.put("entityId", entityId);
                row.put("Entity ID", entityId);
                row.put("message", message);
                row.put("Message", message);
                data.add(row);
            }
            
            List<String> columns = new ArrayList<>();
            columns.add("Timestamp");
            columns.add("Actor");
            columns.add("Action");
            columns.add("Entity Type");
            columns.add("Entity ID");
            columns.add("Message");
            
            populateTable(data, columns);
            
            logger.logInfo("Activity Logs: " + logs.size() + " entries");
            
            if (logs.isEmpty()) {
                showAlert("Info", "No activity logs found for the selected date range.");
            }
        } catch (Exception e) {
            logger.logError("Failed to generate activity logs", e);
            showAlert("Error", "Failed to generate activity logs: " + e.getMessage());
        }
    }
    
    private void generateFeedbackSummary() {
        ReportingService.FeedbackSummary summary = reportingService.generateFeedbackSummary();
        
        // Get all feedback for detailed table
        List<com.hotel.model.Feedback> allFeedback = reportingService.getAllFeedback();
        
        // Prepare data for table
        List<Map<String, Object>> data = new ArrayList<>();
        for (com.hotel.model.Feedback feedback : allFeedback) {
            Map<String, Object> row = new HashMap<>();
            String reservationId = feedback.getReservation() != null ? 
                feedback.getReservation().getId().toString() : "";
            String guest = feedback.getGuest() != null ? feedback.getGuest().getName() : "";
            String rating = String.valueOf(feedback.getRating());
            String comment = feedback.getComments() != null ? feedback.getComments() : "";
            // Only show date part, not full timestamp
            String date = feedback.getCreatedAt() != null ? 
                feedback.getCreatedAt().toLocalDate().toString() : "";
            String sentimentTag = feedback.getSentimentTag() != null ? feedback.getSentimentTag() : "";
            
            // Add both lowercase and original case keys
            row.put("reservationid", reservationId);
            row.put("Reservation ID", reservationId);
            row.put("guest", guest);
            row.put("Guest", guest);
            row.put("rating", rating);
            row.put("Rating", rating);
            row.put("comment", comment);
            row.put("Comment", comment);
            row.put("date", date);
            row.put("Date", date);
            row.put("sentimenttag", sentimentTag);
            row.put("Sentiment Tag", sentimentTag);
            data.add(row);
        }
        
        List<String> columns = new ArrayList<>();
        columns.add("Reservation ID");
        columns.add("Guest");
        columns.add("Rating");
        columns.add("Comment");
        columns.add("Date");
        columns.add("Sentiment Tag");
        
        populateTable(data, columns);
        
        logger.logInfo("Feedback Summary: " + summary.getTotalCount() + " feedbacks, " + 
            "Average rating: " + summary.getAverageRating());
    }
    
    // ========== Export Methods ==========
    @FXML
    private void exportToCSV() {
        if (reportTypeComboBox == null) return;
        
        String reportType = reportTypeComboBox.getValue();
        if (reportType == null) {
            showAlert("Error", "Please select a report type");
            return;
        }
        
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export to CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fileChooser.setInitialFileName("report_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".csv");
            
            Stage stage = (Stage) (reportTable != null ? reportTable.getScene().getWindow() : null);
            if (stage == null) return;
            
            File file = fileChooser.showSaveDialog(stage);
            if (file == null) return;
            
            List<Map<String, Object>> data = prepareExportData(reportType);
            
            switch (reportType) {
                case "Revenue":
                    CsvExporter.exportRevenueReport(file.getAbsolutePath(), data);
                    break;
                case "Occupancy":
                    CsvExporter.exportOccupancyReport(file.getAbsolutePath(), data);
                    break;
                case "Activity Logs":
                    CsvExporter.exportActivityLogs(file.getAbsolutePath(), data);
                    break;
                case "Feedback Summary":
                    CsvExporter.exportFeedbackSummary(file.getAbsolutePath(), data);
                    break;
            }
            
            showAlert("Success", "Report exported to CSV successfully!");
            logger.logInfo("Exported " + reportType + " report to CSV: " + file.getAbsolutePath());
            
        } catch (Exception e) {
            logger.logError("Failed to export to CSV", e);
            showAlert("Error", "Failed to export: " + e.getMessage());
        }
    }
    
    @FXML
    private void exportToPDF() {
        if (reportTypeComboBox == null) return;
        
        String reportType = reportTypeComboBox.getValue();
        if (reportType == null) {
            showAlert("Error", "Please select a report type");
            return;
        }
        
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export to PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            fileChooser.setInitialFileName("report_" + reportType.replace(" ", "_") + "_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".pdf");
            
            Stage stage = (Stage) (reportTable != null ? reportTable.getScene().getWindow() : null);
            if (stage == null) return;
            
            File file = fileChooser.showSaveDialog(stage);
            if (file == null) return;
            
            List<Map<String, Object>> data = prepareExportData(reportType);
            
            switch (reportType) {
                case "Revenue":
                    PdfExporter.exportRevenueReport(file.getAbsolutePath(), data);
                    break;
                case "Occupancy":
                    PdfExporter.exportOccupancyReport(file.getAbsolutePath(), data);
                    break;
                case "Activity Logs":
                    PdfExporter.exportActivityLogs(file.getAbsolutePath(), data);
                    break;
                case "Feedback Summary":
                    PdfExporter.exportFeedbackSummary(file.getAbsolutePath(), data);
                    break;
                default:
                    showAlert("Error", "PDF export not supported for this report type");
                    return;
            }
            
            showAlert("Success", "Report exported to PDF successfully!");
            logger.logInfo("Exported " + reportType + " report to PDF: " + file.getAbsolutePath());
            
        } catch (Exception e) {
            logger.logError("Failed to export to PDF", e);
            showAlert("Error", "Failed to export: " + e.getMessage());
        }
    }
    
    @FXML
    private void exportToTXT() {
        if (reportTypeComboBox == null) return;
        
        String reportType = reportTypeComboBox.getValue();
        if (reportType == null || !reportType.equals("Activity Logs")) {
            showAlert("Error", "TXT export only available for Activity Logs");
            return;
        }
        
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export to TXT");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT Files", "*.txt"));
            fileChooser.setInitialFileName("activity_logs_" + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".txt");
            
            Stage stage = (Stage) (reportTable != null ? reportTable.getScene().getWindow() : null);
            if (stage == null) return;
            
            File file = fileChooser.showSaveDialog(stage);
            if (file == null) return;
            
            List<Map<String, Object>> data = prepareExportData(reportType);
            TxtExporter.exportActivityLogs(file.getAbsolutePath(), data);
            
            showAlert("Success", "Activity logs exported to TXT successfully!");
            logger.logInfo("Exported activity logs to TXT: " + file.getAbsolutePath());
            
        } catch (Exception e) {
            logger.logError("Failed to export to TXT", e);
            showAlert("Error", "Failed to export: " + e.getMessage());
        }
    }
    
    private List<Map<String, Object>> prepareExportData(String reportType) {
        List<Map<String, Object>> data = new ArrayList<>();
        
        // This is a simplified version - actual implementation would get data from services
        // and format it properly for export
        
        switch (reportType) {
            case "Revenue": {
                // Get revenue data from ReportingService
                LocalDate start = startDatePicker != null ? startDatePicker.getValue() : LocalDate.now();
                LocalDate end = endDatePicker != null ? endDatePicker.getValue() : LocalDate.now();
                
                // Get room type filter
                RoomType roomTypeFilter = null;
                if (roomTypeComboBox != null && roomTypeComboBox.getValue() != null) {
                    String roomTypeStr = roomTypeComboBox.getValue();
                    if (!roomTypeStr.equals("All")) {
                        try {
                            roomTypeFilter = RoomType.valueOf(roomTypeStr.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            // Use null if invalid
                        }
                    }
                }
                
                ReportingService.RevenueReport revenueReport = reportingService.generateRevenueReport(start, end, "Custom", roomTypeFilter);
                
                Map<String, Object> revenueRow = new HashMap<>();
                revenueRow.put("period", "Custom");
                revenueRow.put("reservationCount", revenueReport.getReservationCount());
                revenueRow.put("subtotal", revenueReport.getSubtotal());
                revenueRow.put("tax", revenueReport.getTax());
                revenueRow.put("discounts", revenueReport.getDiscounts());
                revenueRow.put("total", revenueReport.getTotal());
                data.add(revenueRow);
                break;
            }
                
            case "Occupancy": {
                LocalDate startDate = startDatePicker != null ? startDatePicker.getValue() : LocalDate.now();
                LocalDate endDate = endDatePicker != null ? endDatePicker.getValue() : LocalDate.now();
                if (startDate == null || endDate == null) {
                    break;
                }
                
                // Get room type filter for export
                RoomType roomTypeFilter = null;
                if (roomTypeComboBox != null && roomTypeComboBox.getValue() != null) {
                    String roomTypeStr = roomTypeComboBox.getValue();
                    if (!roomTypeStr.equals("All")) {
                        try {
                            roomTypeFilter = RoomType.valueOf(roomTypeStr.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            // Use null if invalid
                        }
                    }
                }
                
                List<ReportingService.RoomTypeOccupancyReport> occupancyReports = 
                    reportingService.generateOccupancyReportByRoomType(startDate, endDate, roomTypeFilter);
                
                for (ReportingService.RoomTypeOccupancyReport report : occupancyReports) {
                    Map<String, Object> occupancyRow = new HashMap<>();
                    occupancyRow.put("fromdate", startDate.toString());
                    occupancyRow.put("todate", endDate.toString());
                    occupancyRow.put("roomtype", report.getRoomType().toString());
                    occupancyRow.put("totalrooms", report.getTotalRooms());
                    occupancyRow.put("occupiedrooms", report.getOccupiedRooms());
                    occupancyRow.put("availablerooms", report.getAvailableRooms());
                    occupancyRow.put("occupancypercent", report.getOccupancyPercent());
                    data.add(occupancyRow);
                }
                break;
            }
                
            case "Activity Logs":
                LocalDateTime startDateTime = startDatePicker != null && startDatePicker.getValue() != null ? 
                    startDatePicker.getValue().atStartOfDay() : null;
                LocalDateTime endDateTime = endDatePicker != null && endDatePicker.getValue() != null ? 
                    endDatePicker.getValue().atTime(23, 59, 59) : null;
                
                List<AuditLog> logs = reportingService.getActivityLogs(startDateTime, endDateTime);
                for (AuditLog log : logs) {
                    Map<String, Object> logRow = new HashMap<>();
                    logRow.put("timestamp", log.getTimestamp() != null ? log.getTimestamp().toString() : "");
                    logRow.put("actor", log.getActor() != null ? log.getActor() : "");
                    logRow.put("action", log.getAction() != null ? log.getAction() : "");
                    logRow.put("entityType", log.getEntityType() != null ? log.getEntityType() : "");
                    logRow.put("entityId", log.getEntityId() != null ? log.getEntityId() : null);
                    logRow.put("message", log.getMessage() != null ? log.getMessage() : "");
                    data.add(logRow);
                }
                break;
                
            case "Feedback Summary":
                // Get feedback data
                List<com.hotel.model.Feedback> allFeedback = reportingService.getAllFeedback();
                for (com.hotel.model.Feedback feedback : allFeedback) {
                    Map<String, Object> logRow = new HashMap<>();
                    logRow.put("reservationId", feedback.getReservation() != null ? 
                        feedback.getReservation().getId() : null);
                    logRow.put("guest", feedback.getGuest() != null ? feedback.getGuest().getName() : "");
                    logRow.put("rating", feedback.getRating());
                    logRow.put("comment", feedback.getComments() != null ? feedback.getComments() : "");
                    // Only show date part, not full timestamp
                    logRow.put("date", feedback.getCreatedAt() != null ? 
                        feedback.getCreatedAt().toLocalDate().toString() : "");
                    logRow.put("sentimentTag", feedback.getSentimentTag() != null ? feedback.getSentimentTag() : "");
                    data.add(logRow);
                }
                break;
        }
        
        return data;
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
