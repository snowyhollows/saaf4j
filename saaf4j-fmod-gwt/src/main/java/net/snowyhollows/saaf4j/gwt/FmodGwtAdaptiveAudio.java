package net.snowyhollows.saaf4j.gwt;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.user.client.Window;
import net.snowyhollows.saaf4j.core.AdaptiveAudio;
import net.snowyhollows.saaf4j.core.Bank;
import net.snowyhollows.saaf4j.core.Cue;
import net.snowyhollows.saaf4j.core.Param;

import java.io.InputStream;

public class FmodGwtAdaptiveAudio implements AdaptiveAudio {



    public void init() {
        ScriptInjector.fromUrl(GWT.getModuleBaseForStaticFiles() + "scripts/fmodstudio.js")
                .setCallback(new Callback<Void, Exception>() {
                    @Override
                    public void onFailure(Exception reason) {
                        Window.alert("dupa!");
                    }

                    @Override
                    public void onSuccess(Void result) {
                        Window.alert("sukes!");
                    }
                })
                .setWindow(ScriptInjector.TOP_WINDOW)
                .inject();
    }

    @Override
    public Bank loadBank(String bank) {
        return null;
    }

    @Override
    public Bank loadBank(InputStream bankStream) {
        return null;
    }

    @Override
    public Cue getCue(String path) {
        return null;
    }

    @Override
    public Param getGlobalParam(String path) {
        return null;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public boolean isHealthy() {
        return false;
    }

    @Override
    public void update(float deltaTime) {

    }
}
