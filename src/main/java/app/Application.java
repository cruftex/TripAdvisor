package app;

import app.Trip.TripAdvisor;
import app.Trip.TripRequest;
import app.utils.RestApiUtils;
import org.apache.log4j.*;

import static java.net.HttpURLConnection.*;
import static spark.Spark.post;

public class Application {
    final static Logger logger = Logger.getLogger(Application.class);

    public static void main(String[] args) {

        CacheTest();
        TripAdvisor adviser = new TripAdvisor();

        //Trip Route
        post("/trip", (request, response) -> {
            try {
                TripRequest tripRequest = RestApiUtils.parseTripRequest(request);
                if (tripRequest == null) {
                    response.status(HTTP_BAD_REQUEST); //user incorrect request
                    return "Incorrect trip request.";
                }
                return RestApiUtils.jsonPretify(adviser.getTripAdvice(tripRequest));

            } catch (Exception e) {
                response.status(HTTP_INTERNAL_ERROR); //if we got here , its our fault.
                return e.toString();
            }
        });
    }

    private static void CacheTest() {
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
