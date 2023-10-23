package de.ostfalia.application.data.lamp.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import de.ostfalia.application.data.lamp.model.ILamp;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@Primary
public class Java2NodeRedLampAdapter implements ILamp {

    private static final String url = "http://172.28.24.10/hue/lights/1/state";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String baseUrl = "http://172.28.24.10/hue/lights/1";
    // lock object for synchronization
    private final Object lock = new Object();


    @Override
    public void switchOn() throws IOException {

        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("on", true);
        restTemplate.put(url, jsonObject.toString());

    }

    @Override
    public void switchOn(float intensity) throws IOException {

    }

    @Override
    public void switchOn(Color color) throws IOException {

    }

    @Override
    public void switchOff() throws IOException {
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("on", false);
        restTemplate.put(url , jsonObject.toString());

    }

    @Override
    public void setColor(Color color) throws IOException {

    }

    @Override
    public void setIntensity(float intensity) throws IOException {

    }

    @Override
    public Color getColor() throws IOException {
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(baseUrl, JsonNode.class);
        JsonNode stateNode = response.getBody().get("state");
        if (stateNode != null) {
            int red = stateNode.get("hue").asInt();
            int green = stateNode.get("sat").asInt();
            int blue = stateNode.get("bri").asInt();
            System.out.println("lol");
            return new Color(red, green, blue);

        }
        return null;
    }

    @Override
    public float getIntensity() throws IOException {
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

    }
}
