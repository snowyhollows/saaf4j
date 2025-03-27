package net.snowyhollows.saaf4j.gwt;


import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(namespace = JsPackage.GLOBAL, isNative = true, name = "Object")
public class Result<T> {

    @JsConstructor
    public Result() {
    }

    @JsProperty native T getVal();
    @JsProperty native void setVal(T val);
}
