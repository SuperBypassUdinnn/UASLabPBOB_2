package com.restaurant.gui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class MenuGUI extends JFrame {

    private JPanel panelCards;
    private JTextField searchField;
    private JComboBox<String> filterKategori;

    class MenuItem {
        String nama;
        String kategori;
        double harga;
        MenuItem(String n, String k, double h) { nama = n; kategori = k; harga = h; }
    }

    ArrayList<MenuItem> menuList = new ArrayList<>();

    // Palet warna & font 
    private final Color BG_COLOR = new Color(241, 243, 245);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private final Color BLUE_BUTTON = new Color(59, 130, 246);
    private final Color BORDER_COLOR = new Color(229, 231, 235);

    private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    private final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 13);
    private final Font INPUT_FONT = new Font("SansSerif", Font.PLAIN, 14);

    public MenuGUI() {
        setTitle("Daftar Menu Restoran");
        setSize(600, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        menuList.add(new MenuItem("Nasi Goreng", "Makanan", 15000));
        menuList.add(new MenuItem("Ayam Geprek", "Makanan", 18000));
        menuList.add(new MenuItem("Es Teh Manis", "Minuman", 5000));
        menuList.add(new MenuItem("Es Jeruk", "Minuman", 7000));

        JPanel mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBackground(BG_COLOR);
        mainPanel.setBorder(new EmptyBorder(20,20,20,20));
        setContentPane(mainPanel);

        //TOP PANEL
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(BG_COLOR);

        JLabel judul = new JLabel("Daftar Menu Restoran", SwingConstants.CENTER);
        judul.setFont(TITLE_FONT);   
        judul.setForeground(TEXT_PRIMARY);
        judul.setAlignmentX(Component.CENTER_ALIGNMENT); 
        topPanel.add(Box.createVerticalStrut(5));        
        topPanel.add(judul);
        topPanel.add(Box.createVerticalStrut(10));       

        // Panel untuk search + filter + tombol
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        searchPanel.setBackground(BG_COLOR);

        searchField = new JTextField(14);
        searchField.setFont(INPUT_FONT);
        searchField.setBorder(new CompoundBorder(new LineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8,10,8,10)));

        filterKategori = new JComboBox<>(new String[]{"Semua", "Makanan", "Minuman"});
        filterKategori.setFont(INPUT_FONT);
        filterKategori.setBorder(new CompoundBorder(new LineBorder(BORDER_COLOR,1),
                new EmptyBorder(5,5,5,5)));

        JButton searchBtn = createBlueButton("Cari");
        JButton resetBtn = createBlueButton("Reset");

        searchPanel.add(new JLabel("Search: ")); searchPanel.add(searchField);
        searchPanel.add(new JLabel("Kategori: ")); searchPanel.add(filterKategori);
        searchPanel.add(searchBtn); searchPanel.add(resetBtn);

        topPanel.add(searchPanel);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // PANEL CARDS 
        panelCards = new JPanel(new GridLayout(0,2,15,15));
        panelCards.setBackground(BG_COLOR);

        JScrollPane scroll = new JScrollPane(panelCards);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scroll, BorderLayout.CENTER);

        tampilkanMenu(menuList);

        searchBtn.addActionListener(e -> filterMenu());
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            filterKategori.setSelectedIndex(0);
            tampilkanMenu(menuList);
        });
    }

    private void tampilkanMenu(ArrayList<MenuItem> data) {
        panelCards.removeAll();
        for (MenuItem item : data) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(CARD_COLOR);
            card.setBorder(new CompoundBorder(new LineBorder(BORDER_COLOR,1,true),
                    new EmptyBorder(10,10,10,10)));

            JLabel nama = new JLabel(item.nama, SwingConstants.CENTER);
            nama.setFont(LABEL_FONT);
            nama.setForeground(TEXT_PRIMARY);
            nama.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel info = new JLabel(item.kategori + " - Rp " + (int)item.harga, SwingConstants.CENTER);
            info.setFont(INPUT_FONT);
            info.setForeground(TEXT_SECONDARY);
            info.setAlignmentX(Component.CENTER_ALIGNMENT);

            card.add(Box.createVerticalStrut(10));
            card.add(nama);
            card.add(Box.createVerticalStrut(5));
            card.add(info);
            card.add(Box.createVerticalStrut(10));

            // Hover effect card
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    card.setBorder(new CompoundBorder(new LineBorder(BLUE_BUTTON,2,true),
                            new EmptyBorder(10,10,10,10)));
                }
                public void mouseExited(MouseEvent e) {
                    card.setBorder(new CompoundBorder(new LineBorder(BORDER_COLOR,1,true),
                            new EmptyBorder(10,10,10,10)));
                }
            });

            panelCards.add(card);
        }
        panelCards.revalidate();
        panelCards.repaint();
    }

    private void filterMenu() {
        String keyword = searchField.getText().trim().toLowerCase();
        String kategoriDipilih = filterKategori.getSelectedItem().toString();
        ArrayList<MenuItem> filtered = new ArrayList<>();
        for (MenuItem item : menuList) {
            boolean cocokKeyword = item.nama.toLowerCase().contains(keyword);
            boolean cocokKategori = kategoriDipilih.equals("Semua") || item.kategori.equals(kategoriDipilih);
            if (cocokKeyword && cocokKategori) filtered.add(item);
        }
        tampilkanMenu(filtered);
    }

    private JButton createBlueButton(String text) {
        JButton b = new JButton(text);
        b.setFont(LABEL_FONT);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBackground(BLUE_BUTTON);
        b.setBorder(new EmptyBorder(8,20,8,20));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(BLUE_BUTTON.darker()); }
            @Override public void mouseExited(MouseEvent e) { b.setBackground(BLUE_BUTTON); }
        });
        return b;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuGUI().setVisible(true));
    }
}
