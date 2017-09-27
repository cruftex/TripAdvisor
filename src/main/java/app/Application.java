package app;

import app.Trip.TripAdvisor;
import app.Trip.TripRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.*;
import spark.Request;
import java.io.IOException;

import static spark.Spark.post;

public class Application {
    public static void main(String[] args) {

        TripAdvisor adviser = new TripAdvisor();
        ObjectMapper jacksonObjectMapper = new ObjectMapper();

        post("/trip", (request, response) -> {
            TripRequest tripRequest = getTripRequest(request);
            if (tripRequest != null) {
                return jacksonObjectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(adviser.getTripAdvice(tripRequest));
            }
            return "No trip Advice could be created";
        });


    }


    public static TripRequest getTripRequest(Request request) {
        TripRequest trip = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            trip = mapper.readValue(request.body(), TripRequest.class);
            if (!trip.isValid()) {
                return null;
            }
        } catch (JsonParseException jpe) {
            return null;
        } catch (IOException ex) {
            return null;
        }

        return trip;
    }
}
