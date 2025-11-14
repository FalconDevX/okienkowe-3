import java.time.Year;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ClassEmployee {
    private final String groupName;
    private final int maxCapacity;
    private final List<Employee> employees;

    public ClassEmployee(String groupName, int maxCapacity) {
        this.groupName = groupName;
        this.maxCapacity = maxCapacity;
        this.employees = new ArrayList<>();
    }

    public boolean addEmployee(Employee employee) {
        Objects.requireNonNull(employee, "Employee cannot be null");
        if (employees.size() >= maxCapacity) {
            System.out.println("Grupa jest pełna. Nie można dodać więcej pracowników.");
            return false;
        }

        for (Employee e : employees) {
            if (e.equals(employee)) {
                System.out.println("Pracownik już istnieje w grupie.");
                return false;
            }
        }

        employees.add(employee);
        return true;
    }

    public boolean removeEmployee(Employee employee) {
        return employees.remove(employee);
    }

    public void changeCondition(Employee employee, EmployeeCondition condition) {
        for (Employee e : employees) {
            if (e.equals(employee)) {
                e.setCondition(condition);
                return;
            }
        }
        System.out.println("Nie znaleziono pracownika.");
    }

    public void addSalary(Employee employee, double amount) {
        for (Employee e : employees) {
            if (e.equals(employee)) {
                e.setSalary(e.getSalary() + amount);
                return;
            }
        }
        System.out.println("Nie znaleziono pracownika.");
    }

    public void summary() {
        System.out.println("Grupa: " + groupName);
        for (Employee e : employees) {
            e.printing();
        }
    }

    public Employee search(String lastName) {
        Comparator<Employee> comparatorByName = Comparator.comparing(Employee::getLastName, String.CASE_INSENSITIVE_ORDER);
        return employees.stream()
                .filter(e -> e.getLastName().equalsIgnoreCase(lastName))
                .max(comparatorByName)
                .orElse(null);
    }

    public List<Employee> searchPartial(String fragment) {
        List<Employee> results = new ArrayList<>();
        for (Employee e : employees) {
            if (e.getFirstName().toLowerCase().contains(fragment.toLowerCase()) ||
                    e.getLastName().toLowerCase().contains(fragment.toLowerCase())) {
                results.add(e);
            }
        }
        return results;
    }

    public long countByCondition(EmployeeCondition condition) {
        return employees.stream().filter(e -> e.getCondition() == condition).count();
    }

    public List<Employee> sortByName() {
        List<Employee> sorted = new ArrayList<>(employees);
        Collections.sort(sorted);
        return sorted;
    }

    public List<Employee> sortBySalary() {
        List<Employee> sorted = new ArrayList<>(employees);
        sorted.sort((e1, e2) -> Double.compare(e2.getSalary(), e1.getSalary()));
        return sorted;
    }

    public Employee max() {
        return Collections.max(employees, Comparator.comparingDouble(Employee::getSalary));
    }

    public int getEmployeeCount() {
        return employees.size();
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<Employee> getEmployees() {
        return Collections.unmodifiableList(employees);
    }

    public int removeDuplicates() {
        if (employees.isEmpty()) {
            return 0;
        }

        Set<String> seen = new HashSet<>();
        List<Employee> uniqueEmployees = new ArrayList<>();
        int duplicates = 0;

        for (Employee employee : employees) {
            String key = (employee.getFirstName() + "|" + employee.getLastName()).toLowerCase(Locale.ROOT);
            if (seen.add(key)) {
                uniqueEmployees.add(employee);
            } else {
                duplicates++;
            }
        }

        if (duplicates > 0) {
            employees.clear();
            employees.addAll(uniqueEmployees);
        }

        return duplicates;
    }

    public Map<EmployeeCondition, List<Employee>> groupByCondition() {
        Map<EmployeeCondition, List<Employee>> grouped = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getCondition,
                        () -> new EnumMap<>(EmployeeCondition.class),
                        Collectors.collectingAndThen(Collectors.toList(), list ->
                                list.stream()
                                        .sorted(Comparator.comparing(Employee::getLastName, String.CASE_INSENSITIVE_ORDER)
                                                .thenComparing(Employee::getFirstName, String.CASE_INSENSITIVE_ORDER))
                                        .collect(Collectors.toList())
                        )));

        for (EmployeeCondition condition : EmployeeCondition.values()) {
            grouped.putIfAbsent(condition, new ArrayList<>());
        }

        return grouped;
    }

    public double medianSalary() {
        if (employees.isEmpty()) {
            return 0.0;
        }

        List<Double> salaries = employees.stream()
                .map(Employee::getSalary)
                .sorted()
                .collect(Collectors.toList());

        int size = salaries.size();
        int mid = size / 2;

        if (size % 2 == 1) {
            return salaries.get(mid);
        }

        return (salaries.get(mid - 1) + salaries.get(mid)) / 2.0;
    }

    public Optional<Employee> oldestEmployee() {
        return employees.stream()
                .min(Comparator.comparingInt(Employee::getBirthYear));
    }

    public Optional<Employee> youngestEmployee() {
        return employees.stream()
                .max(Comparator.comparingInt(Employee::getBirthYear));
    }

    public double getAverageAge() {
        if (employees.isEmpty()) {
            return 0.0;
        }

        int currentYear = Year.now().getValue();
        return employees.stream()
                .mapToInt(employee -> currentYear - employee.getBirthYear())
                .average()
                .orElse(0.0);
    }

    public AgeStatistics getAgeStatistics() {
        if (employees.isEmpty()) {
            return AgeStatistics.empty();
        }

        int currentYear = Year.now().getValue();
        List<Integer> ages = employees.stream()
                .map(employee -> currentYear - employee.getBirthYear())
                .sorted()
                .collect(Collectors.toList());

        int youngest = ages.get(0);
        int oldest = ages.get(ages.size() - 1);
        double average = ages.stream().mapToInt(Integer::intValue).average().orElse(0.0);

        Map<String, Long> ranges = ages.stream()
                .collect(Collectors.groupingBy(age -> formatAgeRange(age),
                        LinkedHashMap::new,
                        Collectors.counting()));

        return new AgeStatistics(youngest, oldest, average, ages.size(), ranges);
    }

    private String formatAgeRange(int age) {
        int lowerBound = (age / 10) * 10;
        int upperBound = lowerBound + 9;
        return lowerBound + "-" + upperBound;
    }

    public List<Employee> filterByMinSalary(double minSalary) {
        return employees.stream()
                .filter(SalaryFilter.minSalary(minSalary))
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed()
                        .thenComparing(Employee::getLastName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Employee::getFirstName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<Employee> filterBySalaryRange(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimalne wynagrodzenie nie może być większe niż maksymalne");
        }
        return employees.stream()
                .filter(SalaryFilter.salaryRange(min, max))
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed()
                        .thenComparing(Employee::getLastName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Employee::getFirstName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public List<Employee> getTopEarners(int count) {
        if (count <= 0) {
            return Collections.emptyList();
        }
        return employees.stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Employee> getBottomEarners(int count) {
        if (count <= 0) {
            return Collections.emptyList();
        }
        return employees.stream()
                .sorted(Comparator.comparingDouble(Employee::getSalary))
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Employee> filterByPercentile(double percentile) {
        if (employees.isEmpty()) {
            return Collections.emptyList();
        }
        if (percentile < 0 || percentile > 100) {
            throw new IllegalArgumentException("Percentyl musi mieścić się w zakresie 0-100");
        }

        double topPercent = 100.0 - percentile;
        if (topPercent <= 0) {
            return Collections.emptyList();
        }

        Predicate<Employee> predicate = SalaryFilter.topPercent(employees, topPercent);
        return employees.stream()
                .filter(predicate)
                .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                .collect(Collectors.toList());
    }

    public Map<Double, List<Employee>> groupBySalary() {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getSalary,
                        TreeMap::new,
                        Collectors.collectingAndThen(Collectors.toList(), list ->
                                list.stream()
                                        .sorted(Comparator.comparing(Employee::getLastName, String.CASE_INSENSITIVE_ORDER)
                                                .thenComparing(Employee::getFirstName, String.CASE_INSENSITIVE_ORDER))
                                        .collect(Collectors.toList()))));
    }

    public Map<String, List<Employee>> groupBySalaryRange(double rangeSize) {
        if (rangeSize <= 0) {
            throw new IllegalArgumentException("Rozmiar przedziału musi być dodatni");
        }

        return employees.stream()
                .collect(Collectors.groupingBy(employee -> SalaryAnalyzer.determineRange(employee.getSalary(), rangeSize),
                        TreeMap::new,
                        Collectors.collectingAndThen(Collectors.toList(), list ->
                                list.stream()
                                        .sorted(Comparator.comparingDouble(Employee::getSalary))
                                        .collect(Collectors.toList()))));
    }

    public SalaryDistribution getSalaryDistribution() {
        return SalaryAnalyzer.analyzeDistribution(employees, 1000.0);
    }

    public boolean hasEmployeesOnDelegation() {
        return ConditionAnalyzer.hasCondition(employees, EmployeeCondition.DELEGACJA);
    }

    public boolean allEmployeesPresent() {
        return employees.stream()
                .allMatch(employee -> employee.getCondition() == EmployeeCondition.OBECNY);
    }

    public boolean noEmployeesSick() {
        return employees.stream()
                .noneMatch(employee -> employee.getCondition() == EmployeeCondition.CHORY);
    }

    public Map<EmployeeCondition, Long> getConditionSummary() {
        Map<EmployeeCondition, Long> summary = employees.stream()
                .collect(Collectors.groupingBy(Employee::getCondition,
                        () -> new EnumMap<>(EmployeeCondition.class),
                        Collectors.counting()));

        for (EmployeeCondition condition : EmployeeCondition.values()) {
            summary.putIfAbsent(condition, 0L);
        }
        return summary;
    }

    public List<Employee> getEmployeesSortedByAge() {
        return employees.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(Employee::getBirthYear))
                .collect(Collectors.toList());
    }

    public Map<String, Double> getAverageSalaryByLastName() {
        if (employees.isEmpty()) {
            return Collections.emptyMap();
        }

        return employees.stream()
                .collect(Collectors.groupingBy(Employee::getLastName,
                        TreeMap::new,
                        Collectors.averagingDouble(Employee::getSalary)));
    }

    public boolean hasHighEarner(double threshold) {
        return employees.stream().anyMatch(employee -> employee.getSalary() > threshold);
    }
}
