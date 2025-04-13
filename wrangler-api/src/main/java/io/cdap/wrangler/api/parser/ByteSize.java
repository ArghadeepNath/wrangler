package io.cdap.wrangler.api.parser;

import com.google.gson.JsonObject;

import java.util.Locale;

public class ByteSize implements Token {
    private final String rawValue;
    private final long bytes;

    public ByteSize(String value) {
        this.rawValue = value;

        String input = value.trim().toUpperCase(Locale.ENGLISH);
        if (input.matches("^-?\\d+(\\.\\d+)?[KMGTP]?B?$")) {
            double number;
            String unit;

            int unitIndex = 0;
            for (int i = 0; i < input.length(); i++) {
                if (!Character.isDigit(input.charAt(i)) && input.charAt(i) != '.') {
                    unitIndex = i;
                    break;
                }
            }

            if (unitIndex == 0) {
                // Default to bytes if no unit is provided
                number = Double.parseDouble(input);
                this.bytes = (long) number;
                return;
            }

            number = Double.parseDouble(input.substring(0, unitIndex));
            unit = input.substring(unitIndex).replace("B", "");

            switch (unit) {
                case "":
                    this.bytes = (long) number;
                    break;
                case "K":
                    this.bytes = (long) (number * 1024L);
                    break;
                case "M":
                    this.bytes = (long) (number * 1024L * 1024L);
                    break;
                case "G":
                    this.bytes = (long) (number * 1024L * 1024L * 1024L);
                    break;
                case "T":
                    this.bytes = (long) (number * 1024L * 1024L * 1024L * 1024L);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported ByteSize unit: " + unit);
            }
        } else {
            throw new IllegalArgumentException("Invalid ByteSize format: " + value);
        }
    }

    public long getBytes() {
        return bytes;
    }

    @Override
    public Object value() {
        return rawValue; // Return the original string representation
    }

    @Override
    public TokenType type() {
        return TokenType.BYTE_SIZE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", type().name());
        object.addProperty("value", getBytes());
        return object;
    }
}