package de.ostfalia.application.data.lamp.model;

import java.io.IOException;

public interface LampObserver {
    void updateLampState() throws IOException;
}
