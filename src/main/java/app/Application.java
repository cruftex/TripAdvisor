package app;

import app.Trip.TripAdvisor;
import app.Trip.TripRequest;
import app.utils.RestApiUtils;

import static java.net.HttpURLConnection.*;
import static spark.Spark.post;

public class Application {
    public static void main(String[] args) {

        TripAdvisor adviser = new TripAdvisor();

        post("/trip", (request, response) -> {
            try {
                TripRequest tripRequest = RestApiUtils.parseTripRequest(request);
                if (tripRequest == null) {
                    response.status(HTTP_BAD_REQUEST);
                    return "Incorrect trip request.";
                }
                return RestApiUtils.jsonPretify(adviser.getTripAdvice(tripRequest));

            } catch (Exception e) {
                response.status(HTTP_BAD_REQUEST);
                return e.toString();
            }
        });
    }

}
