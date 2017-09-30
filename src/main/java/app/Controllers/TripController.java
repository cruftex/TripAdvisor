package app.Controllers;

import app.Model.IMetricProvider;
import app.Trip.TripAdviceDto;
import app.Trip.TripAdvisor;
import app.Trip.TripRequest;
import app.utils.SimpleMetricReporter;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import spark.Request;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import static app.utils.JsonUtils.json;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.NANOS;
import static spark.Spark.after;
import static spark.Spark.get;

public class TripController {

    final static Logger logger = Logger.getLogger(TripController.class);
    TripAdvisor adviser = new TripAdvisor();
    IMetricProvider metricProvider = new SimpleMetricReporter();

    public TripController() {

        get("/trip", (request, response) -> {
            try {
                //measure time of handling
                Instant start = Instant.now();
                TripRequest tripRequest = parseTripRequest(request);
                if (tripRequest == null) {
                    response.status(HTTP_BAD_REQUEST); //user incorrect request
                    return "Incorrect trip request.";
                }
                TripAdviceDto adviceDto =  adviser.getTripAdvice(tripRequest.getFrom(), tripRequest.getTo());
                metricProvider.ReportTiming("com.appname.servername.trip.requestTime", Duration.between(start, Instant.now()).getNano()/1000000);
                if(adviceDto.isValid())
                    return adviceDto;
                return "Could not Fetch Trip Advice , no routes found";

            } catch (Exception e) {
                response.status(HTTP_INTERNAL_ERROR); //if we got here , its our fault.
                logger.error("Could not handle trip request. Reason:" + e.getMessage());
                return e.toString();
            }

        },json());

        after((req, res) -> {
            res.type("application/json");
        });
    }

    private TripRequest parseTripRequest(Request request) {
        TripRequest trip;
        //method 1 : parse from url params
        try {

            trip = new TripRequest(request.queryParams("from"), request.queryParams("to"));
            return trip.isValid() ? trip : null;
        } catch (Exception e) { return null;}

    }


}
