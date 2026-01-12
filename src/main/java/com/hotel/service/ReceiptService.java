package com.hotel.service;

import com.hotel.model.Billing;
import com.hotel.model.Payment;
import com.hotel.model.Reservation;
import com.hotel.model.ReservationRoom;
import com.hotel.model.ServiceAddon;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

// generates pdf receipts for hotel reservations
// creates a formatted receipt with all reservation details, charges, and payment information
public class ReceiptService {
    
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    
    // generates a pdf receipt for a reservation
    // includes guest info, room details, charges, discounts, payments, and balance
    public static void generateReceipt(Reservation reservation, Billing billing, String filePath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                float margin = 50;
                float yPosition = page.getMediaBox().getHeight() - margin;
                float lineHeight = 20;
                float currentY = yPosition;
                
                // title
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("HOTEL RECEIPT");
                contentStream.endText();
                currentY -= lineHeight * 2;
                
                // hotel information
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Hotel Reservation System");
                contentStream.endText();
                currentY -= lineHeight;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Receipt Date: " + java.time.LocalDate.now().format(dateFormatter));
                contentStream.endText();
                currentY -= lineHeight * 2;
                
                // reservation information
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Reservation Details");
                contentStream.endText();
                currentY -= lineHeight * 1.5f;
                
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                
                if (reservation.getGuest() != null) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, currentY);
                    contentStream.showText("Guest Name: " + reservation.getGuest().getName());
                    contentStream.endText();
                    currentY -= lineHeight;
                    
                    if (reservation.getGuest().getEmail() != null && !reservation.getGuest().getEmail().isEmpty()) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin, currentY);
                        contentStream.showText("Email: " + reservation.getGuest().getEmail());
                        contentStream.endText();
                        currentY -= lineHeight;
                    }
                    
                    if (reservation.getGuest().getPhone() != null && !reservation.getGuest().getPhone().isEmpty()) {
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin, currentY);
                        contentStream.showText("Phone: " + reservation.getGuest().getPhone());
                        contentStream.endText();
                        currentY -= lineHeight;
                    }
                }
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Reservation #: " + reservation.getId());
                contentStream.endText();
                currentY -= lineHeight;
                
                if (reservation.getConfirmationNumber() != null) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, currentY);
                    contentStream.showText("Confirmation #: " + reservation.getConfirmationNumber());
                    contentStream.endText();
                    currentY -= lineHeight;
                }
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Check-in: " + reservation.getCheckIn().format(dateFormatter));
                contentStream.endText();
                currentY -= lineHeight;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Check-out: " + reservation.getCheckOut().format(dateFormatter));
                contentStream.endText();
                currentY -= lineHeight;
                
                long nights = java.time.temporal.ChronoUnit.DAYS.between(reservation.getCheckIn(), reservation.getCheckOut());
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Nights: " + nights);
                contentStream.endText();
                currentY -= lineHeight * 1.5f;
                
                // rooms
                if (reservation.getReservationRooms() != null && !reservation.getReservationRooms().isEmpty()) {
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, currentY);
                    contentStream.showText("Rooms:");
                    contentStream.endText();
                    currentY -= lineHeight;
                    
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    for (ReservationRoom rr : reservation.getReservationRooms()) {
                        String roomInfo = "Room " + rr.getRoom().getRoomNumber() + " (" + rr.getRoom().getType() + ")";
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin + 10, currentY);
                        contentStream.showText(roomInfo);
                        contentStream.endText();
                        currentY -= lineHeight;
                    }
                    currentY -= lineHeight * 0.5f;
                }
                
                // charges section
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Charges");
                contentStream.endText();
                currentY -= lineHeight * 1.5f;
                
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                
                // room charges
                double roomCharges = 0.0;
                if (reservation.getReservationRooms() != null) {
                    for (ReservationRoom rr : reservation.getReservationRooms()) {
                        roomCharges += rr.getRoom().getBasePrice() * nights;
                    }
                }
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Room Charges (" + nights + " nights):");
                contentStream.endText();
                
                contentStream.beginText();
                    float textWidth = PDType1Font.HELVETICA.getStringWidth("Room Charges (" + nights + " nights):") / 1000 * 10;
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - margin - 100, currentY);
                contentStream.showText(currencyFormat.format(roomCharges));
                contentStream.endText();
                currentY -= lineHeight;
                
                // add-ons
                if (reservation.getReservationAddons() != null && !reservation.getReservationAddons().isEmpty()) {
                    for (com.hotel.model.ReservationAddon ra : reservation.getReservationAddons()) {
                        ServiceAddon addon = ra.getAddon();
                        if (addon != null) {
                            double addonPrice = addon.getPrice();
                            int quantity = ra.getQuantity();
                            double total = addonPrice * quantity;
                            
                            String addonText = addon.getName() + " (x" + quantity + ")";
                            contentStream.beginText();
                            contentStream.newLineAtOffset(margin, currentY);
                            contentStream.showText(addonText);
                            contentStream.endText();
                            
                            contentStream.beginText();
                            contentStream.newLineAtOffset(page.getMediaBox().getWidth() - margin - 100, currentY);
                            contentStream.showText(currencyFormat.format(total));
                            contentStream.endText();
                            currentY -= lineHeight;
                        }
                    }
                }
                
                currentY -= lineHeight * 0.5f;
                
                // draw line
                contentStream.moveTo(margin, currentY);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, currentY);
                contentStream.stroke();
                currentY -= lineHeight;
                
                // subtotal
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Subtotal:");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - margin - 100, currentY);
                contentStream.showText(currencyFormat.format(billing.getSubtotal()));
                contentStream.endText();
                currentY -= lineHeight;
                
                // tax
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Tax (" + String.format("%.1f", billing.getTaxRate() * 100) + "%):");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - margin - 100, currentY);
                contentStream.showText(currencyFormat.format(billing.getTaxAmount()));
                contentStream.endText();
                currentY -= lineHeight;
                
                // discount
                if (billing.getDiscountValue() > 0) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, currentY);
                    contentStream.showText("Discount:");
                    contentStream.endText();
                    
                    contentStream.beginText();
                    contentStream.newLineAtOffset(page.getMediaBox().getWidth() - margin - 100, currentY);
                    contentStream.showText("-" + currencyFormat.format(billing.getDiscountValue()));
                    contentStream.endText();
                    currentY -= lineHeight;
                }
                
                // loyalty points
                if (billing.getLoyaltyRedeemedPoints() > 0) {
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, currentY);
                    contentStream.showText("Loyalty Points Redeemed: " + billing.getLoyaltyRedeemedPoints());
                    contentStream.endText();
                    currentY -= lineHeight;
                }
                
                currentY -= lineHeight * 0.5f;
                
                // draw line
                contentStream.moveTo(margin, currentY);
                contentStream.lineTo(page.getMediaBox().getWidth() - margin, currentY);
                contentStream.stroke();
                currentY -= lineHeight;
                
                // total
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Total Amount:");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - margin - 100, currentY);
                contentStream.showText(currencyFormat.format(billing.getTotalAmount()));
                contentStream.endText();
                currentY -= lineHeight * 2;
                
                // payment information
                if (billing.getPayments() != null && !billing.getPayments().isEmpty()) {
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(margin, currentY);
                    contentStream.showText("Payment History");
                    contentStream.endText();
                    currentY -= lineHeight * 1.5f;
                    
                    contentStream.setFont(PDType1Font.HELVETICA, 10);
                    for (Payment payment : billing.getPayments()) {
                        String paymentText = payment.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")) +
                            " - " + payment.getMethod() + " - " + currencyFormat.format(payment.getAmount());
                        contentStream.beginText();
                        contentStream.newLineAtOffset(margin, currentY);
                        contentStream.showText(paymentText);
                        contentStream.endText();
                        currentY -= lineHeight;
                    }
                    
                    currentY -= lineHeight * 0.5f;
                }
                
                // payment summary
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Payment Summary");
                contentStream.endText();
                currentY -= lineHeight * 1.5f;
                
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Paid Amount:");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - margin - 100, currentY);
                contentStream.showText(currencyFormat.format(billing.getPaidAmount()));
                contentStream.endText();
                currentY -= lineHeight;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Balance:");
                contentStream.endText();
                
                contentStream.beginText();
                contentStream.newLineAtOffset(page.getMediaBox().getWidth() - margin - 100, currentY);
                contentStream.showText(currencyFormat.format(billing.getBalanceAmount()));
                contentStream.endText();
                currentY -= lineHeight;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(margin, currentY);
                contentStream.showText("Payment Status: " + billing.getPaymentStatus());
                contentStream.endText();
            }
            
            document.save(filePath);
        }
    }
}

