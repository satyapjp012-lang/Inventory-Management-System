package inventory.management.system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Opens after employee login: sales workspace (new sale + sales history).
 */
public class EmployeeDashboardFrame extends JFrame {

    String Username, Password;

    public EmployeeDashboardFrame(String Username, String Password) {

        this.Username = Username;
        this.Password = Password;

        setTitle("Inventory Management System - Employee");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        ImageIcon image = new ImageIcon("Src\\Icons\\Logo.png");
        setIconImage(image.getImage());
        getContentPane().setBackground(new Color(14, 9, 41));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(15, 23, 42));
        header.setPreferredSize(new Dimension(getWidth(), 70));

        // Title (center)
        JLabel title = new JLabel("Employee Workspace — Sales", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        // Logout button (right)
        JButton logout = new JButton("Log Out");
        logout.setFont(new Font("SansSerif", Font.BOLD, 18));
        logout.setBorder(BorderFactory.createEmptyBorder(0,25,0,25));
        logout.setFocusPainted(false);
        logout.setBackground(new Color(122, 21, 127));
        logout.setForeground(Color.WHITE);
        logout.addActionListener(e ->{
            new EmployeeLogin();
            dispose();
            return;
        });

        // Add components
        header.add(title, BorderLayout.CENTER);
        header.add(logout, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        add(new EmployeeSalesPanel(Username, Password), BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setBackground(Color.GRAY);
        JLabel footText = new JLabel("© 2026 Inventory Management System");
        footText.setForeground(Color.WHITE);
        footText.setFont(new Font("SansSerif", Font.BOLD, 16));
        footer.add(footText);
        footer.setPreferredSize(new Dimension(getWidth(), 30));
        add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

}
