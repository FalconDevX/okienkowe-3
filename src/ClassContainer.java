import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClassContainer {
    private Map<String, ClassEmployee> groups;
    private StorageMode currentMode;
    private Comparator<String> keyComparator;

    public ClassContainer() {
        this(StorageMode.HASH_MAP);
    }

    public ClassContainer(Comparator<String> keyComparator) {
        this(StorageMode.TREE_MAP, keyComparator);
    }

    public ClassContainer(StorageMode mode) {
        this(mode, defaultComparator());
    }

    public ClassContainer(StorageMode mode, Comparator<String> keyComparator) {
        this.currentMode = mode;
        this.keyComparator = keyComparator != null ? keyComparator : defaultComparator();
        this.groups = createMapForMode(mode);
    }

    private static Comparator<String> defaultComparator() {
        return Comparator.comparing((String name) -> name.toLowerCase(Locale.ROOT))
                .thenComparing(Function.identity());
    }

    private Map<String, ClassEmployee> createMapForMode(StorageMode mode) {
        switch (mode) {
            case TREE_MAP:
                return new TreeMap<>(keyComparator);
            case LINKED_HASH_MAP:
                return new LinkedHashMap<>();
            case HASH_MAP:
            default:
                return new java.util.HashMap<>();
        }
    }

    public boolean addClass(String name, int capacity) {
        Optional<String> existingKey = groups.keySet().stream()
                .filter(key -> key.equalsIgnoreCase(name))
                .findFirst();
        if (existingKey.isPresent()) {
            System.out.println("Grupa o nazwie \"" + existingKey.get() + "\" już istnieje.");
            return false;
        }
        groups.put(name, new ClassEmployee(name, capacity));
        System.out.println("Dodano grupę: " + name + " o pojemności " + capacity);
        return true;
    }

    public boolean removeClass(String name) {
        String keyToRemove = groups.keySet().stream()
                .filter(key -> key.equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
        if (keyToRemove != null) {
            groups.remove(keyToRemove);
            System.out.println("Usunięto grupę: " + keyToRemove);
            return true;
        } else {
            System.out.println("Nie znaleziono grupy o nazwie: " + name);
        }
        return false;
    }

    public List<String> findEmpty() {
        return groups.entrySet().stream()
                .filter(entry -> entry.getValue().getEmployeeCount() == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void summary() {
        System.out.println("===== Podsumowanie grup pracowniczych =====");
        for (Map.Entry<String, ClassEmployee> entry : groups.entrySet()) {
            ClassEmployee group = entry.getValue();
            double fillPercent = group.getMaxCapacity() == 0 ? 0.0 :
                    ((double) group.getEmployeeCount() / group.getMaxCapacity()) * 100.0;

            System.out.printf("Grupa: %-15s | Zapełnienie: %.1f%% (%d/%d)%n",
                    entry.getKey(), fillPercent, group.getEmployeeCount(), group.getMaxCapacity());
        }
    }

    public ClassEmployee getGroup(String name) {
        return groups.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(name))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    public Map<String, ClassEmployee> getGroupsView() {
        return Collections.unmodifiableMap(groups);
    }

    public List<String> getGroupsInOrder() {
        return new ArrayList<>(groups.keySet());
    }

    public Map<String, Integer> countEmployeesInGroups() {
        return groups.entrySet().stream()
                .filter(entry -> entry.getValue().getEmployeeCount() > 0)
                .sorted(Map.Entry.<String, ClassEmployee>comparingByValue(
                        Comparator.comparingInt(ClassEmployee::getEmployeeCount)).reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getEmployeeCount(),
                        (left, right) -> left,
                        LinkedHashMap::new));
    }

    public Map<String, GroupStatistics> getDetailedStatistics() {
        return groups.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> buildGroupStatistics(entry.getValue()),
                        (left, right) -> left,
                        LinkedHashMap::new));
    }

    private GroupStatistics buildGroupStatistics(ClassEmployee classEmployee) {
        int count = classEmployee.getEmployeeCount();
        if (count == 0) {
            return GroupStatistics.empty();
        }

        double averageSalary = classEmployee.getEmployees().stream()
                .mapToDouble(Employee::getSalary)
                .average()
                .orElse(0.0);
        double maxSalary = classEmployee.getEmployees().stream()
                .mapToDouble(Employee::getSalary)
                .max()
                .orElse(0.0);
        double minSalary = classEmployee.getEmployees().stream()
                .mapToDouble(Employee::getSalary)
                .min()
                .orElse(0.0);
        Map<EmployeeCondition, Long> distribution = classEmployee.getConditionSummary();

        return new GroupStatistics(count, averageSalary, maxSalary, minSalary, distribution);
    }

    public void printEmployeeStatistics() {
        Map<String, Integer> stats = countEmployeesInGroups();

        System.out.println("Statystyki pracowników w grupach:");
        stats.forEach((groupName, count) ->
                System.out.println(groupName + ": " + count + " pracowników"));
    }

    public void changeStorageMode(StorageMode newMode) {
        if (newMode == currentMode) {
            return;
        }
        Map<String, ClassEmployee> newMap;
        if (newMode == StorageMode.TREE_MAP) {
            newMap = new TreeMap<>(keyComparator);
        } else if (newMode == StorageMode.LINKED_HASH_MAP) {
            newMap = new LinkedHashMap<>();
        } else {
            newMap = new java.util.HashMap<>();
        }

        groups.entrySet().forEach(entry -> newMap.put(entry.getKey(), entry.getValue()));
        groups = newMap;
        currentMode = newMode;
    }

    public boolean renameGroup(String currentName, String newName) {
        if (currentName == null || newName == null || newName.isBlank()) {
            return false;
        }

        if (groups.keySet().stream().anyMatch(key -> key.equalsIgnoreCase(newName))) {
            return false;
        }

        String keyToUpdate = groups.keySet().stream()
                .filter(key -> key.equalsIgnoreCase(currentName))
                .findFirst()
                .orElse(null);
        if (keyToUpdate == null) {
            return false;
        }

        ClassEmployee group = groups.remove(keyToUpdate);
        group.setGroupName(newName);
        groups.put(newName, group);
        return true;
    }

    public boolean updateGroupCapacity(String name, int newCapacity) {
        ClassEmployee group = getGroup(name);
        if (group == null) {
            return false;
        }
        return group.setMaxCapacity(newCapacity);
    }

    public void demonstrateOrderDifferences() {
        System.out.println("Aktualny tryb przechowywania: " + currentMode);
        System.out.println("Kolejność grup: " + new ArrayList<>(groups.keySet()));
    }
}
