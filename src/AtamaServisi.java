/*
 * Bakım ve Onarım Bilgi Sistemi
 * BLM3722 - Yazilim Muhendisligi
 *
 * Bu sınıf müşteri, ekip lideri, teknik çalışan ve yönetici panellerini içerir.
 * Müşteri talep oluşturur, ekip lideri departman/çalışan ataması yapar,
 * teknik çalışan işi tamamlar veya ek bilgi ister, yönetici rapor görüntüler.
 */









import java.util.List;
import java.util.ArrayList;

public class AtamaServisi {

    public TeknikCalisan uygunCalisanBul(String alan, List<TeknikCalisan> calisanlar) {
        TeknikCalisan secilen = null;

        for (TeknikCalisan calisan : calisanlar) {
            if (calisan.getAlan().equalsIgnoreCase(alan)) {
                if (secilen == null ||
                        calisan.getAktifTalepSayisi() < secilen.getAktifTalepSayisi()) {
                    secilen = calisan;
                }
            }
        }

        return secilen;
    }

    public List<TeknikCalisan> uygunCalisanlariBul(String alan, int kisiSayisi, List<TeknikCalisan> calisanlar) {
        List<TeknikCalisan> sonuc = new ArrayList<>();
        List<TeknikCalisan> kopyaListe = new ArrayList<>();

        for (TeknikCalisan calisan : calisanlar) {
            if (calisan.getAlan().equalsIgnoreCase(alan)) {
                kopyaListe.add(calisan);
            }
        }

        for (int i = 0; i < kisiSayisi; i++) {
            TeknikCalisan secilen = null;

            for (TeknikCalisan calisan : kopyaListe) {
                if (!sonuc.contains(calisan)) {
                    if (secilen == null ||
                            calisan.getAktifTalepSayisi() < secilen.getAktifTalepSayisi()) {
                        secilen = calisan;
                    }
                }
            }

            if (secilen != null) {
                sonuc.add(secilen);
            }
        }

        return sonuc;
    }

    public String departmanlaraGoreCalisanAta(HizmetTalebi talep,
                                               List<String> secilenAlanlar,
                                               List<Integer> kisiSayilari,
                                               List<TeknikCalisan> calisanlar) {

        StringBuilder gerekliAlanlar = new StringBuilder();
        StringBuilder atananCalisanlar = new StringBuilder();

        for (int i = 0; i < secilenAlanlar.size(); i++) {
            String alan = secilenAlanlar.get(i);
            int kisiSayisi = kisiSayilari.get(i);

            gerekliAlanlar
                    .append(alan)
                    .append(" (")
                    .append(kisiSayisi)
                    .append(" kişi), ");

            List<TeknikCalisan> secilenCalisanlar =
                    uygunCalisanlariBul(alan, kisiSayisi, calisanlar);

            if (secilenCalisanlar.isEmpty()) {
                atananCalisanlar
                        .append("Uygun çalışan yok")
                        .append(" (")
                        .append(alan)
                        .append("), ");
            } else {
                for (TeknikCalisan calisan : secilenCalisanlar) {
                    calisan.talepEkle();

                    atananCalisanlar
                            .append(calisan.getAdSoyad())
                            .append(" (")
                            .append(alan)
                            .append("), ");
                }

                if (secilenCalisanlar.size() < kisiSayisi) {
                    atananCalisanlar
                            .append("Eksik çalışan: ")
                            .append(kisiSayisi - secilenCalisanlar.size())
                            .append(" kişi")
                            .append(" (")
                            .append(alan)
                            .append("), ");
                }
            }
        }

        talep.setGerekliAlanlar(gerekliAlanlar.toString());
        talep.setAtananCalisanlar(atananCalisanlar.toString());
        talep.ekipLiderineOnayaGonder();

        return atananCalisanlar.toString();
    }

    // Eski çağrılar hata vermesin diye bırakıldı.
    public String departmanlaraGoreCalisanAta(HizmetTalebi talep,
                                               List<String> secilenAlanlar,
                                               List<TeknikCalisan> calisanlar) {

        List<Integer> kisiSayilari = new ArrayList<>();

        for (int i = 0; i < secilenAlanlar.size(); i++) {
            kisiSayilari.add(1);
        }

        return departmanlaraGoreCalisanAta(talep, secilenAlanlar, kisiSayilari, calisanlar);
    }

    // Eski sistemle uyumluluk için bırakıldı.
    public void talepAta(HizmetTalebi talep, List<TeknikCalisan> calisanlar) {
        TeknikCalisan uygunCalisan = uygunCalisanBul(talep.getAlan(), calisanlar);

        if (uygunCalisan != null) {
            talep.calisanaAta(uygunCalisan);
        } else {
            talep.atamaBekliyorYap();
        }
    }
}