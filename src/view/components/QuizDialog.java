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
        super(owner, "Matra Skill", true);

        this.presenter = presenter;
        this.character = character;
        this.skill = skill;
        this.question = QuizDatabase.getInstance().getRandom(character.getType());

        if (question == null) {
            dispose();
            return;
        }

        setSize(500, 300);
        setLocationRelativeTo(owner);
        setLayout(new GridLayout(5, 1));

        JLabel qLabel = new JLabel(question.getQuestion(), SwingConstants.CENTER);
        qLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        add(qLabel);

        JButton btnA = new JButton("A. " + question.getOptionA());
        JButton btnB = new JButton("B. " + question.getOptionB());
        JButton btnC = new JButton("C. " + question.getOptionC());

        btnA.addActionListener(e -> submitAnswer('A'));
        btnB.addActionListener(e -> submitAnswer('B'));
        btnC.addActionListener(e -> submitAnswer('C'));

        add(btnA);
        add(btnB);
        add(btnC);

        setVisible(true);
    }

    private void submitAnswer(char ans) {
        boolean correct = (ans == question.getCorrectAnswer());

        if (correct) {
            JOptionPane.showMessageDialog(this, "Matra berhasil!");
            presenter.applySkill(character, skill, true);
        } else {
            JOptionPane.showMessageDialog(this, "Matra gagal...");
        }

        dispose();
    }
}
