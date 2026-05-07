import java.util.Scanner;
import java.util.Random;

public class PasangIklan extends Iklan {
    private String noNota, nama, alamat;
    private int jmlIklan, durasi; // jmlIklan bisa berarti jumlah baris atau luas
    private double diskon, biayaIklan, ppn, totBiaya;
    private String souvenir, cabang;

    public PasangIklan(String cabang) {
        this.cabang = cabang;
    }

    public void setNota() {
        Random rand = new Random();
        int notaNum = rand.nextInt(900000) + 100000; // Generate 6 digit angka
        this.noNota = String.valueOf(notaNum);
        System.out.println("No. Nota\t: " + this.noNota);
    }

    public void inputData(Scanner sc) {
        System.out.println("\nIklan Cetak - " + cabang);
        setNota();
        sc.nextLine(); // Clear buffer
        System.out.print("Nama Pemasang\t: ");
        nama = sc.nextLine();
        System.out.print("Alamat\t\t: ");
        alamat = sc.nextLine();
        
        super.inputIklan(sc);
        inputJumlah(sc);
        inputDurasi(sc);
        getDiskon(sc);
        getBiayaIklan();
        getSouvenir();
        getTotal();
    }

    public void inputJumlah(Scanner sc) {
        if (kodeIklan == 1) {
            System.out.print("Jml. Baris\t: ");
        } else if (kodeIklan == 2) {
            System.out.print("Luas (mmk)\t: ");
        }
        jmlIklan = sc.nextInt();
    }

    public double getTarifIklan() {
        return tarifIklan;
    }

    public void inputDurasi(Scanner sc) {
        System.out.print("Durasi (hari)\t: ");
        durasi = sc.nextInt();
    }

    public void getDiskon(Scanner sc) {
        System.out.print("Diskon (%)\t: ");
        double persenDiskon = sc.nextDouble();
        // Diskon dihitung dari total kotor
        double grossBiaya = (tarifIklan * jmlIklan) * durasi;
        diskon = grossBiaya * (persenDiskon / 100.0);
    }

    public void getBiayaIklan() {
        double grossBiaya = (tarifIklan * jmlIklan) * durasi;
        biayaIklan = grossBiaya - diskon;
    }

    public void getSouvenir() {
        souvenir = "-";
        if (kodeIklan == 2) {
            if (durasi >= 3 && durasi <= 6) {
                souvenir = "Mug";
            } else if (durasi >= 7 && durasi <= 10) {
                souvenir = "Payung";
            } else if (durasi >= 11 && durasi <= 14) {
                souvenir = "Tas";
            } else if (durasi > 14) {
                souvenir = "Tas"; // Assuming default if more than 14, or can remain '-'
            }
        }
    }

    public void getTotal() {
        ppn = 0.10 * biayaIklan;
        totBiaya = biayaIklan + ppn;
    }

    public void cetak(int no) {
        System.out.printf("%-4d %-8s %-12s %-10.0f %-8d %-7d %-7.0f %-11.0f %-10s\n",
                no, noNota, nmIklan, getTarifIklan(), jmlIklan, durasi, ppn, totBiaya, souvenir);
    }
    
    // Getters for summary footer
    public double getPpn() {
        return ppn;
    }
    
    public double getTotBiaya() {
        return totBiaya;
    }
}
