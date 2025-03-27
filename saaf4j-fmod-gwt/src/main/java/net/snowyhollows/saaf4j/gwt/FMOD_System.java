package net.snowyhollows.saaf4j.gwt;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class FMOD_System {
    public static native void update();
    public static native void getEvent(String path, Result<FMOD_Studio_EventDescription> result);

    public static native void setParameterByName(String path, float value, boolean ignoreSeekSpeed);
}
