class Mhs {
    String nim, nm;
    float tgs, uts, uas;
    
    Mhs(float a, float b, float c) {
        tgs = a;
        uts = b;
        uas = c;
    }
    
    void cetak() {
        System.out.println("Nama :" + nm);
        System.out.println("Tgs :" + tgs);
        System.out.println("Uts :" + uts);
        System.out.println("Uas :" + uas);
    }
    
    void cetak(String s) {
        System.out.println(s);
    }
}

class Nilai extends Mhs {
    float na;
    
    Nilai(float l, float m, float n) {
        super(l, m, n);
        na = 0;
    }
    
    void cetak() {
        super.cetak("Mahasiswa");
        super.cetak();
    }
    
    void cetak(String x) {
        System.out.println(x);
        System.out.println("Nim = " + super.nim);
        System.out.println("Nama = " + super.nm);
        System.out.println("Tgs = " + tgs);
        System.out.println("Uts = " + uts);
        System.out.println("Uas = " + uas);
        System.out.println("NA = " + na);
    }
    
    public static void main(String args[]) {
        Nilai obj = new Nilai(90, 90, 85);
        obj.nim = "A11.";
        obj.nm = "Najwa";
        obj.na = (obj.tgs + obj.uts + obj.uas) / 3;
        obj.cetak();
        obj.cetak("Nilai");
    }
}
