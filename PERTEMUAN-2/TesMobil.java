public class TesMobil {
    public static void main(String[] args) {
        Mobil Ferrari = new Mobil();
        Mobil Lamborghini = new Mobil();

        Ferrari.maju();
        Ferrari.mundur();
        Ferrari.belok();
        System.out.println("Roda Ferrari: " + Ferrari.roda);
        System.out.println("Mesin Ferrari: " + Ferrari.mesin);

        Lamborghini.maju();
        Lamborghini.mundur();
        Lamborghini.belok();
        System.out.println("Roda Lamborghini: " + Lamborghini.roda);
        System.out.println("Mesin Lamborghini: " + Lamborghini.mesin);
    }
}
