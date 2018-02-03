package com.example.hello.maps1.entities.adapters;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Created by Ivan on 10.12.2017.
 */

public class BooleanAdapter {
    public static class Deserializer extends JsonDeserializer<Boolean> {
        @Override
        public Boolean deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            Integer result = jp.getIntValue();

            return result != null && result > 0 ? true : false;
        }
    }

    public static class Serializer extends JsonSerializer<Boolean> {
        @Override
        public void serialize(Boolean value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (value != null)
                jgen.writeNumber(value == true ? 1 : 0);
        }
    }
}
