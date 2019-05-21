package com.aurora.basicprocessor.facade;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.aurora.auroralib.ExtractedImage;
import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.auroralib.ProcessorCommunicator;
import com.aurora.basicprocessor.basicpluginobject.BasicPluginObject;

import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.CoreMap;

/**
 * Communicator interface to the BasicProcessor
 */
public class BasicProcessorCommunicator extends ProcessorCommunicator {

    public BasicProcessorCommunicator(Context context) {
        /*
         * A UNIQUE_PLUGIN_NAME needs to be passed to the constructor of ProcessorCommunicator for
         * proper configuration of the cache
         */
        super(context);
    }

    /**
     * Very simple process function that just adds some text to extractedText. It also logs whether
     * or not NLP tokens are present
     *
     * @param extractedText The text that was extracted after Aurora's internal processing
     * @return A BasicPluginObject that consists of standard text and the result of
     * extractedText.toString(). It also contains Images if these were present.
     */
    @Override
    protected PluginObject process(@NonNull ExtractedText extractedText) {

        BasicPluginObject res = new BasicPluginObject(extractedText.getFilename());

        // Get the text
        res.setResult("Basic Plugin processed:\n" + extractedText.toString());

        // Get the images
        List<ExtractedImage> images = extractedText.getImages();

        for (ExtractedImage image : images) {
            Bitmap bitmap = image.getBitmap();
            if (bitmap != null) {
                res.getImages().add(bitmap);
            }
        }

        // If API level is at least 26, call NLP services
        // This code is only for illustration and only logs messages. 
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // Log whether there are NLP tags
            if (extractedText.getTitleAnnotation() != null) {
                List<CoreMap> sentences = extractedText.getTitleAnnotation().get(CoreAnnotations.SentencesAnnotation.class);

                processSentences(sentences);
            }
        }
        return res;
    }

    /**
     * Processes the sentences in a given list
     * @param sentences the list of sentences to be processed
     */
    private void processSentences(List<CoreMap> sentences) {
        for (CoreMap sentence : sentences) {
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            if (!tokens.isEmpty()) {
                CoreLabel token = tokens.get(0);
                // This log is currently to manually check if NLP is working.
                Log.d("NLP", token.tag());
            }
        }
    }

}
