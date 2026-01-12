package com.hotel.controller.helper;

import com.hotel.model.PricingModel;
import com.hotel.model.ServiceAddon;
import com.hotel.repository.AddonRepository;
import com.hotel.util.LoggerService;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

//
 // Helper class for add-on services screen logic in KioskController.
 // Extracts add-on calculation and display logic.
//
public final class KioskAddOnHelper {
    
    private KioskAddOnHelper() {
        // Utility class - prevent instantiation
    }
    
    //
     // Calculates add-on total and updates selected add-ons list.
//
    public static AddOnCalculationResult calculateAddOnTotal(
            CheckBox wifiCheckBox,
            CheckBox breakfastCheckBox,
            CheckBox parkingCheckBox,
            CheckBox spaCheckBox,
            LocalDate checkIn,
            LocalDate checkOut,
            AddonRepository addonRepository,
            LoggerService logger) {
        
        AddOnCalculationResult result = new AddOnCalculationResult();
        result.selectedAddons = new ArrayList<>();
        
        if (checkIn == null || checkOut == null) {
            logger.logWarning("checkIn or checkOut is null, cannot calculate addon total");
            result.total = 0.0;
            return result;
        }
        
        long numNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        logger.logInfo("Number of nights: " + numNights);
        
        List<ServiceAddon> allAddons = addonRepository.findAll();
        logger.logInfo("Found " + allAddons.size() + " addons in repository");
        
        // Collect selected add-ons
        if (wifiCheckBox != null && wifiCheckBox.isSelected()) {
            allAddons.stream().filter(a -> a.getName().equalsIgnoreCase("Wi-Fi"))
                .findFirst().ifPresent(addon -> {
                    result.selectedAddons.add(addon);
                    logger.logInfo("Wi-Fi addon selected: " + addon.getPrice());
                });
        }
        if (breakfastCheckBox != null && breakfastCheckBox.isSelected()) {
            allAddons.stream().filter(a -> a.getName().equalsIgnoreCase("Breakfast"))
                .findFirst().ifPresent(addon -> {
                    result.selectedAddons.add(addon);
                    logger.logInfo("Breakfast addon selected: " + addon.getPrice());
                });
        }
        if (parkingCheckBox != null && parkingCheckBox.isSelected()) {
            allAddons.stream().filter(a -> a.getName().equalsIgnoreCase("Parking"))
                .findFirst().ifPresent(addon -> {
                    result.selectedAddons.add(addon);
                    logger.logInfo("Parking addon selected: " + addon.getPrice());
                });
        }
        if (spaCheckBox != null && spaCheckBox.isSelected()) {
            allAddons.stream().filter(a -> a.getName().equalsIgnoreCase("Spa Access"))
                .findFirst().ifPresent(addon -> {
                    result.selectedAddons.add(addon);
                    logger.logInfo("Spa Access addon selected: " + addon.getPrice());
                });
        }
        
        // Calculate total
        double addonTotal = 0.0;
        for (ServiceAddon addon : result.selectedAddons) {
            double addonPrice = 0.0;
            if (addon.getPricingModel() == PricingModel.PER_NIGHT) {
                addonPrice = addon.getPrice() * numNights;
                logger.logInfo("Addon " + addon.getName() + " (PER_NIGHT): " + addon.getPrice() + " x " + numNights + " = " + addonPrice);
            } else {
                addonPrice = addon.getPrice();
                logger.logInfo("Addon " + addon.getName() + " (PER_RESERVATION): " + addon.getPrice());
            }
            addonTotal += addonPrice;
        }
        
        result.total = addonTotal;
        logger.logInfo("Total addon cost: $" + String.format("%.2f", addonTotal));
        
        return result;
    }
    
    //
     // Gets individual add-on price and calculation text.
//
    public static AddOnPriceInfo getAddOnPriceInfo(
            ServiceAddon addon,
            LocalDate checkIn,
            LocalDate checkOut) {
        
        AddOnPriceInfo info = new AddOnPriceInfo();
        
        if (checkIn == null || checkOut == null) {
            info.price = 0.0;
            info.calculationText = "";
            return info;
        }
        
        long numNights = ChronoUnit.DAYS.between(checkIn, checkOut);
        
        if (addon.getPricingModel() == PricingModel.PER_NIGHT) {
            info.price = addon.getPrice() * numNights;
            info.calculationText = String.format("$%.2f × %d nights = $%.2f", addon.getPrice(), numNights, info.price);
        } else {
            info.price = addon.getPrice();
            info.calculationText = String.format("One-time charge: $%.2f", info.price);
        }
        
        return info;
    }
    
    //
     // Updates individual add-on price labels.
//
    public static void updateIndividualAddOnPrice(
            String addonName,
            double price,
            String calculationText,
            Label wifiPriceLabel,
            Label breakfastPriceLabel,
            Label parkingPriceLabel,
            Label spaPriceLabel,
            Label wifiCalculationLabel,
            Label breakfastCalculationLabel,
            Label parkingCalculationLabel,
            Label spaCalculationLabel) {
        
        Label priceLabel = null;
        Label calculationLabel = null;
        
        switch (addonName.toLowerCase()) {
            case "wi-fi":
                priceLabel = wifiPriceLabel;
                calculationLabel = wifiCalculationLabel;
                break;
            case "breakfast":
                priceLabel = breakfastPriceLabel;
                calculationLabel = breakfastCalculationLabel;
                break;
            case "parking":
                priceLabel = parkingPriceLabel;
                calculationLabel = parkingCalculationLabel;
                break;
            case "spa access":
                priceLabel = spaPriceLabel;
                calculationLabel = spaCalculationLabel;
                break;
        }
        
        if (priceLabel != null) {
            priceLabel.setText("$" + String.format("%.2f", price));
            priceLabel.setVisible(true);
        }
        if (calculationLabel != null) {
            calculationLabel.setText(calculationText);
            calculationLabel.setVisible(true);
        }
    }
    
    //
     // Resets all individual add-on price labels.
//
    public static void resetAddOnPriceLabels(
            Label wifiPriceLabel,
            Label breakfastPriceLabel,
            Label parkingPriceLabel,
            Label spaPriceLabel,
            Label wifiCalculationLabel,
            Label breakfastCalculationLabel,
            Label parkingCalculationLabel,
            Label spaCalculationLabel) {
        
        if (wifiPriceLabel != null) {
            wifiPriceLabel.setText("$0.00");
            wifiPriceLabel.setVisible(false);
        }
        if (breakfastPriceLabel != null) {
            breakfastPriceLabel.setText("$0.00");
            breakfastPriceLabel.setVisible(false);
        }
        if (parkingPriceLabel != null) {
            parkingPriceLabel.setText("$0.00");
            parkingPriceLabel.setVisible(false);
        }
        if (spaPriceLabel != null) {
            spaPriceLabel.setText("$0.00");
            spaPriceLabel.setVisible(false);
        }
        if (wifiCalculationLabel != null) {
            wifiCalculationLabel.setText("");
            wifiCalculationLabel.setVisible(false);
        }
        if (breakfastCalculationLabel != null) {
            breakfastCalculationLabel.setText("");
            breakfastCalculationLabel.setVisible(false);
        }
        if (parkingCalculationLabel != null) {
            parkingCalculationLabel.setText("");
            parkingCalculationLabel.setVisible(false);
        }
        if (spaCalculationLabel != null) {
            spaCalculationLabel.setText("");
            spaCalculationLabel.setVisible(false);
        }
    }
    
    // ========== Inner Classes for Return Values ==========
    
    //
     // Result of add-on calculation.
//
    public static class AddOnCalculationResult {
        public double total = 0.0;
        public List<ServiceAddon> selectedAddons = new ArrayList<>();
    }
    
    //
     // Price information for an add-on.
//
    public static class AddOnPriceInfo {
        public double price = 0.0;
        public String calculationText = "";
    }
}

