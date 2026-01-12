package com.hotel.controller;

import com.hotel.app.AppConfig;
import com.hotel.controller.base.BaseController;
import com.hotel.controller.helper.AlertHelper;
import com.hotel.controller.helper.AdminNavigationHelper;
import com.hotel.model.AdminUser;
import com.hotel.model.Feedback;
import com.hotel.repository.FeedbackRepository;
import com.hotel.util.LoggerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//
 // Controller for viewing and managing guest feedback.
 // Displays all feedback with filtering by rating, date, sentiment, and guest name.
 // Shows statistics like average rating and total feedback count.
//
public class AdminFeedbackController extends BaseController {

    @FXML private ComboBox<String> ratingFilterComboBox;
    @FXML private DatePicker feedbackStartDatePicker;
    @FXML private DatePicker feedbackEndDatePicker;
    @FXML private ComboBox<String> sentimentFilterComboBox;
    @FXML private TextField guestFilterField;
    @FXML private Label averageRatingLabel;
    @FXML private Label totalFeedbackLabel;
    @FXML private Label issueTagsLabel;
    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, String> reservationIdColumn;
    @FXML private TableColumn<Feedback, String> guestNameColumn;
    @FXML private TableColumn<Feedback, Integer> ratingColumn;
    @FXML private TableColumn<Feedback, String> commentColumn;
    @FXML private TableColumn<Feedback, LocalDate> dateColumn;
    @FXML private TableColumn<Feedback, String> sentimentColumn;

    private final LoggerService logger = LoggerService.getInstance();
    private FeedbackRepository feedbackRepository;
    private AdminUser currentUser;
    private List<Feedback> allFeedback = new ArrayList<>();

    @FXML
    private void initialize() {
        feedbackRepository = AppConfig.createFeedbackRepository();
        if (ratingFilterComboBox != null) {
            ratingFilterComboBox.getItems().addAll("All", "1", "2", "3", "4", "5");
            ratingFilterComboBox.setValue("All");
        }
        if (sentimentFilterComboBox != null) {
            sentimentFilterComboBox.getItems().addAll("All", "Positive", "Negative", "Neutral", "Complaint", "Praise");
            sentimentFilterComboBox.setValue("All");
        }
    }

    public void init(AdminUser user) {
        this.currentUser = user;
        loadFeedbackData();
    }

    private void loadFeedbackData() {
        try {
            allFeedback = feedbackRepository.findAll();
            displayFeedback(allFeedback);
            updateFeedbackStats();
        } catch (Exception e) {
            logger.logError("Failed to load feedback data", e);
            AlertHelper.showError("Error", "Failed to load feedback: " + e.getMessage());
        }
    }

    @FXML
    private void filterFeedback() {
        try {
            List<Feedback> filtered = new ArrayList<>(allFeedback);
            filtered = applyRatingFilter(filtered);
            filtered = applyDateFilter(filtered);
            filtered = applySentimentFilter(filtered);
            filtered = applyGuestFilter(filtered);
            displayFeedback(filtered);
            updateFeedbackStats(filtered);
        } catch (Exception e) {
            logger.logError("Failed to filter feedback", e);
            AlertHelper.showError("Error", "Failed to filter feedback: " + e.getMessage());
        }
    }

    @FXML
    private void clearFeedbackFilters() {
        if (ratingFilterComboBox != null) ratingFilterComboBox.setValue("All");
        if (feedbackStartDatePicker != null) feedbackStartDatePicker.setValue(null);
        if (feedbackEndDatePicker != null) feedbackEndDatePicker.setValue(null);
        if (sentimentFilterComboBox != null) sentimentFilterComboBox.setValue("All");
        if (guestFilterField != null) guestFilterField.clear();
        displayFeedback(allFeedback);
        updateFeedbackStats();
    }

    @FXML
    private void exportFeedbackToCSV() {
        try {
            if (feedbackTable == null || feedbackTable.getItems().isEmpty()) {
                AlertHelper.showError("Error", "No feedback data to export");
                return;
            }
            
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Export Feedback to CSV");
            fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            fileChooser.setInitialFileName("feedback_" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_DATE) + ".csv");
            
            Stage stage = getCurrentStage();
            java.io.File file = fileChooser.showSaveDialog(stage);
            if (file == null) return;
            
            // Get current filtered feedback or all feedback
            List<Feedback> feedbackToExport = new ArrayList<>(feedbackTable.getItems());
            
            // Use CsvExporter to export
            com.hotel.util.CsvExporter exporter = new com.hotel.util.CsvExporter();
            exporter.exportFeedback(feedbackToExport, file.getAbsolutePath());
            
            AlertHelper.showInfo("Success", "Feedback exported to CSV successfully!");
            logger.logActivity(currentUser != null ? currentUser.getUsername() : "ADMIN",
                "EXPORT_FEEDBACK", "Feedback", null,
                "Exported " + feedbackToExport.size() + " feedback entries to CSV");
        } catch (Exception e) {
            logger.logError("Failed to export feedback to CSV", e);
            AlertHelper.showError("Error", "Failed to export feedback: " + e.getMessage());
        }
    }

    @FXML
    @Override
    protected void goBack() {
        try {
            Stage stage = getCurrentStage();
            AdminNavigationHelper.switchScene(stage, "/view/admin/Dashboard.fxml", controller -> {
                if (controller instanceof AdminDashboardController dashboardController) {
                    dashboardController.init(currentUser);
                }
            });
        } catch (Exception e) {
            logger.logError("Failed to navigate back to dashboard", e);
            AlertHelper.showError("Navigation Error", "Failed to load dashboard: " + e.getMessage());
        }
    }

    private List<Feedback> applyRatingFilter(List<Feedback> feedback) {
        if (ratingFilterComboBox != null && ratingFilterComboBox.getValue() != null &&
            !"All".equals(ratingFilterComboBox.getValue())) {
            try {
                int rating = Integer.parseInt(ratingFilterComboBox.getValue());
                return feedback.stream().filter(f -> f.getRating() == rating).collect(Collectors.toList());
            } catch (NumberFormatException ignore) {
            }
        }
        return feedback;
    }

    private List<Feedback> applyDateFilter(List<Feedback> feedback) {
        if (feedbackStartDatePicker != null && feedbackEndDatePicker != null) {
            LocalDate start = feedbackStartDatePicker.getValue();
            LocalDate end = feedbackEndDatePicker.getValue();
            if (start != null && end != null) {
                return feedback.stream()
                    .filter(f -> !f.getCreatedAt().toLocalDate().isBefore(start) &&
                        !f.getCreatedAt().toLocalDate().isAfter(end))
                    .collect(Collectors.toList());
            }
        }
        return feedback;
    }

    private List<Feedback> applySentimentFilter(List<Feedback> feedback) {
        if (sentimentFilterComboBox != null && sentimentFilterComboBox.getValue() != null &&
            !"All".equals(sentimentFilterComboBox.getValue())) {
            String sentiment = sentimentFilterComboBox.getValue();
            return feedback.stream()
                .filter(f -> sentiment.equalsIgnoreCase(f.getSentimentTag()))
                .collect(Collectors.toList());
        }
        return feedback;
    }

    private List<Feedback> applyGuestFilter(List<Feedback> feedback) {
        if (guestFilterField != null && !guestFilterField.getText().isBlank()) {
            String guestQuery = guestFilterField.getText().toLowerCase();
            return feedback.stream()
                .filter(f -> f.getGuest() != null &&
                    f.getGuest().getName().toLowerCase().contains(guestQuery))
                .collect(Collectors.toList());
        }
        return feedback;
    }

    private void displayFeedback(List<Feedback> feedback) {
        if (feedbackTable == null) return;
        // Disable the filler column
        feedbackTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Configure table columns
        if (reservationIdColumn != null) {
            reservationIdColumn.setCellValueFactory(cell -> {
                Feedback f = cell.getValue();
                return new SimpleStringProperty(
                    f.getReservation() != null ? String.valueOf(f.getReservation().getId()) : "N/A");
            });
        }
        if (guestNameColumn != null) {
            guestNameColumn.setCellValueFactory(cell -> {
                Feedback f = cell.getValue();
                return new SimpleStringProperty(
                    f.getGuest() != null ? f.getGuest().getName() : "N/A");
            });
        }
        if (ratingColumn != null) {
            ratingColumn.setCellValueFactory(cell -> {
                Feedback f = cell.getValue();
                return new SimpleIntegerProperty(f.getRating()).asObject();
            });
        }
        if (commentColumn != null) {
            commentColumn.setCellValueFactory(cell -> {
                Feedback f = cell.getValue();
                return new SimpleStringProperty(
                    f.getComments() != null ? f.getComments() : "");
            });
        }
        if (dateColumn != null) {
            dateColumn.setCellValueFactory(cell -> {
                Feedback f = cell.getValue();
                return new SimpleObjectProperty<>(
                    f.getCreatedAt() != null ? f.getCreatedAt().toLocalDate() : null);
            });
        }
        if (sentimentColumn != null) {
            sentimentColumn.setCellValueFactory(cell -> {
                Feedback f = cell.getValue();
                return new SimpleStringProperty(
                    f.getSentimentTag() != null ? f.getSentimentTag() : "N/A");
            });
        }
        
        ObservableList<Feedback> data = FXCollections.observableArrayList(feedback);
        feedbackTable.setItems(data);
    }

    private void updateFeedbackStats() {
        updateFeedbackStats(allFeedback);
    }

    private void updateFeedbackStats(List<Feedback> feedback) {
        if (totalFeedbackLabel != null) {
            totalFeedbackLabel.setText(String.valueOf(feedback.size()));
        }
        if (averageRatingLabel != null) {
            double average = feedback.stream().mapToInt(Feedback::getRating).average().orElse(0.0);
            averageRatingLabel.setText(String.format("%.2f", average));
        }
        if (issueTagsLabel != null) {
            String tags = feedback.stream()
                .map(Feedback::getSentimentTag)
                .filter(tag -> tag != null && !tag.isBlank())
                .collect(Collectors.joining(", "));
            issueTagsLabel.setText(tags.isBlank() ? "No common tags" : tags);
        }
    }

    //
     // Gets the current stage for navigation.
     // Overrides base method to use admin-specific fields.
//
    @Override
    protected Stage getCurrentStage(Node... fallbackNodes) {
        // Try admin-specific fields first
        if (feedbackTable != null && feedbackTable.getScene() != null) {
            return getCurrentStageFromNode(feedbackTable);
        }
        if (ratingFilterComboBox != null && ratingFilterComboBox.getScene() != null) {
            return getCurrentStageFromNode(ratingFilterComboBox);
        }
        // Try fallback nodes
        return super.getCurrentStage(fallbackNodes);
    }
}

