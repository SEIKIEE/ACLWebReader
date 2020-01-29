import java.io.IOException;

public class Main {
    static public String doadLoadPath = "/Users/zjy/Desktop/Java应用技术基础/homework4/ACLWebReader/output";

    public static void main(String[] args) throws IOException {
        //obtain the basic information
        analyzeAnthology a = new analyzeAnthology("src/anthology.bib");
        //download 500 papers
        for (int i = 33501; i <= 34000; i++) {
            System.out.println(i);
            if (a.MyACLPaperInfo[i].url == null)
                continue;
            ACLwebReader.downloadPapers(a.MyACLPaperInfo[i].url, a.MyACLPaperInfo[i].name + ".pdf", doadLoadPath);
        }
        for (int i = 13333; i < a.MyACLPaperInfo.length; i++) {
            System.out.println(i);
            if (a.MyACLPaperInfo[i].url == null)
                continue;
            ACLwebReader.getAbstract(a.MyACLPaperInfo[i].url, "abstract", a.MyACLPaperInfo[i].name);
        }
        ACLwebReader.getInformation("https://www.aclweb.org/anthology/P19-1001", "P19-1001.txt", "info");
        //collect abstract
        for (int i = 0; i < a.MyACLPaperInfo.length; i++) {
            if (a.MyACLPaperInfo[i].article == 1) {
                if (a.MyACLPaperInfo[i].url == null)
                    continue;
                ACLwebReader.getAbstract(a.MyACLPaperInfo[i].url, a.MyACLPaperInfo[i].name, "abstract1");
            }
        }
    }
}
