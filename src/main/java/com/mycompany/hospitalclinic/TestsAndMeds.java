/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.hospitalclinic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author user
 */
public class TestsAndMeds extends javax.swing.JFrame {

    /**
     * Creates new form TestsAndMeds
     */
    DefaultListModel<String> listModel;
    public TestsAndMeds() {
        initComponents();
        // Assuming you already have the connection to the database and jList4 instance

// Clear the existing items in jList4
listModel = new DefaultListModel<>();
jList4.setModel(listModel);

try {
    Connection connection = DBConnection.getConnection();
    String sql = "SELECT v.visit_number, p.p_name, v.visit_date " +
                 "FROM visit v " +
                 "JOIN patient p ON v.p_id = p.p_id " +
                 "LEFT JOIN lab_order lo ON v.visit_number = lo.visit_id " +
                 "LEFT JOIN rad_order ro ON v.visit_number = ro.visit_id " +
                 "LEFT JOIN drug_order dro ON v.visit_number = dro.visit_id " +
                 "WHERE lo.visit_id IS NULL AND ro.visit_id IS NULL AND dro.visit_id IS NULL " +
                 "AND v.visit_date >= TRUNC(SYSDATE) " +
                 "ORDER BY v.visit_date";

    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);

    while (resultSet.next()) {
        int visitNumber = resultSet.getInt("visit_number");
        String patientName = resultSet.getString("p_name");
        Date visitDate = resultSet.getDate("visit_date");

        // Format the visit date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(visitDate);

        // Construct the list item string
        String listItem = visitNumber + ": " + patientName + " on " + formattedDate;

        // Add the list item to the model
        listModel.addElement(listItem);
    }

    resultSet.close();
    statement.close();
} catch (SQLException e) {
    e.printStackTrace();
}

    jList4.setModel(listModel);

jList4.addListSelectionListener(new ListSelectionListener() {
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            // Get the selected item
            String selectedItem = jList4.getSelectedValue();

            if (selectedItem != null) {
                // Extract the visit number from the selected item
                String visitNumberString = selectedItem.split(":")[0].trim();
                int visitNumber = Integer.parseInt(visitNumberString);

                // Display the visit number in jLabel24
                jLabel24.setText("#" + visitNumber);
            }
        }
    }
});

// Filling jList1 with lt_name values from lab_test
try {
    Connection conn = DBConnection.getConnection();
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT lt_name FROM lab_test");
    
    listModel = new DefaultListModel<>();
    while (rs.next()) {
        String ltName = rs.getString("lt_name");
        listModel.addElement(ltName);
    }
    jList1.setModel(listModel);
    
    rs.close();
    stmt.close();
    conn.close();
} catch (SQLException ex) {
    ex.printStackTrace();
}

// Filling jList2 with rt_name values from rad_test
try {
    Connection conn = DBConnection.getConnection();
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT rt_name FROM rad_test");
    
    listModel = new DefaultListModel<>();
    while (rs.next()) {
        String rtName = rs.getString("rt_name");
        listModel.addElement(rtName);
    }
    jList2.setModel(listModel);
    
    rs.close();
    stmt.close();
    conn.close();
} catch (SQLException ex) {
    ex.printStackTrace();
}

// Filling jList3 with drug_name values from drug
try {
    Connection conn = DBConnection.getConnection();
    Statement stmt = conn.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT drug_name FROM drug");
    
    listModel = new DefaultListModel<>();
    while (rs.next()) {
        String drugName = rs.getString("drug_name");
        listModel.addElement(drugName);
    }
    jList3.setModel(listModel);
    
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
        jLabel12 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();
        jLabel13 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList<>();
        jLabel14 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList<>();
        jLabel23 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jLabel24 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel12.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel12.setText("Laboratory ");

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jList2.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jList2);

        jLabel13.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel13.setText("Radiology");

        jList3.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList3);

        jLabel14.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel14.setText("Drugs");

        jList4.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane6.setViewportView(jList4);

        jLabel23.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel23.setText("Today's visits:");

        jButton1.setBackground(new java.awt.Color(255, 204, 255));
        jButton1.setText("Search");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(255, 204, 255));
        jButton2.setText("Add");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 204, 255));
        jButton3.setText("Search");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 204, 255));
        jButton4.setText("Add");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(255, 204, 255));
        jButton5.setText("Search");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(255, 204, 255));
        jButton6.setText("Add");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(204, 255, 255));
        jButton7.setText("COMPLETE");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(204, 255, 255));
        jButton8.setText("BACK");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tw Cen MT", 0, 18)); // NOI18N
        jLabel24.setText("............");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel24)
                        .addGap(41, 41, 41)))
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 585, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addGap(98, 98, 98)
                        .addComponent(jButton3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addGap(57, 57, 57))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel24))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        
    String searchValue = jTextField6.getText().trim();

    listModel = (DefaultListModel<String>) jList1.getModel();
    int size = listModel.getSize();

    for (int i = 0; i < size; i++) {
        String listItem = listModel.getElementAt(i);
        if (listItem.toLowerCase().contains(searchValue.toLowerCase())) {
            jList1.addSelectionInterval(i, i);
            jList1.ensureIndexIsVisible(i);
        }
    }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        String searchValue = jTextField7.getText().trim();

    listModel = (DefaultListModel<String>) jList2.getModel();
    int size = listModel.getSize();

    for (int i = 0; i < size; i++) {
        String listItem = listModel.getElementAt(i);
        if (listItem.toLowerCase().contains(searchValue.toLowerCase())) {
            jList2.addSelectionInterval(i, i);
            jList2.ensureIndexIsVisible(i);
        }
    }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        String searchValue = jTextField8.getText().trim();

    listModel = (DefaultListModel<String>) jList3.getModel();
    int size = listModel.getSize();

    for (int i = 0; i < size; i++) {
        String listItem = listModel.getElementAt(i);
        if (listItem.toLowerCase().contains(searchValue.toLowerCase())) {
            jList3.addSelectionInterval(i, i);
            jList3.ensureIndexIsVisible(i);
        }
    }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        // Get the selected lab tests from jList1
    List<String> selectedLabTests = jList1.getSelectedValuesList();

    // Get the current visit number from jList4
    String visitNumber = "";
    String selectedVisit = jList4.getSelectedValue();
    if (selectedVisit != null) {
        visitNumber = selectedVisit.split(":")[0].trim();
    }

    // Insert the lab orders into the database
    try {
        Connection conn = DBConnection.getConnection();

        // Get the max lo_id from the lab_order table
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX(lo_id) FROM lab_order");
        int maxLoId = 0;
        if (rs.next()) {
            maxLoId = rs.getInt(1);
        }
        int newLoId = maxLoId + 1;

        // Prepare the insert statement
        PreparedStatement insertStmt = conn.prepareStatement(
            "INSERT INTO lab_order (lo_id, visit_id, lt_id) VALUES (?, ?, ?)"
        );

        // Insert lab orders for each selected lab test
        for (String labTest : selectedLabTests) {
            // Get the lt_id from the lab_test table
            PreparedStatement selectStmt = conn.prepareStatement(
                "SELECT lt_id FROM lab_test WHERE lt_name = ?"
            );
            selectStmt.setString(1, labTest);
            ResultSet selectRs = selectStmt.executeQuery();
            if (selectRs.next()) {
                int ltId = selectRs.getInt("lt_id");
                // Insert the lab order
                insertStmt.setInt(1, newLoId);
                insertStmt.setString(2, visitNumber);
                insertStmt.setInt(3, ltId);
                insertStmt.executeUpdate();
                newLoId++;
            }
        }

        // Close the database connections
        insertStmt.close();
        stmt.close();
        conn.close();

        JOptionPane.showMessageDialog(null, "Lab orders added successfully.");
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to add lab orders.");
    }
    jList1.clearSelection();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
            // Get the selected lab tests from jList1
    List<String> selectedLabTests = jList2.getSelectedValuesList();

    // Get the current visit number from jList4
    String visitNumber = "";
    String selectedVisit = jList4.getSelectedValue();
    if (selectedVisit != null) {
        visitNumber = selectedVisit.split(":")[0].trim();
    }

    // Insert the lab orders into the database
    try {
        Connection conn = DBConnection.getConnection();

        // Get the max lo_id from the lab_order table
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX(ro_id) FROM rad_order");
        int maxRoId = 0;
        if (rs.next()) {
            maxRoId = rs.getInt(1);
        }
        int newRoId = maxRoId + 1;

        // Prepare the insert statement
        PreparedStatement insertStmt = conn.prepareStatement(
            "INSERT INTO rad_order (ro_id, visit_id, rt_id) VALUES (?, ?, ?)"
        );

        // Insert lab orders for each selected lab test
        for (String labTest : selectedLabTests) {
            // Get the lt_id from the lab_test table
            PreparedStatement selectStmt = conn.prepareStatement(
                "SELECT rt_id FROM rad_test WHERE rt_name = ?"
            );
            selectStmt.setString(1, labTest);
            ResultSet selectRs = selectStmt.executeQuery();
            if (selectRs.next()) {
                int rtId = selectRs.getInt("rt_id");
                // Insert the lab order
                insertStmt.setInt(1, newRoId);
                insertStmt.setString(2, visitNumber);
                insertStmt.setInt(3, rtId);
                insertStmt.executeUpdate();
                newRoId++;
            }
        }

        // Close the database connections
        insertStmt.close();
        stmt.close();
        conn.close();

        JOptionPane.showMessageDialog(null, "Radiation orders added successfully.");
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to add radiation orders.");
    }
    jList2.clearSelection();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
            // Get the selected lab tests from jList1
    List<String> selectedDrugTests = jList3.getSelectedValuesList();

    // Get the current visit number from jList4
    String visitNumber = "";
    String selectedVisit = jList4.getSelectedValue();
    if (selectedVisit != null) {
        visitNumber = selectedVisit.split(":")[0].trim();
    }

    // Insert the lab orders into the database
    try {
        Connection conn = DBConnection.getConnection();

        // Get the max lo_id from the lab_order table
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX(do_id) FROM drug_order");
        int maxDoId = 0;
        if (rs.next()) {
            maxDoId = rs.getInt(1);
        }
        int newDoId = maxDoId + 1;

        // Prepare the insert statement
        PreparedStatement insertStmt = conn.prepareStatement(
            "INSERT INTO drug_order (do_id, visit_id, drug_id) VALUES (?, ?, ?)"
        );

        // Insert lab orders for each selected lab test
        for (String DrugTest : selectedDrugTests) {
            // Get the lt_id from the lab_test table
            PreparedStatement selectStmt = conn.prepareStatement(
                "SELECT drug_id FROM drug WHERE drug_name = ?"
            );
            selectStmt.setString(1, DrugTest);
            ResultSet selectRs = selectStmt.executeQuery();
            if (selectRs.next()) {
                int DrugId = selectRs.getInt("drug_id");
                // Insert the lab order
                insertStmt.setInt(1, newDoId);
                insertStmt.setString(2, visitNumber);
                insertStmt.setInt(3, DrugId);
                insertStmt.executeUpdate();
                newDoId++;
            }
        }

        // Close the database connections
        insertStmt.close();
        stmt.close();
        conn.close();

        JOptionPane.showMessageDialog(null, "Drug orders added successfully.");
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Failed to add Drug orders.");
    }
    jList3.clearSelection();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        // Clear the existing items in jList4
listModel = new DefaultListModel<>();
jList4.setModel(listModel);

try {
    Connection connection = DBConnection.getConnection();
    String sql = "SELECT v.visit_number, p.p_name, v.visit_date " +
                 "FROM visit v " +
                 "JOIN patient p ON v.p_id = p.p_id " +
                 "LEFT JOIN lab_order lo ON v.visit_number = lo.visit_id " +
                 "LEFT JOIN rad_order ro ON v.visit_number = ro.visit_id " +
                 "LEFT JOIN drug_order dro ON v.visit_number = dro.visit_id " +
                 "WHERE lo.visit_id IS NULL AND ro.visit_id IS NULL AND dro.visit_id IS NULL " +
                 "AND v.visit_date >= TRUNC(SYSDATE) " +
                 "ORDER BY v.visit_date";

    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(sql);

    while (resultSet.next()) {
        int visitNumber = resultSet.getInt("visit_number");
        String patientName = resultSet.getString("p_name");
        Date visitDate = resultSet.getDate("visit_date");

        // Format the visit date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(visitDate);

        // Construct the list item string
        String listItem = visitNumber + ": " + patientName + " on " + formattedDate;

        // Add the list item to the model
        listModel.addElement(listItem);
    }

    resultSet.close();
    statement.close();
} catch (SQLException e) {
    e.printStackTrace();
}

    jList4.setModel(listModel);
    jList1.clearSelection();
    jList2.clearSelection();
    jList3.clearSelection();
    jList4.clearSelection();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
        new ForDoctor().setVisible(true);
    }//GEN-LAST:event_jButton8ActionPerformed

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
            java.util.logging.Logger.getLogger(TestsAndMeds.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TestsAndMeds.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TestsAndMeds.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TestsAndMeds.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestsAndMeds().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JList<String> jList3;
    private javax.swing.JList<String> jList4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables
}
