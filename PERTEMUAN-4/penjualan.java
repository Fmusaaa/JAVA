import java.util.Scanner;

public class penjualan {
    private String kode;
    private String nama;
    private float harga;
    private int jumlah;

    public void setData(String kode, String nama, float harga, int jumlah) {
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
        this.jumlah = jumlah;
    }

    public float getTotalPembelian() {
        return harga * jumlah;
    }

    public String getBonus() {
        float total = getTotalPembelian();
        if (total >= 500000 && jumlah > 5) {
            return "Setrika";
        } else if (total >= 100000 && jumlah > 3) {
            return "Payung";
        } else if (total >= 50000 || jumlah > 2) {
            return "Ballpoint";
        } else {
            return "-";
        }
    }

    public void cetakNota() {
        System.out.println("----------------------------");
        System.out.println("Nota Pembelian");
        System.out.println("Kode    : " + kode);
        System.out.println("Nama    : " + nama);
        System.out.println("Harga   : " + harga);
        System.out.println("Jumlah  : " + jumlah);
        System.out.println("Total   : " + getTotalPembelian());
        System.out.println("Bonus   : " + getBonus());
        System.out.println("----------------------------");
    }

    // Interactive main to read multiple entries
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String lagi = "Y";
        while (true) {
            penjualan p = new penjualan();
            System.out.print("Input kode: ");
            String kode = sc.nextLine();
            System.out.print("Input nama: ");
            String nama = sc.nextLine();

            float harga = 0f;
            while (true) {
                try {
                    System.out.print("Input harga: ");
                    harga = Float.parseFloat(sc.nextLine());
                    if (harga < 0) {
                        System.out.println("Harga tidak boleh negatif");
                        continue;
                    }
                    break;
                } catch (NumberFormatException ex) {
                    System.out.println("Masukkan angka untuk harga!");
                }
            }

            int jumlah = 0;
            while (true) {
                try {
                    System.out.print("Input jumlah: ");
                    jumlah = Integer.parseInt(sc.nextLine());
                    if (jumlah < 0) {
                        System.out.println("Jumlah tidak boleh negatif");
                        continue;
                    }
                    break;
                } catch (NumberFormatException ex) {
                    System.out.println("Masukkan angka untuk jumlah!");
                }
            }

            p.setData(kode, nama, harga, jumlah);
            p.cetakNota();

            System.out.print("Input data lagi [Y/T] ? ");
            lagi = sc.nextLine().trim();
            if (lagi.equalsIgnoreCase("T")) {
                break;
            }
            // any other reply continue (including Y)
        }
        sc.close();
        System.out.println("Selesai.");
    }
}

    

