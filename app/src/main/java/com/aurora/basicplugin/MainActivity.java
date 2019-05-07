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
     *  Textview for showing the processed text
     */
    private TextView mTextView;

    // TODO: This should be singleton-like
    /**
     * Communicator that acts as an interface to the BasicPlugin's processor
     */
    private BasicProcessorCommunicator mBasicProcessorCommunicator;
    /**
     * ServiceCaller for using Aurora's translatipon service
     */
    private TranslationServiceCaller mTranslationServiceCaller;
    /**
     * The BasicPluginObject that is being represented
     */
    private BasicPluginObject mBasicPluginObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.textView);
        /*
         * This OnClickListener calls The translationTask which is defined lower.
         */
        mTextView.setOnClickListener(v -> {
            List<String> inputSentences;
            if(mBasicPluginObject.getResult() != null) {
                inputSentences = Arrays.asList(mBasicPluginObject.getResult().split("\n"));
                new TranslationTask(inputSentences, "en", "nl",
                        mTranslationServiceCaller, v).execute();
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

        //Remove this (Is now used for testing sometimes)
        /*
        BasicPluginObject testBasicPluginObject = (BasicPluginObject)
                mBasicProcessorCommunicator.pipeline("dummyfilename", "test");
        String testResult = testBasicPluginObject.getResult();
        mTextView.setText(testResult);
        */
        mBasicPluginObject = new BasicPluginObject("");
        mBasicPluginObject.setResult(mTextView.getText().toString());

        // Handle the data that came with the intent that opened BasicPlugin
        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity.getAction() == null) {
            Toast.makeText(this, "ERROR: The intent had no action.", Snackbar.LENGTH_LONG).show();
            return;
        } else if(!intentThatStartedThisActivity.getAction().equals(Constants.PLUGIN_ACTION)) {
            Toast.makeText(this, "ERROR: The intent had incorrect action.", Snackbar.LENGTH_LONG).show();
            return;
        } else if(!intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_TYPE)) {
            Toast.makeText(this, "ERROR: The intent had no specified input type.",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        // Get the input type
        String inputType = intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TYPE);

        // Get the Uri to the transferred file
        Uri fileUri = intentThatStartedThisActivity.getData();
        if(fileUri == null) {
            Toast.makeText(this, "ERROR: The intent had no url in the data field",
                    Snackbar.LENGTH_LONG).show();
            return;
        }

        // Switch on the different kinds of input types that could be in the temp file
        switch (inputType) {

            case Constants.PLUGIN_INPUT_TYPE_EXTRACTED_TEXT:
                // Convert the read file to an ExtractedText object
                try {
                    ExtractedText inputText = ExtractedText.getExtractedTextFromFile( fileUri,
                            this);
                    mBasicPluginObject = (BasicPluginObject) mBasicProcessorCommunicator.pipeline(inputText);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                break;

            case Constants.PLUGIN_INPUT_TYPE_OBJECT:
                // Convert the read file to an PluginObject
                try {
                    mBasicPluginObject = BasicPluginObject.getPluginObjectFromFile(fileUri, this,
                            BasicPluginObject.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                break;


            default:
                Toast.makeText(this, "ERROR: The intent had an unsupported input type.",
                        Snackbar.LENGTH_LONG).show();
                return;
        }

        // Show the processed text
        if (mBasicPluginObject != null){
            String filename = mBasicPluginObject.getFileName();
            String result = mBasicPluginObject.getResult();
            mTextView.setText(filename + '\n' + result);

            List<Bitmap> images = mBasicPluginObject.getImages();
            if(images != null && !mBasicPluginObject.getImages().isEmpty()) {
                LinearLayout imageGallery = findViewById(R.id.imageGallery);
                for (Bitmap image : mBasicPluginObject.getImages()) {
                    imageGallery.addView(getImageView(image));
                }
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    private View getImageView(Bitmap image) {
        ImageView imageView = new ImageView(getApplicationContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 10, 0);
        imageView.setLayoutParams(lp);
        imageView.setImageBitmap(image);
        return imageView;
    }

    /**
     * Asynctask to perform translation operation.
     * You should only adapt the onPostExecute method to your liking.
     *
     * For now it is good that this is not part of auroralib, since the TranslationTask here also
     * receives a View as an input (the view that is to be updated), but you might want to change
     * this to your liking for your plugin.
     */
    // TODO: Maybe move abstract version to auroralib instead of ProcessorTranslationThread and make
    //  onPostExecute abstract
    static public class TranslationTask extends AsyncTask<Void, Void, List<String>> {
        private List<String> mSentences;
        private String mSourceLanguage;
        private String mDestinationLanguage;
        private TranslationServiceCaller mTranslationServiceCaller;
        private TextView mTextView;
        //private WeakReference<Activity> mActivityWeakReference;


        TranslationTask(List<String> sentences, String sourceLanguage, String destinationLanguage,
                        TranslationServiceCaller translationServiceCaller, View v){
            this.mSentences = sentences;
            this.mSourceLanguage = sourceLanguage;
            this.mDestinationLanguage = destinationLanguage;
            this.mTranslationServiceCaller = translationServiceCaller;
            this.mTextView = (TextView) v;
            //this.mActivityWeakReference = new WeakReference<Activity>(activity);
        }


        @Override protected List<String> doInBackground(Void... params) {
            List<String> result = mTranslationServiceCaller.translateOperation(mSentences,
                    mSourceLanguage, mDestinationLanguage);
            Log.d(getClass().getSimpleName(), result.toString());
            return result;
        }

        @Override protected void onPostExecute(List<String> translatedSentences) {
            Log.d(getClass().getSimpleName(), translatedSentences.toString());
            StringBuilder sb = new StringBuilder();
            for (String s : translatedSentences)
            {
                sb.append(s);
                sb.append("\n");
            }
            mTextView.setText(sb.toString());
        }
    }
}