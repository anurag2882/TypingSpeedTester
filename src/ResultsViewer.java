import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

public class ResultsViewer extends JDialog {

    // ─── Colors (matching main UI) ────────────────────────────────────────────
    private static final Color BG         = new Color(15, 17, 26);
    private static final Color PANEL_BG   = new Color(22, 25, 40);
    private static final Color ACCENT     = new Color(99, 179, 237);
    private static final Color TEXT_MAIN  = new Color(226, 232, 240);
    private static final Color TEXT_DIM   = new Color(113, 128, 150);
    private static final Color ROW_ALT    = new Color(30, 34, 52);

    /**
     * Constructor — builds and shows the results dialog.
     * @param parent The parent window (for centering)
     */
    public ResultsViewer(JFrame parent) {
        super(parent, "📊 Past Results", true); // 'true' = modal dialog
        setSize(680, 420);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        buildUI();
    }

    /**
     * Build all UI components for this dialog.
     */
    private void buildUI() {
        // ── Title Bar ──────────────────────────────────────────────────────────
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(PANEL_BG);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel titleLabel = new JLabel("Your Typing History");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ACCENT);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Stats summary in title bar
        int total = FileHandler.getTotalTests();
        double bestWPM = FileHandler.getBestWPM();
        JLabel statsLabel = new JLabel(String.format("Total Tests: %d   |   Best WPM: %.1f", total, bestWPM));
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statsLabel.setForeground(TEXT_DIM);
        titlePanel.add(statsLabel, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);

        // ── Table ─────────────────────────────────────────────────────────────
        String[] columns = {"Date & Time", "WPM", "Accuracy (%)", "Time (sec)", "Difficulty"};
        ArrayList<String[]> data = FileHandler.loadResults();

        // Convert ArrayList to 2D array for JTable
        Object[][] tableData = new Object[data.size()][5];
        for (int i = 0; i < data.size(); i++) {
            String[] row = data.get(i);
            for (int j = 0; j < Math.min(row.length, 5); j++) {
                tableData[i][j] = row[j];
            }
        }

        // Show message if no data
        if (data.isEmpty()) {
            JLabel noData = new JLabel("No results yet. Complete a test first!", SwingConstants.CENTER);
            noData.setFont(new Font("Segoe UI", Font.ITALIC, 15));
            noData.setForeground(TEXT_DIM);
            noData.setBackground(BG);
            noData.setOpaque(true);
            noData.setBorder(BorderFactory.createEmptyBorder(60, 0, 60, 0));
            add(noData, BorderLayout.CENTER);
        } else {
            DefaultTableModel model = new DefaultTableModel(tableData, columns) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return false; // Make table read-only
                }
            };

            JTable table = new JTable(model);
            styleTable(table);

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setBackground(BG);
            scrollPane.getViewport().setBackground(BG);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            add(scrollPane, BorderLayout.CENTER);
        }

        // ── Close Button ──────────────────────────────────────────────────────
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(PANEL_BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setBackground(new Color(45, 55, 72));
        closeBtn.setForeground(TEXT_MAIN);
        closeBtn.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose()); // Close dialog
        bottomPanel.add(closeBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Apply custom styling to the JTable.
     */
    private void styleTable(JTable table) {
        // Table itself
        table.setBackground(BG);
        table.setForeground(TEXT_MAIN);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(49, 61, 99));
        table.setSelectionForeground(Color.WHITE);

        // Alternating row colors using a custom renderer
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? BG : ROW_ALT);
                    setForeground(TEXT_MAIN);
                }
                return this;
            }
        });

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(PANEL_BG);
        header.setForeground(ACCENT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.CENTER);
    }
}
