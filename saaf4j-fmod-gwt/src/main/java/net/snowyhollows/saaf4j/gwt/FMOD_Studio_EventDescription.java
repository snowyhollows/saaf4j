package net.snowyhollows.saaf4j.gwt;


import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface FMOD_Studio_EventDescription {
    void createInstance(Result<FMOD_Studio_EventInstance> result);
}
