package net.snowyhollows.saaf4j.gwt;

import com.google.gwt.core.client.GWT;

public class FmodGwtAdaptiveAudioConfiguration {
    public int initialMemorySize = 64 * 1024 * 1024; // 64 MB
    public String fmodScriptPath = GWT.getModuleBaseForStaticFiles() + "saaf4j/fmodstudio.js";
}
