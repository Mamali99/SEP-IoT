package de.ostfalia.application.data.lamp.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.ostfalia.application.data.lamp.model.ILamp;
import de.ostfalia.application.data.lamp.model.LampObserver;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Primary
public class Java2NodeRedLampAdapter implements ILamp {

    private static final String url = "http://172.28.24.10/hue/lights/1/state";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String baseUrl = "http://172.28.24.10/hue/lights/1";
    // lock object for synchronization
    private final Object lock = new Object();

    // Observer Pattern
    private List<LampObserver> observers = new ArrayList<LampObserver>();

    public void addObserver(LampObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(LampObserver observer) {
        observers.remove(observer);
    }

    // Benachrichtige alle Observers über eine Änderung
    public void notifyObservers() {
        for (LampObserver observer : observers) {
            try {
                observer.updateLampState();
            } catch (IOException e) {
                System.out.println("Observer Issue im Lamp Adapter");
            }
        }
    }


    @Override
    public void switchOn() throws IOException {

        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("on", true);
        restTemplate.put(url, jsonObject.toString());
        notifyObservers();

    }

    @Override
    public void switchOn(float intensity) throws IOException {
        // ein Wert zwischen 0 - 254
        if (intensity < 0 || intensity > 254) {
            throw new IllegalArgumentException("Intensität muss zwischen 0 und 254 liegen.");
        }
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("on", true);
        jsonObject.put("bri", (int) (intensity));
        restTemplate.put(url, jsonObject.toString());
        notifyObservers();

    }

    @Override
    public void switchOn(Color color) throws IOException {

        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("on", true);
        jsonObject.put("hue", (int) (hsb[0] * 65535));
        jsonObject.put("sat", (int) (hsb[1] * 254));
        jsonObject.put("bri", (int) (hsb[2] * 254));
        restTemplate.put(url, jsonObject.toString());
        notifyObservers();
    }

    @Override
    public void switchOff() throws IOException {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("on", false);
        restTemplate.put(url, jsonObject.toString());
        notifyObservers();
    }

    @Override
    public void setColor(Color color) throws IOException {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("on", true);
        jsonObject.put("hue", (int) (hsb[0] * 65535));
        jsonObject.put("sat", (int) (hsb[1] * 254));
        jsonObject.put("bri", (int) (hsb[2] * 254));
        restTemplate.put(url, jsonObject.toString());
        notifyObservers();
    }

    @Override
    public void setIntensity(float intensity) throws IOException {
        if (intensity < 0 || intensity > 254) {
            throw new IllegalArgumentException("Intensity must be between 0 and 254.");
        }

        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("on", true);
        jsonObject.put("bri", (int) intensity);

        restTemplate.put(url, jsonObject.toString());
        notifyObservers();
    }

    @Override
    public Color getColor() throws IOException {
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(baseUrl, JsonNode.class);
        JsonNode stateNode = response.getBody().get("state");
        if (stateNode != null) {
            int hue = stateNode.get("hue").intValue();
            int sat = stateNode.get("sat").intValue();
            int bri = stateNode.get("bri").intValue();

            // Convert HSB values to RGB
            Color color = Color.getHSBColor(hue / 65535.0f, sat / 254.0f, bri / 254.0f);
            return color;
        }
        return null;
    }


    @Override
    public float getIntensity() throws IOException {

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(baseUrl, JsonNode.class);
        JsonNode jsonNode = response.getBody().get("state");
        if (jsonNode != null) {
            return jsonNode.get("bri").floatValue();
        }
        return 0;

    }

    @Override
    public boolean getState() throws IOException {

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(baseUrl, JsonNode.class);
        JsonNode stateNode = response.getBody().get("state");

        if (stateNode != null && stateNode.has("on")) {
            return stateNode.get("on").asBoolean();
        }
        return false;
    }

    public String getName() throws IOException {

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(baseUrl, JsonNode.class);
        JsonNode nameNode = response.getBody().get("name");

        if (nameNode != null) {
            String name = nameNode.asText();
            return name;
        }
        return null;
    }

    @Override
    public void setName(String name) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("name", name);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonObject.toString(), headers);
        restTemplate.put(baseUrl, requestEntity);
        notifyObservers();

    }
}
