package model.data;

public class QuizQuestion {
    private final int id;
    private final String question;
    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final char correctAnswer;

    public QuizQuestion(int id, String question,
                        String optionA, String optionB, String optionC,
                        char correctAnswer) {
        this.id = id;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.correctAnswer = correctAnswer;
    }

    public int getId() { return id; }
    public String getQuestion() { return question; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public char getCorrectAnswer() { return correctAnswer; }
}
