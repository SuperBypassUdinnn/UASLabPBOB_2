package com.restaurant.gui;

import com.restaurant.model.akun.Akun;
import com.restaurant.model.menu.MenuItem;
import com.restaurant.model.pesanan.DetailPesanan;
import com.restaurant.model.pesanan.Pesanan;
import com.restaurant.service.RestaurantSystem;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.Timer;

public class CustomerGUI extends JFrame {

    private RestaurantSystem sys = RestaurantSystem.getInstance();
    private int nomorMeja = -1;

    private JPanel pnlStatus;
    private DefaultListModel<String> cartModel;
    private List<DetailPesanan> keranjang = new ArrayList<>();
    private JLabel lblTotalCart;

    // Timer untuk refresh status pesanan real-time
    private Timer refreshTimer;

    public CustomerGUI(Akun akun) {
        this.akun = akun;

        // 1. Dialog Pilih Meja di Awal
        pilihMejaDialog();
        if (nomorMeja == -1) {
            // Jika user cancel pilih meja, tutup window
            dispose();
            return;
        }

        setTitle("Customer Dashboard - Meja " + nomorMeja + " (" + akun.getNama() + ")");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // TAB 1: MENU & PEMESANAN
        tabbedPane.addTab("Pesan Makanan", createOrderPanel());

        // TAB 2: STATUS PESANAN
        tabbedPane.addTab("Status Pesanan", createStatusPanel());

        // Header Logout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);

        // Auto refresh status setiap 3 detik
        refreshTimer = new Timer(3000, e -> refreshStatusPesanan());
        refreshTimer.start();
    }

    private void pilihMejaDialog() {
        String input = JOptionPane.showInputDialog(null, "Silakan Masukkan Nomor Meja Anda:", "Pilih Meja",
                JOptionPane.QUESTION_MESSAGE);
        if (input != null && !input.isEmpty()) {
            try {
                nomorMeja = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Nomor meja harus angka!");
                pilihMejaDialog();
            }
        }
    }

    // --- TAB 1: ORDER PANEL ---
    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // List Menu (Kiri)
        JPanel menuContainer = new JPanel(new GridLayout(0, 2, 10, 10));
        List<MenuItem> menuList = sys.getMenuList();

        for (MenuItem item : menuList) {
            menuContainer.add(createMenuItemCard(item));
        }

        JScrollPane scrollMenu = new JScrollPane(menuContainer);
        scrollMenu.setBorder(BorderFactory.createTitledBorder("Daftar Menu"));

        // Keranjang (Kanan)
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setPreferredSize(new Dimension(300, 0));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Keranjang"));

        cartModel = new DefaultListModel<>();
        JList<String> listCart = new JList<>(cartModel);
        cartPanel.add(new JScrollPane(listCart), BorderLayout.CENTER);

        JPanel checkoutPanel = new JPanel(new BorderLayout());
        lblTotalCart = new JLabel("Total: Rp 0");
        lblTotalCart.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalCart.setBorder(new EmptyBorder(5, 5, 5, 5));

        JButton btnPesan = new JButton("BUAT PESANAN");
        btnPesan.setBackground(new Color(34, 197, 94));
        btnPesan.setForeground(Color.WHITE);
        btnPesan.setFont(new Font("Arial", Font.BOLD, 14));
        btnPesan.addActionListener(e -> prosesCheckout());

        checkoutPanel.add(lblTotalCart, BorderLayout.NORTH);
        checkoutPanel.add(btnPesan, BorderLayout.SOUTH);
        cartPanel.add(checkoutPanel, BorderLayout.SOUTH);

        panel.add(scrollMenu, BorderLayout.CENTER);
        panel.add(cartPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMenuItemCard(MenuItem item) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);

        JLabel lblNama = new JLabel(item.getNama());
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblHarga = new JLabel("Rp " + (int) item.getHarga());
        lblHarga.setForeground(new Color(22, 163, 74));

        JButton btnAdd = new JButton("Tambah");
        btnAdd.addActionListener(e -> addToCart(item));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setBackground(Color.WHITE);
        info.add(lblNama);
        info.add(lblHarga);
        info.setBorder(new EmptyBorder(5, 5, 5, 5));

        card.add(info, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.EAST);
        return card;
    }

    private void addToCart(MenuItem item) {
        String cat = JOptionPane.showInputDialog("Catatan (opsional):");
        if (cat == null)
            cat = "";

        DetailPesanan dp = new DetailPesanan(item, 1, cat);
        keranjang.add(dp);
        updateCartDisplay();
    }

    private void updateCartDisplay() {
        cartModel.clear();
        double total = 0;
        for (DetailPesanan dp : keranjang) {
            cartModel.addElement(dp.getMenu().getNama() + " (Rp" + (int) dp.getSubtotal() + ")");
            total += dp.getSubtotal();
        }
        lblTotalCart.setText("Total: Rp " + (int) total);
    }

    private void prosesCheckout() {
        if (keranjang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang kosong!");
            return;
        }

        Pesanan p = sys.buatPesananKosong(nomorMeja);
        for (DetailPesanan dp : keranjang) {
            p.tambahItem(dp);
        }
        p.setStatus("MENUNGGU");
        sys.saveData();

        JOptionPane.showMessageDialog(this, "Pesanan berhasil dibuat! Mohon tunggu.");
        keranjang.clear();
        updateCartDisplay();
        refreshStatusPesanan(); // Update tab status
    }

    // --- TAB 2: STATUS PANEL ---
    private JComponent createStatusPanel() {
        pnlStatus = new JPanel();
        pnlStatus.setLayout(new BoxLayout(pnlStatus, BoxLayout.Y_AXIS));
        return new JScrollPane(pnlStatus);
    }

    private void refreshStatusPesanan() {
        pnlStatus.removeAll();
        sys.refreshPesananFromFile();
        List<Pesanan> myOrders = sys.getPesananByMeja(nomorMeja);

        if (myOrders.isEmpty()) {
            pnlStatus.add(new JLabel("Belum ada riwayat pesanan."));
        } else {
            for (Pesanan p : myOrders) {
                // Jangan tampilkan yang sudah lunas agar tidak penuh (opsional)
                if (p.getStatus().equals("LUNAS"))
                    continue;

                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(new CompoundBorder(
                        BorderFactory.createLineBorder(Color.GRAY, 1),
                        new EmptyBorder(10, 10, 10, 10)));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
                card.setBackground(Color.WHITE);

                JLabel lblInfo = new JLabel("Pesanan #" + p.getId() + " - Rp " + (int) p.getTotal());
                lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 16));

                JLabel lblStatus = new JLabel(p.getStatus());
                lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
                lblStatus.setForeground(getStatusColor(p.getStatus()));

                card.add(lblInfo, BorderLayout.WEST);
                card.add(lblStatus, BorderLayout.EAST);

                pnlStatus.add(card);
                pnlStatus.add(Box.createVerticalStrut(10));
            }
        }
        pnlStatus.revalidate();
        pnlStatus.repaint();
    }

    private Color getStatusColor(String s) {
        if (s.equals("MENUNGGU"))
            return Color.RED;
        if (s.equals("DIPROSES") || s.equals("SEDANG DIMASAK"))
            return Color.ORANGE;
        if (s.equals("SIAP DISAJIKAN"))
            return Color.BLUE;
        if (s.equals("DISAJIKAN"))
            return new Color(34, 197, 94);
        return Color.BLACK;
    }

    private JPanel createHeader() {
        JPanel h = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        h.setBackground(new Color(59, 130, 246));
        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            refreshTimer.stop();
            dispose();
            new LoginGUI().setVisible(true);
        });
        h.add(logout);
        return h;
    }
}