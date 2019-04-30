package com.aurora.basicprocessor.facade;

/**
 * Communicator interface to the BasicProcessor
 */
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

public class BasicProcessorCommunicator extends ProcessorCommunicator {

    public BasicProcessorCommunicator(Context context) {
        super(PluginConstants.UNIQUE_PLUGIN_NAME, context);
    }

    /**
     * Very simple process function that just adds some text to extractedText //TODO
     *
     * @param extractedText The text that was extracted after Aurora's internal processing
     * @return A string that consists of standard text and the result of extractedText.toString()
     */
    @Override
    protected PluginObject process(ExtractedText extractedText) {
        // TODO: use extractedText.getFilename()
        BasicPluginObject res = new BasicPluginObject("dummyfilename");
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

    // TODO depending on whether we will also allow regular Strings to be passed: either remove this
    // or include this as an abstract method maybe
    // Maybe also include it as an abstract method in the superclass  then because then it should
    // also always be implemented


    /**
     * Very simple process function that just adds some text to a String
     *
     * @param fileName  the name of the file that contained the original text
     * @param inputText The string that has to be processed
     * @return A string that consists of standard text and the inputText
     */
    /*
    @Override
    protected PluginObject process(String fileName, String inputText) {
        BasicPluginObject res = new BasicPluginObject(fileName);
        res.setResult("Basic Plugin processed:\n" + inputText);
        return res;
    }

    */

}
