package id.ac.dinus.test;
import id.ac.dinus.lib.*;
public class HisLib {

    public void cetak() {
        System.out.println("HisLib cetak()......");
    }

    public void cetak3() {
        HisLib m=new HisLib();
        m.cetak();
        System.out.println("HisLib cetak()......");
    }
}
