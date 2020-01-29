public class ACLPaperInfo {
    public String title;
    public String author;
    public String month;
    public String year;
    public String address;
    public String publisher;
    public String url;
    public String pages;
    public String name;
    public String booktitle;
    public int article;

    ACLPaperInfo(String[] info, int a) {
        title = info[0];
        author = info[1];
        month = info[2];
        year = info[3];
        address = info[4];
        publisher = info[5];
        url = info[6];
        pages = info[7];
        booktitle = info[8];
        if (url != null) {
            String[] elems = url.split("/");
            name = elems[elems.length - 1];
        }
        article = a;
    }
}
