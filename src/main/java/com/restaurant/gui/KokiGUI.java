package com.restaurant.gui;

import com.restaurant.model.akun.Akun;
import com.restaurant.model.pesanan.DetailPesanan;
import com.restaurant.model.pesanan.Pesanan;
import com.restaurant.service.RestaurantSystem;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.*;

public class KokiGUI extends JFrame {

    private final JPanel listContainer;
    private final RestaurantSystem sys = RestaurantSystem.getInstance();
    private final Timer refreshTimer;

    // --- WARNA ---
    private final Color THEME_BLUE = new Color(59, 130, 246);
    private final Color BG_COLOR = new Color(248, 250, 252);
    private final Color CARD_BG = Color.WHITE;

    // Status Colors
    private final Color COLOR_BTN_COOK = new Color(234, 179, 8); // Kuning (Mulai Masak)
    private final Color COLOR_BTN_DONE = new Color(34, 197, 94); // Hijau (Selesai)
    private final Color COLOR_DISABLED = new Color(209, 213, 219); // Abu-abu

    public KokiGUI(Akun akun) {
        setTitle("Dapur Restaurant - Chef " + akun.getNama());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        setContentPane(mainPanel);

        mainPanel.add(createHeader(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        contentPanel.add(createTableHeaders(), BorderLayout.NORTH);

        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setBackground(BG_COLOR);

        JScrollPane scrollPane = new JScrollPane(listContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        refreshTimer = new Timer(3000, e -> refreshList());
        refreshTimer.start();
        refreshList();
    }

    private void refreshList() {
        listContainer.removeAll();
        sys.refreshPesananFromFile();
        List<Pesanan> allOrders = sys.getDaftarPesanan();

        // 1. FILTER: Hanya ambil status aktif dapur
        List<String> visibleStatuses = Arrays.asList(
                "DIPROSES", "SEDANG DIMASAK"); // Koki hanya melihat yang perlu diproses

        List<Pesanan> filtered = new ArrayList<>();
        if (allOrders != null) {
            for (Pesanan p : allOrders) {
                if (visibleStatuses.contains(p.getStatus().toUpperCase())) {
                    filtered.add(p);
                }
            }
        }

        if (filtered.isEmpty()) {
            JLabel empty = new JLabel("Tidak ada pesanan aktif di dapur.", SwingConstants.CENTER);
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            empty.setForeground(Color.GRAY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            listContainer.add(Box.createVerticalStrut(30));
            listContainer.add(empty);
        } else {
            // 2. GROUPING: Satukan berdasarkan Nomor Meja
            Map<Integer, List<Pesanan>> groupedByTable = filtered.stream()
                    .collect(Collectors.groupingBy(p -> p.getMeja().getNomor()));

            List<Integer> sortedTables = new ArrayList<>(groupedByTable.keySet());
            java.util.Collections.sort(sortedTables);

            for (Integer mejaNum : sortedTables) {
                List<Pesanan> pesananMeja = groupedByTable.get(mejaNum);
                JPanel wrapper = new JPanel(new BorderLayout());
                wrapper.setBackground(BG_COLOR);
                wrapper.add(createGroupCard(mejaNum, pesananMeja), BorderLayout.NORTH);
                listContainer.add(wrapper);
                listContainer.add(Box.createVerticalStrut(15));
            }
        }

        listContainer.add(Box.createVerticalGlue());
        listContainer.revalidate();
        listContainer.repaint();
    }

    private JPanel createGroupCard(int mejaNum, List<Pesanan> orders) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 5, 0, 5);

        // --- 1. INFO MEJA ---
        gbc.gridx = 0;
        gbc.weightx = 0.25;
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);

        JLabel lblMeja = new JLabel("Meja " + mejaNum);
        lblMeja.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblMeja.setForeground(new Color(15, 23, 42));

        String ids = orders.stream().map(p -> "#" + p.getId()).collect(Collectors.joining(", "));
        JLabel lblId = new JLabel("<html><div style='width:100px;'>" + ids + "</div></html>");
        lblId.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblId.setForeground(Color.GRAY);

        infoPanel.add(lblMeja);
        infoPanel.add(lblId);
        card.add(infoPanel, gbc);

        // --- 2. DETAIL MENU ---
        gbc.gridx = 1;
        gbc.weightx = 0.45;

        StringBuilder sb = new StringBuilder("<html><body style='font-family: Segoe UI;'>");
        for (Pesanan p : orders) {
            for (DetailPesanan dp : p.getItems()) {
                sb.append("<div style='margin-bottom: 4px;'>");
                sb.append("<b>").append(dp.getMenu().getNama()).append("</b>");
                sb.append(" <span style='color:gray'>x").append(dp.getJumlah()).append("</span>");
                sb.append("</div>");
            }
            if (p.getCatatan() != null && !p.getCatatan().isEmpty() && !p.getCatatan().equals("-")) {
                sb.append("<span style='color:rgb(220,38,38); font-size:11px;'><i>Note: ")
                        .append(p.getCatatan()).append("</i></span><br>");
            }
        }
        sb.append("</body></html>");

        JLabel lblMenu = new JLabel(sb.toString());
        lblMenu.setVerticalAlignment(SwingConstants.TOP);
        card.add(lblMenu, gbc);

        // --- 3. ACTION BUTTONS (MODIFIKASI DISINI) ---
        gbc.gridx = 2;
        gbc.weightx = 0.30;

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        btnPanel.setOpaque(false);

        // Cek status saat ini (ambil dari pesanan pertama di grup)
        String currentStatus = orders.get(0).getStatus();

        JButton btnMulai = new JButton("<html><center>Mulai<br>Masak</center></html>");
        JButton btnSelesai = new JButton("<html><center>Tandai<br>Selesai</center></html>");

        styleButton(btnMulai, COLOR_BTN_COOK);
        styleButton(btnSelesai, COLOR_BTN_DONE);

        // LOGIKA BATASAN STATUS
        if ("DIPROSES".equals(currentStatus)) {
            // Jika baru masuk, hanya bisa klik Mulai. Selesai disable.
            btnMulai.setEnabled(true);
            btnSelesai.setEnabled(false);
            btnSelesai.setBackground(COLOR_DISABLED);
        } else if ("SEDANG DIMASAK".equals(currentStatus)) {
            // Jika sedang masak, tombol Mulai disable, tombol Selesai aktif.
            btnMulai.setEnabled(false);
            btnMulai.setBackground(COLOR_DISABLED);
            btnSelesai.setEnabled(true);
        }

        // Action Listener: Mulai Masak (DIPROSES -> SEDANG DIMASAK)
        btnMulai.addActionListener(e -> {
            for (Pesanan p : orders) {
                sys.updateStatusPesanan(p.getId(), "SEDANG DIMASAK");
            }
            refreshList();
        });

        // Action Listener: Selesai Masak (SEDANG DIMASAK -> SIAP DISAJIKAN)
        btnSelesai.addActionListener(e -> {
            for (Pesanan p : orders) {
                sys.updateStatusPesanan(p.getId(), "SIAP DISAJIKAN");
            }
            refreshList(); // Item akan hilang dari list karena status filter
            JOptionPane.showMessageDialog(this, "Pesanan Meja " + mejaNum + " siap disajikan!");
        });

        btnPanel.add(btnMulai);
        btnPanel.add(btnSelesai);
        card.add(btnPanel, gbc);

        return card;
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(THEME_BLUE);
        header.setPreferredSize(new Dimension(100, 70));
        header.setBorder(new EmptyBorder(0, 30, 0, 30));

        JLabel title = new JLabel("Dapur Restaurant");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setOpaque(true);
        btnLogout.addActionListener(e -> {
            if (refreshTimer != null)
                refreshTimer.stop();
            dispose();
            new LoginGUI().setVisible(true);
        });

        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setOpaque(false);
        btnPanel.add(btnLogout);
        header.add(btnPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createTableHeaders() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(THEME_BLUE);
        panel.setBorder(new EmptyBorder(12, 15, 12, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 5, 0, 5);
        Font font = new Font("Segoe UI", Font.BOLD, 14);
        Color color = Color.WHITE;

        gbc.gridx = 0;
        gbc.weightx = 0.25;
        JLabel h1 = new JLabel("Info Meja");
        h1.setFont(font);
        h1.setForeground(color);
        panel.add(h1, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.45;
        JLabel h2 = new JLabel("Detail Menu");
        h2.setFont(font);
        h2.setForeground(color);
        panel.add(h2, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0.30;
        JLabel h3 = new JLabel("Aksi Koki");
        h3.setFont(font);
        h3.setForeground(color);
        panel.add(h3, gbc);

        return panel;
    }
}