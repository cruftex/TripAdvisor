package app.Trip;

import app.Model.IDirectionProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class GoogleDirectionProvider implements IDirectionProvider {

    //TODO : put in config File
    private static final String ApiEndPoint = "https://maps.googleapis.com/maps/api/directions/json";
    private static final String ApiKey = "AIzaSyCRnR0uSv0moUkSd6dK2OfH1tijkLaU8W0";

    private ObjectMapper mapper = new ObjectMapper();

    public MapDirections getDirections(String from, String to) {
        try {
            HttpResponse<String> directionResponse = Unirest.post(ApiEndPoint)
                    .header("accept", "application/json")
                    .queryString("key", ApiKey)
                    .queryString("origin", from)
                    .queryString("destination", to)
                    .asString();

            return mapper.readValue(directionResponse.getBody(), MapDirections.class);

        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
