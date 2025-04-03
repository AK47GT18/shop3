package com.example.shop3.helpers;

            import android.util.Patterns;

            public class ValidationHelper {
                private static final int MIN_PASSWORD_LENGTH = 8;

                public static boolean isValidEmail(String email) {
                    return email != null && !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
                }

                public static boolean isValidPassword(String password) {
                    return password != null && password.length() >= MIN_PASSWORD_LENGTH;
                }

                public static String getPasswordError(String password) {
                    if (password == null || password.isEmpty()) {
                        return "Password cannot be empty";
                    }
                    if (password.length() < MIN_PASSWORD_LENGTH) {
                        return "Password must be at least 8 characters long";
                    }
                    return null;
                }
            }