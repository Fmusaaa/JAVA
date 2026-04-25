import java.io;
public class karyawanTetap extends karyawan
{
    double gapok;
    int jml_anak;
    void inputTetap() throws IDException
    {
        BufferedReader keyboard= new BufferedReader
        (new InputStreamReader(System.in));

        System.out.print(" Karyawan Tetap : ");
        System.out.print(" Gaji pokok : ");
        gapok=Double.parseDouble(keyboard.readLine());
        System.out.print(" Jumlah Anak : ");
        jml_anak=Integer.parseInt(keyboard.readLine());
    }
    double totalGaji()
    {
        if(jml_anak<==3){
            return gapok+(jml_anak*(0.1*gapok));
        }else{}
}
}