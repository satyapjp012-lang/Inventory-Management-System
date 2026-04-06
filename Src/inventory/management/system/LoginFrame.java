package inventory.management.system;

import java.awt.*;
import java.awt.event.*;

import javax.swing.ImageIcon;

/**
 * LOGIN PAGE – the home / entry point of the application.
 * Admin logs in → AdminDashboard
 * Employee logs in → EmployeeDashboard (scoped to their category)
 */

public class LoginFrame extends Frame implements ActionListener {

    // ! ── UI components ──────────────────────────────────────────────────────────
    private TextField tfUsername, tfPassword;
    private Button btnLogin, btnClear, btnExit, btnEmployeeLogin;
    private Label lblStatus;
    private Checkbox cbShowPwd;
    private Panel header, imagePanel, card;
    private Canvas imgCanvas;

    // ! ── Colours & fonts ────────────────────────────────────────────────────────
    private static final Color BG_DARK = new Color(15, 23, 42); // slate-900
    private static final Color BG_CARD = new Color(30, 41, 59); // slate-800
    private static final Color ACCENT = new Color(56, 189, 248); // sky-400
    private static final Color ACCENT2 = new Color(99, 102, 241); // indigo-500
    private static final Color TEXT_WHITE = new Color(241, 245, 249);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);
    private static final Color ERROR_RED = new Color(248, 113, 113);
    private static final Color SUCCESS = new Color(52, 211, 153);

    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 26);
    private static final Font FONT_INPUT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 18);

    // ! Function to check is the entered email is valid or not.
    boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(regex);
    }

    // ! Function to check is the entered email ibelongs to the organization or not.
    boolean isCompanyEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@bigbazaar\\.com$");
    }

    // ! ── Constructor ────────────────────────────────────────────────────────────
    public LoginFrame() {
        super("Inventory Management System - Login");
        // setSize(1200, 700);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setBackground(BG_DARK);
        setLayout(null);
        setResizable(false);
        ImageIcon image = new ImageIcon("Src\\Icons\\Logo.png");
        setIconImage(image.getImage());

        buildUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
    }

    // ! ── Build UI ───────────────────────────────────────────────────────────────
    private void buildUI() {

        // ! ── Header banner with logo ────────────────────────────────────────────
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
        header.setBounds(0, 10, 1550, 160);
        add(header);

        // ! App icon/title inside header
        Label appIcon = new Label("Bigbazaar", Label.CENTER);
        appIcon.setFont(new Font("SansSerif", Font.BOLD, 36));
        appIcon.setForeground(TEXT_WHITE);
        appIcon.setBackground(ACCENT2);
        appIcon.setBounds(20, 50, 180, 80);
        header.add(appIcon);

        Label appTitle = new Label("INVENTORY MANAGEMENT SYSTEM", Label.CENTER);
        appTitle.setFont(new Font("SansSerif", Font.BOLD, 38));
        appTitle.setForeground(TEXT_WHITE);
        appTitle.setBackground(ACCENT);
        appTitle.setBounds(400, 45, 750, 70);
        header.add(appTitle);

        Label appSub = new Label("         Hardware         Grocery         Shoes", Label.LEFT);
        appSub.setFont(new Font("SansSerif", Font.PLAIN, 20));
        appSub.setForeground(new Color(200, 220, 255));
        appSub.setBounds(400, 115, 750, 30);
        header.add(appSub);

        // ! Logo placeholder area on the right top using header hero section
        Image img = Toolkit.getDefaultToolkit().getImage("Src/Icons/Logo.png");
        Canvas canvas = new Canvas() {
            public void paint(Graphics g) {
                g.drawImage(img, 0, 0, 120, 120, this);
            }
        };
        canvas.setBounds(1320, 30, 120, 120);
        canvas.setBackground(Color.WHITE);
        header.add(canvas);

        // ! ── Admin Image Panel ──────────────────────────────────────────────────
        imagePanel = new Panel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(BG_CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        imagePanel.setLayout(null);
        imagePanel.setBounds(20, 200, 700, 700);
        add(imagePanel);

        // ! Admin image inside the panel
        try {
            Image adminImg = Toolkit.getDefaultToolkit().getImage("Src/Icons/Admin.png");
            imgCanvas = new Canvas(null) {
                public void paint(Graphics g) {
                    g.drawImage(adminImg, 0, 0, getWidth(), getHeight(), this);
                }
            };
            imagePanel.setLayout(null);
            imgCanvas.setBounds(30, 20, 640, 660);
            imagePanel.add(imgCanvas, Canvas.CENTER_ALIGNMENT);
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
        card.setBounds(900, 275, 450, 550);
        add(card, Panel.CENTER_ALIGNMENT);

        // ! Card title
        Label loginTitle = new Label("Admin Login", Label.CENTER);
        loginTitle.setFont(FONT_TITLE);
        loginTitle.setForeground(ACCENT);
        loginTitle.setBackground(BG_CARD);
        loginTitle.setBounds(0, 20, 440, 28);
        card.add(loginTitle, Panel.CENTER_ALIGNMENT);

        Label loginSub = new Label("Enter your credentials to continue", Label.CENTER);
        loginSub.setFont(new Font("SansSerif", Font.PLAIN, 16));
        loginSub.setForeground(TEXT_MUTED);
        loginSub.setBackground(BG_CARD);
        loginSub.setBounds(0, 60, 440, 20);
        card.add(loginSub);

        // ! ── Username field ─────────────────────────────────────────────────────
        Label lblUser = new Label("Username");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblUser.setForeground(TEXT_WHITE);
        lblUser.setBackground(BG_CARD);
        lblUser.setBounds(40, 115, 360, 20);
        card.add(lblUser);

        tfUsername = new TextField();
        tfUsername.setFont(FONT_INPUT);
        tfUsername.setBackground(new Color(51, 65, 85));
        tfUsername.setForeground(TEXT_WHITE);
        tfUsername.setBounds(40, 145, 360, 36);
        card.add(tfUsername);

        // ! ── Password field ─────────────────────────────────────────────────────
        Label lblPwd = new Label("Password");
        lblPwd.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblPwd.setForeground(TEXT_WHITE);
        lblPwd.setBackground(BG_CARD);
        lblPwd.setBounds(40, 205, 360, 20);
        card.add(lblPwd);

        tfPassword = new TextField();
        tfPassword.setEchoChar('●');
        tfPassword.setFont(FONT_INPUT);
        tfPassword.setBackground(new Color(51, 65, 85));
        tfPassword.setForeground(TEXT_WHITE);
        tfPassword.setBounds(40, 235, 360, 36);
        card.add(tfPassword);

        // ! Show password checkbox
        cbShowPwd = new Checkbox("Show Password");
        cbShowPwd.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cbShowPwd.setForeground(TEXT_MUTED);
        cbShowPwd.setBackground(BG_CARD);
        cbShowPwd.setBounds(40, 280, 160, 24);
        cbShowPwd.addItemListener(e -> {
            tfPassword.setEchoChar(cbShowPwd.getState() ? '\0' : '●');
        });
        card.add(cbShowPwd);

        // ! ── Status label ───────────────────────────────────────────────────────
        lblStatus = new Label("", Label.CENTER);
        lblStatus.setFont(new Font("SansSerif", Font.ITALIC, 13));
        lblStatus.setForeground(ERROR_RED);
        lblStatus.setBackground(BG_CARD);
        lblStatus.setBounds(0, 300, 440, 22);
        card.add(lblStatus);

        // ! ── Buttons ────────────────────────────────────────────────────────────
        btnLogin = makeButton("Login", ACCENT2, TEXT_WHITE);
        btnLogin.setBounds(40, 340, 150, 42);
        card.add(btnLogin);

        btnEmployeeLogin = makeButton("Employee Login", new Color(34, 197, 94), TEXT_WHITE);
        btnEmployeeLogin.setBounds(250, 340, 150, 42);
        card.add(btnEmployeeLogin);

        btnClear = makeButton("Clear", new Color(100, 116, 139), TEXT_WHITE);
        btnClear.setBounds(40, 420, 150, 42);
        card.add(btnClear, Button.CENTER_ALIGNMENT);

        btnExit = makeButton("Exit", new Color(153, 27, 27), TEXT_WHITE);
        btnExit.setBounds(250, 420, 150, 42);
        card.add(btnExit);

        // ! Demo info label
        Label hint = new Label("Demo: admin@bigbazaar.com / admin123", Label.CENTER);
        hint.setFont(new Font("SansSerif", Font.ITALIC, 16));
        hint.setForeground(TEXT_MUTED);
        hint.setBackground(BG_CARD);
        hint.setBounds(0, 480, 440, 20);
        card.add(hint);

        // ! Register actions
        btnLogin.addActionListener(this);
        btnClear.addActionListener(this);
        btnExit.addActionListener(this);
        btnEmployeeLogin.addActionListener(this);

        // ! Allow Enter key to trigger login
        tfPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    doLogin();
            }
        });

    }

    // ! ── Helper: create styled button ──────────────────────────────────────────
    private Button makeButton(String text, Color bg, Color fg) {
        Button b = new Button(text);
        b.setFont(FONT_BTN);
        b.setBackground(bg);
        b.setForeground(fg);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ! ── ActionListener ─────────────────────────────────────────────────────────
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnLogin)
            doLogin();

        else if (src == btnClear) {
            tfUsername.setText("");
            tfPassword.setText("");
            lblStatus.setText("");
        } else if (src == btnExit) {
            dispose();
            System.exit(0);
        } else if (src == btnEmployeeLogin) {
            dispose();
            new EmployeeLogin();
        }
    }

    //! ── Login logic ────────────────────────────────────────────────────────────
    private void doLogin() {
        String username = tfUsername.getText().trim();
        String password = tfPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            setStatus("Please enter both username and password.", false);
            return;
        } else if (username.contains(" ")) {
            setStatus("Username does not contain Space", false);
            return;
        } else if (!isValidEmail(username)) {
            setStatus("Invalid email format!", false);
            return;
        } else if (!isCompanyEmail(username)) {
            setStatus("Please use company email only!", false);
            return;
        } else {
            // TODO: Replace with real authentication logic (DB/API)
            if (username.equals("admin@bigbazaar.com") && password.equals("admin123")) {
                setStatus("Login successful!", true);
                new AdminFeatures();
                dispose();
            } else {
                setStatus("Invalid credentials!", false);
            }
        }
    }

    private void setStatus(String msg, boolean ok) {
        lblStatus.setForeground(ok ? SUCCESS : ERROR_RED);
        lblStatus.setText(msg);
    }

    // ── main ───────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        // System.setProperty("awt.useSystemAAFontSettings", "on");
        // System.setProperty("swing.aatext", "true");
        EventQueue.invokeLater(() -> new LoginFrame());
    }
}
