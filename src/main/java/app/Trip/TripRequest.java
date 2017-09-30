package app.Trip;
        import app.Model.Validable;
        import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
        import lombok.Data;
        import org.eclipse.jetty.util.StringUtil;



@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TripRequest implements Validable {
    private String from ;
    private String to ;

    public TripRequest(){}
    public TripRequest(String from,String to){
        this.from = from;
        this.to = to;
    }
    public String toString(){
        return "[" + from + "-" + to + "]";
    }
    public boolean isValid() {
        return StringUtil.isNotBlank(from) && StringUtil.isNotBlank(to);
    }
}
