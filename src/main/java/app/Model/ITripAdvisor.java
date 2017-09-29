package app.Model;

import app.Trip.TripAdviceDto;
import app.Trip.TripRequest;

public interface ITripAdvisor {
    public TripAdviceDto getTripAdvice(TripRequest request);
}

