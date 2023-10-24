package de.ostfalia.application.data.lamp.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.ostfalia.application.data.lamp.model.ILamp;
import elemental.json.JsonObject;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.io.IOException;

@Component
@Primary
public class Java2NodeRedLampAdapter implements ILamp {

    private static final String url = "http://172.28.24.10/hue/lights/1/state";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        return false;
    }
}
