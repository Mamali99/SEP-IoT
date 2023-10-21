package de.ostfalia.application.data.lamp.model;



import java.awt.*;
import java.io.IOException;

public interface ILamp {

    /**
     * @brief Switches on the Lamp, keeping current color and intensity values
     * @throws IOException
     */
    void switchOn() throws IOException ;

    /**
     * @brief Switches on the Lamp, keeping the current color but using the provided intensity
     * @param intensity 0..1
     * @note Setting the intensity to 0 may result in undefined behavior e.g. turning the lamp off or keeping it on at no light emission
     * @throws IOException
     */
    void switchOn(float intensity) throws IOException ;

    /**
     * @brief Switches on the Lamp, setting both intensity and color from the provided Color object
     * @param color Color Object, the alpha channel is used to indicate the inverted intensity.
     *              Providing no alpha value resulting in a default alpha value of 255, which indicate that the intensity of the lamp shall not be changed
     * @throws IOException
     */
    void switchOn(Color color) throws IOException ;

    /**
     * @brief Switches off the lamp. Intensity and color of the lamp should be left unchanged
     * @throws IOException
     */
    void switchOff() throws IOException;

    /**
     * @brief Sets the color and the intensity of the Lamp
     * @param color Color Object, the alpha channel is used to indicate the inverted intensity.
     *              Providing no alpha value resulting in a default alpha value of 255, which indicate that the intensity of the lamp shall not be changed
     * @throws IOException
     */
    void setColor(Color color) throws IOException;

    /**
     * @brief Sets the Intensity of the Lamp
     * @param intensity at a scale 0 .. 1
     * @note Setting the intensity to 0 may result in undefined behavior e.g. turning the lamp off or keeping it on at no light emission
     * @throws IOException
     */
    void setIntensity(float intensity) throws IOException;

    /**
     * @brief gets the current Color of the Lamp incl. the inverted intensity in the alpha channel
     * @return Color incl. inverted intensity
     * @throws IOException
     */
    Color getColor() throws IOException;

    /**
     * @brief Gets the non-inverted intensity of the Lamp
     * @return intensity at a scale 0 .. 1
     * @throws IOException
     */
    float getIntensity() throws IOException;

    /**
     * @brief Gets the current operation state of the Lamp
     * @note A Lamp emitting light at an intensity of 0 may be treated on or off. Concrete mapping is implementation defined
     * @return boolsch state
     * @throws IOException
     */
    boolean getState() throws IOException;

    String getName() throws IOException;



}
