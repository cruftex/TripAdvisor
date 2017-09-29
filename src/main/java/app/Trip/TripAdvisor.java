package app.Trip;

import app.Model.IDirectionProvider;
import app.Model.ITripAdvisor;
import app.Model.IWeatherProvider;
import app.TripRules.TripAdvicePredicate;
import app.Weather.WeatherProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.*;


public class TripAdvisor implements ITripAdvisor {

    private ObjectMapper mapper = new ObjectMapper();
    private IWeatherProvider weatherProvider;
    private IDirectionProvider directionProvider;

    public TripAdvisor() {
        this(new WeatherProvider(), new GoogleDirectionProvider());
    }

    public TripAdvisor(IWeatherProvider weatherProvider, IDirectionProvider directionProvider) {
        this.weatherProvider = weatherProvider;
        this.directionProvider = directionProvider;
    }

    ///The Tripe Advice main function
    public TripAdviceDto getTripAdvice(String from, String to) {
        try {

            //build advice response
            TripAdviceDto tripAdvice = new TripAdviceDto(from, to);
            //1. get directions
            MapDirections directions = directionProvider.getDirections(from, to);
            if (directions == null)
                return tripAdvice;
            //2. Weather -> (Parallel)
            List<StepDto> stepsDto = fetchWeatherInfo(directions);

            //3. Project to tripAdviceDto and calc recommendation
            tripAdvice.setSteps(stepsDto);
            tripAdvice.setTotalDistanceInMeters(directions.distance());
            tripAdvice.setTotalDurationInMinutes(directions.duration());
            tripAdvice.setTravelAdvice(TripAdvicePredicate.isTripLegal(tripAdvice));
            return tripAdvice;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private List<StepDto> fetchWeatherInfo(MapDirections directions) {
        List<StepDto> stepsDto = Arrays.stream(directions.steps()).map(step -> StepDto.fromStep(step)).collect(Collectors.toList());
        List<Callable<Integer>> weatherCallables = stepsDto.stream().map(step -> weatherInfoCallable(step)).collect(Collectors.toList());

        ExecutorService executor = Executors.newWorkStealingPool();
        try {
            executor.invokeAll(weatherCallables).stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (Exception e) {
                            return 0;
                        }
                    }).collect(Collectors.summingInt(Integer::intValue));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return stepsDto;
    }


    private Callable<Integer> weatherInfoCallable(StepDto step) {
        return () -> {
            step.weather = weatherProvider.getWeather(step.end_location.lat, step.end_location.lng);
            return step.weather != null ? 1 : 0;
        };
    }
}
