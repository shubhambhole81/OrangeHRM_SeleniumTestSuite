package com.orangehrm.automation.config;

public final class Config {
    private Config() { }

    private static String value(String property, String environment, String fallback) {
        String configured = System.getProperty(property);
        if (configured == null || configured.isBlank()) configured = System.getenv(environment);
        return configured == null || configured.isBlank() ? fallback : configured;
    }

    public static String baseUrl() { return value("baseUrl", "ORANGEHRM_BASE_URL", "https://opensource-demo.orangehrmlive.com"); }
    public static String username() { return value("username", "ORANGEHRM_USERNAME", "Admin"); }
    public static String password() { return value("password", "ORANGEHRM_PASSWORD", "admin123"); }
    public static boolean headless() { return Boolean.parseBoolean(value("headless", "HEADLESS", "true")); }
    public static long timeoutSeconds() {
        try { return Long.parseLong(value("timeout", "SELENIUM_TIMEOUT", "15")); }
        catch (NumberFormatException ignored) { return 15; }
    }
}
