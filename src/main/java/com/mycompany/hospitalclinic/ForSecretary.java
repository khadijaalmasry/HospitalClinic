/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.hospitalclinic;

import static java.lang.Integer.parseInt;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import oracle.jdbc.pool.OracleDataSource;

/**
 *
 * @author user
 */
public class ForSecretary extends javax.swing.JFrame {

    /**
     * Creates new form ForSecretary
     */
  DefaultListModel<String> timeSlotsModel;
      // Define a method to update the available time slots
private void updateAvailableTimeSlots() {
    // Get the selected day, month, and year from the combo boxes
    Object selectedDayObj = jComboBox3.getSelectedItem();
    Object selectedMonthObj = jComboBox4.getSelectedItem();
    Object selectedYearObj = jComboBox5.getSelectedItem();

    // Check if any of the selected items are null
    if (selectedDayObj == null || selectedMonthObj == null || selectedYearObj == null) {
        // Handle the case when a selection is missing
        JOptionPane.showMessageDialog(null, "Please select a day, month, and year", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Check if the selected day is not specified (e.g., "Select Day")
    String selectedDayStr = selectedDayObj.toString();
    if (selectedDayStr.equalsIgnoreCase("Select Day")) {
        // Set the selected day as today's day
        LocalDate today = LocalDate.now();
        selectedDayStr = String.valueOf(today.getDayOfMonth());
    }

    // Parse the selected day as an integer
    int selectedDay;
    try {
        selectedDay = Integer.parseInt(selectedDayStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Invalid selected day", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Map the month name to its corresponding Month enum value
    String selectedMonth = selectedMonthObj.toString();
    Month selectedMonthEnum;
    switch (selectedMonth) {
        case "JAN":
            selectedMonthEnum = Month.JANUARY;
            break;
        case "FEB":
            selectedMonthEnum = Month.FEBRUARY;
            break;
        case "MAR":
            selectedMonthEnum = Month.MARCH;
            break;
        case "APR":
            selectedMonthEnum = Month.APRIL;
            break;
        case "MAY":
            selectedMonthEnum = Month.MAY;
            break;
        case "JUN":
            selectedMonthEnum = Month.JUNE;
            break;
        case "JUL":
            selectedMonthEnum = Month.JULY;
            break;
        case "AUG":
            selectedMonthEnum = Month.AUGUST;
            break;
        case "SEPT":
            selectedMonthEnum = Month.SEPTEMBER;
            break;
        case "OCT":
            selectedMonthEnum = Month.OCTOBER;
            break;
        case "NOV":
            selectedMonthEnum = Month.NOVEMBER;
            break;
        case "DEC":
            selectedMonthEnum = Month.DECEMBER;
            break;
        default:
            // Handle invalid month value
            JOptionPane.showMessageDialog(null, "Invalid month selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
    }

    // Create the visitDate variable using the selected day, month, and year
    int selectedYear = Integer.parseInt(selectedYearObj.toString());
    LocalDate visitDate = LocalDate.of(selectedYear, selectedMonthEnum, selectedDay);
    
    // Check if the selected date is less than today
    LocalDate today = LocalDate.now();
    if (visitDate.isBefore(today)) {
        timeSlotsModel.clear();
        // Display a message that no time slots are available
        JOptionPane.showMessageDialog(null, "No time slots are available for selected date", "Information", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    // Calculate the start and end time for visits
    LocalTime startTime = LocalTime.of(8, 0);
    LocalTime endTime = LocalTime.of(14, 0);

    // Get the duration for each visit in minutes
    int visitDuration = 20;

    // Create a DefaultListModel to hold the available time slots
    DefaultListModel<String> timeSlotsModel = new DefaultListModel<>();

    // Iterate over the time slots and add them to the model
    LocalTime currentTime = startTime;
    while (currentTime.plusMinutes(visitDuration).isBefore(endTime) || currentTime.plusMinutes(visitDuration).equals(endTime)) {
        // Check if the current time slot is available
       
            LocalDateTime slotStartDateTime = LocalDateTime.of(visitDate, currentTime);
        LocalDateTime slotEndDateTime = slotStartDateTime.plusMinutes(visitDuration);

        boolean isTimeSlotAvailable = true;
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM visit WHERE visit_date >= ? AND visit_date < ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setTimestamp(1, Timestamp.valueOf(slotStartDateTime));
                preparedStatement.setTimestamp(2, Timestamp.valueOf(slotEndDateTime));
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    isTimeSlotAvailable = count == 0;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to check time slot availability", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add the time slot to the model if it is available
        if (isTimeSlotAvailable) {
            timeSlotsModel.addElement(currentTime.format(DateTimeFormatter.ofPattern("h:mm a")));
        }

        // Move to the next time slot
        currentTime = currentTime.plusMinutes(visitDuration);
    }

    // Set the model for the JList
    jList1.setModel(timeSlotsModel);
}

  
// Method to get the doctor ID based on the name
private int getDoctorId(String doctorName) throws SQLException {
    int doctorId = 0;

    Connection connection = DBConnection.getConnection();
    String query = "SELECT d_id FROM doctor WHERE d_name = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        preparedStatement.setString(1, doctorName);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            doctorId = resultSet.getInt("d_id");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        throw ex; // Re-throw the exception to be handled by the calling code
    }

    return doctorId;
}

// Method to get the patient ID based on the name
private int getPatientId(String patientName) throws SQLException {
    int patientId = 0;

    Connection connection = DBConnection.getConnection();
    String query = "SELECT p_id FROM patient WHERE p_name = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        preparedStatement.setString(1, patientName);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            patientId = resultSet.getInt("p_id");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        throw ex; // Re-throw the exception to be handled by the calling code
    }

    return patientId;
}
    
    public ForSecretary() throws SQLException {
        initComponents();
        jComboBox1.removeAllItems();
        jComboBox2.removeAllItems();
        try{
        Connection con = DBConnection.getConnection();
        con.setAutoCommit(false);
        String selectstrng = "SELECT * FROM jtxtareafill";
        java.sql.Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(selectstrng);
        
        // D O C T O R S     C O M B O B O X
        ResultSet dctrs = stmt.executeQuery("SELECT * FROM doctor ORDER BY d_name");
        while(dctrs.next()){
        String dnm = dctrs.getString("d_name");
        this.jComboBox1.addItem(dnm);
        }
        
        // P A T I E N T S    C O M B O B O X
        ResultSet ptnts = stmt.executeQuery("SELECT * FROM patient ORDER BY p_name");
        while(ptnts.next()){
        String pnm = ptnts.getString("p_name");
        this.jComboBox2.addItem(pnm);
        }
        
         // V I S I T S    J L I S T
        ResultSet vsts = stmt.executeQuery("SELECT * FROM jtxtareafill order by visit_date desc");
         DefaultListModel<String> model3 = new DefaultListModel<>();
        jList4.setModel(model3);
        jList4.removeAll();
        while(vsts.next()){
        String visitNUM = vsts.getString("visit_number");
            String DNAME = vsts.getString("d_name");
            String PNAME = vsts.getString("p_name");
            String visitdt = vsts.getDate("visit_date").toString();
            String outpt  = visitNUM + ": " + PNAME + " visits " + DNAME + " on " + visitdt;
           
           model3.addElement(outpt);
        }

        
        stmt.close();
        rs.close();
        dctrs.close();
        ptnts.close();
        vsts.close();
        con.close();
        }
        catch(Exception ex){
        JOptionPane.showMessageDialog(null, ex.toString());
        
        }
        
        
        // C A L E N D A R
 
        jComboBox4.removeAllItems();
        jComboBox4.addItem("JAN");
        jComboBox4.addItem("FEB");
        jComboBox4.addItem("MAR");
        jComboBox4.addItem("APR");
        jComboBox4.addItem("MAY");
        jComboBox4.addItem("JUN");
        jComboBox4.addItem("JUL");
        jComboBox4.addItem("AUG");
        jComboBox4.addItem("SEPT");
        jComboBox4.addItem("OCT");
        jComboBox4.addItem("NOV");
        jComboBox4.addItem("DEC");
        jComboBox3.removeAllItems();
        String Day;
        for(int i = 1; i <= 31; i++){
            Day = String.valueOf(i);
        jComboBox3.addItem(Day);
        }
       
        jComboBox5.removeAllItems();
        for(int i = 1900; i <= 2023; i++){
        jComboBox5.addItem(String.valueOf(i));
        }
        // Get today's date
        LocalDate currentDate = LocalDate.now();

        // Set the day, month, and year in the combo boxes
        jComboBox3.setSelectedItem(String.format("%02d", currentDate.getDayOfMonth()));
        jComboBox4.setSelectedItem(currentDate.getMonth().toString().substring(0, 3).toUpperCase());
        jComboBox5.setSelectedItem(String.valueOf(currentDate.getYear()));
        
   Connection connection = DBConnection.getConnection();
  // Get the selected day, month, and year from the combo boxes
int selectedDay = Integer.parseInt(jComboBox3.getSelectedItem().toString());
String selectedMonth = jComboBox4.getSelectedItem().toString();
int selectedYear = Integer.parseInt(jComboBox5.getSelectedItem().toString());

// Map the month name to its corresponding Month enum value
Month selectedMonthEnum = Month.valueOf(selectedMonth.toUpperCase());

// Create the visitDate variable using the selected day, month, and year
LocalDate visitDate = LocalDate.of(selectedYear, selectedMonthEnum, selectedDay);

// Calculate the start and end time for visits
LocalTime startTime = LocalTime.of(8, 0);
LocalTime endTime = LocalTime.of(14, 0);

// Get the duration for each visit in minutes
int visitDuration = 20;

// Create a DefaultListModel to hold the available time slots
    timeSlotsModel = new DefaultListModel<>();

// Iterate over the time slots and add them to the model
LocalTime currentTime = startTime;
while (currentTime.plusMinutes(visitDuration).isBefore(endTime) || currentTime.plusMinutes(visitDuration).equals(endTime)) {
    // Check if the current time slot is available
    LocalDateTime slotStartDateTime = LocalDateTime.of(visitDate, currentTime);
    LocalDateTime slotEndDateTime = slotStartDateTime.plusMinutes(visitDuration);

    boolean isTimeSlotAvailable = true;
    try (Statement statement = connection.createStatement()) {
        String query = "SELECT COUNT(*) FROM visit WHERE visit_date >= ? AND visit_date < ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(slotStartDateTime));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(slotEndDateTime));
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                isTimeSlotAvailable = count == 0;
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to check time slot availability", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Add the time slot to the model if it is available
    if (isTimeSlotAvailable) {
        timeSlotsModel.addElement(currentTime.format(DateTimeFormatter.ofPattern("h:mm a")));
    }

    // Move to the next time slot
    currentTime = currentTime.plusMinutes(visitDuration);
}

// Set the model for the JList
jList1.setModel(timeSlotsModel);
        
         try {
    Connection conn = DBConnection.getConnection();
    Statement stmt = conn.createStatement();

    // Retrieve the maximum visit number from the database
    ResultSet rs = stmt.executeQuery("SELECT MAX(visit_number) FROM visit");

    if (rs.next()) {
        int maxVisitNumber = rs.getInt(1); // Get the value of the first column
        int nextVisitNumber = maxVisitNumber + 1;

        // Set the incremented value in jLabel9
        jLabel9.setText(String.valueOf(nextVisitNumber));
    }

    // Close the result set, statement, and connection
    rs.close();
    stmt.close();
    conn.close();
} catch (SQLException ex) {
    ex.printStackTrace();
}
    }
    
    



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jComboBox5 = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jButton5 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jButton1.setBackground(new java.awt.Color(204, 255, 255));
        jButton1.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jButton1.setText("Add visit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(204, 255, 255));
        jButton3.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jButton3.setText("Delete selected");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tw Cen MT", 0, 36)); // NOI18N
        jLabel6.setText("manage clinic visits");

        jButton4.setBackground(new java.awt.Color(204, 255, 255));
        jButton4.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jButton4.setText("Back ");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jList4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 255, 255)));
        jList4.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(jList4);

        jLabel8.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel8.setText("visit number:");

        jLabel9.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel9.setText(".........");

        jLabel10.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 0, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(173, 206, 206));
        jLabel10.setText("select visits you wish to delete");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jLabel1.setText("Doctor name");

        jLabel2.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jLabel2.setText("Patient name");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel7.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jLabel7.setText("Date of visit:");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox5ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Arial Narrow", 0, 18)); // NOI18N
        jLabel11.setText("Available Time slots:");

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jButton5.setBackground(new java.awt.Color(204, 255, 255));
        jButton5.setFont(new java.awt.Font("Tw Cen MT", 0, 24)); // NOI18N
        jButton5.setText("refresh");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton11.setBackground(new java.awt.Color(204, 255, 255));
        jButton11.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jButton11.setText("add new");
        jButton11.setToolTipText("click after filling the spaces with data of a new patient.");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(233, 233, 233)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 278, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(111, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(111, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9))
                            .addComponent(jLabel11)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(50, 50, 50)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(50, 50, 50))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(89, 89, 89))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
// A D D    V I S I T:
try {
    // Retrieve selected values from comboboxes
    String doctorName = (String) jComboBox1.getSelectedItem();
    String patientName = (String) jComboBox2.getSelectedItem();
    String icdCode = "0000";
    Connection connection = DBConnection.getConnection();
    
    // Retrieve visit date and time from comboboxes
    String day = (String) jComboBox3.getSelectedItem();
    String month = (String) jComboBox4.getSelectedItem();
    String year = (String) jComboBox5.getSelectedItem();
    String timeSlot = jList1.getSelectedValue();

// Validate selected time slot
if (timeSlot == null) {
    JOptionPane.showMessageDialog(null, "Please select a time slot", "Error", JOptionPane.ERROR_MESSAGE);
    return;
}

// Create a LocalDateTime object using the selected date and time
LocalDate visitDate = LocalDate.of(Integer.parseInt(year), Month.valueOf(month.toUpperCase()), Integer.parseInt(day));

DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
LocalTime visitTime = LocalTime.parse(timeSlot, timeFormatter);

LocalDateTime visitDateTime = LocalDateTime.of(visitDate, visitTime);

    // Retrieve the maximum visit number from the database
    int maxVisitNumber = 0;
    try (Statement statement = connection.createStatement()) {
        String query = "SELECT MAX(visit_number) FROM visit";
        ResultSet resultSet = statement.executeQuery(query);
        if (resultSet.next()) {
            maxVisitNumber = resultSet.getInt(1);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to retrieve maximum visit number", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Increment the maximum visit number to get the new visit number
    int newVisitNumber = maxVisitNumber + 1;

    // Perform the necessary operations, such as inserting the visit into the database
    // Use the retrieved values in your insert statement or method call

    // Example insert statement
    String insertQuery = "INSERT INTO visit (d_id, p_id, icd_code, visit_number, visit_date) VALUES (?, ?, ?, ?, ?)";
    try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
        // Set the parameter values
        preparedStatement.setInt(1, getDoctorId(doctorName));
        preparedStatement.setInt(2, getPatientId(patientName));
        preparedStatement.setString(3, icdCode);
        preparedStatement.setInt(4, newVisitNumber);
        preparedStatement.setTimestamp(5, Timestamp.valueOf(visitDateTime));

        // Execute the insert statement
        preparedStatement.executeUpdate();

        // Display success message or perform any additional actions
        JOptionPane.showMessageDialog(null, "Visit added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to add visit", "Error", JOptionPane.ERROR_MESSAGE);
    }
} catch (SQLException ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(null, "Error occurred while retrieving data", "Error", JOptionPane.ERROR_MESSAGE);
}
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        String selectedVisit = jList4.getSelectedValue();
        if (selectedVisit == null) {
            JOptionPane.showMessageDialog(null, "Please select a visit to delete.");
            return;
        }
        

        String visitNum = selectedVisit.split(":")[0].trim();
        int visitNumber = Integer.parseInt(visitNum);

DefaultListModel<String> model = (DefaultListModel<String>) jList4.getModel();
for (int i = model.size() - 1; i >= 0; i--) {
    String item = model.getElementAt(i);
    String currentVisitNum = item.split(":")[0].trim();
    if (Integer.parseInt(currentVisitNum) == visitNumber) {
        model.remove(i);
    }
}

// Delete corresponding records from the database
try (Connection connection = DBConnection.getConnection()) {
    // Delete from lab_order table
    String labOrderDeleteQuery = "DELETE FROM lab_order WHERE visit_id = ?";
    try (PreparedStatement labOrderDeleteStatement = connection.prepareStatement(labOrderDeleteQuery)) {
        labOrderDeleteStatement.setInt(1, visitNumber);
        labOrderDeleteStatement.executeUpdate();
    }

    // Delete from rad_order table
    String radOrderDeleteQuery = "DELETE FROM rad_order WHERE visit_id = ?";
    try (PreparedStatement radOrderDeleteStatement = connection.prepareStatement(radOrderDeleteQuery)) {
        radOrderDeleteStatement.setInt(1, visitNumber);
        radOrderDeleteStatement.executeUpdate();
    }

    // Delete from drug_order table
    String drugOrderDeleteQuery = "DELETE FROM drug_order WHERE visit_id = ?";
    try (PreparedStatement drugOrderDeleteStatement = connection.prepareStatement(drugOrderDeleteQuery)) {
        drugOrderDeleteStatement.setInt(1, visitNumber);
        drugOrderDeleteStatement.executeUpdate();
    }

    // Delete from visit table
    String visitDeleteQuery = "DELETE FROM visit WHERE visit_number = ?";
    try (PreparedStatement visitDeleteStatement = connection.prepareStatement(visitDeleteQuery)) {
        visitDeleteStatement.setInt(1, visitNumber);
        visitDeleteStatement.executeUpdate();
    }

    JOptionPane.showMessageDialog(null, "Visit " + visitNumber + " has been deleted.");
} catch (SQLException e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(null, "An error occurred while deleting the visit.");
}

        model = (DefaultListModel<String>) jList4.getModel();
        for (int i = model.size() - 1; i >= 0; i--) {
            String item = model.getElementAt(i);
            String currentVisitNum = item.split(":")[0].trim();
            if (Integer.parseInt(currentVisitNum) == visitNumber) {
                model.remove(i);
            }
        }

     
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        new Welcome().setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        // TODO add your handling code here:
        // M O N T H
        int currday = jComboBox3.getSelectedIndex();
        if(jComboBox4.getSelectedItem() == "FEB"){
            jComboBox3.removeAllItems();
            String Day;
            String yr = jComboBox5.getSelectedItem().toString();
            int yeAr = parseInt(yr);
            yeAr = yeAr % 4;
            if(yeAr % 4 == 0){
                for(int i = 1; i <= 29; i++){
                    Day = String.valueOf(i);
                    jComboBox3.addItem(Day);
                }
            }
            else{
                for(int i = 1; i <= 28; i++){
                    Day = String.valueOf(i);
                    jComboBox3.addItem(Day);
                }
            }
        }
        else if(jComboBox4.getSelectedItem() == "JAN" || jComboBox4.getSelectedItem() == "MAR" || jComboBox4.getSelectedItem() == "MAY" || jComboBox4.getSelectedItem() == "JUL" || jComboBox4.getSelectedItem() == "AUG" || jComboBox4.getSelectedItem() == "OCT" || jComboBox4.getSelectedItem() == "DEC"){
            jComboBox3.removeAllItems();
            String Day;
            for(int i = 1; i <= 31; i++){
                Day = String.valueOf(i);
                jComboBox3.addItem(Day);
            }
        }
        else{
            jComboBox3.removeAllItems();
            String Day;
            for(int i = 1; i <= 30; i++){
                Day = String.valueOf(i);
                jComboBox3.addItem(Day);
            }
        }
        jComboBox3.setSelectedIndex(currday);
        
        
      //  updateAvailableTimeSlots();
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void jComboBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox5ActionPerformed
        // TODO add your handling code here:
        // Y E A R
        if(jComboBox4.getSelectedItem() == "FEB"){
            jComboBox3.removeAllItems();
            String Day;
            String yr = jComboBox5.getSelectedItem().toString();
            int yeAr = parseInt(yr);
            yeAr = yeAr % 4;
            if(yeAr % 4 == 0){
                for(int i = 1; i <= 29; i++){
                    Day = String.valueOf(i);
                    jComboBox3.addItem(Day);
                }
            }
            else{
                for(int i = 1; i <= 28; i++){
                    Day = String.valueOf(i);
                    jComboBox3.addItem(Day);
                }
            }
        }
    
    }//GEN-LAST:event_jComboBox5ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed

    
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
       updateAvailableTimeSlots();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed

        // TODO add your handling code here:
        new AddPatient().setVisible(true);

        this.setVisible(false);
    }//GEN-LAST:event_jButton11ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ForSecretary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ForSecretary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ForSecretary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ForSecretary.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ForSecretary().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(ForSecretary.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane5;
    // End of variables declaration//GEN-END:variables
}
