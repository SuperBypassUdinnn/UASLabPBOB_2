package com.restaurant.gui;

import com.restaurant.model.akun.Akun;
import com.restaurant.model.pesanan.DetailPesanan;
import com.restaurant.model.pesanan.Pesanan;
import com.restaurant.service.RestaurantSystem;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class PelayanGUI extends JFrame {

    private final RestaurantSystem sys = RestaurantSystem.getInstance();
    private JPanel pnlPesananMasuk;
    private JPanel pnlSiapSaji;
    private final Timer refreshTimer;

    // --- WARNA TEMA LIGHT ---
    private final Color BG_COLOR = new Color(248, 250, 252);
    private final Color HEADER_COLOR = new Color(59, 130, 246);
    private final Color CARD_BG = Color.WHITE;

    public PelayanGUI(Akun akun) {
        setTitle("Dashboard Pelayan - " + akun.getNama());
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        mainPanel.add(createHeader(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG_COLOR);

        // Tab 1: Terima Pesanan
        tabbedPane.addTab("Pesanan Masuk (Menunggu)", createIncomingPanel());

        // Tab 2: Antar Pesanan
        tabbedPane.addTab("Siap Disajikan (Dari Dapur)", createServePanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(mainPanel);

        refreshTimer = new Timer(3000, e -> refreshLists());
        refreshTimer.start();
        refreshLists();
    }

    private JComponent createIncomingPanel() {
        pnlPesananMasuk = new JPanel();
        pnlPesananMasuk.setLayout(new BoxLayout(pnlPesananMasuk, BoxLayout.Y_AXIS));
        pnlPesananMasuk.setBackground(BG_COLOR);

        JScrollPane sp = new JScrollPane(pnlPesananMasuk);
        sp.setBorder(null);
        sp.getViewport().setBackground(BG_COLOR);
        return sp;
    }

    private JComponent createServePanel() {
        pnlSiapSaji = new JPanel();
        pnlSiapSaji.setLayout(new BoxLayout(pnlSiapSaji, BoxLayout.Y_AXIS));
        pnlSiapSaji.setBackground(BG_COLOR);

        JScrollPane sp = new JScrollPane(pnlSiapSaji);
        sp.setBorder(null);
        sp.getViewport().setBackground(BG_COLOR);
        return sp;
    }

    private void refreshLists() {
        sys.refreshPesananFromFile();
        List<Pesanan> all = sys.getDaftarPesanan();

        pnlPesananMasuk.removeAll();
        pnlSiapSaji.removeAll();

        for (Pesanan p : all) {
            // TAB 1: Pesanan dari Customer (Status: MENUNGGU)
            if ("MENUNGGU".equals(p.getStatus())) {
                pnlPesananMasuk.add(createCard(p, "Kirim ke Dapur", "DIPROSES"));
                pnlPesananMasuk.add(Box.createVerticalStrut(10));
            }
            // TAB 2: Pesanan dari Koki (Status: SIAP DISAJIKAN)
            else if ("SIAP DISAJIKAN".equals(p.getStatus())) {
                pnlSiapSaji.add(createCard(p, "Sajikan ke Meja", "DISAJIKAN"));
                pnlSiapSaji.add(Box.createVerticalStrut(10));
            }
        }

        // Pesan kosong jika tidak ada data
        if (pnlPesananMasuk.getComponentCount() == 0) {
            JLabel lbl = new JLabel("Tidak ada pesanan baru.");
            lbl.setForeground(Color.GRAY);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            pnlPesananMasuk.add(lbl);
        }
        if (pnlSiapSaji.getComponentCount() == 0) {
            JLabel lbl = new JLabel("Tidak ada pesanan siap saji.");
            lbl.setForeground(Color.GRAY);
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            pnlSiapSaji.add(lbl);
        }

        pnlPesananMasuk.revalidate();
        pnlPesananMasuk.repaint();
        pnlSiapSaji.revalidate();
        pnlSiapSaji.repaint();
    }

    private JPanel createCard(Pesanan p, String btnText, String nextStatus) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(10, 10, 10, 10)));
        card.setBackground(CARD_BG);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Info Pesanan
        String info = "<html><b style='color:black;'>Meja " + p.getMeja().getNomor() + "</b> (Order #" + p.getId()
                + ")<br>"
                + "<span style='color:gray;'>Pelanggan: " + p.getNamaPelanggan() + "</span></html>";
        JLabel lblInfo = new JLabel(info);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Detail Menu
        StringBuilder detail = new StringBuilder("<html><font color='gray'>");
        for (DetailPesanan dp : p.getItems()) {
            detail.append("- ").append(dp.getMenu().getNama()).append(" x").append(dp.getJumlah()).append("<br>");
        }
        detail.append("</font></html>");
        JLabel lblDetail = new JLabel(detail.toString());

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(CARD_BG);
        left.add(lblInfo, BorderLayout.NORTH);
        left.add(lblDetail, BorderLayout.CENTER);

        // Tombol Aksi
        JButton btnAction = new JButton(btnText);
        btnAction.setBackground(HEADER_COLOR);
        btnAction.setForeground(Color.WHITE);
        btnAction.setOpaque(true);
        btnAction.setBorderPainted(false);

        btnAction.addActionListener(e -> {
            sys.updateStatusPesanan(p.getId(), nextStatus);
            JOptionPane.showMessageDialog(this, "Status diupdate: " + nextStatus);
            refreshLists();
        });

        card.add(left, BorderLayout.CENTER);
        card.add(btnAction, BorderLayout.EAST);
        return card;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_COLOR);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("Pelayan Dashboard");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setOpaque(true);
        btnLogout.setBorderPainted(false);
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