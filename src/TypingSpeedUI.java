import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class TypingSpeedUI extends JFrame {

    // ─── Color Palette (dark blue-grey theme) ─────────────────────────────────
    private static final Color BG_DARK    = new Color(13, 15, 23);   // Main background
    private static final Color BG_PANEL   = new Color(20, 23, 38);   // Card background
    private static final Color BG_INPUT   = new Color(26, 31, 50);   // Input field background
    private static final Color ACCENT     = new Color(99, 179, 237); // Blue accent
    private static final Color ACCENT2    = new Color(154, 230, 180);// Green accent
    private static final Color ACCENT_RED = new Color(252, 129, 129);// Red / error
    private static final Color TEXT_MAIN  = new Color(226, 232, 240);// Main text
    private static final Color TEXT_DIM   = new Color(100, 116, 139);// Dimmed text
    private static final Color BTN_START  = new Color(56, 161, 105); // Green button
    private static final Color BTN_FINISH = new Color(49, 130, 206); // Blue button
    private static final Color BTN_RETRY  = new Color(113, 128, 150);// Grey button

    // ─── Fonts ────────────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_LARGE  = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_MONO   = new Font("Consolas", Font.PLAIN, 16);
    private static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_STAT   = new Font("Segoe UI", Font.BOLD, 28);

    // ─── Logic & State ────────────────────────────────────────────────────────
    private TypingLogic logic;        // Handles calculations
    private boolean testStarted;      // Has the test begun?
    private boolean testFinished;     // Has the test ended?
    private javax.swing.Timer liveTimer; // Swing timer for live clock

    // ─── UI Components ────────────────────────────────────────────────────────
    private JLabel      sentenceLabel;    // Shows the sentence to type
    private JTextArea   typingArea;       // User types here
    private JLabel      timerLabel;       // Live countdown/elapsed
    private JLabel      statusLabel;      // Status message
    private JButton     startBtn;
    private JButton     finishBtn;
    private JButton     retryBtn;
    private JButton     viewResultsBtn;
    private JComboBox<String> difficultyBox; // Easy / Medium / Hard

    // Result labels
    private JLabel wpmValueLabel;
    private JLabel accuracyValueLabel;
    private JLabel timeValueLabel;
    private JPanel  resultPanel;

    // ─── Constructor ──────────────────────────────────────────────────────────
    public TypingSpeedUI() {
        logic = new TypingLogic();
        testStarted = false;
        testFinished = false;

        setTitle("⌨️  Typing Speed Tester");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 680);
        setMinimumSize(new Dimension(750, 600));
        setLocationRelativeTo(null); // Center on screen
        setResizable(true);

        buildUI();
        initTimer();
    }

    // ─── Build the Entire UI ──────────────────────────────────────────────────
    private void buildUI() {
        // Root panel with dark background
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        setContentPane(root);

        root.add(buildHeader(),  BorderLayout.NORTH);
        root.add(buildCenter(),  BorderLayout.CENTER);
        root.add(buildFooter(),  BorderLayout.SOUTH);
    }

    // ── Header: Title + Difficulty Selector ───────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(30, 40, 65)),
            new EmptyBorder(18, 28, 18, 28)
        ));

        // Left: App title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);

        JLabel iconLabel = new JLabel("⌨ ");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        iconLabel.setForeground(ACCENT);

        JLabel titleLabel = new JLabel("Typing Speed Tester");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_MAIN);

        leftPanel.add(iconLabel);
        leftPanel.add(titleLabel);
        header.add(leftPanel, BorderLayout.WEST);

        // Right: Difficulty + View Results button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);

        JLabel diffLabel = new JLabel("Difficulty:");
        diffLabel.setFont(FONT_NORMAL);
        diffLabel.setForeground(TEXT_DIM);

        difficultyBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        difficultyBox.setSelectedItem("Medium");
        difficultyBox.setFont(FONT_NORMAL);
        difficultyBox.setBackground(BG_INPUT);
        difficultyBox.setForeground(TEXT_MAIN);
        difficultyBox.setFocusable(false);
        difficultyBox.setPreferredSize(new Dimension(110, 32));

        viewResultsBtn = createButton("📊 History", BTN_RETRY);
        viewResultsBtn.addActionListener(e -> showResults());

        rightPanel.add(diffLabel);
        rightPanel.add(difficultyBox);
        rightPanel.add(viewResultsBtn);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    // ── Center: Sentence Display + Typing Area + Buttons ──────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(BG_DARK);
        center.setBorder(new EmptyBorder(24, 28, 24, 28));

        // ── Sentence Card ──────────────────────────────────────────────────────
        JPanel sentenceCard = createCard();
        sentenceCard.setLayout(new BorderLayout(0, 8));

        JLabel promptLabel = new JLabel("TYPE THIS:");
        promptLabel.setFont(FONT_SMALL);
        promptLabel.setForeground(TEXT_DIM);
        promptLabel.setBorder(new EmptyBorder(0, 0, 4, 0));

        sentenceLabel = new JLabel("<html><center>Click <b>Start</b> to load a sentence.</center></html>");
        sentenceLabel.setFont(FONT_MONO);
        sentenceLabel.setForeground(TEXT_DIM);
        sentenceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sentenceLabel.setBorder(new EmptyBorder(8, 8, 8, 8));

        sentenceCard.add(promptLabel, BorderLayout.NORTH);
        sentenceCard.add(sentenceLabel, BorderLayout.CENTER);
        sentenceCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        center.add(sentenceCard);
        center.add(Box.createVerticalStrut(16));

        // ── Timer display ──────────────────────────────────────────────────────
        JPanel timerRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        timerRow.setOpaque(false);

        JLabel clockIcon = new JLabel("⏱");
        clockIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        clockIcon.setForeground(ACCENT);

        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("Consolas", Font.BOLD, 20));
        timerLabel.setForeground(ACCENT);

        statusLabel = new JLabel("Press Start to begin");
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(TEXT_DIM);

        timerRow.add(clockIcon);
        timerRow.add(timerLabel);
        timerRow.add(Box.createHorizontalStrut(20));
        timerRow.add(statusLabel);
        timerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        center.add(timerRow);
        center.add(Box.createVerticalStrut(10));

        // ── Typing Input Area ──────────────────────────────────────────────────
        JPanel inputCard = createCard();
        inputCard.setLayout(new BorderLayout(0, 8));

        JLabel typeLabel = new JLabel("YOUR INPUT:");
        typeLabel.setFont(FONT_SMALL);
        typeLabel.setForeground(TEXT_DIM);

        typingArea = new JTextArea(4, 40);
        typingArea.setFont(FONT_MONO);
        typingArea.setBackground(BG_INPUT);
        typingArea.setForeground(TEXT_MAIN);
        typingArea.setCaretColor(ACCENT);
        typingArea.setLineWrap(true);
        typingArea.setWrapStyleWord(true);
        typingArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        typingArea.setEnabled(false); // Disabled until Start is pressed

        // Listen for any text change (DocumentListener) — starts timer on first keypress
        typingArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { onTextChanged(); }
            @Override public void removeUpdate(DocumentEvent e)  { onTextChanged(); }
            @Override public void changedUpdate(DocumentEvent e) { }
        });

        JScrollPane scrollPane = new JScrollPane(typingArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(40, 50, 80), 1));
        scrollPane.setBackground(BG_INPUT);

        inputCard.add(typeLabel, BorderLayout.NORTH);
        inputCard.add(scrollPane, BorderLayout.CENTER);
        inputCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        center.add(inputCard);
        center.add(Box.createVerticalStrut(20));

        // ── Buttons Row ────────────────────────────────────────────────────────
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttonRow.setOpaque(false);

        startBtn  = createButton("▶  Start",   BTN_START);
        finishBtn = createButton("✔  Finish",  BTN_FINISH);
        retryBtn  = createButton("↺  Restart", BTN_RETRY);

        finishBtn.setEnabled(false);
        retryBtn.setEnabled(false);

        startBtn.addActionListener(e  -> onStart());
        finishBtn.addActionListener(e -> onFinish());
        retryBtn.addActionListener(e  -> onRestart());

        buttonRow.add(startBtn);
        buttonRow.add(finishBtn);
        buttonRow.add(retryBtn);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        center.add(buttonRow);
        center.add(Box.createVerticalStrut(20));

        // ── Result Panel ───────────────────────────────────────────────────────
        resultPanel = buildResultPanel();
        resultPanel.setVisible(false);
        center.add(resultPanel);

        return center;
    }

    // ── Result Panel: WPM / Accuracy / Time ───────────────────────────────────
    private JPanel buildResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel resultTitle = new JLabel("📈  Your Results", SwingConstants.CENTER);
        resultTitle.setFont(FONT_LARGE);
        resultTitle.setForeground(ACCENT2);
        panel.add(resultTitle, BorderLayout.NORTH);

        // Three stat cards in a row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);

        wpmValueLabel      = new JLabel("—", SwingConstants.CENTER);
        accuracyValueLabel = new JLabel("—", SwingConstants.CENTER);
        timeValueLabel     = new JLabel("—", SwingConstants.CENTER);

        statsRow.add(createStatCard("WPM",       wpmValueLabel,      "⚡"));
        statsRow.add(createStatCard("ACCURACY",  accuracyValueLabel, "🎯"));
        statsRow.add(createStatCard("TIME",       timeValueLabel,     "⏱"));

        panel.add(statsRow, BorderLayout.CENTER);
        return panel;
    }

    // ── Helper: Single Stat Card ───────────────────────────────────────────────
    private JPanel createStatCard(String label, JLabel valueLabel, String icon) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 4));

        JLabel iconLbl = new JLabel(icon + "  " + label, SwingConstants.CENTER);
        iconLbl.setFont(FONT_SMALL);
        iconLbl.setForeground(TEXT_DIM);

        valueLabel.setFont(FONT_STAT);
        valueLabel.setForeground(TEXT_MAIN);

        card.add(iconLbl,    BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ── Footer ─────────────────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG_PANEL);
        footer.setBorder(new MatteBorder(1, 0, 0, 0, new Color(30, 40, 65)));

        JLabel footerLabel = new JLabel("© 2026 Typing Speed Tester. All rights reserved.");
        footerLabel.setFont(FONT_SMALL);
        footerLabel.setForeground(TEXT_DIM);
        footer.add(footerLabel);
        return footer;
    }

    // ─── Event Handlers ───────────────────────────────────────────────────────

    /**
     * Called when Start button is clicked.
     * Loads a random sentence and prepares the UI for typing.
     */
    private void onStart() {
        String difficulty = (String) difficultyBox.getSelectedItem();
        String sentence = logic.getRandomSentence(difficulty);

        // Show the sentence in monospaced font, wrapped in HTML for proper display
        sentenceLabel.setFont(FONT_MONO);
        sentenceLabel.setForeground(TEXT_MAIN);
        sentenceLabel.setText("<html><center>" + escapeHtml(sentence) + "</center></html>");

        // Reset typing area
        typingArea.setText("");
        typingArea.setEnabled(true);
        typingArea.setBackground(BG_INPUT);
        typingArea.requestFocus();

        // Reset state
        testStarted  = false; // Will be set true on first keystroke
        testFinished = false;

        // Reset result display
        resultPanel.setVisible(false);
        wpmValueLabel.setText("—");
        accuracyValueLabel.setText("—");
        timeValueLabel.setText("—");

        timerLabel.setForeground(ACCENT);
        timerLabel.setText("00:00");
        statusLabel.setText("Start typing...");
        statusLabel.setForeground(new Color(154, 230, 180));

        // Button states
        startBtn.setEnabled(false);
        finishBtn.setEnabled(true);
        retryBtn.setEnabled(false);
        difficultyBox.setEnabled(false);
    }

    /**
     * Called when any text change occurs in the typing area.
     * Starts the timer on first keystroke.
     */
    private void onTextChanged() {
        if (!testStarted && !testFinished && typingArea.isEnabled()) {
            testStarted = true;
            logic.startTimer();
            liveTimer.start();
            statusLabel.setText("Typing...");
        }
    }

    /**
     * Called when Finish button is clicked.
     * Stops the timer and shows results.
     */
    private void onFinish() {
        if (!testStarted) {
            JOptionPane.showMessageDialog(this,
                "You haven't started typing yet!\nPlease type something first.",
                "Not started", JOptionPane.WARNING_MESSAGE);
            return;
        }

        testFinished = true;
        liveTimer.stop();
        typingArea.setEnabled(false);

        // Get data
        double elapsed  = logic.getElapsedSeconds();
        String typed    = typingArea.getText();
        double wpm      = logic.calculateWPM(typed, elapsed);
        double accuracy = logic.calculateAccuracy(typed);

        // Update result labels
        wpmValueLabel.setText(String.format("%.1f", wpm));
        accuracyValueLabel.setText(String.format("%.1f%%", accuracy));
        timeValueLabel.setText(String.format("%.1fs", elapsed));

        // Color-code accuracy
        if (accuracy >= 90) {
            accuracyValueLabel.setForeground(ACCENT2);
        } else if (accuracy >= 70) {
            accuracyValueLabel.setForeground(new Color(246, 173, 85));
        } else {
            accuracyValueLabel.setForeground(ACCENT_RED);
        }

        resultPanel.setVisible(true);

        statusLabel.setText("Done! Great job!");
        statusLabel.setForeground(ACCENT2);

        // Save to file
        FileHandler.saveResult(wpm, accuracy, elapsed, logic.getDifficulty());

        // Update buttons
        startBtn.setEnabled(false);
        finishBtn.setEnabled(false);
        retryBtn.setEnabled(true);
    }

    /**
     * Called when Restart button is clicked.
     * Resets everything for a fresh attempt.
     */
    private void onRestart() {
        liveTimer.stop();
        testStarted  = false;
        testFinished = false;

        sentenceLabel.setFont(FONT_MONO);
        sentenceLabel.setForeground(TEXT_DIM);
        sentenceLabel.setText("<html><center>Click <b>Start</b> to load a sentence.</center></html>");

        typingArea.setText("");
        typingArea.setEnabled(false);
        typingArea.setBackground(BG_INPUT);

        resultPanel.setVisible(false);
        timerLabel.setText("00:00");
        timerLabel.setForeground(ACCENT);
        statusLabel.setText("Press Start to begin");
        statusLabel.setForeground(TEXT_DIM);

        startBtn.setEnabled(true);
        finishBtn.setEnabled(false);
        retryBtn.setEnabled(false);
        difficultyBox.setEnabled(true);
    }

    /**
     * Show the Results History dialog.
     */
    private void showResults() {
        ResultsViewer viewer = new ResultsViewer(this);
        viewer.setVisible(true);
    }

    // ─── Live Timer ───────────────────────────────────────────────────────────

    /**
     * Initialize a Swing Timer that fires every second to update the clock.
     */
    private void initTimer() {
        liveTimer = new javax.swing.Timer(1000, e -> updateClock());
    }

    /**
     * Update the timer label with elapsed time every second.
     */
    private void updateClock() {
        if (testStarted && !testFinished) {
            double secs = logic.getElapsedSeconds();
            int minutes = (int) secs / 60;
            int seconds = (int) secs % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }

    // ─── Helper Methods ───────────────────────────────────────────────────────

    /**
     * Create a styled card panel with rounded feel.
     */
    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(BG_PANEL);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(35, 45, 70), 1),
            new EmptyBorder(12, 16, 12, 16)
        ));
        return card;
    }

    /**
     * Create a styled button with given background color.
     */
    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setBorder(new EmptyBorder(10, 28, 10, 28));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);

        // Hover effect: slightly brighten
        Color hoverColor = bg.brighter();
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(hoverColor);
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }

    /**
     * Escape special HTML characters in a string (so it's safe to put in HTML labels).
     */
    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}
