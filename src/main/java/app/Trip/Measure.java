package app.Trip;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Measure {
    public String text ;
    public int value;

    public Measure(){}
    public Measure(int val, String txt){
        value = val;
        text = txt;
    }
}
