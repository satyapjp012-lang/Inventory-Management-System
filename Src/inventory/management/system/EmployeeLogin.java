package inventory.management.system;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;

public class EmployeeLogin extends Frame {

    Button login, reset, back;
    Image image, logo;
    Panel loginPanel;
    TextField tfUser, tfPass;
    Label lblMessage;

    // ! Function to check is the entered email is valid or not.
    boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(regex);
    }

    // ! Function to check is the entered email ibelongs to the organization or not.
    boolean isCompanyEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@bigbazaar\\.com$");
    }

    public EmployeeLogin() {

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        setTitle("Login - Inventory System");

        // ! Overall layout: left panel (fixed width) + right area for image
        setLayout(new BorderLayout());
        ImageIcon Image = new ImageIcon("Src\\Icons\\Logo.png");
        setIconImage(Image.getImage());

        // ! Left login panel
        loginPanel = new Panel() {
            public void paint(Graphics g) {
                super.paint(g);
                if (logo != null) {
                    g.drawImage(logo, 20, 20, 200, 200, this);
                } else {
                    g.setColor(new Color(240, 240, 240));
                    g.fillRoundRect(40, 40, getWidth() - 80, getHeight() - 80, 20, 20);
                    g.setColor(Color.DARK_GRAY);
                    g.drawString("Image area (place an illustration here)", 60, 80);
                }
            }
        };
        loginPanel.setBackground(new Color(20, 24, 40));
        loginPanel.setPreferredSize(new Dimension(700, 0));
        loginPanel.setLayout(null);

        Label heading = new Label("Employee Login", Label.CENTER);
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("SansSerif", Font.BOLD, 52));
        heading.setBounds(150, 230, 450, 70);
        loginPanel.add(heading);

        Label lblUser = new Label("Username: ");
        lblUser.setBounds(75, 370, 100, 35);
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblUser.setForeground(Color.WHITE);
        loginPanel.add(lblUser);

        tfUser = new TextField();
        tfUser.setBounds(200, 370, 330, 35);
        tfUser.setFont(new Font("SansSerif", Font.BOLD, 20));
        loginPanel.add(tfUser);

        Label lblPass = new Label("Password: ");
        lblPass.setForeground(Color.WHITE);
        lblPass.setBounds(75, 440, 100, 35);
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 20));
        loginPanel.add(lblPass);

        tfPass = new TextField();
        tfPass.setEchoChar('*');
        tfPass.setBounds(200, 440, 330, 35);
        tfPass.setFont(new Font("SansSerif", Font.BOLD, 30));
        tfPass.setForeground(Color.BLACK);
        loginPanel.add(tfPass);

        login = new Button("Login");
        login.setBounds(200, 520, 140, 35);
        login.setFont(new Font("SansSerif", Font.BOLD, 20));
        loginPanel.add(login);

        reset = new Button("Reset");
        reset.setBounds(390, 520, 140, 35);
        reset.setFont(new Font("SansSerif", Font.BOLD, 20));
        loginPanel.add(reset);

        back = new Button("Back");
        back.setBounds(200, 590, 330, 35);
        back.setFont(new Font("SansSerif", Font.BOLD, 20));
        loginPanel.add(back);

        lblMessage = new Label("", Label.CENTER);
        lblMessage.setForeground(Color.ORANGE);
        lblMessage.setBounds(200, 480, 330, 25);
        lblMessage.setFont(new Font("SansSerif", Font.PLAIN, 15));
        loginPanel.add(lblMessage);

        // ! Right panel reserved for image / illustration
        Panel imagePanel = new Panel() {
            public void paint(Graphics g) {
                super.paint(g);
                if (image != null) {
                    int w = getWidth();
                    int h = getHeight();
                    g.drawImage(image, 20, 50, w - 40, h - 100, this);
                } else {
                    g.setColor(new Color(240, 240, 240));
                    g.fillRoundRect(40, 40, getWidth() - 80, getHeight() - 80, 20, 20);
                    g.setColor(Color.DARK_GRAY);
                    g.drawString("Image area (place an illustration here)", 60, 80);
                }
            }
        };
        imagePanel.setBackground(new Color(20, 24, 40));

        // ! Attempt to load an image from the Icons folder (optional). If not present,
        // ! placeholder is shown.
        try {
            image = Toolkit.getDefaultToolkit().getImage("Src/Icons/Employee.png");
            logo = Toolkit.getDefaultToolkit().getImage("Src/Icons/Logo.png");
        } catch (Exception ex) {
            image = null;
            logo = null;
        }

        add(loginPanel, BorderLayout.WEST);
        add(imagePanel, BorderLayout.CENTER);

        // ! Button actions
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Demo mode: open sales workspace directly on login click.
                dispose();
                new EmployeeDashboardFrame();
            }
        });

        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tfUser.setText("");
                tfPass.setText("");
                lblMessage.setText(" ");
            }
        });

        back.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame();
            }
        });

        setBackground(new Color(30, 30, 60));
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setResizable(false);
        setVisible(true);

    }

    public static void main(String[] args) {
        new EmployeeLogin();
    }
}
