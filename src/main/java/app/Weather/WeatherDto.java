package app.Weather;

import app.Model.Validable;
import lombok.Data;
import org.eclipse.jetty.util.StringUtil;

@Data
public class WeatherDto implements Validable {
    public WeatherDto() {}
    public WeatherDto(double temp , String desc) {
        celsiusTemp = temp;
        description = desc;
    }
    private double celsiusTemp;
    private String description;


    public boolean isValid() {
        return StringUtil.isNotBlank(description);
    }

}
