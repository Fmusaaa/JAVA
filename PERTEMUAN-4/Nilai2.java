import java.util.Scanner;
public class Nilai2{
    String nim,nama;
    float nilaiUts,nilaiTugas,nilaiUas,pNilaiUts,pNilaiTugas,pNilaiUas;
    Scanner in=new Scanner(System.in);
    public Nilai2(){}
    public Nilai2(String nim,String nama,float nilaiUts,float nilaiTugas,float nilaiUas){
        this.nim=nim;
        this.nama=nama;
        this.nilaiUts=nilaiUts;
        this.nilaiTugas=nilaiTugas;
        this.nilaiUas=nilaiUas;
    }

}