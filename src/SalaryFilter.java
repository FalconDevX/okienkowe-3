import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class SalaryFilter {
    private SalaryFilter() {
    }

    public static Predicate<Employee> minSalary(double min) {
        return employee -> employee != null && employee.getSalary() >= min;
    }

    public static Predicate<Employee> salaryRange(double min, double max) {
        return employee -> employee != null && employee.getSalary() >= min && employee.getSalary() <= max;
    }

    public static Predicate<Employee> topPercent(List<Employee> allEmployees, double percent) {
        Objects.requireNonNull(allEmployees, "Lista pracowników nie może być null");
        if (allEmployees.isEmpty()) {
            return employee -> false;
        }
        if (percent <= 0) {
            return employee -> false;
        }

        double threshold = Math.min(percent, 100.0) / 100.0;
        int limit = (int) Math.ceil(allEmployees.size() * threshold);
        limit = Math.max(limit, 1);

        double cutoffSalary = allEmployees.stream()
                .sorted((e1, e2) -> Double.compare(e2.getSalary(), e1.getSalary()))
                .limit(limit)
                .mapToDouble(Employee::getSalary)
                .min()
                .orElse(Double.MAX_VALUE);

        double finalCutoffSalary = cutoffSalary;
        return employee -> employee != null && employee.getSalary() >= finalCutoffSalary;
    }
}
