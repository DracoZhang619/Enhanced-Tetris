import javax.swing.*;
import java.awt.*;

public class BottomPanel extends JPanel {

    public BottomPanel(GameController gameController, String authorName) {
        setLayout(new BorderLayout());

        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(150, 50));
        backButton.addActionListener(e -> gameController.showMainMenu());
        add(backButton, BorderLayout.NORTH);

        JLabel authorLabel = new JLabel("Author: " + authorName, JLabel.CENTER);
        add(authorLabel, BorderLayout.SOUTH);

        setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
    }
}