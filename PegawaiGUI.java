package com.restaurant.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class PegawaiGUI extends JFrame {

    private JPanel panelCards;
    private ArrayList<MenuItem> menuList = new ArrayList<>();

    // Warna & Font 
    private final Color BG_COLOR = new Color(241,243,245);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color TEXT_PRIMARY = new Color(33,37,41);
    private final Color TEXT_SECONDARY = new Color(108,117,125);
    private final Color BUTTON_COLOR = new Color(59,130,246);
    private final Color BUTTON_DANGER = new Color(220,53,69);
    private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    private final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 14);
    private final Font INFO_FONT = new Font("SansSerif", Font.PLAIN, 13);

    static class MenuItem {
        String nama, kategori;
        double harga;
        MenuItem(String n, String k, double h){ nama=n; kategori=k; harga=h; }
        String getInfo(){ return nama + " | " + kategori + " | Rp " + (int)harga; }
    }

    public PegawaiGUI() {
        setTitle("Manajemen Menu - Pegawai");
        setSize(600,650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout(10,10));

        // HEADER 
        JLabel title = new JLabel("Manajemen Menu (Pegawai)", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setOpaque(true);
        title.setBackground(BUTTON_COLOR);
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(15,0,15,0));
        add(title, BorderLayout.NORTH);

        // TOP PANEL 
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10,10));
        topPanel.setBackground(BG_COLOR);

        JButton btnTambah = createButton("Tambah Menu", BUTTON_COLOR);
        JButton btnRefresh = createButton("Refresh", BUTTON_COLOR);
        JButton btnBack = createButton("Kembali", BUTTON_DANGER);

        topPanel.add(btnTambah); topPanel.add(btnRefresh); topPanel.add(btnBack);
        add(topPanel, BorderLayout.NORTH);

        // PANEL CARDS 
        panelCards = new JPanel(new GridLayout(0,2,15,15));
        panelCards.setBackground(BG_COLOR);
        JScrollPane scroll = new JScrollPane(panelCards);
        scroll.setBorder(new EmptyBorder(10,10,10,10));
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // EVENTS
        btnTambah.addActionListener(e -> tambahMenu());
        btnRefresh.addActionListener(e -> reloadCards());
        btnBack.addActionListener(e -> dispose());

        // SAMPLE DATA 
        menuList.add(new MenuItem("Nasi Goreng","Makanan",15000));
        menuList.add(new MenuItem("Ayam Geprek","Makanan",18000));
        menuList.add(new MenuItem("Es Teh","Minuman",5000));
        menuList.add(new MenuItem("Es Jeruk","Minuman",7000));

        reloadCards();
    }

    private JButton createButton(String text, Color bg){
        JButton b = new JButton(text);
        b.setFont(LABEL_FONT);
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(6,12,6,12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){ b.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e){ b.setBackground(bg); }
        });
        return b;
    }

    private void reloadCards(){
        panelCards.removeAll();
        for(MenuItem m : menuList){
            panelCards.add(createCard(m));
        }
        panelCards.revalidate();
        panelCards.repaint();
    }

    private JPanel createCard(MenuItem m){
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_COLOR);
        card.setBorder(new LineBorder(new Color(220,220,220),1,true));

        JLabel lblNama = new JLabel(m.nama, SwingConstants.CENTER);
        lblNama.setFont(LABEL_FONT);
        lblNama.setForeground(TEXT_PRIMARY);
        lblNama.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblInfo = new JLabel(m.kategori + " - Rp " + (int)m.harga, SwingConstants.CENTER);
        lblInfo.setFont(INFO_FONT);
        lblInfo.setForeground(TEXT_SECONDARY);
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,5,5));
        btnPanel.setBackground(CARD_COLOR);

        JButton btnHapus = createButton("Hapus", BUTTON_DANGER);
        btnHapus.setFont(INFO_FONT);
        btnHapus.addActionListener(e -> { menuList.remove(m); reloadCards(); });

        btnPanel.add(btnHapus);

        card.add(Box.createVerticalStrut(10));
        card.add(lblNama);
        card.add(Box.createVerticalStrut(5));
        card.add(lblInfo);
        card.add(Box.createVerticalStrut(5));
        card.add(btnPanel);
        card.add(Box.createVerticalStrut(10));

        card.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){ card.setBackground(new Color(245,245,245)); }
            public void mouseExited(MouseEvent e){ card.setBackground(CARD_COLOR); }
        });

        return card;
    }

    private void tambahMenu(){
        JTextField fNama = new JTextField();
        JTextField fHarga = new JTextField();
        JComboBox<String> fKategori = new JComboBox<>(new String[]{"Makanan","Minuman"});
        Object[] form = {"Nama:", fNama, "Harga:", fHarga, "Kategori:", fKategori};
        int result = JOptionPane.showConfirmDialog(this, form, "Tambah Menu", JOptionPane.OK_CANCEL_OPTION);
        if(result == JOptionPane.OK_OPTION){
            try{
                String nama = fNama.getText().trim();
                double harga = Double.parseDouble(fHarga.getText());
                String kategori = fKategori.getSelectedItem().toString();
                menuList.add(new MenuItem(nama,kategori,harga));
                reloadCards();
            }catch(Exception e){ JOptionPane.showMessageDialog(this,"Input tidak valid!"); }
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new PegawaiGUI().setVisible(true));
    }
}
