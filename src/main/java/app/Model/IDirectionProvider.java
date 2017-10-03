package app.Model;

import app.Trip.MapDirections;
import app.Trip.TripRequest;

public interface IDirectionProvider {
    MapDirections getDirections(TripRequest request);

}
