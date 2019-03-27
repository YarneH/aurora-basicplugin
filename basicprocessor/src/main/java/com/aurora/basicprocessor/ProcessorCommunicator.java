package com.aurora.basicprocessor;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;

public abstract class ProcessorCommunicator {
    private PluginObject mPluginObject;

    public abstract PluginObject process(ExtractedText extractedText);


    /*public static BasicPluginObject delegate(String inputText){
        BasicPluginObject res = new BasicPluginObject();
        res.setResult("Basic Plugin processed:\n" + inputText);
        return res;
    }*/
}
