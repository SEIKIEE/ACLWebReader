import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ACLwebReader {
    //download PDF
    public static void downLoadByUrl(String urlStr, String fileName, String savePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //avoid 403
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //get input
        InputStream inputStream = conn.getInputStream();
        byte[] getData = readInputStream(inputStream);
        //save the document
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        System.out.println("info:" + url + " download success");
    }

    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    //collect paper download address
    public static void downloadPapers(String url, String fineName, String savePath) throws IOException {
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")
                .get();
        Elements hrefs = doc.select("a[href]");
        for (Element elem : hrefs) {
            if (elem.text().endsWith(".pdf")) {
                downLoadByUrl(elem.text(), fineName, savePath);
            }
        }
    }

    //collect paper'a abstract if exists
    public static void getAbstract(String url, String fileName, String filePath) throws IOException {
        //not judge whether url is null or not
        //if abstract is not exists, return null
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")
                .get();
        Elements abs = doc.getElementsByClass("card-body acl-abstract");
        System.out.println(abs.text());
        //if the path does not exist, make a new dir
        File savePath = new File(filePath);
        if (!savePath.exists()) {
            savePath.mkdir();
        }
        //write the abstract to file
        File abstractFile = new File(savePath + File.separator + fileName);
        Writer out = new FileWriter(abstractFile);
        out.write(abs.text());
        out.close();
    }

    //get the information from web
    public static void getInformation(String url, String fileName, String filePath) throws IOException {
        //do not judge whether url is null or not
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")
                .get();
        String authors = doc.getElementsByClass("lead").text() + "\n";
        String title = doc.getElementById("title").text() + "\n";
        String abstracts = doc.getElementsByClass("card-body acl-abstract").text() + "\n";
        String contents = doc.select("dl").text() + "\n";
        File path = new File(filePath);
        if (!path.exists()) {
            path.mkdir();
        }
        File file = new File(path, fileName);
        FileOutputStream out = new FileOutputStream(file, true);
        if (title != null)
            out.write(title.getBytes());
        if (authors != null)
            out.write(authors.getBytes());
        if (!abstracts.equals("\n"))
            out.write(abstracts.getBytes());
        if (contents != null)
            out.write(contents.getBytes());
        out.close();
        //print the basic information
        if (title != null) {
            System.out.println("Title:");
            System.out.println(title);
        }
        if (authors != null) {
            System.out.println("Authors:");
            System.out.println(authors);
        }
        if (!abstracts.equals("\n")) {
            System.out.println("Abstract:");
            System.out.println(abstracts);
        }
        if (contents != null) {
            System.out.println("Content:");
            System.out.println(contents);
        }
    }
}

