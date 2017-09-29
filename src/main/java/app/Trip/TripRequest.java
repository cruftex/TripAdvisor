package app.Trip;
        import app.utils.*;
        import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
        import lombok.Data;
        import org.eclipse.jetty.util.StringUtil;



@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TripRequest implements Validable{
    private String from ;
    private String to ;

    public boolean isValid() {
        return StringUtil.isNotBlank(from) && StringUtil.isNotBlank(to);
    }
}
