package app.Trip;

import app.utils.GoogleApiConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class TripAdvisor {


    private ObjectMapper mapper = new ObjectMapper();

    public TripAdvice getTripAdvice(TripRequest request) {
        Directions directions = getDirections(request);
        if (directions == null)
            System.out.println("No Directions found");
        return directions == null ? null :
                new TripAdvice(
                        request.getFrom(),
                        request.getTo(),
                        directions.defaultRouteDuration(),
                        directions.defaultRouteDistance());
    }

    private Directions getDirections(TripRequest request) {
        try {
            HttpResponse<String> directionResponse = Unirest.post(GoogleApiConfig.ApiEndPoint)
                    .header("accept", "application/json")
                    .queryString("key", GoogleApiConfig.GoogleApiKey)
                    .queryString("origin", request.getFrom())
                    .queryString("destination", request.getTo())
                    .asString();

            return mapper.readValue(directionResponse.getBody(), Directions.class);

        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }
}
