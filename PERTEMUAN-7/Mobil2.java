public class Mobil2{
    int roda = 4;
    int body = 1;
    static int mesin = 1;
    
    static void maju() {
        System.out.println("Mobil maju");
    }
    
    void mundur() {
        System.out.println("Mobil mundur");
    }
    
    void belok() {
        System.out.println("Mobil belok");
    }
    
    void hidupkanMobil(String nama) {
        System.out.println("Mobil hidup : " + nama);
    }
    
    void matikanMobil(String nama) {
        System.out.println("Mobil mati : " + nama);
    }
    
    void ubahGigi(String nama){
        System.out.println("Mobil ganti gigi : " + nama);
    }
}
