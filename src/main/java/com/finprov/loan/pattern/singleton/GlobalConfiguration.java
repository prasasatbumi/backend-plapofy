package com.finprov.loan.pattern.singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Pattern Example: GlobalConfiguration
 * 
 * Ensures that the configuration class has only one instance and provides
 * a global point of access to it.
 */
public class GlobalConfiguration {
    
    // The single instance of the class
    private static GlobalConfiguration instance;
    
    // Example state: application settings
    private Map<String, String> settings;

    // Private constructor to prevent instantiation from outside
    private GlobalConfiguration() {
        settings = new HashMap<>();
        // Load default settings
        settings.put("appName", "LoanManagementSystem");
        settings.put("maxUploadSize", "10MB");
        settings.put("currency", "IDR");
    }

    // Public method to provide access to the instance
    public static synchronized GlobalConfiguration getInstance() {
        if (instance == null) {
            instance = new GlobalConfiguration();
        }
        return instance;
    }

    public String getSetting(String key) {
        return settings.get(key);
    }

    public void setSetting(String key, String value) {
        settings.put(key, value);
    }
}
