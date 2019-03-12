package com.aurora.basicplugin;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.aurora.auroralib.Constants;
import com.aurora.basicprocessor.BasicPluginObject;
import com.aurora.basicprocessor.ProcessorCommunicator;

public class MainActivity extends AppCompatActivity {
    //private static final ProcessorCommunicator mProcessorCommunicator = new ProcessorCommunicator();
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.getAction().equals(Constants.PLUGIN_ACTION)) {
            if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_TEXT)) {
                String inputText = intentThatStartedThisActivity.getStringExtra(Constants.PLUGIN_INPUT_TEXT);
                BasicPluginObject basicPluginObject = ProcessorCommunicator.delegate(inputText);
                String result = basicPluginObject.getResult();
                mTextView.setText(result);
            }
            // TODO handle a PluginObject that was cached
            else if (intentThatStartedThisActivity.hasExtra(Constants.PLUGIN_INPUT_OBJECT)){

            }
        }
    }
}