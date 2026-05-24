import java.time.LocalDate;

public class HizmetTalebi {

    private static int sayac = 1000;

    private int talepId;

    private String alan;
    private String aciklama;

    private TalepDurumu durum;
    private LocalDate olusturmaTarihi;

    private Musteri musteri;
    private TeknikCalisan atananCalisan;

    private String teknikNot;

    private EkipLideri ekipLideri;

    private String gerekliAlanlar;
    private String atananCalisanlar;

    private String calisanDurumlari = "";
    private String ekipLideriNotlari = "";
    private String musteriCevaplari = "";
    private String tamamlayanCalisanlar = "";

    private int retSayisi = 0;

    private int puan = 0;
    private boolean puanlandi = false;

    public HizmetTalebi(String alan, String aciklama, Musteri musteri) {
        this(
                ++sayac,
                alan,
                aciklama,
                musteri,
                TalepDurumu.EKIP_LIDERINDE,
                LocalDate.now(),
                null,
                "",
                null,
                "",
                ""
        );
    }

    public HizmetTalebi(
            int talepId,
            String alan,
            String aciklama,
            Musteri musteri,
            TalepDurumu durum,
            LocalDate olusturmaTarihi,
            TeknikCalisan atananCalisan,
            String teknikNot,
            EkipLideri ekipLideri,
            String gerekliAlanlar,
            String atananCalisanlar) {

        this.talepId = talepId;
        this.alan = alan;
        this.aciklama = aciklama;
        this.musteri = musteri;
        this.durum = durum;
        this.olusturmaTarihi = olusturmaTarihi;
        this.atananCalisan = atananCalisan;
        this.teknikNot = teknikNot == null ? "" : teknikNot;
        this.ekipLideri = ekipLideri;
        this.gerekliAlanlar = gerekliAlanlar == null ? "" : gerekliAlanlar;
        this.atananCalisanlar = atananCalisanlar == null ? "" : atananCalisanlar;

        if (talepId > sayac) {
            sayac = talepId;
        }
    }

    public int getTalepId() {
        return talepId;
    }

    public String getAlan() {
        return alan;
    }

    public String getAciklama() {
        return aciklama;
    }

    public TalepDurumu getDurum() {
        return durum;
    }

    public LocalDate getOlusturmaTarihi() {
        return olusturmaTarihi;
    }

    public Musteri getMusteri() {
        return musteri;
    }

    public TeknikCalisan getAtananCalisan() {
        return atananCalisan;
    }

    public String getTeknikNot() {
        return teknikNot;
    }

    public EkipLideri getEkipLideri() {
        return ekipLideri;
    }

    public String getGerekliAlanlar() {
        return gerekliAlanlar;
    }

    public String getAtananCalisanlar() {
        return atananCalisanlar;
    }

    public String getCalisanDurumlari() {
        return calisanDurumlari;
    }

    public String getEkipLideriNotlari() {
        return ekipLideriNotlari;
    }

    public String getMusteriCevaplari() {
        return musteriCevaplari;
    }

    public String getTamamlayanCalisanlar() {
        return tamamlayanCalisanlar;
    }

    public int getRetSayisi() {
        return retSayisi;
    }

    public int getPuan() {
        return puan;
    }

    public boolean isPuanlandi() {
        return puanlandi;
    }

    public void puanVer(int puan) {
        this.puan = puan;
        this.puanlandi = true;
        this.durum = TalepDurumu.KAPANDI;
    }

    public void setTeknikNot(String teknikNot) {
        this.teknikNot = teknikNot == null ? "" : teknikNot;
    }

    public void setEkipLideri(EkipLideri ekipLideri) {
        this.ekipLideri = ekipLideri;
        this.durum = TalepDurumu.EKIP_LIDERINDE;

        if (ekipLideri != null) {
            ekipLideri.talepEkle();
        }
    }

    public void setGerekliAlanlar(String gerekliAlanlar) {
        this.gerekliAlanlar = gerekliAlanlar == null ? "" : gerekliAlanlar;
    }

    public void setAtananCalisanlar(String atananCalisanlar) {
        this.atananCalisanlar = atananCalisanlar == null ? "" : atananCalisanlar;
        this.durum = TalepDurumu.TEKNIK_CALISANLARA_ATANDI;
    }

    public void teknikBilgiEkle(String bilgi) {
        calisanDurumlari += bilgi + "\n";
    }

    public void ekipLideriBilgiEkle(String bilgi) {
        ekipLideriNotlari += bilgi + "\n";
    }

    public void musteriBilgiEkle(String bilgi) {
        musteriCevaplari += bilgi + "\n";
    }

    public void calisanTamamladi(String calisanAdi) {
        if (!tamamlayanCalisanlar.toLowerCase().contains(calisanAdi.toLowerCase())) {
            tamamlayanCalisanlar += calisanAdi + ",";
        }

        if (tumCalisanlarTamamladi()) {
            durum = TalepDurumu.CALISANLAR_ISI_TAMAMLADI;
        } else {
            durum = TalepDurumu.TEKNIK_CALISANLARA_ATANDI;
        }
    }

    public boolean tumCalisanlarTamamladi() {
        if (atananCalisanlar == null || atananCalisanlar.isEmpty()) {
            return false;
        }

        String[] atananlar = atananCalisanlar.split(",");

        for (String kisi : atananlar) {
            kisi = kisi.trim();

            if (kisi.isEmpty()) {
                continue;
            }

            if (kisi.toLowerCase().contains("uygun çalışan yok")) {
                continue;
            }

            if (kisi.toLowerCase().contains("eksik çalışan")) {
                continue;
            }

            int index = kisi.indexOf("(");

            if (index != -1) {
                kisi = kisi.substring(0, index).trim();
            }

            if (!tamamlayanCalisanlar.toLowerCase().contains(kisi.toLowerCase())) {
                return false;
            }
        }

        return true;
    }

    public void ekipLiderineOnayaGonder() {
        durum = TalepDurumu.EKIP_LIDERI_ONAY_BEKLIYOR;
    }

    public void cozulduYap() {
        durum = TalepDurumu.COZULDU_ONAY_BEKLIYOR;
    }

    public void islemdeYap() {
        durum = TalepDurumu.ISLEMDE;
    }

    public void musteriYanitiBekliyorYap() {
        durum = TalepDurumu.MUSTERI_YANITI_BEKLIYOR;
    }

    public void liderAtamaBekliyorYap() {
        durum = TalepDurumu.LIDER_ATAMA_BEKLIYOR;
        ekipLideri = null;
    }

    public void onayla() {
        durum = TalepDurumu.KAPANDI;
    }

    public void reddet() {
        retSayisi++;
        durum = TalepDurumu.EKIP_LIDERINDE;
        atananCalisan = null;
        gerekliAlanlar = "";
        atananCalisanlar = "";
        tamamlayanCalisanlar = "";
        teknikNot = "Müşteri reddetti.";
    }

    public void calisanaAta(TeknikCalisan calisan) {
        this.atananCalisan = calisan;
        this.durum = TalepDurumu.ISLEMDE;

        if (calisan != null) {
            calisan.talepEkle();
        }
    }

    public void atamaBekliyorYap() {
        this.durum = TalepDurumu.ATAMA_BEKLIYOR;
    }

    @Override
    public String toString() {
        return "Talep #" + talepId + " - " + durum;
    }
}