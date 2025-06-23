import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class FruitStockManagementSystem extends JFrame {
    private JTextField nameField;
    private JTextField quantityField;
    private JTextField priceField;
    private JTable fruitTable;
    private DefaultTableModel tableModel;

    public FruitStockManagementSystem() {
        setTitle("Fruit Stock Management System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        inputPanel.add(quantityField);

        inputPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        inputPanel.add(priceField);

        add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Quantity", "Price"}, 0);
        fruitTable = new JTable(tableModel);
        add(new JScrollPane(fruitTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create");
        createButton.addActionListener(new CreateButtonListener());
        buttonPanel.add(createButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(new UpdateButtonListener());
        buttonPanel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new DeleteButtonListener());
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadFruitData();
    }

    private void loadFruitData() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM fruits")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                tableModel.addRow(new Object[]{id, name, quantity, price});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private class CreateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO fruits (name, quantity, price) VALUES (?, ?, ?)")) {
                pstmt.setString(1, name);
                pstmt.setInt(2, quantity);
                pstmt.setDouble(3, price);
                pstmt.executeUpdate();
                loadFruitData();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = fruitTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                String name = nameField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());

                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement("UPDATE fruits SET name = ?, quantity = ?, price = ? WHERE id = ?")) {
                    pstmt.setString(1, name);
                    pstmt.setInt(2, quantity);
                    pstmt.setDouble(3, price);
                    pstmt.setInt(4, id);
                    pstmt.executeUpdate();
                    loadFruitData();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = fruitTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);

                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement("DELETE FROM fruits WHERE id = ?")) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                    loadFruitData();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FruitStockManagementSystem().setVisible(true);
        });
    }
}
