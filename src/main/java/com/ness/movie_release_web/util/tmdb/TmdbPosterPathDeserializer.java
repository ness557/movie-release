package com.ness.movie_release_web.util.tmdb;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class TmdbPosterPathDeserializer extends StdDeserializer<String> {

    private static final long serialVersionUID = -7857518805513504955L;

    public TmdbPosterPathDeserializer() {
        this(null);
    }

    protected TmdbPosterPathDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public String deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);
        String value = node.textValue();

        return "https://image.tmdb.org/t/p/original" + value;
    }
}
