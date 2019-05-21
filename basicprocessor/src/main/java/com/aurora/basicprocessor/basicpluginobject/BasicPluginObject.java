package com.aurora.basicprocessor.basicpluginobject;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.aurora.auroralib.BitmapListAdapter;
import com.aurora.auroralib.PluginObject;
import com.google.gson.annotations.JsonAdapter;

import java.util.ArrayList;
import java.util.List;

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
    @JsonAdapter(BitmapListAdapter.class)
    private final List<Bitmap> mImages = new ArrayList<>();

    public BasicPluginObject(String fileName) {
        super(fileName);
        mResult = "";
    }

    public String getResult() { return mResult; }

    public void setResult(String result) { mResult = result; }

    public @NonNull List<Bitmap> getImages() {
        return mImages;
    }
}
