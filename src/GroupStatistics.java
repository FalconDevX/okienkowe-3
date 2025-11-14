import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class GroupStatistics {
    private final int employeeCount;
    private final double averageSalary;
    private final double highestSalary;
    private final double lowestSalary;
    private final Map<EmployeeCondition, Long> conditionDistribution;

    public GroupStatistics(int employeeCount,
                           double averageSalary,
                           double highestSalary,
                           double lowestSalary,
                           Map<EmployeeCondition, Long> conditionDistribution) {
        this.employeeCount = employeeCount;
        this.averageSalary = averageSalary;
        this.highestSalary = highestSalary;
        this.lowestSalary = lowestSalary;
        this.conditionDistribution = new EnumMap<>(conditionDistribution);
    }

    public static GroupStatistics empty() {
        EnumMap<EmployeeCondition, Long> emptyMap = new EnumMap<>(EmployeeCondition.class);
        for (EmployeeCondition condition : EmployeeCondition.values()) {
            emptyMap.put(condition, 0L);
        }
        return new GroupStatistics(0, 0.0, 0.0, 0.0, emptyMap);
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public double getAverageSalary() {
        return averageSalary;
    }

    public double getHighestSalary() {
        return highestSalary;
    }

    public double getLowestSalary() {
        return lowestSalary;
    }

    public Map<EmployeeCondition, Long> getConditionDistribution() {
        return Collections.unmodifiableMap(conditionDistribution);
    }

    @Override
    public String toString() {
        return "GroupStatistics{" +
                "employeeCount=" + employeeCount +
                ", averageSalary=" + averageSalary +
                ", highestSalary=" + highestSalary +
                ", lowestSalary=" + lowestSalary +
                ", conditionDistribution=" + conditionDistribution +
                '}';
    }
}
