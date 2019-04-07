package com.aurora.basicprocessor.facade;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.basicprocessor.ProcessorCommunicator;
import com.aurora.basicprocessor.basicpluginobject.BasicPluginObject;

public class BasicProcessorCommunicator extends ProcessorCommunicator {

    public BasicProcessorCommunicator(){}

    /**
     * Very simple process function that just adds some text to extractedText
     *
     * @param extractedText The text that was extracted after Aurora's internal processing
     * @return A string that consists of standard text and the result of extractedText.toString()
     */
    @Override
    public PluginObject process(ExtractedText extractedText) {
        BasicPluginObject res = new BasicPluginObject();
        res.setResult("Basic Plugin processed:\n" + extractedText.toString());
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
