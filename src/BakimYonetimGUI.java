import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BakimYonetimGUI extends JFrame {

    private JTextField adSoyadField;
    private JTextField kurumField;
    private JTextField departmanField;
    private JTextArea aciklamaArea;
    private JTextArea musteriSonucArea;
    private JTextField musteriTalepIdField;
    private JTextArea musteriEkBilgiArea;
    private JComboBox<Integer> puanComboBox;

    private JTextField ekipLideriAdiField;
    private JPasswordField ekipLideriSifreField;
    private DefaultListModel<HizmetTalebi> ekipLideriTalepListModel;
    private JList<HizmetTalebi> ekipLideriTalepListesi;
    private JTextArea ekipLideriDetayArea;
    private JCheckBox[] alanCheckBoxlari;
    private JSpinner[] alanKisiSpinnerlari;
    private JTextArea liderCozumNotArea;

    private DefaultListModel<EkipLideri> ekipLideriListModel;
    private JList<EkipLideri> ekipLideriListesi;
    private JTextField yeniEkipLideriAdField;

    private JTextField calisanAdiField;
    private JPasswordField calisanSifreField;
    private DefaultListModel<HizmetTalebi> talepListModel;
    private JList<HizmetTalebi> talepListesi;
    private JTextArea teknikTalepArea;
    private JTextArea teknikNotArea;

    private JPasswordField yoneticiSifreField;
    private JTextArea yoneticiRaporArea;
    private boolean yoneticiGirisYapti = false;

    private List<TeknikCalisan> calisanlar;
    private List<HizmetTalebi> talepler;
    private List<String> hizmetAlanlari;
    private List<EkipLideri> ekipLiderleri;

    private DefaultListModel<TeknikCalisan> calisanListModel;
    private JList<TeknikCalisan> calisanListesi;
    private JTextField yeniCalisanAdField;
    private JComboBox<String> yeniCalisanAlanComboBox;
    private JTextField yeniAlanField;

    private AtamaServisi atamaServisi;
    private XmlTalepServisi xmlTalepServisi;
    private XmlPersonelServisi xmlPersonelServisi;

    private TeknikCalisan girisYapanCalisan;
    private HizmetTalebi seciliTalep;

    private EkipLideri girisYapanEkipLideri;
    private HizmetTalebi ekipLideriSeciliTalep;

    public BakimYonetimGUI() {
        setTitle("Bakım ve Onarım Bilgi Sistemi");
        setSize(1100, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        xmlPersonelServisi = new XmlPersonelServisi();
        hizmetAlanlari = xmlPersonelServisi.hizmetAlanlariYukle();
        calisanlar = xmlPersonelServisi.calisanlariYukle();
        ekipLiderleri = xmlPersonelServisi.ekipLiderleriYukle();

        atamaServisi = new AtamaServisi();
        xmlTalepServisi = new XmlTalepServisi();
        talepler = xmlTalepServisi.talepleriYukle(calisanlar, ekipLiderleri);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Müşteri Paneli", musteriPaneliOlustur());
        tabbedPane.addTab("Ekip Lideri Paneli", ekipLideriPaneliOlustur());
        tabbedPane.addTab("Teknik Çalışan Paneli", teknikCalisanPaneliOlustur());
        tabbedPane.addTab("Yönetici Paneli", yoneticiPaneliOlustur());

        add(tabbedPane);
    }

    private JPanel musteriPaneliOlustur() {
        JPanel anaPanel = new JPanel(new BorderLayout());
        JTabbedPane musteriTabs = new JTabbedPane();
        musteriTabs.addTab("Talep Oluştur", musteriTalepOlusturPaneli());
        musteriTabs.addTab("Talep Takip / Ek Bilgi / Onay", musteriTakipPaneli());
        anaPanel.add(musteriTabs, BorderLayout.CENTER);
        return anaPanel;
    }

    private JPanel musteriTalepOlusturPaneli() {
        JPanel anaPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        formPanel.add(new JLabel("Ad Soyad:"));
        adSoyadField = new JTextField();
        formPanel.add(adSoyadField);

        formPanel.add(new JLabel("Kurum:"));
        kurumField = new JTextField();
        formPanel.add(kurumField);

        formPanel.add(new JLabel("Departman:"));
        departmanField = new JTextField();
        formPanel.add(departmanField);

        formPanel.add(new JLabel("Problem Açıklaması:"));
        aciklamaArea = new JTextArea(5, 20);
        formPanel.add(new JScrollPane(aciklamaArea));

        JButton talepOlusturButton = new JButton("Talep Oluştur");
        formPanel.add(talepOlusturButton);

        musteriSonucArea = new JTextArea();
        musteriSonucArea.setEditable(false);

        anaPanel.add(formPanel, BorderLayout.NORTH);
        anaPanel.add(new JScrollPane(musteriSonucArea), BorderLayout.CENTER);

        talepOlusturButton.addActionListener(e -> talepOlustur());
        return anaPanel;
    }

    private JPanel musteriTakipPaneli() {
        JPanel anaPanel = new JPanel(new BorderLayout());
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        formPanel.add(new JLabel("Talep ID:"));
        musteriTalepIdField = new JTextField();
        formPanel.add(musteriTalepIdField);

        JButton talepGoruntuleButton = new JButton("Talebimi Görüntüle");
        formPanel.add(talepGoruntuleButton);

        formPanel.add(new JLabel("Ek Bilgi Cevabı / Ret Açıklaması:"));
        musteriEkBilgiArea = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(musteriEkBilgiArea));

        JButton ekBilgiGonderButton = new JButton("Ek Bilgi Gönder");
        JButton reddetButton = new JButton("Çözümü Reddet");

        formPanel.add(ekBilgiGonderButton);
        formPanel.add(reddetButton);

        formPanel.add(new JLabel("Puan:"));
        puanComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        formPanel.add(puanComboBox);

        JButton puanVerButton = new JButton("Onayla ve Puan Ver");
        formPanel.add(puanVerButton);

        anaPanel.add(formPanel, BorderLayout.NORTH);
        anaPanel.add(new JScrollPane(musteriSonucArea), BorderLayout.CENTER);

        talepGoruntuleButton.addActionListener(e -> musteriTalepGoruntule());
        ekBilgiGonderButton.addActionListener(e -> ekBilgiGonder());
        reddetButton.addActionListener(e -> musteriCozumuReddet());
        puanVerButton.addActionListener(e -> puanVer());
        return anaPanel;
    }

    private JPanel ekipLideriPaneliOlustur() {
        JPanel anaPanel = new JPanel(new BorderLayout());

        JPanel loginPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        loginPanel.add(new JLabel("Ekip Lideri Adı:"));
        ekipLideriAdiField = new JTextField();
        loginPanel.add(ekipLideriAdiField);

        loginPanel.add(new JLabel("Şifre:"));
        ekipLideriSifreField = new JPasswordField();
        loginPanel.add(ekipLideriSifreField);

        JButton girisButton = new JButton("Giriş Yap");
        loginPanel.add(girisButton);

        ekipLideriTalepListModel = new DefaultListModel<>();
        ekipLideriTalepListesi = new JList<>(ekipLideriTalepListModel);

        ekipLideriDetayArea = new JTextArea();
        ekipLideriDetayArea.setEditable(false);

        JPanel ortaPanel = new JPanel(new GridLayout(1, 2));
        ortaPanel.add(new JScrollPane(ekipLideriTalepListesi));
        ortaPanel.add(new JScrollPane(ekipLideriDetayArea));

        JPanel departmanPanel = new JPanel(new GridLayout(0, 3, 5, 5));
        alanCheckBoxlari = new JCheckBox[hizmetAlanlari.size()];
        alanKisiSpinnerlari = new JSpinner[hizmetAlanlari.size()];

        for (int i = 0; i < hizmetAlanlari.size(); i++) {
            alanCheckBoxlari[i] = new JCheckBox(hizmetAlanlari.get(i));
            alanKisiSpinnerlari[i] = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

            departmanPanel.add(alanCheckBoxlari[i]);
            departmanPanel.add(new JLabel("Kişi:"));
            departmanPanel.add(alanKisiSpinnerlari[i]);
        }

        JScrollPane departmanScroll = new JScrollPane(departmanPanel);
        departmanScroll.setPreferredSize(new Dimension(320, 0));

        JPanel altPanel = new JPanel(new BorderLayout());

        JPanel notPanel = new JPanel(new BorderLayout());
        notPanel.add(new JLabel("Müşteriye Gönderilecek Çözüm / Ek Bilgi Notu:"), BorderLayout.NORTH);

        liderCozumNotArea = new JTextArea(3, 20);
        notPanel.add(new JScrollPane(liderCozumNotArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        JButton ataButton = new JButton("Çalışanlara Ata");
        JButton liderEkBilgiIsteButton = new JButton("Müşteriden Ek Bilgi İste");
        JButton musteriyiBilgilendirButton = new JButton("Çözümü Müşteriye Gönder");

        buttonPanel.add(ataButton);
        buttonPanel.add(liderEkBilgiIsteButton);
        buttonPanel.add(musteriyiBilgilendirButton);

        altPanel.add(notPanel, BorderLayout.CENTER);
        altPanel.add(buttonPanel, BorderLayout.SOUTH);

        anaPanel.add(loginPanel, BorderLayout.NORTH);
        anaPanel.add(ortaPanel, BorderLayout.CENTER);
        anaPanel.add(departmanScroll, BorderLayout.EAST);
        anaPanel.add(altPanel, BorderLayout.SOUTH);

        girisButton.addActionListener(e -> ekipLideriGirisYap());

        ekipLideriTalepListesi.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ekipLideriSeciliTalep = ekipLideriTalepListesi.getSelectedValue();
                ekipLideriTalepDetayGoster();
            }
        });

        ataButton.addActionListener(e -> departmanlariAta());
        liderEkBilgiIsteButton.addActionListener(e -> liderEkBilgiIste());
        musteriyiBilgilendirButton.addActionListener(e -> liderCozumuMusteriyeGonder());

        return anaPanel;
    }

    private JPanel teknikCalisanPaneliOlustur() {
        JPanel anaPanel = new JPanel(new BorderLayout());

        JPanel loginPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        loginPanel.add(new JLabel("Çalışan Adı:"));
        calisanAdiField = new JTextField();
        loginPanel.add(calisanAdiField);

        loginPanel.add(new JLabel("Şifre:"));
        calisanSifreField = new JPasswordField();
        loginPanel.add(calisanSifreField);

        JButton girisButton = new JButton("Giriş Yap");
        loginPanel.add(girisButton);

        talepListModel = new DefaultListModel<>();
        talepListesi = new JList<>(talepListModel);

        teknikTalepArea = new JTextArea();
        teknikTalepArea.setEditable(false);

        teknikNotArea = new JTextArea(4, 20);

        JButton isiTamamladimButton = new JButton("Kendi İşimi Tamamladım");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(isiTamamladimButton);

        JPanel ortaPanel = new JPanel(new GridLayout(1, 2));
        ortaPanel.add(new JScrollPane(talepListesi));
        ortaPanel.add(new JScrollPane(teknikTalepArea));

        JPanel altPanel = new JPanel(new BorderLayout());
        altPanel.add(new JLabel("Yapılan İş Notu:"), BorderLayout.NORTH);
        altPanel.add(new JScrollPane(teknikNotArea), BorderLayout.CENTER);
        altPanel.add(buttonPanel, BorderLayout.SOUTH);

        anaPanel.add(loginPanel, BorderLayout.NORTH);
        anaPanel.add(ortaPanel, BorderLayout.CENTER);
        anaPanel.add(altPanel, BorderLayout.SOUTH);

        girisButton.addActionListener(e -> teknikGirisYap());

        talepListesi.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seciliTalep = talepListesi.getSelectedValue();
                seciliTalepDetayGoster();
            }
        });

        isiTamamladimButton.addActionListener(e -> cozulduYap());

        return anaPanel;
    }

    private JPanel yoneticiPaneliOlustur() {
        JPanel anaPanel = new JPanel(new BorderLayout());
        JPanel ustPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        ustPanel.add(new JLabel("Yönetici Şifresi:"));
        yoneticiSifreField = new JPasswordField();
        ustPanel.add(yoneticiSifreField);

        JButton girisButton = new JButton("Giriş Yap");
        ustPanel.add(girisButton);

        JTabbedPane yoneticiTabs = new JTabbedPane();
        yoneticiTabs.addTab("Raporlar", raporPaneliOlustur());
        yoneticiTabs.addTab("Bakım Sistemi", bakimSistemiPaneliOlustur());

        anaPanel.add(ustPanel, BorderLayout.NORTH);
        anaPanel.add(yoneticiTabs, BorderLayout.CENTER);
        girisButton.addActionListener(e -> yoneticiGirisYap());
        return anaPanel;
    }

    private JPanel raporPaneliOlustur() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton raporButton = new JButton("Rapor Oluştur");
        yoneticiRaporArea = new JTextArea();
        yoneticiRaporArea.setEditable(false);
        panel.add(raporButton, BorderLayout.NORTH);
        panel.add(new JScrollPane(yoneticiRaporArea), BorderLayout.CENTER);
        raporButton.addActionListener(e -> raporOlustur());
        return panel;
    }

    private JPanel bakimSistemiPaneliOlustur() {
        JPanel anaPanel = new JPanel(new GridLayout(1, 2));

        JPanel calisanPanel = new JPanel(new BorderLayout());
        calisanListModel = new DefaultListModel<>();
        calisanListesi = new JList<>(calisanListModel);
        JPanel calisanFormPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        calisanFormPanel.add(new JLabel("Çalışan Ad Soyad:"));
        yeniCalisanAdField = new JTextField();
        calisanFormPanel.add(yeniCalisanAdField);
        calisanFormPanel.add(new JLabel("Hizmet Alanı:"));
        yeniCalisanAlanComboBox = new JComboBox<>(hizmetAlanlari.toArray(new String[0]));
        calisanFormPanel.add(yeniCalisanAlanComboBox);

        JButton calisanEkleButton = new JButton("Çalışan Ekle");
        JButton calisanSilButton = new JButton("Seçili Çalışanı Sil");
        JButton calisanGuncelleButton = new JButton("Seçili Çalışanı Güncelle");
        calisanFormPanel.add(calisanEkleButton);
        calisanFormPanel.add(calisanSilButton);
        calisanFormPanel.add(calisanGuncelleButton);

        calisanFormPanel.add(new JLabel("Yeni Hizmet Alanı:"));
        yeniAlanField = new JTextField();
        calisanFormPanel.add(yeniAlanField);
        JButton alanEkleButton = new JButton("Hizmet Alanı Ekle");
        calisanFormPanel.add(alanEkleButton);

        calisanPanel.add(new JLabel("Teknik Çalışan Yönetimi"), BorderLayout.NORTH);
        calisanPanel.add(new JScrollPane(calisanListesi), BorderLayout.CENTER);
        calisanPanel.add(calisanFormPanel, BorderLayout.SOUTH);

        calisanEkleButton.addActionListener(e -> calisanEkle());
        calisanSilButton.addActionListener(e -> calisanSil());
        calisanGuncelleButton.addActionListener(e -> calisanGuncelle());
        alanEkleButton.addActionListener(e -> hizmetAlaniEkle());
        calisanListesi.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                TeknikCalisan secili = calisanListesi.getSelectedValue();
                if (secili != null) {
                    yeniCalisanAdField.setText(secili.getAdSoyad());
                    yeniCalisanAlanComboBox.setSelectedItem(secili.getAlan());
                }
            }
        });

        JPanel liderPanel = new JPanel(new BorderLayout());
        ekipLideriListModel = new DefaultListModel<>();
        ekipLideriListesi = new JList<>(ekipLideriListModel);
        JPanel liderFormPanel = new JPanel(new GridLayout(0, 2, 10, 10));

        liderFormPanel.add(new JLabel("Ekip Lideri Ad Soyad:"));
        yeniEkipLideriAdField = new JTextField();
        liderFormPanel.add(yeniEkipLideriAdField);
        JButton liderEkleButton = new JButton("Ekip Lideri Ekle");
        JButton liderSilButton = new JButton("Seçili Lideri Sil");
        liderFormPanel.add(liderEkleButton);
        liderFormPanel.add(liderSilButton);

        liderPanel.add(new JLabel("Ekip Lideri Yönetimi"), BorderLayout.NORTH);
        liderPanel.add(new JScrollPane(ekipLideriListesi), BorderLayout.CENTER);
        liderPanel.add(liderFormPanel, BorderLayout.SOUTH);
        liderEkleButton.addActionListener(e -> ekipLideriEkle());
        liderSilButton.addActionListener(e -> ekipLideriSil());

        anaPanel.add(calisanPanel);
        anaPanel.add(liderPanel);
        calisanListesiniYenile();
        ekipLideriListesiniYenile();
        return anaPanel;
    }

    private void talepOlustur() {
        String adSoyad = adSoyadField.getText().trim();
        String kurum = kurumField.getText().trim();
        String departman = departmanField.getText().trim();
        String aciklama = aciklamaArea.getText().trim();

        if (adSoyad.isEmpty() || kurum.isEmpty() || departman.isEmpty() || aciklama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurunuz!");
            return;
        }

        Musteri musteri = new Musteri(adSoyad, kurum, departman);
        HizmetTalebi talep = new HizmetTalebi("Ekip Lideri Belirleyecek", aciklama, musteri);

        EkipLideri lider = uygunEkipLideriBul();
        if (lider != null) talep.setEkipLideri(lider);
        else talep.liderAtamaBekliyorYap();

        talepler.add(talep);
        xmlTalepServisi.talepleriKaydet(talepler);
        xmlPersonelServisi.ekipLiderleriKaydet(ekipLiderleri);

        String sonuc = "Talep başarıyla oluşturuldu.\n\n";
        sonuc += "Talep ID: " + talep.getTalepId() + "\n";
        sonuc += "Müşteri: " + musteri.getAdSoyad() + "\n";
        sonuc += "Kurum: " + musteri.getKurum() + "\n";
        sonuc += "Departman: " + musteri.getDepartman() + "\n";
        sonuc += "Açıklama: " + talep.getAciklama() + "\n";
        sonuc += "Talep Durumu: " + talep.getDurum() + "\n";
        sonuc += "Atanan Ekip Lideri: " + (talep.getEkipLideri() == null ? "Henüz atanmadı - bekleme kuyruğunda" : talep.getEkipLideri().getAdSoyad()) + "\n";

        musteriSonucArea.setText(sonuc);

        JOptionPane.showMessageDialog(this,
                "Talebiniz başarıyla oluşturuldu.\n\nTalep ID: " + talep.getTalepId() +
                        "\nAtanan Ekip Lideri: " + (talep.getEkipLideri() == null ? "Henüz atanmadı - bekleme kuyruğunda" : talep.getEkipLideri().getAdSoyad()) +
                        "\n\nBu ID ile talebinizi takip edebilirsiniz.",
                "Talep Oluşturuldu",
                JOptionPane.INFORMATION_MESSAGE);

        if (girisYapanEkipLideri != null) ekipLideriTalepleriniYenile();
    }

    private EkipLideri uygunEkipLideriBul() {
        EkipLideri secilen = null;
        int maksimumTalep = 7;
        for (EkipLideri lider : ekipLiderleri) {
            if (lider.getAktifTalepSayisi() >= maksimumTalep) continue;
            if (secilen == null || lider.getAktifTalepSayisi() < secilen.getAktifTalepSayisi()) secilen = lider;
        }
        return secilen;
    }

    private void ekipLideriGirisYap() {
        String ad = ekipLideriAdiField.getText().trim();
        String sifre = new String(ekipLideriSifreField.getPassword());
        if (!sifre.equals("4321")) {
            JOptionPane.showMessageDialog(this, "Şifre hatalı!");
            return;
        }
        for (EkipLideri lider : ekipLiderleri) {
            if (lider.getAdSoyad().equalsIgnoreCase(ad)) {
                girisYapanEkipLideri = lider;
                JOptionPane.showMessageDialog(this, "Ekip lideri girişi başarılı: " + lider.getAdSoyad());
                ekipLideriTalepleriniYenile();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Bu isimde ekip lideri bulunamadı.");
    }

    private void ekipLideriListesiniYenile() {
        if (ekipLideriListModel == null) return;
        ekipLideriListModel.clear();
        for (EkipLideri lider : ekipLiderleri) ekipLideriListModel.addElement(lider);
    }

    private void ekipLideriEkle() {
        if (!yoneticiGirisYapti) {
            JOptionPane.showMessageDialog(this, "Önce yönetici girişi yapınız.");
            return;
        }
        String ad = yeniEkipLideriAdField.getText().trim();
        if (ad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ekip lideri adı boş olamaz.");
            return;
        }
        for (EkipLideri lider : ekipLiderleri) {
            if (lider.getAdSoyad().equalsIgnoreCase(ad)) {
                JOptionPane.showMessageDialog(this, "Bu ekip lideri zaten var.");
                return;
            }
        }
        ekipLiderleri.add(new EkipLideri(ad, 0));
        xmlPersonelServisi.ekipLiderleriKaydet(ekipLiderleri);
        bekleyenTalepleriLiderlereAta();
        ekipLideriListesiniYenile();
        yeniEkipLideriAdField.setText("");
        JOptionPane.showMessageDialog(this, "Yeni ekip lideri eklendi ve bekleyen talepler otomatik atandı.");
    }

    private void ekipLideriSil() {
        if (!yoneticiGirisYapti) {
            JOptionPane.showMessageDialog(this, "Önce yönetici girişi yapınız.");
            return;
        }
        EkipLideri secili = ekipLideriListesi.getSelectedValue();
        if (secili == null) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek ekip liderini seçiniz.");
            return;
        }
        for (HizmetTalebi talep : talepler) {
            if (talep.getEkipLideri() != null && talep.getEkipLideri().getAdSoyad().equalsIgnoreCase(secili.getAdSoyad()) && talep.getDurum() != TalepDurumu.KAPANDI) {
                JOptionPane.showMessageDialog(this, "Bu ekip liderinin üzerinde aktif talep var. Önce talepleri tamamlayın veya başka lidere yönlendirin.");
                return;
            }
        }
        ekipLiderleri.remove(secili);
        xmlPersonelServisi.ekipLiderleriKaydet(ekipLiderleri);
        ekipLideriListesiniYenile();
        JOptionPane.showMessageDialog(this, "Ekip lideri silindi ve XML güncellendi.");
    }

    private void ekipLideriTalepleriniYenile() {
        ekipLideriTalepListModel.clear();
        for (HizmetTalebi talep : talepler) {
            if (talep.getEkipLideri() != null && girisYapanEkipLideri != null && talep.getEkipLideri().getAdSoyad().equalsIgnoreCase(girisYapanEkipLideri.getAdSoyad()) && talep.getDurum() != TalepDurumu.KAPANDI) {
                ekipLideriTalepListModel.addElement(talep);
            }
        }
        if (ekipLideriTalepListModel.isEmpty()) ekipLideriDetayArea.setText("Bu ekip liderine atanmış aktif talep bulunmamaktadır.");
    }

    private void bekleyenTalepleriLiderlereAta() {
        for (HizmetTalebi talep : talepler) {
            boolean bekliyorMu = talep.getDurum() == TalepDurumu.LIDER_ATAMA_BEKLIYOR || (talep.getDurum() == TalepDurumu.EKIP_LIDERINDE && talep.getEkipLideri() == null);
            if (bekliyorMu) {
                EkipLideri lider = uygunEkipLideriBul();
                if (lider == null) break;
                talep.setEkipLideri(lider);
            }
        }
        xmlTalepServisi.talepleriKaydet(talepler);
        xmlPersonelServisi.ekipLiderleriKaydet(ekipLiderleri);
        if (girisYapanEkipLideri != null) ekipLideriTalepleriniYenile();
    }

    private void ekipLideriTalepDetayGoster() {
        if (ekipLideriSeciliTalep == null) return;
        String detay = "";
        detay += "Talep ID: " + ekipLideriSeciliTalep.getTalepId() + "\n";
        detay += "Müşteri: " + ekipLideriSeciliTalep.getMusteri().getAdSoyad() + "\n";
        detay += "Kurum: " + ekipLideriSeciliTalep.getMusteri().getKurum() + "\n";
        detay += "Departman: " + ekipLideriSeciliTalep.getMusteri().getDepartman() + "\n";
        detay += "Açıklama: " + ekipLideriSeciliTalep.getAciklama() + "\n";
        detay += "Durum: " + ekipLideriSeciliTalep.getDurum() + "\n";
        detay += "Gerekli Alanlar: " + ekipLideriSeciliTalep.getGerekliAlanlar() + "\n";
        detay += "Atanan Çalışanlar: " + ekipLideriSeciliTalep.getAtananCalisanlar() + "\n";
        detay += "\nÇalışan Durumları:\n" + ekipLideriSeciliTalep.getCalisanDurumlari() + "\n";
        detay += "\nMüşteri Cevapları:\n" + ekipLideriSeciliTalep.getMusteriCevaplari() + "\n";
        detay += "\nEkip Lideri Notları:\n" + ekipLideriSeciliTalep.getEkipLideriNotlari() + "\n";
        detay += "\nTeknik Not: " + ekipLideriSeciliTalep.getTeknikNot() + "\n";
        ekipLideriDetayArea.setText(detay);
    }

    private void departmanlariAta() {
        if (girisYapanEkipLideri == null) {
            JOptionPane.showMessageDialog(this, "Önce ekip lideri girişi yapınız.");
            return;
        }
        if (ekipLideriSeciliTalep == null) {
            JOptionPane.showMessageDialog(this, "Lütfen bir talep seçiniz.");
            return;
        }

        List<String> secilenAlanlar = new ArrayList<>();
        List<Integer> kisiSayilari = new ArrayList<>();
        for (int i = 0; i < alanCheckBoxlari.length; i++) {
            if (alanCheckBoxlari[i].isSelected()) {
                secilenAlanlar.add(alanCheckBoxlari[i].getText());
                kisiSayilari.add((Integer) alanKisiSpinnerlari[i].getValue());
            }
        }
        if (secilenAlanlar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen en az bir departman seçiniz.");
            return;
        }

        atamaServisi.departmanlaraGoreCalisanAta(ekipLideriSeciliTalep, secilenAlanlar, kisiSayilari, calisanlar);
        ekipLideriSeciliTalep.ekipLideriBilgiEkle("Ekip lideri " + girisYapanEkipLideri.getAdSoyad() + " departman ve kişi ataması yaptı.");

        xmlTalepServisi.talepleriKaydet(talepler);
        xmlPersonelServisi.calisanlariKaydet(calisanlar);
        ekipLideriTalepleriniYenile();
        if (girisYapanCalisan != null) teknikTalepListesiniYenile();
        for (int i = 0; i < alanCheckBoxlari.length; i++) {
            alanCheckBoxlari[i].setSelected(false);
            alanKisiSpinnerlari[i].setValue(1);
        }
        JOptionPane.showMessageDialog(this, "Departmanlara göre çalışan ataması yapıldı.");
    }

    private void liderEkBilgiIste() {
        if (girisYapanEkipLideri == null) {
            JOptionPane.showMessageDialog(this, "Önce ekip lideri girişi yapınız.");
            return;
        }

        if (ekipLideriSeciliTalep == null) {
            JOptionPane.showMessageDialog(this, "Lütfen bir talep seçiniz.");
            return;
        }

        String mesaj = JOptionPane.showInputDialog(
                this,
                "Müşteriden istenecek ek bilgiyi yazınız:",
                "Ek Bilgi İste",
                JOptionPane.QUESTION_MESSAGE
        );

        if (mesaj == null || mesaj.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ek bilgi mesajı boş olamaz.");
            return;
        }

        String bilgi = "Ekip lideri " + girisYapanEkipLideri.getAdSoyad()
                + " müşteriden ek bilgi istedi: " + mesaj.trim();

        ekipLideriSeciliTalep.setTeknikNot(bilgi);
        ekipLideriSeciliTalep.ekipLideriBilgiEkle(bilgi);
        ekipLideriSeciliTalep.musteriYanitiBekliyorYap();

        xmlTalepServisi.talepleriKaydet(talepler);

        ekipLideriTalepleriniYenile();
        ekipLideriTalepDetayGoster();

        JOptionPane.showMessageDialog(this, "Ek bilgi isteği müşteriye gönderildi.");
    }

    private void liderCozumuMusteriyeGonder() {
        if (girisYapanEkipLideri == null) {
            JOptionPane.showMessageDialog(this, "Önce ekip lideri girişi yapınız.");
            return;
        }

        if (ekipLideriSeciliTalep == null) {
            JOptionPane.showMessageDialog(this, "Lütfen bir talep seçiniz.");
            return;
        }

        if (!ekipLideriSeciliTalep.tumCalisanlarTamamladi()) {
            JOptionPane.showMessageDialog(this, "Tüm teknik çalışanlar işi tamamlamadan çözüm müşteriye gönderilemez.");
            return;
        }

        String liderCozumNotu = liderCozumNotArea.getText().trim();

        if (liderCozumNotu.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen müşteriye gönderilecek çözüm notunu yazınız.");
            return;
        }

        ekipLideriSeciliTalep.setTeknikNot("Ekip Lideri Çözüm Notu: " + liderCozumNotu);

        ekipLideriSeciliTalep.ekipLideriBilgiEkle(
                "Ekip lideri çözümü müşteriye gönderdi: "
                        + girisYapanEkipLideri.getAdSoyad()
                        + " | Not: "
                        + liderCozumNotu
        );

        ekipLideriSeciliTalep.cozulduYap();

        xmlTalepServisi.talepleriKaydet(talepler);

        liderCozumNotArea.setText("");

        ekipLideriTalepleriniYenile();
        ekipLideriTalepDetayGoster();

        JOptionPane.showMessageDialog(this, "Çözüm notu müşteriye onay için gönderildi.");
    }

    private void musteriTalepGoruntule() {
        HizmetTalebi talep = musteriTalepIdIleBul();

        if (talep == null) return;

        String sonuc = "";
        sonuc += "Talep Bilgileri\n";
        sonuc += "-------------------------\n";
        sonuc += "Talep ID: " + talep.getTalepId() + "\n";
        sonuc += "Açıklama: " + talep.getAciklama() + "\n";
        sonuc += "Durum: " + talep.getDurum() + "\n";
        sonuc += "Tarih: " + talep.getOlusturmaTarihi() + "\n";
        sonuc += "Ekip Lideri: " + (talep.getEkipLideri() == null ? "Henüz atanmadı" : talep.getEkipLideri().getAdSoyad()) + "\n\n";

        if (talep.getDurum() == TalepDurumu.MUSTERI_YANITI_BEKLIYOR) {
            sonuc += "Ekip lideri sizden ek bilgi istiyor:\n";
            sonuc += talep.getTeknikNot() + "\n\n";
            sonuc += "Cevabınızı aşağıdaki alana yazıp 'Ek Bilgi Gönder' butonuna basınız.\n";
        } else if (talep.getDurum() == TalepDurumu.COZULDU_ONAY_BEKLIYOR) {
            sonuc += "Çözüm ekip lideri tarafından onayınıza gönderildi.\n";
            sonuc += "Çözüm Notu:\n" + talep.getTeknikNot() + "\n\n";
            sonuc += "Çözümü kabul ediyorsanız puan seçip 'Onayla ve Puan Ver' butonuna basınız.\n";
            sonuc += "Sorun devam ediyorsa ret açıklaması yazıp 'Çözümü Reddet' butonuna basınız.\n";
        } else if (talep.getDurum() == TalepDurumu.KAPANDI) {
            sonuc += "Talep kapatılmıştır.\n";
            if (talep.isPuanlandi()) {
                sonuc += "Verilen Puan: " + talep.getPuan() + " / 5\n";
            }
        } else {
            sonuc += "Talebiniz işlem sürecindedir. Gerekli durumda ekip lideri sizinle sistem üzerinden iletişime geçecektir.\n";
        }

        if (!talep.getMusteriCevaplari().isEmpty()) {
            sonuc += "\nGönderdiğiniz Cevaplar:\n" + talep.getMusteriCevaplari() + "\n";
        }

        musteriSonucArea.setText(sonuc);
    }

    private void ekBilgiGonder() {
        HizmetTalebi talep = musteriTalepIdIleBul();
        if (talep == null) return;
        String cevap = musteriEkBilgiArea.getText().trim();
        if (cevap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen ek bilgi cevabını giriniz.");
            return;
        }
        if (talep.getDurum() != TalepDurumu.MUSTERI_YANITI_BEKLIYOR) {
            JOptionPane.showMessageDialog(this, "Bu talep şu anda ek bilgi beklemiyor.");
            return;
        }
        talep.musteriBilgiEkle("Müşteri cevabı: " + cevap);
        talep.setTeknikNot("Müşteri cevabı: " + cevap);
        talep.islemdeYap();
        xmlTalepServisi.talepleriKaydet(talepler);
        JOptionPane.showMessageDialog(this, "Ek bilgi gönderildi. Bilgi ekip lideri ve teknik çalışanlara kaydedildi.");
        musteriEkBilgiArea.setText("");
        if (girisYapanCalisan != null) {
            teknikTalepListesiniYenile();
            seciliTalepDetayGoster();
        }
        if (girisYapanEkipLideri != null) {
            ekipLideriTalepleriniYenile();
            ekipLideriTalepDetayGoster();
        }
    }

    private void puanVer() {
        HizmetTalebi talep = musteriTalepIdIleBul();
        if (talep == null) return;
        if (talep.getDurum() != TalepDurumu.COZULDU_ONAY_BEKLIYOR) {
            JOptionPane.showMessageDialog(this, "Bu talep henüz puanlanamaz. Önce ekip lideri çözümü müşteriye göndermelidir.");
            return;
        }
        int puan = (Integer) puanComboBox.getSelectedItem();
        talep.puanVer(puan);
        bekleyenTalepleriLiderlereAta();
        xmlTalepServisi.talepleriKaydet(talepler);
        JOptionPane.showMessageDialog(this, "Çözüm onaylandı ve " + puan + " yıldız puan verildi.");
        musteriTalepGoruntule();
    }

    private void musteriCozumuReddet() {
        HizmetTalebi talep = musteriTalepIdIleBul();
        if (talep == null) return;
        if (talep.getDurum() != TalepDurumu.COZULDU_ONAY_BEKLIYOR) {
            JOptionPane.showMessageDialog(this, "Bu talep henüz çözüm onayı beklemiyor.");
            return;
        }
        String retAciklamasi = musteriEkBilgiArea.getText().trim();
        if (retAciklamasi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen ret açıklaması yazınız.");
            return;
        }
        talep.musteriBilgiEkle("Müşteri çözümü reddetti: " + retAciklamasi);
        talep.reddet();
        talep.setTeknikNot("Müşteri çözümü reddetti: " + retAciklamasi);
        xmlTalepServisi.talepleriKaydet(talepler);
        if (girisYapanEkipLideri != null) ekipLideriTalepleriniYenile();
        JOptionPane.showMessageDialog(this, "Çözüm reddedildi. Talep tekrar ekip liderine yönlendirildi.");
        musteriTalepGoruntule();
    }

    private HizmetTalebi musteriTalepIdIleBul() {
        String idText = musteriTalepIdField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen Talep ID giriniz.");
            return null;
        }
        int id;
        try {
            id = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Talep ID sayısal olmalıdır.");
            return null;
        }
        for (HizmetTalebi talep : talepler) {
            if (talep.getTalepId() == id) return talep;
        }
        JOptionPane.showMessageDialog(this, "Bu ID ile talep bulunamadı.");
        return null;
    }

    private void teknikGirisYap() {
        String ad = calisanAdiField.getText().trim();
        String sifre = new String(calisanSifreField.getPassword());
        if (!sifre.equals("1234")) {
            JOptionPane.showMessageDialog(this, "Şifre hatalı!");
            return;
        }
        TeknikCalisan bulunan = null;
        for (TeknikCalisan calisan : calisanlar) {
            if (calisan.getAdSoyad().equalsIgnoreCase(ad)) {
                bulunan = calisan;
                break;
            }
        }
        if (bulunan == null) {
            JOptionPane.showMessageDialog(this, "Bu isimde teknik çalışan bulunamadı!");
            return;
        }
        girisYapanCalisan = bulunan;
        JOptionPane.showMessageDialog(this, "Giriş başarılı: " + girisYapanCalisan.getAdSoyad());
        teknikTalepListesiniYenile();
    }

    private void teknikTalepListesiniYenile() {
        talepListModel.clear();
        for (HizmetTalebi talep : talepler) {
            if (talep.getAtananCalisanlar() != null &&
                    talep.getAtananCalisanlar().toLowerCase().contains(girisYapanCalisan.getAdSoyad().toLowerCase()) &&
                    talep.getDurum() != TalepDurumu.KAPANDI) {
                talepListModel.addElement(talep);
            }
        }
        if (talepListModel.isEmpty()) teknikTalepArea.setText("Bu çalışana atanmış aktif talep bulunmamaktadır.");
    }

    private void seciliTalepDetayGoster() {
        if (seciliTalep == null) return;
        String detay = "";
        detay += "Talep ID: " + seciliTalep.getTalepId() + "\n";
        detay += "Müşteri: " + seciliTalep.getMusteri().getAdSoyad() + "\n";
        detay += "Kurum: " + seciliTalep.getMusteri().getKurum() + "\n";
        detay += "Departman: " + seciliTalep.getMusteri().getDepartman() + "\n";
        detay += "Açıklama: " + seciliTalep.getAciklama() + "\n";
        detay += "Durum: " + seciliTalep.getDurum() + "\n";
        detay += "Tarih: " + seciliTalep.getOlusturmaTarihi() + "\n";
        detay += "Ekip Lideri: " + (seciliTalep.getEkipLideri() == null ? "Yok" : seciliTalep.getEkipLideri().getAdSoyad()) + "\n";
        detay += "Gerekli Alanlar: " + seciliTalep.getGerekliAlanlar() + "\n";
        detay += "Atanan Çalışanlar: " + seciliTalep.getAtananCalisanlar() + "\n";
        detay += "\nÇalışan Durumları:\n" + seciliTalep.getCalisanDurumlari() + "\n";
        detay += "\nMüşteri Cevapları:\n" + seciliTalep.getMusteriCevaplari() + "\n";
        detay += "\nTeknik Not: " + seciliTalep.getTeknikNot() + "\n";
        teknikTalepArea.setText(detay);
    }

    

    private void cozulduYap() {
        if (girisYapanCalisan == null) {
            JOptionPane.showMessageDialog(this, "Önce teknik çalışan girişi yapınız.");
            return;
        }

        if (seciliTalep == null) {
            seciliTalep = talepListesi.getSelectedValue();
        }

        if (seciliTalep == null) {
            JOptionPane.showMessageDialog(this, "Lütfen önce listeden bir talep seçiniz.");
            return;
        }

        String calisanAdi = girisYapanCalisan.getAdSoyad();

        if (seciliTalep.getTamamlayanCalisanlar() != null &&
                seciliTalep.getTamamlayanCalisanlar().toLowerCase().contains(calisanAdi.toLowerCase())) {

            JOptionPane.showMessageDialog(this, "Bu talep için işinizi zaten tamamladınız. Tekrar tamamlandı gönderemezsiniz.");
            return;
        }

        String cozumNotu = teknikNotArea.getText().trim();

        if (cozumNotu.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen yaptığınız iş ile ilgili not giriniz.");
            return;
        }

        String bilgi = calisanAdi + " kendi işini tamamladı: " + cozumNotu;

        seciliTalep.setTeknikNot(bilgi);
        seciliTalep.teknikBilgiEkle(bilgi);
        seciliTalep.calisanTamamladi(calisanAdi);

        xmlTalepServisi.talepleriKaydet(talepler);

        boolean herkesBitirdi = seciliTalep.tumCalisanlarTamamladi();

        teknikTalepListesiniYenile();

        seciliTalep = talepListesi.getSelectedValue();

        if (seciliTalep != null) {
            seciliTalepDetayGoster();
        } else {
            teknikTalepArea.setText("İş tamamlama bilginiz ekip liderine gönderildi.");
        }

        if (girisYapanEkipLideri != null) {
            ekipLideriTalepleriniYenile();
            ekipLideriTalepDetayGoster();
        }

        if (herkesBitirdi) {
            JOptionPane.showMessageDialog(this, "Tüm çalışanlar işi tamamladı. Talep artık ekip liderinin müşteri onayına göndermesini bekliyor.");
        } else {
            JOptionPane.showMessageDialog(this, "İş tamamlama bilginiz ekip liderine gönderildi. Diğer çalışanların tamamlaması bekleniyor.");
        }

        teknikNotArea.setText("");
    }

    private void yoneticiGirisYap() {
        String sifre = new String(yoneticiSifreField.getPassword());
        if (sifre.equals("admin123")) {
            yoneticiGirisYapti = true;
            JOptionPane.showMessageDialog(this, "Yönetici girişi başarılı.");
        } else {
            yoneticiGirisYapti = false;
            JOptionPane.showMessageDialog(this, "Yönetici şifresi hatalı!");
        }
    }

    private void raporOlustur() {
        if (!yoneticiGirisYapti) {
            JOptionPane.showMessageDialog(this, "Önce yönetici girişi yapınız.");
            return;
        }

        int toplam = talepler.size();
        int liderBekliyor = 0, liderde = 0, teknikAtandi = 0, islemde = 0;
        int calisanlarBitirdi = 0, liderOnayBekliyor = 0, musteriYanitiBekliyor = 0;
        int cozulduOnayBekliyor = 0, kapandi = 0;

        for (HizmetTalebi talep : talepler) {
            if (talep.getDurum() == TalepDurumu.LIDER_ATAMA_BEKLIYOR) liderBekliyor++;
            else if (talep.getDurum() == TalepDurumu.EKIP_LIDERINDE) liderde++;
            else if (talep.getDurum() == TalepDurumu.TEKNIK_CALISANLARA_ATANDI) teknikAtandi++;
            else if (talep.getDurum() == TalepDurumu.ISLEMDE) islemde++;
            else if (talep.getDurum() == TalepDurumu.CALISANLAR_ISI_TAMAMLADI) calisanlarBitirdi++;
            else if (talep.getDurum() == TalepDurumu.EKIP_LIDERI_ONAY_BEKLIYOR) liderOnayBekliyor++;
            else if (talep.getDurum() == TalepDurumu.MUSTERI_YANITI_BEKLIYOR) musteriYanitiBekliyor++;
            else if (talep.getDurum() == TalepDurumu.COZULDU_ONAY_BEKLIYOR) cozulduOnayBekliyor++;
            else if (talep.getDurum() == TalepDurumu.KAPANDI) kapandi++;
        }

        String rapor = "";
        rapor += "BAKIM VE ONARIM BİLGİ SİSTEMİ RAPORU\n";
        rapor += "--------------------------------------\n";
        rapor += "Toplam Talep Sayısı: " + toplam + "\n";
        rapor += "Lider Atama Bekleyen: " + liderBekliyor + "\n";
        rapor += "Ekip Liderinde Bekleyen: " + liderde + "\n";
        rapor += "Teknik Çalışanlara Atanmış: " + teknikAtandi + "\n";
        rapor += "İşlemde Olan: " + islemde + "\n";
        rapor += "Çalışanlar İşi Tamamladı: " + calisanlarBitirdi + "\n";
        rapor += "Ekip Lideri Onayı Bekleyen: " + liderOnayBekliyor + "\n";
        rapor += "Müşteri Yanıtı Bekleyen: " + musteriYanitiBekliyor + "\n";
        rapor += "Çözüldü - Müşteri Onayı Bekleyen: " + cozulduOnayBekliyor + "\n";
        rapor += "Kapalı Talep: " + kapandi + "\n\n";

        rapor += "DEPARTMAN BAZLI PERFORMANS\n";
        rapor += "--------------------------------------\n";

        for (String alan : hizmetAlanlari) {
            int alanToplam = 0, alanKapali = 0, alanRet = 0, alanPuanToplam = 0, alanPuanSayisi = 0;
            for (HizmetTalebi talep : talepler) {
                if (talep.getGerekliAlanlar() != null && talep.getGerekliAlanlar().toLowerCase().contains(alan.toLowerCase())) {
                    alanToplam++;
                    if (talep.getDurum() == TalepDurumu.KAPANDI) alanKapali++;
                    alanRet += talep.getRetSayisi();
                    if (talep.isPuanlandi()) {
                        alanPuanToplam += talep.getPuan();
                        alanPuanSayisi++;
                    }
                }
            }
            double isYukuOrani = toplam == 0 ? 0 : (alanToplam * 100.0) / toplam;
            double ortPuan = alanPuanSayisi == 0 ? 0 : (double) alanPuanToplam / alanPuanSayisi;
            rapor += alan
                    + " | Toplam İş: " + alanToplam
                    + " | Genel İş Yükü: %" + String.format("%.2f", isYukuOrani)
                    + " | Kapalı: " + alanKapali
                    + " | Reddedilen Çözüm: " + alanRet
                    + " | Ortalama Puan: " + String.format("%.2f", ortPuan) + " / 5\n";
        }

        rapor += "\nÇALIŞANLARA GÖRE TALEP SAYILARI VE PUANLAR\n";
        rapor += "--------------------------------------\n";
        for (TeknikCalisan calisan : calisanlar) {
            int sayac = 0, toplamPuan = 0, puanSayisi = 0, retSayisi = 0;
            for (HizmetTalebi talep : talepler) {
                if (talep.getAtananCalisanlar() != null && talep.getAtananCalisanlar().toLowerCase().contains(calisan.getAdSoyad().toLowerCase())) {
                    sayac++;
                    retSayisi += talep.getRetSayisi();
                    if (talep.isPuanlandi()) {
                        toplamPuan += talep.getPuan();
                        puanSayisi++;
                    }
                }
            }
            double ortalama = puanSayisi == 0 ? 0 : (double) toplamPuan / puanSayisi;
            rapor += calisan.getAdSoyad()
                    + " (" + calisan.getAlan() + ")"
                    + " | Talep: " + sayac
                    + " | Ortalama Puan: " + String.format("%.2f", ortalama) + " / 5"
                    + " | Değerlendirme: " + puanSayisi
                    + " | Reddedilen Çözüm: " + retSayisi + "\n";
        }
        yoneticiRaporArea.setText(rapor);
    }

    private void calisanListesiniYenile() {
        if (calisanListModel == null) return;
        calisanListModel.clear();
        for (TeknikCalisan calisan : calisanlar) calisanListModel.addElement(calisan);
    }

    private void comboBoxlariYenile() {
        if (yeniCalisanAlanComboBox != null) {
            yeniCalisanAlanComboBox.removeAllItems();
            for (String alan : hizmetAlanlari) yeniCalisanAlanComboBox.addItem(alan);
        }
    }

    private void calisanEkle() {
        if (!yoneticiGirisYapti) {
            JOptionPane.showMessageDialog(this, "Önce yönetici girişi yapınız.");
            return;
        }
        String ad = yeniCalisanAdField.getText().trim();
        if (yeniCalisanAlanComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Lütfen hizmet alanı seçiniz.");
            return;
        }
        String alan = yeniCalisanAlanComboBox.getSelectedItem().toString();
        if (ad.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Çalışan adı boş olamaz.");
            return;
        }
        calisanlar.add(new TeknikCalisan(ad, alan, 0));
        xmlPersonelServisi.calisanlariKaydet(calisanlar);
        calisanListesiniYenile();
        yeniCalisanAdField.setText("");
        JOptionPane.showMessageDialog(this, "Yeni çalışan eklendi ve XML dosyasına kaydedildi.");
    }

    private void calisanSil() {
        if (!yoneticiGirisYapti) {
            JOptionPane.showMessageDialog(this, "Önce yönetici girişi yapınız.");
            return;
        }
        TeknikCalisan secili = calisanListesi.getSelectedValue();
        if (secili == null) {
            JOptionPane.showMessageDialog(this, "Lütfen silinecek çalışanı seçiniz.");
            return;
        }
        calisanlar.remove(secili);
        xmlPersonelServisi.calisanlariKaydet(calisanlar);
        calisanListesiniYenile();
        JOptionPane.showMessageDialog(this, "Çalışan silindi ve XML güncellendi.");
    }

    private void calisanGuncelle() {
        if (!yoneticiGirisYapti) {
            JOptionPane.showMessageDialog(this, "Önce yönetici girişi yapınız.");
            return;
        }
        TeknikCalisan secili = calisanListesi.getSelectedValue();
        if (secili == null) {
            JOptionPane.showMessageDialog(this, "Lütfen güncellenecek çalışanı seçiniz.");
            return;
        }
        String yeniAd = yeniCalisanAdField.getText().trim();
        if (yeniCalisanAlanComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Lütfen hizmet alanı seçiniz.");
            return;
        }
        String yeniAlan = yeniCalisanAlanComboBox.getSelectedItem().toString();
        if (yeniAd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Yeni ad boş olamaz.");
            return;
        }
        String eskiAd = secili.getAdSoyad();
        secili.setAdSoyad(yeniAd);
        secili.setAlan(yeniAlan);
        for (HizmetTalebi talep : talepler) {
            if (talep.getAtananCalisanlar() != null && talep.getAtananCalisanlar().toLowerCase().contains(eskiAd.toLowerCase())) {
                String yeniAtama = talep.getAtananCalisanlar().replace(eskiAd, yeniAd);
                talep.setAtananCalisanlar(yeniAtama);
            }
        }
        xmlPersonelServisi.calisanlariKaydet(calisanlar);
        xmlTalepServisi.talepleriKaydet(talepler);
        calisanListesiniYenile();
        if (girisYapanCalisan != null && girisYapanCalisan.getAdSoyad().equalsIgnoreCase(eskiAd)) {
            girisYapanCalisan = secili;
            teknikTalepListesiniYenile();
        }
        JOptionPane.showMessageDialog(this, "Çalışan bilgileri güncellendi ve XML dosyaları yenilendi.");
    }

    private void hizmetAlaniEkle() {
        if (!yoneticiGirisYapti) {
            JOptionPane.showMessageDialog(this, "Önce yönetici girişi yapınız.");
            return;
        }
        String yeniAlan = yeniAlanField.getText().trim();
        if (yeniAlan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hizmet alanı boş olamaz.");
            return;
        }
        for (String alan : hizmetAlanlari) {
            if (alan.equalsIgnoreCase(yeniAlan)) {
                JOptionPane.showMessageDialog(this, "Bu hizmet alanı zaten var.");
                return;
            }
        }
        hizmetAlanlari.add(yeniAlan);
        xmlPersonelServisi.hizmetAlanlariKaydet(hizmetAlanlari);
        comboBoxlariYenile();
        yeniAlanField.setText("");
        JOptionPane.showMessageDialog(this, "Yeni hizmet alanı eklendi ve XML dosyasına kaydedildi.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BakimYonetimGUI().setVisible(true);
        });
    }
}
