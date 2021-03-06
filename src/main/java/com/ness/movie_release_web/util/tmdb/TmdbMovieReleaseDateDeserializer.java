package com.ness.movie_release_web.util.tmdb;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TmdbMovieReleaseDateDeserializer extends StdDeserializer<LocalDate> {

    private static final long serialVersionUID = 3254470969072186990L;

    public TmdbMovieReleaseDateDeserializer() {
        this(null);
    }

    protected TmdbMovieReleaseDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext context)
            throws IOException {

        JsonNode node = jp.getCodec().readTree(jp);
        String value = node.textValue();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            return LocalDate.parse(value, formatter);
        } catch (Exception e) {
            return LocalDate.of(1870, 1, 1);
        }
    }

    @Override
    public LocalDate getNullValue(DeserializationContext ctxt){
        return LocalDate.of(1870, 1, 1);
    }
}
