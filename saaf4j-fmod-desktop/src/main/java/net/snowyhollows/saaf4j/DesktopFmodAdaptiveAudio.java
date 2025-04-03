package net.snowyhollows.saaf4j;

import net.snowyhollows.saaf4j.core.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.fmod.FMOD;
import org.lwjgl.fmod.FMODStudio;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Locale;

public class DesktopFmodAdaptiveAudio implements AdaptiveAudio {

    MemoryStack stack = MemoryStack.stackPush();
    private final long system;

    public DesktopFmodAdaptiveAudio() {
        String module = "net/snowyhollows/saaf4j";
        Configuration.FMOD_LIBRARY_NAME.set(resourcePath("fmod", module));
        Configuration.FMOD_STUDIO_LIBRARY_NAME.set(resourcePath("fmodstudio", module));

        try (MemoryStack stack = MemoryStack.stackPush()) {
            int result;
            PointerBuffer buf = stack.mallocPointer(1);
            result = FMODStudio.FMOD_Studio_System_Create(buf, FMOD.FMOD_VERSION);
            check(result);

            system = buf.get(0);

            int maxchannels = 32;
            int studioflags = FMODStudio.FMOD_STUDIO_INIT_NORMAL | FMODStudio.FMOD_STUDIO_INIT_LIVEUPDATE;
            int flags = FMOD.FMOD_INIT_NORMAL;
            result = FMODStudio.FMOD_Studio_System_Initialize(system, maxchannels, studioflags, flags, 0);
            check(result);
        }
    }

    @Override
    public SoundBank loadBank(InputStream bankStream) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer buf = stack.mallocPointer(1);
            byte[] data = toByteBuffer(bankStream);
            ByteBuffer buffer = MemoryUtil.memAlloc(data.length);

            try {
                buffer.put(data);
                buffer.flip();
                int result = FMODStudio.FMOD_Studio_System_LoadBankMemory(system, buffer, FMODStudio.FMOD_STUDIO_LOAD_BANK_NORMAL, FMODStudio.FMOD_STUDIO_LOAD_BANK_NORMAL, buf);
                check(result);
            } finally {
                MemoryUtil.memFree(buffer);
            }


            long bank = buf.get(0);

            return new SoundBank() {
                public void unload() {
                    int result = FMODStudio.FMOD_Studio_Bank_Unload(bank);
                    check(result);
                }

            };
        }
    }

    @Override
    public Event getEvent(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer eventPointer = stack.mallocPointer(1);
            int result = FMODStudio.FMOD_Studio_System_GetEvent(system, path, eventPointer);
            check(result);
            long event = eventPointer.get(0);

            return new Event() {
                @Override
                public Cue getCue() {
                    try (MemoryStack stack = MemoryStack.stackPush()) {
                        PointerBuffer buf = stack.mallocPointer(1);
                        int result = FMODStudio.FMOD_Studio_EventDescription_CreateInstance(event, buf);
                        check(result);
                        long eventInstance = buf.get(0);

                        return new Cue() {
                            @Override
                            public void in() {
                                int result = FMODStudio.FMOD_Studio_EventInstance_Start(eventInstance);
                                check(result);
                            }

                            @Override
                            public void out() {
                                int result = FMODStudio.FMOD_Studio_EventInstance_Stop(eventInstance, 0);
                                check(result);
                            }
                        };
                    }
                }
            };
        }
    }

    @Override
    public FloatParam getGlobalFloatParam(String uri) {
        return new FloatParam() {
            @Override
            public void set(float value) {
                int result = FMODStudio.FMOD_Studio_System_SetParameterByName(system, uri, value, 0);
                check(result);
            }
        };
    }

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void update(float deltaTime) {
        int result = FMODStudio.FMOD_Studio_System_Update(system);
        check(result);
    }

    @Override
    public void dispose() {
        int result = FMODStudio.FMOD_Studio_System_Release(system);
        check(result);
    }

    private static void check(int result) {
        if (result != FMOD.FMOD_OK) {
            throw new RuntimeException("unexpected error: " + result);
        }
    }

    private String resourcePath(String shortLibraryName, String module) {
        String name = Platform.get().getName();
        Platform.Architecture architecture = Platform.getArchitecture();
        String libName = Platform.mapLibraryNameBundled(shortLibraryName);

        String resourceDir = name.toLowerCase(Locale.ROOT)
                + "/" + architecture.name().toLowerCase(Locale.ROOT);
        return resourceDir + "/" + module + "/" + System.mapLibraryName(libName);
    }

    private byte[] toByteBuffer(InputStream inputStream) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] temp = new byte[64 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(temp)) != -1) {
                buffer.write(temp, 0, bytesRead);

            }
            return buffer.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
