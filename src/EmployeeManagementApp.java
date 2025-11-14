import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

public class EmployeeManagementApp extends Application {
    private final ClassContainer container = new ClassContainer();
    private final ObservableList<String> groupNames = FXCollections.observableArrayList();
    private final ObservableList<Employee> employeeItems = FXCollections.observableArrayList();
    private FilteredList<Employee> filteredEmployees;

    private ListView<String> groupListView;
    private TableView<Employee> employeeTable;
    private TextField filterField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("System ewidencji pracowników");
        seedData();
        groupNames.setAll(container.getGroupsView().keySet());

        filteredEmployees = new FilteredList<>(employeeItems, employee -> true);

        groupListView = createGroupList();
        employeeTable = createEmployeeTable();
        filterField = new TextField();
        filterField.setPromptText("Filtruj po nazwisku...");
        filterField.setOnAction(event -> applyFilter());

        Button filterButton = new Button("Filtruj");
        filterButton.setOnAction(event -> applyFilter());

        HBox filterBox = new HBox(10, new Label("Nazwisko:"), filterField, filterButton);
        filterBox.setPadding(new Insets(0, 0, 10, 0));

        VBox groupBox = new VBox(10,
                new Label("Grupy pracownicze"),
                groupListView,
                createGroupControls());
        groupBox.setPadding(new Insets(10));
        groupBox.setPrefWidth(260);

        VBox employeeBox = new VBox(10,
                new Label("Pracownicy"),
                filterBox,
                employeeTable,
                createEmployeeControls());
        employeeBox.setPadding(new Insets(10));
        VBox.setVgrow(employeeTable, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setLeft(groupBox);
        root.setCenter(employeeBox);

        primaryStage.setScene(new Scene(root, 1100, 600));
        primaryStage.show();
    }

    private void seedData() {
        if (container.addClass("Programiści", 5)) {
            ClassEmployee devs = container.getGroup("Programiści");
            Objects.requireNonNull(devs);
            devs.addEmployee(new Employee("Jan", "Kowalski", EmployeeCondition.OBECNY, 1990, 8500));
            devs.addEmployee(new Employee("Anna", "Lewandowska", EmployeeCondition.DELEGACJA, 1995, 9200));
            devs.addEmployee(new Employee("Michał", "Krawczyk", EmployeeCondition.CHORY, 1992, 7200));
        }
        if (container.addClass("Księgowość", 4)) {
            ClassEmployee accounting = container.getGroup("Księgowość");
            Objects.requireNonNull(accounting);
            accounting.addEmployee(new Employee("Karolina", "Mazur", EmployeeCondition.OBECNY, 1988, 6400));
            accounting.addEmployee(new Employee("Marta", "Wójcik", EmployeeCondition.NIEOBECNY, 1991, 6100));
        }
        if (container.addClass("HR", 3)) {
            ClassEmployee hr = container.getGroup("HR");
            Objects.requireNonNull(hr);
            hr.addEmployee(new Employee("Robert", "Zieliński", EmployeeCondition.OBECNY, 1985, 6800));
        }
    }

    private ListView<String> createGroupList() {
        ListView<String> listView = new ListView<>(groupNames);
        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) ->
                showEmployeesForGroup(newValue));
        listView.setPrefHeight(400);
        return listView;
    }

    private TableView<Employee> createEmployeeTable() {
        TableView<Employee> tableView = new TableView<>(filteredEmployees);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Employee, String> firstNameCol = new TableColumn<>("Imię");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Employee, String> lastNameCol = new TableColumn<>("Nazwisko");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Employee, EmployeeCondition> conditionCol = new TableColumn<>("Stan");
        conditionCol.setCellValueFactory(new PropertyValueFactory<>("condition"));

        TableColumn<Employee, Integer> birthYearCol = new TableColumn<>("Rok ur.");
        birthYearCol.setCellValueFactory(new PropertyValueFactory<>("birthYear"));

        TableColumn<Employee, Double> salaryCol = new TableColumn<>("Pensja");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));

        tableView.getColumns().addAll(firstNameCol, lastNameCol, conditionCol, birthYearCol, salaryCol);
        return tableView;
    }

    private FlowPane createGroupControls() {
        Button add = new Button("Dodaj");
        add.setOnAction(event -> addGroup());

        Button edit = new Button("Edytuj");
        edit.disableProperty().bind(groupListView.getSelectionModel().selectedItemProperty().isNull());
        edit.setOnAction(event -> editGroup());

        Button delete = new Button("Usuń");
        delete.disableProperty().bind(groupListView.getSelectionModel().selectedItemProperty().isNull());
        delete.setOnAction(event -> removeGroup());

        Button sort = new Button("Sortuj");
        sort.setOnAction(event -> sortData());

        FlowPane pane = new FlowPane(10, 10, add, edit, delete, sort);
        return pane;
    }

    private FlowPane createEmployeeControls() {
        Button add = new Button("Dodaj pracownika");
        add.disableProperty().bind(groupListView.getSelectionModel().selectedItemProperty().isNull());
        add.setOnAction(event -> addEmployee());

        Button edit = new Button("Modyfikuj dane");
        edit.disableProperty().bind(Bindings.isNull(employeeTable.getSelectionModel().selectedItemProperty()));
        edit.setOnAction(event -> modifyEmployee());

        Button delete = new Button("Usuń pracownika");
        delete.disableProperty().bind(Bindings.isNull(employeeTable.getSelectionModel().selectedItemProperty()));
        delete.setOnAction(event -> removeEmployee());

        Button reset = new Button("Wyczyść filtr");
        reset.setOnAction(event -> {
            filterField.clear();
            applyFilter();
        });

        FlowPane pane = new FlowPane(10, 10, add, edit, delete, reset);
        return pane;
    }

    private void showEmployeesForGroup(String groupName) {
        employeeItems.clear();
        if (groupName == null) {
            return;
        }
        ClassEmployee group = container.getGroup(groupName);
        if (group != null) {
            employeeItems.addAll(group.getEmployees());
        }
        applyFilter();
    }

    private void addGroup() {
        Dialog<GroupFormData> dialog = buildGroupDialog("Dodaj grupę", "", 5);
        Optional<GroupFormData> result = dialog.showAndWait();
        result.ifPresent(data -> {
            if (data.capacity <= 0 || data.name.isBlank()) {
                showError("Nieprawidłowe dane grupy");
                return;
            }
            if (!container.addClass(data.name.trim(), data.capacity)) {
                showError("Grupa o podanej nazwie już istnieje");
                return;
            }
            refreshGroupList();
            groupListView.getSelectionModel().select(data.name.trim());
        });
    }

    private void editGroup() {
        String selectedGroup = groupListView.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            return;
        }
        ClassEmployee group = container.getGroup(selectedGroup);
        if (group == null) {
            return;
        }

        Dialog<GroupFormData> dialog = buildGroupDialog("Edytuj grupę", group.getGroupName(), group.getMaxCapacity());
        Optional<GroupFormData> result = dialog.showAndWait();
        result.ifPresent(data -> {
            String targetName = selectedGroup;
            if (!data.name.equalsIgnoreCase(selectedGroup)) {
                if (!container.renameGroup(selectedGroup, data.name.trim())) {
                    showError("Nie można zmienić nazwy grupy. Upewnij się, że jest unikalna.");
                    return;
                }
                targetName = data.name.trim();
            }
            if (!container.updateGroupCapacity(targetName, data.capacity)) {
                showError("Nowa pojemność nie może być mniejsza niż liczba pracowników.");
                return;
            }
            refreshGroupList();
            groupListView.getSelectionModel().select(targetName);
        });
    }

    private void removeGroup() {
        String selectedGroup = groupListView.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Czy na pewno chcesz usunąć grupę " + selectedGroup + "?",
                ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText(null);
        alert.showAndWait().filter(ButtonType.OK::equals).ifPresent(type -> {
            container.removeClass(selectedGroup);
            refreshGroupList();
            employeeItems.clear();
        });
    }

    private void addEmployee() {
        String selectedGroup = groupListView.getSelectionModel().getSelectedItem();
        if (selectedGroup == null) {
            showError("Wybierz grupę przed dodaniem pracownika");
            return;
        }
        ClassEmployee group = container.getGroup(selectedGroup);
        if (group == null) {
            return;
        }

        Dialog<EmployeeFormData> dialog = buildEmployeeDialog("Dodaj pracownika", null);
        Optional<EmployeeFormData> result = dialog.showAndWait();
        result.ifPresent(data -> {
            if (data.firstName.isBlank() || data.lastName.isBlank()) {
                showError("Imię i nazwisko nie mogą być puste");
                return;
            }
            Employee employee = data.toEmployee();
            if (!group.addEmployee(employee)) {
                showError("Nie można dodać pracownika. Grupa może być pełna lub zawierać duplikat.");
                return;
            }
            showEmployeesForGroup(group.getGroupName());
        });
    }

    private void modifyEmployee() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            return;
        }
        Dialog<EmployeeFormData> dialog = buildEmployeeDialog("Modyfikuj dane", selectedEmployee);
        Optional<EmployeeFormData> result = dialog.showAndWait();
        result.ifPresent(data -> {
            if (data.firstName.isBlank() || data.lastName.isBlank()) {
                showError("Imię i nazwisko nie mogą być puste");
                return;
            }
            selectedEmployee.setFirstName(data.firstName);
            selectedEmployee.setLastName(data.lastName);
            selectedEmployee.setBirthYear(data.birthYear);
            selectedEmployee.setSalary(data.salary);
            selectedEmployee.setCondition(data.condition);
            employeeTable.refresh();
        });
    }

    private void removeEmployee() {
        String selectedGroup = groupListView.getSelectionModel().getSelectedItem();
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedGroup == null || selectedEmployee == null) {
            return;
        }
        ClassEmployee group = container.getGroup(selectedGroup);
        if (group == null) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Czy na pewno chcesz usunąć pracownika " +
                        selectedEmployee.getFirstName() + " " + selectedEmployee.getLastName() + "?",
                ButtonType.OK, ButtonType.CANCEL);
        alert.setHeaderText(null);
        alert.showAndWait().filter(ButtonType.OK::equals).ifPresent(type -> {
            group.removeEmployee(selectedEmployee);
            showEmployeesForGroup(group.getGroupName());
        });
    }

    private void sortData() {
        FXCollections.sort(groupNames, (left, right) -> {
            ClassEmployee leftGroup = container.getGroup(left);
            ClassEmployee rightGroup = container.getGroup(right);
            double leftFill = leftGroup != null ? leftGroup.getFillPercentage() : 0.0;
            double rightFill = rightGroup != null ? rightGroup.getFillPercentage() : 0.0;
            int compare = Double.compare(rightFill, leftFill);
            if (compare == 0) {
                return left.compareToIgnoreCase(right);
            }
            return compare;
        });

        FXCollections.sort(employeeItems,
                Comparator.comparing(Employee::getLastName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Employee::getFirstName, String.CASE_INSENSITIVE_ORDER));
        applyFilter();
    }

    private void applyFilter() {
        String filterText = filterField.getText();
        if (filterText == null || filterText.isBlank()) {
            filteredEmployees.setPredicate(employee -> true);
        } else {
            String lowered = filterText.toLowerCase();
            filteredEmployees.setPredicate(employee ->
                    employee.getLastName().toLowerCase().contains(lowered));
        }
    }

    private Dialog<GroupFormData> buildGroupDialog(String title, String name, int capacity) {
        Dialog<GroupFormData> dialog = new Dialog<>();
        dialog.setTitle(title);

        TextField nameField = new TextField(name);
        nameField.setPromptText("Nazwa grupy");
        Spinner<Integer> capacitySpinner = new Spinner<>();
        capacitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, Math.max(1, capacity)));

        GridLikePane content = new GridLikePane();
        content.addRow("Nazwa", nameField);
        content.addRow("Pojemność", capacitySpinner);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new GroupFormData(nameField.getText().trim(), capacitySpinner.getValue());
            }
            return null;
        });
        return dialog;
    }

    private Dialog<EmployeeFormData> buildEmployeeDialog(String title, Employee employee) {
        Dialog<EmployeeFormData> dialog = new Dialog<>();
        dialog.setTitle(title);

        TextField firstNameField = new TextField(employee != null ? employee.getFirstName() : "");
        TextField lastNameField = new TextField(employee != null ? employee.getLastName() : "");
        Spinner<Integer> birthYearSpinner = new Spinner<>();
        birthYearSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1940, 2025,
                employee != null ? employee.getBirthYear() : 1990));
        Spinner<Double> salarySpinner = new Spinner<>();
        salarySpinner.setEditable(true);
        salarySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1_000_000,
                employee != null ? employee.getSalary() : 6000, 100));
        ChoiceBox<EmployeeCondition> conditionChoice = new ChoiceBox<>(FXCollections.observableArrayList(EmployeeCondition.values()));
        conditionChoice.getSelectionModel().select(employee != null ? employee.getCondition() : EmployeeCondition.OBECNY);

        GridLikePane content = new GridLikePane();
        content.addRow("Imię", firstNameField);
        content.addRow("Nazwisko", lastNameField);
        content.addRow("Rok urodzenia", birthYearSpinner);
        content.addRow("Pensja", salarySpinner);
        content.addRow("Stan", conditionChoice);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new EmployeeFormData(
                        firstNameField.getText().trim(),
                        lastNameField.getText().trim(),
                        birthYearSpinner.getValue(),
                        salarySpinner.getValue(),
                        conditionChoice.getSelectionModel().getSelectedItem());
            }
            return null;
        });
        return dialog;
    }

    private void refreshGroupList() {
        String previouslySelected = groupListView != null ? groupListView.getSelectionModel().getSelectedItem() : null;
        groupNames.setAll(container.getGroupsView().keySet());
        if (previouslySelected != null) {
            groupListView.getSelectionModel().select(previouslySelected);
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private static class GroupFormData {
        final String name;
        final int capacity;

        GroupFormData(String name, int capacity) {
            this.name = name;
            this.capacity = capacity;
        }
    }

    private static class EmployeeFormData {
        final String firstName;
        final String lastName;
        final int birthYear;
        final double salary;
        final EmployeeCondition condition;

        EmployeeFormData(String firstName, String lastName, int birthYear, double salary, EmployeeCondition condition) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.birthYear = birthYear;
            this.salary = salary;
            this.condition = condition;
        }

        Employee toEmployee() {
            return new Employee(firstName, lastName, condition, birthYear, salary);
        }
    }

    private static class GridLikePane extends VBox {
        GridLikePane() {
            super(8);
        }

        void addRow(String label, javafx.scene.Node node) {
            HBox row = new HBox(10);
            Label lbl = new Label(label + ":");
            lbl.setMinWidth(120);
            row.getChildren().addAll(lbl, node);
            getChildren().add(row);
        }
    }
}
