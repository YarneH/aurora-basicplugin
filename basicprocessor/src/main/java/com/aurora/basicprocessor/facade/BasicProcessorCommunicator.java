package com.aurora.basicprocessor.facade;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.basicprocessor.ProcessorCommunicator;
import com.aurora.basicprocessor.basicpluginobject.BasicPluginObject;

public class BasicProcessorCommunicator extends ProcessorCommunicator {

    public BasicProcessorCommunicator(){

    }

    @Override
    public PluginObject process(ExtractedText extractedText) {
        BasicPluginObject res = new BasicPluginObject();
        res.setResult("Basic Plugin processed:\n" + extractedText.toString());
        return res;
    }

    // TODO depending on whether we will also allow regular Strings to be passed: either remove this or include this as an abstract method maybe
    // Maybe also not include it as abstract method because then it should also always be implemented
    public PluginObject process(String inputText) {
        BasicPluginObject res = new BasicPluginObject();
        res.setResult("Basic Plugin processed:\n" + inputText);
        return res;
    }


    /*
    public static BasicPluginObject delegate(String inputText){
        BasicPluginObject res = new BasicPluginObject();
        res.setResult("Basic Plugin processed:\n" + inputText);
        return res;
    }
    */
}
