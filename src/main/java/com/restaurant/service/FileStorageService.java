package main.java.com.restaurant.service;

import java.io.*;
import java.util.*;
import main.java.com.restaurant.model.menu.*;
import main.java.com.restaurant.model.pesanan.*;

public class FileStorageService {

    private static final String MENU_FILE = "src/main/resources/data/menu.txt";
    private static final String PESANAN_FILE = "src/main/resources/data/pesanan.txt";

    // ============================
    // LOAD MENU
    // ============================
    public static List<MenuItem> loadMenu() {
        List<MenuItem> menu = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(MENU_FILE))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] arr = line.split(";");

                if (arr[0].equals("makanan")) {
                    menu.add(new Makanan(arr[1],
                            Double.parseDouble(arr[2]),
                            arr[3],
                            arr[4]));
                } else if (arr[0].equals("minuman")) {
                    menu.add(new Minuman(arr[1],
                            Double.parseDouble(arr[2]),
                            arr[3],
                            arr[4]));
                }
            }
        } catch (Exception e) {
            System.out.println("Menu file kosong, membuat data dummy...");
            menu = dummyMenu();
        }

        return menu;
    }

    private static List<MenuItem> dummyMenu() {
        List<MenuItem> d = new ArrayList<>();
        d.add(new Makanan("Nasi Goreng", 20000, "Main Course", "Sedang"));
        d.add(new Minuman("Es Teh", 7000, "Medium", "Dingin"));
        return d;
    }

    // ============================
    // SAVE PESANAN
    // ============================
    public static void savePesanan(List<Pesanan> list) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PESANAN_FILE))) {

            for (Pesanan p : list) {
                pw.print(p.getId() + ";" + p.getMeja().getNomor() + ";" + p.getStatus());

                for (DetailPesanan dp : p.getDetail()) {
                    pw.print(";" + dp.getItem().getNama() + ":" + dp.getJumlah());
                }

                pw.println();
            }

        } catch (Exception e) {
            System.out.println("Gagal menyimpan pesanan.");
        }
    }
}
