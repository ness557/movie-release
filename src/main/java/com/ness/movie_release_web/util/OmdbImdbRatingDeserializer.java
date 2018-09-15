package com.ness.movie_release_web.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class OmdbImdbRatingDeserializer extends StdDeserializer<Float> {

    public OmdbImdbRatingDeserializer() {
        this(null);
    }

    protected OmdbImdbRatingDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Float deserialize(JsonParser jp, DeserializationContext context)
            throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);
        String value = node.textValue();

        try {
            return Float.valueOf(value);
        } catch (Exception e) {
            return 0f;
        }
    }
}
