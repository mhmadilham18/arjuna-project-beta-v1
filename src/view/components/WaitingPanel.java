package view.components;

import javax.swing.*;
import java.awt.*;

public class WaitingPanel extends JPanel {

    public WaitingPanel(String message) {
        setLayout(new BorderLayout());
        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setFont(new Font("Dialog", Font.BOLD, 22));
        add(label, BorderLayout.CENTER);
    }
}