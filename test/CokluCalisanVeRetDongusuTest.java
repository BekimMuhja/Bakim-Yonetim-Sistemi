import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Çoklu Çalışan Tamamlama & Yük Dengeleme")
public class CokluCalisanVeRetDongusuTest {

    private Musteri musteri;
    private EkipLideri lider;
    private HizmetTalebi talep;
    private AtamaServisi atamaServisi;

    @BeforeEach
    void setUp() {
        musteri = TestHelper.standartMusteri();
        lider = TestHelper.musaitLider("Lider Mehmet");
        atamaServisi = TestHelper.atamaServisi();
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
        // 3 çalışan atanıyor (AtamaServisi'nin ürettiği formatta)
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
     Senaryo: Aynı alandan iki çalışan var, iş yükleri farklı.
     Ardı ardına 4 talep geldiğinde AtamaServisi her seferinde en az
     yüklü çalışanı seçmeli; böylece iş yükü eşit dağılmalı.

     Başlangıç: Ali (0 aktif talep), Mehmet (0 aktif talep)
     Beklenen dağılım: 4 talep sonunda her biri 2'şer talep almış olmalı.
     Hiçbiri diğerinden 1'den fazla fazla talep taşımamalı.

     Bu test IncelemeVeCalisanAtamaTest'ten farklıdır: orada tek bir
     atama yapılır; burada çok sayıda ardışık atama sonucunda
     birikimli iş yükü dengesi ölçülür.
     **/
    @Test
    @DisplayName("Çok talep geldiğinde AtamaServisi iş yükünü çalışanlar arasında dengeli dağıtmalı")
    void cokTalepGeldigindeCalisanlaraEsitYukDagitilmali() {
        TeknikCalisan ali    = TestHelper.musaitCalisan("Ali Teknik", "Donanim");
        TeknikCalisan mehmet = TestHelper.musaitCalisan("Mehmet Usta", "Donanim");

        java.util.List<TeknikCalisan> havuz = new java.util.ArrayList<>();
        havuz.add(ali);
        havuz.add(mehmet);

        // 4 talep ardarda gelir; her atama çalışanın sayacını artırır
        for (int i = 1; i <= 4; i++) {
            HizmetTalebi t = new HizmetTalebi("Donanim", "Ariza " + i, musteri);
            TeknikCalisan secilen = atamaServisi.uygunCalisanBul("Donanim", havuz);
            assertNotNull(secilen, "Talep " + i + " için uygun çalışan bulunmalı");
            t.calisanaAta(secilen);
        }

        int aliYuk    = ali.getAktifTalepSayisi();
        int mehmetYuk = mehmet.getAktifTalepSayisi();

        assertEquals(4, aliYuk + mehmetYuk,
                "Toplam 4 talep ikisi arasında paylaşılmış olmalı");
        assertTrue(Math.abs(aliYuk - mehmetYuk) <= 1,
                "İş yükü farkı en fazla 1 olmalı; Ali=" + aliYuk + " Mehmet=" + mehmetYuk);
    }
}