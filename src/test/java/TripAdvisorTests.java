package app;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import app.Trip.*;
import app.TripRules.TripAdvicePredicate;
import app.Weather.WeatherDto;
import app.Weather.WeatherProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;



class TripAdvisorTests {


    @Test
    @DisplayName("TripAdviceRuleRouteTooLong_Fail")
    public void TripAdviceRuleRouteTooLong_Fail() {
        TripAdviceDto trip = new TripAdviceDto("a","b");
        trip.setTotalDurationInMinutes(320);
        assertEquals("No",TripAdvicePredicate.isTripLegal(trip));
    }

    @Test
    @DisplayName("TripAdviceRuleRouteBelowMinTemp_Fail")
    public void TripAdviceRuleRouteBelowMinTemp_Fail() {
        TripAdviceDto trip = new TripAdviceDto("a","b");
        trip.setSteps(getStepWithWeather(15));
        trip.setTotalDurationInMinutes(100);
        assertEquals("No",TripAdvicePredicate.isTripLegal(trip));
    }

    @Test
    @DisplayName("TripAdviceRuleRouteBelowMinTemp_Ok")
    public void TripAdviceRuleRouteBelowMinTemp_Ok() {
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
    @DisplayName("TripAdvisorTest_OkAdvice")
    public void TripAdvisorTest_OkAdvice() {

        TripAdvisor advisor = mockTripAdvisor(10140,22);
        TripAdviceDto adviceDto = advisor.getTripAdvice("A","B");
        assertNotNull(adviceDto);
        assertEquals(adviceDto.getTravelAdvice(),"Yes");
    }

    @Test
    @DisplayName("TripAdvisorTest_OkAdvice")
    public void TripAdvisorTest_FailAdvice_TempratureHigh() {

        TripAdvisor advisor = mockTripAdvisor(10140,32);
        TripAdviceDto adviceDto = advisor.getTripAdvice("A","B");
        assertNotNull(adviceDto);
        assertEquals(adviceDto.getTravelAdvice(),"No");
    }

    @Test
    @DisplayName("TripAdvisorTest_OkAdvice")
    public void TripAdvisorTest_FailAdvice_DurationLong() {

        TripAdvisor advisor = mockTripAdvisor(1014000,25);
        TripAdviceDto adviceDto = advisor.getTripAdvice("A","B");
        assertNotNull(adviceDto);
        assertEquals(adviceDto.getTravelAdvice(),"No");
    }



    private TripAdvisor mockTripAdvisor(int duration,int temprature){
        WeatherProvider weatherMock = mock(WeatherProvider.class);
        when(weatherMock.getWeather(anyDouble(),anyDouble())).thenReturn(new WeatherDto(temprature,String.valueOf(temprature)));
        //Generate map direction
        Route r = new Route();
        Leg l = new Leg();
        Step step = new Step();
        step.end_location = new Location(1,1);
        l.duration = step.duration = new Measure(duration,"duration");
        l.distance = step.distance = new Measure(190,"190");
        l.steps = new Step[] {step};
        r.legs = new Leg[]{l};
        MapDirections mapDirections = new MapDirections();
        mapDirections.routes = new Route[] {r};
        GoogleDirectionProvider directorMock = mock(GoogleDirectionProvider.class);
        when(directorMock.getDirections(anyString(),anyString())).thenReturn(mapDirections);
        return  new TripAdvisor(weatherMock,directorMock);
    }


}
