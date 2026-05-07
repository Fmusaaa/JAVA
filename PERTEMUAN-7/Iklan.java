import java.util.Scanner;

public class Iklan {
    protected int kodeIklan;
    protected String nmIklan;
    protected double tarifIklan;

    public void inputIklan() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Kode Produk [1. Baris, 2. Display]: ");
        kodeIklan = sc.nextInt();
        if (kodeIklan == 1) {
            nmIklan = "Iklan Baris";
            tarifIklan = 20000;
        } else {
            nmIklan = "Iklan Display";
            tarifIklan = 25000;
        }
    }
}

class PasangIklan extends Iklan {
    private String noNota, nama, alamat, souvenir;
    private int jmlIklan, durasi;
    private double diskon, biayaIklan, ppn, totBiaya;
    private String cabang;

    public PasangIklan(String cabang) {
        this.cabang = cabang;
    }

    public void setNota() {
        Scanner sc = new Scanner(System.in);
        System.out.print("No. Nota      : "); noNota = sc.next();
        System.out.print("Nama Pemasang : "); nama = sc.next();
        System.out.print("Alamat        : "); alamat = sc.next();
    }

    public void inputJumlah() {
        Scanner sc = new Scanner(System.in);
        if (kodeIklan == 1) {
            System.out.print("Jml. Baris    : ");
        } else {
            System.out.print("Luas (mmk)    : ");
        }
        jmlIklan = sc.nextInt();
    }

    public void inputDurasi() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Durasi (hari) : "); durasi = sc.nextInt();
        System.out.print("Diskon (%)    : "); diskon = sc.nextDouble();
    }

    public void hitungBiaya() {
        // Logika biaya: (Tarif * jml) * durasi - diskon
        double bruto = (tarifIklan * jmlIklan) * durasi;
        double potongan = (diskon / 100) * bruto;
        biayaIklan = bruto - potongan;
        ppn = 0.10 * biayaIklan;
        totBiaya = biayaIklan + ppn;
    }

    public void tentukanSouvenir() {
        souvenir = "-";
        if (kodeIklan == 2) { // Khusus Display
            if (durasi >= 3 && durasi <= 6) souvenir = "Mug";
            else if (durasi >= 7 && durasi <= 10) souvenir = "Payung";
            else if (durasi >= 11 && durasi <= 14) souvenir = "Tas";
        }
    }

    // Getter untuk keperluan tabel
    public String getNoNota() { return noNota; }
    public String getNmIklan() { return nmIklan; }
    public double getTarif() { return tarifIklan; }
    public int getJml() { return jmlIklan; }
    public int getDurasi() { return durasi; }
    public double getPpn() { return ppn; }
    public double getTot() { return totBiaya; }
    public String getSouvenir() { return souvenir; }
}
