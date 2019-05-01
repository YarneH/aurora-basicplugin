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
import com.aurora.basicprocessor.basicpluginobject.BasicPluginObject;
import com.aurora.basicprocessor.facade.BasicProcessorCommunicator;

import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    /**
     *  Textview for showing the processed text
     */
    private TextView mTextView;

    // TODO: This should be singleton-like
    /**
     * Communicator that acts as an interface to the BasicPlugin's processor
     */
    private BasicProcessorCommunicator mBasicProcessorCommunicator = new BasicProcessorCommunicator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.textView);
        mTextView.setMovementMethod(new ScrollingMovementMethod());

        // Handle the data that came with the intent that opened BasicPlugin
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.getAction().equals(Constants.PLUGIN_ACTION)) {

            BasicPluginObject basicPluginObject = null;

            // TODO remove this if statement maybe. Is currently used to handle cases where a plain
            // String is sent instead of an ExtractedText
            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_TEXT)) {
                String inputText = intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TEXT);
                basicPluginObject = (BasicPluginObject) mBasicProcessorCommunicator.process(inputText);
            }

            // Handle ExtractedText object (received when first opening a new file)
            else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_EXTRACTED_TEXT)) {
                // Get the Uri to the transferred file
                Uri fileUri = intentThatStartedThisActivity.getData();

                StringBuilder total = new StringBuilder();
                if(fileUri != null) {
                    // Open the file
                    ParcelFileDescriptor inputPFD = null;
                    try {
                        inputPFD = getContentResolver().openFileDescriptor(fileUri, "r");
                    } catch (FileNotFoundException e) {
                        Log.e("MAIN", "There was a problem receiving the file from " +
                                "the plugin", e);
                    }

                    // Read the file
                    if(inputPFD != null) {
                        InputStream fileStream = new FileInputStream(inputPFD.getFileDescriptor());
                        BufferedReader r = new BufferedReader(new InputStreamReader(fileStream));
                        try {
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
                ExtractedText inputText = ExtractedText.fromJson(total.toString());
                basicPluginObject = (BasicPluginObject) mBasicProcessorCommunicator.process(inputText);
            }

            // TODO handle a BasicPluginObject that was cached (will come in Json format)
            else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_OBJECT)){
                return;
            }

            // Show the processed text
            if (basicPluginObject != null){
                String result = basicPluginObject.getResult();
                mTextView.setText(result);

                if(!basicPluginObject.getImages().isEmpty()) {
                    LinearLayout imageGallery = findViewById(R.id.imageGallery);
                    for (Bitmap image : basicPluginObject.getImages()) {
                        imageGallery.addView(getImageView(image));
                    }
                }
            }
        }
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