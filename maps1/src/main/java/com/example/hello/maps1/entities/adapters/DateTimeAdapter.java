package com.example.hello.maps1.entities.adapters;

import com.example.hello.maps1.helpers.data_types.StringHelper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;

/**
 * Created by Ivan on 10.12.2017.
 */

public class DateTimeAdapter {
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static class Deserializer extends JsonDeserializer<DateTime> {
        @Override
        public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            String s = jp.getText();
            if (StringHelper.isEmpty(s))
                return null;

            return DATETIME_FORMATTER.parseDateTime(s);
        }
    }

    public static class Serializer extends JsonSerializer<DateTime> {
        @Override
        public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            if (value != null)
                jgen.writeString(DATETIME_FORMATTER.print(value));
        }
    }
}
