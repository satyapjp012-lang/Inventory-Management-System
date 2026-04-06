package inventory.management.system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Employee-facing sales form (New Sale only).
 * Sales history is visible in admin purchases section.
 */
public class EmployeeSalesPanel extends JPanel {

    private static final String[] SALES_HISTORY_COLS = {
            "ID", "Cust ID", "Customer", "Category", "Product", "Qty", "Selling Price", "Total", "Date"
    };

    /** Single in-memory ledger for all embedded sales UIs (demo). */
    private static DefaultTableModel sharedSalesHistoryModel;
    private static int sharedNextSaleTxnId = 1;

    private final DefaultTableModel salesHistoryModel;
    private JTabbedPane mainTabs;

    private static final Color BG_DARK = new Color(15, 23, 42);
    private static final Color BG_CARD = new Color(30, 41, 59);
    private static final Color ACCENT = new Color(56, 189, 248);
    private static final Color ACCENT2 = new Color(99, 102, 241);
    private static final Color TEXT_WHITE = new Color(241, 245, 249);
    private static final Color INPUT_BG = new Color(51, 65, 85);
    private static final Color TABLE_HEADER_BLUE = new Color(37, 99, 235);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_FIELD = new Font("SansSerif", Font.PLAIN, 15);
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 16);

    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");

    public EmployeeSalesPanel() {
        salesHistoryModel = getSharedSalesHistoryModel();

        setLayout(new BorderLayout());
        setBackground(BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel heading = new JLabel("Sales", SwingConstants.CENTER);
        heading.setFont(FONT_TITLE);
        heading.setForeground(ACCENT);
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        add(heading, BorderLayout.NORTH);

        mainTabs = new JTabbedPane(JTabbedPane.TOP);
        mainTabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainTabs.setBackground(BG_CARD);
        mainTabs.setForeground(TEXT_WHITE);

        mainTabs.addTab("New Sale", wrapTab(buildNewSaleTab()));

        add(mainTabs, BorderLayout.CENTER);
    }

    public static synchronized DefaultTableModel getSharedSalesHistoryModel() {
        if (sharedSalesHistoryModel == null) {
            sharedSalesHistoryModel = new DefaultTableModel(SALES_HISTORY_COLS, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        }
        return sharedSalesHistoryModel;
    }

    private JPanel wrapTab(JPanel inner) {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(BG_DARK);
        shell.add(inner, BorderLayout.CENTER);
        return shell;
    }

    private JPanel buildNewSaleTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblCustomerId = makeFormLabel("Customer ID");
        JLabel lblCustomer = makeFormLabel("Customer Name");
        JLabel lblCategory = makeFormLabel("Category");
        JLabel lblProduct = makeFormLabel("Product Name");
        JLabel lblQty = makeFormLabel("Quantity");
        JLabel lblPrice = makeFormLabel("Selling Price");
        JLabel lblDate = makeFormLabel("Date (dd/MM/yyyy)");

        JTextField tfCustomerId = makeField();
        JTextField tfCustomer = makeField();
        JComboBox<String> cbCategory = makeCombo(ProductCatalog.categoryComboItems());
        JComboBox<String> cbProduct = makeCombo(new String[] { ProductCatalog.PLACEHOLDER });
        cbCategory.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                ProductCatalog.refreshProductCombo(cbProduct, cbCategory.getSelectedItem());
            }
        });

        JTextField tfQty = makeField();
        JTextField tfPrice = makeField();
        JTextField tfDate = makeField();

        int row = 0;
        addFormRow(panel, gbc, row++, lblCustomerId, tfCustomerId);
        addFormRow(panel, gbc, row++, lblCustomer, tfCustomer);
        addFormRow(panel, gbc, row++, lblCategory, cbCategory);
        addFormRow(panel, gbc, row++, lblProduct, cbProduct);
        addFormRow(panel, gbc, row++, lblQty, tfQty);
        addFormRow(panel, gbc, row++, lblPrice, tfPrice);
        addFormRow(panel, gbc, row++, lblDate, tfDate);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submit = makePrimaryButton("Submit Sale");
        submit.addActionListener(e -> {
            String customerIdStr = tfCustomerId.getText().trim();
            String customer = tfCustomer.getText().trim();
            String category = String.valueOf(cbCategory.getSelectedItem());
            String product = String.valueOf(cbProduct.getSelectedItem());
            String qtyStr = tfQty.getText().trim();
            String priceStr = tfPrice.getText().trim();
            String dateStr = tfDate.getText().trim();

            String idErr = validateEntityId(customerIdStr, "Customer ID");
            if (idErr != null) {
                JOptionPane.showMessageDialog(this, idErr, "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (ProductCatalog.isPlaceholder(category)) {
                JOptionPane.showMessageDialog(this, "Please select a category.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (ProductCatalog.isPlaceholder(product)) {
                JOptionPane.showMessageDialog(this, "Please select a product.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String err = validateSaleRest(customer, qtyStr, priceStr, dateStr);
            if (err != null) {
                JOptionPane.showMessageDialog(this, err, "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int qty = Integer.parseInt(qtyStr);
            double price = Double.parseDouble(priceStr);
            double total = qty * price;
            int custId = Integer.parseInt(customerIdStr);
            int txnId;
            synchronized (EmployeeSalesPanel.class) {
                txnId = sharedNextSaleTxnId++;
            }
            salesHistoryModel.addRow(new Object[] {
                    txnId, custId, customer, category, product, qty,
                    formatRupee(price), formatRupee(total), dateStr
            });
            clearAfterSale(tfCustomerId, tfCustomer, cbCategory, cbProduct, tfQty, tfPrice, tfDate);
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                    "Sale recorded successfully.\nCustomer ID: " + customerIdStr + "\nCustomer: " + customer
                            + "\nCategory: " + category + "\nProduct: " + product + "\nTotal amount: "
                            + formatRupee(total),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            mainTabs.setSelectedIndex(0);
        });
        panel.add(submit, gbc);

        return panel;
    }

    private JPanel tableInScrollPane(DefaultTableModel model) {
        JPanel holder = new JPanel(new BorderLayout());
        holder.setBackground(BG_DARK);

        JTable table = new JTable(model);
        table.setFont(FONT_FIELD);
        table.setForeground(TEXT_WHITE);
        table.setBackground(BG_CARD);
        table.setGridColor(new Color(71, 85, 105));
        table.setRowHeight(26);
        table.setFillsViewportHeight(true);

        styleTableHeader(table);

        DefaultTableCellRenderer left = new DefaultTableCellRenderer();
        left.setBackground(BG_CARD);
        left.setForeground(TEXT_WHITE);
        left.setFont(FONT_FIELD);
        for (int c = 0; c < table.getColumnCount(); c++) {
            table.getColumnModel().getColumn(c).setCellRenderer(left);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(ACCENT2, 1));
        scroll.getViewport().setBackground(BG_CARD);
        holder.add(scroll, BorderLayout.CENTER);
        return holder;
    }

    private void styleTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_BLUE);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setReorderingAllowed(false);
        DefaultTableCellRenderer hr = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                comp.setBackground(TABLE_HEADER_BLUE);
                comp.setForeground(Color.WHITE);
                comp.setFont(new Font("SansSerif", Font.BOLD, 14));
                setHorizontalAlignment(SwingConstants.CENTER);
                return comp;
            }
        };
        header.setDefaultRenderer(hr);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, JLabel label, JTextField field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, JLabel label, JComboBox<String> combo) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(label, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(combo, gbc);
    }

    private JLabel makeFormLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_WHITE);
        return l;
    }

    private JTextField makeField() {
        JTextField t = new JTextField(24);
        t.setFont(FONT_FIELD);
        t.setBackground(INPUT_BG);
        t.setForeground(TEXT_WHITE);
        t.setCaretColor(ACCENT);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT2, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return t;
    }

    private JComboBox<String> makeCombo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(FONT_FIELD);
        c.setBackground(INPUT_BG);
        c.setForeground(TEXT_WHITE);
        return c;
    }

    private JButton makePrimaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BTN);
        b.setBackground(ACCENT2);
        b.setForeground(TEXT_WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        return b;
    }

    private void clearAfterSale(JTextField tfCustomerId, JTextField tfCustomer, JComboBox<String> cbCategory,
            JComboBox<String> cbProduct, JTextField tfQty, JTextField tfPrice, JTextField tfDate) {
        tfCustomerId.setText("");
        tfCustomer.setText("");
        cbCategory.setSelectedIndex(0);
        ProductCatalog.refreshProductCombo(cbProduct, cbCategory.getSelectedItem());
        tfQty.setText("");
        tfPrice.setText("");
        tfDate.setText("");
    }

    private String validateEntityId(String idStr, String fieldLabel) {
        if (idStr.isEmpty()) {
            return fieldLabel + " is required.";
        }
        try {
            int id = Integer.parseInt(idStr);
            if (id <= 0) {
                return fieldLabel + " must be a positive whole number.";
            }
        } catch (NumberFormatException ex) {
            return fieldLabel + " must be a valid whole number.";
        }
        return null;
    }

    private String validateSaleRest(String customer, String qtyStr, String priceStr, String dateStr) {
        if (customer.isEmpty()) {
            return "Customer name is required.";
        }
        if (qtyStr.isEmpty()) {
            return "Quantity is required.";
        }
        try {
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                return "Quantity must be a positive whole number.";
            }
        } catch (NumberFormatException ex) {
            return "Quantity must be a valid whole number.";
        }
        if (priceStr.isEmpty()) {
            return "Price is required.";
        }
        try {
            double price = Double.parseDouble(priceStr);
            if (price < 0) {
                return "Price cannot be negative.";
            }
        } catch (NumberFormatException ex) {
            return "Price must be a valid number (e.g. 199.50).";
        }
        if (dateStr.isEmpty()) {
            return "Date is required.";
        }
        if (!DATE_PATTERN.matcher(dateStr).matches()) {
            return "Date must be in dd/MM/yyyy format.";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            sdf.parse(dateStr);
        } catch (Exception ex) {
            return "Invalid calendar date.";
        }
        return null;
    }

    private static String formatRupee(double amount) {
        return String.format(Locale.US, "₹%,.2f", amount);
    }
}
