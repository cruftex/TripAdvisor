package app.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.ResponseTransformer;

public class JsonUtils {

    public static ResponseTransformer json() {
        return JsonUtils::toJson;
    }
    //toJson helper 12
    public static String toJson(Object obj) throws JsonProcessingException {
        ObjectMapper jacksonObjectMapper = new ObjectMapper();
        return jacksonObjectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(obj);

    }
}
