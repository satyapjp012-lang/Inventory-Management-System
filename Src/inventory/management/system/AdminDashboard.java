package inventory.management.system;

import java.awt.*;
import java.awt.event.*;

/**
 * Admin Dashboard Frame.
 */
public class AdminDashboard extends Frame implements ActionListener {
    private User user;
    private Label welcomeLabel;

    public AdminDashboard(User user) {
        super("Admin Dashboard - " + user.getFullName());
        this.user = user;

        setSize(900, 600);
        setLocationRelativeTo(null);
        setBackground(new Color(240, 240, 240));
        setLayout(new BorderLayout());
        setResizable(false);

        // Header panel
        Panel headerPanel = new Panel();
        headerPanel.setBackground(new Color(70, 130, 220));
        headerPanel.setLayout(new BorderLayout());

        welcomeLabel = new Label("Welcome, " + user.getFullName() + " (Admin)", Label.LEFT);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
        logoutBtn.setBackground(new Color(200, 50, 50));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(this);
        Panel btnPanel = new Panel();
        btnPanel.setBackground(new Color(70, 130, 220));
        btnPanel.add(logoutBtn);
        headerPanel.add(btnPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Main content panel
        Panel contentPanel = new Panel();
        contentPanel.setBackground(new Color(240, 240, 240));
        contentPanel.setLayout(new GridLayout(2, 3, 20, 20));
        contentPanel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Add buttons
        contentPanel.add(createAdminButton("📦 Manage Inventory", "View and manage all inventory items"));
        contentPanel.add(createAdminButton("💰 Manage Sales", "View sales records and transactions"));
        contentPanel.add(createAdminButton("📊 Generate Reports", "Generate inventory and sales reports"));
        contentPanel.add(createAdminButton("👥 Manage Users", "Add, edit, and delete users"));
        contentPanel.add(createAdminButton("⚙️ Settings", "Configure system settings"));
        contentPanel.add(createAdminButton("📝 Logs", "View system activity logs"));

        Panel paddedContent = new Panel(new BorderLayout());
        paddedContent.add(contentPanel, BorderLayout.CENTER);
        paddedContent.setBackground(new Color(240, 240, 240));

        add(paddedContent, BorderLayout.CENTER);

        // Footer
        Panel footerPanel = new Panel();
        footerPanel.setBackground(new Color(200, 200, 200));
        Label statusLabel = new Label("Admin Dashboard - Inventory Management System v1.0");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        footerPanel.add(statusLabel);
        add(footerPanel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private Button createAdminButton(String title, String description) {
        Button btn = new Button(title + "\n" + description);
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setBackground(new Color(52, 152, 219));
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            Dialog d = new Dialog(this, "Feature", true);
            d.setSize(300, 150);
            d.setLocationRelativeTo(this);
            d.setLayout(new FlowLayout());
            d.add(new Label("Feature Coming Soon:\n" + title));
            Button closeBtn = new Button("Close");
            closeBtn.addActionListener(ev -> d.dispose());
            d.add(closeBtn);
            d.setVisible(true);
        });
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Logout")) {
            dispose();
            new LoginFrame();
        }
    }
}
