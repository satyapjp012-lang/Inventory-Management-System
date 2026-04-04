package inventory.management.system;

import java.util.*;

/**
 * Simplified database connection class using in-memory storage.
 * Demonstrates authentication without needing a real database.
 */
public class DBConnection {
    private static Map<String, String> Users = new HashMap<>();

    static {
        // Initialize with demo users
        Users.put("admin", "admin123:admin:Admin User:admin:null");
        Users.put("emp1", "emp123:employee:John Doe:employee:Hardware");
    }

    public static User authenticate(String username, String password) {
        if (Users.containsKey(username)) {
            String[] parts = Users.get(username).split(":");
            String storedPassword = parts[0];
            
            if (storedPassword.equals(password)) {
                return new User(
                    Users.keySet().size(),
                    username,
                    parts[2],  // fullName
                    parts[3],  // role
                    "null".equals(parts[4]) ? null : parts[4]  // category
                );
            }
        }
        return null;
    }

    public static void closeConnection() {
        // No-op for in-memory connection
    }
}
