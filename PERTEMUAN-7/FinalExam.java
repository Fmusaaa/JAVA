public class FinalExam extends GradeActivity {
    private int numberOfQuestions;
    private int numberOfMiss;
    private double pointsPerQuestion;
    
    public FinalExam(int questions, int missed) {
        super("FinalExam");
        numberOfQuestions = questions;
        numberOfMiss = missed;
        pointsPerQuestion = 100.0 / numberOfQuestions;
        double score = 100.0 - (numberOfMiss * pointsPerQuestion);
        setScore(score);
    }
    
    public double getPointsPerQuestion() {
        return pointsPerQuestion;
    }
    
    public int getNumberMissed() {
        return numberOfMiss;
    }
}