import java.util.Scanner;
import java.util.InputMismatchException;

public class MainIklan {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Masukkan Nama Agen\t: ");
        String cabang = sc.nextLine();
        
        int jumlahData = 0;
        while (true) {
            try {
                System.out.print("Jumlah Data Iklan\t: ");
                jumlahData = sc.nextInt();
                break; // Keluar dari loop jika input benar
            } catch (InputMismatchException e) {
                System.out.println("Maaf bro, masukin angka ya buat Jumlah Data Iklan!");
                sc.next(); // Bersihkan input yang salah
            }
        }
        
        PasangIklan[] daftarIklan = new PasangIklan[jumlahData];
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < jumlahData; i++) {
            System.out.println("\n============ Data Ke-" + (i+1) + " ============");
            daftarIklan[i] = new PasangIklan(cabang);
            daftarIklan[i].setNota();
            
            // Loop validasi agar tidak error jika salah input
            while(true){
                try {
                    daftarIklan[i].inputIklan();
                    break;
                } catch(InputMismatchException e){
                    System.out.println("Harap masukkan angka (1 atau 2).");
                }
            }
            
            while(true){
                try{
                     daftarIklan[i].inputJumlah();
                     break;
                }catch(InputMismatchException e){
                    System.out.println("Harap masukkan angka untuk Jumlah/Luas Iklan.");
                }
            }
           
            while(true){
                try{
                    daftarIklan[i].inputDurasi();
                    break;
                }catch(InputMismatchException e){
                    System.out.println("Harap masukkan angka untuk Durasi dan Diskon.");
                }
            }
            
            daftarIklan[i].hitungBiaya();
            daftarIklan[i].tentukanSouvenir();
        }
        
        long endTime = System.currentTimeMillis();
        double timeTakenHours = (endTime - startTime) / (1000.0 * 60 * 60); // Waktu dihitung ke dalam satuan jam (sesuai spesifikasi soal)
        
        String nilai = "";
        if (timeTakenHours < 1.0) {
            nilai = "A";
        } else if (timeTakenHours >= 1.01 && timeTakenHours <= 1.15) {
            nilai = "AB";
        } else if (timeTakenHours >= 1.16 && timeTakenHours <= 1.30) {
            nilai = "B";
        } else {
            nilai = "C-BC";
        }
        
        System.out.println("\n\nDaftar Iklan Cetak " + cabang);
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("No   Nota      Produk Iklan    Tarif       Jml/Luas Durasi  PPN        Total Biaya   Souvenir");
        System.out.println("--------------------------------------------------------------------------------------------------");
        
        double totalKeseluruhan = 0;
        double totalPPN = 0;
        
        for (int i = 0; i < daftarIklan.length; i++) {
            System.out.printf("%-4d %-9s %-15s %-11.0f %-8d %-7d %-10.0f %-13.0f %s\n",
                (i + 1),
                daftarIklan[i].getNoNota(),
                daftarIklan[i].getNmIklan(),
                daftarIklan[i].getTarif(),
                daftarIklan[i].getJml(),
                daftarIklan[i].getDurasi(),
                daftarIklan[i].getPpn(),
                daftarIklan[i].getTot(),
                daftarIklan[i].getSouvenir());
                
            totalPPN += daftarIklan[i].getPpn();
            totalKeseluruhan += daftarIklan[i].getTot();
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf("\t\t\t\t\t***Total***\t %-10.0f %-13.0f\n", totalPPN, totalKeseluruhan);
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.println("\nKet:");
        System.out.println("Nilai diperoleh: " + nilai);
    }
}