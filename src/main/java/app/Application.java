package app;

import app.Trip.TripAdvisor;
import app.Trip.TripRequest;
import app.Weather.WeatherData;
import app.Weather.WeatherProvider;
import app.utils.RestApiUtils;
import org.apache.log4j.*;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import app.Trip.*;

import java.util.concurrent.TimeUnit;

import static java.net.HttpURLConnection.*;
import static spark.Spark.post;

public class Application {
    final static Logger logger = Logger.getLogger(Application.class);

    public static void main(String[] args) {

        CacheTest();

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

    private static void CacheTest() {
        try {
            WeatherProvider wp = new WeatherProvider();
            System.out.println("wd1");
            WeatherData wd1 = wp.getWeather(12.7789, -71.992);
            System.out.println("wd2");
            WeatherData wd2 = wp.getWeather(12.7789, -71.992);
            System.out.println("wd3");
            WeatherData wd3 = wp.getWeather(12.7789, 71.997);
            System.out.println("wd4");
            WeatherData wd4 = wp.getWeather(12.7789, 71.997);
            Thread.sleep(6000);
            System.out.println("wd5");
            WeatherData wd5 = wp.getWeather(12.7789, -71.992);
            System.out.println("wd6");
            WeatherData wd6 = wp.getWeather(12.7789, 71.997);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
