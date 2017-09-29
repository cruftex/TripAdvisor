package app.Model;

import app.Trip.MapDirections;

public interface IDirectionProvider {
    MapDirections getDirections(String from,String to);

}
