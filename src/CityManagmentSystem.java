import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;
import javax.swing.*;
import java.awt.event.WindowEvent;

interface Alertable {
    void sendEmergencyAlert(String location, String message);
}
interface Reportable {
    void generateUsageReport();
}
//---------------------------------------Login Frame ----------------------------------------------
class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel backgroundLabel;
    private CityRepository<CityResource> repository;

    public LoginFrame(CityRepository<CityResource> repository) {
        this.repository = repository ;
        setTitle("Islamabad Management System - Login");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
          
        setupBackground(); 
        
        JPanel mainPanel = new JPanel(new FlowLayout());
        mainPanel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);
        
        JButton adminBtn = new JButton("Admin");
        adminBtn.addActionListener(e -> AdminLogin());

        
        JButton userBtn = new JButton("User");
        userBtn.addActionListener(e -> {
            new UserDashboard(repository).setVisible(true);
            dispose();
        });
        
        buttonPanel.add(adminBtn);
        buttonPanel.add(userBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }
 
    public void setupBackground() {
        try {
             ImageIcon image = new ImageIcon("images.png");
             setIconImage(image.getImage());
            ImageIcon icon = new ImageIcon("Untitled design.png"); 
            backgroundLabel = new JLabel(icon);
            backgroundLabel.setLayout(new FlowLayout());
            setContentPane(backgroundLabel);
        } catch (Exception e) {
            getContentPane().setBackground(new Color(240, 240, 240));
        }
    }
    //endofLoginframe method

public void AdminLogin() {

    JPanel loginPanel = new JPanel();
    loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
    loginPanel.setOpaque(false);
    loginPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));

    
    JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    usernamePanel.setOpaque(false);
    usernamePanel.add(new JLabel("Username:"));
    usernameField = new JTextField(15); 
    usernamePanel.add(usernameField);
    loginPanel.add(usernamePanel);
    loginPanel.add(Box.createRigidArea(new Dimension(0, 10))); 

    
    JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    passwordPanel.setOpaque(false);
    passwordPanel.add(new JLabel("Password:"));
    passwordField = new JPasswordField(15); 
    passwordPanel.add(passwordField);
    loginPanel.add(passwordPanel);
    loginPanel.add(Box.createRigidArea(new Dimension(0, 20))); 

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
    buttonPanel.setOpaque(false);
    
    JButton loginBtn = new JButton("Login");
    loginBtn.setPreferredSize(new Dimension(100, 30)); 
    loginBtn.addActionListener(this::performAdminLogin);
    buttonPanel.add(loginBtn);
    
    JButton backBtn = new JButton("Back");
    backBtn.setPreferredSize(new Dimension(100, 30)); 
    backBtn.addActionListener(e -> {
        setupBackground();
        setVisible(false);
        new LoginFrame(repository).setVisible(true);
        dispose();
    });
    buttonPanel.add(backBtn);
    
    loginPanel.add(buttonPanel);

    getContentPane().removeAll();
    getContentPane().add(loginPanel, BorderLayout.CENTER);
    revalidate();
    }
    public void performAdminLogin(ActionEvent e) {
    String username = usernameField.getText();
    String password = new String(passwordField.getPassword());
    if ("admin".equals(username) && "admin123".equals(password)) {
       
        new AdminDashboard(this.repository).setVisible(true);  
        dispose();
    } else {
        JOptionPane.showMessageDialog(this, "Invalid credentials", 
            "Login Failed", JOptionPane.ERROR_MESSAGE);
        AdminLogin();
    }
}
}
//-------------------------------------------User Dashboard-------------------------------------
class UserDashboard extends JFrame {
    private JPanel currentView;
    private JPanel mainPanel;

    private CityRepository<CityResource> repository;

    public UserDashboard(CityRepository <CityResource> repository) {

        this.repository = repository;
        setTitle("Islamabad Management System - User Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
         ImageIcon image = new ImageIcon("images.png");
        setIconImage(image.getImage());
    
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> {
            this.dispose();
            new LoginFrame(repository).setVisible(true);
        });
        

        JPanel viewButtonsPanel = new JPanel();
        viewButtonsPanel.setLayout(new BoxLayout(viewButtonsPanel, BoxLayout.X_AXIS));
        
        JButton viewAlertsBtn = new JButton("View Alerts");
        JButton generateReportsBtn = new JButton("Generate Reports");
        
        viewAlertsBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        generateReportsBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        viewAlertsBtn.addActionListener(e -> showResourcesView());
        generateReportsBtn.addActionListener(e -> showReportsView());
        
        viewButtonsPanel.add(viewAlertsBtn);
        viewButtonsPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        viewButtonsPanel.add(generateReportsBtn);
        
      
        topPanel.add(backBtn);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(viewButtonsPanel);
        
        mainPanel.add(topPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
    
        currentView = new JPanel();
        currentView.add(new JLabel("Please select an option above"));
        mainPanel.add(currentView);
        
        add(mainPanel);
    }

    public void showResourcesView() {
        mainPanel.remove(currentView);
        currentView = new UserAlertPanel(repository);
        mainPanel.add(currentView);
        revalidate();
        repaint();
    }

    public void showReportsView() {
        mainPanel.remove(currentView);
        currentView = new UserReportsPanel(repository);
        mainPanel.add(currentView);
        revalidate();
        repaint();
    }
}
//------------------------------------User Report Panel--------------------------------------
class UserReportsPanel extends JPanel{
    private CityRepository<CityResource> repository;
    private JTextArea reportOutput;
    
    public UserReportsPanel(CityRepository<CityResource> repository) {
        this.repository = repository;
        setLayout(new BorderLayout());

        JPanel optionsPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton transportReportbtn = createReportButton("Transport Usage Button");
        JButton powerReportbtn = createReportButton("Power Station Button"); 
        JButton emergencyReportbtn = createReportButton("Emergency Services Button");
        
        transportReportbtn.addActionListener(e -> generateTransportReport());
        powerReportbtn.addActionListener(e -> generatePowerReport());
        emergencyReportbtn.addActionListener(e -> generateEmergencyReport());

        optionsPanel.add(transportReportbtn);
        optionsPanel.add(powerReportbtn);
        optionsPanel.add(emergencyReportbtn);
        
        add(optionsPanel, BorderLayout.NORTH);

        reportOutput = new JTextArea();
        reportOutput.setEditable(false);
        add(new JScrollPane(reportOutput), BorderLayout.CENTER);

    }
    public void generateTransportReport(){
        StringBuilder report = new StringBuilder();
        report.append("Transport Report\n");
        report.append("---------------\n");
        for(CityResource resource : repository.getResources()){
            if(resource instanceof TransportUnit){
                TransportUnit unit = (TransportUnit) resource;
                report.append("ID: ").append(unit.getResourceID()).append("\n");
                report.append("Type: ").append(unit.getTransportType()).append("\n");
                report.append("Status: ").append(unit.getStatus()).append("\n");
                report.append("Passengers: ").append(unit.getPassengerCount()).append("\n");
                report.append("Maintenance Cost: $").append(unit.calculateMaintenanceCost()).append("\n");
                report.append("----------------------------\n");
                
                unit.generateUsageReport();
            }            
           
        }
        reportOutput.setText(report.toString());
    }
    
    public void generatePowerReport(){
        StringBuilder report = new StringBuilder();
        report.append("=== POWER STATIONS REPORT ===\n\n");

        for( CityResource resource : repository.getResources()){
            if(resource instanceof PowerStation){
                PowerStation station = (PowerStation) resource;
                report.append("ID: ").append(station.getResourceID()).append("\n");
                report.append("Location: ").append (station.getLocation());
                report.append("Output: ").append(station.getEnergyOutput()).append(" kWh\n");
                report.append("Households: ").append(station.getNumberOfHousehold()).append("\n");
                report.append("----------------------------\n");
                 station.generateUsageReport();
            }
        }
        reportOutput.setText(report.toString());
    }
    public void generateEmergencyReport(){
        StringBuilder report = new StringBuilder();
        report.append("=== EMERGENCY SERVICES REPORT ===\n\n");

for (CityResource resource : repository.getResources()) {
    if (resource instanceof EmergencyService) {
        EmergencyService service = (EmergencyService) resource;
        report.append("ID: ").append(service.getResourceID()).append("\n");
        report.append("Incident: ").append(service.getIncident_happened()).append("\n");
        report.append("People Served: ").append(service.getPeopleServed()).append("\n");
        report.append("Response Units: ").append(service.getResponseUnits()).append("\n");
        report.append("----------------------------\n");
        
        service.generateUsageReport(); 
    }
    report.append("\nAverage Response Time: ")
      .append(EmergencyService.getAverageResponseTime())
      .append(" minutes");
    }
    reportOutput.setText(report.toString());
}
 public JButton createReportButton(String text){
    JButton button = new JButton(text);
    button.setPreferredSize(new Dimension(50, 50));   
    return button;
    }
  
  }
  // ------------------------------------------User Alert Panel-----------------------------------
class UserAlertPanel extends JPanel {
    private CityRepository<CityResource> repository; 
    private JTextArea alertOutput;
    private JButton refreshBtn;

    public UserAlertPanel(CityRepository<CityResource> repository) { 
        this.repository = repository; 
        setLayout(new BorderLayout());
    
        JLabel titleLabel = new JLabel("CURRENT EMERGENCY ALERTS", SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);
        
        alertOutput = new JTextArea();
        alertOutput.setEditable(false);
        add(new JScrollPane(alertOutput), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        refreshBtn = new JButton("Refresh Alerts");
        refreshBtn.addActionListener(e -> loadAndDisplayAlerts());
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);
        
        loadAndDisplayAlerts();
    }
    
    private void loadAndDisplayAlerts() {
        StringBuilder alertsText = new StringBuilder();
        ArrayList<String> systemAlerts = EmergencyService.getSystemAlerts();
        if (!systemAlerts.isEmpty()) {
            alertsText.append("=== SYSTEM ALERTS ===\n\n");
            for (String alert : systemAlerts) {
                alertsText.append(alert).append("\n\n");
            }
        }
        
        alertsText.append("\n=== ACTIVE EMERGENCIES ===\n\n");
        boolean activeEmergencies = false;
        
        for (CityResource resource : repository.getResources()) { 
            if (resource instanceof EmergencyService) {
                EmergencyService es = (EmergencyService) resource;
                if ("On Emergency Duty".equalsIgnoreCase(es.getStatus())) {
                    alertsText.append("Service ID: ").append(es.getResourceID()).append("\n");
                    alertsText.append("Location: ").append(es.getLocation()).append("\n");
                    alertsText.append("Incident: ").append(es.getIncident_happened()).append("\n");
                    alertsText.append("Units Deployed: ").append(es.getResponseUnits()).append("\n\n");
                    activeEmergencies = true;
                }
            }
        }
        
        if (systemAlerts.isEmpty() && !activeEmergencies) {
            alertsText.append("No active emergency alerts at this time.");
        }
        
        alertOutput.setText(alertsText.toString());
        alertOutput.setCaretPosition(0); // Scroll to top
    }
}
//---------------------------------------------Admin dashboard-------------------------------------------
class AdminDashboard extends JFrame {
    private JComboBox<String> resourceCombo;
    private JTextField fuelField, passengersField, damageCostField;
    private JTextField energyOutputField, householdsField;
    private JTextField responseUnitsField, peopleServedField, incidentField;
    private JTextField idField, locationField, statusField;
    private JButton addBtn, updateBtn, deleteBtn, viewBtn;
    private CityRepository<CityResource> repository;  
    private ArrayList<String> alertHistory = new ArrayList<>();
    private JPanel formPanel;

    public AdminDashboard(CityRepository<CityResource> repository) {
        this.repository = repository;
        setupUI();
    }
    
     private void saveAndPrintChanges() {
        // Save resources
        FileOperations.saveToFile(new ArrayList<>(repository.getResources()));
        
         FileOperations.saveAlerts(new ArrayList<>(EmergencyService.getSystemAlerts()));
        // Print current state
        System.out.println("\n=== Current Resources ===");
        repository.getResources().forEach(r -> {
            System.out.println(r);
            
            if (r instanceof Reportable) {
                ((Reportable) r).generateUsageReport();
            }
        });
    }
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            // Save before closing
            saveAndPrintChanges();
            super.processWindowEvent(e);
        } else {
            super.processWindowEvent(e);
        }
    }
    public void setupUI() {
        setTitle("Resource Manager");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         ImageIcon image = new ImageIcon("images.png");
             setIconImage(image.getImage());

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        
        idField = new JTextField(15);
        locationField = new JTextField(15);
        statusField = new JTextField(15);
        fuelField = new JTextField(15);
        passengersField = new JTextField(15);
        damageCostField = new JTextField(15);
        energyOutputField = new JTextField(15);
        householdsField = new JTextField(15);
        responseUnitsField = new JTextField(15);
        peopleServedField = new JTextField(15);
        incidentField = new JTextField(15);
        
        resourceCombo = new JComboBox<>(new String[]{"Transport", "Power", "Emergency"});
        resourceCombo.addActionListener(e -> updateFormFields());
        
        updateFormFields();
        
        // Button panel setup
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 10, 5));
        addBtn = new JButton("Add");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        viewBtn = new JButton("ViewResources");

        addBtn.addActionListener(this::addResource);
        updateBtn.addActionListener(this::updateResource);
        deleteBtn.addActionListener(this::deleteResource);
        viewBtn.addActionListener(e -> viewResource());

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(viewBtn);

        JButton emergencyAlertBtn = new JButton("Send Emergency Alert");
        emergencyAlertBtn.setBackground(new Color(144, 238, 144));
        emergencyAlertBtn.addActionListener(this::showEmergencyAlertDialog);
        buttonPanel.add(emergencyAlertBtn);

        JButton historyBtn = new JButton("View Alert History");
        historyBtn.setBackground(new Color(144, 238, 144));
        historyBtn.addActionListener(e -> showAlertHistory());
        buttonPanel.add(historyBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBackground(new Color(173, 216, 230));
        backBtn.addActionListener(e -> {
            this.dispose();
            new LoginFrame(repository).setVisible(true);         
        });
        buttonPanel.add(backBtn);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void updateFormFields() {
        formPanel.removeAll();
        
        formPanel.add(createFieldPanel("Resource ID:", idField));
        formPanel.add(createFieldPanel("Location:", locationField));
        formPanel.add(createFieldPanel("Status:", statusField));
        
        String selectedType = (String)resourceCombo.getSelectedItem();
        
        if ("Transport".equals(selectedType)) {
            formPanel.add(createFieldPanel("Fuel Consumption (L):", fuelField));
            formPanel.add(createFieldPanel("Passenger Count:", passengersField));
            formPanel.add(createFieldPanel("Damage Cost ($):", damageCostField));
        } 
        else if ("Power".equals(selectedType)) {
            formPanel.add(createFieldPanel("Energy Output (kWh):", energyOutputField));
            formPanel.add(createFieldPanel("Number of Households:", householdsField));
        }
        else if ("Emergency".equals(selectedType)) {
            formPanel.add(createFieldPanel("Response Units:", responseUnitsField));
            formPanel.add(createFieldPanel("People Served:", peopleServedField));
            formPanel.add(createFieldPanel("Incident Type:", incidentField));
        }
        
        formPanel.add(createFieldPanel("Resource Type:", resourceCombo));
        
        formPanel.revalidate();
        formPanel.repaint();
    }
 
    private JPanel createFieldPanel(String label, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label));
        panel.add(field);
        return panel;
    }

    public void showEmergencyAlertDialog(ActionEvent e) {
        JDialog alertDialog = new JDialog(this, "Send Emergency Alert", true);
        alertDialog.setSize(400, 300);
        alertDialog.setLayout(new GridLayout(5, 2, 10, 10));
        
        JTextField locationField = new JTextField();
        JTextField resourcesField = new JTextField();
        JTextField timeField = new JTextField();
        JComboBox<String> emergencyTypeCombo = new JComboBox<>(new String[]{
            "Police Emergency", 
            "Fire Emergency", 
            "Ambulance Required",
            "General Emergency Responders"
        });
        
        alertDialog.add(new JLabel("Resources Type"));
        alertDialog.add(emergencyTypeCombo); 
        alertDialog.add(new JLabel("Location:"));
        alertDialog.add(locationField);
        alertDialog.add(new JLabel("Resources Needed:"));
        alertDialog.add(resourcesField);
        alertDialog.add(new JLabel("Response Time (mins):"));
        alertDialog.add(timeField);
        
        JButton sendBtn = new JButton("Send Alert");
        sendBtn.addActionListener(evt -> {
            sendEmergencyAlert(
                locationField.getText(),
                resourcesField.getText(),
                timeField.getText(),
                (String)emergencyTypeCombo.getSelectedItem()
            );
            alertDialog.dispose();
        });
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(evt -> alertDialog.dispose());
        
        alertDialog.add(sendBtn);
        alertDialog.add(cancelBtn);
        
        alertDialog.setVisible(true);
    }
    
    private CityResource createResource(String type, String id, String location, String status) {
        switch (type) {
            case "Transport":
                double fuel = Double.parseDouble(fuelField.getText());
                int passengers = Integer.parseInt(passengersField.getText());
                double damage = Double.parseDouble(damageCostField.getText());
                TransportUnit transport = new TransportUnit(id, location, status, fuel, passengers);
                transport.setDamageCost(damage);
                return transport;
            case "Power":
                double output = Double.parseDouble(energyOutputField.getText());
                int households = Integer.parseInt(householdsField.getText());
                return new PowerStation(id, location, status, output, households);
            case "Emergency":
                int units = Integer.parseInt(responseUnitsField.getText());
                int people = Integer.parseInt(peopleServedField.getText());
                String incident = incidentField.getText();
                EmergencyService service = new EmergencyService(id, location, status, units, people, 0, incident);
                return service;
            default:
                throw new IllegalArgumentException("Invalid resource type: " + type);
        }
    }

    private void sendEmergencyAlert(String resourcestype, String location, String resources, String time) {
    String alertRecord = String.format("[%s] %s at %s\nResources: %s\nResponse Time: %s mins",
        new Date().toString(),
        resourcestype,
        location,
        resources,
        time
    );

    // Add to both local and system-wide alerts
    alertHistory.add(alertRecord);
    EmergencyService.addSystemAlert(alertRecord);

    // Update emergency services
    for (CityResource resource : repository.getResources()) {
        if (resource instanceof EmergencyService && 
            resource.getLocation().equalsIgnoreCase(location)) {
            resource.setStatus("On Emergency Duty");
            ((EmergencyService) resource).setIncident_happened(resourcestype);
            ((EmergencyService) resource).setResponseTime(Double.parseDouble(time));
            
            // Also update the transport units if needed
        } else if (resource instanceof TransportUnit && 
                   resource.getLocation().equalsIgnoreCase(location)) {
            resource.setStatus("On Emergency Duty");
        }
    }

    // Save changes
    saveAndPrintChanges();
    JOptionPane.showMessageDialog(this, "Alert sent successfully!");
}
    private void showAlertHistory() {
        JDialog historyDialog = new JDialog(this, "Alert History", true);
        historyDialog.setSize(600, 400);
        
        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        
        if (alertHistory.isEmpty()) {
            historyArea.setText("No alerts have been sent yet.");
        } else {
            historyArea.setText(String.join("\n\n=====\n\n", alertHistory));
        }
        
        historyDialog.add(new JScrollPane(historyArea));
        historyDialog.setVisible(true);
    }

    private void addResource(ActionEvent e) {
        String id = idField.getText();
        String location = locationField.getText();
        String status = statusField.getText();
        String type = (String) resourceCombo.getSelectedItem();
        
        if (repository == null) {  
            JOptionPane.showMessageDialog(this, "Repository not initialized!", 
                "System Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (id.isEmpty() || location.isEmpty() || status.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        try {
            CityResource resource = createResource(type, id, location, status);
            repository.addResource(resource);  
            saveAndPrintChanges();
            JOptionPane.showMessageDialog(this, "Resource added!");
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for numeric fields", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateResource(ActionEvent e) {
        String id = idField.getText();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter ID to update");
            return;
        }

        boolean found = false;
        for (CityResource res : repository.getResources()) {
            if (res.getResourceID().equals(id)) {
                res.setLocation(locationField.getText());
                res.setStatus(statusField.getText());
                
                if (res instanceof TransportUnit) {
                    TransportUnit tu = (TransportUnit) res;
                    tu.setFuelConsumption(Double.parseDouble(fuelField.getText()));
                    tu.setPassengerCount(Integer.parseInt(passengersField.getText()));
                    tu.setDamageCost(Double.parseDouble(damageCostField.getText()));
                } 
                else if (res instanceof PowerStation) {
                    PowerStation ps = (PowerStation) res;
                    ps.setEnergyOutput(Double.parseDouble(energyOutputField.getText()));
                    ps.setNumberOfHousehold(Integer.parseInt(householdsField.getText()));
                }
                else if (res instanceof EmergencyService) {
                    EmergencyService es = (EmergencyService) res;
                    es.setResponseUnits(Integer.parseInt(responseUnitsField.getText()));
                    es.setPeopleServed(Integer.parseInt(peopleServedField.getText()));
                    es.setIncident_happened(incidentField.getText());
                }
                
                found = true;
                break;
            }
        }

        if (found) {
            saveAndPrintChanges(); 
            JOptionPane.showMessageDialog(this, "Resource updated!");
        } else {
            JOptionPane.showMessageDialog(this, "Resource not found");
        }
    }

    public void deleteResource(ActionEvent e) {
        String id = idField.getText();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter ID to delete");
            return;
        }

        if (repository.getResources().removeIf(res -> res.getResourceID().equals(id))) {
            saveAndPrintChanges();
            JOptionPane.showMessageDialog(this, "Resource deleted!");
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Resource not found");
        }
    }
      private void viewResource() {
        String id = idField.getText();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an ID to view");
            return;
        }

    CityResource foundResource = null;
    for (CityResource res : repository.getResources()) {
        if (res.getResourceID().equalsIgnoreCase(id)) {
            foundResource = res;
            break; 
        }
    }    
    if (foundResource != null) {
        showResourceDetails(foundResource);
    } else {
        JOptionPane.showMessageDialog(this, "No resource found with ID: " + id, 
                                    "Not Found", JOptionPane.INFORMATION_MESSAGE);
    }
    }

    public void showResourceDetails(CityResource resource) {
        JDialog detailsDialog = new JDialog(this, "Resource Details", true);
        detailsDialog.setSize(500, 400);
        detailsDialog.setLayout(new BorderLayout());

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setText(fRD(resource));

        JScrollPane scrollPane = new JScrollPane(detailsArea);
        detailsDialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> detailsDialog.dispose());
        detailsDialog.add(closeBtn, BorderLayout.SOUTH);

        detailsDialog.setVisible(true);
    }


    public String fRD(CityResource resource) {
        StringBuilder sb = new StringBuilder();
        sb.append("RESOURCE DETAILS \n\n");

        sb.append("ID: ").append(resource.getResourceID()).append("\n");
        sb.append("Location: ").append(resource.getLocation()).append("\n");
        sb.append("Status: ").append(resource.getStatus()).append("\n");
        
        if (resource instanceof TransportUnit) {
            TransportUnit tu = (TransportUnit) resource;
            sb.append("\nType: Transport Unit\n");
            sb.append("Transport Type: ").append(tu.getTransportType()).append("\n");
            sb.append("Fuel Consumption: ").append(tu.getFuelConsumption()).append(" L\n");
            sb.append("Passenger Count: ").append(tu.getPassengerCount()).append("\n");
            sb.append("Damage Cost: $").append(tu.getDamageCost()).append("\n");
            sb.append("Maintenance Cost: $").append(tu.calculateMaintenanceCost()).append("\n");
        } 
        else if (resource instanceof PowerStation) {
            PowerStation ps = (PowerStation) resource;
            sb.append("\nType: Power Station\n");
            sb.append("Energy Output: ").append(ps.getEnergyOutput()).append(" kWh\n");
            sb.append("Households Served: ").append(ps.getNumberOfHousehold()).append("\n");
            sb.append("Maintenance Cost: $").append(ps.calculateMaintenanceCost()).append("\n");
        }
        else if (resource instanceof EmergencyService) {
            EmergencyService es = (EmergencyService) resource;
            sb.append("\nType: Emergency Service\n");
            sb.append("Response Units: ").append(es.getResponseUnits()).append("\n");
            sb.append("People Served: ").append(es.getPeopleServed()).append("\n");
            sb.append("Incident Type: ").append(es.getIncident_happened()).append("\n");
            sb.append("Maintenance Cost: $").append(es.calculateMaintenanceCost()).append("\n");
        }
        
        return sb.toString();
    }

    public void clearFields() {
        idField.setText("");
        locationField.setText("");
        statusField.setText("");
        fuelField.setText("");
        passengersField.setText("");
        damageCostField.setText("");
        energyOutputField.setText("");
        householdsField.setText("");
        responseUnitsField.setText("");
        peopleServedField.setText("");
        incidentField.setText("");
    }
}
// -----------------------------------City Resources done ------------------------------------------------
abstract class CityResource implements Serializable {
    protected String resourceID;
    protected String location;
    protected String status;

    CityResource() {}
    CityResource(String resourceID, String location, String status) {
        this.resourceID = resourceID;
        this.location = location;
        this.status = status;
    }

    public void setLocation(String location) {
        this.location = location;
    }public void setStatus(String status) {
        this.status = status;
    }public String getResourceID() {
        return resourceID;
    }public String getLocation() {
        return location;
    }public String getStatus() {
        return status;
    }

    public abstract double calculateMaintenanceCost();
    
    public String toString() {
        return "======City Resources======\n"
                + "Resource ID: " + resourceID + "\n"
                + "Location: " + location + "\n"
                + "Status: " + status;
    }
}
//----------------------------------------Transport unit class done-----------------------------------------
class TransportUnit extends CityResource implements Alertable, Reportable {
    private double fuelConsumption;
    private int passengerCount;
    private double maintenanceCost;
    private double damageCost;
    private String transportType;

    TransportUnit() {}
    TransportUnit(double fuelConsumption, int passengerCount) {
        this.fuelConsumption = fuelConsumption;
        this.passengerCount = passengerCount;
    }
    TransportUnit(String resourceID, String location, String status, double fuelConsumption, int passengerCount) {
        super(resourceID, location, status); 
        this.fuelConsumption = fuelConsumption;
        this.passengerCount = passengerCount;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }public void setFuelConsumption(double f) {
        fuelConsumption = f;
    }public void setPassengerCount(int p) {
        passengerCount = p;
    }public double getFuelConsumption() {
        return fuelConsumption;
    }public int getPassengerCount() {
        return passengerCount;
    }public void setDamageCost(double damageCost) {
        this.damageCost = damageCost;
    }public double getDamageCost() {
        return damageCost;
    }public void setTransportType(String transportType) {
        this.transportType = transportType;
    }public String getTransportType() {
        return transportType;
    }

    public double calculateMaintenanceCost() {
        maintenanceCost = fuelConsumption * damageCost * 1000;
        return maintenanceCost;
    }
      public void sendEmergencyAlert(String location, String message) {
        System.out.println("TRANSPORT ALERT RECEIVED AT " + location);
        System.out.println(message);
        System.out.println("Redirecting transport unit " + resourceID + "...");
         
        if (this.status.equalsIgnoreCase("Active")) {
            this.status = "On Emergency Duty";
        }
    }
    public String toString() {
        return super.toString() + "\n" +
                "FuelConsumption: " + fuelConsumption + "L\n" +
                "Passengers: " + passengerCount + "\n" +
                "DamageCost: $" + damageCost;
    }

    public void generateUsageReport() {
        System.out.println("Usage Report for Transport Unit: " + resourceID + "\nPassenger Count: " + passengerCount);
    }
}
//---------------------------------------------Power Station done ------------------------------------------
class PowerStation extends CityResource implements Reportable {
    private double energyOutput;
    private int numberOfHouseholds;

    PowerStation() {}
    PowerStation(String resourceID, String location, String status, double energyOutput, int numberOfHouseholds) {
        super(resourceID, location, status);
        this.energyOutput = energyOutput;
        this.numberOfHouseholds = numberOfHouseholds;
    }

    public void setEnergyOutput(double energyOutput) {
        this.energyOutput = energyOutput;
    }public double getEnergyOutput() {
        return energyOutput;
    }public void setNumberOfHousehold(int numberOfHouseholds) {
        this.numberOfHouseholds = numberOfHouseholds;
    }public int getNumberOfHousehold() {
        return numberOfHouseholds;
    }

    public double calculateMaintenanceCost() {
        return energyOutput * 0.2;
    }
    public String toString() {
        return super.toString() + "\n" +
                "EnergyOutput: " + energyOutput + " kWh\n" +
                "Households: " + numberOfHouseholds;


            
    }
    public void generateUsageReport() {
        System.out.println("Energy Usage Report for Power Station: " + resourceID + "\nEnergy Output: " + energyOutput);
    }
}
//-----------------------------------------------Emergency Service class----------------------------------------
class EmergencyService extends CityResource implements Alertable, Reportable {
    private static int totalResponses = 0;              //------- static data members
    private static double totalResponseTime = 0;

    private int responseUnits;
    private int peopleServed;
    private double repairCost;
    private String incident_happened;
    private double thisResponseTime;

    EmergencyService() {totalResponses++;}
    EmergencyService(String resourceID, String location, String status, int responseUnits, int peopleServed, double repairCost, String incident_happened) {
        super(resourceID, location, status);
        this.responseUnits = responseUnits;
        this.peopleServed = peopleServed;
        this.repairCost = repairCost;
        this.incident_happened = incident_happened;
        totalResponses++;
    }
    private static ArrayList<String> systemAlerts = new ArrayList<>(); 

    public static void addSystemAlert(String alert) {
        systemAlerts.add(alert);
    }
    
    public static ArrayList<String> getSystemAlerts() {
        return new ArrayList<>(systemAlerts); // Return a copy for thread safety
    }
    public void setResponseTime(double minutes) {
        thisResponseTime = minutes;
        totalResponseTime += minutes;
    }
    public static double getAverageResponseTime() {     //------------static method
        if (totalResponses == 0) {
            return 0;
        } else {
            return totalResponseTime / totalResponses;
        }
    }
      public void sendEmergencyAlert(String location, String message) {
        System.out.println("EMERGENCY ALERT RECEIVED AT " + location);
        System.out.println(message);
        System.out.println("Dispatching " + this.responseUnits + " units...");
        
        JOptionPane.showMessageDialog(null, 
            "EMERGENCY ALERT!\n" + message,
            "Emergency Dispatch - " + resourceID,
            JOptionPane.WARNING_MESSAGE);
    }
    public void whichServiceIsNeeded() {
        if (incident_happened.equalsIgnoreCase("fire")) {
            System.out.println("Fire Department on its way.");
        } else if (incident_happened.equalsIgnoreCase("medical")) {
            System.out.println("Medical Emergency Team dispatched.");
        } else if (incident_happened.equalsIgnoreCase("police")) {
            System.out.println("Police Department responding.");
        } else if (incident_happened.equalsIgnoreCase("accident")) {
            System.out.println("Emergency units dispatched to the accident site.");
        } else {
            System.out.println("Unknown incident type. Dispatching general emergency response.");
        }
    }

    public int getResponseUnits() {
        return responseUnits;
    }public void setResponseUnits(int responseUnits) {
        this.responseUnits = responseUnits;
    }public int getPeopleServed() {
        return peopleServed;
    }public void setPeopleServed(int peopleServed) {
        this.peopleServed = peopleServed;
    }public double getRepairCost() {
        return repairCost;
    }public void setRepairCost(double repairCost) {
        this.repairCost = repairCost;
    }public void setIncident_happened (String str){
        incident_happened = str ;
    }public String getIncident_happened (){
        return incident_happened ;
    }

    public double calculateMaintenanceCost() {
        return responseUnits * 1000 + repairCost;
    }
    
    public void generateUsageReport() {
        System.out.println("Usage Report for Emergency Service: " + resourceID);
        System.out.println("People Served: " + peopleServed);
        System.out.println("This Incident Response Time: " + thisResponseTime + " minutes");
        System.out.println("City-Wide Average Response Time: " + getAverageResponseTime() + " minutes");
    }
    public String toString() {
        return super.toString() + "\n" +
                "ResponseUnits: " + responseUnits + "\n" +
                "PeopleServed: " + peopleServed + "\n" +
                "RepairCost: $" + repairCost + "\n" +
                "Incident Type: " + incident_happened + "\n" +
                "Response Time (this): " + thisResponseTime + " minutes";
    }
}
//----------------------------------------------Resource Hub--------------------------------------------
class ResourceHub {
    private String hubID;
    private String hubName;
    private String location;
    private ArrayList<TransportUnit> transportUnits = new ArrayList<>();

    public ResourceHub(){}
    public ResourceHub(String hubID, String hubName ,String location){
        this.hubName = hubName ;
        this.hubID = hubID ;
        this.location = location ;
    }
    public String getHubID() {
        return hubID;
    }public String getHubName() {
        return hubName;
    }public String getLocation() {
        return location;
    }public void setHubName(String hubName) {
        this.hubName = hubName;
    }public void setLocation(String location) {
        this.location = location;
    }
    
    public void addTransportUnit(TransportUnit unit) {
        transportUnits.add(unit);
    }
    public void removeTransportUnit (TransportUnit unit){
        transportUnits.remove(unit);
    }
    public String toString() {
        String units = "";
        for (TransportUnit unit : transportUnits) {
            units += "\n  - " + unit.getTransportType() ;
        }
        return "ResourceHub " + hubID + " (" + hubName + ") at " + location +
            "\nTransport Units: " + transportUnits.size() + units;
    }

}
//------------------------------------------City Zone-------------------------------------------------
class CityZone {
    private String zoneID;
    private String zoneName;
    private String location;  
    private ArrayList<ResourceHub> resourceHubs = new ArrayList<>();

    CityZone(){}
    public CityZone(String zoneID, String zoneName, String location) {
        this.zoneID = zoneID;
        this.zoneName = zoneName;
        this.location = location;
    }

    public void addResource (ResourceHub r){
        resourceHubs.add(r);
        System.out.println("Resource "+r.getHubName()+" successfully added. ");
    }
    public void removeResource (ResourceHub r){
        resourceHubs.remove(r);
        System.out.println("Resource "+r.getHubName()+" successfully removed. ");
    }
    public void searchResourceHub (ResourceHub r){
        for (ResourceHub hub : resourceHubs ){
            if (hub.getHubID().equalsIgnoreCase(r.getHubID())){
                System.out.println("Resource hub found ! ");
            }
        }
    }
    public String toString() {
        String result = "CityZone ID: " + zoneID +
                        "\nName: " + zoneName +
                        "\nLocation: " + location +
                        "\nResource Hubs:\n";

        for (ResourceHub hub : resourceHubs) {
            result += hub.toString() + "\n------------------\n";
        }

        return result;
    }
}
//--------------------------------------------Consumer class -------------------------------------------
class Consumer {
    private String consumerID;
    private String type; 
    private double energyConsumption; 

    public Consumer(String consumerID, String type, double energyConsumption) {
        this.consumerID = consumerID;
        this.type = type;
        this.energyConsumption = energyConsumption;
    }

    public double getEnergyConsumption() {
        return energyConsumption;
    }
    public String toString() {
        return "Consumer " + consumerID + " (" + type + ") consumes " + energyConsumption + " kWh";
    }
}
//--------------------------------------------Smart Grid ------------------------------------------------
class SmartGrid {
    private ArrayList <PowerStation> stations = new ArrayList<>();
    private ArrayList <Consumer> consumers = new ArrayList<>();
    private static double totalEnergyConsumed = 0 ;         // ---------static member 

    public void addPowerStation(PowerStation ps) {
        stations.add(ps);
    }
    public void addConsumer(Consumer c) {
        consumers.add(c);
        totalEnergyConsumed += c.getEnergyConsumption();
    }
    public  double energyConsumptionTracking() {
        return totalEnergyConsumed;
    }
    public double getTotalEnergyOutput() {
        double total = 0;
        for (PowerStation ps : stations) {
            total += ps.getEnergyOutput();
        }
        return total;
    }
    public double getTotalConsumption() {
        double total = 0;
        for (Consumer c : consumers) {
            total += c.getEnergyConsumption();
        }
        return total;
    }
     public String toString() {
        String result = "========= Smart Grid Overview =========\n";
        result += "\n-- Power Stations --\n";
        for (PowerStation ps : stations) {
            result += "• ID: " + ps.getResourceID() + ", Output: " + ps.getEnergyOutput() + " kWh\n";
        }
        result += "\n-- Consumers --\n";
        for (Consumer c : consumers) {
            result += "• " + c.toString() + "\n";
        }
        result += "\nTotal Energy Output: " + getTotalEnergyOutput() + " kWh\n";
        result += "Total Consumption: " + getTotalConsumption() + " kWh\n";
        result += "Net Energy Available: " + energyConsumptionTracking() + " kWh\n";
        return result;
    }
}
//----------------------------------------File Operations-------------------------------------------- 

//----------------------------------------File Operations-------------------------------------------- 
class FileOperations implements Serializable {
    private static final String RESOURCES_FILE = "resources.dat";
    private static final String SMART_GRID_FILE = "smart_grid.dat";
    private static final String ALERTS_FILE = "alerts.dat";
    
    // Add these methods
    public static void saveAlerts(ArrayList<String> alerts) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ALERTS_FILE))) {
            out.writeObject(alerts);
        } catch (IOException e) {
            System.out.println("Error saving alerts: " + e.getMessage());
        }
    }
    
    public static ArrayList<String> loadAlerts() {
        File file = new File(ALERTS_FILE);
        if (!file.exists()) return new ArrayList<>();
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (ArrayList<String>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading alerts: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    // Modified saveToFile method to properly overwrite existing file
    public static void saveToFile(ArrayList<CityResource> resources) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(RESOURCES_FILE))) {
            out.writeObject(resources);
            System.out.println("=== Saved " + resources.size() + " resources to file ===");
            for (CityResource r : resources) {
                System.out.println("Saved: " + r.toString());
            }
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    // Modified loadFromFile method
    public static ArrayList<CityResource> loadFromFile() {
        File file = new File(RESOURCES_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            ArrayList<CityResource> loaded = (ArrayList<CityResource>) in.readObject();
            System.out.println("=== Loaded " + loaded.size() + " resources from file ===");
            return loaded;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Load error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Smart Grid file operations
    public static void saveSmartGrid(ArrayList<SmartGrid> grids) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SMART_GRID_FILE))) {
            out.writeObject(grids);
            System.out.println("=== Saved smart grid data ===");
        } catch (IOException e) {
            System.out.println("Smart grid save error: " + e.getMessage());
        }
    }
    
    public static ArrayList<SmartGrid> loadSmartGrid() {
        File file = new File(SMART_GRID_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (ArrayList<SmartGrid>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Smart grid load error: " + e.getMessage());
            return new ArrayList<>();
        }//
    }
}
//---------------------------------------------------Myobjectoutput class--------------------------
class MyObjectOutput extends ObjectOutputStream {
    public MyObjectOutput(OutputStream out) throws IOException {
        super(out);
    }
    protected void writeStreamHeader() throws IOException {
        reset();  
    }
}
//------------------------------------------City Repository--------------------------------------
class CityRepository<T extends CityResource> {
    private ArrayList<T> resources = new ArrayList<>();

    public void addResource(T resource) {
        resources.add(resource);
    }public ArrayList<T> getResources() {
        return resources;
    }public void updateResource(T updatedResource) {
        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i).getResourceID().equals(updatedResource.getResourceID())) {
                resources.set(i, updatedResource);
                break;
            }
        }
    }
    public int countTransportUnitsByType(String type) {
        int count = 0;
        for (T resource : resources) {
            if (resource instanceof TransportUnit) {
                TransportUnit tu = (TransportUnit) resource;
                String transportType = tu.getTransportType();
                if (transportType != null && transportType.equalsIgnoreCase(type)) {
                    count++;
                }
            }
        }
        return count;
    }
    public int countOperationalPowerStations() {
        int count = 0;
        for (T resource : resources) {
            if (resource instanceof PowerStation) {
                PowerStation ps = (PowerStation) resource;
                String status = ps.getStatus();
                if (status != null && status.equalsIgnoreCase("operational")) {
                    count++;
                }
            }
        }
        return count;
    }
    public int countEmergencyServicesByStatus(String status) {
        int count = 0;
        for (T resource : resources) {
            if (resource instanceof EmergencyService) {
                EmergencyService es = (EmergencyService) resource;
                String esStatus = es.getStatus();
                if (esStatus != null && esStatus.equalsIgnoreCase(status)) {
                    count++;
                }
            }
        }
        return count;
    }
}
//-------------------------------------------------Runner class-----------------------------------
public class CityManagmentSystem {
    public static void main(String[] args) {
        CityRepository<CityResource> repository = new CityRepository<>();
        ArrayList<String> savedAlerts = FileOperations.loadAlerts();
        for (String alert : savedAlerts) {
            EmergencyService.addSystemAlert(alert);
        }
        // Load existing resources
        ArrayList<CityResource> loadedResources = FileOperations.loadFromFile();
        if (!loadedResources.isEmpty()) {
            loadedResources.forEach(repository::addResource);
            System.out.println("Successfully loaded " + loadedResources.size() + " resources");
        } else {
            System.out.println("No existing data found, creating sample resources");

            // ----------------- 7 Transport Units ------------------
            TransportUnit bus = new TransportUnit("T001", "Main Street", "Active", 10.5, 35);
            bus.setTransportType("Bus");
            bus.setDamageCost(1.5);

            TransportUnit ambulance = new TransportUnit("T002", "Health Avenue", "Active", 5.0, 5);
            ambulance.setTransportType("Ambulance");
            ambulance.setDamageCost(0.8);

            TransportUnit metro = new TransportUnit("T003", "Station Plaza", "Operational", 50.0, 120);
            metro.setTransportType("Metro");
            metro.setDamageCost(2.0);

            TransportUnit tram = new TransportUnit("T004", "Old Town", "Maintenance", 20.0, 60);
            tram.setTransportType("Tram");
            tram.setDamageCost(1.2);

            TransportUnit taxi = new TransportUnit("T005", "Downtown", "Active", 7.0, 4);
            taxi.setTransportType("Taxi");
            taxi.setDamageCost(0.5);

            TransportUnit fireTruck = new TransportUnit("T006", "Fire HQ", "Available", 12.0, 8);
            fireTruck.setTransportType("Fire Truck");
            fireTruck.setDamageCost(2.5);

            TransportUnit cargoTruck = new TransportUnit("T007", "Warehouse", "Idle", 25.0, 2);
            cargoTruck.setTransportType("Cargo");
            cargoTruck.setDamageCost(3.0);

            // ----------------- 7 Power Stations ------------------
            PowerStation plant1 = new PowerStation("P001", "Industrial Zone", "Operational", 10000, 2000);
            PowerStation plant2 = new PowerStation("P002", "River Side", "Maintenance", 8000, 1500);
            PowerStation plant3 = new PowerStation("P003", "Hill Top", "Active", 12000, 1800);
            PowerStation plant4 = new PowerStation("P004", "Green Area", "Offline", 5000, 700);
            PowerStation plant5 = new PowerStation("P005", "Tech Park", "Operational", 9000, 1600);
            PowerStation plant6 = new PowerStation("P006", "North Grid", "Active", 11000, 1900);
            PowerStation plant7 = new PowerStation("P007", "West Field", "Standby", 9500, 1300);

            // ----------------- 7 Emergency Services ------------------
            EmergencyService hospital = new EmergencyService("E001", "Central Hospital", "Available", 3, 6, 8, "Medical");
            EmergencyService fireStation = new EmergencyService("E002", "Fire Station", "On Call", 4, 5, 6, "Fire");
            EmergencyService policeStation = new EmergencyService("E003", "Police HQ", "Standby", 5, 7, 9, "Law Enforcement");
            EmergencyService rescueTeam = new EmergencyService("E004", "Rescue Base", "Active", 6, 4, 5, "Rescue");
            EmergencyService disasterRelief = new EmergencyService("E005", "Relief Center", "Active", 3, 2, 4, "Disaster");
            EmergencyService traumaUnit = new EmergencyService("E006", "Trauma Unit", "Available", 2, 3, 3, "Medical");
            EmergencyService floodResponse = new EmergencyService("E007", "Flood Response Team", "Deployed", 4, 4, 4, "Disaster");

            // Add all to repository
            repository.addResource(bus);
            repository.addResource(ambulance);
            repository.addResource(metro);
            repository.addResource(tram);
            repository.addResource(taxi);
            repository.addResource(fireTruck);
            repository.addResource(cargoTruck);

            repository.addResource(plant1);
            repository.addResource(plant2);
            repository.addResource(plant3);
            repository.addResource(plant4);
            repository.addResource(plant5);
            repository.addResource(plant6);
            repository.addResource(plant7);

            repository.addResource(hospital);
            repository.addResource(fireStation);
            repository.addResource(policeStation);
            repository.addResource(rescueTeam);
            repository.addResource(disasterRelief);
            repository.addResource(traumaUnit);
            repository.addResource(floodResponse);

            FileOperations.saveToFile(new ArrayList<>(repository.getResources()));
        }

     
        LoginFrame login = new LoginFrame(repository);
        login.setVisible(true);
    }
}