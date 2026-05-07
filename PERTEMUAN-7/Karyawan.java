import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Karyawan {
    String nip, nama;
    int sts_peg;
    
    void inputkar() throws IOException {
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("=== Data Karyawan ===");
        System.out.print("NIP : ");
        nip = keyboard.readLine();
        System.out.print("Nama : ");
        nama = keyboard.readLine();
        System.out.print("Status Peg : ");
        sts_peg = Integer.parseInt(keyboard.readLine());
    }
}