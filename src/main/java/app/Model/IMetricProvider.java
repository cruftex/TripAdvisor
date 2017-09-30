package app.Model;

public interface IMetricProvider {
      void ReportTiming(String metric, long timeInms);
}
