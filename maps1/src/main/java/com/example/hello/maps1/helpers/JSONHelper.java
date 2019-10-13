package com.example.hello.maps1.helpers;

import android.util.Log;

import com.example.hello.maps1.entities.Order;
import com.example.hello.maps1.entities.responses.Infos;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by Ivan on 17.12.2017.
 */

public class JSONHelper {
    public static final String TAG = JSONHelper.class.getName();

    public static String getJsonFromObject(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            //objectMapper.configure(SerializationFeature. .FAIL_ON_UNKNOWN_PROPERTIES, false);

            StringWriter stringObj = new StringWriter();
            objectMapper.writeValue(stringObj, object);

            return stringObj.toString();
        } catch (IOException e) {
            Log.d(TAG, String.format("Object %s converting to json failed", object.getClass()));
            //e.printStackTrace();
            return "";
        }
    }

    public static Object getObjectFromJson(String response, Class classType) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Object objectDTO = objectMapper.readValue(response, classType);

            return objectDTO;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private void mapDtoFromServer(String responseInfo) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            TypeReference<Order[]> typeReference = new TypeReference<Order[]>() {};
            //List<Order> orders = objectMapper.readValue(responseInfo, typeReference);

            Infos infos = objectMapper.readValue(responseInfo, Infos.class);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
