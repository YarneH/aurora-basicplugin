package com.aurora.basicprocessor;

public class ProcessorCommunicator {

    public ProcessorCommunicator(){

    }

    public static BasicPluginObject delegate(String inputText){
        BasicPluginObject res = new BasicPluginObject();
        res.setResult("Basic Plugin processed:\n" + inputText);
        return res;
    }
}
