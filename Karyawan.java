import javv.io.*;
public class Karyawan{

    String nip,nama;
    int sts_peg;

    void inputKar() throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Data Karyawan : ");
        System.out.print(" NIP : ");
        nip = keyboard.readLine();
        System.out.print(" Nama : ");
        nama = keyboard.readLine();
        System.out.print(" sts : ");
        sts_peg = Integer.parseInt(keyboard.readLine());
    }
}
