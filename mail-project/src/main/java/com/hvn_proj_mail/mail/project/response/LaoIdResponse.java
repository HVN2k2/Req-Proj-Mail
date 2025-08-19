package com.hvn_proj_mail.mail.project.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LaoIdResponse {
    private boolean success;
    private String message;
    private Data data;
    private String statusCode;

    public static class Data {
        // Token fields
        private String accessToken;
        private long expiresIn;
        private String idToken;
        private String tokenType;

        // User info fields
        private String id;
        private String username;
        private String firstName;
        private String lastName;
        private String avatar;
        private String dateOfBirth;
        private String gender;
        private String[] emails;
        @JsonProperty("phoneNumber")
        @JsonDeserialize(using = PhoneNumberDeserializer.class)
        private String[] phoneNumbers;

        public String getAccessToken() { return accessToken; }
        public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
        public long getExpiresIn() { return expiresIn; }
        public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
        public String getIdToken() { return idToken; }
        public void setIdToken(String idToken) { this.idToken = idToken; }
        public String getTokenType() { return tokenType; }
        public void setTokenType(String tokenType) { this.tokenType = tokenType; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public String getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public String[] getEmails() { return emails; }
        public void setEmails(String[] emails) { this.emails = emails; }
        public String[] getPhoneNumbers() { return phoneNumbers; }
        public void setPhoneNumbers(String[] phoneNumbers) { this.phoneNumbers = phoneNumbers; }
    }

    public static class PhoneNumberDeserializer extends JsonDeserializer<String[]> {
        @Override
        public String[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            List<String> phoneNumbers = new ArrayList<>();

            if (node.isArray()) {
                // Nếu là array
                for (JsonNode element : node) {
                    if (element.isTextual()) {
                        phoneNumbers.add(element.asText());
                    } else if (element.isObject()) {
                        // Nếu element là object, thử lấy value
                        if (element.has("value")) {
                            phoneNumbers.add(element.get("value").asText());
                        } else if (element.has("number")) {
                            phoneNumbers.add(element.get("number").asText());
                        } else {
                            // Lấy toàn bộ object như string
                            phoneNumbers.add(element.toString());
                        }
                    }
                }
            } else if (node.isObject()) {
                // Nếu là object, thử lấy các field có thể
                if (node.has("value")) {
                    phoneNumbers.add(node.get("value").asText());
                } else if (node.has("number")) {
                    phoneNumbers.add(node.get("number").asText());
                } else {
                    // Lấy toàn bộ object như string
                    phoneNumbers.add(node.toString());
                }
            } else if (node.isTextual()) {
                // Nếu là string đơn
                phoneNumbers.add(node.asText());
            }

            return phoneNumbers.toArray(new String[0]);
        }
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }
    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
}