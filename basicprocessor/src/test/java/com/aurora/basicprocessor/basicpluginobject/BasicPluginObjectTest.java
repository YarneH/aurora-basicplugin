package com.aurora.basicprocessor.basicpluginobject;

import org.junit.Test;

public class BasicPluginObjectTest {

    @Test
    public void BasicPluginObject_Gson(){
        // Arrange
        // Create a BasicPluginObject
        BasicPluginObject originalBasicPluginObject = new BasicPluginObject();
        originalBasicPluginObject.setResult("Test");

        // Act
        // Json the originalBasicPluginObject to prepare it to be sent to Aurora for caching
        String jsonPluginObject = originalBasicPluginObject.toJSON();
        // de-JSON the JSON string that was sent to Aurora (and would be received back when opening a cached file)
        BasicPluginObject extractedBasicPluginObject = BasicPluginObject.fromJson(jsonPluginObject, BasicPluginObject.class);

        // Assert
        // Assert that JSONing and de-JSONing the BasicPluginObject does not alter it
        assert (extractedBasicPluginObject.getResult().equals(originalBasicPluginObject.getResult()));
    }

}
