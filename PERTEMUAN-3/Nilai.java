import java.util.Scanner;
public class Nilai{
    String nim,nama;
    float nilaiUts,nilaiTugas,nilaiUas,pNilaiUts,pNilaiTugas,pNilaiUas;
    Scanner in=new Scanner(System.in);
    public Nilai(){}
    public Nilai(String nim,String nama,float nilaiUts,float nilaiTugas,float nilaiUas){
        this.nim=nim;
        this.nama=nama;
        this.nilaiUts=nilaiUts;
        this.nilaiTugas=nilaiTugas;
        this.nilaiUas=nilaiUas;
    }

}