/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.hospitalclinic;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import static java.lang.Integer.parseInt;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author user
 */
public class AddPatient extends javax.swing.JFrame {

    /**
     * Creates new form AddPatient
     */
    
    public int getMonthNumber(String month) {
    switch (month) {
        case "JAN":
            return 1;
        case "FEB":
            return 2;
        case "MAR":
            return 3;
        case "APR":
            return 4;
        case "MAY":
            return 5;
        case "JUN":
            return 6;
        case "JUL":
            return 7;
        case "AUG":
            return 8;
        case "SEP":
            return 9;
        case "OCT":
            return 10;
        case "NOV":
            return 11;
        case "DEC":
            return 12;
        default:
            throw new IllegalArgumentException("Invalid month: " + month);
    }
}

    DefaultListModel<String> listModel = new DefaultListModel<>();
    ButtonGroup genderGroup = new ButtonGroup();
    public AddPatient() {
        initComponents();
        // P A T I E N T:
        genderGroup.add(jRadioButton1);
        genderGroup.add(jRadioButton2);
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
        jComboBox6.removeAllItems();
        String Day;
        
        for(int i = 1; i <= 31; i++){
            Day = String.valueOf(i);
        jComboBox6.addItem(Day);
        }
       
        jComboBox5.removeAllItems();
        for(int i = 1900; i <= 2023; i++){
        jComboBox5.addItem(String.valueOf(i));
        }
        // Retrieve the list of patient names from the database
        ArrayList<String> patientNames = new ArrayList<String>();
        try {
            Connection conn3 = DBConnection.getConnection();
            Statement stmt3 = conn3.createStatement();
            ResultSet rs3 = stmt3.executeQuery("SELECT p_name FROM patient ORDER BY p_name");
            while (rs3.next()) {
                patientNames.add(rs3.getString("p_name"));
            }
            rs3.close();
            stmt3.close();
            conn3.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Set the patient names to the JList
        String[] patientArray = new String[patientNames.size()];
        patientArray = patientNames.toArray(patientArray);
        jList2.setListData(patientArray);
        
        
        // get the maximum patient ID value from the database
    int maxID = 0;
    try {
        Connection conn3 = DBConnection.getConnection();
        String query = "SELECT MAX(p_id) FROM patient";
        Statement stmt3 = conn3.createStatement();
        ResultSet rs3 = stmt3.executeQuery(query);
        if (rs3.next()) {
            maxID = rs3.getInt(1);
    }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

        // set the value of the p_id text field
    jTextField6.setText(String.valueOf(maxID + 1));
    
    
    //patient jlist
    jList2.addMouseListener(new MouseAdapter() {
    public void mouseClicked(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            // Get the selected patient's name from the JList
            String selectedPatientName = jList2.getSelectedValue();

            // Query the database to retrieve the data of the selected patient
            try {
                Connection conn2 = DBConnection.getConnection();
                PreparedStatement stmt2 = conn2.prepareStatement("SELECT * FROM patient WHERE p_name = ?");
                stmt2.setString(1, selectedPatientName);
                ResultSet rs2 = stmt2.executeQuery();

                // Set the retrieved data into the text fields and comboboxes
               if (rs2.next()) {
                jTextField6.setText(rs2.getString("p_id"));
                jTextField7.setText(rs2.getString("p_name"));
                jTextField8.setText(rs2.getString("p_phonenum"));
                jTextField9.setText(rs2.getString("p_address"));
                Calendar cal = Calendar.getInstance();
                cal.setTime(rs2.getDate("p_birthdate"));
                String day =  String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
                int daynum = Integer.parseInt(day) - 1;
                int month = cal.get(Calendar.MONTH);
                String mnth = new DateFormatSymbols().getShortMonths()[month].toUpperCase();
                jComboBox4.setSelectedIndex(getMonthNumber(mnth)-1);
                jComboBox5.setSelectedItem(String.format("%04d", cal.get(Calendar.YEAR)));
                jComboBox6.setSelectedIndex(daynum);
                if (rs2.getString("gender").equals("M")) {
                    jRadioButton1.setSelected(true);
                } else {
                    jRadioButton2.setSelected(true);
                }
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
});
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jComboBox4 = new javax.swing.JComboBox<>();
        jComboBox5 = new javax.swing.JComboBox<>();
        jComboBox6 = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton14 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jButton15 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 255, 255), 2));
        jPanel2.setPreferredSize(new java.awt.Dimension(586, 338));

        jButton11.setBackground(new java.awt.Color(204, 255, 255));
        jButton11.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jButton11.setText("add patient");
        jButton11.setToolTipText("click after filling the spaces with data of a new patient.");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setBackground(new java.awt.Color(204, 255, 255));
        jButton12.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jButton12.setText("Update data");
        jButton12.setToolTipText("click after changing an existing patient's data.");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setBackground(new java.awt.Color(204, 255, 255));
        jButton13.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jButton13.setText("Delete");
        jButton13.setToolTipText("make sure to double click the patient you want  to delete.");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
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

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox6ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel11.setText("name");

        jLabel12.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel12.setText("patients's id");

        jLabel13.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel13.setText("phone number");

        jLabel14.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel14.setText("address");

        jLabel16.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel16.setText("birth date");

        jLabel2.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel2.setText("gender");

        jRadioButton1.setText("Male");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        jRadioButton2.setText("Female");

        jButton14.setBackground(new java.awt.Color(204, 255, 255));
        jButton14.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jButton14.setText("Clear");
        jButton14.setToolTipText("");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jList2.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList2);

        jLabel15.setForeground(new java.awt.Color(99, 129, 129));
        jLabel15.setText("double-click on patient to select!");

        jLabel17.setFont(new java.awt.Font("Tw Cen MT", 0, 36)); // NOI18N
        jLabel17.setText("PATIENTS");

        jButton15.setBackground(new java.awt.Color(204, 255, 255));
        jButton15.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jButton15.setText("BACK");
        jButton15.setToolTipText("make sure to double click the patient you want  to delete.");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel13))
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel14))
                                .addGap(12, 12, 12)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jRadioButton2)
                                        .addGap(18, 18, 18)
                                        .addComponent(jRadioButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31))))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(206, 206, 206)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton15)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(jLabel17)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jRadioButton2)
                            .addComponent(jRadioButton1))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton11)
                    .addComponent(jButton12)
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        // Get the values from the text fields and comboboxes
        String name = jTextField7.getText();
        String phoneNumber = jTextField8.getText();
        String address = jTextField9.getText();
        String gender = jRadioButton1.isSelected() ? "M" : "F";
        String birthDateStr = jComboBox6.getSelectedItem().toString() + "-" + getMonthNumber(jComboBox4.getSelectedItem().toString()) + "-" + jComboBox5.getSelectedItem().toString();

        // Convert birthdate string to a date object
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date birthDate = null;
        try {
            birthDate = dateFormat.parse(birthDateStr);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

        // Get the next available patient id
        int patientId = 0;
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(p_id) FROM patient");
            if (rs.next()) {
                patientId = rs.getInt(1) + 1;
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Insert the new patient into the database
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "INSERT INTO patient (p_id, p_name, p_phonenum, p_birthdate, p_address, gender) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, patientId);
            pstmt.setString(2, name);
            pstmt.setString(3, phoneNumber);
            pstmt.setDate(4, new java.sql.Date(birthDate.getTime()));
            pstmt.setString(5, address);
            pstmt.setString(6, gender);
            pstmt.executeUpdate();
            conn.close();
            String outpt = "Added patient with id " + jTextField6.getText();
            JOptionPane.showMessageDialog(null, outpt);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Clear the text fields and set the id field to the next available id
        jTextField7.setText("");
        jTextField8.setText("");
        jTextField9.setText("");
        jRadioButton2.setSelected(true);
        jComboBox6.setSelectedIndex(0);
        jComboBox4.setSelectedIndex(0);
        jComboBox5.setSelectedIndex(0);
        int maxID = 0;
        try {
            Connection conn3 = DBConnection.getConnection();
            String query = "SELECT MAX(p_id) FROM patient";
            Statement stmt3 = conn3.createStatement();
            ResultSet rs3 = stmt3.executeQuery(query);
            if (rs3.next()) {
                maxID = rs3.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // set the value of the p_id text field
        jTextField6.setText(String.valueOf(maxID + 1));

        // Retrieve the list of patient names from the database
        ArrayList<String> patientNames = new ArrayList<String>();
        try {
            Connection conn3 = DBConnection.getConnection();
            Statement stmt3 = conn3.createStatement();
            ResultSet rs3 = stmt3.executeQuery("SELECT p_name FROM patient ORDER BY p_name");
            while (rs3.next()) {
                patientNames.add(rs3.getString("p_name"));
            }
            rs3.close();
            stmt3.close();
            conn3.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Set the patient names to the JList
        String[] patientArray = new String[patientNames.size()];
        patientArray = patientNames.toArray(patientArray);
        jList2.setListData(patientArray);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        // E D I T    P A T I E N T
        // Get the data from the text fields and combo boxes
        String pID = jTextField6.getText();
        String pName = jTextField7.getText();
        String pPhoneNum = jTextField8.getText();
        String pAddress = jTextField9.getText();
        String pBirthdate = jComboBox6.getSelectedItem() + "-" + jComboBox4.getSelectedItem() + "-" + jComboBox5.getSelectedItem();
        String gender = jRadioButton1.isSelected() ? "M" : "F";

        // Update the patient data in the database
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("UPDATE patient SET p_name = ?, p_phonenum = ?, p_birthdate = ?, p_address = ?, gender = ? WHERE p_id = ?");
            stmt.setString(1, pName);
            stmt.setString(2, pPhoneNum);
            stmt.setString(4, pAddress);
            stmt.setString(3, pBirthdate);
            stmt.setString(5, gender);
            stmt.setString(6, pID);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Patient data updated successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
        // Get the id of the patient to be deleted from the id text field
        int patientId = Integer.parseInt(jTextField6.getText());

        // Delete the patient from the database
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM patient WHERE p_id = ?");
            stmt.setInt(1, patientId);
            int rowsAffected = stmt.executeUpdate();

            // Clear the text fields and comboboxes
            jTextField6.setText("");
            jTextField7.setText("");
            jTextField8.setText("");
            jTextField9.setText("");
            jComboBox6.setSelectedIndex(0);
            jComboBox4.setSelectedIndex(0);
            jComboBox5.setSelectedIndex(0);
            jRadioButton2.setSelected(true);

            Connection conn1 = null;
            PreparedStatement stmt1 = null;
            ResultSet rs1 = null;
            try {
                conn1 = DBConnection.getConnection();
                stmt1 = conn1.prepareStatement("SELECT MAX(p_id) FROM patient");
                rs1 = stmt1.executeQuery();
                int id = 1;
                if (rs1.next()) {
                    int maxId = rs1.getInt(1);
                    if (!rs1.wasNull()) {
                        id = maxId + 1;
                    }
                }
                jTextField6.setText(Integer.toString(id));
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error retrieving ID: " + e.getMessage());
            } finally {
                if (rs1 != null) {
                    try {
                        rs1.close();
                    } catch (SQLException e) {
                        // Ignore
                    }
                }
                if (stmt1 != null) {
                    try {
                        stmt1.close();
                    } catch (SQLException e) {
                        // Ignore
                    }
                }
                if (conn1 != null) {
                    try {
                        conn1.close();
                    } catch (SQLException e) {
                        // Ignore
                    }
                }
            }
            // Retrieve the list of patient names from the database
            ArrayList<String> patientNames = new ArrayList<String>();
            try {
                Connection conn3 = DBConnection.getConnection();
                Statement stmt3 = conn3.createStatement();
                ResultSet rs3 = stmt3.executeQuery("SELECT p_name FROM patient ORDER BY p_name");
                while (rs3.next()) {
                    patientNames.add(rs3.getString("p_name"));
                }
                rs3.close();
                stmt3.close();
                conn3.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            // Set the patient names to the JList
            String[] patientArray = new String[patientNames.size()];
            patientArray = patientNames.toArray(patientArray);
            jList2.setListData(patientArray);

            // Display a message to the user indicating success or failure of the delete operation
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Patient deleted successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete patient!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while trying to delete patient!");
        }

    }//GEN-LAST:event_jButton13ActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        // TODO add your handling code here:
        // M O N T H
        int currday = jComboBox6.getSelectedIndex();
        if(jComboBox4.getSelectedItem() == "FEB"){
            jComboBox6.removeAllItems();
            String Day;
            String yr = jComboBox5.getSelectedItem().toString();
            int yeAr = parseInt(yr);
            yeAr = yeAr % 4;
            if(yeAr % 4 == 0){
                for(int i = 1; i <= 29; i++){
                    Day = String.valueOf(i);
                    jComboBox6.addItem(Day);
                }
            }
            else{
                for(int i = 1; i <= 28; i++){
                    Day = String.valueOf(i);
                    jComboBox6.addItem(Day);
                }
            }
        }
        else if(jComboBox4.getSelectedItem() == "JAN" || jComboBox4.getSelectedItem() == "MAR" || jComboBox4.getSelectedItem() == "MAY" || jComboBox4.getSelectedItem() == "JUL" || jComboBox4.getSelectedItem() == "AUG" || jComboBox4.getSelectedItem() == "OCT" || jComboBox4.getSelectedItem() == "DEC"){
            jComboBox6.removeAllItems();
            String Day;
            for(int i = 1; i <= 31; i++){
                Day = String.valueOf(i);
                jComboBox6.addItem(Day);
            }
        }
        else{
            jComboBox6.removeAllItems();
            String Day;
            for(int i = 1; i <= 30; i++){
                Day = String.valueOf(i);
                jComboBox6.addItem(Day);
            }
        }
        jComboBox6.setSelectedIndex(currday);
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void jComboBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox5ActionPerformed
        // TODO add your handling code here:++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // Y E A R
        if(jComboBox4.getSelectedItem() == "FEB"){
            jComboBox6.removeAllItems();
            String Day;
            String yr = jComboBox5.getSelectedItem().toString();
            int yeAr = parseInt(yr);
            yeAr = yeAr % 4;
            if(yeAr % 4 == 0){
                for(int i = 1; i <= 29; i++){
                    Day = String.valueOf(i);
                    jComboBox6.addItem(Day);
                }
            }
            else{
                for(int i = 1; i <= 28; i++){
                    Day = String.valueOf(i);
                    jComboBox6.addItem(Day);
                }
            }
        }
    }//GEN-LAST:event_jComboBox5ActionPerformed

    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
        // TODO add your handling code here:++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // D A Y
    }//GEN-LAST:event_jComboBox6ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
        jTextField7.setText("");
        jTextField8.setText("");
        jTextField9.setText("");
        jRadioButton2.setSelected(true);
        jComboBox6.setSelectedIndex(0);
        jComboBox4.setSelectedIndex(0);
        jComboBox5.setSelectedIndex(0);
        jList2.clearSelection();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            stmt = conn.prepareStatement("SELECT MAX(p_id) FROM patient");
            rs = stmt.executeQuery();
            int id = 1;
            if (rs.next()) {
                int maxId = rs.getInt(1);
                if (!rs.wasNull()) {
                    id = maxId + 1;
                }
            }
            jTextField6.setText(Integer.toString(id));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving ID: " + e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        new ForDoctor().setVisible(true);
    }//GEN-LAST:event_jButton15ActionPerformed

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
            java.util.logging.Logger.getLogger(AddPatient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddPatient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddPatient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddPatient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AddPatient().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JComboBox<String> jComboBox5;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList<String> jList2;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
