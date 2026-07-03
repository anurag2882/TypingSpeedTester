public class Main {
    public static void main(String[] args) {
        // Use SwingUtilities to run the UI on the Event Dispatch Thread (best practice)
        javax.swing.SwingUtilities.invokeLater(() -> {
            TypingSpeedUI app = new TypingSpeedUI();
            app.setVisible(true);
        });
    }
}
