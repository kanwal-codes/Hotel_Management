package com.hotel.service;

import com.hotel.model.*;
import com.hotel.repository.*;
import com.hotel.util.LoggerService;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for generating reports
 * Revenue, Occupancy, Activity Logs, Feedback summaries
 */
public class ReportingService {
    
    private BillingRepository billingRepository;
    private ReservationRepository reservationRepository;
    private RoomRepository roomRepository;
    private FeedbackRepository feedbackRepository;
    private AuditLogRepository auditLogRepository;
    private LoggerService logger;
    private EntityManager em;
    
    public ReportingService(EntityManager em) {
        this.em = em;
        this.billingRepository = new BillingRepository(em);
        this.reservationRepository = new ReservationRepository(em);
        this.roomRepository = new RoomRepository(em);
        this.feedbackRepository = new FeedbackRepository(em);
        this.auditLogRepository = new AuditLogRepository(em);
        this.logger = LoggerService.getInstance();
    }
    
    /**
     * Generate revenue report for a date range
     * @param startDate Start date for the report
     * @param endDate End date for the report
     * @param period Period description (e.g., "Day", "Week", "Month")
     * @param roomType Optional room type filter. If null, includes all room types.
     */
    public RevenueReport generateRevenueReport(LocalDate startDate, LocalDate endDate, String period, RoomType roomType) {
        List<Reservation> reservations = reservationRepository.findByDateRange(startDate, endDate);
        
        int reservationCount = 0;
        double subtotal = 0.0;
        double tax = 0.0;
        double discounts = 0.0;
        double total = 0.0;
        
        for (Reservation reservation : reservations) {
            if (reservation.getStatus() != ReservationStatus.CANCELLED) {
                // Filter by room type if specified
                if (roomType != null) {
                    boolean hasMatchingRoomType = false;
                    if (reservation.getReservationRooms() != null) {
                        for (ReservationRoom rr : reservation.getReservationRooms()) {
                            if (rr.getRoom() != null && rr.getRoom().getType() == roomType) {
                                hasMatchingRoomType = true;
                                break;
                            }
                        }
                    }
                    if (!hasMatchingRoomType) {
                        continue; // Skip reservations without the specified room type
                    }
                }
                
                Billing billing = reservation.getBilling();
                if (billing != null) {
                    reservationCount++;
                    subtotal += billing.getSubtotal();
                    tax += billing.getTaxAmount();
                    discounts += billing.getDiscountValue();
                    total += billing.getTotalAmount();
                }
            }
        }
        
        return new RevenueReport(period, reservationCount, subtotal, tax, discounts, total);
    }
    
    /**
     * Generate occupancy report by room type for a date range
     * @param startDate Start date for the report
     * @param endDate End date for the report
     * @param roomType Optional room type filter. If null, generates report for all room types.
     */
    public List<RoomTypeOccupancyReport> generateOccupancyReportByRoomType(LocalDate startDate, LocalDate endDate, RoomType roomType) {
        // Ensure transaction is active for JPA queries
        boolean transactionActive = em.getTransaction().isActive();
        if (!transactionActive) {
            em.getTransaction().begin();
        }
        
        try {
            em.clear();
            
            List<RoomTypeOccupancyReport> reports = new ArrayList<>();
            
            // Get room types to process - if roomType is specified, only process that type
            RoomType[] roomTypesToProcess = roomType != null ? new RoomType[]{roomType} : RoomType.values();
            
            // Process each room type
            for (RoomType currentRoomType : roomTypesToProcess) {
                // Get all rooms of this type
                List<Room> roomsOfType = roomRepository.findByType(currentRoomType);
                int totalRooms = roomsOfType.size();
                
                if (totalRooms == 0) {
                    continue; // Skip room types with no rooms
                }
                
                // Count occupied rooms of this type by finding distinct rooms that have overlapping reservations
                // A reservation overlaps if: checkIn <= endDate AND checkOut >= startDate
                javax.persistence.TypedQuery<Long> occupiedRoomsQuery = em.createQuery(
                    "SELECT COUNT(DISTINCT rr.room.id) FROM ReservationRoom rr " +
                    "JOIN rr.reservation r " +
                    "JOIN rr.room room " +
                    "WHERE r.checkIn <= :endDate " +
                    "AND r.checkOut >= :startDate " +
                    "AND r.status IN (:confirmed, :checkedIn) " +
                    "AND room.type = :roomType",
                    Long.class);
                occupiedRoomsQuery.setParameter("startDate", startDate);
                occupiedRoomsQuery.setParameter("endDate", endDate);
                occupiedRoomsQuery.setParameter("confirmed", ReservationStatus.CONFIRMED);
                occupiedRoomsQuery.setParameter("checkedIn", ReservationStatus.CHECKED_IN);
                occupiedRoomsQuery.setParameter("roomType", currentRoomType);
                
                Long occupiedCount = occupiedRoomsQuery.getSingleResult();
                int occupiedRooms = occupiedCount != null ? occupiedCount.intValue() : 0;
                
                int availableRooms = totalRooms - occupiedRooms;
                double occupancyPercent = totalRooms > 0 ? (occupiedRooms * 100.0 / totalRooms) : 0.0;
                
                reports.add(new RoomTypeOccupancyReport(currentRoomType, totalRooms, occupiedRooms, availableRooms, occupancyPercent));
            }
            
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
            
            return reports;
        } catch (Exception e) {
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.logError("Failed to generate occupancy report by room type", e);
            throw e;
        }
    }
    
    /**
     * Generate occupancy report for a date range (legacy method for backward compatibility)
     */
    public OccupancyReport generateOccupancyReport(LocalDate date) {
        // Ensure transaction is active for JPA queries
        boolean transactionActive = em.getTransaction().isActive();
        if (!transactionActive) {
            em.getTransaction().begin();
        }
        
        try {
            List<Room> allRooms = roomRepository.findAll();
            int totalRooms = allRooms.size();
            
            // First, let's check if there are ANY reservations at all
            List<Reservation> allReservationsInDB = reservationRepository.findAll();
            logger.logInfo("DEBUG: Total reservations in database: " + allReservationsInDB.size());
            for (Reservation r : allReservationsInDB) {
                logger.logInfo("DEBUG: Reservation #" + r.getId() + " - Status: " + r.getStatus() + 
                    ", Check-in: " + r.getCheckIn() + ", Check-out: " + r.getCheckOut() + 
                    ", Guest: " + (r.getGuest() != null ? r.getGuest().getName() : "null"));
            }
            
            // Clear EntityManager cache to ensure we get the latest data
            em.clear();
            
            // Find ALL reservations that overlap with this date (regardless of status)
            // A reservation overlaps if: checkIn <= date AND checkOut >= date (check-out date is inclusive for the day)
            // This matches the revenue report logic: (r.checkIn <= :endDate AND r.checkOut >= :startDate)
            javax.persistence.TypedQuery<Reservation> debugQuery = em.createQuery(
                "SELECT DISTINCT r FROM Reservation r " +
                "LEFT JOIN FETCH r.reservationRooms rr " +
                "WHERE r.checkIn <= :reportDate " +
                "AND r.checkOut >= :reportDate",
                Reservation.class);
            debugQuery.setParameter("reportDate", date);
            List<Reservation> overlappingReservations = debugQuery.getResultList();
            logger.logInfo("DEBUG: Found " + overlappingReservations.size() + " total reservations overlapping date " + date);
            for (Reservation r : overlappingReservations) {
                logger.logInfo("DEBUG: Reservation #" + r.getId() + " - Status: " + r.getStatus() + 
                    ", Check-in: " + r.getCheckIn() + ", Check-out: " + r.getCheckOut() + 
                    ", Rooms: " + (r.getReservationRooms() != null ? r.getReservationRooms().size() : 0));
            }
            
            // Count occupied rooms (rooms with active reservations on this date)
            // Use a query that eagerly fetches reservationRooms to avoid lazy loading issues
            // A reservation is active on a date if: checkIn <= date AND checkOut >= date (check-out date is inclusive)
            // This matches the revenue report logic for consistency
            // Only count CONFIRMED and CHECKED_IN reservations (exclude PENDING, CANCELLED, CHECKED_OUT)
            javax.persistence.TypedQuery<Reservation> query = em.createQuery(
                "SELECT DISTINCT r FROM Reservation r " +
                "LEFT JOIN FETCH r.reservationRooms rr " +
                "WHERE r.checkIn <= :reportDate " +
                "AND r.checkOut >= :reportDate " +
                "AND r.status IN (:confirmed, :checkedIn)",
                Reservation.class);
            query.setParameter("reportDate", date);
            query.setParameter("confirmed", ReservationStatus.CONFIRMED);
            query.setParameter("checkedIn", ReservationStatus.CHECKED_IN);
            
            List<Reservation> reservations = query.getResultList();
            logger.logInfo("Found " + reservations.size() + " active (CONFIRMED or CHECKED_IN) reservations for date " + date);
            
            int occupiedRooms = 0;
            
            for (Reservation reservation : reservations) {
                // Count rooms for confirmed or checked-in reservations
                // reservationRooms should now be loaded due to JOIN FETCH
                if (reservation.getReservationRooms() != null && !reservation.getReservationRooms().isEmpty()) {
                    int roomCount = reservation.getReservationRooms().size();
                    occupiedRooms += roomCount;
                    logger.logInfo("Reservation #" + reservation.getId() + " (Status: " + reservation.getStatus() + 
                        ", Check-in: " + reservation.getCheckIn() + ", Check-out: " + reservation.getCheckOut() + 
                        ") has " + roomCount + " room(s)");
                } else {
                    logger.logWarning("Reservation #" + reservation.getId() + " has no rooms assigned!");
                }
            }
            
            int availableRooms = totalRooms - occupiedRooms;
            double occupancyPercent = totalRooms > 0 ? (occupiedRooms * 100.0 / totalRooms) : 0.0;
            
            logger.logInfo("Occupancy Report for " + date + ": " + occupiedRooms + " occupied rooms out of " + totalRooms + " (" + String.format("%.2f", occupancyPercent) + "%)");
            
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
            
            return new OccupancyReport(date, availableRooms, occupiedRooms, occupancyPercent);
        } catch (Exception e) {
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.logError("Failed to generate occupancy report", e);
            throw e;
        }
    }
    
    /**
     * Get activity logs
     * Can read from either audit table (database) OR log file
     */
    public List<AuditLog> getActivityLogs(LocalDateTime start, LocalDateTime end) {
        // Try to read from audit table first (default)
        return getActivityLogsFromDatabase(start, end);
    }
    
    /**
     * Get activity logs from database (audit table)
     */
    public List<AuditLog> getActivityLogsFromDatabase(LocalDateTime start, LocalDateTime end) {
        // Ensure transaction is active for JPA queries
        boolean transactionActive = em.getTransaction().isActive();
        if (!transactionActive) {
            em.getTransaction().begin();
        }
        
        try {
            List<AuditLog> logs;
            if (start != null && end != null) {
                logs = auditLogRepository.findByDateRange(start, end);
            } else {
                logs = auditLogRepository.findAll();
            }
            
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
            
            return logs;
        } catch (Exception e) {
            if (!transactionActive && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.logError("Failed to get activity logs from database", e);
            throw e;
        }
    }
    
    /**
     * Get activity logs from log file (alternative source)
     * Reads from system_logs.%g.log files
     */
    public List<AuditLog> getActivityLogsFromFile(LocalDateTime start, LocalDateTime end) {
        List<AuditLog> logs = new ArrayList<>();
        
        try {
            // Read from log files (system_logs.0.log, system_logs.1.log, etc.)
            for (int i = 0; i < 10; i++) {
                String fileName = i == 0 ? "system_logs.log" : "system_logs." + i + ".log";
                java.io.File logFile = new java.io.File(fileName);
                
                if (!logFile.exists()) {
                    continue;
                }
                
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.FileReader(logFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        AuditLog log = parseLogLine(line);
                        if (log != null) {
                            // Filter by date range if provided
                            if (start != null && end != null) {
                                if (!log.getTimestamp().isBefore(start) && 
                                    !log.getTimestamp().isAfter(end)) {
                                    logs.add(log);
                                }
                            } else {
                                logs.add(log);
                            }
                        }
                    }
                } catch (java.io.IOException e) {
                    logger.logWarning("Failed to read from log file: " + fileName);
                }
            }
            
            // Sort by timestamp (newest first)
            logs.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
            
        } catch (Exception e) {
            logger.logError("Failed to get activity logs from file", e);
        }
        
        return logs;
    }
    
    /**
     * Parse a log line into an AuditLog object
     * Format: [actor] ACTION - EntityType (ID: entityId): message
     */
    private AuditLog parseLogLine(String line) {
        try {
            // Expected format: [actor] ACTION - EntityType (ID: entityId): message
            // Example: [admin] CREATE_RESERVATION - Reservation (ID: 123): Created reservation for guest
            
            if (line == null || line.trim().isEmpty() || !line.contains("]")) {
                return null;
            }
            
            // Extract actor (between [ and ])
            int actorStart = line.indexOf('[');
            int actorEnd = line.indexOf(']');
            if (actorStart == -1 || actorEnd == -1 || actorEnd <= actorStart) {
                return null;
            }
            String actor = line.substring(actorStart + 1, actorEnd).trim();
            
            // Extract action (after ] and before -)
            int actionStart = actorEnd + 1;
            int actionEnd = line.indexOf(" - ", actionStart);
            if (actionEnd == -1) {
                return null;
            }
            String action = line.substring(actionStart, actionEnd).trim();
            
            // Extract entity type (after " - " and before " (ID:")
            int entityStart = actionEnd + 3;
            int entityEnd = line.indexOf(" (ID:", entityStart);
            if (entityEnd == -1) {
                return null;
            }
            String entityType = line.substring(entityStart, entityEnd).trim();
            
            // Extract entity ID (between "(ID: " and ")")
            int idStart = line.indexOf("(ID: ", entityEnd) + 5;
            int idEnd = line.indexOf(")", idStart);
            if (idStart < 5 || idEnd == -1) {
                return null;
            }
            String idStr = line.substring(idStart, idEnd).trim();
            Long entityId = null;
            try {
                entityId = Long.parseLong(idStr);
            } catch (NumberFormatException e) {
                // ID might be 0 or invalid, continue
            }
            
            // Extract message (after ": ")
            int messageStart = line.indexOf(": ", idEnd);
            String message = messageStart != -1 && messageStart + 2 < line.length() 
                ? line.substring(messageStart + 2).trim() 
                : "";
            
            // Create AuditLog (timestamp will be set to now, as we can't parse from log format)
            AuditLog log = new AuditLog(actor, action, entityType, entityId, message);
            // Note: Log file doesn't store timestamp in parseable format, so we use current time
            // In a real implementation, you'd want to parse timestamp from log line
            
            return log;
        } catch (Exception e) {
            logger.logWarning("Failed to parse log line: " + line);
            return null;
        }
    }
    
    /**
     * Generate feedback summary
     */
    public FeedbackSummary generateFeedbackSummary() {
        List<Feedback> allFeedback = feedbackRepository.findAll();
        
        if (allFeedback.isEmpty()) {
            return new FeedbackSummary(0, 0.0, new HashMap<>());
        }
        
        double totalRating = 0.0;
        Map<String, Integer> sentimentCounts = new HashMap<>();
        
        for (Feedback feedback : allFeedback) {
            totalRating += feedback.getRating();
            
            String sentiment = feedback.getSentimentTag();
            if (sentiment != null) {
                sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
            }
        }
        
        double averageRating = totalRating / allFeedback.size();
        
        return new FeedbackSummary(allFeedback.size(), averageRating, sentimentCounts);
    }
    
    /**
     * Get all feedback entries
     */
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }
    
    // Inner classes for report data
    public static class RevenueReport {
        private String period;
        private int reservationCount;
        private double subtotal;
        private double tax;
        private double discounts;
        private double total;
        
        public RevenueReport(String period, int reservationCount, double subtotal, double tax, double discounts, double total) {
            this.period = period;
            this.reservationCount = reservationCount;
            this.subtotal = subtotal;
            this.tax = tax;
            this.discounts = discounts;
            this.total = total;
        }
        
        // Getters
        public String getPeriod() { return period; }
        public int getReservationCount() { return reservationCount; }
        public double getSubtotal() { return subtotal; }
        public double getTax() { return tax; }
        public double getDiscounts() { return discounts; }
        public double getTotal() { return total; }
    }
    
    public static class OccupancyReport {
        private LocalDate date;
        private int availableRooms;
        private int occupiedRooms;
        private double occupancyPercent;
        
        public OccupancyReport(LocalDate date, int availableRooms, int occupiedRooms, double occupancyPercent) {
            this.date = date;
            this.availableRooms = availableRooms;
            this.occupiedRooms = occupiedRooms;
            this.occupancyPercent = occupancyPercent;
        }
        
        // Getters
        public LocalDate getDate() { return date; }
        public int getAvailableRooms() { return availableRooms; }
        public int getOccupiedRooms() { return occupiedRooms; }
        public double getOccupancyPercent() { return occupancyPercent; }
    }
    
    public static class RoomTypeOccupancyReport {
        private RoomType roomType;
        private int totalRooms;
        private int occupiedRooms;
        private int availableRooms;
        private double occupancyPercent;
        
        public RoomTypeOccupancyReport(RoomType roomType, int totalRooms, int occupiedRooms, int availableRooms, double occupancyPercent) {
            this.roomType = roomType;
            this.totalRooms = totalRooms;
            this.occupiedRooms = occupiedRooms;
            this.availableRooms = availableRooms;
            this.occupancyPercent = occupancyPercent;
        }
        
        // Getters
        public RoomType getRoomType() { return roomType; }
        public int getTotalRooms() { return totalRooms; }
        public int getOccupiedRooms() { return occupiedRooms; }
        public int getAvailableRooms() { return availableRooms; }
        public double getOccupancyPercent() { return occupancyPercent; }
    }
    
    public static class FeedbackSummary {
        private int totalCount;
        private double averageRating;
        private Map<String, Integer> sentimentCounts;
        
        public FeedbackSummary(int totalCount, double averageRating, Map<String, Integer> sentimentCounts) {
            this.totalCount = totalCount;
            this.averageRating = averageRating;
            this.sentimentCounts = sentimentCounts;
        }
        
        // Getters
        public int getTotalCount() { return totalCount; }
        public double getAverageRating() { return averageRating; }
        public Map<String, Integer> getSentimentCounts() { return sentimentCounts; }
    }
}

