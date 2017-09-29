package app.Trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
    public double lat;
    public double lng;

    public Location() {}
    public Location(double lat , double lng){
        this.lat = lat;
        this.lng = lng;
    }
}
