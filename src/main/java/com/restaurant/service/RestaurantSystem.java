package main.java.com.restaurant.service;

import java.util.ArrayList;
import java.util.List;

import main.java.com.restaurant.model.menu.*;
import main.java.com.restaurant.model.pesanan.*;

public class RestaurantSystem {

    private List<MenuItem> menu = new ArrayList<>();
    private List<Pesanan> pesanan = new ArrayList<>();

    public RestaurantSystem() {
        // contoh menu default
        menu.add(new Makanan("Nasi Goreng", 20000, "Main Course", "Sedang"));
        menu.add(new Minuman("Es Teh", 8000, "Medium", "Dingin"));
    }

    public void tampilMenu() {
        int no = 1;
        for (MenuItem m : menu) {
            System.out.println(no++ + ". " + m.getInfo());
        }
    }

    public MenuItem getMenu(int index) {
        return menu.get(index);
    }

    public Pesanan buatPesanan(int id, int noMeja) {
        Pesanan p = new Pesanan(id, new Meja(noMeja));
        pesanan.add(p);
        return p;
    }
}
