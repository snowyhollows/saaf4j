package net.snowyhollows.saaf4j.gwt;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import net.snowyhollows.saaf4j.core.AdaptiveAudio;
import net.snowyhollows.saaf4j.core.Bank;
import net.snowyhollows.saaf4j.core.Cue;
import net.snowyhollows.saaf4j.core.Param;

import java.io.InputStream;

public class FmodGwtAdaptiveAudio implements AdaptiveAudio {

    private enum State {
        INITIALIZING, NOT_READY, READY, ERROR;
    }

    private static State state = State.NOT_READY;
    private Exception reason;

    public void init() {
        assertNotError();
        if (state == State.NOT_READY) {
            ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + "scripts/fmodstudio.js")
                    .setCallback(new Callback<>() {
                        @Override
                        public void onFailure(Exception reason) {
                            state = State.ERROR;
                            FmodGwtAdaptiveAudio.this.reason = reason;
                        }

                        @Override
                        public void onSuccess(Void result) {
                            state = State.READY;
                            prepareGlobals();
                        }
                    })
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
        }
    }

    private native void prepareGlobals(int initialMemory) /*-{
        var FMOD = {};                           // FMOD global object which must be declared to enable 'main' and 'preRun' and then call the constructor function.
        FMOD['preRun'] = function() {};                 // Will be called before FMOD runs, but after the Emscripten runtime has initialized
        FMOD['onRuntimeInitialized'] = function () {
            $wnd.FMOD_System = new $wnd.FMOD_System();
        };
        FMOD['INITIAL_MEMORY'] = initialMemory;  // This demo loads some large banks, so it needs more memory than the default (16 MB)
        FMODModule(FMOD);       }-*/

    private void assertNotError() {
        if (state == State.ERROR) {
            throw new IllegalStateException("Error occurred during initialization", reason);
        }
    }

    @Override
    public Bank loadBank(InputStream bankStream) {
        return null;
    }

    @Override
    public Cue getCue(String path) {
        Result<FMOD_Studio_EventDescription> resultDescription = new Result<>();
        Result<FMOD_Studio_EventInstance> resultInstance = new Result<>();

        FMOD_System.getEvent(path, resultDescription);
        resultDescription.getVal().createInstance(resultInstance);

        return new Cue() {
            @Override
            public void in() {
                resultInstance.getVal().start();
            }

            @Override
            public void out() {
                resultInstance.getVal().stop(0);
            }
        };
    }

    @Override
    public Param getGlobalParam(String path) {
        return new Param() {
            @Override
            public void set(float value) {
                FMOD_System.setParameterByName(path, value, false);
            }
        };
    }

    @Override
    public boolean isReady() {
        assertNotError();
        return state == State.READY;
    }

    @Override
    public void update(float deltaTime) {
        assertNotError();
        FMOD_System.update();
    }
}
