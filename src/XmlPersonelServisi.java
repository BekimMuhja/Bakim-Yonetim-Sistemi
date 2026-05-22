import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlPersonelServisi {

    private static final String CALISAN_DOSYA = "calisanlar.xml";
    private static final String ALAN_DOSYA = "hizmetAlanlari.xml";
    private static final String EKIP_LIDERI_DOSYA = "ekipLiderleri.xml";

    public void calisanlariKaydet(List<TeknikCalisan> calisanlar) {
        try {
            Document doc = yeniDokuman();
            Element root = doc.createElement("calisanlar");
            doc.appendChild(root);

            for (TeknikCalisan c : calisanlar) {
                Element calisan = doc.createElement("calisan");
                elemanEkle(doc, calisan, "adSoyad", c.getAdSoyad());
                elemanEkle(doc, calisan, "alan", c.getAlan());
                elemanEkle(doc, calisan, "aktifTalepSayisi", String.valueOf(c.getAktifTalepSayisi()));
                root.appendChild(calisan);
            }

            xmlYaz(doc, CALISAN_DOSYA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<TeknikCalisan> calisanlariYukle() {
        List<TeknikCalisan> calisanlar = new ArrayList<>();

        try {
            File file = new File(CALISAN_DOSYA);

            if (!file.exists()) {
                calisanlar.add(new TeknikCalisan("Ali Teknik", "Donanim", 1));
                calisanlar.add(new TeknikCalisan("Mehmet Usta", "Donanim", 3));
                calisanlar.add(new TeknikCalisan("Ayşe Uzman", "Ag", 2));
                calisanlar.add(new TeknikCalisan("Fatma Yazılımcı", "Yazilim", 0));
                calisanlariKaydet(calisanlar);
                return calisanlar;
            }

            Document doc = belgeOku(file);
            NodeList list = doc.getElementsByTagName("calisan");

            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                String adSoyad = getText(e, "adSoyad");
                String alan = getText(e, "alan");
                int aktifTalepSayisi = Integer.parseInt(getText(e, "aktifTalepSayisi"));

                calisanlar.add(new TeknikCalisan(adSoyad, alan, aktifTalepSayisi));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return calisanlar;
    }

    public void hizmetAlanlariKaydet(List<String> alanlar) {
        try {
            Document doc = yeniDokuman();
            Element root = doc.createElement("hizmetAlanlari");
            doc.appendChild(root);

            for (String alan : alanlar) {
                elemanEkle(doc, root, "alan", alan);
            }

            xmlYaz(doc, ALAN_DOSYA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> hizmetAlanlariYukle() {
        List<String> alanlar = new ArrayList<>();

        try {
            File file = new File(ALAN_DOSYA);

            if (!file.exists()) {
                alanlar.add("Donanim");
                alanlar.add("Ag");
                alanlar.add("Yazilim");
                alanlar.add("Elektrik");
                alanlar.add("Mekanik");
                hizmetAlanlariKaydet(alanlar);
                return alanlar;
            }

            Document doc = belgeOku(file);
            NodeList list = doc.getElementsByTagName("alan");

            for (int i = 0; i < list.getLength(); i++) {
                alanlar.add(list.item(i).getTextContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return alanlar;
    }

    public void ekipLiderleriKaydet(List<EkipLideri> ekipLiderleri) {
        try {
            Document doc = yeniDokuman();
            Element root = doc.createElement("ekipLiderleri");
            doc.appendChild(root);

            for (EkipLideri lider : ekipLiderleri) {
                Element liderElement = doc.createElement("ekipLideri");
                elemanEkle(doc, liderElement, "adSoyad", lider.getAdSoyad());
                elemanEkle(doc, liderElement, "aktifTalepSayisi", String.valueOf(lider.getAktifTalepSayisi()));
                root.appendChild(liderElement);
            }

            xmlYaz(doc, EKIP_LIDERI_DOSYA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<EkipLideri> ekipLiderleriYukle() {
        List<EkipLideri> liderler = new ArrayList<>();

        try {
            File file = new File(EKIP_LIDERI_DOSYA);

            if (!file.exists()) {
                liderler.add(new EkipLideri("Lider Ali", 0));
                liderler.add(new EkipLideri("Lider Ayse", 0));
                ekipLiderleriKaydet(liderler);
                return liderler;
            }

            Document doc = belgeOku(file);
            NodeList list = doc.getElementsByTagName("ekipLideri");

            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                String adSoyad = getText(e, "adSoyad");
                int aktifTalepSayisi = Integer.parseInt(getText(e, "aktifTalepSayisi"));

                liderler.add(new EkipLideri(adSoyad, aktifTalepSayisi));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return liderler;
    }

    private Document yeniDokuman() throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.newDocument();
    }

    private Document belgeOku(File file) throws Exception {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(file);
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

    private void xmlYaz(Document doc, String dosyaAdi) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(dosyaAdi));

        transformer.transform(source, result);
    }
}