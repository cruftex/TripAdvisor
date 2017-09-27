package app.Trip;

import lombok.Data;

@Data
public class TripAdvice {
    private String origin;
    private String destination;
    private int totalDurationInMinutes ;
    private int totalDistanceInMeters;


    public TripAdvice(String origin, String destination,int duration , int distance) {
        this.origin = origin;
        this.destination = destination;
        totalDurationInMinutes = duration;
        totalDistanceInMeters = distance;
    }
}
