package com.restaurant.gui;

import com.restaurant.model.akun.Akun;
import com.restaurant.model.menu.MenuItem;
import com.restaurant.model.pesanan.DetailPesanan;
import com.restaurant.model.pesanan.Pesanan;
import com.restaurant.service.RestaurantSystem;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;
import javax.swing.Timer;

public class PelayanGUI extends JFrame {

    private RestaurantSystem sys = RestaurantSystem.getInstance();

    // Komponen Tab Input
    private JTextField tfMeja;
    private JComboBox<String> cbMenu;
    private JSpinner spinJumlah;
    private JTextField tfCatatan;
    private DefaultListModel<String> cartModel;
    private java.util.List<DetailPesanan> tempItems = new java.util.ArrayList<>();

    // Komponen Tab Sajikan
    private JPanel pnlSiapSaji;
    private Timer refreshTimer;

    public PelayanGUI(Akun akun) {
        setTitle("Dashboard Pelayan - " + akun.getNama());
        setSize(800, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Header
        add(createHeader(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Input Order (Kode lama dipindahkan ke method createInputPanel)
        tabbedPane.addTab("Input Pesanan Baru", createInputPanel());

        // Tab 2: Antar Pesanan (Fitur Baru untuk replace CLI Sajikan Pesanan)
        tabbedPane.addTab("Antar Pesanan (Siap Saji)", createServePanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Auto Refresh untuk Tab Antar Pesanan
        refreshTimer = new Timer(3000, e -> refreshServeList());
        refreshTimer.start();
        refreshServeList();
    }

    // PANEL 1: INPUT PESANAN (Logika Lama)
    private JPanel createInputPanel() {
        JPanel content = new JPanel(new BorderLayout(15, 15));
        content.setBorder(new EmptyBorder(20, 20, 20, 20));
        content.setBackground(new Color(248, 250, 252));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nomor Meja:"), gbc);
        tfMeja = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        formPanel.add(tfMeja, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Menu:"), gbc);
        cbMenu = new JComboBox<>();
        // Load Menu
        if (sys.getMenuList() != null) {
            for (MenuItem m : sys.getMenuList())
                cbMenu.addItem(m.getNama());
        }
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        formPanel.add(cbMenu, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Jumlah:"), gbc);
        spinJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        formPanel.add(spinJumlah, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Catatan:"), gbc);
        tfCatatan = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        formPanel.add(tfCatatan, gbc);

        JButton btnAdd = new JButton("Tambah Item");
        btnAdd.setBackground(new Color(59, 130, 246));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> tambahKeKeranjang());
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(btnAdd, gbc);

        content.add(formPanel, BorderLayout.NORTH);

        cartModel = new DefaultListModel<>();
        JList<String> cartList = new JList<>(cartModel);
        cartList.setBorder(new TitledBorder("Keranjang Sementara"));
        content.add(new JScrollPane(cartList), BorderLayout.CENTER);

        JButton btnKirim = new JButton("KIRIM KE DAPUR");
        btnKirim.setBackground(new Color(34, 197, 94));
        btnKirim.setForeground(Color.WHITE);
        btnKirim.setPreferredSize(new Dimension(100, 50));
        btnKirim.addActionListener(e -> kirimPesanan());

        content.add(btnKirim, BorderLayout.SOUTH);
        return content;
    }

    // PANEL 2: SERVE ORDER (Logika Baru Integrasi CLI)
    private JComponent createServePanel() {
        pnlSiapSaji = new JPanel();
        pnlSiapSaji.setLayout(new BoxLayout(pnlSiapSaji, BoxLayout.Y_AXIS));
        pnlSiapSaji.setBackground(new Color(248, 250, 252));
        return new JScrollPane(pnlSiapSaji);
    }

    private void refreshServeList() {
        pnlSiapSaji.removeAll();
        sys.refreshPesananFromFile();
        List<Pesanan> all = sys.getDaftarPesanan();

        boolean adaData = false;
        for (Pesanan p : all) {
            // Pelayan hanya melihat pesanan yg "SIAP DISAJIKAN" (Output Koki)
            if ("SIAP DISAJIKAN".equals(p.getStatus())) {
                adaData = true;
                JPanel card = new JPanel(new BorderLayout());
                card.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(10, 10, 10, 10)));
                card.setBackground(Color.WHITE);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

                JLabel lblInfo = new JLabel(
                        "<html><b>Meja " + p.getMeja().getNomor() + "</b> - Pesanan #" + p.getId() + "</html>");
                lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

                JButton btnSajikan = new JButton("Sajikan ke Meja");
                btnSajikan.setBackground(new Color(34, 197, 94));
                btnSajikan.setForeground(Color.WHITE);

                // Action: Ubah status jadi DISAJIKAN (Agar muncul di Kasir)
                btnSajikan.addActionListener(e -> {
                    sys.updateStatusPesanan(p.getId(), "DISAJIKAN");
                    JOptionPane.showMessageDialog(this, "Pesanan disajikan ke Meja " + p.getMeja().getNomor());
                    refreshServeList();
                });

                card.add(lblInfo, BorderLayout.CENTER);
                card.add(btnSajikan, BorderLayout.EAST);

                pnlSiapSaji.add(card);
                pnlSiapSaji.add(Box.createVerticalStrut(10));
            }
        }

        if (!adaData) {
            JLabel empty = new JLabel("Tidak ada pesanan yang perlu diantar.");
            empty.setAlignmentX(CENTER_ALIGNMENT);
            pnlSiapSaji.add(empty);
        }

        pnlSiapSaji.revalidate();
        pnlSiapSaji.repaint();
    }

    // --- Helper Methods untuk Input ---
    private void tambahKeKeranjang() {
        String nama = (String) cbMenu.getSelectedItem();
        if (nama == null)
            return;
        int qty = (int) spinJumlah.getValue();
        String cat = tfCatatan.getText();

        MenuItem item = null;
        for (MenuItem m : sys.getMenuList())
            if (m.getNama().equals(nama))
                item = m;

        if (item != null) {
            tempItems.add(new DetailPesanan(item, qty, cat));
            cartModel.addElement(nama + " x" + qty);
            spinJumlah.setValue(1);
            tfCatatan.setText("");
        }
    }

    private void kirimPesanan() {
        if (tfMeja.getText().isEmpty() || tempItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Data belum lengkap!");
            return;
        }
        try {
            int meja = Integer.parseInt(tfMeja.getText());
            Pesanan p = sys.buatPesananKosong(meja);
            for (DetailPesanan dp : tempItems)
                p.tambahItem(dp);

            // Jika pelayan input, status awal DIPROSES (langsung ke koki)
            // atau MENUNGGU jika ingin konfirmasi dulu. Sesuai CLI lama: DIPROSES
            p.setStatus("DIPROSES");
            sys.saveData();

            JOptionPane.showMessageDialog(this, "Terkirim ke Dapur!");
            tempItems.clear();
            cartModel.clear();
            tfMeja.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Meja harus angka!");
        }
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(59, 130, 246));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("Pelayan Dashboard");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(Color.RED);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> {
            refreshTimer.stop();
            dispose();
            new LoginGUI().setVisible(true);
        });

        header.add(lblTitle, BorderLayout.WEST);
        header.add(btnLogout, BorderLayout.EAST);
        return header;
    }
}