package com.aurora.basicplugin;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurora.auroralib.translation.TranslationServiceCaller;
import com.aurora.basicprocessor.basicpluginobject.BasicPluginObject;
import com.aurora.basicprocessor.facade.BasicProcessorCommunicator;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends PluginActivity {

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
     * ServiceCaller for using Aurora's translation service
     */
    private TranslationServiceCaller mTranslationServiceCaller = null;
    
    /**
     * The BasicPluginObject that is being represented
     */
    private BasicPluginObject mBasicPluginObject = null;


    /**
     * Perform required initialization steps during the onCreate method.
     * First of three functions called during onCreate.
     */
    @Override
    protected void initializeOnCreate(){
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
        mProcessorCommunicator = new BasicProcessorCommunicator(getApplicationContext());
        /*
         * Initialize the TranslationServiceCaller
         */
        mTranslationServiceCaller = new TranslationServiceCaller(getApplicationContext());

        /*
         * Initialize a BasicPluginObject to avoid null pointers
         */
        mBasicPluginObject = new BasicPluginObject("");
        mBasicPluginObject.setResult(mTextView.getText().toString());
    }


    /**
     * Make a call to the framework function to process the intent.
     * This function needs to be implement to select the proper variable to assign the result to.
     * Second of three functions called during onCreate.
     *
     * @param intentThatStartedThisActivity Intent that started the activity. Handled by the framework
     */
    @Override
    protected void callProcessIntent(Intent intentThatStartedThisActivity){
        mBasicPluginObject = processIntent(intentThatStartedThisActivity,
                BasicPluginObject.class);
    }

    /**
     * Show the processed BasicPluginObject.
     * Third of three functions called during onCreate.
     */
    @Override
    protected void representPluginObject(){
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
            if (translatedSentences != null && !translatedSentences.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String s : translatedSentences) {
                    sb.append(s);
                    sb.append("\n");
                }
                mTextView.setText(sb.toString());
            } else {
                Toast.makeText(mTextView.getContext(), R.string.translation_error,
                        Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG, "Error in translation request");
            }
        }
    }
}
