package com.aurora.basicplugin;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.auroralib.translation.ProcessorTranslationThread;
import com.aurora.auroralib.translation.TranslationServiceCaller;
import com.aurora.basicprocessor.basicpluginobject.BasicPluginObject;
import com.aurora.basicprocessor.facade.BasicProcessorCommunicator;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
    private TranslationServiceCaller mTranslationServiceCaller;
    private BasicPluginObject mBasicPluginObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.textView);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> inputSentences;
                //inputSentences = Arrays.asList(mBasicPluginObject.getResult().split("."));
                inputSentences = new ArrayList<>();
                inputSentences.add(mBasicPluginObject.getResult());

                ProcessorTranslationThread processorTranslationThread = new ProcessorTranslationThread(
                      inputSentences, "en", "nl", mTranslationServiceCaller);
                processorTranslationThread.start();
                /*
                try {
                    processorTranslationThread.join();
                } catch (InterruptedException e) {
                    Log.e(getClass().getSimpleName(), "Exception during translation join", e);

                    // Restore the interrupted state:
                    // https://www.ibm.com/developerworks/java/library/j-jtp05236/index.html?ca=drs-#2.1
                    Thread.currentThread().interrupt();
                }
                List<String> translatedSentences = processorTranslationThread.getTranslatedSentences();

                StringBuilder sb = new StringBuilder();
                for (String s : translatedSentences)
                {
                    sb.append(s);
                    sb.append(".");
                }
                mTextView.setText(sb.toString());
                */

            }
        });

        mTextView.setMovementMethod(new ScrollingMovementMethod());

        /*
         * Initialize the communicator
         */
        mBasicProcessorCommunicator = new BasicProcessorCommunicator(getApplicationContext());
        mTranslationServiceCaller = new TranslationServiceCaller(getApplicationContext());

        //Remove this
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
        if (intentThatStartedThisActivity.getAction().equals(Constants.PLUGIN_ACTION)) {
            // Handle ExtractedText object (received when first opening a new file)
            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_EXTRACTED_TEXT)) {
                // Get the Uri to the transferred file
                Uri fileUri = intentThatStartedThisActivity.getData();


                // Convert the read file to an ExtractedText object
                ExtractedText inputText = getExtractedTextFromFile(fileUri);
                mBasicPluginObject = (BasicPluginObject) mBasicProcessorCommunicator.pipeline(inputText);
            }

            // TODO handle a BasicPluginObject that was cached (will come in Json format)
            else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_OBJECT)){
                String basicPluginObjectJson =
                        intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_OBJECT);
                Log.d(getClass().getCanonicalName(), basicPluginObjectJson);
                mBasicPluginObject = (BasicPluginObject)
                        BasicPluginObject.fromJson(basicPluginObjectJson, BasicPluginObject.class);
                Log.d(getClass().getCanonicalName(), mBasicPluginObject.getFileName());
                Log.d(getClass().getCanonicalName(), mBasicPluginObject.getResult());
            }

            // Represent
            // Show the processed text
            if (mBasicPluginObject != null){
                String filename = mBasicPluginObject.getFileName();
                String result = mBasicPluginObject.getResult();
                mTextView.setText(filename + '\n' + result);

                if(!mBasicPluginObject.getImages().isEmpty()) {
                    LinearLayout imageGallery = findViewById(R.id.imageGallery);
                    for (Bitmap image : mBasicPluginObject.getImages()) {
                        imageGallery.addView(getImageView(image));
                    }
                }
            }
        }
    }

    //TODO: make this a fuunction of ExtractedText in auroralib
    private ExtractedText getExtractedTextFromFile(Uri fileUri){
        StringBuilder total = new StringBuilder();
        ParcelFileDescriptor inputPFD = null;
        if(fileUri != null) {
            // Open the file
            try {
                inputPFD = getContentResolver().openFileDescriptor(fileUri, "r");
            } catch (FileNotFoundException e) {
                Log.e("MAIN", "There was a problem receiving the file from " +
                        "the plugin", e);
            }

            // Read the file
            if (inputPFD != null) {
                InputStream fileStream = new FileInputStream(inputPFD.getFileDescriptor());


                try (BufferedReader r = new BufferedReader(new InputStreamReader(fileStream))) {
                    for (String line; (line = r.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }
                } catch (IOException e) {
                    Log.e("MAIN", "There was a problem receiving the file from " +
                            "the plugin", e);
                }
            } else {
                Log.e("MAIN", "There was a problem receiving the file from " +
                        "the plugin");
            }
        } else {
            Log.e("MAIN", "There was a problem receiving the file from " +
                    "the plugin");
        }

        // Convert the read file to an ExtractedText object
        return ExtractedText.fromJson(total.toString());
    }

    protected void onDestroy() {
        //mCacheServiceCaller.unbindService();
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
}