package com.aurora.basicprocessor.basicpluginobject;

import android.graphics.Bitmap;

import com.aurora.auroralib.PluginObject;

import java.util.ArrayList;
import java.util.List;

import static com.aurora.basicprocessor.PluginConstants.UNIQUE_PLUGIN_NAME;

/**
 * A concrete PluginObject that only has a String and possibly a list of Images,
 * which are to be shown in the environment
 */
public class BasicPluginObject extends PluginObject {
    /**
     * The resulting text to be displayed by BasicPlugin
     */
    private String mResult;

    /**
     * The possibly empty list of images from the file that is opened
     */
    private transient List<Bitmap> mImages = new ArrayList<>();

    public BasicPluginObject(String fileName) {
        super(fileName, UNIQUE_PLUGIN_NAME);
        mResult = "";
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
