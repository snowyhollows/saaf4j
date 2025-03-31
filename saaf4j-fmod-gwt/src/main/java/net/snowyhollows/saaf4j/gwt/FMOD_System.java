package net.snowyhollows.saaf4j.gwt;

import com.google.gwt.typedarrays.shared.ArrayBuffer;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class FMOD_System {
    public static native void update();
    public static native void getEvent(String path, Result<FMOD_Studio_EventDescription> result);
    public static native void setParameterByName(String path, float value, boolean ignoreSeekSpeed);
    public static native void loadBankMemory(ArrayBuffer buffer, int length, int mode, int flags, Result<FMOD_Studio_Bank> result);

}
