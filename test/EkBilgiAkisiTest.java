import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

//Ek Bilgi İsteme ve Eksik Bilgiyi Sağlama
@DisplayName("Ek Bilgi İsteme & Sağlama")
public class EkBilgiAkisiTest {

    private Musteri musteri;
    private EkipLideri lider;
    private TeknikCalisan calisan;
    private HizmetTalebi talep;

    @BeforeEach
    void setUp() {
        musteri = TestHelper.standartMusteri();
        lider = TestHelper.musaitLider("Lider Ayşe");
        calisan = TestHelper.teknikCalisan("Ali Teknik", "Donanim", 1);

        talep = new HizmetTalebi("Donanim",
                "Bilgisayar kendiliğinden kapanıyor, sebebi belirsiz", musteri);
        talep.setEkipLideri(lider);
        talep.calisanaAta(calisan); // durum → ISLEMDE
    }

    // ── Test 1 ──────────────────────────────────────────────────────────────────
    /**
     Ana Senaryo: Teknik çalışan, müşteriden ek bilgi talep eder.
     Beklenen sonuç:
        Talebin durumu MUSTERI_YANITI_BEKLIYOR olur.
        Teknik not ve çalışan durumları güncellenir.
        Mesaj içeriği kayıtlarda görünür.
     **/
    @Test
    @DisplayName("Ek bilgi istendiğinde durum MUSTERI_YANITI_BEKLIYOR olmalı ve mesaj kaydedilmeli")
    void ekBilgiIstendigindeDurumVeMesajGuncellenmeli() {
        // Arrange
        assertEquals(TalepDurumu.ISLEMDE, talep.getDurum(),
                "Ön koşul: talep ISLEMDE olmalı");
        String mesaj = "Ali Teknik ek bilgi istedi: Kapanma hangi sıklıkta yaşanıyor?";

        // Act
        talep.setTeknikNot(mesaj);
        talep.teknikBilgiEkle(mesaj);
        talep.musteriYanitiBekliyorYap();

        // Assert
        assertEquals(TalepDurumu.MUSTERI_YANITI_BEKLIYOR, talep.getDurum(),
                "Ek bilgi istenince durum MUSTERI_YANITI_BEKLIYOR olmalı");
        assertTrue(talep.getTeknikNot().contains("ek bilgi istedi"),
                "Teknik not mesajı içermeli");
        assertTrue(talep.getCalisanDurumlari().contains("Kapanma hangi sıklıkta"),
                "Çalışan durumlarına mesaj eklenmeli");
    }

    // ── Test 2 ──────────────────────────────────────────────────────────────────
    /**
     Ana Senaryo: Müşteri istenen ek bilgiyi sağlar.
     Ön koşul: Talep MUSTERI_YANITI_BEKLIYOR durumundadır.
     Beklenen sonuç:
        Talebin durumu tekrar ISLEMDE olur.
        Müşterinin cevabı musteriCevaplari alanına kaydedilir.
        Teknik not müşteri cevabı ile güncellenir.
     **/
    @Test
    @DisplayName("Müşteri ek bilgi sağladığında durum ISLEMDE olmalı ve cevap kaydedilmeli")
    void musteriEkBilgiSagladigindaDurumIslemdeOlmaliVeCevapKaydedilmeli() {
        talep.musteriYanitiBekliyorYap(); //önce ek bilgi istenir
        assertEquals(TalepDurumu.MUSTERI_YANITI_BEKLIYOR, talep.getDurum());

        String musteriCevabi = "Günde 3-4 kez, özellikle ağır programlar açıkken kapanıyor";

        //müşteri cevap verir
        talep.musteriBilgiEkle("Müşteri cevabı: " + musteriCevabi);
        talep.setTeknikNot("Müşteri cevabı: " + musteriCevabi);
        talep.islemdeYap();

        assertEquals(TalepDurumu.ISLEMDE, talep.getDurum(),
                "Cevap sonrası durum tekrar ISLEMDE olmalı");
        assertTrue(talep.getMusteriCevaplari().contains("ağır programlar açıkken"),
                "Müşteri cevabı kayıtlara geçmeli");
        assertTrue(talep.getTeknikNot().contains(musteriCevabi),
                "Teknik not müşteri cevabını içermeli");
    }
}
