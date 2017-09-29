package app.Trip;

import lombok.Data;

import java.util.List;

@Data
public class TripAdvice {
    private String origin;
    private String destination;
    private int totalDurationInMinutes ;
    private int totalDistanceInMeters;
    private String travelAdvice;

    private List<StepDto> steps;

    public TripAdvice(String origin, String destination){
        this.origin = origin;
        this.destination = destination;
    }

}
