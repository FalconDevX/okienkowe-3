public class Employee implements Comparable<Employee> {
    private String firstName;
    private String lastName;
    private EmployeeCondition condition;
    private int birthYear;
    private double salary;

    public Employee(String firstName, String lastName, EmployeeCondition condition, int birthYear, double salary) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.condition = condition;
        this.birthYear = birthYear;
        this.salary = salary;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public EmployeeCondition getCondition() { return condition; }
    public int getBirthYear() { return birthYear; }
    public double getSalary() { return salary; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setCondition(EmployeeCondition condition) { this.condition = condition; }
    public void setBirthYear(int birthYear) { this.birthYear = birthYear; }
    public void setSalary(double salary) { this.salary = salary; }

    public void printing() {
        System.out.println("Imię: " + firstName +
                ", Nazwisko: " + lastName +
                ", Stan: " + condition +
                ", Rok urodzenia: " + birthYear +
                ", Pensja: " + salary);
    }

    @Override
    public int compareTo(Employee employee) {
        return this.lastName.compareToIgnoreCase(employee.lastName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Employee)) return false;
        Employee other = (Employee) obj;
        return firstName.equalsIgnoreCase(other.firstName) && lastName.equalsIgnoreCase(other.lastName);
    }

    @Override
    public int hashCode() {
        return (firstName + lastName).toLowerCase().hashCode();
    }
}
