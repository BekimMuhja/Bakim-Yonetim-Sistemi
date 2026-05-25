import java.util.ArrayList;
import java.util.List;
public class TestHelper {

    //Testler yazılırken kolaylık sağlaması açısından oluşturulmuşlardır.

    //Kurumsal müşteri oluşturur
    public static Musteri standartMusteri() {
        return new Musteri("Ahmet Yılmaz", "ABC Teknoloji", "Bilgi İşlem");
    }

    //Müşteri oluşturur
    public static Musteri musteri(String adSoyad, String kurum, String departman) {
        return new Musteri(adSoyad, kurum, departman);
    }

    //Teknik çalışan oluşturur
    public static TeknikCalisan teknikCalisan(String adSoyad, String alan, int aktifTalep) {
        return new TeknikCalisan(adSoyad, alan, aktifTalep);
    }

    //Müsait (0 aktif talepli) teknik çalışan oluşturur
    public static TeknikCalisan musaitCalisan(String adSoyad, String alan) {
        return new TeknikCalisan(adSoyad, alan, 0);
    }

    //Müsait ekip lideri oluşturur
    public static EkipLideri musaitLider(String adSoyad) {
        return new EkipLideri(adSoyad, 0);
    }

    //Varsayılan çalışan listesi oluşturur
    public static List<TeknikCalisan> ornekCalisanListesi() {
        List<TeknikCalisan> list = new ArrayList<>();
        list.add(new TeknikCalisan("Ali Teknik", "Donanim", 1));
        list.add(new TeknikCalisan("Mehmet Usta", "Donanim", 3));
        list.add(new TeknikCalisan("Ayşe Uzman", "Ag", 2));
        list.add(new TeknikCalisan("Fatma Yazılımcı", "Yazilim", 0));
        return list;
    }

    //Atama servisi döndürür
    public static AtamaServisi atamaServisi() {
        return new AtamaServisi();
    }
}
