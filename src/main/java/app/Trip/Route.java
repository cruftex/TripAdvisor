package app.Trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Route {
    public Leg[] legs;

    public int distance() {
        return legs != null && legs.length > 0 ? legs[0].distance.value : 0;
    }

    public int duration() { return legs != null && legs.length > 0 ? legs[0].duration.value : 0; }

    public Step[] steps() { return legs != null && legs.length > 0 ? legs[0].steps : null; }
}




