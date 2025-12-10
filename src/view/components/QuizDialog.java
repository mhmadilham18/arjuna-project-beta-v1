package view.components;

import model.data.QuizDatabase;
import model.data.QuizQuestion;
import model.data.Skill;
import model.entities.GameCharacter;
import presenter.GamePresenter;

import javax.swing.*;
import java.awt.*;

public class QuizDialog extends JDialog {

    private final GamePresenter presenter;
    private final GameCharacter character;
    private final Skill skill;
    private final QuizQuestion question;

    public QuizDialog(Frame owner, GamePresenter presenter, GameCharacter character, Skill skill) {
        super(owner, "Rapal Mantra (Quiz)", true); // Modal true = pause game click

        this.presenter = presenter;
        this.character = character;
        this.skill = skill;
        this.question = QuizDatabase.getInstance().getRandom(character.getType());

        setSize(600, 350);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(5, 1, 10, 10));

        if (question == null) { dispose(); return; }

        JLabel qLabel = new JLabel("<html><center>" + question.getQuestion() + "</center></html>", SwingConstants.CENTER);
        qLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        add(qLabel);

        add(createButton("A. " + question.getOptionA(), 'A'));
        add(createButton("B. " + question.getOptionB(), 'B'));
        add(createButton("C. " + question.getOptionC(), 'C'));

        setVisible(true);
    }

    private JButton createButton(String text, char ans) {
        JButton btn = new JButton(text);
        btn.addActionListener(e -> submitAnswer(ans));
        return btn;
    }

    private void submitAnswer(char ans) {
        boolean correct = (ans == question.getCorrectAnswer());

        if (correct) {
            JOptionPane.showMessageDialog(this, "Mantra Berhasil! Skill Aktif.");
            presenter.applySkill(character, skill, true);
        } else {
            JOptionPane.showMessageDialog(this, "Mantra Gagal... Sukma dikembalikan.");
            character.addSukma(skill.getSukmaCost());
        }
        dispose();
    }
}