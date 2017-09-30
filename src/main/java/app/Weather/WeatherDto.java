package app.Weather;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.eclipse.jetty.util.StringUtil;

@Data
public class WeatherDto {
    public WeatherDto() {}
    public WeatherDto(double temp , String desc) {
        celsiusTemp = temp;
        description = desc;
    }
    private double celsiusTemp;
    private String description;

    @JsonIgnore
    public boolean isValid() {
        return StringUtil.isNotBlank(description);
    }

}
