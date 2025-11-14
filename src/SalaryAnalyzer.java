import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class SalaryAnalyzer {
    private SalaryAnalyzer() {
    }

    public static String determineRange(double salary, double rangeSize) {
        double lower = Math.floor(salary / rangeSize) * rangeSize;
        double upper = lower + rangeSize;
        return String.format(Locale.US, "%.2f-%.2f", lower, upper);
    }

    public static SalaryDistribution analyzeDistribution(List<Employee> employees, double rangeSize) {
        if (employees.isEmpty()) {
            return SalaryDistribution.empty();
        }

        List<Double> salaries = employees.stream()
                .map(Employee::getSalary)
                .sorted()
                .collect(Collectors.toList());

        double min = salaries.get(0);
        double max = salaries.get(salaries.size() - 1);
        double mean = salaries.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double median = computeMedian(salaries);
        double stdDev = computeStandardDeviation(salaries, mean);

        Map<String, Integer> ranges = new LinkedHashMap<>();
        for (double salary : salaries) {
            String range = determineRange(salary, rangeSize);
            ranges.merge(range, 1, Integer::sum);
        }

        return new SalaryDistribution(min, max, mean, median, stdDev, ranges);
    }

    private static double computeMedian(List<Double> salaries) {
        int size = salaries.size();
        int mid = size / 2;
        if (size % 2 == 1) {
            return salaries.get(mid);
        }
        return (salaries.get(mid - 1) + salaries.get(mid)) / 2.0;
    }

    private static double computeStandardDeviation(List<Double> salaries, double mean) {
        if (salaries.isEmpty()) {
            return 0.0;
        }
        double variance = salaries.stream()
                .mapToDouble(salary -> Math.pow(salary - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }

    public static List<Employee> findOutliers(List<Employee> employees, double threshold) {
        if (employees.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        if (threshold <= 0) {
            threshold = 2.0;
        }

        List<Double> salaries = employees.stream()
                .map(Employee::getSalary)
                .collect(Collectors.toList());
        double mean = salaries.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double stdDev = computeStandardDeviation(salaries, mean);

        if (stdDev == 0.0) {
            return java.util.Collections.emptyList();
        }

        double finalThreshold = threshold;
        double lowerBound = mean - finalThreshold * stdDev;
        double upperBound = mean + finalThreshold * stdDev;

        return employees.stream()
                .filter(employee -> employee.getSalary() < lowerBound || employee.getSalary() > upperBound)
                .collect(Collectors.toList());
    }

    public static double calculateGiniCoefficient(List<Employee> employees) {
        if (employees.isEmpty()) {
            return 0.0;
        }

        List<Double> incomes = employees.stream()
                .map(Employee::getSalary)
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));

        double totalIncome = incomes.stream().mapToDouble(Double::doubleValue).sum();
        if (totalIncome == 0.0) {
            return 0.0;
        }

        int n = incomes.size();
        double cumulative = 0.0;
        for (int i = 0; i < n; i++) {
            cumulative += (i + 1) * incomes.get(i);
        }

        return (2.0 * cumulative) / (n * totalIncome) - (n + 1.0) / n;
    }
}
