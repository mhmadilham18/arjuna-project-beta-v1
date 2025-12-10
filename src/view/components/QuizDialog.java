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

    public QuizDialog(Frame owner, GamePresenter presenter, GameCharacter character, Skill skill) {
        super(owner, "Rapal Mantra", true);
        this.presenter = presenter;
        this.character = character;
        this.skill = skill;
        this.question = QuizDatabase.getInstance().getRandom(character.getType());
        setSize(600, 400);
        setLocationRelativeTo(owner);
        setContentPane(new BackgroundPanel());
        setLayout(new GridBagLayout());

        if (question == null) { presenter.endLocalPause(); dispose(); return; }

        initUI();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                character.addSukma(skill.getSukmaCost());
                presenter.endLocalPause(); // FIX: Resume game
            }
        });
        setVisible(true);
    }

    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel qLabel = new JLabel("<html><div style='text-align: center; color: white;'>" + question.getQuestion() + "</div></html>", SwingConstants.CENTER);
        qLabel.setFont(new Font("Serif", Font.BOLD, 20));
        gbc.gridy = 0; add(qLabel, gbc);

        gbc.gridy = 1; add(createButton("A. " + question.getOptionA(), 'A'), gbc);
        gbc.gridy = 2; add(createButton("B. " + question.getOptionB(), 'B'), gbc);
        gbc.gridy = 3; add(createButton("C. " + question.getOptionC(), 'C'), gbc);
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Image bg = AssetLoader.getInstance().getQuizBackground();
            if (bg != null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
            else { g.setColor(new Color(40, 0, 0)); g.fillRect(0, 0, getWidth(), getHeight()); }
        }
    }

    private JButton createButton(String text, char ans) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Dialog", Font.BOLD, 16));
        btn.setBackground(new Color(255, 255, 200));
        btn.addActionListener(e -> submitAnswer(ans));
        return btn;
    }

    private void submitAnswer(char ans) {
        boolean correct = (ans == question.getCorrectAnswer());
        if (correct) {
            JOptionPane.showMessageDialog(this, "Mantra Berhasil!");
            presenter.applySkill(character, skill, true);
        } else {
            JOptionPane.showMessageDialog(this, "Gagal! Sukma kembali.");
            character.addSukma(skill.getSukmaCost());
        }
        presenter.endLocalPause(); // FIX: Resume game
        dispose();
    }
}