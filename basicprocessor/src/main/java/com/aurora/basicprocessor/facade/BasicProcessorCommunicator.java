package com.aurora.basicprocessor.facade;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.auroralib.Section;
import com.aurora.basicprocessor.ProcessorCommunicator;
import com.aurora.basicprocessor.basicpluginobject.BasicPluginObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class BasicProcessorCommunicator extends ProcessorCommunicator {

    public BasicProcessorCommunicator(){}

    /**
     * Very simple process function that just adds some text to extractedText //TODO
     *
     * @param extractedText The text that was extracted after Aurora's internal processing
     * @return A string that consists of standard text and the result of extractedText.toString()
     */
    @Override
    public PluginObject process(ExtractedText extractedText) {
        BasicPluginObject res = new BasicPluginObject();
        res.setResult("Basic Plugin processed:\n" + extractedText.toString());

        for (Section section: extractedText.getSections()) {
            if(section.getImages() != null && !section.getImages().isEmpty()) {
                for (String image: section.getImages()) {
                    try{
                        InputStream stream = new ByteArrayInputStream(Base64.decode(image.getBytes()
                                , Base64.DEFAULT));
                        Bitmap imageBitmap = BitmapFactory.decodeStream(stream);
                        res.getImages().add(imageBitmap);
                    }
                    catch (Exception e) {
                        Log.e("IMAGE_LOADER", "Failed to load or decode an image", e);
                    }
                }
            }
        }

        return res;
    }

    // TODO depending on whether we will also allow regular Strings to be passed: either remove this
    // or include this as an abstract method maybe
    // Maybe also include it as an abstract method in the superclass  then because then it should
    // also always be implemented
    /**
     * Very simple process function that just adds some text to a String
     *
     * @param inputText The string that has to be processed
     * @return A string that consists of standard text and the inputText
     */
    public PluginObject process(String inputText) {
        BasicPluginObject res = new BasicPluginObject();
        res.setResult("Basic Plugin processed:\n" + inputText);
        return res;
    }
}
