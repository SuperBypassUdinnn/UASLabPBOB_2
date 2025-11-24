package com.restaurant.gui;

import com.restaurant.model.akun.Akun;
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
    private JPanel pnlPesananMasuk;
    private JPanel pnlSiapSaji;
    private Timer refreshTimer;

    public PelayanGUI(Akun akun) {
        setTitle("Dashboard Pelayan - " + akun.getNama());
        setSize(900, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        add(createHeader(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Terima Pesanan (Dari Customer -> Kirim ke Dapur)
        tabbedPane.addTab("Pesanan Masuk (Menunggu)", createIncomingPanel());

        // Tab 2: Antar Pesanan (Dari Dapur -> Ke Meja)
        tabbedPane.addTab("Siap Disajikan (Dari Dapur)", createServePanel());

        add(tabbedPane, BorderLayout.CENTER);

        refreshTimer = new Timer(3000, e -> refreshLists());
        refreshTimer.start();
        refreshLists();
    }

    private JComponent createIncomingPanel() {
        pnlPesananMasuk = new JPanel();
        pnlPesananMasuk.setLayout(new BoxLayout(pnlPesananMasuk, BoxLayout.Y_AXIS));
        return new JScrollPane(pnlPesananMasuk);
    }

    private JComponent createServePanel() {
        pnlSiapSaji = new JPanel();
        pnlSiapSaji.setLayout(new BoxLayout(pnlSiapSaji, BoxLayout.Y_AXIS));
        return new JScrollPane(pnlSiapSaji);
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

        pnlPesananMasuk.revalidate();
        pnlPesananMasuk.repaint();
        pnlSiapSaji.revalidate();
        pnlSiapSaji.repaint();
    }

    private JPanel createCard(Pesanan p, String btnText, String nextStatus) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new CompoundBorder(new LineBorder(Color.LIGHT_GRAY), new EmptyBorder(10, 10, 10, 10)));
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Info Pesanan
        String info = "<html><b>Meja " + p.getMeja().getNomor() + "</b> (Order #" + p.getId() + ")<br>"
                + "Pelanggan: " + p.getNamaPelanggan() + "</html>";
        JLabel lblInfo = new JLabel(info);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Detail Menu (Tooltip / Text)
        StringBuilder detail = new StringBuilder("<html><font color='gray'>");
        for (DetailPesanan dp : p.getItems()) {
            detail.append("- ").append(dp.getMenu().getNama()).append(" x").append(dp.getJumlah()).append("<br>");
        }
        detail.append("</font></html>");
        JLabel lblDetail = new JLabel(detail.toString());

        JPanel left = new JPanel(new BorderLayout());
        left.setBackground(Color.WHITE);
        left.add(lblInfo, BorderLayout.NORTH);
        left.add(lblDetail, BorderLayout.CENTER);

        // Tombol Aksi
        JButton btnAction = new JButton(btnText);
        btnAction.setBackground(new Color(37, 99, 235));
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
        header.setBackground(new Color(59, 130, 246));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("Pelayan Dashboard");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton btnLogout = new JButton("Logout");
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