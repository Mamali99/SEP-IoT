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
    //private static final String urlState = "http://172.28.24.10/hue/lights/1";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // lock object for synchronization
    //private final Object lock = new Object();


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
