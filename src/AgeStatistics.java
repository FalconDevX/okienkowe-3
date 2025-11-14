import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AgeStatistics {
    private final int youngestAge;
    private final int oldestAge;
    private final double averageAge;
    private final int employeeCount;
    private final Map<String, Long> ageRanges;

    public AgeStatistics(int youngestAge, int oldestAge, double averageAge, int employeeCount, Map<String, Long> ageRanges) {
        this.youngestAge = youngestAge;
        this.oldestAge = oldestAge;
        this.averageAge = averageAge;
        this.employeeCount = employeeCount;
        this.ageRanges = new LinkedHashMap<>(ageRanges);
    }

    public static AgeStatistics empty() {
        return new AgeStatistics(0, 0, 0.0, 0, Collections.emptyMap());
    }

    public int getYoungestAge() {
        return youngestAge;
    }

    public int getOldestAge() {
        return oldestAge;
    }

    public double getAverageAge() {
        return averageAge;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public Map<String, Long> getAgeRanges() {
        return Collections.unmodifiableMap(ageRanges);
    }

    @Override
    public String toString() {
        return "AgeStatistics{" +
                "youngestAge=" + youngestAge +
                ", oldestAge=" + oldestAge +
                ", averageAge=" + averageAge +
                ", employeeCount=" + employeeCount +
                ", ageRanges=" + ageRanges +
                '}';
    }
}
