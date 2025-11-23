package com.restaurant.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class PelayanGUI extends JFrame {

    private JPanel cardPanel;
    private JScrollPane scrollPane;
    private JComboBox<String> cmbFilter;
    private List<Order> dataOrder = new ArrayList<>();
    private List<MenuItem> menuList = new ArrayList<>();

    private enum Status {MENUNGGU, SEDANG_DIMASAK, SIAP, DIANTAR, SELESAI}

    private static class Order {
        private final String id;
        private final int meja;
        private final List<String> items;
        private Status status;
        private final String catatan;

        Order(String id, int meja, List<String> items, Status status, String catatan) {
            this.id = id; this.meja = meja; this.items = new ArrayList<>(items);
            this.status = status; this.catatan = catatan;
        }

        public int getMeja() { return meja; }
        public Status getStatus() { return status; }
        public void setStatus(Status s) { this.status = s; }
        public List<String> getItems() { return items; }
        public String getId() { return id; }
        public String getCatatan() { return catatan; }
    }

    private static class MenuItem {
        String nama; double harga;
        MenuItem(String n, double h) { nama = n; harga = h; }
        public String getInfo() { return nama + " - Rp" + (int)harga; }
        public String getNama() { return nama; }
    }

    public PelayanGUI() {
        setTitle("Pelayan - Daftar Pesanan");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10,10));

        Color primaryColor = new Color(59,130,246);
        Color cardBg = new Color(245,245,245);

        // HEADER 
        JLabel title = new JLabel("Daftar Pesanan | Pelayan", SwingConstants.CENTER);
        title.setOpaque(true);
        title.setBackground(primaryColor);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(new EmptyBorder(18,0,18,0));
        add(title, BorderLayout.NORTH);

        // TOP PANEL
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        topPanel.setBackground(cardBg);
        cmbFilter = new JComboBox<>(new String[]{"Semua","MENUNGGU","SEDANG_DIMASAK","SIAP","DIANTAR","SELESAI"});
        cmbFilter.setPreferredSize(new Dimension(180, 32));
        JButton btnTambah = new JButton("Pesanan Baru"); styleButton(btnTambah, new Color(34,197,94));
        topPanel.add(new JLabel("Filter Status:")); topPanel.add(cmbFilter); topPanel.add(btnTambah);
        add(topPanel, BorderLayout.NORTH);

        // CARD PANEL 
        cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(0,2,12,12));
        cardPanel.setBackground(cardBg);
        scrollPane = new JScrollPane(cardPanel);
        scrollPane.setBorder(new EmptyBorder(12,12,12,12));
        add(scrollPane, BorderLayout.CENTER);

        // ACTIONS 
        cmbFilter.addActionListener(e -> reloadCards());
        btnTambah.addActionListener(e -> tambahPesananBaru());

        seedMenu();
        seedData();
        reloadCards();
    }

    private void seedMenu() {
        menuList.clear();
        menuList.add(new MenuItem("Nasi Goreng",15000));
        menuList.add(new MenuItem("Mie Ayam",12000));
        menuList.add(new MenuItem("Sate Ayam",20000));
        menuList.add(new MenuItem("Ayam Geprek",18000));
        menuList.add(new MenuItem("Es Teh",5000));
        menuList.add(new MenuItem("Es Jeruk",7000));
        menuList.add(new MenuItem("Kopi Panas",10000));
    }

    private void seedData() {
        dataOrder.clear();
        dataOrder.add(new Order("#101",1,List.of("Sate Ayam x2","Es Teh"),Status.MENUNGGU,""));
        dataOrder.add(new Order("#102",3,List.of("Nasi Goreng","Kopi Panas"),Status.SEDANG_DIMASAK,""));
    }

    private void reloadCards() {
        cardPanel.removeAll();
        String filter = (String)cmbFilter.getSelectedItem();
        for (Order o : dataOrder) {
            if(filter.equals("Semua")||o.getStatus().name().equals(filter)) {
                cardPanel.add(createOrderCard(o));
            }
        }
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    private JPanel createOrderCard(Order o) {
        Color borderColor;
        switch(o.getStatus()) {
            case MENUNGGU: borderColor=new Color(250,204,21); break;
            case SEDANG_DIMASAK: borderColor=new Color(251,146,60); break;
            case SIAP: borderColor=new Color(37,99,235); break;
            case DIANTAR: borderColor=new Color(59,130,246); break;
            case SELESAI: borderColor=new Color(16,185,129); break;
            default: borderColor=Color.LIGHT_GRAY;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(borderColor,2,true));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblTitle = new JLabel(o.getId()+" | Meja "+o.getMeja(),SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI",Font.BOLD,16));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea txtItems = new JTextArea("Status: "+o.getStatus()+"\nItems:\n - "+String.join("\n - ",o.getItems())+
                (o.getCatatan().isBlank()?"":"\nCatatan: "+o.getCatatan()));
        txtItems.setFont(new Font("Segoe UI",Font.PLAIN,14));
        txtItems.setEditable(false);
        txtItems.setBackground(Color.WHITE);

        panel.add(Box.createVerticalStrut(5));
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(5));
        panel.add(txtItems);
        panel.add(Box.createVerticalStrut(5));

        // BUTTONS STATUS 
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,6,0));
        btnPanel.setBackground(Color.WHITE);

        JButton btnAntar = new JButton("Antar"); styleButtonSmall(btnAntar,new Color(59,130,246));
        JButton btnSelesai = new JButton("Selesai"); styleButtonSmall(btnSelesai,new Color(16,185,129));
        JButton btnHapus = new JButton("Hapus"); styleButtonSmall(btnHapus,new Color(244,67,54));

        btnAntar.addActionListener(e->{ o.setStatus(Status.DIANTAR); reloadCards(); });
        btnSelesai.addActionListener(e->{ o.setStatus(Status.SELESAI); reloadCards(); });
        btnHapus.addActionListener(e->{ dataOrder.remove(o); reloadCards(); });

        btnPanel.add(btnAntar); 
        btnPanel.add(btnSelesai); 
        btnPanel.add(btnHapus);

        panel.add(btnPanel);
        panel.add(Box.createVerticalStrut(5));

        // Hover effect
        panel.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){panel.setBackground(new Color(240,248,255));}
            public void mouseExited(MouseEvent e){panel.setBackground(Color.WHITE);}
        });

        return panel;
    }

    private void styleButton(JButton b, Color c) {
        b.setBackground(c); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setFont(new Font("Segoe UI",Font.BOLD,14));
        b.setBorder(BorderFactory.createEmptyBorder(6,12,6,12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleButtonSmall(JButton b, Color c) {
        b.setBackground(c); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setFont(new Font("Segoe UI",Font.PLAIN,12));
        b.setBorder(BorderFactory.createEmptyBorder(4,6,4,6));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void tambahPesananBaru() {
        JDialog dialog = new JDialog(this,"Pesanan Baru",true);
        dialog.setSize(400,450); dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(); panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10,10,10,10));

        JTextField txtMeja = new JTextField(); txtMeja.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        txtMeja.setBorder(BorderFactory.createTitledBorder("Nomor Meja")); panel.add(txtMeja); panel.add(Box.createRigidArea(new Dimension(0,10)));

        JPanel menuCheckboxPanel = new JPanel(); menuCheckboxPanel.setLayout(new BoxLayout(menuCheckboxPanel,BoxLayout.Y_AXIS));
        menuCheckboxPanel.setBorder(BorderFactory.createTitledBorder("Pilih Menu"));
        List<JCheckBox> checkBoxes = new ArrayList<>();
        for(MenuItem m: menuList){JCheckBox cb = new JCheckBox(m.getInfo()); checkBoxes.add(cb); menuCheckboxPanel.add(cb);}
        JScrollPane scrollMenu = new JScrollPane(menuCheckboxPanel); scrollMenu.setPreferredSize(new Dimension(350,220));
        panel.add(scrollMenu);

        JButton btnOk = new JButton("Tambah"); styleButton(btnOk,new Color(34,197,94));
        btnOk.addActionListener(e->{
            try{
                int meja = Integer.parseInt(txtMeja.getText().trim());
                List<String> items = new ArrayList<>();
                for(int i=0;i<checkBoxes.size();i++){if(checkBoxes.get(i).isSelected()) items.add(menuList.get(i).getNama());}
                if(items.isEmpty()){JOptionPane.showMessageDialog(dialog,"Pilih minimal 1 menu!");return;}
                String id="#"+(100+dataOrder.size()+1);
                dataOrder.add(new Order(id,meja,items,Status.MENUNGGU,""));
                reloadCards(); dialog.dispose();
            }catch(NumberFormatException ex){JOptionPane.showMessageDialog(dialog,"Nomor meja harus angka!");}
        });

        JPanel btnPanel = new JPanel(); btnPanel.add(btnOk);
        dialog.add(panel,BorderLayout.CENTER); dialog.add(btnPanel,BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static void main(String[] args){ SwingUtilities.invokeLater(() -> new PelayanGUI().setVisible(true)); }
}
