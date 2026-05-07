class Satu2 {
    Satu2(String s){
        System.out.println("satu....." + s);
    }
}

class Dua2 extends Satu2{
    Dua2(String s) {
        super(s); 
        System.out.println("dua.....");
    }
}

class Tiga2 extends Dua2{
    Tiga2(String s){
        super(s); 
        System.out.println("tiga.....");
    }
}

class Empat2 extends Tiga2{
    Empat2(String s){
        super(s); 
        System.out.println("empat.....");
    }
}

public class TestTurunan2{
    public static void main(String[] a){
        new Empat2("kiriman untuk mbah buyut.... alfatihah");
    }
}
