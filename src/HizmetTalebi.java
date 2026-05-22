/*
 * Bakım ve Onarım Bilgi Sistemi
 * BLM3722 - Yazilim Muhendisligi
 *
 * Bu sınıf müşteri, ekip lideri, teknik çalışan ve yönetici panellerini içerir.
 * Müşteri talep oluşturur, ekip lideri departman/çalışan ataması yapar,
 * teknik çalışan işi tamamlar veya ek bilgi ister, yönetici rapor görüntüler.
 */





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

    public void calisanIsiBitirdi() {
        durum = TalepDurumu.CALISANLAR_ISI_TAMAMLADI;
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