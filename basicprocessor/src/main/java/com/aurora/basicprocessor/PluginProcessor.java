package com.aurora.basicprocessor;

import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;

public interface PluginProcessor {
    public PluginObject process(ExtractedText extractedText);
}
