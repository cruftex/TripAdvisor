package app.TripRules;

import app.Trip.StepDto;
import app.Trip.TripAdviceDto;

import java.util.function.Predicate;

public class TripAdvicePredicate {
    private static Predicate<StepDto> isStepLegal() {
        return s ->
                s.weather.getCelsiusTemp() <= TripRulesConfig.MaxCellciusTemp //TODO CONFIG
                && s.weather.getCelsiusTemp() >= TripRulesConfig.MinCellciusTemp;
    }
}
