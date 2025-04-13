package io.cdap.wrangler.api.parser;

import com.google.gson.JsonObject;

public class TimeDuration extends Token {
    private final long milliseconds;

    public TimeDuration(String value) {
        super(Type.TIME_DURATION, value);

        String input = value.trim().toLowerCase();
        long factor;

        if (input.endsWith("ms")) {
            factor = 1L;
            input = input.substring(0, input.length() - 2);
        } else if (input.endsWith("s")) {
            factor = 1000L;
            input = input.substring(0, input.length() - 1);
        } else if (input.endsWith("m")) {
            factor = 60_000L;
            input = input.substring(0, input.length() - 1);
        } else if (input.endsWith("h")) {
            factor = 60L * 60_000L;
            input = input.substring(0, input.length() - 1);
        } else if (input.endsWith("d")) {
            factor = 24L * 60L * 60_000L;
            input = input.substring(0, input.length() - 1);
        } else {
            throw new IllegalArgumentException("Invalid TimeDuration format: " + value);
        }

        try {
            double number = Double.parseDouble(input);
            this.milliseconds = (long) (number * factor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number in TimeDuration: " + value);
        }
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    @Override
    public Object value() {
        return getMilliseconds();
    }

    @Override
    public TokenType type() {
        return Type.TIME_DURATION;
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.addProperty("type", type().name());
        object.addProperty("value", getMilliseconds());
        return object;
    }

}
