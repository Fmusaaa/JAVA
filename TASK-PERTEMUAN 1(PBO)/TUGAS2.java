import java.util.Scanner;

/**
 * Program: Menghitung Luas dan Keliling Bangun Datar & Ruang
 * Persegi, Segitiga, Lingkaran, dan Kubus
 */

public class TUGAS2 {
    static Scanner input = new Scanner(System.in);
    
    // ===== PERSEGI =====
    static void hitungPersegi() {
        System.out.println("\n========== PERSEGI ==========");
        System.out.print("Masukkan sisi (s): ");
        double s = input.nextDouble();
        
        double luas = s * s;
        double keliling = 4 * s;
        
        System.out.println("Luas Persegi    : " + luas);
        System.out.println("Keliling Persegi: " + keliling);
    }
    
    // ===== SEGITIGA =====
    static void hitungSegitiga() {
        System.out.println("\n========== SEGITIGA ==========");
        System.out.print("Masukkan alas (a): ");
        double alas = input.nextDouble();
        System.out.print("Masukkan tinggi (t): ");
        double tinggi = input.nextDouble();
        System.out.print("Masukkan sisi 1 (s1): ");
        double s1 = input.nextDouble();
        System.out.print("Masukkan sisi 2 (s2): ");
        double s2 = input.nextDouble();
        System.out.print("Masukkan sisi 3 (s3): ");
        double s3 = input.nextDouble();
        
        double luas = (alas * tinggi) / 2;
        double keliling = s1 + s2 + s3;
        
        System.out.println("Luas Segitiga    : " + luas);
        System.out.println("Keliling Segitiga: " + keliling);
    }
    
    // ===== LINGKARAN =====
    static void hitungLingkaran() {
        System.out.println("\n========== LINGKARAN ==========");
        System.out.print("Masukkan jari-jari (r): ");
        double r = input.nextDouble();
        
        double luas = Math.PI * r * r;
        double keliling = 2 * Math.PI * r;
        
        System.out.println("Luas Lingkaran    : " + luas);
        System.out.println("Keliling Lingkaran: " + keliling);
    }
    
    // ===== KUBUS =====
    static void hitungKubus() {
        System.out.println("\n========== KUBUS ==========");
        System.out.print("Masukkan sisi (s): ");
        double s = input.nextDouble();
        
        double luasPermukaan = 6 * s * s;
        double volume = s * s * s;
        
        System.out.println("Luas Permukaan Kubus: " + luasPermukaan);
        System.out.println("Volume Kubus        : " + volume);
    }
    
    // ===== MENU =====
    public static void main(String[] args) {
        int pilihan;
        boolean lanjut = true;
        
        while (lanjut) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║   MENGHITUNG LUAS DAN KELILING     ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. Persegi                         ║");
            System.out.println("║ 2. Segitiga                        ║");
            System.out.println("║ 3. Lingkaran                       ║");
            System.out.println("║ 4. Kubus                           ║");
            System.out.println("║ 5. Keluar                          ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Pilih menu (1-5): ");
            pilihan = input.nextInt();
            
            switch(pilihan) {
                case 1:
                    hitungPersegi();
                    break;
                case 2:
                    hitungSegitiga();
                    break;
                case 3:
                    hitungLingkaran();
                    break;
                case 4:
                    hitungKubus();
                    break;
                case 5:
                    System.out.println("\nTerima kasih sudah menggunakan program ini!");
                    lanjut = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid! Silakan pilih 1-5");
            }
        }
        input.close();
    }
}
