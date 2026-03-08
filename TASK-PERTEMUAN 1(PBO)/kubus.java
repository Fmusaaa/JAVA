/**
 * Program: kubus
 * @author Fadhlillah Musa Ulil Albab
 * @github: Fmusaaa
 */
public class kubus {
    public static void main(String[] args) {
        float sisi = 5.0f;
        float volume;
        float luasPermukaan;
        
        volume = sisi * sisi * sisi;
        luasPermukaan = 6 * sisi * sisi;
        
        System.out.println("Sisi Kubus: " + sisi);
        System.out.println("Volume Kubus: " + volume);
        System.out.println("Luas Permukaan Kubus: " + luasPermukaan);
    }
}
