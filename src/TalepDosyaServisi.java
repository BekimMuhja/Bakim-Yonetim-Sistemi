import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TalepDosyaServisi {

    public void talepKaydet(HizmetTalebi talep) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("talepler.txt", true));

            writer.println("Talep ID: " + talep.getTalepId());
            writer.println("Müşteri: " + talep.getMusteri().getAdSoyad());
            writer.println("Kurum: " + talep.getMusteri().getKurum());
            writer.println("Departman: " + talep.getMusteri().getDepartman());
            writer.println("Konu Alanı: " + talep.getAlan());
            writer.println("Açıklama: " + talep.getAciklama());
            writer.println("Durum: " + talep.getDurum());
            writer.println("Tarih: " + talep.getOlusturmaTarihi());

            if (talep.getAtananCalisan() != null) {
                writer.println("Atanan Çalışan: " + talep.getAtananCalisan().getAdSoyad());
            } else {
                writer.println("Atanan Çalışan: Yok");
            }

            writer.println("-----------------------------");
            writer.close();

        } catch (IOException e) {
            System.out.println("Dosyaya kayıt sırasında hata oluştu.");
        }
    }
}