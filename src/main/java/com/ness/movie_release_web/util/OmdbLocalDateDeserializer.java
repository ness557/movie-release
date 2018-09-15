package com.ness.movie_release_web.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class OmdbLocalDateDeserializer extends StdDeserializer<LocalDate> {

    public OmdbLocalDateDeserializer() {
        this(null);
    }

    protected OmdbLocalDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext context)
            throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);
        String value = node.textValue();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(Locale.US);

        try {
            return LocalDate.parse(value,formatter);
        } catch (Exception e) {
            return LocalDate.of(1870, 1, 1);
        }
    }
}
