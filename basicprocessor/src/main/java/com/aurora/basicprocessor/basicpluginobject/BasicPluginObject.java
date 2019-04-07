package com.aurora.basicprocessor.basicpluginobject;

import com.aurora.auroralib.PluginObject;

/**
 * A concrete PluginObject that only has a String, which is to be shown in the environment
 */
public class BasicPluginObject extends PluginObject {
    /**
     * The resulting text to be displayed by BasicPlugin
     */
    private String mResult;

    public BasicPluginObject(){
        this.mResult = "";
    }

    public String getResult() { return mResult; }

    public void setResult(String result) { mResult = result; }

}
