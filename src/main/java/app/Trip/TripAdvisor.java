package app.Trip;

import app.Model.IDirectionProvider;
import app.Model.ITripAdvisor;
import app.Model.IWeatherProvider;
import app.TripRules.TripAdvicePredicate;
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
    private Cache<TripRequest, TripAdviceDto> tripCache = initCache();


    public TripAdvisor() {
        this(new WeatherProvider(), new GoogleDirectionProvider());
    }

    public TripAdvisor(IWeatherProvider weatherProvider, IDirectionProvider directionProvider) {
        this.weatherProvider = weatherProvider;
        this.directionProvider = directionProvider;
        tripCache = initCache();
    }

    ///The Tripe Advice main function
    public TripAdviceDto getTripAdvice(TripRequest request) {
        return tripCache.get(request);
    }

    //create a full trip advice calculation
    private TripAdviceDto calcTripAdvice(TripRequest request){
        try {
            TripAdviceDto tripAdvice = new TripAdviceDto(request);
            //step 1 : get the map directions
            MapDirections directions = directionProvider.getDirections(request);
            if (directions == null || directions.routes.length == 0)
                return tripAdvice;
            //step 2 : fill weather info
            //2. Weather -> (Parallel)
            List<StepDto> stepsDto = fetchWeatherInfo(directions);

            //3. Project to tripAdviceDto and calc recommendation
            tripAdvice.setTripData(stepsDto,directions.duration(),directions.distance());
            return tripAdvice;

        } catch (Exception e) {
            logger.error("Could not getTrip Advice. Reason " + e.getStackTrace());
            return null;
        }
    }

    private  Cache<TripRequest,TripAdviceDto> initCache() {
        return new Cache2kBuilder<TripRequest, TripAdviceDto>() {}
                .expireAfterWrite(5, TimeUnit.MINUTES)    // expire/refresh after 5 minutes
                .resilienceDuration(30, TimeUnit.SECONDS) // cope with at most 30 seconds
                .entryCapacity(1000) //store last 1000 items
                .loader(new CacheLoader<TripRequest, TripAdviceDto>() {
                    @Override
                    public TripAdviceDto load(TripRequest request) throws Exception {
                        return calcTripAdvice(request);
                    }
                })
                .build();
    }

    private List<StepDto> fetchWeatherInfo(MapDirections directions) {
        List<StepDto> stepsDto = Arrays.stream(directions.steps()).map(step -> StepDto.fromStep(step)).collect(Collectors.toList());
        List<Callable<Integer>> weatherCallables = stepsDto.stream().map(step -> weatherInfoCallable(step)).collect(Collectors.toList());

        ExecutorService executor = Executors.newWorkStealingPool();
        try {
            Integer collect = executor.invokeAll(weatherCallables).stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (Exception e) {
                            return 0;
                        }
                    }).collect(Collectors.summingInt(Integer::intValue));
            if(collect.intValue() != weatherCallables.size()) {
                logger.warn("Not all weather info was gatherethed. expecting : " + weatherCallables.size() + " got :" + collect.intValue());
            }
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
