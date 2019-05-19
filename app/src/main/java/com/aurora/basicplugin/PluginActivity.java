package com.aurora.basicplugin;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.PluginObject;
import com.aurora.auroralib.ProcessorCommunicator;

import java.io.IOException;

/**
 * Superclass for a Mainactivity of a plugin.
 * This class holds framework functionality that deals with processing the intent sent by Aurora
 */
public abstract class PluginActivity extends AppCompatActivity {

    /**
     * Tag for logging
     */
    private static final String LOG_TAG = PluginActivity.class.getSimpleName();

    /**
     * Processes the intent that started this activity
     *
     * @param intentThatStartedThisActivity the intent that started this activity
     * @return boolean indicating if processing the intent was successful
     */
    protected <T extends PluginObject> T processIntent(Intent intentThatStartedThisActivity, ProcessorCommunicator
                                         processorCommunicator, Class<T> type) {

        // First check if intent is good
        if (checkIntent(intentThatStartedThisActivity)) {
            return null;
        }

        // If not failed, continue processing

        // Get the Uri to the transferred file
        Uri fileUri = intentThatStartedThisActivity.getData();
        if (fileUri == null) {
            Toast.makeText(this, "ERROR: The intent had no url in the data field",
                    Snackbar.LENGTH_LONG).show();
            return null;
        }

        // Get the input type
        String inputType =
                intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TYPE);

        // Switch on the different kinds of input types that could be in the temp file
        if (Constants.PLUGIN_INPUT_TYPE_EXTRACTED_TEXT.equals(inputType)) {
            return processExtractedText(fileUri, processorCommunicator, type);

        } else if (Constants.PLUGIN_INPUT_TYPE_OBJECT.equals(inputType)) {
            return processPluginObject(fileUri, type);

        } else {
            Toast.makeText(this, "ERROR: The intent had an unsupported input type.",
                    Snackbar.LENGTH_LONG).show();
            return null;
        }
    }

    /**
     * Processes the read file as a plugin object
     *
     * @param fileUri the uri of the file
     * @return true if the processing was successful, false otherwise
     */
    private <T extends PluginObject> T processPluginObject(Uri fileUri, @NonNull Class<T> type) {
        T pluginObject = null;
        try {
            pluginObject = T.getPluginObjectFromFile(fileUri, this,
                    type);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Something went wrong with getting the plugin object", e);
        }
        return pluginObject;
    }

    /**
     * Processes the read file as an extracted text object
     *
     * @param fileUri the uri of the file
     * @return true if successful, false otherwise
     */
    private <T extends PluginObject> T processExtractedText(Uri fileUri, ProcessorCommunicator
            processorCommunicator, @NonNull Class<T> type) {
        T pluginObject = null;
        try {
            ExtractedText inputText = ExtractedText.getExtractedTextFromFile(fileUri,
                    this);
            pluginObject =
                    (T) processorCommunicator.pipeline(inputText);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Something went wrong with getting the extracted text", e);
        }
        return pluginObject;
    }


    /**
     * Check if the intent that started this activity has the right attributes, will also show a
     * toast to the user
     * in that case.
     *
     * @param intentThatStartedThisActivity the intent that started this activity
     * @return true if the intent does not have the right attributes
     */
    private boolean checkIntent(Intent intentThatStartedThisActivity) {
        boolean intentWrong = false;

        if (intentThatStartedThisActivity.getAction() == null) {
            Toast.makeText(this, "ERROR: The intent had no action.", Snackbar.LENGTH_LONG).show();
            intentWrong = true;
        } else if (!intentThatStartedThisActivity.getAction().equals(Constants.PLUGIN_ACTION)) {
            Toast.makeText(this, "ERROR: The intent had incorrect action.", Snackbar.LENGTH_LONG).show();
            intentWrong = true;
        } else if (!intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_TYPE)) {
            Toast.makeText(this, "ERROR: The intent had no specified input type.",
                    Snackbar.LENGTH_LONG).show();
            intentWrong = true;
        }

        return intentWrong;
    }
}
