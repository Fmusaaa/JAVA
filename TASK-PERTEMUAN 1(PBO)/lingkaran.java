/**
 * Program: lingkaran
 * @author Fadhlillah Musa Ulil Albab
 * @github: Fmusaaa
 */
public class lingkaran {
    public static void main(String[] args) {
        float PHI = 3.17f;
        int r=12;
        double luas=PHI*r*r;
        double kel=2*PHI*r;

        System.out.println("phi: " + PHI);
        System.out.println("Luas Lingkaran: " + luas);
        System.out.println("Keliling Lingkaran: " + kel);
        System.err.println("jari jari " + r);
    }
}