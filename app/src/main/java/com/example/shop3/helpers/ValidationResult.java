package com.example.shop3.helpers;

        public class ValidationResult {
            private final boolean isValid;
            private final String error;

            private ValidationResult(boolean isValid, String error) {
                this.isValid = isValid;
                this.error = error;
            }

            public static ValidationResult success() {
                return new ValidationResult(true, null);
            }

            public static ValidationResult error(String message) {
                return new ValidationResult(false, message);
            }

            public boolean isValid() {
                return isValid;
            }

            public String getError() {
                return error;
            }
        }