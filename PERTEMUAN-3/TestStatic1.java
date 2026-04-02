public class TestStatic1 {
    static int a = 10;
    static int b = 20;

    static void display() {
        System.out.println("a = " + a);
        System.out.println("b = " + b);
    }

    public static void main(String[] args) {
        TestStatic1.display();
    }
}