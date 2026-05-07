import java.io.*;

public class KaryawanTetap extends Karyawan {
    double gapok;
    int jml_anak;
    
    void inputtetap() throws IOException {
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("=== Karyawan Tetap ===");
        System.out.print("Gaji Pokok : ");
        gapok = Double.parseDouble(keyboard.readLine());
        System.out.print("Jumlah Anak : ");
        jml_anak = Integer.parseInt(keyboard.readLine());
    }
    
    double totalGaji() {
        return gapok + (0.1 * gapok * jml_anak);
    }
}