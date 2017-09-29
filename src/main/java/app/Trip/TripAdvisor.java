package app.Trip;

import app.Model.ITripAdvisor;
import app.TripRules.TripAdvicePredicate;
import app.Weather.WeatherProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.*;


public class TripAdvisor implements ITripAdvisor {


    private ObjectMapper mapper = new ObjectMapper();
    private WeatherProvider weatherProvider = new WeatherProvider();

    ///The Tripe Advice main function
    public TripAdviceDto getTripAdvice(TripRequest request) {

        //build advice response
        TripAdviceDto tripAdvice = new TripAdviceDto(request.getFrom(), request.getTo());
        //1. get directions
        MapDirections directions = getDirections(request);
        if (directions == null)
            return tripAdvice;

        //2. Weather -> (Parallel)
        List<StepDto> stepsDto = fillStepsWithWeather(directions);
        //3. Project to tripAdviceDto and calc recommendation
        tripAdvice.setSteps(stepsDto);
        tripAdvice.setTotalDistanceInMeters(directions.distance());
        tripAdvice.setTotalDurationInMinutes(directions.duration());
        tripAdvice.setTravelAdvice(TripAdvicePredicate.isTripLegal(tripAdvice));

        return tripAdvice;

        //3. Calc Recommendation

        //return tripAdvice;
    }


    private MapDirections getDirections(TripRequest request) {
        try {
            HttpResponse<String> directionResponse = Unirest.post(GoogleApiConfig.ApiEndPoint)
                    .header("accept", "application/json")
                    .queryString("key", GoogleApiConfig.ApiKey)
                    .queryString("origin", request.getFrom())
                    .queryString("destination", request.getTo())
                    .asString();

            return mapper.readValue(directionResponse.getBody(), MapDirections.class);

        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<StepDto> fillStepsWithWeather(MapDirections directions) {
        List<StepDto> stepsDto = Arrays.stream(directions.steps()).map(step -> StepDto.fromStep(step)).collect(Collectors.toList());
        List<Callable<Integer>> weatherCallables = stepsDto.stream().map(step -> weatherInfoCallable(step)).collect(Collectors.toList());

        ExecutorService executor = Executors.newWorkStealingPool();
        try {
            invokeAllCallables(executor, weatherCallables);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return stepsDto;
    }

    private Integer invokeAllCallables(ExecutorService executorService, List<Callable<Integer>> callables) throws InterruptedException {
        return executorService.invokeAll(callables).stream()
                .map(f -> {
                    try {
                        return f.get();
                    } catch (Exception e) {
                        return 0;
                    }
                }).collect(Collectors.summingInt(Integer::intValue));
    }

    private Callable<Integer> weatherInfoCallable(StepDto step) {
        return () -> {
            step.weather = weatherProvider.getWeather(step.end_location.lat, step.end_location.lng);
            return step.weather != null ? 1 : 0;
        };
    }
}
