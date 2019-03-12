package com.aurora.basicprocessor;

import com.aurora.auroralib.PluginObject;

public class BasicPluginObject extends PluginObject {
    /**
     * The resulting text to be displayed by BasicPlugin
     */
    private String mResult;

    public BasicPluginObject(){
        this.mResult = "";
    }

    public String getResult() {
        return mResult;
    }

    public void setResult(String result) {
        mResult = result;
    }

}
