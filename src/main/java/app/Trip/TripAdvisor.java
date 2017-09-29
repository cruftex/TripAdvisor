package app.Trip;

import app.Weather.WeatherDto;
import app.Weather.WeatherProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;


public class TripAdvisor {


    private ObjectMapper mapper = new ObjectMapper();
    private WeatherProvider weatherProvider = new WeatherProvider();

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
        List<Callable<Integer>> weatherCallables = stepsDto.stream().map(step -> weatherInfoCallable(step)).collect(Collectors.toList());


        ExecutorService executor = Executors.newWorkStealingPool();
        try {
            int counter = invokeAllCallables(executor,weatherCallables);
            if(counter != stepsDto.size())
                System.out.println("Expected " + stepsDto.size()  + " , got only " + counter + " weather objects");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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


    private Integer invokeAllCallables(ExecutorService executorService, List<Callable<Integer>>  callables) throws InterruptedException {
        return  executorService.invokeAll(callables).stream()
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
            WeatherDto weather= weatherProvider.getWeather(step.end_location.lat, step.end_location.lng);
            return step.weather != null ? 1 :0;
        };
    }
}
