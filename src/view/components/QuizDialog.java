package view.components;

import model.data.QuizDatabase;
import model.data.QuizQuestion;
import model.data.Skill;
import model.entities.GameCharacter;
import presenter.GamePresenter;
import util.AssetLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class QuizDialog extends JDialog {

    private final GamePresenter presenter;
    private final GameCharacter character;
    private final Skill skill;
    private final QuizQuestion question;

    // GOLD Theme
    private final Color GOLD = new Color(255, 215, 0);
    private final Color GOLD_DARK = new Color(180, 145, 20);
    private final Font TITLE_FONT = new Font("Serif", Font.BOLD, 22);
    private final Font BTN_FONT = new Font("Dialog", Font.BOLD, 16);

    public QuizDialog(Frame owner, GamePresenter presenter, GameCharacter character, Skill skill) {
        super(owner, "Rapal Mantra", true);

        this.presenter = presenter;
        this.character = character;
        this.skill = skill;
        this.question = QuizDatabase.getInstance().getRandom(character.getType());

        setSize(650, 450);
        setLocationRelativeTo(owner);

        // Custom background panel
        setContentPane(new BackgroundPanel());
        setLayout(new GridBagLayout());

        if (question == null) {
            presenter.endLocalPause();
            dispose();
            return;
        }

        initUI();

        // Prevent closing cheat
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                character.addSukma(skill.getSukmaCost());
                presenter.endLocalPause();
            }
        });

        setVisible(true);
    }

    /** UI SETUP ********************************************************************/

    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(14, 10, 14, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // ==== QUESTION LABEL ====
        JLabel qLabel = new JLabel(
                "<html><div style='text-align:center; color:#FFD700;'>" +
                question.getQuestion() +
                "</div></html>",
                SwingConstants.CENTER
        );
        qLabel.setFont(TITLE_FONT);

        JPanel glassPanel = themedGlassPanel();
        glassPanel.setLayout(new GridBagLayout());

        GridBagConstraints pgbc = new GridBagConstraints();
        pgbc.insets = new Insets(12, 10, 12, 10);
        pgbc.fill = GridBagConstraints.HORIZONTAL;
        pgbc.gridx = 0;

        pgbc.gridy = 0;
        glassPanel.add(qLabel, pgbc);

        pgbc.gridy = 1;
        glassPanel.add(createButton("A. " + question.getOptionA(), 'A'), pgbc);

        pgbc.gridy = 2;
        glassPanel.add(createButton("B. " + question.getOptionB(), 'B'), pgbc);

        pgbc.gridy = 3;
        glassPanel.add(createButton("C. " + question.getOptionC(), 'C'), pgbc);

        gbc.gridy = 0;
        add(glassPanel, gbc);
    }

    /** GOLD GLASS PANEL ************************************************************/
    private JPanel themedGlassPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;

                // Semi-transparent black background
                g2.setColor(new Color(0, 0, 0, 170));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                // Gold border
                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, getWidth(), getHeight(), 18, 18);

                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Image bg = AssetLoader.getInstance().getImage("src/assets/images/bg_quiz.jpg");

            if (bg != null) {
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
            } else {
                // fallback maroon dark
                g.setColor(new Color(30, 0, 0));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    /** BUTTON */
    private JButton createButton(String text, char ans) {
        JButton btn = new JButton(text);

        btn.setFont(BTN_FONT);
        btn.setFocusPainted(false);
        btn.setBackground(GOLD);
        btn.setForeground(Color.BLACK);
        btn.setBorder(BorderFactory.createLineBorder(GOLD_DARK, 2));
        btn.setPreferredSize(new Dimension(360, 40));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(GOLD_DARK);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(GOLD);
            }
        });

        btn.addActionListener(e -> submitAnswer(ans));
        return btn;
    }

    /** SUBMIT ANSWER */
    private void submitAnswer(char ans) {
        boolean correct = (ans == question.getCorrectAnswer());

        if (correct) {
            JOptionPane.showMessageDialog(this, "Mantra Berhasil!", "Berhasil", JOptionPane.INFORMATION_MESSAGE);
            presenter.applySkill(character, skill, true);
        } else {
            JOptionPane.showMessageDialog(this, "Gagal! Sukma kembali.", "Gagal", JOptionPane.WARNING_MESSAGE);
            character.addSukma(skill.getSukmaCost());
        }

        presenter.endLocalPause();
        dispose();
    }
}
