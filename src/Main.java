public class Main {
    public static void main(String[] args) {
        ClassEmployee grupa = new ClassEmployee("Programiści", 3);

        grupa.addEmployee(new Employee("Jan", "Kowalski", EmployeeCondition.OBECNY, 1990, 5000));
        grupa.addEmployee(new Employee("Adam", "Nowak", EmployeeCondition.DELEGACJA, 1988, 6000));

        Employee found = grupa.search("Nowak");
        if (found != null)
            found.printing();
        else
            System.out.println("Nie znaleziono pracownika.");
    }
}
