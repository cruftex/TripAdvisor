package app.Trip;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class TripAdvisor {


    private ObjectMapper mapper = new ObjectMapper();

    ///The Tripe Advice main function
    public TripAdvice getTripAdvice(TripRequest request) {

        //build advice response
        TripAdvice tripAdvice = new TripAdvice(request.getFrom(),request.getTo());
        //1. get directions
        TripDirections directions = getDirections(request);
        if(directions == null)
            return tripAdvice;

        //2. Weather -> Parallel
        List<StepDto> stepsDto = Arrays.stream(directions.steps()).map(step-> StepDto.fromStep(step)).collect(Collectors.toList());
        tripAdvice.setSteps(stepsDto);
        return tripAdvice;

        //3. Calc Recommendation

        //return tripAdvice;
    }

    private TripDirections getDirections(TripRequest request) {
        try {
            HttpResponse<String> directionResponse = Unirest.post(GoogleApiConfig.ApiEndPoint)
                    .header("accept", "application/json")
                    .queryString("key", GoogleApiConfig.ApiKey)
                    .queryString("origin", request.getFrom())
                    .queryString("destination", request.getTo())
                    .asString();

            return mapper.readValue(directionResponse.getBody(), TripDirections.class);

        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
