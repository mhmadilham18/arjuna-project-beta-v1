package view.components;

import javax.swing.*;
import java.awt.*;

public class ResultDialog extends JDialog {

    public ResultDialog(Frame owner, String winner, boolean isWinner) {
        super(owner, "Hasil Pertandingan", true);

        JPanel p = new JPanel(new GridLayout(3, 1));

        JLabel l1 = new JLabel("Pemenang: " + winner, SwingConstants.CENTER);
        l1.setFont(new Font("Dialog", Font.BOLD, 22));

        JButton ok = new JButton("Kembali");
        ok.addActionListener(e -> System.exit(0));

        p.add(l1);
        p.add(ok);

        add(p);

        setSize(400, 200);
        setLocationRelativeTo(owner);
        setVisible(true);
    }
}
