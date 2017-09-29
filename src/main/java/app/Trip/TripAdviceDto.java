package app.Trip;

import lombok.Data;

import java.util.List;

@Data
public class TripAdviceDto {
    private String origin;
    private String destination;
    private int totalDurationInMinutes ;
    private int totalDistanceInMeters;
    private String travelAdvice;

    private List<StepDto> steps;

    public TripAdviceDto(String origin, String destination){
        this.origin = origin;
        this.destination = destination;
    }

}
