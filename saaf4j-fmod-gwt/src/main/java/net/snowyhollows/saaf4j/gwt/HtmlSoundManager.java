package net.snowyhollows.saaf4j.gwt;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import net.snowyhollows.jam.mvmxxiii.sound.SoundEvent;
import net.snowyhollows.jam.mvmxxiii.sound.SoundEventManager;
import net.snowyhollows.jam.mvmxxiii.sound.SoundParameter;

public class HtmlSoundManager implements SoundEventManager {

    private boolean ready = false;

    public HtmlSoundManager() {
        new Timer() {
            @Override
            public void run() {
                try {

                    Result<FMOD_Studio_EventDescription> resultDescription = new Result<>();
                    Result<FMOD_Studio_EventInstance> resultInstance = new Result<>();

                    FMOD_System.getEvent("event:/Music/music_ship_interior", resultDescription);
                    resultDescription.getVal().createInstance(resultInstance);
                    resultInstance.getVal().start();

                    ready = true;

                } catch (RuntimeException runtimeException) {
                    Window.alert("problems with FMOD: " + runtimeException.getMessage());
                }
            }
        }.schedule(1000);
    }

    // "event:/Music/music_ship_interior"
    @Override
    public SoundEvent getEvent(String path) {
        Result<FMOD_Studio_EventDescription> resultDescription = new Result<>();
        Result<FMOD_Studio_EventInstance> resultInstance = new Result<>();

        FMOD_System.getEvent(path, resultDescription);
        resultDescription.getVal().createInstance(resultInstance);

        return new SoundEvent() {
            @Override
            public void play() {
                resultInstance.getVal().start();
            }

            @Override
            public void stop() {
                resultInstance.getVal().stop(0);
            }
        };
    }

    @Override
    public SoundParameter getParameter(String path) {
        return new SoundParameter() {
            @Override
            public void set(float value) {
                FMOD_System.setParameterByName(path, value, false);
            }
        };
    }

    @Override
    public boolean isReady() {
        return ready;
    }
}
