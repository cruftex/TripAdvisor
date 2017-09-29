package app.Trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Directions {
    public Route[] routes;
    public String status ;

    public int defaultRouteDistance() {
        return (routes != null && routes.length > 0) ? routes[0].defaultLegDistance() : 0;
    }
    public int defaultRouteDuration() {
        return (routes != null && routes.length > 0) ? routes[0].defaultLegDuration() : 0;
    }
}
