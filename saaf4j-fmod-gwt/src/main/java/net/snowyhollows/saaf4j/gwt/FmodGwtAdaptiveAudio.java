package net.snowyhollows.saaf4j.gwt;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.typedarrays.shared.Uint8Array;
import com.google.gwt.user.client.Timer;
import net.snowyhollows.saaf4j.core.AdaptiveAudio;
import net.snowyhollows.saaf4j.core.Bank;
import net.snowyhollows.saaf4j.core.Cue;
import net.snowyhollows.saaf4j.core.Param;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class FmodGwtAdaptiveAudio implements AdaptiveAudio {
    private static final int STUDIO_LOAD_MEMORY = 0;
    private static final int STUDIO_LOAD_BANK_NORMAL = 0;
    private final ArrayList<Runnable> runnables = new ArrayList<>();

    private enum State {
        INITIALIZING, NOT_READY, READY, ERROR;
    }

    private static State state = State.NOT_READY;
    private Exception reason;

    public FmodGwtAdaptiveAudio(FmodGwtAdaptiveAudioConfiguration config) {
        init(config.initialMemorySize);
    }

    private void init(int initialMemory) {
        assertNotError();
        if (state == State.NOT_READY) {
            state = State.INITIALIZING;
            ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + "scripts/fmodstudio.js")
                    .setCallback(new Callback<>() {
                        @Override
                        public void onFailure(Exception reason) {
                            state = State.ERROR;
                            FmodGwtAdaptiveAudio.this.reason = reason;
                        }

                        @Override
                        public void onSuccess(Void result) {
                            new Timer() {

                                int millisWaitingForFmod = 0;
                                int step = 20;

                                @Override
                                public void run() {
                                    if (!isFmodReady()) {
                                        millisWaitingForFmod += step;
                                        if (millisWaitingForFmod > 10000) {
                                            state = State.ERROR;
                                            reason = new Exception("FMOD library not ready after 10 seconds");
                                            return;
                                        }
                                        schedule(step);
                                        return;
                                    }
                                    nativeInitGlobals(initialMemory);

                                    new Timer() {

                                        int millisWaitingForSystem = 0;
                                        int step = 20;

                                        @Override
                                        public void run() {
                                            if (!isSystemReady()) {
                                                millisWaitingForSystem += step;
                                                if (millisWaitingForSystem > 10000) {
                                                    state = State.ERROR;
                                                    reason = new Exception("FMOD system not ready after 10 seconds");
                                                    return;
                                                }
                                                schedule(step);
                                                return;
                                            }
                                            state = State.READY;
                                            afterInit();
                                        }
                                    }.run();
                                }
                            }.run();
                        }
                    })
                    .setWindow(ScriptInjector.TOP_WINDOW)
                    .inject();
        }
    }

    private void assertNotError() {
        if (state == State.ERROR) {
            throw new IllegalStateException("Error occurred during initialization", reason);
        }
    }

    private native Uint8Array createArrayBuffer(int length) /*-{
        return new $wnd.Uint8Array(length);
    }-*/;

    @Override
    public Bank loadBank(InputStream bankStream) {
        executeOrDelay(() -> {
            try {
                int length = bankStream.available();

                Uint8Array int8Array = createArrayBuffer(length);

                for (int i = 0; i < length; i++) {
                    int8Array.set(i, bankStream.read());
                }

                Result<FMOD_Studio_Bank> result = new Result<>();
                FMOD_System.loadBankMemory(int8Array.buffer(), int8Array.length(), STUDIO_LOAD_MEMORY, STUDIO_LOAD_BANK_NORMAL, result);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read and load bank data from InputStream", e);
            }
        });
        return new Bank() {
        };
    }

    @Override
    public Cue getCue(String s) {
        Result<FMOD_Studio_EventDescription> resultDescription = new Result<>();
        final Result<FMOD_Studio_EventInstance> resultInstance = new Result<>();

        executeOrDelay(() -> {
            FMOD_System.getEvent(s, resultDescription);
            resultDescription.getVal().createInstance(resultInstance);
        });

        return new Cue() {
            @Override
            public void in() {
                executeOrDelay(() -> {
                    resultInstance.getVal().start();
                });
            }

            @Override
            public void out() {
                executeOrDelay(() -> {
                    resultInstance.getVal().stop(0);
                });
            }
        };
    }

    private native void debug(String label, Object o) /*-{
        $wnd[label] = o;
    }-*/;

    @Override
    public Param getGlobalParam(String path) {
        return new Param() {
            @Override
            public void set(float value) {
                FMOD_System.setParameterByName(path, value, false);
            }
        };
    }

    private void executeOrDelay(Runnable runnable) {
        if (state == State.READY) {
            runnable.run();
        } else if (state != State.ERROR) {
            runnables.add(runnable);
        }
    }

    @Override
    public boolean isReady() {
        assertNotError();
        return state == State.READY;
    }

    @Override
    public void update(float deltaTime) {
        if (!isReady()) {
            return;
        }
        FMOD_System.update();
    }

    private void afterInit() {
        for (Runnable runnable : runnables) {
            runnable.run();
        }
    }

    private native boolean isSystemReady() /*-{
        return $wnd.FMOD_System_Core != null;
    }-*/;

    private native boolean isFmodReady() /*-{
        return $wnd.FMODModule != null;
    }-*/;

    private native void nativeInitGlobals(int initialMemory) /*-{
        $wnd.FMOD = {
            preRun: function () {
            },
            onRuntimeInitialized: function () {
                var outval = {};
                var result;

                result = $wnd.FMOD.Studio_System_Create(outval);
                CHECK_RESULT(result);

                $wnd.FMOD_System = outval.val;

                result = $wnd.FMOD_System.getCoreSystem(outval);
                CHECK_RESULT(result);

                $wnd.FMOD_System_Core = outval.val;

                result = $wnd.FMOD_System_Core.setDSPBufferSize(4096, 2);
                CHECK_RESULT(result);

                result = $wnd.FMOD_System_Core.getDriverInfo(0, null, null, outval, null, null);
                CHECK_RESULT(result);

                result = $wnd.FMOD_System_Core.setSoftwareFormat(outval.val, $wnd.FMOD.SPEAKERMODE_DEFAULT, 0)
                CHECK_RESULT(result);

                result = $wnd.FMOD_System.initialize(1024, $wnd.FMOD.STUDIO_INIT_NORMAL, $wnd.FMOD.INIT_NORMAL, null);
                CHECK_RESULT(result);

                return $wnd.FMOD.OK;
            },
            INITIAL_MEMORY: initialMemory
        };

        $wnd.FMODModule($wnd.FMOD);

        function CHECK_RESULT(result) {
            if (result != $wnd.FMOD.OK) throw $wnd.FMOD.ErrorString(result);
        }
    }-*/;
}
