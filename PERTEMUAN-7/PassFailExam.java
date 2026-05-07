public class PassFailExam extends PassFailActivity {
    private int numberOfQuestions;
    private int numberMissed;
    private double pointsPerQuestion;
    
    public PassFailExam(int questions, int missed, double minPassing) {
        super(minPassing);
        numberOfQuestions = questions;
        numberMissed = missed;
        pointsPerQuestion = 100.0 / questions;
        double score = 100.0 - (numberMissed * pointsPerQuestion);
        setScore(score);
    }
    
    public double getPointsEach() {
        return pointsPerQuestion;
    }
    
    public int getNumMissed() {
        return numberMissed;
    }
}