package com.restaurant.gui;

import com.restaurant.model.akun.Akun;
import com.restaurant.model.menu.MenuItem;
import com.restaurant.model.pesanan.DetailPesanan;
import com.restaurant.model.pesanan.Meja;
import com.restaurant.model.pesanan.Pesanan;
import com.restaurant.service.RestaurantSystem;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class CustomerGUI extends JFrame {

    private final RestaurantSystem sys = RestaurantSystem.getInstance();
    private final Akun akun;

    // --- WARNA TEMA ---
    private final Color BG_COLOR = new Color(248, 250, 252);
    private final Color HEADER_COLOR = new Color(59, 130, 246);
    private final Color CARD_BG = Color.WHITE;
    private final Color BTN_ADD_COLOR = new Color(37, 99, 235); // Biru lebih gelap untuk tombol

    // Components
    private JPanel pnlMejaKosong;
    private JTextField tfNomorMeja;
    private JTextArea taCatatanPesanan;
    private DefaultListModel<String> cartModel;
    private final List<DetailPesanan> keranjang = new ArrayList<>();
    private JLabel lblTotalCart;
    private JPanel pnlStatus;
    private final Timer refreshTimer;

    public CustomerGUI(Akun akun) {
        this.akun = akun;
        setTitle("Customer Dashboard - " + akun.getNama());
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG_COLOR);

        tabbedPane.addTab("Buat Pesanan", createOrderPanel());
        tabbedPane.addTab("Riwayat & Struk", createHistoryPanel());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);

        refreshTimer = new Timer(3000, e -> {
            refreshHistory();
            refreshMejaKosong();
        });
        refreshTimer.start();
        refreshMejaKosong();
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BG_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // --- KIRI: MENU ---
        JPanel menuContainer = new JPanel(new GridLayout(0, 3, 10, 10));
        menuContainer.setBackground(BG_COLOR);
        List<MenuItem> menuList = sys.getMenuList();
        for (MenuItem item : menuList) {
            menuContainer.add(createMenuItemCard(item));
        }
        JScrollPane scrollMenu = new JScrollPane(menuContainer);
        scrollMenu.setBorder(BorderFactory.createTitledBorder(null, "Pilih Menu", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Segoe UI", Font.BOLD, 14), Color.BLACK));
        scrollMenu.setBackground(BG_COLOR);
        scrollMenu.getViewport().setBackground(BG_COLOR);
        panel.add(scrollMenu, BorderLayout.CENTER);

        // --- KANAN: PANEL INPUT & KERANJANG ---
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(BG_COLOR);
        rightPanel.setPreferredSize(new Dimension(360, 0));

        // 1. Panel Info & Input Meja
        JPanel pnlInfo = new JPanel(new BorderLayout());
        pnlInfo.setBackground(CARD_BG);
        pnlInfo.setBorder(new CompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new TitledBorder(null, "Informasi Meja", TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION, new Font("Segoe UI", Font.BOLD, 12), Color.BLACK)));

        // Tampilan Meja Kosong
        pnlMejaKosong = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlMejaKosong.setBackground(CARD_BG);
        pnlMejaKosong.setPreferredSize(new Dimension(320, 120));

        JScrollPane scrollMeja = new JScrollPane(pnlMejaKosong);
        scrollMeja.setBorder(null);
        scrollMeja.getViewport().setBackground(CARD_BG);

        // Input Meja
        JPanel pnlInputMeja = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlInputMeja.setBackground(CARD_BG);
        JLabel lblPilih = new JLabel("Pilih Nomor Meja: ");
        lblPilih.setForeground(Color.BLACK);
        pnlInputMeja.add(lblPilih);

        tfNomorMeja = new JTextField(5);
        tfNomorMeja.setForeground(Color.WHITE);
        tfNomorMeja.setCaretColor(Color.WHITE);
        pnlInputMeja.add(tfNomorMeja);

        JPanel pnlTopRight = new JPanel(new BorderLayout());
        pnlTopRight.setBackground(CARD_BG);
        JLabel lblAvail = new JLabel("  Meja Tersedia (Hijau):");
        lblAvail.setForeground(Color.BLACK);
        pnlTopRight.add(lblAvail, BorderLayout.NORTH);
        pnlTopRight.add(scrollMeja, BorderLayout.CENTER);
        pnlTopRight.add(pnlInputMeja, BorderLayout.SOUTH);

        pnlInfo.add(pnlTopRight, BorderLayout.CENTER);
        rightPanel.add(pnlInfo, BorderLayout.NORTH);

        // 2. Keranjang
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setBackground(CARD_BG);
        cartPanel.setBorder(new TitledBorder(null, "Keranjang Pesanan", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Segoe UI", Font.BOLD, 12), Color.BLACK));

        cartModel = new DefaultListModel<>();
        JList<String> listCart = new JList<>(cartModel);
        listCart.setBackground(Color.WHITE);
        listCart.setForeground(Color.BLACK);
        cartPanel.add(new JScrollPane(listCart), BorderLayout.CENTER);
        rightPanel.add(cartPanel, BorderLayout.CENTER);

        // 3. Catatan & Total
        JPanel pnlCheckout = new JPanel(new BorderLayout(5, 5));
        pnlCheckout.setBackground(BG_COLOR);

        JLabel lblCatatan = new JLabel("Catatan Tambahan (Opsional):");
        lblCatatan.setForeground(Color.BLACK);
        pnlCheckout.add(lblCatatan, BorderLayout.NORTH);

        taCatatanPesanan = new JTextArea(3, 20);
        taCatatanPesanan.setBackground(Color.WHITE);
        taCatatanPesanan.setForeground(Color.BLACK);
        taCatatanPesanan.setCaretColor(Color.BLACK);
        taCatatanPesanan.setBorder(new LineBorder(Color.LIGHT_GRAY));
        pnlCheckout.add(new JScrollPane(taCatatanPesanan), BorderLayout.CENTER);

        // Total & Tombol
        JPanel pnlAction = new JPanel(new BorderLayout());
        pnlAction.setBackground(BG_COLOR);

        lblTotalCart = new JLabel("Total: Rp 0", SwingConstants.CENTER);
        lblTotalCart.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalCart.setForeground(new Color(22, 163, 74));
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
        pnlBtns.setBackground(BG_COLOR);
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
            lbl.setForeground(new Color(22, 163, 74)); // HIJAU
            lbl.setFont(new Font("Monospaced", Font.BOLD, 14));
            lbl.setBorder(new EmptyBorder(2, 2, 2, 2));
            pnlMejaKosong.add(lbl);
        }
        pnlMejaKosong.revalidate();
        pnlMejaKosong.repaint();
    }

    // --- FIX KETERBACAAN MENU ---
    private JPanel createMenuItemCard(MenuItem item) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        card.setBackground(CARD_BG);
        card.setPreferredSize(new Dimension(150, 90));

        JLabel lblNama = new JLabel("<html><center>" + item.getNama() + "</center></html>", SwingConstants.CENTER);
        lblNama.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNama.setForeground(Color.BLACK); // Nama Hitam

        JLabel lblHarga = new JLabel("Rp " + (int) item.getHarga(), SwingConstants.CENTER);
        lblHarga.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHarga.setForeground(new Color(80, 80, 80)); // Harga Abu Gelap

        // TOMBOL DIBUAT KONTRAS (BIRU on PUTIH)
        JButton btnAdd = new JButton("Tambah");
        btnAdd.setBackground(BTN_ADD_COLOR);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdd.setOpaque(true);
        btnAdd.setBorderPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> tambahItemDialog(item));

        card.add(lblNama, BorderLayout.NORTH);
        card.add(lblHarga, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.SOUTH);
        return card;
    }

    private void tambahItemDialog(MenuItem item) {
        String qtyStr = JOptionPane.showInputDialog(this, "Masukkan Jumlah untuk " + item.getNama() + ":", "1");
        if (qtyStr != null && !qtyStr.isEmpty()) {
            try {
                int qty = Integer.parseInt(qtyStr);
                if (qty > 0) {
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
            boolean valid = sys.getMejaKosong().stream().anyMatch(m -> m.getNomor() == meja);
            if (!valid) {
                JOptionPane.showMessageDialog(this,
                        "Meja " + meja + " sedang dipakai atau tidak ada. Pilih meja lain.");
                return;
            }

            Pesanan p = sys.buatPesananKosong(meja, akun.getNama());
            for (DetailPesanan dp : keranjang)
                p.tambahItem(dp);

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

    private JComponent createHistoryPanel() {
        pnlStatus = new JPanel();
        pnlStatus.setLayout(new BoxLayout(pnlStatus, BoxLayout.Y_AXIS));
        pnlStatus.setBackground(BG_COLOR);

        JScrollPane sp = new JScrollPane(pnlStatus);
        sp.setBorder(null);
        sp.getViewport().setBackground(BG_COLOR);
        return sp;
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
        if (!ada) {
            JLabel lbl = new JLabel("Belum ada riwayat.");
            lbl.setForeground(Color.GRAY);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            pnlStatus.add(lbl);
        }
        pnlStatus.revalidate();
        pnlStatus.repaint();
    }

    private JPanel createHistoryCard(Pesanan p) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new LineBorder(Color.LIGHT_GRAY));
        card.setBackground(CARD_BG);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        String txt = "<html><b style='color:black;'>Order #" + p.getId() + "</b> (Meja " + p.getMeja().getNomor()
                + ")<br>"
                + "Status: <font color='blue'>" + p.getStatus() + "</font> | Total: Rp " + (int) p.getTotal()
                + "</html>";
        card.add(new JLabel(txt), BorderLayout.CENTER);

        if ("LUNAS".equals(p.getStatus())) {
            JButton btn = new JButton("Struk");
            btn.setBackground(HEADER_COLOR);
            btn.setForeground(Color.WHITE);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
            btn.addActionListener(e -> showStruk(p));
            card.add(btn, BorderLayout.EAST);
        } else if ("DISAJIKAN".equals(p.getStatus())) {
            JButton btn = new JButton("Bayar");
            btn.setBackground(Color.ORANGE);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
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
        h.setBackground(HEADER_COLOR);
        h.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel l = new JLabel("Halo, " + akun.getNama());
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton out = new JButton("Logout");
        out.setBackground(new Color(220, 53, 69));
        out.setForeground(Color.WHITE);
        out.setOpaque(true);
        out.setBorderPainted(false);
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