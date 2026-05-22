public class EkipLideri {
    private String adSoyad;
    private int aktifTalepSayisi;

    public EkipLideri(String adSoyad, int aktifTalepSayisi) {
        this.adSoyad = adSoyad;
        this.aktifTalepSayisi = aktifTalepSayisi;
    }

    public String getAdSoyad() {
        return adSoyad;
    }

    public int getAktifTalepSayisi() {
        return aktifTalepSayisi;
    }

    public void talepEkle() {
        aktifTalepSayisi++;
    }

    @Override
    public String toString() {
        return adSoyad + " (" + aktifTalepSayisi + " aktif talep)";
    }
}