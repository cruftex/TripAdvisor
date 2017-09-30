package app.TripRules;

import app.Trip.StepDto;
import app.Trip.TripAdviceDto;

import java.util.function.Predicate;

public class TripAdvicePredicate {
    //TODO : put those values in Config
    public static final int MinCellciusTemp = 20;
    public static final int MaxCellciusTemp = 30;
    public static final int MaxTravelTimeInMinutes = 180;

    public  static Predicate<StepDto> isIllegalStep() {
        return s ->
                s.weather == null // in case we dont have weather info, move on
                || s.weather.getCelsiusTemp() > MaxCellciusTemp
                || s.weather.getCelsiusTemp() < MinCellciusTemp;
    }

    public static String isTripLegal (TripAdviceDto trip) {
        return  (trip.getTotalDurationInMinutes() <= MaxTravelTimeInMinutes
                && !trip.getSteps().stream().filter(isIllegalStep()).findFirst().isPresent()) ? "Yes" : "No";
        }
    }

