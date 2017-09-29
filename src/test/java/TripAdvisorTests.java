package app;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.Trip.StepDto;
import app.Trip.TripAdviceDto;
import app.Trip.TripAdvisor;
import app.TripRules.TripAdvicePredicate;
import app.Weather.WeatherDto;
import app.Weather.WeatherProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;



class TripAdvisorTests {

    @Test
    @DisplayName("My 1st JUnit 5 test! 😎")
    public void myFirstTest() {
        assertEquals(2, 1 + 1);
    }

    @Test
    @DisplayName("My 2nd JUnit 5 test! 😎")
    public void TripAdviceTestRouteTooLong_Fail() {
        TripAdviceDto trip = new TripAdviceDto("a","b");
        trip.setTotalDurationInMinutes(320);
        assertEquals("No",TripAdvicePredicate.isTripLegal(trip));
    }

    @Test
    @DisplayName("TripAdviceTestRouteBelowMinTemp_Fail")
    public void TripAdviceTestRouteBelowMinTemp_Fail() {
        TripAdviceDto trip = new TripAdviceDto("a","b");
        trip.setSteps(getStepWithWeather(15));
        trip.setTotalDurationInMinutes(100);
        assertEquals("No",TripAdvicePredicate.isTripLegal(trip));
    }

    @Test
    @DisplayName("TripAdviceTestRouteBelowMinTemp_Fail")
    public void TripAdviceTestRouteBelowMinTemp_Ok() {
        TripAdviceDto trip = new TripAdviceDto("a","b");
        trip.setSteps(getStepWithWeather(21));
        trip.setTotalDurationInMinutes(100);
        assertEquals("Yes",TripAdvicePredicate.isTripLegal(trip));
    }


    private List<StepDto> getStepWithWeather(double temprature){
        List<StepDto> steps = new ArrayList<StepDto>();
        StepDto step = new StepDto();
        step.weather = new WeatherDto(temprature,"");
        steps.add(step);
        return  steps;
    }

    @Test
    @DisplayName("TripAdviceTestRouteBelowMinTemp_Fail")
    public void GetWeatherApiCall_Ok() {
        WeatherProvider provider = new WeatherProvider();
        WeatherDto dto = provider.getWeather(12,12);
        assertNotNull(dto);

    }

    @Test
    @DisplayName("TripAdviceTestRouteBelowMinTemp_Fail")
    public void TripAdvisorTest() {

        WeatherProvider weatherMock = mock(WeatherProvider.class);
        when(weatherMock.getWeather(anyDouble(),anyDouble())).thenReturn(new WeatherDto(22,""));
        WeatherDto dto = weatherMock.getWeather(13,13);



    }
}
