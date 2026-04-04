package inventory.management.system;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.ImageIcon;

/**
 * LOGIN PAGE – the home / entry point of the application.
 * Admin logs in → AdminDashboard
 * Employee logs in → EmployeeDashboard (scoped to their category)
 */
public class LoginFrame extends Frame implements ActionListener {

    // ── UI components ──────────────────────────────────────────────────────────
    private TextField tfUsername, tfPassword;
    private Button btnLogin, btnClear, btnExit, btnEmployeeLogin;
    private Label lblStatus;
    private Checkbox cbShowPwd;
    private Panel header, imagePanel, card;
    private Canvas imgCanvas;

    // ── Colours & fonts ────────────────────────────────────────────────────────
    private static final Color BG_DARK = new Color(15, 23, 42);   // slate-900
    private static final Color BG_CARD = new Color(30, 41, 59);   // slate-800
    private static final Color ACCENT = new Color(56, 189, 248);   // sky-400
    private static final Color ACCENT2 = new Color(99, 102, 241);   // indigo-500
    private static final Color TEXT_WHITE = new Color(241, 245, 249);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);
    private static final Color ERROR_RED = new Color(248, 113, 113);
    private static final Color SUCCESS = new Color(52, 211, 153);

    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 26);
    private static final Font FONT_SUB = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 13);
    private static final Font FONT_INPUT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 14);

    // ── Constructor ────────────────────────────────────────────────────────────
    public LoginFrame() {
        super("Inventory Management System – Login");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setBackground(BG_DARK);
        setLayout(null);
        setResizable(true);

        buildUI();

        // Add resize listener to handle responsive layout
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                recalculateLayout();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DBConnection.closeConnection();
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
    }

    // ── Build UI ───────────────────────────────────────────────────────────────
    private void buildUI() {

        // ── Header banner with logo ────────────────────────────────────────────
        header = new Panel(null) {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT2, getWidth(), 0, ACCENT);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            }
        };
        header.setBounds(0, 10, 1200, 160);
        add(header);

        // App icon/title inside header
        Label appIcon = new Label("📦", Label.CENTER);
        appIcon.setFont(new Font("SansSerif", Font.PLAIN, 36));
        appIcon.setForeground(TEXT_WHITE);
        appIcon.setBounds(20, 30, 120, 80);
        header.add(appIcon);

        Label appTitle = new Label("INVENTORY MANAGEMENT SYSTEM", Label.LEFT);
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        appTitle.setForeground(TEXT_WHITE);
        appTitle.setBounds(150, 30, 600, 35);
        header.add(appTitle);

        Label appSub = new Label("Hardware  •  Grocery  •  Clothes", Label.LEFT);
        appSub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        appSub.setForeground(new Color(200, 220, 255));
        appSub.setBounds(150, 70, 600, 20);
        header.add(appSub);

        // Logo placeholder area on the right top using header hero section
        Image img = new ImageIcon(getClass().getResource("Logo.png")).getImage();
        Canvas canvas = new Canvas() {
            public void paint(Graphics g) {
                g.drawImage(img, 0, 0, 120, 120, this);
            }
        };
        canvas.setBounds(780, 10, 120, 120);
        header.add(canvas);

        // ── Admin Image Panel ──────────────────────────────────────────────────
        imagePanel = new Panel(null) {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        imagePanel.setBounds(20, 200, 380, 420);
        add(imagePanel);

        // Admin image inside the panel
        try {
            Image adminImg = new ImageIcon(getClass().getResource("admin.png")).getImage();
            imgCanvas = new Canvas() {
                public void paint(Graphics g) {
                    g.drawImage(adminImg, 0, 0, getWidth(), getHeight(), this);
                }
            };
            imgCanvas.setBounds(30, 30, 320, 360);
            imagePanel.add(imgCanvas);
        } catch (Exception e) {
            // Fallback: show placeholder if image not found
            Label placeholder = new Label("👨‍💼\nAdmin Image", Label.CENTER);
            placeholder.setFont(new Font("SansSerif", Font.BOLD, 24));
            placeholder.setForeground(ACCENT);
            placeholder.setBounds(20, 20, 340, 380);
            imagePanel.add(placeholder);
        }

        // ── Card panel ─────────────────────────────────────────────────────────
        card = new Panel(null) {
            @Override
            public void paint(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        card.setBounds(420, 200, 440, 420);
        add(card);

        // Card title
        Label loginTitle = new Label("Admin / Employee Login", Label.CENTER);
        loginTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        loginTitle.setForeground(ACCENT);
        loginTitle.setBounds(0, 20, 440, 28);
        card.add(loginTitle);

        Label loginSub = new Label("Enter your credentials to continue", Label.CENTER);
        loginSub.setFont(FONT_SUB);
        loginSub.setForeground(TEXT_MUTED);
        loginSub.setBounds(0, 50, 440, 20);
        card.add(loginSub);

        // ── Username field ─────────────────────────────────────────────────────
        Label lblUser = new Label("Username");
        lblUser.setFont(FONT_LABEL);
        lblUser.setForeground(TEXT_WHITE);
        lblUser.setBounds(40, 90, 360, 20);
        card.add(lblUser);

        tfUsername = new TextField();
        tfUsername.setFont(FONT_INPUT);
        tfUsername.setBackground(new Color(51, 65, 85));
        tfUsername.setForeground(TEXT_WHITE);
        tfUsername.setBounds(40, 114, 360, 36);
        card.add(tfUsername);

        // ── Password field ─────────────────────────────────────────────────────
        Label lblPwd = new Label("Password");
        lblPwd.setFont(FONT_LABEL);
        lblPwd.setForeground(TEXT_WHITE);
        lblPwd.setBounds(40, 165, 360, 20);
        card.add(lblPwd);

        tfPassword = new TextField();
        tfPassword.setEchoChar('●');
        tfPassword.setFont(FONT_INPUT);
        tfPassword.setBackground(new Color(51, 65, 85));
        tfPassword.setForeground(TEXT_WHITE);
        tfPassword.setBounds(40, 189, 360, 36);
        card.add(tfPassword);

        // Show password checkbox
        cbShowPwd = new Checkbox("Show Password");
        cbShowPwd.setFont(FONT_SUB);
        cbShowPwd.setForeground(TEXT_MUTED);
        cbShowPwd.setBackground(BG_CARD);
        cbShowPwd.setBounds(40, 234, 160, 24);
        cbShowPwd.addItemListener(e -> {
            tfPassword.setEchoChar(cbShowPwd.getState() ? '\0' : '●');
        });
        card.add(cbShowPwd);

        // ── Status label ───────────────────────────────────────────────────────
        lblStatus = new Label("", Label.CENTER);
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblStatus.setForeground(ERROR_RED);
        lblStatus.setBounds(0, 268, 440, 22);
        card.add(lblStatus);

        // ── Buttons ────────────────────────────────────────────────────────────
        btnLogin = makeButton("🔐  LOGIN", ACCENT2, TEXT_WHITE);
        btnLogin.setBounds(40, 300, 130, 42);
        card.add(btnLogin);

        btnEmployeeLogin = makeButton("👷 Employee Login", new Color(34, 197, 94), TEXT_WHITE);
        btnEmployeeLogin.setBounds(180, 300, 130, 42);
        card.add(btnEmployeeLogin);

        btnClear = makeButton("✖  CLEAR", new Color(100, 116, 139), TEXT_WHITE);
        btnClear.setBounds(260, 352, 80, 42);
        card.add(btnClear);

        btnExit = makeButton("⏻  EXIT", new Color(153, 27, 27), TEXT_WHITE);
        btnExit.setBounds(360, 352, 80, 42);
        card.add(btnExit);

        // Demo info label
        Label hint = new Label("Demo: admin / admin123", Label.CENTER);
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(TEXT_MUTED);
        hint.setBounds(0, 406, 440, 18);
        card.add(hint);

        // Register actions
        btnLogin.addActionListener(this);
        btnClear.addActionListener(this);
        btnExit.addActionListener(this);
        btnEmployeeLogin.addActionListener(this);

        // Allow Enter key to trigger login
        tfPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });

        // Initial layout calculation
        recalculateLayout();
    }

    // ── Helper: create styled button ──────────────────────────────────────────
    private Button makeButton(String text, Color bg, Color fg) {
        Button b = new Button(text);
        b.setFont(FONT_BTN);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Responsive Layout Update ──────────────────────────────────────────────
    private void recalculateLayout() {
        int width = getWidth();
        int height = getHeight();

        // Header uses full width
        header.setBounds(0, 10, width, 160);

        // Calculate proportional panel layout
        // Layout: |margin|image_panel|gap|login_panel|margin|
        int topMargin = 180;
        int panelHeight = Math.max(350, Math.min(450, height - 230));
        int usableWidth = Math.max(400, width - 100);
        int panelWidth = (usableWidth - 30) / 2;  // 30px gap
        int leftMargin = (width - usableWidth) / 2;
        int gapSize = 30;

        // Image panel
        int imagePanelX = leftMargin;
        imagePanel.setBounds(imagePanelX, topMargin, panelWidth, panelHeight);

        // Login panel
        int cardPanelX = imagePanelX + panelWidth + gapSize;
        card.setBounds(cardPanelX, topMargin, panelWidth, panelHeight);

        // Update image canvas with padding
        if (imgCanvas != null) {
            int padding = 15;
            imgCanvas.setBounds(padding, padding, panelWidth - (padding * 2), panelHeight - (padding * 2));
        }
    }

    // ── ActionListener ─────────────────────────────────────────────────────────
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnLogin) doLogin();
        else if (src == btnClear) {
            tfUsername.setText("");
            tfPassword.setText("");
            lblStatus.setText("");
        }
        else if (src == btnExit) {
            DBConnection.closeConnection();
            dispose();
            System.exit(0);
        }
        else if (src == btnEmployeeLogin) {
            // Employee Dashboard is being developed by friend - button placeholder
        }
    }

    // ── Login logic ────────────────────────────────────────────────────────────
    private void doLogin() {
        String username = tfUsername.getText().trim();
        String password = tfPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            setStatus("Please enter both username and password.", false);
            return;
        }

        User user = DBConnection.authenticate(username, password);

        if (user != null) {
            if (!user.isAdmin()) {
                setStatus("Please use the Employee Login button.", false);
                return;
            }
            setStatus("Welcome, " + user.getFullName() + "!", true);

            // Small delay so the user sees the welcome message
            new Thread(() -> {
                try {
                    Thread.sleep(700);
                } catch (InterruptedException ex) {
                    // ignore
                }
                dispose();
                new AdminDashboard(user);
            }).start();
        } else {
            setStatus("Invalid username or password!", false);
        }
    }

    private void setStatus(String msg, boolean ok) {
        lblStatus.setForeground(ok ? SUCCESS : ERROR_RED);
        lblStatus.setText(msg);
    }

    // ── main ───────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        EventQueue.invokeLater(() -> new LoginFrame());
    }
}
