public class GradeActivityDemo {
    public static void main (String[] args){
        FinalExam fe = new FinalExam(40,5);
        System.out.println("-Final Exam-");
        System.out.println("Score = " + fe.getScore());
        System.out.println("Grade = " + fe.getGrade());
        System.out.println("Missed = " + fe.getNumberMissed());
        
        PassFailExam pfe = new PassFailExam (40,10,60);
        System.out.println("\n-Pass Fail Exam-");
        System.out.println("Score = " + pfe.getScore());
        System.out.println("Grade = " + pfe.getGrade());
        System.out.println("Missed = " + pfe.getNumMissed());
    }
}