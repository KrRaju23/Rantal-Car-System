import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CarRentalSystemGUI extends JFrame {

    private CarRentalSystem rentalSystem;
    private JTextField customerNameField;
    private JComboBox<String> carComboBox;
    private JTextField rentalDaysField;
    private JTextArea displayArea;

    public CarRentalSystemGUI(CarRentalSystem rentalSystem) {
        this.rentalSystem = rentalSystem;
        setTitle("Car Rental System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(6, 2));

        // Customer Name
        JLabel nameLabel = new JLabel("Customer Name:");
        customerNameField = new JTextField();
        mainPanel.add(nameLabel);
        mainPanel.add(customerNameField);

        // Car Selection
        JLabel carLabel = new JLabel("Select Car:");
        carComboBox = new JComboBox<>();
        updateCarComboBox();
        mainPanel.add(carLabel);
        mainPanel.add(carComboBox);

        // Rental Days
        JLabel daysLabel = new JLabel("Rental Days:");
        rentalDaysField = new JTextField();
        mainPanel.add(daysLabel);
        mainPanel.add(rentalDaysField);

        // Rent Button
        JButton rentButton = new JButton("Rent Car");
        rentButton.addActionListener(new RentButtonListener());
        mainPanel.add(new JLabel()); // Empty cell
        mainPanel.add(rentButton);

        // Return Button
        JButton returnButton = new JButton("Return Car");
        returnButton.addActionListener(new ReturnButtonListener());
        mainPanel.add(new JLabel()); // Empty cell
        mainPanel.add(returnButton);

        // Display Area
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        // Add panels to the frame
        add(mainPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void updateCarComboBox() {
        carComboBox.removeAllItems();
        List<Car> availableCars = rentalSystem.getAvailableCars();
        for (Car car : availableCars) {
            carComboBox.addItem(car.getCarId() + " - " + car.getBrand() + " " + car.getModel());
        }
    }

    private class RentButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String customerName = customerNameField.getText();
            String selectedCarId = carComboBox.getSelectedItem().toString().split(" - ")[0];
            int rentalDays;
            try {
                rentalDays = Integer.parseInt(rentalDaysField.getText());
            } catch (NumberFormatException ex) {
                displayArea.setText("Please enter a valid number of days.");
                return;
            }

            String customerId = rentalSystem.generateCustomerId();
            Customer newCustomer = new Customer(customerId, customerName);
            rentalSystem.addCustomer(newCustomer);

            Car selectedCar = rentalSystem.getCarById(selectedCarId);
            if (selectedCar != null) {
                rentalSystem.rentCar(selectedCar, newCustomer, rentalDays);
                double totalPrice = selectedCar.calculatePrice(rentalDays);
                displayArea.setText("Customer ID: " + newCustomer.getCustomerId() + "\n");
                displayArea.append("Customer Name: " + newCustomer.getName() + "\n");
                displayArea.append("Car: " + selectedCar.getBrand() + " " + selectedCar.getModel() + "\n");
                displayArea.append("Rental Days: " + rentalDays + "\n");
                displayArea.append("Total Price: $" + totalPrice + "\n");
                updateCarComboBox();
            } else {
                displayArea.setText("Invalid car selection.");
            }
        }
    }

    private class ReturnButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedCarId = carComboBox.getSelectedItem().toString().split(" - ")[0];
            Car carToReturn = rentalSystem.getCarById(selectedCarId);

            if (carToReturn != null && !carToReturn.isAvailable()) {
                rentalSystem.returnCar(carToReturn);
                displayArea.setText("Car returned successfully.");
                updateCarComboBox();
            } else {
                displayArea.setText("Invalid car ID or car is not rented.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CarRentalSystem rentalSystem = new CarRentalSystem();
            rentalSystem.addCar(new Car("C001", "Toyota", "Camry", 60.0));
            rentalSystem.addCar(new Car("C002", "Honda", "Accord", 70.0));
            rentalSystem.addCar(new Car("C003", "Mahindra", "Thar", 150.0));
            CarRentalSystemGUI gui = new CarRentalSystemGUI(rentalSystem);
            gui.setVisible(true);
        });
    }
}
