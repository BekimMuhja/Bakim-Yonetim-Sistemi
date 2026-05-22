import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class XmlTalepServisi {

    private static final String DOSYA_ADI = "talepler.xml";

    public void talepleriKaydet(List<HizmetTalebi> talepler) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("talepler");
            doc.appendChild(root);

            for (HizmetTalebi talep : talepler) {
                Element talepElement = doc.createElement("talep");

                elemanEkle(doc, talepElement, "id", String.valueOf(talep.getTalepId()));
                elemanEkle(doc, talepElement, "musteri", talep.getMusteri().getAdSoyad());
                elemanEkle(doc, talepElement, "kurum", talep.getMusteri().getKurum());
                elemanEkle(doc, talepElement, "departman", talep.getMusteri().getDepartman());
                elemanEkle(doc, talepElement, "alan", talep.getAlan());
                elemanEkle(doc, talepElement, "aciklama", talep.getAciklama());
                elemanEkle(doc, talepElement, "durum", talep.getDurum().toString());
                elemanEkle(doc, talepElement, "tarih", talep.getOlusturmaTarihi().toString());
                elemanEkle(doc, talepElement, "teknikNot", talep.getTeknikNot());

                String liderAdi = "";
                if (talep.getEkipLideri() != null) {
                    liderAdi = talep.getEkipLideri().getAdSoyad();
                }

                elemanEkle(doc, talepElement, "ekipLideri", liderAdi);
                elemanEkle(doc, talepElement, "gerekliAlanlar", talep.getGerekliAlanlar());
                elemanEkle(doc, talepElement, "atananCalisanlar", talep.getAtananCalisanlar());

                elemanEkle(doc, talepElement, "calisanDurumlari", talep.getCalisanDurumlari());
                elemanEkle(doc, talepElement, "ekipLideriNotlari", talep.getEkipLideriNotlari());
                elemanEkle(doc, talepElement, "musteriCevaplari", talep.getMusteriCevaplari());
                elemanEkle(doc, talepElement, "retSayisi", String.valueOf(talep.getRetSayisi()));

                elemanEkle(doc, talepElement, "puan", String.valueOf(talep.getPuan()));
                elemanEkle(doc, talepElement, "puanlandi", String.valueOf(talep.isPuanlandi()));

                root.appendChild(talepElement);
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(DOSYA_ADI));

            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<HizmetTalebi> talepleriYukle(
            List<TeknikCalisan> calisanlar,
            List<EkipLideri> ekipLiderleri) {

        List<HizmetTalebi> talepler = new ArrayList<>();

        try {
            File file = new File(DOSYA_ADI);

            if (!file.exists()) {
                return talepler;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(file);

            NodeList nodeList = doc.getElementsByTagName("talep");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element e = (Element) nodeList.item(i);

                int id = Integer.parseInt(getText(e, "id"));

                String musteriAdi = getText(e, "musteri");
                String kurum = getText(e, "kurum");
                String departman = getText(e, "departman");

                String alan = getText(e, "alan");
                String aciklama = getText(e, "aciklama");

                TalepDurumu durum = TalepDurumu.valueOf(getText(e, "durum"));
                LocalDate tarih = LocalDate.parse(getText(e, "tarih"));

                String teknikNot = getText(e, "teknikNot");
                String ekipLideriAdi = getText(e, "ekipLideri");
                String gerekliAlanlar = getText(e, "gerekliAlanlar");
                String atananCalisanlar = getText(e, "atananCalisanlar");

                String calisanDurumlari = getText(e, "calisanDurumlari");
                String ekipLideriNotlari = getText(e, "ekipLideriNotlari");
                String musteriCevaplari = getText(e, "musteriCevaplari");
                String retText = getText(e, "retSayisi");

                Musteri musteri = new Musteri(musteriAdi, kurum, departman);
                EkipLideri ekipLideri = ekipLideriBul(ekipLideriAdi, ekipLiderleri);

                HizmetTalebi talep = new HizmetTalebi(
                        id,
                        alan,
                        aciklama,
                        musteri,
                        durum,
                        tarih,
                        null,
                        teknikNot,
                        ekipLideri,
                        gerekliAlanlar,
                        atananCalisanlar
                );

                if (!calisanDurumlari.isEmpty()) {
                    talep.teknikBilgiEkle(calisanDurumlari);
                }

                if (!ekipLideriNotlari.isEmpty()) {
                    talep.ekipLideriBilgiEkle(ekipLideriNotlari);
                }

                if (!musteriCevaplari.isEmpty()) {
                    talep.musteriBilgiEkle(musteriCevaplari);
                }

                if (!retText.isEmpty()) {
                    int retSayisi = Integer.parseInt(retText);

                    for (int j = 0; j < retSayisi; j++) {
                        talep.reddet();
                    }
                }

                String puanText = getText(e, "puan");
                String puanlandiText = getText(e, "puanlandi");

                if (!puanText.isEmpty() && !puanlandiText.isEmpty()) {
                    int puan = Integer.parseInt(puanText);
                    boolean puanlandi = Boolean.parseBoolean(puanlandiText);

                    if (puanlandi) {
                        talep.puanVer(puan);
                    }
                }

                talepler.add(talep);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return talepler;
    }

    private void elemanEkle(Document doc, Element parent, String ad, String deger) {
        Element element = doc.createElement(ad);
        element.appendChild(doc.createTextNode(deger == null ? "" : deger));
        parent.appendChild(element);
    }

    private String getText(Element element, String tag) {
        NodeList list = element.getElementsByTagName(tag);

        if (list.getLength() == 0) {
            return "";
        }

        return list.item(0).getTextContent();
    }

    private EkipLideri ekipLideriBul(String adSoyad, List<EkipLideri> ekipLiderleri) {
        for (EkipLideri lider : ekipLiderleri) {
            if (lider.getAdSoyad().equalsIgnoreCase(adSoyad)) {
                return lider;
            }
        }

        return null;
    }
}