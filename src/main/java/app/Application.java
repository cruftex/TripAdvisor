package app;

import app.Controllers.TripController;
import app.Trip.TripAdvisor;
import app.utils.SimpleMetricReporter;


public class Application {

    public static void main(String[] args) {
        new TripController(new TripAdvisor(),new SimpleMetricReporter());
    }

}
