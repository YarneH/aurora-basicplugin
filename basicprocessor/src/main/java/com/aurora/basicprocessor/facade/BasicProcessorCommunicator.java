package com.aurora.basicprocessor.facade;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.auroralib.Section;
import com.aurora.auroralib.ProcessorCommunicator;
import com.aurora.basicprocessor.PluginConstants;
import com.aurora.basicprocessor.basicpluginobject.BasicPluginObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreNLPProtos;
import edu.stanford.nlp.pipeline.ProtobufAnnotationSerializer;
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
        super(PluginConstants.UNIQUE_PLUGIN_NAME, context);
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
    protected PluginObject process(ExtractedText extractedText) {
        // TODO: use extractedText.getFilename()
        BasicPluginObject res = new BasicPluginObject("dummyfilename");

        // Get the text
        res.setResult("Basic Plugin processed:\n" + extractedText.toString());

        // Get the images
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

        // Log whether there are NLP tags
        if(extractedText.getTitleAnnotation() != null) {
            List<CoreMap> sentences = extractedText.getTitleAnnotation().get(CoreAnnotations.SentencesAnnotation.class);

            for (CoreMap sentence : sentences) {
                List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
                if (tokens.size() > 0) {
                    CoreLabel token = tokens.get(0);
                    Log.d("NLP", token.tag());
                }
            }
        }
        return res;
    }

}
