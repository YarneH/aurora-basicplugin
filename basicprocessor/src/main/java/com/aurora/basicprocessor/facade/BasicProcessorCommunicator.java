package com.aurora.basicprocessor.facade;

import android.content.Context;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.auroralib.ProcessorCommunicator;
import com.aurora.basicprocessor.PluginConstants;
import com.aurora.basicprocessor.basicpluginobject.BasicPluginObject;

/**
 * Communicator interface to the BasicProcessor
 */
public class BasicProcessorCommunicator extends ProcessorCommunicator {

    public BasicProcessorCommunicator(Context context) {
        super(PluginConstants.UNIQUE_PLUGIN_NAME, context);
    }

    /**
     * Very simple process function that just adds some text to extractedText
     *
     * @param extractedText The text that was extracted after Aurora's internal processing
     * @return A string that consists of standard text and the result of extractedText.toString()
     */
    @Override
    protected PluginObject process(ExtractedText extractedText) {
        BasicPluginObject res = new BasicPluginObject(extractedText.getFilename());
        res.setResult("Basic Plugin processed and cached with result:" + "\n" + extractedText.toString());
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
    @Override
    protected PluginObject process(String fileName, String inputText) {
        BasicPluginObject res = new BasicPluginObject(fileName);
        res.setResult("Basic Plugin processed:\n" + inputText);
        return res;
    }


    /*
    private class ProcessorCacheThread extends Thread {
        private int mCacheResult = -1000; // - 1000 means that the cache service from Aurora has not been reached
        private BasicPluginObject pluginObject;
        private CacheServiceCaller mCacheServiceCaller;

        protected ProcessorCacheThread(BasicPluginObject pluginObject, CacheServiceCaller cacheServiceCaller) {
            this.pluginObject = pluginObject;
            this.mCacheServiceCaller = cacheServiceCaller;
        }

        protected int getCacheResult() {
            return mCacheResult;
        }

        public void run() {
            int cacheResult = mCacheServiceCaller.cacheOperation(pluginObject.toJSON());
            Log.d("PROCESSOR_CACHE_THREAD", "" + cacheResult);
        }
    }*/

}
