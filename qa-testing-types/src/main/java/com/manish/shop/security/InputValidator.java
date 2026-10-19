package com.manish.shop.security;

import java.util.regex.Pattern;

/** Allow-list input validation and output encoding: the two main defenses against injection and XSS. */
public final class InputValidator {

    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_]{3,20}$");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern US_ZIP = Pattern.compile("^\\d{5}(-\\d{4})?$");

    private InputValidator() { }

    /** 3-20 characters: letters, digits, underscore. */
    public static boolean isValidUsername(String input) {
        return input != null && USERNAME.matcher(input).matches();
    }

    public static boolean isValidEmail(String input) {
        return input != null && input.length() <= 254 && EMAIL.matcher(input).matches();
    }

    /** 12345 or 12345-6789 */
    public static boolean isValidZip(String input) {
        return input != null && US_ZIP.matcher(input).matches();
    }

    /** Encodes text so a browser shows it as text instead of running it as HTML/JavaScript. */
    public static String escapeHtml(String input) {
        if (input == null) return "";
        StringBuilder out = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            switch (c) {
                case '&' -> out.append("&amp;");
                case '<' -> out.append("&lt;");
                case '>' -> out.append("&gt;");
                case '"' -> out.append("&quot;");
                case '\'' -> out.append("&#x27;");
                default -> out.append(c);
            }
        }
        return out.toString();
    }
}
