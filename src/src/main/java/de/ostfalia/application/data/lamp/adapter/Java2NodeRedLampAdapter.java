package de.ostfalia.application.data.lamp.adapter;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.stream.JsonReader;
import de.ostfalia.application.data.lamp.model.ILamp;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@Primary
public class Java2NodeRedLampAdapter implements ILamp {

    private static final String url = "http://172.28.24.10/hue/lights";
    private static final String urlState = "http://172.28.24.10/hue/lights/1";

    private String baseUrl = "http://172.28.24.10/hue/lights/1";
    // lock object for synchronization
    private final Object lock = new Object();


    @Override
    public void switchOn() throws IOException {

        //Mann kann mit JsonObject jsonObject = Json.createObjectBuilder() request an Hue Bridge schicken
    }

    @Override
    public void switchOn(float intensity) throws IOException {

    }

    @Override
    public void switchOn(Color color) throws IOException {

    }

    @Override
    public void switchOff() throws IOException {

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

        try {
            URL url = new URL(urlState);
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
    }

}
