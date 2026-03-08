/**
 * Program: Segitiga
 * @author Fadhlillah Musa Ulil Albab
 * @github: Fmusaaa
 */
public class Segitiga {
    public static void main(String[] args) {
        double alas = 10.0;
        double tinggi = 5.0;
        double sisi1 = 8.0;
        double sisi2 = 6.0;
        double sisi3 = 7.0;
        
        double luas = (alas * tinggi) / 2;
        double keliling = sisi1 + sisi2 + sisi3;
        
        System.out.println("Alas: " + alas);
        System.out.println("Tinggi: " + tinggi);
        System.out.println("Sisi 1: " + sisi1);
        System.out.println("Sisi 2: " + sisi2);
        System.out.println("Sisi 3: " + sisi3);
        System.out.println("Luas Segitiga: " + luas);
        System.out.println("Keliling Segitiga: " + keliling);
    }
}
