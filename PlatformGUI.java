import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;

public class PlatformGUI {
    static List<FoodItem> foodItems = new ArrayList<>(100);
    static List<Donor> donors = new ArrayList<>(50);
    static List<Receiver> receivers = new ArrayList<>(50);
    static List<User> admins = new ArrayList<>(10);

    private static final String ADMIN_REGISTRATION_CODE = "ADMIN123";

    private static JFrame mainFrame;
    private static CardLayout cardLayout;
    private static JPanel cardPanel;

    public static void main(String[] args) {
        admins.add(new User("admin", "Admin", "admin123"));
        donors.add(new Donor("restaurant1", "donor123"));
        receivers.add(new Receiver("ngo1", "receiver123"));

        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        mainFrame = new JFrame("Food Waste Reduction Platform");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
        mainFrame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        cardPanel.add(new LoginPanel(), "Login");
        cardPanel.add(new RegisterPanel(), "Register");
        cardPanel.add(new AdminPanel(), "Admin");
        cardPanel.add(new DonorPanel(), "Donor");
        cardPanel.add(new ReceiverPanel(), "Receiver");

        mainFrame.add(cardPanel);
        mainFrame.setVisible(true);
    }

    public static void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }

    public static void logout() {
        showPanel("Login");
    }

    static Donor findDonor(String username) {
        for (Donor donor : donors) {
            if (donor.username.equals(username)) {
                return donor;
            }
        }
        return null;
    }

    static Receiver findReceiver(String username) {
        for (Receiver receiver : receivers) {
            if (receiver.username.equals(username)) {
                return receiver;
            }
        }
        return null;
    }

    static User findAdmin(String username) {
        for (User admin : admins) {
            if (admin.username.equals(username)) {
                return admin;
            }
        }
        return null;
    }

    static class LoginPanel extends JPanel {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JComboBox<String> roleCombo;

        public LoginPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            JLabel titleLabel = new JLabel("Food Waste Reduction Platform - Login");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            add(titleLabel, gbc);

            gbc.gridwidth = 1;
            gbc.gridy++;
            add(new JLabel("Username:"), gbc);

            gbc.gridx = 1;
            usernameField = new JTextField(20);
            add(usernameField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            add(new JLabel("Password:"), gbc);

            gbc.gridx = 1;
            passwordField = new JPasswordField(20);
            add(passwordField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            add(new JLabel("Role:"), gbc);

            gbc.gridx = 1;
            String[] roles = {"Donor", "Receiver", "Admin"};
            roleCombo = new JComboBox<>(roles);
            add(roleCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            JButton loginButton = new JButton("Login");
            loginButton.addActionListener(e -> login());
            add(loginButton, gbc);

            gbc.gridy++;
            JButton registerButton = new JButton("Register New User");
            registerButton.addActionListener(e -> showPanel("Register"));
            add(registerButton, gbc);

            gbc.gridy++;
            JButton adminRegisterButton = new JButton("Register as Admin (Special Code Required)");
            adminRegisterButton.addActionListener(e -> registerAdmin());
            add(adminRegisterButton, gbc);
        }

        private void login() {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (role.equalsIgnoreCase("donor")) {
                Donor donor = findDonor(username);
                if (donor == null) {
                    int response = JOptionPane.showConfirmDialog(this,
                            "User not found! Would you like to register?", "User Not Found",
                            JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        showPanel("Register");
                    }
                    return;
                }

                if (donor.password.equals(password)) {
                    ((DonorPanel)cardPanel.getComponent(3)).setDonor(donor);
                    showPanel("Donor");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            else if (role.equalsIgnoreCase("receiver")) {
                Receiver receiver = findReceiver(username);
                if (receiver == null) {
                    int response = JOptionPane.showConfirmDialog(this,
                            "User not found! Would you like to register?", "User Not Found",
                            JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        showPanel("Register");
                    }
                    return;
                }

                if (receiver.password.equals(password)) {
                    ((ReceiverPanel)cardPanel.getComponent(4)).setReceiver(receiver);
                    showPanel("Receiver");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            else if (role.equalsIgnoreCase("admin")) {
                User admin = findAdmin(username);
                if (admin == null) {
                    JOptionPane.showMessageDialog(this, "Admin not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (admin.password.equals(password)) {
                    ((AdminPanel)cardPanel.getComponent(2)).refreshData();
                    showPanel("Admin");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        private void registerAdmin() {
            String code = JOptionPane.showInputDialog(this, "Enter admin registration code:");
            if (code == null) return;

            if (!code.equals(ADMIN_REGISTRATION_CODE)) {
                JOptionPane.showMessageDialog(this, "Invalid admin registration code!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String username = JOptionPane.showInputDialog(this, "Enter admin username:");
            if (username == null || username.trim().isEmpty()) return;

            String password = JOptionPane.showInputDialog(this, "Enter admin password:");
            if (password == null || password.trim().isEmpty()) return;

            if (findAdmin(username) != null) {
                JOptionPane.showMessageDialog(this, "Admin username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            admins.add(new User(username, "Admin", password));
            JOptionPane.showMessageDialog(this, "Admin registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    static class RegisterPanel extends JPanel {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JComboBox<String> roleCombo;

        public RegisterPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);

            JLabel titleLabel = new JLabel("User Registration");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            add(titleLabel, gbc);

            gbc.gridwidth = 1;
            gbc.gridy++;
            add(new JLabel("Username:"), gbc);

            gbc.gridx = 1;
            usernameField = new JTextField(20);
            add(usernameField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            add(new JLabel("Password:"), gbc);

            gbc.gridx = 1;
            passwordField = new JPasswordField(20);
            add(passwordField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            add(new JLabel("Role:"), gbc);

            gbc.gridx = 1;
            String[] roles = {"Donor", "Receiver"};
            roleCombo = new JComboBox<>(roles);
            add(roleCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            JButton registerButton = new JButton("Register");
            registerButton.addActionListener(e -> registerUser());
            add(registerButton, gbc);

            gbc.gridy++;
            JButton backButton = new JButton("Back to Login");
            backButton.addActionListener(e -> showPanel("Login"));
            add(backButton, gbc);
        }

        private void registerUser() {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (role.equalsIgnoreCase("donor")) {
                if (findDonor(username) != null) {
                    JOptionPane.showMessageDialog(this, "Username already exists! Please choose another.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                donors.add(new Donor(username, password));
                JOptionPane.showMessageDialog(this, "Donor registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                showPanel("Login");
            }
            else if (role.equalsIgnoreCase("receiver")) {
                if (findReceiver(username) != null) {
                    JOptionPane.showMessageDialog(this, "Username already exists! Please choose another.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                receivers.add(new Receiver(username, password));
                JOptionPane.showMessageDialog(this, "Receiver registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                showPanel("Login");
            }
        }
    }

    static class AdminPanel extends JPanel {
        private JTextArea foodItemsArea;
        private JTextArea usersArea;

        public AdminPanel() {
            setLayout(new BorderLayout());

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton logoutButton = new JButton("Logout");
            logoutButton.addActionListener(e -> logout());
            topPanel.add(logoutButton);
            add(topPanel, BorderLayout.NORTH);

            JTabbedPane tabbedPane = new JTabbedPane();

            JPanel foodPanel = new JPanel(new BorderLayout());
            foodItemsArea = new JTextArea(20, 60);
            foodItemsArea.setEditable(false);
            JScrollPane foodScroll = new JScrollPane(foodItemsArea);
            foodPanel.add(foodScroll, BorderLayout.CENTER);

            JPanel foodButtonPanel = new JPanel();
            JButton refreshFoodButton = new JButton("Refresh Food Items");
            refreshFoodButton.addActionListener(e -> refreshFoodItems());
            foodButtonPanel.add(refreshFoodButton);
            foodPanel.add(foodButtonPanel, BorderLayout.SOUTH);

            tabbedPane.addTab("Food Items", foodPanel);

            JPanel usersPanel = new JPanel(new BorderLayout());
            usersArea = new JTextArea(20, 60);
            usersArea.setEditable(false);
            JScrollPane usersScroll = new JScrollPane(usersArea);
            usersPanel.add(usersScroll, BorderLayout.CENTER);

            JPanel usersButtonPanel = new JPanel();
            JButton refreshUsersButton = new JButton("Refresh Users");
            refreshUsersButton.addActionListener(e -> refreshUsers());
            usersButtonPanel.add(refreshUsersButton);
            usersPanel.add(usersButtonPanel, BorderLayout.SOUTH);

            tabbedPane.addTab("Users", usersPanel);

            add(tabbedPane, BorderLayout.CENTER);
        }

        public void refreshData() {
            refreshFoodItems();
            refreshUsers();
        }

        private void refreshFoodItems() {
            StringBuilder sb = new StringBuilder();
            if (foodItems.isEmpty()) {
                sb.append("No food items available.\n");
            } else {
                for (FoodItem item : foodItems) {
                    sb.append(item).append("\n\n");
                }
            }
            foodItemsArea.setText(sb.toString());
        }

        private void refreshUsers() {
            StringBuilder sb = new StringBuilder();

            sb.append("-- Admins --\n");
            for (User user : admins) {
                sb.append("Username: ").append(user.username).append(", Role: ").append(user.role).append("\n");
            }

            sb.append("\n-- Donors --\n");
            for (User user : donors) {
                sb.append("Username: ").append(user.username).append(", Role: ").append(user.role).append("\n");
            }

            sb.append("\n-- Receivers --\n");
            for (User user : receivers) {
                sb.append("Username: ").append(user.username).append(", Role: ").append(user.role).append("\n");
            }

            usersArea.setText(sb.toString());
        }
    }

    static class DonorPanel extends JPanel {
        private Donor currentDonor;
        private JTextArea donationsArea;
        private JLabel pointsLabel;

        public DonorPanel() {
            setLayout(new BorderLayout());

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton logoutButton = new JButton("Logout");
            logoutButton.addActionListener(e -> logout());
            topPanel.add(logoutButton);
            add(topPanel, BorderLayout.NORTH);

            JTabbedPane tabbedPane = new JTabbedPane();

            JPanel addDonationPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;

            JLabel titleLabel = new JLabel("Add New Donation");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            addDonationPanel.add(titleLabel, gbc);

            gbc.gridwidth = 1;
            gbc.gridy++;
            addDonationPanel.add(new JLabel("Food Name:"), gbc);

            gbc.gridx = 1;
            JTextField foodNameField = new JTextField(20);
            addDonationPanel.add(foodNameField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            addDonationPanel.add(new JLabel("Quantity:"), gbc);

            gbc.gridx = 1;
            JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
            addDonationPanel.add(quantitySpinner, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            addDonationPanel.add(new JLabel("Expiry Date (DD/MM/YYYY):"), gbc);

            gbc.gridx = 1;
            JTextField expiryField = new JTextField(10);
            addDonationPanel.add(expiryField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            addDonationPanel.add(new JLabel("Food Type:"), gbc);

            gbc.gridx = 1;
            String[] types = {"Vegetables", "Fruits", "Dairy", "Grains", "Meat", "Baked Goods", "Prepared Food", "Other"};
            JComboBox<String> typeCombo = new JComboBox<>(types);
            addDonationPanel.add(typeCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.CENTER;
            JButton addButton = new JButton("Add Donation");
            addButton.addActionListener(e -> {
                String name = foodNameField.getText();
                int quantity = (Integer) quantitySpinner.getValue();
                String expdate = expiryField.getText();
                String type = (String) typeCombo.getSelectedItem();

                if (name.isEmpty() || expdate.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!isValidDate(expdate)) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid date format! Please use DD/MM/YYYY format.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                FoodItem newDonation = new FoodItem(name, quantity, expdate, currentDonor.username, type);
                currentDonor.foodItemsDonated.add(newDonation);
                foodItems.add(newDonation);
                currentDonor.addPoints(quantity * 10);

                JOptionPane.showMessageDialog(this,
                        "Donation added successfully!\nYou earned " + (quantity * 10) + " points!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                foodNameField.setText("");
                quantitySpinner.setValue(1);
                expiryField.setText("");
                refreshDonations();
                updatePoints();
            });
            addDonationPanel.add(addButton, gbc);

            tabbedPane.addTab("Add Donation", addDonationPanel);

            JPanel viewDonationsPanel = new JPanel(new BorderLayout());
            donationsArea = new JTextArea(20, 60);
            donationsArea.setEditable(false);
            JScrollPane donationsScroll = new JScrollPane(donationsArea);
            viewDonationsPanel.add(donationsScroll, BorderLayout.CENTER);

            JPanel donationsButtonPanel = new JPanel();
            JButton refreshDonationsButton = new JButton("Refresh Donations");
            refreshDonationsButton.addActionListener(e -> refreshDonations());
            donationsButtonPanel.add(refreshDonationsButton);
            viewDonationsPanel.add(donationsButtonPanel, BorderLayout.SOUTH);

            tabbedPane.addTab("My Donations", viewDonationsPanel);

            JPanel pointsPanel = new JPanel(new BorderLayout());
            pointsLabel = new JLabel("", JLabel.CENTER);
            pointsLabel.setFont(new Font("Arial", Font.BOLD, 24));
            pointsPanel.add(pointsLabel, BorderLayout.CENTER);

            JPanel pointsButtonPanel = new JPanel();
            JButton refreshPointsButton = new JButton("Refresh Points");
            refreshPointsButton.addActionListener(e -> updatePoints());
            pointsButtonPanel.add(refreshPointsButton);
            pointsPanel.add(pointsButtonPanel, BorderLayout.SOUTH);

            tabbedPane.addTab("My Points", pointsPanel);

            add(tabbedPane, BorderLayout.CENTER);
        }

        private boolean isValidDate(String dateStr) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);

            try {
                sdf.parse(dateStr);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }

        public void setDonor(Donor donor) {
            this.currentDonor = donor;
            refreshDonations();
            updatePoints();
        }

        private void refreshDonations() {
            if (currentDonor == null) return;

            StringBuilder sb = new StringBuilder();
            if (currentDonor.foodItemsDonated.isEmpty()) {
                sb.append("You haven't made any donations yet.\n");
            } else {
                sb.append("List of your donations:\n\n");
                for (int i = 0; i < currentDonor.foodItemsDonated.size(); i++) {
                    FoodItem item = currentDonor.foodItemsDonated.get(i);
                    sb.append((i+1) + ". " + item.toString()).append("\n\n");
                }
            }
            donationsArea.setText(sb.toString());
        }

        private void updatePoints() {
            if (currentDonor != null) {
                pointsLabel.setText("You have earned a total of " + currentDonor.getPoints() + " points!");
            }
        }
    }

    static class ReceiverPanel extends JPanel {
        private Receiver currentReceiver;
        private JTextArea availableFoodArea;
        private JTextArea receivedFoodArea;

        public ReceiverPanel() {
            setLayout(new BorderLayout());

            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton logoutButton = new JButton("Logout");
            logoutButton.addActionListener(e -> logout());
            topPanel.add(logoutButton);
            add(topPanel, BorderLayout.NORTH);

            JTabbedPane tabbedPane = new JTabbedPane();

            JPanel availableFoodPanel = new JPanel(new BorderLayout());
            availableFoodArea = new JTextArea(20, 60);
            availableFoodArea.setEditable(false);
            JScrollPane availableScroll = new JScrollPane(availableFoodArea);
            availableFoodPanel.add(availableScroll, BorderLayout.CENTER);

            JPanel availableButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JButton refreshAvailableButton = new JButton("Refresh Available Food");
            refreshAvailableButton.addActionListener(e -> refreshAvailableFood());
            availableButtonPanel.add(refreshAvailableButton);

            JButton selectFoodButton = new JButton("Select Food");
            selectFoodButton.addActionListener(e -> selectFood());
            availableButtonPanel.add(selectFoodButton);

            availableFoodPanel.add(availableButtonPanel, BorderLayout.SOUTH);

            tabbedPane.addTab("Available Food", availableFoodPanel);

            JPanel receivedFoodPanel = new JPanel(new BorderLayout());
            receivedFoodArea = new JTextArea(20, 60);
            receivedFoodArea.setEditable(false);
            JScrollPane receivedScroll = new JScrollPane(receivedFoodArea);
            receivedFoodPanel.add(receivedScroll, BorderLayout.CENTER);

            JPanel receivedButtonPanel = new JPanel();
            JButton refreshReceivedButton = new JButton("Refresh Received Food");
            refreshReceivedButton.addActionListener(e -> refreshReceivedFood());
            receivedButtonPanel.add(refreshReceivedButton);
            receivedFoodPanel.add(receivedButtonPanel, BorderLayout.SOUTH);

            tabbedPane.addTab("Received Food", receivedFoodPanel);

            add(tabbedPane, BorderLayout.CENTER);
        }

        public void setReceiver(Receiver receiver) {
            this.currentReceiver = receiver;
            refreshAvailableFood();
            refreshReceivedFood();
        }

        private void refreshAvailableFood() {
            StringBuilder sb = new StringBuilder();
            if (foodItems.isEmpty()) {
                sb.append("No food items available for donation.\n");
            } else {
                for (FoodItem item : foodItems) {
                    sb.append(item).append("\n\n");
                }
            }
            availableFoodArea.setText(sb.toString());
        }

        private void refreshReceivedFood() {
            if (currentReceiver == null) return;

            StringBuilder sb = new StringBuilder();
            if (currentReceiver.foodItemsReceived.isEmpty()) {
                sb.append("You haven't received any food items yet.\n");
            } else {
                for (String item : currentReceiver.foodItemsReceived) {
                    sb.append(item).append("\n\n");
                }
            }
            receivedFoodArea.setText(sb.toString());
        }

        private void selectFood() {
            if (foodItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No food items available for selection.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String foodItemName = JOptionPane.showInputDialog(this, "Enter the name of the food item you want to select:");
            if (foodItemName == null || foodItemName.trim().isEmpty()) return;

            boolean found = false;
            Iterator<FoodItem> iterator = foodItems.iterator();

            while (iterator.hasNext()) {
                FoodItem item = iterator.next();
                if (item.getName().toLowerCase().contains(foodItemName.toLowerCase())) {
                    found = true;
                    String quantStr = JOptionPane.showInputDialog(this,
                            "Available quantity: " + item.getQuantity() +
                                    "\nEnter the quantity you want:");

                    if (quantStr == null || quantStr.trim().isEmpty()) return;

                    try {
                        int quant = Integer.parseInt(quantStr);
                        if (item.getQuantity() >= quant) {
                            item.setQuantity(item.getQuantity() - quant);
                            currentReceiver.foodItemsReceived.add(item.toString() + " (Quantity received: " + quant + ")");
                            JOptionPane.showMessageDialog(this, "Food item selected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                            if (item.getQuantity() == 0) {
                                iterator.remove();
                            }

                            refreshAvailableFood();
                            refreshReceivedFood();
                        } else {
                            JOptionPane.showMessageDialog(this, "Available quantity is less than your required quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid number for quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "Food item not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

class User {
    protected String username;
    protected String role;
    protected String password;

    public User(String username, String role, String password) {
        this.username = username;
        this.role = role;
        this.password = password;
    }
}

class Receiver extends User {
    protected List<String> foodItemsReceived = new ArrayList<>(70);

    public Receiver(String username, String password) {
        super(username, "Receiver", password);
        this.foodItemsReceived = new ArrayList<>();
    }
}

class Donor extends User {
    protected List<FoodItem> foodItemsDonated = new ArrayList<>(70);
    private int points = 0;

    public Donor(String username, String password) {
        super(username, "Donor", password);
        this.foodItemsDonated = new ArrayList<>();
    }

    public void addPoints(int pointsToAdd) {
        if (pointsToAdd > 0) {
            this.points += pointsToAdd;
        }
    }

    public int getPoints() {
        return points;
    }
}

class FoodItem {
    private String name;
    private int quantity;
    private String expdate;
    private String donorName;
    private String type;
    private static int idCounter = 1000;
    private String id;

    public FoodItem(String name, int quantity, String expdate, String donorName, String type) {
        this.id = "F" + (++idCounter);
        this.name = name;
        this.quantity = quantity;
        this.expdate = expdate;
        this.donorName = donorName;
        this.type = type;
    }

    public String toString() {
        return "ID: " + id +
                ", Name: '" + name + "'" +
                ", Quantity: " + quantity +
                ", Expiry Date: '" + expdate + "'" +
                ", Type: '" + type + "'" +
                ", Donor: '" + donorName + "'";
    }

    public void setQuantity(int quant) {
        this.quantity = quant;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getName() {
        return name;
    }

    public String getDonorName() {
        return donorName;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }
}