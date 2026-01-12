package com.hotel.util;

import java.util.regex.Pattern;

// utility methods for validating user input
// checks emails, phone numbers, names, numbers, and ratings
public class Validator {
    
    // pattern for valid email addresses
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // pattern for valid phone numbers
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,9}$"
    );
    
    // checks if email is valid
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    // checks if phone number is valid
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    // checks if name is valid (2-100 characters)
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return name.trim().length() >= 2 && name.trim().length() <= 100;
    }
    
    // checks if number is positive
    public static boolean isPositive(double number) {
        return number > 0;
    }
    
    // checks if number is non-negative
    public static boolean isNonNegative(double number) {
        return number >= 0;
    }
    
    // checks if rating is valid (1-5 stars)
    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }
}



