package com.restaurant.gui;

import com.restaurant.model.akun.Akun;
import com.restaurant.model.menu.MenuItem;
import com.restaurant.model.pesanan.DetailPesanan;
import com.restaurant.model.pesanan.Meja;
import com.restaurant.model.pesanan.Pesanan;
import com.restaurant.service.RestaurantSystem;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

public class CustomerGUI extends JFrame {

    private RestaurantSystem sys = RestaurantSystem.getInstance();
    private Akun akun;

    // Components
    private JPanel pnlMejaKosong;
    private JTextField tfNomorMeja;
    private JTextArea taCatatanPesanan;
    private DefaultListModel<String> cartModel;
    private List<DetailPesanan> keranjang = new ArrayList<>();
    private JLabel lblTotalCart;
    private JPanel pnlStatus;
    private Timer refreshTimer;

    public CustomerGUI(Akun akun) {
        this.akun = akun;

        // HAPUS DIALOG PILIH MEJA DI AWAL

        setTitle("Customer Dashboard - " + akun.getNama());
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Buat Pesanan", createOrderPanel());
        tabbedPane.addTab("Riwayat & Struk", createHistoryPanel());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);

        refreshTimer = new Timer(3000, e -> {
            refreshHistory();
            refreshMejaKosong();
        });
        refreshTimer.start();
        refreshMejaKosong(); // Init load
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- KIRI: MENU ---
        JPanel menuContainer = new JPanel(new GridLayout(0, 3, 10, 10)); // Grid 3 kolom
        List<MenuItem> menuList = sys.getMenuList();
        for (MenuItem item : menuList) {
            menuContainer.add(createMenuItemCard(item));
        }
        JScrollPane scrollMenu = new JScrollPane(menuContainer);
        scrollMenu.setBorder(BorderFactory.createTitledBorder("Pilih Menu"));
        panel.add(scrollMenu, BorderLayout.CENTER);

        // --- KANAN: PANEL INPUT & KERANJANG ---
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setPreferredSize(new Dimension(350, 0));

        // 1. Panel Info & Input Meja (ATAS KANAN)
        JPanel pnlInfo = new JPanel(new BorderLayout());
        pnlInfo.setBorder(new TitledBorder("Informasi Meja"));

        // Tampilan Meja Kosong
        pnlMejaKosong = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlMejaKosong.setPreferredSize(new Dimension(300, 60));
        JScrollPane scrollMeja = new JScrollPane(pnlMejaKosong);
        scrollMeja.setBorder(null);

        // Input Meja
        JPanel pnlInputMeja = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlInputMeja.add(new JLabel("Pilih Nomor Meja: "));
        tfNomorMeja = new JTextField(5);
        pnlInputMeja.add(tfNomorMeja);

        JPanel pnlTopRight = new JPanel(new BorderLayout());
        pnlTopRight.add(new JLabel("  Meja Tersedia:"), BorderLayout.NORTH);
        pnlTopRight.add(scrollMeja, BorderLayout.CENTER);
        pnlTopRight.add(pnlInputMeja, BorderLayout.SOUTH);

        pnlInfo.add(pnlTopRight, BorderLayout.CENTER);
        rightPanel.add(pnlInfo, BorderLayout.NORTH);

        // 2. Keranjang (TENGAH KANAN)
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBorder(new TitledBorder("Keranjang Pesanan"));
        cartModel = new DefaultListModel<>();
        JList<String> listCart = new JList<>(cartModel);
        cartPanel.add(new JScrollPane(listCart), BorderLayout.CENTER);
        rightPanel.add(cartPanel, BorderLayout.CENTER);

        // 3. Catatan & Total (BAWAH KANAN)
        JPanel pnlCheckout = new JPanel(new BorderLayout(5, 5));
        pnlCheckout.setBorder(new EmptyBorder(5, 0, 0, 0));

        // Input Catatan Global
        pnlCheckout.add(new JLabel("Catatan Tambahan (Opsional):"), BorderLayout.NORTH);
        taCatatanPesanan = new JTextArea(3, 20);
        taCatatanPesanan.setBorder(new LineBorder(Color.LIGHT_GRAY));
        pnlCheckout.add(new JScrollPane(taCatatanPesanan), BorderLayout.CENTER);

        // Total & Tombol
        JPanel pnlAction = new JPanel(new BorderLayout());
        lblTotalCart = new JLabel("Total: Rp 0", SwingConstants.CENTER);
        lblTotalCart.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalCart.setBorder(new EmptyBorder(10, 0, 10, 0));

        JButton btnOrder = new JButton("KIRIM PESANAN");
        btnOrder.setBackground(new Color(34, 197, 94));
        btnOrder.setForeground(Color.WHITE);
        btnOrder.setFont(new Font("Arial", Font.BOLD, 14));
        btnOrder.setPreferredSize(new Dimension(100, 45));
        btnOrder.setOpaque(true);
        btnOrder.setBorderPainted(false);
        btnOrder.addActionListener(e -> prosesCheckout());

        JButton btnClear = new JButton("Reset");
        btnClear.setBackground(new Color(220, 53, 69));
        btnClear.setForeground(Color.WHITE);
        btnClear.addActionListener(e -> resetKeranjang());

        JPanel pnlBtns = new JPanel(new GridLayout(1, 2, 5, 0));
        pnlBtns.add(btnClear);
        pnlBtns.add(btnOrder);

        pnlAction.add(lblTotalCart, BorderLayout.NORTH);
        pnlAction.add(pnlBtns, BorderLayout.CENTER);

        pnlCheckout.add(pnlAction, BorderLayout.SOUTH);
        rightPanel.add(pnlCheckout, BorderLayout.SOUTH);

        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    private void refreshMejaKosong() {
        pnlMejaKosong.removeAll();
        sys.refreshPesananFromFile();
        List<Meja> kosong = sys.getMejaKosong();

        for (Meja m : kosong) {
            JLabel lbl = new JLabel("[" + m.getNomor() + "] ");
            lbl.setForeground(new Color(22, 163, 74));
            lbl.setFont(new Font("Monospaced", Font.BOLD, 14));
            pnlMejaKosong.add(lbl);
        }
        pnlMejaKosong.revalidate();
        pnlMejaKosong.repaint();
    }

    private JPanel createMenuItemCard(MenuItem item) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new LineBorder(new Color(229, 231, 235)));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(150, 80));

        JLabel lblNama = new JLabel("<html><center>" + item.getNama() + "</center></html>", SwingConstants.CENTER);
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblHarga = new JLabel("Rp " + (int) item.getHarga(), SwingConstants.CENTER);
        lblHarga.setForeground(new Color(100, 116, 139));

        JButton btnAdd = new JButton("Tambah");
        btnAdd.setBackground(new Color(241, 245, 249));
        btnAdd.addActionListener(e -> tambahItemDialog(item));

        card.add(lblNama, BorderLayout.NORTH);
        card.add(lblHarga, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.SOUTH);
        return card;
    }

    private void tambahItemDialog(MenuItem item) {
        // HAPUS INPUT CATATAN PER ITEM
        String qtyStr = JOptionPane.showInputDialog(this, "Masukkan Jumlah untuk " + item.getNama() + ":", "1");
        if (qtyStr != null && !qtyStr.isEmpty()) {
            try {
                int qty = Integer.parseInt(qtyStr);
                if (qty > 0) {
                    // Logic Merge: Jika item sudah ada, tambah qty
                    boolean exists = false;
                    for (DetailPesanan dp : keranjang) {
                        if (dp.getMenu().getNama().equals(item.getNama())) {
                            dp.setJumlah(dp.getJumlah() + qty);
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        keranjang.add(new DetailPesanan(item, qty));
                    }
                    updateCartDisplay();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Jumlah harus angka!");
            }
        }
    }

    private void updateCartDisplay() {
        cartModel.clear();
        double total = 0;
        for (DetailPesanan dp : keranjang) {
            cartModel.addElement(dp.getMenu().getNama() + " x" + dp.getJumlah() + " = " + (int) dp.getSubtotal());
            total += dp.getSubtotal();
        }
        lblTotalCart.setText("Total: Rp " + (int) total);
    }

    private void resetKeranjang() {
        keranjang.clear();
        updateCartDisplay();
        taCatatanPesanan.setText("");
        tfNomorMeja.setText("");
    }

    private void prosesCheckout() {
        if (keranjang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang kosong!");
            return;
        }
        String mejaStr = tfNomorMeja.getText().trim();
        if (mejaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Harap isi Nomor Meja!");
            return;
        }

        try {
            int meja = Integer.parseInt(mejaStr);
            // Validasi Meja Kosong
            boolean valid = sys.getMejaKosong().stream().anyMatch(m -> m.getNomor() == meja);
            if (!valid) {
                JOptionPane.showMessageDialog(this,
                        "Meja " + meja + " sedang dipakai atau tidak ada. Pilih meja lain.");
                return;
            }

            Pesanan p = sys.buatPesananKosong(meja, akun.getNama());
            for (DetailPesanan dp : keranjang)
                p.tambahItem(dp);

            // Set Catatan Global
            String note = taCatatanPesanan.getText().trim();
            p.setCatatan(note.isEmpty() ? "-" : note);

            sys.saveData();

            JOptionPane.showMessageDialog(this, "Pesanan Berhasil Dibuat!");
            resetKeranjang();
            refreshMejaKosong();
            refreshHistory();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nomor Meja harus angka!");
        }
    }

    // --- TAB HISTORY (Sama seperti sebelumnya, disesuaikan dikit) ---
    private JComponent createHistoryPanel() {
        pnlStatus = new JPanel();
        pnlStatus.setLayout(new BoxLayout(pnlStatus, BoxLayout.Y_AXIS));
        return new JScrollPane(pnlStatus);
    }

    private void refreshHistory() {
        pnlStatus.removeAll();
        sys.refreshPesananFromFile();
        List<Pesanan> myOrders = sys.getDaftarPesanan();

        boolean ada = false;
        for (Pesanan p : myOrders) {
            if (p.getNamaPelanggan().equalsIgnoreCase(akun.getNama())) {
                ada = true;
                pnlStatus.add(createHistoryCard(p));
                pnlStatus.add(Box.createVerticalStrut(10));
            }
        }
        if (!ada)
            pnlStatus.add(new JLabel("Belum ada riwayat."));
        pnlStatus.revalidate();
        pnlStatus.repaint();
    }

    // createHistoryCard sama seperti sebelumnya, hanya ambil p.getCatatan() jika
    // mau ditampilkan
    private JPanel createHistoryCard(Pesanan p) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new LineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        String txt = "<html><b>Order #" + p.getId() + "</b> (Meja " + p.getMeja().getNomor() + ")<br>"
                + "Status: <font color='blue'>" + p.getStatus() + "</font> | Total: Rp " + (int) p.getTotal()
                + "</html>";
        card.add(new JLabel(txt), BorderLayout.CENTER);

        if ("LUNAS".equals(p.getStatus())) {
            JButton btn = new JButton("Struk");
            btn.addActionListener(e -> showStruk(p));
            card.add(btn, BorderLayout.EAST);
        } else if ("DISAJIKAN".equals(p.getStatus())) {
            JButton btn = new JButton("Bayar");
            btn.addActionListener(
                    e -> JOptionPane.showMessageDialog(this, "Silakan ke kasir untuk bayar Order #" + p.getId()));
            card.add(btn, BorderLayout.EAST);
        }
        return card;
    }

    private void showStruk(Pesanan p) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== STRUK ===\nOrder: #").append(p.getId()).append("\nPelanggan: ").append(p.getNamaPelanggan())
                .append("\n");
        if (p.getCatatan() != null)
            sb.append("Catatan: ").append(p.getCatatan()).append("\n");
        sb.append("----------------\n");
        for (DetailPesanan dp : p.getItems())
            sb.append(dp.toString()).append("\n");
        sb.append("----------------\nTOTAL: Rp ").append((int) p.getTotal());
        JOptionPane.showMessageDialog(this, new JScrollPane(new JTextArea(sb.toString())));
    }

    private JPanel createHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(new Color(59, 130, 246));
        h.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel l = new JLabel("Halo, " + akun.getNama());
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton out = new JButton("Logout");
        out.addActionListener(e -> {
            refreshTimer.stop();
            dispose();
            new LoginGUI().setVisible(true);
        });
        h.add(l, BorderLayout.WEST);
        h.add(out, BorderLayout.EAST);
        return h;
    }
}