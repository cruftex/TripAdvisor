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

    public List<StepDto> steps;

    public void setSteps(List<StepDto> st){
        steps = st;
    }

    public void setTotalDurationInMinutes(int durationInMinutes) {
        this.totalDurationInMinutes = durationInMinutes;
    }

    public void setTotalDistanceInMeters(int distance) {
        totalDistanceInMeters = distance;
    }

    public TripAdviceDto(String from, String to){
        this.origin  = from;
        this.destination = to;
    }

    public TripAdviceDto(TripRequest request){
        this.origin  = request.getFrom();
        this.destination = request.getTo();
    }

    public void setTripData(List<StepDto> steps,int totalDuration,int totalDistance){
        this.steps = steps;
        this.totalDistanceInMeters = totalDistance;
        this.totalDurationInMinutes = totalDuration;
        travelAdvice = TripAdvicePredicate.isTripLegal(this);
    }

    public String getTravelAdvice() {
        return travelAdvice;
    }

    @Override
    @JsonIgnore
    public boolean isValid() {
        return totalDistanceInMeters >0 && StringUtil.isNotBlank(travelAdvice);
    }
}
