package inventory.management.system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ReportPanel extends JPanel {

    private JTabbedPane mainTabs;

    private static final Color BG_DARK = new Color(15, 23, 42);
    private static final Color BG_CARD = new Color(30, 41, 59);
    private static final Color ACCENT = new Color(56, 189, 248);
    private static final Color ACCENT2 = new Color(99, 102, 241);
    private static final Color TEXT_WHITE = new Color(241, 245, 249);
    private static final Color INPUT_BG = new Color(51, 65, 85);
    private static final Color TABLE_HEADER_BLUE = new Color(37, 99, 235);
    private static final Font FONT_LABEL = new Font("SansSerif", Font.BOLD, 16);
    private static final Font FONT_FIELD = new Font("SansSerif", Font.BOLD, 15);
    private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 22);
    private static final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 16);

    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$");

    private final DefaultTableModel lowStockModel;

    public ReportPanel() {
        lowStockModel = createNonEditableTableModel(new String[] { "Product ID", "Product Name", "Available Stock" });

        setLayout(new BorderLayout());
        setBackground(BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel heading = new JLabel("Reports", SwingConstants.CENTER);
        heading.setFont(FONT_TITLE);
        heading.setForeground(ACCENT);
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        add(heading, BorderLayout.NORTH);

        mainTabs = new JTabbedPane(JTabbedPane.TOP);
        mainTabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainTabs.setBackground(BG_CARD);
        mainTabs.setForeground(TEXT_WHITE);

        mainTabs.addTab("Low Stocks", wrapTab(buildLowStocksTab()));
        mainTabs.addTab("Profit & Loss", wrapTab(buildProfitLossTab()));

        mainTabs.addChangeListener(e -> {
            int idx = mainTabs.getSelectedIndex();
            if (idx == 0) {
                heading.setText("Low Stocks");
            } else if (idx == 1) {
                heading.setText("Profit & Loss");
            } else {
                heading.setText("Reports");
            }
        });

        add(mainTabs, BorderLayout.CENTER);
    }

    private static DefaultTableModel createNonEditableTableModel(String[] columnNames) {
        return new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JPanel wrapTab(JPanel inner) {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(BG_DARK);
        shell.add(inner, BorderLayout.CENTER);
        return shell;
    }

    private JPanel buildLowStocksTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblThreshold = new JLabel("Threshold");
        lblThreshold.setFont(FONT_LABEL);
        lblThreshold.setForeground(TEXT_WHITE);

        JTextField tfThreshold = new JTextField("5", 6);
        tfThreshold.setFont(FONT_FIELD);
        tfThreshold.setBackground(INPUT_BG);
        tfThreshold.setForeground(TEXT_WHITE);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(FONT_BTN);
        btnRefresh.setBackground(ACCENT2);
        btnRefresh.setForeground(TEXT_WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        p.add(lblThreshold, gbc);
        gbc.gridx = 1;
        p.add(tfThreshold, gbc);
        gbc.gridx = 2;
        p.add(btnRefresh, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        JPanel tableHolder = tableInScrollPane(lowStockModel);
        p.add(tableHolder, gbc);

        btnRefresh.addActionListener(a -> {
            String t = tfThreshold.getText().trim();
            int thr = 5;
            try {
                thr = Integer.parseInt(t);
                if (thr < 0)
                    thr = 0;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid threshold", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            loadLowStockData(thr);
        });

        // initial load
        loadLowStockData(5);

        return p;
    }

    private JPanel buildProfitLossTab() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(BG_DARK);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Left column: inputs + summary cards
        JPanel leftCol = new JPanel(new GridBagLayout());
        leftCol.setBackground(BG_DARK);
        leftCol.setBorder(BorderFactory.createEmptyBorder(40, 10, 10, 10));
        GridBagConstraints lg = new GridBagConstraints();
        lg.insets = new Insets(6, 6, 6, 6);
        lg.anchor = GridBagConstraints.NORTHWEST;

        JLabel lblFrom = new JLabel("From (dd/mm/yyyy)");
        lblFrom.setFont(FONT_LABEL);
        lblFrom.setForeground(TEXT_WHITE);
        JTextField tfFrom = new JTextField(10);
        tfFrom.setFont(new Font("SansSerif", Font.BOLD, 18));
        tfFrom.setBackground(INPUT_BG);
        tfFrom.setForeground(TEXT_WHITE);
        tfFrom.setPreferredSize(new Dimension(380, 40));

        JLabel lblTo = new JLabel("To (dd/mm/yyyy)");
        lblTo.setFont(FONT_LABEL);
        lblTo.setForeground(TEXT_WHITE);
        JTextField tfTo = new JTextField(10);
        tfTo.setFont(new Font("SansSerif", Font.BOLD, 18));
        tfTo.setBackground(INPUT_BG);
        tfTo.setForeground(TEXT_WHITE);
        tfTo.setPreferredSize(new Dimension(380, 40));

        JButton btnCalc = new JButton("Calculate");
        btnCalc.setFont(FONT_BTN);
        btnCalc.setBackground(ACCENT2);
        btnCalc.setForeground(TEXT_WHITE);

        lg.weighty = 0;

        lg.gridx = 0;
        lg.gridy = 0;
        leftCol.add(lblFrom, lg);
        lg.gridx = 1;
        leftCol.add(tfFrom, lg);
        lg.gridx = 0;
        lg.gridy = 1;
        leftCol.add(lblTo, lg);
        lg.gridx = 1;
        leftCol.add(tfTo, lg);
        lg.gridx = 0;
        lg.gridy = 2;
        lg.gridwidth = 2;
        leftCol.add(btnCalc, lg);

        // top spacer (for space-around distribution)
        lg.gridx = 0;
        lg.gridy = 3;
        lg.gridwidth = 2;
        lg.weighty = 0.6; // proportional spacing above summary
        lg.fill = GridBagConstraints.VERTICAL;
        leftCol.add(new JLabel(), lg);

        // Summary cards
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBackground(BG_DARK);
        GridBagConstraints sg = new GridBagConstraints();
        sg.insets = new Insets(6, 6, 6, 6);
        sg.fill = GridBagConstraints.BOTH;

        JPanel cardSales = new JPanel(new BorderLayout());
        cardSales.setBackground(BG_CARD);
        cardSales.setBorder(BorderFactory.createLineBorder(ACCENT2, 1));
        JLabel cardSalesTitle = new JLabel("Total Sales", SwingConstants.CENTER);
        cardSalesTitle.setForeground(TEXT_WHITE);
        cardSalesTitle.setFont(FONT_LABEL);
        JLabel cardSalesAmt = new JLabel("0.00", SwingConstants.CENTER);
        cardSalesAmt.setForeground(ACCENT);
        cardSalesAmt.setFont(FONT_TITLE);
        cardSales.add(cardSalesTitle, BorderLayout.NORTH);
        cardSales.add(cardSalesAmt, BorderLayout.CENTER);

        JPanel cardPurch = new JPanel(new BorderLayout());
        cardPurch.setBackground(BG_CARD);
        cardPurch.setBorder(BorderFactory.createLineBorder(ACCENT2, 1));
        JLabel cardPurchTitle = new JLabel("Total Purchases", SwingConstants.CENTER);
        cardPurchTitle.setForeground(TEXT_WHITE);
        cardPurchTitle.setFont(FONT_LABEL);
        JLabel cardPurchAmt = new JLabel("0.00", SwingConstants.CENTER);
        cardPurchAmt.setForeground(ACCENT);
        cardPurchAmt.setFont(FONT_TITLE);
        cardPurch.add(cardPurchTitle, BorderLayout.NORTH);
        cardPurch.add(cardPurchAmt, BorderLayout.CENTER);

        JPanel cardProfit = new JPanel(new BorderLayout());
        cardProfit.setBackground(BG_CARD);
        cardProfit.setBorder(BorderFactory.createLineBorder(ACCENT2, 1));
        JLabel cardProfitTitle = new JLabel("Profit / Loss", SwingConstants.CENTER);
        cardProfitTitle.setForeground(TEXT_WHITE);
        cardProfitTitle.setFont(FONT_LABEL);
        JLabel cardProfitAmt = new JLabel("0.00", SwingConstants.CENTER);
        cardProfitAmt.setForeground(ACCENT);
        cardProfitAmt.setFont(FONT_TITLE);
        cardProfit.add(cardProfitTitle, BorderLayout.NORTH);
        cardProfit.add(cardProfitAmt, BorderLayout.CENTER);

        sg.gridx = 0;
        sg.gridy = 0;
        sg.weightx = 1;
        sg.weighty = 0.6;
        summaryPanel.add(cardSales, sg);
        sg.gridx = 0;
        sg.gridy = 1;
        summaryPanel.add(cardPurch, sg);
        sg.gridx = 0;
        sg.gridy = 2;
        summaryPanel.add(cardProfit, sg);

        lg.gridx = 0;
        lg.gridy = 4;
        lg.gridwidth = 2;
        lg.weighty = 0;
        lg.fill = GridBagConstraints.HORIZONTAL;
        lg.anchor = GridBagConstraints.CENTER;
        leftCol.add(summaryPanel, lg);

        // bottom spacer (for space-around distribution)
        lg.gridx = 0;
        lg.gridy = 5;
        lg.gridwidth = 2;
        lg.weighty = 0.6; // proportional spacing below summary
        lg.fill = GridBagConstraints.VERTICAL;
        leftCol.add(new JLabel(), lg);

        // Right column: mini chart and recent transactions
        JPanel rightCol = new JPanel(new GridBagLayout());
        rightCol.setBackground(BG_DARK);
        GridBagConstraints rg = new GridBagConstraints();
        rg.insets = new Insets(6, 6, 6, 6);
        rg.fill = GridBagConstraints.BOTH;

        JPanel chartWrapper = new JPanel(new GridBagLayout());
        chartWrapper.setBackground(BG_DARK);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ACCENT2), "Overview");
        border.setTitleColor(ACCENT);
        chartWrapper.setBorder(border);

        GridBagConstraints cg = new GridBagConstraints();
        cg.insets = new Insets(6, 6, 6, 6);
        cg.gridx = 0;
        cg.gridy = 0;
        cg.anchor = GridBagConstraints.WEST;

        final JPanel barSales = new JPanel();
        barSales.setBackground(ACCENT);
        barSales.setPreferredSize(new Dimension(0, 18));

        final JPanel barPurch = new JPanel();
        barPurch.setBackground(ACCENT2);
        barPurch.setPreferredSize(new Dimension(0, 18));

        final JPanel barProfit = new JPanel();
        barProfit.setBackground(new Color(52, 211, 153));
        barProfit.setPreferredSize(new Dimension(0, 18));

        JLabel lSales = new JLabel("Sales");
        lSales.setForeground(TEXT_WHITE);
        lSales.setFont(FONT_FIELD);
        JLabel lPurch = new JLabel("Purchases");
        lPurch.setForeground(TEXT_WHITE);
        lPurch.setFont(FONT_FIELD);
        JLabel lProfit = new JLabel("Profit");
        lProfit.setForeground(TEXT_WHITE);
        lProfit.setFont(FONT_FIELD);

        cg.gridx = 0;
        cg.gridy = 0;
        chartWrapper.add(lSales, cg);
        cg.gridx = 1;
        chartWrapper.add(barSales, cg);
        cg.gridx = 0;
        cg.gridy = 1;
        chartWrapper.add(lPurch, cg);
        cg.gridx = 1;
        chartWrapper.add(barPurch, cg);
        cg.gridx = 0;
        cg.gridy = 2;
        chartWrapper.add(lProfit, cg);
        cg.gridx = 1;
        chartWrapper.add(barProfit, cg);

        rg.gridx = 0;
        rg.gridy = 0;
        rg.weightx = 1;
        rg.weighty = 0.5;
        rightCol.add(chartWrapper, rg);

        // Recent transactions table
        final DefaultTableModel recentModel = createNonEditableTableModel(new String[] { "Date", "Type", "Amount" });
        JPanel recentHolder = tableInScrollPane(recentModel);
        TitledBorder border2 =  BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ACCENT2), "Recent Transactions");
        border2.setTitleColor(ACCENT);
        recentHolder.setBorder(border2);

        rg.gridy = 1;
        rg.weighty = 0.5;
        rightCol.add(recentHolder, rg);

        // Place left and right columns into main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.45;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        p.add(leftCol, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.55;
        p.add(rightCol, gbc);

        // Calculation action
        btnCalc.addActionListener(a -> {
            String from = tfFrom.getText().trim();
            String to = tfTo.getText().trim();
            if (!DATE_PATTERN.matcher(from).matches() || !DATE_PATTERN.matcher(to).matches()) {
                JOptionPane.showMessageDialog(this, "Dates must be in dd/mm/yyyy format.", "Validation",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            double totalSales = computeTotalSales(from, to);
            double totalPurchases = computeTotalPurchases(from, to);
            double profit = totalSales - totalPurchases;

            // Update cards
            cardSalesAmt.setText(String.format("%.2f", totalSales));
            cardPurchAmt.setText(String.format("%.2f", totalPurchases));
            cardProfitAmt.setText(String.format("%.2f", profit));

            // Update bars (proportional widths)
            double max = Math.max(1.0, Math.max(totalSales, totalPurchases));
            int full = 260; // max pixel width for bars
            int wSales = (int) Math.round((totalSales / max) * full);
            int wPurch = (int) Math.round((totalPurchases / max) * full);
            int wProfit = (int) Math
                    .round((Math.abs(profit) / Math.max(1.0, Math.max(Math.abs(totalSales), Math.abs(totalPurchases))))
                            * full);
            barSales.setPreferredSize(new Dimension(Math.max(4, wSales), 18));
            barPurch.setPreferredSize(new Dimension(Math.max(4, wPurch), 18));
            barProfit.setPreferredSize(new Dimension(Math.max(4, wProfit), 18));
            barSales.revalidate();
            barPurch.revalidate();
            barProfit.revalidate();

            // Load recent transactions (defensive, avoids NPE when DB empty)
            recentModel.setRowCount(0);
            String qRecent = "SELECT DATE, 'Sale' AS TYPE, (QUANTITY * SELLING_UNIT_PRICE) AS AMOUNT FROM SALES WHERE DATE >= ? AND DATE <= ? "
                    + "UNION ALL SELECT DATE, 'Purchase' AS TYPE, (QUANTITY * UNIT_PRICE) AS AMOUNT FROM PURCHASE WHERE DATE >= ? AND DATE <= ? ORDER BY DATE DESC LIMIT 10;";
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) {
                    System.err.println("DB connection is null when loading recent transactions");
                } else {
                    try (PreparedStatement ps = conn.prepareStatement(qRecent)) {
                        ps.setString(1, from);
                        ps.setString(2, to);
                        ps.setString(3, from);
                        ps.setString(4, to);
                        try (ResultSet rs = ps.executeQuery()) {
                            while (rs.next()) {
                                String date = rs.getString("DATE");
                                String type = rs.getString("TYPE");
                                double amount = rs.getDouble("AMOUNT");
                                if (rs.wasNull()) amount = 0.0;
                                recentModel.addRow(new Object[] { date == null ? "" : date, type == null ? "" : type,
                                        String.format("%.2f", amount) });
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        return p;
    }

    private void loadLowStockData(int threshold) {
        lowStockModel.setRowCount(0);
        try {
            String q = "SELECT PRODUCT.PRODUCT_ID, PRODUCT.PRODUCT_NAME, PRODUCT_QUANTITY.QUANTITY FROM PRODUCT INNER JOIN PRODUCT_QUANTITY WHERE PRODUCT.PRODUCT_ID = PRODUCT_QUANTITY.PRODUCT_ID AND PRODUCT_QUANTITY.QUANTITY <= ? ORDER BY PRODUCT_QUANTITY.QUANTITY ASC;";
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) {
                    System.err.println("DB connection is null in loadLowStockData");
                    return;
                }
                try (PreparedStatement ps = conn.prepareStatement(q)) {
                    ps.setInt(1, threshold);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String pid = rs.getString("PRODUCT_ID");
                            String pname = rs.getString("PRODUCT_NAME");
                            int qty = rs.getInt("QUANTITY");
                            if (rs.wasNull()) qty = 0;
                            lowStockModel.addRow(new Object[] { pid == null ? "" : pid, pname == null ? "" : pname, qty });
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double computeTotalSales(String from, String to) {
        double total = 0.0;
        try {
            String q = "SELECT COALESCE(SUM(QUANTITY * SELLING_UNIT_PRICE), 0) AS TOTAL FROM SALES WHERE DATE >= ? AND DATE <= ?;";
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) {
                    System.err.println("DB connection is null in computeTotalSales");
                    return 0.0;
                }
                try (PreparedStatement ps = conn.prepareStatement(q)) {
                    ps.setString(1, from);
                    ps.setString(2, to);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            total = rs.getDouble("TOTAL");
                            if (rs.wasNull()) total = 0.0;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    private double computeTotalPurchases(String from, String to) {
        double total = 0.0;
        try {
            String q = "SELECT COALESCE(SUM(QUANTITY * UNIT_PRICE), 0) AS TOTAL FROM PURCHASE WHERE DATE >= ? AND DATE <= ?;";
            try (Connection conn = DBConnection.getConnection()) {
                if (conn == null) {
                    System.err.println("DB connection is null in computeTotalPurchases");
                    return 0.0;
                }
                try (PreparedStatement ps = conn.prepareStatement(q)) {
                    ps.setString(1, from);
                    ps.setString(2, to);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            total = rs.getDouble("TOTAL");
                            if (rs.wasNull()) total = 0.0;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
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

}
