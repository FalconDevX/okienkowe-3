import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SalaryDistribution {
    private final double min;
    private final double max;
    private final double mean;
    private final double median;
    private final double standardDeviation;
    private final Map<String, Integer> ranges;

    public SalaryDistribution(double min, double max, double mean, double median, double standardDeviation, Map<String, Integer> ranges) {
        this.min = min;
        this.max = max;
        this.mean = mean;
        this.median = median;
        this.standardDeviation = standardDeviation;
        this.ranges = new LinkedHashMap<>(ranges);
    }

    public static SalaryDistribution empty() {
        return new SalaryDistribution(0.0, 0.0, 0.0, 0.0, 0.0, Collections.emptyMap());
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getMean() {
        return mean;
    }

    public double getMedian() {
        return median;
    }

    public double getStandardDeviation() {
        return standardDeviation;
    }

    public Map<String, Integer> getRanges() {
        return Collections.unmodifiableMap(ranges);
    }

    @Override
    public String toString() {
        return "SalaryDistribution{" +
                "min=" + min +
                ", max=" + max +
                ", mean=" + mean +
                ", median=" + median +
                ", standardDeviation=" + standardDeviation +
                ", ranges=" + ranges +
                '}';
    }
}
