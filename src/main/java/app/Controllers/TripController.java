package app.utils;

import app.Application;
import app.Trip.TripAdvisor;
import app.Trip.TripRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.StringUtil;
import spark.Request;

import java.io.IOException;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static spark.Spark.get;

public class TripController {

    final static Logger logger = Logger.getLogger(TripController.class);
    TripAdvisor adviser = new TripAdvisor();

    public TripController() {

        get("/trip", (request, response) -> {
            try {
                TripRequest tripRequest = parseTripRequest(request);
                if (tripRequest == null) {
                    response.status(HTTP_BAD_REQUEST); //user incorrect request
                    return "Incorrect trip request.";
                }
                return adviser.getTripAdvice(tripRequest.getFrom(), tripRequest.getTo());

            } catch (Exception e) {
                response.status(HTTP_INTERNAL_ERROR); //if we got here , its our fault.
                logger.error("Could not handle trip request. Reason:" + e.getMessage());
                return e.toString();
            }
        });
    }

    private TripRequest parseTripRequest(Request request) {
        TripRequest trip;
        //method 1 : parse from url params
        try {

            trip = new TripRequest(request.queryParams("from"), request.queryParams("to"));
            if (trip.isValid())
                return trip;
        } catch (Exception e) { }

        //method 2 : body parsing
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


}
