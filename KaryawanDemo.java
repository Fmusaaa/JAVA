import.java.io.*;
import.java.text.DecimalFormat;
public class KaryawanDemo{
    public static void main(String[] args) throws IDException
    {   DecimalFormat df= new DecimalFormat("###,###,###.00");
        Karyawan kr= new Karyawan();
        KaryawanTetap tetap=new KaryawanTetap();
        KaryawanKontrak kontrak=new KaryawanKontrak();
        
        kar.inputKar();
        if(kar.sts_peg==1)
        {
            tetap.inputTetap();
            System.out.println("Gaji Diterima :"+
            df.format(tetap.totalGaji()));
        }else
        {
            kontrak.inputKontrak();
            System.out.println("Gaji Diterima :"+
            df.format(kontrak.totalGaji()));
        }


    }
}