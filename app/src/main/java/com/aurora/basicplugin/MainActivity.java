package com.aurora.basicplugin;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurora.auroralib.Constants;
import com.aurora.auroralib.ExtractedText;
import com.aurora.basicprocessor.basicpluginobject.BasicPluginObject;
import com.aurora.basicprocessor.facade.BasicProcessorCommunicator;

import java.io.IOException;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.textView);
        mTextView.setMovementMethod(new ScrollingMovementMethod());

        /*
         * Initialize the communicator
         */
        mBasicProcessorCommunicator = new BasicProcessorCommunicator(getApplicationContext());

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

        BasicPluginObject basicPluginObject;

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
                    basicPluginObject = (BasicPluginObject) mBasicProcessorCommunicator.pipeline(inputText);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                break;

            case Constants.PLUGIN_INPUT_TYPE_OBJECT:
                // Convert the read file to an PluginObject
                try {
                    basicPluginObject = BasicPluginObject.getPluginObjectFromFile(fileUri, this,
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
        if (basicPluginObject != null){
            String filename = basicPluginObject.getFileName();
            String result = basicPluginObject.getResult();
            mTextView.setText(filename + '\n' + result);

            if(!basicPluginObject.getImages().isEmpty()) {
                LinearLayout imageGallery = findViewById(R.id.imageGallery);
                for (Bitmap image : basicPluginObject.getImages()) {
                    imageGallery.addView(getImageView(image));
                }
            }
        }
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