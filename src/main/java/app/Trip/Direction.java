package app.Trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
class Measure {
    public String text ;
    public int value;
}
@JsonIgnoreProperties(ignoreUnknown = true)
class Location {
    public double lat;
    public double lng;
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Step {
    public Measure distance ;
    public Measure duration ;
    public Location end_location;
    public Location start_location;
    public String html_instructions;
}
@JsonIgnoreProperties(ignoreUnknown = true)
class Leg {
    public Measure distance;
    public Measure duration ;
    public Step[] steps;
}
@JsonIgnoreProperties(ignoreUnknown = true)
class Route {
    public Leg[] legs;

    public int defaultLegDistance(){
       return legs != null && legs.length > 0 ? legs[0].distance.value : 0;
    }
    public int defaultLegDuration(){
        return legs != null && legs.length > 0 ? legs[0].duration.value : 0;
    }
}

