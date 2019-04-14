package com.aurora.basicprocessor.basicpluginobject;

import android.graphics.Bitmap;

import com.aurora.auroralib.PluginObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A concrete PluginObject that only has a String, which is to be shown in the environment
 */
public class BasicPluginObject extends PluginObject {
    /**
     * The resulting text to be displayed by BasicPlugin
     */
    private String mResult;
    private List<Bitmap> mImages = new ArrayList<>();

    public BasicPluginObject(){
        this.mResult = "";
    }

    public String getResult() { return mResult; }

    public void setResult(String result) { mResult = result; }

    public List<Bitmap> getImages() {
        return mImages;
    }

    public void setImages(List<Bitmap> mImages) {
        this.mImages = mImages;
    }
}
