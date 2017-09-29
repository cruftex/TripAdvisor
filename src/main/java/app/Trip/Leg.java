package app.Trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Leg {
    public Measure distance;
    public Measure duration ;
    public Step[] steps;
}
