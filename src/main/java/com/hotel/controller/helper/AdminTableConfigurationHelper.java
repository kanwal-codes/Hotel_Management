package com.hotel.controller.helper;

import com.hotel.model.PricingModel;
import com.hotel.model.ReservationAddon;
import com.hotel.model.RoomType;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.text.NumberFormat;

//
 // Helper class for configuring tables in AdminReservationController.
 // Handles table column setup and cell value factories.
 // Extracted from AdminReservationController to reduce controller size.
//
public final class AdminTableConfigurationHelper {
    
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    
    private AdminTableConfigurationHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Configures the service/addon table columns.
//
    public static void configureServiceTable(
            TableView<ReservationAddon> servicesTable,
            ObservableList<ReservationAddon> serviceTableData,
            TableColumn<ReservationAddon, String> serviceNameColumn,
            TableColumn<ReservationAddon, String> servicePriceColumn,
            TableColumn<ReservationAddon, String> servicePricingModelColumn,
            TableColumn<ReservationAddon, String> serviceQuantityColumn) {
        
        if (servicesTable == null) return;
        
        servicesTable.setItems(serviceTableData);
        servicesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        if (serviceNameColumn != null) {
            serviceNameColumn.setCellValueFactory(cell -> 
                new SimpleStringProperty(cell.getValue().getAddon() != null ? 
                    cell.getValue().getAddon().getName() : "N/A"));
        }
        
        if (servicePriceColumn != null) {
            servicePriceColumn.setCellValueFactory(cell -> {
                if (cell.getValue().getAddon() != null) {
                    double price = cell.getValue().getAddon().getPrice();
                    return new SimpleStringProperty(currencyFormat.format(price));
                }
                return new SimpleStringProperty("N/A");
            });
        }
        
        if (servicePricingModelColumn != null) {
            servicePricingModelColumn.setCellValueFactory(cell -> {
                if (cell.getValue().getAddon() != null) {
                    PricingModel model = cell.getValue().getAddon().getPricingModel();
                    return new SimpleStringProperty(model == PricingModel.PER_NIGHT ? "Per Night" : "Per Reservation");
                }
                return new SimpleStringProperty("N/A");
            });
        }
        
        if (serviceQuantityColumn != null) {
            serviceQuantityColumn.setCellValueFactory(cell -> 
                new SimpleStringProperty(String.valueOf(cell.getValue().getQuantity())));
        }
    }
    
    //
     // Configures the room type summary table columns.
//
    @SuppressWarnings("unchecked")
    public static void configureRoomTable(
            TableView<?> roomsTable,
            ObservableList<?> roomTypeSummaryData,
            TableColumn<?, String> roomTypeColumn,
            TableColumn<?, Integer> roomCountColumn,
            TableColumn<?, Integer> roomCapacityColumn,
            TableColumn<?, Integer> roomTotalCapacityColumn) {
        
        if (roomsTable == null) return;
        
        // Note: Using raw types here because RoomTypeSummary is an inner class
        // This is a limitation of extracting from controller
        // Unchecked cast needed due to wildcard type erasure
        ((TableView<Object>) roomsTable).setItems((ObservableList<Object>) roomTypeSummaryData);
        roomsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        if (roomTypeColumn != null) {
            @SuppressWarnings("unchecked")
            TableColumn<Object, String> typedColumn = (TableColumn<Object, String>) roomTypeColumn;
            typedColumn.setCellValueFactory(cell -> {
                Object value = cell.getValue();
                if (value != null) {
                    try {
                        java.lang.reflect.Method getType = value.getClass().getMethod("getType");
                        Object type = getType.invoke(value);
                        return new SimpleStringProperty(type != null ? type.toString() : "");
                    } catch (Exception e) {
                        return new SimpleStringProperty("");
                    }
                }
                return new SimpleStringProperty("");
            });
        }
        
        if (roomCountColumn != null) {
            @SuppressWarnings("unchecked")
            TableColumn<Object, Integer> typedColumn = (TableColumn<Object, Integer>) roomCountColumn;
            typedColumn.setCellValueFactory(cell -> {
                Object value = cell.getValue();
                if (value != null) {
                    try {
                        java.lang.reflect.Method getCount = value.getClass().getMethod("getCount");
                        Integer count = (Integer) getCount.invoke(value);
                        return new SimpleIntegerProperty(count != null ? count : 0).asObject();
                    } catch (Exception e) {
                        return new SimpleIntegerProperty(0).asObject();
                    }
                }
                return new SimpleIntegerProperty(0).asObject();
            });
        }
        
        if (roomCapacityColumn != null) {
            @SuppressWarnings("unchecked")
            TableColumn<Object, Integer> typedColumn = (TableColumn<Object, Integer>) roomCapacityColumn;
            typedColumn.setCellValueFactory(cell -> {
                Object value = cell.getValue();
                if (value != null) {
                    try {
                        java.lang.reflect.Method getCapacity = value.getClass().getMethod("getCapacity");
                        Integer capacity = (Integer) getCapacity.invoke(value);
                        return new SimpleIntegerProperty(capacity != null ? capacity : 0).asObject();
                    } catch (Exception e) {
                        return new SimpleIntegerProperty(0).asObject();
                    }
                }
                return new SimpleIntegerProperty(0).asObject();
            });
        }
        
        if (roomTotalCapacityColumn != null) {
            @SuppressWarnings("unchecked")
            TableColumn<Object, Integer> typedColumn = (TableColumn<Object, Integer>) roomTotalCapacityColumn;
            typedColumn.setCellValueFactory(cell -> {
                Object value = cell.getValue();
                if (value != null) {
                    try {
                        java.lang.reflect.Method getTotalCapacity = value.getClass().getMethod("getTotalCapacity");
                        Integer totalCapacity = (Integer) getTotalCapacity.invoke(value);
                        return new SimpleIntegerProperty(totalCapacity != null ? totalCapacity : 0).asObject();
                    } catch (Exception e) {
                        return new SimpleIntegerProperty(0).asObject();
                    }
                }
                return new SimpleIntegerProperty(0).asObject();
            });
        }
    }
}


