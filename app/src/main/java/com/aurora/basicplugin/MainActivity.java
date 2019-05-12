package com.aurora.basicplugin;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.translation.TranslationServiceCaller;
import com.aurora.basicprocessor.basicpluginobject.BasicPluginObject;
import com.aurora.basicprocessor.facade.BasicProcessorCommunicator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * Tag for logging
     */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Constant for right margin
     */
    private static final int RIGHT_MARGIN = 10;

    /**
     * Textview for showing the processed text
     */
    private TextView mTextView = null;

    /**
     * Communicator that acts as an interface to the BasicPlugin's processor
     */
    private BasicProcessorCommunicator mBasicProcessorCommunicator = null;

    /**
     * ServiceCaller for using Aurora's translatipon service
     */
    private TranslationServiceCaller mTranslationServiceCaller = null;
    
    /**
     * The BasicPluginObject that is being represented
     */
    private BasicPluginObject mBasicPluginObject = null;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.textView);
        /*
         * This OnClickListener calls The translationTask which is defined lower.
         */
        mTextView.setOnClickListener((View v) -> {
            List<String> inputSentences;
            if (mBasicPluginObject.getResult() != null) {
                inputSentences = Arrays.asList(mBasicPluginObject.getResult().split("\n"));
                new TranslationTask(inputSentences, "en", "nl",
                        mTranslationServiceCaller, v).execute();
                v.setOnClickListener(null);
            }
        });

        mTextView.setMovementMethod(new ScrollingMovementMethod());

        /*
         * Initialize the communicator
         */
        mBasicProcessorCommunicator = new BasicProcessorCommunicator(getApplicationContext());
        /*
         * Initialize the TranslationServiceCaller
         */
        mTranslationServiceCaller = new TranslationServiceCaller(getApplicationContext());

        // TODO: DO these 2 lines something meaningfull?
        mBasicPluginObject = new BasicPluginObject("");
        mBasicPluginObject.setResult(mTextView.getText().toString());

        // Handle the data that came with the intent that opened BasicPlugin
        Intent intentThatStartedThisActivity = getIntent();

        processIntent(intentThatStartedThisActivity);
    }

    /**
     * Processes the intent that started this activity
     *
     * @param intentThatStartedThisActivity the intent that started this activity
     */
    private void processIntent(Intent intentThatStartedThisActivity) {

        // First check if intent is good
        if (checkIntent(intentThatStartedThisActivity)) {
            return;
        }

        // If not failed, continue processing

        // Get the Uri to the transferred file
        Uri fileUri = intentThatStartedThisActivity.getData();
        if (fileUri == null) {
            Toast.makeText(this, "ERROR: The intent had no url in the data field",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        // Get the input type
        String inputType =
                intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TYPE);
        boolean successful;

        // Switch on the different kinds of input types that could be in the temp file
        if (Constants.PLUGIN_INPUT_TYPE_EXTRACTED_TEXT.equals(inputType)) {
            successful = processExtractedText(fileUri);

        } else if (Constants.PLUGIN_INPUT_TYPE_OBJECT.equals(inputType)) {
            successful = processPluginObject(fileUri);

        } else {
            Toast.makeText(this, "ERROR: The intent had an unsupported input type.",
                    Snackbar.LENGTH_LONG).show();
            successful = false;
        }

        // If extraction was not successful, return
        if (!successful) {
            return;
        }

        // Show the processed text
        if (mBasicPluginObject != null) {
            String text =
                    mBasicPluginObject.getFileName() +
                    "\n" +
                    mBasicPluginObject.getResult();
            mTextView.setText(text);

            List<Bitmap> images = mBasicPluginObject.getImages();
            if (!images.isEmpty()) {
                LinearLayout imageGallery = findViewById(R.id.imageGallery);
                for (Bitmap image : images) {
                    imageGallery.addView(getImageView(image));
                }
            }
        }

    }

    /**
     * Processes the read file as a plugin object
     *
     * @param fileUri the uri of the file
     * @return true if the processing was successful, false otherwise
     */
    private boolean processPluginObject(Uri fileUri) {
        boolean success = true;
        try {
            mBasicPluginObject = BasicPluginObject.getPluginObjectFromFile(fileUri, this,
                    BasicPluginObject.class);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Something went wrong with getting the plugin object", e);
            success = false;
        }
        return success;
    }

    /**
     * Processes the read file as an extracted text object
     *
     * @param fileUri the uri of the file
     * @return true if successful, false otherwise
     */
    private boolean processExtractedText(Uri fileUri) {
        boolean success = true;
        try {
            ExtractedText inputText = ExtractedText.getExtractedTextFromFile(fileUri,
                    this);
            mBasicPluginObject =
                    (BasicPluginObject) mBasicProcessorCommunicator.pipeline(inputText);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Something went wrong with getting the extracted text", e);
            success = false;
        }
        return success;
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

    /**
     * Returns an image view for a single bitmap. This is intended for use in the galery.
     *
     * @param image a Bitmap of the image
     * @return a View of the image
     */
    private View getImageView(Bitmap image) {
        ImageView imageView = new ImageView(getApplicationContext());
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, RIGHT_MARGIN, 0);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageBitmap(image);
        return imageView;
    }

    /**
     * Asynctask to perform translation operation.
     * You should only adapt the onPostExecute method to your liking.
     * <p>
     * For now it is good that this is not part of auroralib, since the TranslationTask here also
     * receives a View as an input (the view that is to be updated), but you might want to change
     * this to your liking for your plugin.
     */
    // TODO: Maybe move abstract version to auroralib instead of ProcessorTranslationThread and make
    //  onPostExecute abstract
    public static class TranslationTask extends AsyncTask<Void, Void, List<String>> {
        private List<String> mSentences;
        private String mSourceLanguage;
        private String mDestinationLanguage;
        private TranslationServiceCaller mTranslationServiceCaller;
        private TextView mTextView;


        TranslationTask(List<String> sentences, String sourceLanguage, String destinationLanguage,
                        TranslationServiceCaller translationServiceCaller, View v) {
            this.mSentences = sentences;
            this.mSourceLanguage = sourceLanguage;
            this.mDestinationLanguage = destinationLanguage;
            this.mTranslationServiceCaller = translationServiceCaller;
            this.mTextView = (TextView) v;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> result = mTranslationServiceCaller.translateOperation(mSentences,
                    mSourceLanguage, mDestinationLanguage);
            Log.v(LOG_TAG, result.toString());
            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute(List<String> translatedSentences) {
            if (translatedSentences != null) {
                StringBuilder sb = new StringBuilder();
                for (String s : translatedSentences) {
                    sb.append(s);
                    sb.append("\n");
                }
                mTextView.setText(sb.toString());
            } else {
                Log.e(LOG_TAG, "Error in translation request");
            }
        }
    }
}
