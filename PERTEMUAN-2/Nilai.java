public class Nilai {
    String nim;
    String nama;
    double nilaiUts;
    double nilaiTugas;
    double nilaiUas;

    double pNilaiUts;   // 20% dari nilaiUts
    double pNilaiTugas; // 35% dari nilaiTugas
    double pNilaiUas;   // 45% dari nilaiUas
    double nilaiAkhir;

    public Nilai() {}

    public Nilai(String nim, String nama, double nilaiUts, double nilaiTugas, double nilaiUas) {
        this.nim = nim;
        this.nama = nama;
        this.nilaiUts = nilaiUts;
        this.nilaiTugas = nilaiTugas;
        this.nilaiUas = nilaiUas;
    }

    public void hitungNilai() {
        pNilaiUts = 0.20 * nilaiUts;
        pNilaiTugas = 0.35 * nilaiTugas;
        pNilaiUas = 0.45 * nilaiUas;
        nilaiAkhir = pNilaiUts + pNilaiTugas + pNilaiUas;
    }

    public void cetakNilai() {
        System.out.println("Nim		: " + nim);
        System.out.println("Nama		: " + nama);
        System.out.printf("Nilai Tugas : %.1f 35%% : %.1f\n", nilaiTugas, pNilaiTugas);
        System.out.printf("Nilai Uts   : %.1f 20%% : %.1f\n", nilaiUts, pNilaiUts);
        System.out.printf("Nilai Uas   : %.1f 45%% : %.1f\n", nilaiUas, pNilaiUas);
        System.out.printf("Nilai Akhir : %.6f\n", nilaiAkhir);
    }
}
