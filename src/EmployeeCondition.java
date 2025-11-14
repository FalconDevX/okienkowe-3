public enum EmployeeCondition {
    OBECNY("Obecny"),
    DELEGACJA("Delegacja"),
    CHORY("Chory"),
    NIEOBECNY("Nieobecny");

    private final String name;

    EmployeeCondition(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
