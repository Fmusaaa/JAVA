import java.io.*;
import java.text.DecimalFormat;

public class KaryawanDemo {
    public static void main(String[] args) throws IOException {
        DecimalFormat df = new DecimalFormat("###,###,###");
        Karyawan kar = new Karyawan();
        KaryawanTetap tetap = new KaryawanTetap();
        KaryawanKontrak kontrak = new KaryawanKontrak();
        
        kar.inputkar();
        
        // Memperbaiki error == 1 dari kode awal
        if (kar.sts_peg == 1) { 
            tetap.inputtetap();
            System.out.println("Gaji Diterima: " + df.format(tetap.totalGaji()));
        } else {
            kontrak.inputkontrak();
            System.out.println("Upah Diterima: " + df.format(kontrak.totalUpah()));
        }
    }
}