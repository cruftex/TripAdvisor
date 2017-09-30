package app.utils;

import app.Model.IMetricProvider;
import org.apache.log4j.Logger;

public class SimpleMetricReporter implements IMetricProvider {
    final static Logger logger = Logger.getLogger(SimpleMetricReporter.class);

    public void ReportTiming(String metric, int timeInms){
        //TODO : put real data into Graphite/ NewRelic
        logger.info(metric + ":" + timeInms);
    }
}
