package com.menkaix.backlogs.utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Base64;
import java.util.Optional;

public class GcpUserInfoExtractor {

    private static final String HEADER = "X-Apigateway-Api-Userinfo";

    public static Optional<String> extractEmail(HttpServletRequest request) {
        String raw = request.getHeader(HEADER);
        if (raw == null || raw.isBlank()) return Optional.empty();

        try {
            // GCP API Gateway pad le base64 si nécessaire
            int padding = (4 - raw.length() % 4) % 4;
            raw = raw + "=".repeat(padding);

            String json = new String(Base64.getUrlDecoder().decode(raw));
            JsonObject claims = JsonParser.parseString(json).getAsJsonObject();

            if (claims.has("email")) {
                return Optional.of(claims.get("email").getAsString());
            }
        } catch (Exception ignored) {}

        return Optional.empty();
    }

    public static Optional<JsonObject> extractClaims(HttpServletRequest request) {
        String raw = request.getHeader(HEADER);
        if (raw == null || raw.isBlank()) return Optional.empty();

        try {
            int padding = (4 - raw.length() % 4) % 4;
            raw = raw + "=".repeat(padding);

            String json = new String(Base64.getUrlDecoder().decode(raw));
            return Optional.of(JsonParser.parseString(json).getAsJsonObject());
        } catch (Exception ignored) {}

        return Optional.empty();
    }
}
