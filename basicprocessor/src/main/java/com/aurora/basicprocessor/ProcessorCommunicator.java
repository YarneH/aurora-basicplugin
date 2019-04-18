package com.aurora.basicprocessor;

import android.content.Context;

import com.aurora.auroralib.CacheServiceCaller;
import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;

/**
 * Superclass for a 'Communicator', i.e. the interface between plugin environment and plugin processor
 */
public abstract class ProcessorCommunicator {

    /**
     * Processes an ExtractedText object (received from Aurora) and returns a PluginObject (or an object
     * of a subclass specific for the current plugin)
     *
     * @param extractedText The text that was extracted after Aurora's internal processing
     * @return The PluginObject that is the result of the plugin's processing of the extractedText
     */
    public abstract PluginObject process(ExtractedText extractedText, CacheServiceCaller cacheServiceCaller);

}
