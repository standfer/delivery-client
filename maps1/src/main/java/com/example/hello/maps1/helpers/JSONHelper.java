package com.example.hello.maps1.helpers;

import android.util.Log;

import com.example.hello.maps1.entities.responses.OrdersResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by Ivan on 17.12.2017.
 */

public class JSONHelper {
    public static String getJsonFromObject(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            //objectMapper.configure(SerializationFeature. .FAIL_ON_UNKNOWN_PROPERTIES, false);

            StringWriter stringObj = new StringWriter();
            objectMapper.writeValue(stringObj, object);

            return stringObj.toString();
        } catch (IOException e) {
            Log.d("ConvertObjectToJson", String.format("Object %s converting to json failed", object.getClass()));
            //e.printStackTrace();
            return "";
        }
    }

    /*public Object getObjectFromJson(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        Object objectDTO = objectMapper.readValue(response, objectDTO.class);

        return null;
    }*/
}
