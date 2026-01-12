package com.hotel.security;

import org.mindrot.jbcrypt.BCrypt;

// handles password hashing and verification using bcrypt
// never store plain text passwords - always hash them first
public class BCryptPasswordHasher {
    
    // hashes a plain text password using bcrypt
    // returns a hash that can be safely stored in the database
    public static String hash(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    
    // checks if a plain text password matches a stored hash
    // used during login to verify the password is correct
    public static boolean verify(String password, String hash) {
        if (password == null || hash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(password, hash);
        } catch (Exception e) {
            return false;
        }
    }
}



