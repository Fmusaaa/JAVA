public class PassFailActivity extends GradeActivity {
    private double minimumPassingScore;
    
    public PassFailActivity(double min) {
        super("PassFailActivity");
        minimumPassingScore = min;
    }
    
    public char getGrade() {
        if (getScore() >= minimumPassingScore)
            return 'P';
        else
            return 'F';
    }
}