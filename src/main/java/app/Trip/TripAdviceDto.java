package app.Trip;

import app.Model.Validable;
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

    public TripAdviceDto(String origin, String destination){
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    @JsonIgnore
    public boolean isValid() {
        return totalDistanceInMeters >0 && StringUtil.isNotBlank(travelAdvice);
    }
}
