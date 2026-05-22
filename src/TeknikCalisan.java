public class TeknikCalisan {
    private String adSoyad;
    private String alan;
    private int aktifTalepSayisi;

    public TeknikCalisan(String adSoyad, String alan, int aktifTalepSayisi) {
        this.adSoyad = adSoyad;
        this.alan = alan;
        this.aktifTalepSayisi = aktifTalepSayisi;
    }

    public String getAdSoyad() {
        return adSoyad;
    }

    public String getAlan() {
        return alan;
    }

    public int getAktifTalepSayisi() {
        return aktifTalepSayisi;
    }

    public void setAdSoyad(String adSoyad) {
        this.adSoyad = adSoyad;
    }

    public void setAlan(String alan) {
        this.alan = alan;
    }

    public void talepEkle() {
        aktifTalepSayisi++;
    }

    @Override
    public String toString() {
        return adSoyad + " - " + alan + " (" + aktifTalepSayisi + " aktif talep)";
    }
}