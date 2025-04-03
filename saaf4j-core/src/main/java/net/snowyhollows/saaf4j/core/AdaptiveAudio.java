package net.snowyhollows.saaf4j.core;

import java.io.InputStream;

public interface AdaptiveAudio {

    SoundBank loadBank(InputStream bankStream);

    Event getEvent(String uri);

    FloatParam getGlobalFloatParam(String uri);

    boolean isReady();

    void update(float deltaTime);

    void dispose();
}
