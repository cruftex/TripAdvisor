package app.Trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TripDirections {
    public Route[] routes;


    public int distance() {
        return (routes != null && routes.length > 0) ? routes[0].distance() : 0;
    }
    public int duration() {
        return (routes != null && routes.length > 0) ? routes[0].duration() : 0;
    }

    public Step[] steps() {
        return routes != null && routes.length > 0 ?routes[0].steps():  null;
    }

}
