public class TestStatic {
    int a=10;
    static int b=20;
    protected int c=30;
    public int d=40;
    private int e=50;
    void satu (){
        dua();
        System.out.println("satu..............");
        System.err.println("Satu..............a : " + a);
        System.err.println("Satu..............b : " + b);
        System.err.println("Satu..............c : " + c);
        System.err.println("Satu..............d : " + d);
        System.err.println("Satu..............e : " + e);
    }
    static void dua(){
        //satu();  -->error static cell non static
        System.out.println("dua.............."+b);
        //System.out.println("Dua.............."+a);-->error static cell var non static

        
    }
    public static void main(String[] args) {
        //satu();-->error static cell non static
        dua();
    }
}