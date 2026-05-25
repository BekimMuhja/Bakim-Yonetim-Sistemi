import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kısmi Tamamlama & Eksik Çalışanla Atama")
public class KismiTamamlamaVeEksikAtamaTest {

    private Musteri musteri;
    private EkipLideri lider;
    private HizmetTalebi talep;
    private AtamaServisi atamaServisi;
    private java.util.List<TeknikCalisan> calisanlar;

    @BeforeEach
    void setUp() {
        musteri = TestHelper.standartMusteri();
        lider = TestHelper.musaitLider("Lider Mehmet");
        atamaServisi = TestHelper.atamaServisi();
        calisanlar = TestHelper.ornekCalisanListesi();
        talep = new HizmetTalebi("Donanim",
                "Sunucu odasında hem donanım hem ağ hem yazılım sorunu var", musteri);
        talep.setEkipLideri(lider);
    }

    // ── Test 1 ──────────────────────────────────────────────────────────────────
    /**
     Senaryo: 3 farklı departmandan çalışan atanmış bir talep var.
     Çalışanlar birer birer işi bitiriyor:
        1) Birinci bitirince durum hâlâ TEKNIK_CALISANLARA_ATANDI kalmalı.
        2) İkinci bitirince durum yine TEKNIK_CALISANLARA_ATANDI kalmalı.
        3) Üçüncü bitirince durum CALISANLAR_ISI_TAMAMLADI olmalı.
     Bu test, tumCalisanlarTamamladi() mantığının kısmi tamamlamada
     erken CALISANLAR_ISI_TAMAMLADI üretmediğini doğrular.
     **/
    @Test
    @DisplayName("3 çalışandan yalnızca biri veya ikisi bitirdiğinde durum değişmemeli, hepsi bitirince CALISANLAR_ISI_TAMAMLADI olmalı")
    void kismiTamamlamaErkenDurumDegisikligineYolAcmamali() {
        talep.setAtananCalisanlar(
                "Ali Teknik (Donanim), Ayşe Uzman (Ag), Fatma Yazılımcı (Yazilim),");

        assertEquals(TalepDurumu.TEKNIK_CALISANLARA_ATANDI, talep.getDurum(),
                "Atama sonrası başlangıç durumu TEKNIK_CALISANLARA_ATANDI olmalı");
        assertFalse(talep.tumCalisanlarTamamladi(),
                "Henüz kimse bitirmemişken tumCalisanlarTamamladi false olmalı");

        // Birinci çalışan bitirir
        talep.calisanTamamladi("Ali Teknik");

        assertEquals(TalepDurumu.TEKNIK_CALISANLARA_ATANDI, talep.getDurum(),
                "1/3 tamamlandığında durum hâlâ TEKNIK_CALISANLARA_ATANDI olmalı");
        assertFalse(talep.tumCalisanlarTamamladi(),
                "1/3 tamamlandığında tumCalisanlarTamamladi false olmalı");

        // İkinci çalışan bitirir
        talep.calisanTamamladi("Ayşe Uzman");

        assertEquals(TalepDurumu.TEKNIK_CALISANLARA_ATANDI, talep.getDurum(),
                "2/3 tamamlandığında durum hâlâ TEKNIK_CALISANLARA_ATANDI olmalı");
        assertFalse(talep.tumCalisanlarTamamladi(),
                "2/3 tamamlandığında tumCalisanlarTamamladi false olmalı");

        // Üçüncü çalışan bitirir → hepsi bitti
        talep.calisanTamamladi("Fatma Yazılımcı");

        assertEquals(TalepDurumu.CALISANLAR_ISI_TAMAMLADI, talep.getDurum(),
                "3/3 tamamlandığında durum CALISANLAR_ISI_TAMAMLADI olmalı");
        assertTrue(talep.tumCalisanlarTamamladi(),
                "Tüm çalışanlar bitirince tumCalisanlarTamamladi true olmalı");
        assertTrue(talep.getTamamlayanCalisanlar().contains("Ali Teknik"),
                "Ali Teknik tamamlayanlar listesinde olmalı");
        assertTrue(talep.getTamamlayanCalisanlar().contains("Fatma Yazılımcı"),
                "Fatma Yazılımcı tamamlayanlar listesinde olmalı");
    }

    // ── Test 2 ──────────────────────────────────────────────────────────────────
    /**
     Senaryo: Ekip lideri Donanım departmanından 3 kişi talep eder,
     ancak havuzda yalnızca 2 Donanım çalışanı vardır.

     Beklenen sonuç:
        - Mevcut 2 çalışan atanır, "Eksik çalışan: 1 kişi" notu eklenir.
        - Eksik kadro olsa bile talep işleme alınır:
          durum EKIP_LIDERI_ONAY_BEKLIYOR olur.
        - atananCalisanlar hem gerçek çalışanları hem eksik notu içerir.
     **/
    @Test
    @DisplayName("Departmanda eksik çalışan olduğunda mevcut kadro atanmalı ve talep yine de işleme alınmalı")
    void eksikCalisanlaAtamaYapildigindaTalepIslemAlinmali() {
        // ornekCalisanListesi: Donanım'da Ali Teknik (1) ve Mehmet Usta (3) var → toplam 2 kişi
        // 3 kişi isteniyor → 1 eksik kalacak
        java.util.List<String> alanlar = java.util.List.of("Donanim");
        java.util.List<Integer> kisiSayilari = java.util.List.of(3);

        String sonuc = atamaServisi.departmanlaraGoreCalisanAta(
                talep, alanlar, kisiSayilari, calisanlar);

        // Mevcut 2 Donanım çalışanı atanmış olmalı
        assertTrue(sonuc.contains("Ali Teknik"),
                "Mevcut Donanım çalışanı Ali Teknik atanmalı");
        assertTrue(sonuc.contains("Mehmet Usta"),
                "Mevcut Donanım çalışanı Mehmet Usta atanmalı");

        // Eksik kadro notu eklenmiş olmalı
        assertTrue(sonuc.contains("Eksik çalışan"),
                "Karşılanamayan kota için 'Eksik çalışan' notu olmalı");

        // Eksik kadro olsa bile talep işleme alınmalı
        assertEquals(TalepDurumu.EKIP_LIDERI_ONAY_BEKLIYOR, talep.getDurum(),
                "Eksik çalışan olsa bile talep EKIP_LIDERI_ONAY_BEKLIYOR durumuna geçmeli");
    }
}
