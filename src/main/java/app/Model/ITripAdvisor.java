package app.Model;

import app.Trip.TripAdviceDto;
import app.Trip.TripRequest;

public interface ITripAdvisor {
    TripAdviceDto getTripAdvice(TripRequest request);
}

