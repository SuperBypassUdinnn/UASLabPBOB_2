package com.restaurant.gui;

import com.restaurant.model.akun.Akun;
import com.restaurant.model.menu.MenuItem;
import com.restaurant.model.pesanan.DetailPesanan;
import com.restaurant.model.pesanan.Pesanan;
import com.restaurant.service.RestaurantSystem;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

public class CustomerGUI extends JFrame {

    private RestaurantSystem sys = RestaurantSystem.getInstance();
    private Akun akun;
    private int nomorMeja = -1;

    private JPanel pnlStatus;
    private DefaultListModel<String> cartModel;
    private List<DetailPesanan> keranjang = new ArrayList<>();
    private JLabel lblTotalCart;
    private Timer refreshTimer;

    public CustomerGUI(Akun akun) {
        this.akun = akun;

        pilihMejaDialog();
        if (nomorMeja == -1) {
            dispose();
            return;
        }

        setTitle("Customer Dashboard - Meja " + nomorMeja + " (" + akun.getNama() + ")");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Menu & Pesan
        tabbedPane.addTab("Buat Pesanan", createOrderPanel());

        // Tab 2: Riwayat & Struk
        tabbedPane.addTab("Riwayat & Struk", createHistoryPanel());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        setContentPane(mainPanel);

        refreshTimer = new Timer(3000, e -> refreshHistory());
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

        // KIRI: Daftar Menu
        JPanel menuContainer = new JPanel(new GridLayout(0, 2, 10, 10));
        List<MenuItem> menuList = sys.getMenuList();
        for (MenuItem item : menuList) {
            menuContainer.add(createMenuItemCard(item));
        }
        JScrollPane scrollMenu = new JScrollPane(menuContainer);
        scrollMenu.setBorder(BorderFactory.createTitledBorder("Daftar Menu"));

        // KANAN: Keranjang
        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.setPreferredSize(new Dimension(320, 0));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Keranjang Anda"));

        cartModel = new DefaultListModel<>();
        JList<String> listCart = new JList<>(cartModel);
        cartPanel.add(new JScrollPane(listCart), BorderLayout.CENTER);

        JPanel checkoutPanel = new JPanel(new BorderLayout());
        lblTotalCart = new JLabel("Total: Rp 0");
        lblTotalCart.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalCart.setHorizontalAlignment(SwingConstants.CENTER);
        lblTotalCart.setBorder(new EmptyBorder(10, 5, 10, 5));

        JButton btnReset = new JButton("Reset Keranjang");
        btnReset.setBackground(new Color(220, 53, 69));
        btnReset.setForeground(Color.WHITE);
        btnReset.addActionListener(e -> resetKeranjang());

        JButton btnPesan = new JButton("BUAT PESANAN SEKARANG");
        btnPesan.setBackground(new Color(34, 197, 94));
        btnPesan.setForeground(Color.WHITE);
        btnPesan.setFont(new Font("Arial", Font.BOLD, 14));
        btnPesan.setPreferredSize(new Dimension(100, 50));
        // Fix tombol Mac
        btnPesan.setOpaque(true);
        btnPesan.setBorderPainted(false);

        btnPesan.addActionListener(e -> prosesCheckout());

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        btnPanel.add(btnReset);
        btnPanel.add(btnPesan);

        checkoutPanel.add(lblTotalCart, BorderLayout.NORTH);
        checkoutPanel.add(btnPanel, BorderLayout.SOUTH);
        cartPanel.add(checkoutPanel, BorderLayout.SOUTH);

        panel.add(scrollMenu, BorderLayout.CENTER);
        panel.add(cartPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createMenuItemCard(MenuItem item) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setBackground(Color.WHITE);

        JLabel lblNama = new JLabel("<html><b>" + item.getNama() + "</b></html>");
        lblNama.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblHarga = new JLabel("Rp " + (int) item.getHarga());
        lblHarga.setForeground(new Color(22, 163, 74));

        JButton btnAdd = new JButton("+ Add");
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> dialogTambahItem(item));

        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setBackground(Color.WHITE);
        info.add(lblNama);
        info.add(lblHarga);
        info.setBorder(new EmptyBorder(5, 5, 5, 5));

        card.add(info, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.EAST);
        return card;
    }

    // --- FIX: Dialog Input Jumlah & Merge Logic ---
    private void dialogTambahItem(MenuItem item) {
        // Panel input custom
        JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
        JSpinner spinQty = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
        JTextField tfCatatan = new JTextField();

        p.add(new JLabel("Jumlah:"));
        p.add(spinQty);
        p.add(new JLabel("Catatan:"));
        p.add(tfCatatan);

        int result = JOptionPane.showConfirmDialog(this, p, "Tambah " + item.getNama(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // FIX 1: Tombol Cancel sekarang berfungsi (tidak lanjut jika != OK_OPTION)
        if (result == JOptionPane.OK_OPTION) {
            int qty = (int) spinQty.getValue();
            String cat = tfCatatan.getText();

            // FIX 2: Merge item jika menu sama & catatan sama
            boolean merged = false;
            for (DetailPesanan existing : keranjang) {
                if (existing.getMenu().getNama().equals(item.getNama()) &&
                        existing.getCatatan().equalsIgnoreCase(cat)) {

                    existing.setJumlah(existing.getJumlah() + qty);
                    merged = true;
                    break;
                }
            }

            if (!merged) {
                keranjang.add(new DetailPesanan(item, qty, cat));
            }
            updateCartDisplay();
        }
    }

    private void updateCartDisplay() {
        cartModel.clear();
        double total = 0;
        for (DetailPesanan dp : keranjang) {
            String line = dp.getMenu().getNama() + " x" + dp.getJumlah();
            if (!dp.getCatatan().isEmpty())
                line += " (" + dp.getCatatan() + ")";
            line += " - Rp" + (int) dp.getSubtotal();

            cartModel.addElement(line);
            total += dp.getSubtotal();
        }
        lblTotalCart.setText("Total: Rp " + (int) total);
    }

    private void resetKeranjang() {
        keranjang.clear();
        updateCartDisplay();
    }

    private void prosesCheckout() {
        if (keranjang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Keranjang kosong!");
            return;
        }

        // Buat pesanan dengan Nama Pelanggan
        Pesanan p = sys.buatPesananKosong(nomorMeja, akun.getNama());
        for (DetailPesanan dp : keranjang) {
            p.tambahItem(dp);
        }
        // Status awal MENUNGGU (masuk ke Pelayan)
        p.setStatus("MENUNGGU");
        sys.saveData();

        JOptionPane.showMessageDialog(this, "Pesanan Terkirim! Silakan cek status di tab Riwayat.");
        resetKeranjang();
        refreshHistory();
    }

    // --- TAB 2: HISTORY & STRUK ---
    private JComponent createHistoryPanel() {
        pnlStatus = new JPanel();
        pnlStatus.setLayout(new BoxLayout(pnlStatus, BoxLayout.Y_AXIS));
        pnlStatus.setBackground(new Color(248, 250, 252));
        return new JScrollPane(pnlStatus);
    }

    private void refreshHistory() {
        pnlStatus.removeAll();
        sys.refreshPesananFromFile();
        List<Pesanan> myOrders = sys.getDaftarPesanan(); // Ambil semua dulu

        boolean ada = false;
        for (Pesanan p : myOrders) {
            // Filter Privasi: Hanya tampilkan pesanan milik user ini
            if (p.getNamaPelanggan().equalsIgnoreCase(akun.getNama())) {
                ada = true;
                pnlStatus.add(createHistoryCard(p));
                pnlStatus.add(Box.createVerticalStrut(10));
            }
        }

        if (!ada) {
            JLabel empty = new JLabel("Belum ada riwayat pesanan.");
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            pnlStatus.add(empty);
        }
        pnlStatus.revalidate();
        pnlStatus.repaint();
    }

    private JPanel createHistoryCard(Pesanan p) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY, 1), new EmptyBorder(10, 10, 10, 10)));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.setBackground(Color.WHITE);
        JLabel lblId = new JLabel("Order #" + p.getId() + " (Meja " + p.getMeja().getNomor() + ")");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblStatus = new JLabel("Status: " + p.getStatus());
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(getStatusColor(p.getStatus()));

        left.add(lblId);
        left.add(lblStatus);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(Color.WHITE);

        // Tombol Bayar muncul jika sudah Disajikan
        if ("DISAJIKAN".equals(p.getStatus())) {
            JButton btnBayar = new JButton("Bayar Sekarang");
            btnBayar.setBackground(Color.ORANGE);
            btnBayar.addActionListener(e -> showPaymentDialog(p));
            right.add(btnBayar);
        }
        // Tombol Struk muncul jika sudah Lunas
        else if ("LUNAS".equals(p.getStatus())) {
            JButton btnStruk = new JButton("Lihat Struk");
            btnStruk.setBackground(new Color(59, 130, 246));
            btnStruk.setForeground(Color.WHITE);
            btnStruk.addActionListener(e -> showStruk(p));
            right.add(btnStruk);
        } else {
            JLabel lblTot = new JLabel("Total: Rp " + (int) p.getTotal());
            right.add(lblTot);
        }

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);
        return card;
    }

    private void showPaymentDialog(Pesanan p) {
        String[] options = { "Cash", "QRIS" };
        int choice = JOptionPane.showOptionDialog(this,
                "Total Tagihan: Rp " + (int) p.getTotal() + "\nPilih metode pembayaran:",
                "Pembayaran", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);

        if (choice == 0) { // Cash
            JOptionPane.showMessageDialog(this,
                    "Silakan menuju Kasir untuk melakukan pembayaran tunai.\nSebutkan Order ID #" + p.getId());
        } else if (choice == 1) { // QRIS
            JOptionPane.showMessageDialog(this,
                    "Tunjukkan QRIS ini ke Kasir untuk konfirmasi.\n(Simulasi: Status akan diupdate oleh kasir)");
        }
    }

    private void showStruk(Pesanan p) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== STRUK PEMBAYARAN ==========\n");
        sb.append("Order ID  : ").append(p.getId()).append("\n");
        sb.append("Pelanggan : ").append(p.getNamaPelanggan()).append("\n");
        sb.append("--------------------------------------\n");
        for (DetailPesanan dp : p.getItems()) {
            sb.append(dp.getMenu().getNama()).append(" x").append(dp.getJumlah())
                    .append(" = ").append((int) dp.getSubtotal()).append("\n");
        }
        sb.append("--------------------------------------\n");
        sb.append("TOTAL     : Rp ").append((int) p.getTotal()).append("\n");
        sb.append("Status    : LUNAS\n");
        sb.append("======================================\n");

        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Struk Digital", JOptionPane.PLAIN_MESSAGE);
    }

    private Color getStatusColor(String s) {
        if (s.equals("MENUNGGU"))
            return Color.RED;
        if (s.equals("DIPROSES"))
            return Color.ORANGE;
        if (s.equals("SIAP DISAJIKAN"))
            return Color.MAGENTA;
        if (s.equals("DISAJIKAN"))
            return Color.BLUE;
        if (s.equals("LUNAS"))
            return new Color(34, 197, 94);
        return Color.BLACK;
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