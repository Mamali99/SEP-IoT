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
        // ein Wert zwischen 0 - 254
        if (intensity < 0 || intensity > 254) {
            throw new IllegalArgumentException("Intensität muss zwischen 0 und 254 liegen.");
        }
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("on", true);
        jsonObject.put("bri", (int) (intensity));
        restTemplate.put(url, jsonObject.toString());

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
        return null;
    }

    @Override
    public float getIntensity() throws IOException {
        return 0;
    }

    @Override
    public boolean getState() throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(baseUrl, JsonNode.class);
        JsonNode stateNode = response.getBody().get("state");

        if (stateNode != null && stateNode.has("on")) {
            boolean isOn = stateNode.get("on").asBoolean();
            if (isOn) {
                System.out.println("It is on.");
            } else {
                System.out.println("It is off.");
            }
            return isOn;
        }
        return false;
        /*

        try {
            URL url = new URL(baseUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //Parse d' j soon
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject state = jsonResponse.getJSONObject("state");
            boolean isOn = state.getBoolean("on");

            //state check
            if (isOn) {
                System.out.println("It is on.");
            } else {
                System.out.println("It is off.");
            }
        } catch (JSONException j) {
            j.printStackTrace();
        }
        return false;

         */
    }

    public String getName() throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(baseUrl, JsonNode.class);
        JsonNode nameNode = response.getBody().get("name");

        if (nameNode != null) {
            String name = nameNode.asText();
            return name;
        }
        return null;
        /*
        try {
            URL url = new URL(baseUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //Parse d' j soon
            JSONObject jsonResponse = new JSONObject(response.toString());
            String name = jsonResponse.getString("name");
            return name;

        } catch (JSONException j) {
            j.printStackTrace();
        }
        return null;
    }

         */
    }

    @Override
    public void setName(String name) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put("name", name);
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonObject.toString(), headers);
        restTemplate.put(baseUrl, requestEntity);
    }
}
