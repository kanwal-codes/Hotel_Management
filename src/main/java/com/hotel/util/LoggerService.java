package com.hotel.util;

import java.io.IOException;
import java.util.logging.*;

// singleton logger for the whole application
// writes logs to files that rotate when they get too big (1MB each, keeps 10 files)
// also prints to console during development
public class LoggerService {
    private static LoggerService instance;
    private Logger logger;
    
    private LoggerService() {
        logger = Logger.getLogger("HotelSystem");
        logger.setLevel(Level.ALL);
        // Prevent duplicate logging by disabling parent handler propagation
        logger.setUseParentHandlers(false);
        
        try {
            // set up file logging with rotation - when file hits 1MB, create new one
            // keeps up to 10 files, then starts overwriting the oldest
            FileHandler fileHandler = new FileHandler("system_logs.%g.log", 1024 * 1024, 10, true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
            
            // also show logs in console so we can see what's happening during development
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(formatter);
            logger.addHandler(consoleHandler);
            
            logger.info("LoggerService initialized successfully");
        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // gets the single instance of the logger (singleton pattern)
    // thread-safe double-checked locking
    public static LoggerService getInstance() {
        if (instance == null) {
            synchronized (LoggerService.class) {
                if (instance == null) {
                    instance = new LoggerService();
                }
            }
        }
        return instance;
    }
    
    // log a normal info message
    public void logInfo(String message) {
        logger.info(message);
    }
    
    // log an error with the exception details
    public void logError(String message, Exception e) {
        logger.log(Level.SEVERE, message, e);
    }
    
    // log an error message without exception details
    public void logError(String message) {
        logger.severe(message);
    }
    
    // log a warning message
    public void logWarning(String message) {
        logger.warning(message);
    }
    
    // log an admin action for audit trail
    // format: [username] ACTION - EntityType (ID: 123): description
    public void logActivity(String actor, String action, String entityType, Long entityId, String message) {
        String logMessage = String.format("[%s] %s - %s (ID: %d): %s", 
            actor, action, entityType, entityId != null ? entityId : 0, message);
        logger.info(logMessage);
    }
    
    // get the underlying java logger if you need advanced features
    public Logger getLogger() {
        return logger;
    }
}



