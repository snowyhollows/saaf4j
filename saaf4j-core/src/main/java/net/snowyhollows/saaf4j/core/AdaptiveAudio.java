package net.snowyhollows.saaf4j.core;

import java.io.InputStream;

public interface AdaptiveAudio {

    Bank loadBank(InputStream bankStream);

    Cue getCue(String path);

    Param getGlobalParam(String path);

    boolean isReady();

    void update(float deltaTime);
}
