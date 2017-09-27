package app.utils;

import app.Trip.TripRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import spark.Request;

import java.io.IOException;

public class RestApiUtils {

    public static TripRequest parseTripRequest(Request request) {
        TripRequest trip;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            trip = mapper.readValue(request.body(), TripRequest.class);
            return trip.isValid() ? trip : null;

        } catch (JsonParseException jpe) {
            return null;
        } catch (IOException ex) {
            return null;
        }

    }

    public static String jsonPretify(Object obj) throws JsonProcessingException {
        ObjectMapper jacksonObjectMapper = new ObjectMapper();
        return jacksonObjectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(obj);
    }
}
