public class Musteri {
    private String adSoyad;
    private String kurum;
    private String departman;

    public Musteri(String adSoyad, String kurum, String departman) {
        this.adSoyad = adSoyad;
        this.kurum = kurum;
        this.departman = departman;
    }

    public String getAdSoyad() {
        return adSoyad;
    }

    public String getKurum() {
        return kurum;
    }

    public String getDepartman() {
        return departman;
    }
}