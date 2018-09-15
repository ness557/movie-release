package com.ness.movie_release_web.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class OmdbMetascoreRatingDeserializer extends StdDeserializer<Integer> {

    public OmdbMetascoreRatingDeserializer() {
        this(null);
    }

    protected OmdbMetascoreRatingDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Integer deserialize(JsonParser jp, DeserializationContext context)
            throws IOException, JsonProcessingException {

        JsonNode node = jp.getCodec().readTree(jp);
        String value = node.textValue();

        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
