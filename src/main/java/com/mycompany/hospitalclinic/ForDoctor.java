/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.hospitalclinic;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.export.*;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import oracle.jdbc.pool.OracleDataSource;

/**
 *
 * @author user
 */
public class ForDoctor extends javax.swing.JFrame {

    /**
     * Creates new form ForDoctor
     */
    private String retrieveICDCode(String icdName) {
    String icdCode = null;
    
    try (Connection connection = DBConnection.getConnection()) {
        String query = "SELECT icd_code FROM icd_10 WHERE icd_name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, icdName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                icdCode = resultSet.getString("icd_code");
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    
    return icdCode;
}
    
    
    
    // Method to get the patient ID based on the name
private String getPatientName(int patientId) throws SQLException {
    String pname = null;

    Connection connection = DBConnection.getConnection();
    String query = "SELECT p_name FROM patient WHERE p_id = ?";
    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        preparedStatement.setInt(1, patientId);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            pname = resultSet.getString("p_name");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        throw ex; // Re-throw the exception to be handled by the calling code
    }

    return pname;
}
    
    
    public ForDoctor() {
        initComponents();
        updateVisitList();
        jList3.setVisible(false);
        jList4.setVisible(false);
        jList5.setVisible(false);
        jList6.setVisible(false);
    jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
    public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            // Get the selected visit information from jList1
            String selectedVisitInfo = jList1.getSelectedValue();

            // Extract the visit number from the selected visit information
            int visitNumber = Integer.parseInt(selectedVisitInfo.split(":")[0].trim());
            jLabel14.setText(selectedVisitInfo.split(":")[0].trim());

            try {
                Connection connection = DBConnection.getConnection();
                // Retrieve patient id and name for the selected visit
                String patientQuery = "SELECT visit.p_id, p_name " +
                        "FROM visit " +
                        "JOIN patient ON visit.p_id = patient.p_id " +
                        "WHERE visit_number = ?";
                
                try (PreparedStatement patientStatement = connection.prepareStatement(patientQuery)) {
                    patientStatement.setInt(1, visitNumber);
                    ResultSet patientResultSet = patientStatement.executeQuery();
                    
                    if (patientResultSet.next()) {
                        int patientId = patientResultSet.getInt("p_id");
                        String patientName = patientResultSet.getString("p_name");
                        
                        // Display patient id in jTextField6
                        jTextField6.setText(Integer.toString(patientId));
                        
                        // Display patient name in jTextField7
                        jTextField7.setText(patientName);
                        
                        // Retrieve the total visits count and the date of the last visit with the same patient id
                        String visitsCountQuery = "SELECT COUNT(*) AS total_visits, MAX(visit_date) AS last_visit_date " +
                                "FROM visit " +
                                "WHERE p_id = ? AND icd_code != '0000'";
                        
                        try (PreparedStatement visitsCountStatement = connection.prepareStatement(visitsCountQuery)) {
                            visitsCountStatement.setInt(1, patientId);
                            ResultSet visitsCountResultSet = visitsCountStatement.executeQuery();
                            
                            if (visitsCountResultSet.next()) {
                                // Display total visits count in jLabel19
                                int totalVisits = visitsCountResultSet.getInt("total_visits");
                                jLabel19.setText(Integer.toString(totalVisits));
                                
                                // Display the date of the last visit in jLabel22
                                Timestamp lastVisitTimestamp = visitsCountResultSet.getTimestamp("last_visit_date");
                                if (lastVisitTimestamp != null) {
                                    LocalDate lastVisitDate = lastVisitTimestamp.toLocalDateTime().toLocalDate();
                                    jLabel22.setText(lastVisitDate.toString());
                                } else {
                                    jLabel22.setText("N/A");
                                }
                            }
                        }
                        
                        // Retrieve past icd_names for the selected patient
                        String icdNamesQuery = "SELECT icd_name " +
                                "FROM visit " +
                                "JOIN icd_10 ON visit.icd_code = icd_10.icd_code " +
                                "WHERE p_id = ? AND icd_name != 'DEFAULT'";
                        
                        try (PreparedStatement icdNamesStatement = connection.prepareStatement(icdNamesQuery)) {
                            icdNamesStatement.setInt(1, patientId);
                            ResultSet icdNamesResultSet = icdNamesStatement.executeQuery();
                            
                            // Create a list to hold the past icd_names
                            List<String> pastIcdNames = new ArrayList<>();
                            
                            while (icdNamesResultSet.next()) {
                                String icdName = icdNamesResultSet.getString("icd_name");
                                pastIcdNames.add(icdName);
                            }
                            
                            // Update jList3 with the past icd_names
                            jList3.setModel(new javax.swing.AbstractListModel<String>() {
                                String[] strings = pastIcdNames.toArray(new String[0]);
                                
                                public int getSize() {
                                    return strings.length;
                                }
                                                                public String getElementAt(int index) {
                                    return strings[index];
                                }
                            });
                            jList3.setVisible(true);

                        }
                        
                        // Retrieve past icd_names for the selected patient
                        String ltNamesQuery = "SELECT lab_test.lt_name FROM visit, lab_order, lab_test, patient WHERE patient.p_id = ? AND lab_order.visit_id = visit.visit_number AND patient.p_id = visit.p_id AND lab_order.lt_id = lab_test.lt_id";
                        
                        try (PreparedStatement ltNamesStatement = connection.prepareStatement(ltNamesQuery)) {
                            ltNamesStatement.setInt(1, patientId);
                            ResultSet ltNamesResultSet = ltNamesStatement.executeQuery();
                            
                            // Create a list to hold the past icd_names
                            List<String> pastltNames = new ArrayList<>();
                            
                            while (ltNamesResultSet.next()) {
                                String ltName = ltNamesResultSet.getString(1);
                                pastltNames.add(ltName);
                            }
                            
                            // Update jList3 with the past icd_names
                            jList4.setModel(new javax.swing.AbstractListModel<String>() {
                                String[] strings = pastltNames.toArray(new String[0]);
                                
                                public int getSize() {
                                    return strings.length;
                                }
                                                                public String getElementAt(int index) {
                                    return strings[index];
                                }
                            });
                            jList4.setVisible(true);

                        }
                        
                        // Retrieve past icd_names for the selected patient
                        String rtNamesQuery = "SELECT rad_test.rt_name FROM visit, rad_order, rad_test, patient WHERE patient.p_id = ? AND rad_order.visit_id = visit.visit_number AND patient.p_id = visit.p_id AND rad_order.rt_id = rad_test.rt_id";
                        
                        try (PreparedStatement rtNamesStatement = connection.prepareStatement(rtNamesQuery)) {
                            rtNamesStatement.setInt(1, patientId);
                            ResultSet rtNamesResultSet = rtNamesStatement.executeQuery();
                            
                            // Create a list to hold the past icd_names
                            List<String> pastrtNames = new ArrayList<>();
                            
                            while (rtNamesResultSet.next()) {
                                String rtName = rtNamesResultSet.getString(1);
                                pastrtNames.add(rtName);
                            }
                            
                            // Update jList3 with the past icd_names
                            jList5.setModel(new javax.swing.AbstractListModel<String>() {
                                String[] strings = pastrtNames.toArray(new String[0]);
                                
                                public int getSize() {
                                    return strings.length;
                                }
                                                                public String getElementAt(int index) {
                                    return strings[index];
                                }
                            });
                            jList5.setVisible(true);
                        }
                        
                        // Retrieve past icd_names for the selected patient
                        String dtNamesQuery = "SELECT drug.drug_name FROM visit, drug_order, drug, patient WHERE patient.p_id = ? AND drug_order.visit_id = visit.visit_number AND patient.p_id = visit.p_id AND drug_order.drug_id = drug.drug_id";
                        
                        try (PreparedStatement dtNamesStatement = connection.prepareStatement(dtNamesQuery)) {
                            dtNamesStatement.setInt(1, patientId);
                            ResultSet dtNamesResultSet = dtNamesStatement.executeQuery();
                            
                            // Create a list to hold the past icd_names
                            List<String> pastdtNames = new ArrayList<>();
                            
                            while (dtNamesResultSet.next()) {
                                String rtName = dtNamesResultSet.getString(1);
                                pastdtNames.add(rtName);
                            }
                            
                            // Update jList3 with the past icd_names
                            jList6.setModel(new javax.swing.AbstractListModel<String>() {
                                String[] strings = pastdtNames.toArray(new String[0]);
                                
                                public int getSize() {
                                    return strings.length;
                                }
                                                                public String getElementAt(int index) {
                                    return strings[index];
                                }
                            });
                            jList6.setVisible(true);

                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error occurred while retrieving visit details", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
});

    try (Connection connection = DBConnection.getConnection()) {
    String query = "SELECT icd_name FROM icd_10";
    try (Statement statement = connection.createStatement()) {
        ResultSet resultSet = statement.executeQuery(query);

        // Clear existing items in jComboBox6
        jComboBox6.removeAllItems();

        // Iterate over the result set and add icd_name values to jComboBox6
        while (resultSet.next()) {
            String icdName = resultSet.getString("icd_name");
            jComboBox6.addItem(icdName);
        }
        jComboBox6.setSelectedItem("DEFAULT");
    }
} catch (SQLException ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(null, "Failed to retrieve ICD names", "Error", JOptionPane.ERROR_MESSAGE);
}
    
    }
    
    private void updateVisitList() {
    DefaultListModel<String> visitListModel = new DefaultListModel<>();

    try {
        Connection connection = DBConnection.getConnection();

        // SQL query to retrieve the visits matching the conditions
        String query = "SELECT v.visit_number, p.p_name, v.visit_date " +
                "FROM visit v " +
                "JOIN patient p ON v.p_id = p.p_id " +
                "WHERE v.icd_code = '0000'";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            // Format for displaying the time as "hh:mm a" (e.g., 11:30 AM)
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");

            // Iterate over the result set and format the visit strings
            while (resultSet.next()) {
                int visitNumber = resultSet.getInt("visit_number");
                String patientName = resultSet.getString("p_name");
                java.util.Date visitDateTime = resultSet.getTimestamp("visit_date");

                // Format the visit date and time
                String formattedDateTime = new SimpleDateFormat("MM/dd/yyyy").format(visitDateTime)
                        + " at " + timeFormat.format(visitDateTime);

                // Format the visit string as "visitnumber: patientname on date at time"
                String visitString = visitNumber + ": " + patientName + " on " + formattedDateTime;

                // Add the formatted visit string to the list model
                visitListModel.addElement(visitString);
            }
        }
        
        // Set the list model for jList1
    jList1.setModel(visitListModel);
        
    } catch (SQLException ex) {
        ex.printStackTrace();
        // Handle any exceptions or display an error message
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

        jPanel2 = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList<>();
        jLabel17 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList5 = new javax.swing.JList<>();
        jButton16 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jList6 = new javax.swing.JList<>();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jComboBox6 = new javax.swing.JComboBox<>();
        jButton22 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 255, 255), 2));
        jPanel2.setPreferredSize(new java.awt.Dimension(586, 338));

        jButton11.setBackground(new java.awt.Color(204, 255, 255));
        jButton11.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jButton11.setText("manage patients");
        jButton11.setToolTipText("click after filling the spaces with data of a new patient.");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel11.setText("Patient's name");

        jLabel12.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel12.setText("patients's id");

        jLabel13.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(0, 153, 153));
        jLabel13.setText("VISIT NUMBER:");

        jLabel14.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 153, 153));
        jLabel14.setText("........");

        jLabel15.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel15.setText("taken laboratory tests:");

        jLabel16.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel16.setText("medical history:");

        jList3.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList3);

        jList4.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList4);

        jLabel17.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel17.setText("taken Radiology tests:");

        jList5.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(jList5);

        jButton16.setBackground(new java.awt.Color(204, 255, 255));
        jButton16.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jButton16.setText("order tests and prescribe meds");
        jButton16.setToolTipText("");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jList6.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane5.setViewportView(jList6);

        jLabel18.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel18.setText("prevoiusly prescribed meds:");

        jLabel19.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(0, 153, 153));
        jLabel19.setText(".........");

        jLabel20.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(0, 153, 153));
        jLabel20.setText("TOTAL VISITS:");

        jLabel21.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(0, 153, 153));
        jLabel21.setText("LAST VISIT:");

        jLabel22.setFont(new java.awt.Font("Tw Cen MT", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(0, 153, 153));
        jLabel22.setText("...........");

        jButton19.setBackground(new java.awt.Color(204, 255, 255));
        jButton19.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jButton19.setText("END VISIT");
        jButton19.setToolTipText("");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setBackground(new java.awt.Color(204, 255, 255));
        jButton20.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jButton20.setText("BACK");
        jButton20.setToolTipText("");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton21.setBackground(new java.awt.Color(204, 255, 255));
        jButton21.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jButton21.setText("GET REPORT(PDF)");
        jButton21.setToolTipText("");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane6.setViewportView(jList1);

        jLabel23.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel23.setText("Upcoming visits:");

        jLabel24.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel24.setText("Diagnosis:");

        jTextField8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField8ActionPerformed(evt);
            }
        });

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox6ActionPerformed(evt);
            }
        });

        jButton22.setBackground(new java.awt.Color(204, 255, 255));
        jButton22.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jButton22.setText("Search");
        jButton22.setToolTipText("");
        jButton22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton22ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                            .addComponent(jScrollPane3))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                            .addComponent(jScrollPane4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18))
                        .addGap(0, 27, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel14))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel11))
                                        .addGap(6, 6, 6)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(56, 56, 56)
                                        .addComponent(jButton11)))
                                .addGap(75, 75, 75)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jComboBox6, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(106, 106, 106)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jButton21, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton19, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(jLabel22))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jComboBox6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton22, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)))))
                .addGap(59, 59, 59)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton16, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton21, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 901, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
     
            // TODO add your handling code here:
            new AddPatient().setVisible(true);
        
        this.setVisible(false);
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
    String icdName = jComboBox6.getSelectedItem().toString();
    int visitNumber = Integer.parseInt(jLabel14.getText());
    
    // Retrieve the corresponding icd_code from the database
    String icdCode = retrieveICDCode(icdName);
    if (icdCode == null) {
        JOptionPane.showMessageDialog(null, "ICD code not found", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Update the visit table with the new icd_code
    try (Connection connection = DBConnection.getConnection()) {
        String query = "UPDATE visit SET icd_code = ? WHERE visit_number = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, icdCode);
            preparedStatement.setInt(2, visitNumber);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Visit ended successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Perform any additional actions after ending the visit
                jTextField6.setText("");
                jTextField7.setText("");
                jTextField8.setText("");
      
                jLabel14.setText(".......");
                jLabel19.setText(".......");
                jLabel22.setText(".......");
                String[] emptyData = new String[0];
                jList3.setVisible(false);
                jList4.setVisible(false);
                jList5.setVisible(false);
                jList6.setVisible(false);
                jComboBox6.setSelectedItem("DEFAULT");  
                updateVisitList(); // Update the visit list after ending the visit
                jList1.clearSelection(); // Clear the selection in the visit list
                
            } else {
                JOptionPane.showMessageDialog(null, "Failed to end visit", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to update visit in the database", "Error", JOptionPane.ERROR_MESSAGE);
    }
      
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        new Welcome().setVisible(true);
    }//GEN-LAST:event_jButton20ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:
     try {
            // Load the JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Create a connection to the database
            Connection connection = DBConnection.getConnection();

            // Compile the JasperReport template
            File reportTemplate = new File("pastMonthVisits.jrxml");
            JasperCompileManager.compileReportToFile(reportTemplate.getAbsolutePath());

            // Fill the JasperReport template with data
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportTemplate.getAbsolutePath(), null, connection);

            // Export the JasperReport to PDF
            OutputStream output = new FileOutputStream(new File("VisitSummary.pdf"));
            JasperExportManager.exportReportToPdfStream(jasperPrint, output);

            output.close();
            connection.close();

            JOptionPane.showMessageDialog(null, "Report created.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        new TestsAndMeds().setVisible(true);
    }//GEN-LAST:event_jButton16ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField8ActionPerformed

    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox6ActionPerformed

    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton22ActionPerformed
        // TODO add your handling code here:
         String icdName = jTextField8.getText().trim();
        
        // Perform the database query to retrieve the matching ICD name
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT icd_name FROM icd_10 WHERE icd_name LIKE ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, "%" + icdName + "%");
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String matchingICDName = resultSet.getString("icd_name");
                    
                    // Check if the retrieved ICD name is in the combo box items
                    for (int i = 0; i < jComboBox6.getItemCount(); i++) {
                        String item = jComboBox6.getItemAt(i).toString();
                        if (item.equalsIgnoreCase(matchingICDName)) {
                            jComboBox6.setSelectedIndex(i);
                            return;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to retrieve ICD name from the database", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        // If no matching ICD name is found, display a message
       JOptionPane.showMessageDialog(null, "ICD name not found", "Error", JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_jButton22ActionPerformed

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
            java.util.logging.Logger.getLogger(ForDoctor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ForDoctor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ForDoctor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ForDoctor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ForDoctor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JComboBox<String> jComboBox6;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList3;
    private javax.swing.JList<String> jList4;
    private javax.swing.JList<String> jList5;
    private javax.swing.JList<String> jList6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables
}
