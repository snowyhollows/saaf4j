package net.snowyhollows.saaf4j.core;

import java.io.InputStream;

public interface AdaptiveAudio {

    AudioBank loadBank(String bankPath);

    AudioBank loadBank(InputStream bankStream);

    AudioCue getCue(String path);

    AudioFloatParameter getGlobalParam(String path);

    boolean isReady();

    void update(float deltaTime);
}
