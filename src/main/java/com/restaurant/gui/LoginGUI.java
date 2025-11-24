package com.restaurant.gui;

import com.restaurant.model.akun.Akun;
import com.restaurant.service.AuthService;
import java.awt.*;
import java.awt.event.ItemEvent;
import javax.swing.*;
import javax.swing.border.*;

public class LoginGUI extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel cardContainer;
    private final AuthService auth = AuthService.getInstance();

    // Warna & Font
    private final Color BG_COLOR = new Color(241, 243, 245);
    private final Color CARD_COLOR = Color.WHITE;
    private final Color BLUE_BUTTON = new Color(59, 130, 246);
    private final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);

    public LoginGUI() {
        setTitle("Restaurant System");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 750);
        setLocationRelativeTo(null);

        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(BG_COLOR);
        setContentPane(mainContainer);

        cardLayout = new CardLayout();
        cardContainer = new JPanel(cardLayout);
        cardContainer.setBackground(CARD_COLOR);
        cardContainer.setBorder(new CompoundBorder(
                new LineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(40, 50, 40, 50)));

        cardContainer.add(createLoginPanel(), "LOGIN");
        cardContainer.add(createRegisterPanel(), "REGISTER");

        mainContainer.add(cardContainer);
        cardLayout.show(cardContainer, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = createGbc();

        addTitle(panel, "Restaurant System", "Silakan Login", gbc);

        addLabel(panel, "Username", gbc);
        JTextField tfUser = createTextField();
        panel.add(tfUser, gbc);
        addSpacer(gbc, 15);

        addLabel(panel, "Password", gbc);
        JPasswordField pfPass = createPasswordField();
        panel.add(pfPass, gbc);
        addSpacer(gbc, 25);

        JButton btnLogin = createBlueButton("Login");
        btnLogin.addActionListener(e -> {
            String user = tfUser.getText().trim();
            String pass = new String(pfPass.getPassword());

            Akun a = auth.login(user, pass);

            if (a != null) {
                String role = a.getRole();
                String nama = a.getNama();
                JOptionPane.showMessageDialog(this, "Selamat Datang " + nama + " (" + role + ")", "Login Berhasil",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();

                SwingUtilities.invokeLater(() -> {
                    if (role.equalsIgnoreCase("kasir"))
                        new KasirGUI(a).setVisible(true);
                    else if (role.equalsIgnoreCase("koki"))
                        new KokiGUI(a).setVisible(true);
                    else if (role.equalsIgnoreCase("pelayan"))
                        new PelayanGUI(a).setVisible(true);
                    else if (role.equalsIgnoreCase("customer"))
                        new CustomerGUI(a).setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this, "Username atau Password salah!", "Login Gagal",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(btnLogin, gbc);

        addFooterLink(panel, "Belum punya akun? ", "Daftar di sini", gbc,
                () -> cardLayout.show(cardContainer, "REGISTER"));

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = createGbc();

        addTitle(panel, "Daftar Akun Baru", "Isi data lengkap", gbc);

        addLabel(panel, "Nama Lengkap", gbc);
        JTextField tfNama = createTextField();
        panel.add(tfNama, gbc);
        addSpacer(gbc, 10);

        addLabel(panel, "Username", gbc);
        JTextField tfUser = createTextField();
        panel.add(tfUser, gbc);
        addSpacer(gbc, 10);

        addLabel(panel, "Email", gbc);
        JTextField tfEmail = createTextField();
        panel.add(tfEmail, gbc);
        addSpacer(gbc, 10);

        addLabel(panel, "Password", gbc);
        JPasswordField pfPass = createPasswordField();
        panel.add(pfPass, gbc);
        addSpacer(gbc, 10);

        addLabel(panel, "Tipe Akun", gbc);
        String[] tipeAkun = { "Customer", "Pegawai" };
        JComboBox<String> cbTipe = createComboBox(tipeAkun);
        panel.add(cbTipe, gbc);
        addSpacer(gbc, 10);

        JPanel rolePanel = new JPanel(new BorderLayout());
        rolePanel.setBackground(CARD_COLOR);

        JLabel lblRole = new JLabel("Role Pegawai");
        lblRole.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblRole.setForeground(Color.BLACK);
        rolePanel.add(lblRole, BorderLayout.NORTH);

        String[] roles = { "Kasir", "Koki", "Pelayan" };
        JComboBox<String> cbRole = createComboBox(roles);
        rolePanel.add(cbRole, BorderLayout.CENTER);
        rolePanel.setVisible(false);
        panel.add(rolePanel, gbc);

        cbTipe.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                boolean isPegawai = "Pegawai".equals(cbTipe.getSelectedItem());
                rolePanel.setVisible(isPegawai);
                panel.revalidate();
            }
        });

        JButton btnDaftar = createBlueButton("Daftar");
        btnDaftar.addActionListener(e -> {
            String nama = tfNama.getText();
            String user = tfUser.getText();
            String pass = new String(pfPass.getPassword());
            String email = tfEmail.getText();

            if (nama.isEmpty() || user.isEmpty() || pass.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Harap isi semua kolom.", "Peringatan",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String role = (String) cbRole.getSelectedItem();
            int success = auth.register(nama, user, pass, email, role);
            switch (success) {
                case 0 -> {
                    JOptionPane.showMessageDialog(this, "Registrasi Berhasil.", "SUKSES",
                            JOptionPane.INFORMATION_MESSAGE);
                    cardLayout.show(cardContainer, "LOGIN");
                }
                case 1 -> {
                    JOptionPane.showMessageDialog(this,
                            "Username harus 5-12 karakter. Tidak boleh ada karakter spesial",
                            "GAGAL",
                            JOptionPane.ERROR_MESSAGE);
                }
                case 2 -> {
                    JOptionPane.showMessageDialog(this, "Username telah digunakan.", "GAGAL",
                            JOptionPane.ERROR_MESSAGE);
                }
                case 3 -> {
                    JOptionPane.showMessageDialog(this, "Email tidak valid atau domain tidak terdaftar.", "GAGAL",
                            JOptionPane.ERROR_MESSAGE);
                }
                case 4 -> {
                    JOptionPane.showMessageDialog(this, "Anda tidak memiliki akses untuk mendaftar sebagai pegawai.",
                            "GAGAL",
                            JOptionPane.ERROR_MESSAGE);
                }
                case 5 -> {
                    JOptionPane.showMessageDialog(this, "Email sudah digunakan.", "GAGAL",
                            JOptionPane.ERROR_MESSAGE);
                }
                case 6 -> {
                    JOptionPane.showMessageDialog(this,
                            "Password tidak kuat. Gunakan kombinasi Upper Case, Lower Case, Digit, dan Special Character",
                            "GAGAL",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(btnDaftar, gbc);
        addFooterLink(panel, "Sudah punya akun? ", "Login di sini", gbc, () -> cardLayout.show(cardContainer, "LOGIN"));

        return panel;
    }

    // --- HELPERS ---
    private GridBagConstraints createGbc() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        return gbc;
    }

    private void addSpacer(GridBagConstraints gbc, int h) {
        gbc.insets = new Insets(0, 0, h, 0);
    }

    private void addTitle(JPanel p, String t, String s, GridBagConstraints gbc) {
        JLabel lt = new JLabel(t, SwingConstants.CENTER);
        lt.setFont(TITLE_FONT);
        lt.setForeground(new Color(33, 37, 41)); // TEXT HITAM PASTI
        JLabel ls = new JLabel(s, SwingConstants.CENTER);
        ls.setForeground(Color.GRAY);
        gbc.insets = new Insets(0, 0, 5, 0);
        p.add(lt, gbc);
        gbc.insets = new Insets(0, 0, 20, 0);
        p.add(ls, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
    }

    private void addLabel(JPanel p, String t, GridBagConstraints gbc) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(new Color(33, 37, 41)); // TEXT HITAM PASTI
        gbc.insets = new Insets(0, 0, 5, 0);
        p.add(l, gbc);
        gbc.insets = new Insets(0, 0, 5, 0);
    }

    // --- FIX INPUT FIELDS AGAR JELAS TERLIHAT ---
    private JTextField createTextField() {
        return (JTextField) styleInput(new JTextField(20));
    }

    private JPasswordField createPasswordField() {
        return (JPasswordField) styleInput(new JPasswordField(20));
    }

    private JComponent styleInput(JTextField t) {
        t.setFont(new Font("SansSerif", Font.PLAIN, 14));
        t.setOpaque(true);
        t.setBackground(Color.BLACK);
        t.setForeground(Color.WHITE);
        t.setCaretColor(Color.WHITE);
        t.setBorder(new CompoundBorder(new LineBorder(new Color(229, 231, 235), 1), new EmptyBorder(8, 10, 8, 10)));
        return t;
    }

    private JComboBox<String> createComboBox(String[] i) {
        JComboBox<String> c = new JComboBox<>(i);
        c.setBackground(Color.WHITE);
        c.setForeground(Color.BLACK);
        c.setOpaque(true);
        return c;
    }

    private JButton createBlueButton(String t) {
        JButton b = new JButton(t);
        b.setOpaque(true);
        b.setBackground(BLUE_BUTTON);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(100, 40));
        return b;
    }

    private void addFooterLink(JPanel p, String pr, String lt, GridBagConstraints gbc, Runnable act) {
        JLabel l = new JLabel("<html>" + pr + "<span style='color:#3b82f6;'><u>" + lt + "</u></span></html>",
                SwingConstants.CENTER);
        l.setForeground(Color.GRAY);
        l.setCursor(new Cursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                act.run();
            }
        });
        gbc.insets = new Insets(15, 0, 0, 0);
        p.add(l, gbc);
    }
}