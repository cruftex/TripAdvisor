package app.Trip;

import app.Model.IDirectionProvider;
import app.Model.ITripAdvisor;
import app.Model.IWeatherProvider;
import app.TripRules.TripAdvicePredicate;
import app.Weather.WeatherConfig;
import app.Weather.WeatherDto;
import app.Weather.WeatherProvider;
import org.apache.log4j.Logger;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.integration.CacheLoader;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.*;


public class TripAdvisor implements ITripAdvisor {

    final static Logger logger = Logger.getLogger(TripAdvisor.class);

    private IWeatherProvider weatherProvider;
    private IDirectionProvider directionProvider;
    private Cache<String, MapDirections> directionsCache  = initCache();


    public TripAdvisor() {
        this(new WeatherProvider(), new GoogleDirectionProvider());
    }

    public TripAdvisor(IWeatherProvider weatherProvider, IDirectionProvider directionProvider) {
        this.weatherProvider = weatherProvider;
        this.directionProvider = directionProvider;
        directionsCache = initCache();
    }

    ///The Tripe Advice main function
    public TripAdviceDto getTripAdvice(String from, String to) {
        try {

            //build advice response
            TripAdviceDto tripAdvice = new TripAdviceDto(from, to);
            //1. get directions
            String key = from + ":" + to;
            MapDirections directions = directionsCache.get(key);

            if (directions == null || directions.routes.length == 0)
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
            logger.error("Could not getTrip Advice. Reason " + e.getStackTrace());
            return null;
        }
    }

    private  Cache<String,MapDirections> initCache() {
        return new Cache2kBuilder<String, MapDirections>() {}
                .expireAfterWrite(5, TimeUnit.MINUTES)    // expire/refresh after 5 minutes
                .resilienceDuration(30, TimeUnit.SECONDS) // cope with at most 30 seconds
                .entryCapacity(1000) //store last 1000 items
                .loader(new CacheLoader<String, MapDirections>() {
                    @Override
                    public MapDirections load(String s) throws Exception {
                        String[] args = s.split(":");
                        String from = args[0];
                        String to = args[1];
                        return directionProvider.getDirections(from, to);
                    }
                })
                .build();
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
            logger.error("Could not fetchWeatherInfo. Reason " + e.getStackTrace());
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
