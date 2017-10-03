package app.Trip;

import app.Model.Validable;
import app.TripRules.TripAdvicePredicate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.eclipse.jetty.util.StringUtil;

import java.util.List;

@Data
public class TripAdviceDto implements Validable {
    private String origin;
    private String destination;
    private int totalDurationInMinutes ;
    private int totalDistanceInMeters;
    private String travelAdvice;

    private List<StepDto> steps;

    public TripAdviceDto(TripRequest request){
        this.origin = request.getFrom();
        this.destination = request.getTo();
    }

    public void setTripData(List<StepDto> steps,int totalDuration,int totalDistance){
        this.steps = steps;
        this.totalDistanceInMeters = totalDistance;
        this.totalDurationInMinutes = totalDuration;
        travelAdvice = TripAdvicePredicate.isTripLegal(this);
    }

    @Override
    @JsonIgnore
    public boolean isValid() {
        return totalDistanceInMeters >0 && StringUtil.isNotBlank(travelAdvice);
    }
}
