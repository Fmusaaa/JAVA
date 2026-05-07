public class FinalExamDemo {
    public static void main(String[] args) {
        FinalExam fe = new FinalExam(7,3);
        
        System.out.println("point: " + fe.getPointsPerQuestion());
        System.out.println("Missed: " + fe.getNumberMissed());
        fe.setScore(fe.getPointsPerQuestion());
        System.out.println("Grade: " + fe.getGrade());
    }
}