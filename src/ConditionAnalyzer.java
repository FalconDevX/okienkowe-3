import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ConditionAnalyzer {
    private ConditionAnalyzer() {
    }

    public static boolean hasCondition(List<Employee> employees, EmployeeCondition condition) {
        return employees.stream().anyMatch(employee -> employee.getCondition() == condition);
    }

    public static double getConditionPercentage(List<Employee> employees, EmployeeCondition condition) {
        if (employees.isEmpty()) {
            return 0.0;
        }
        long count = employees.stream().filter(employee -> employee.getCondition() == condition).count();
        return (double) count / employees.size() * 100.0;
    }

    public static Map<EmployeeCondition, Double> percentageSummary(List<Employee> employees) {
        Map<EmployeeCondition, Double> summary = new EnumMap<>(EmployeeCondition.class);
        for (EmployeeCondition condition : EmployeeCondition.values()) {
            summary.put(condition, getConditionPercentage(employees, condition));
        }
        return summary;
    }
}
