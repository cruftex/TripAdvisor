package app.Trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Step {
    public Measure distance ;
    public Measure duration ;
    public Location end_location;
    public String html_instructions;
}
