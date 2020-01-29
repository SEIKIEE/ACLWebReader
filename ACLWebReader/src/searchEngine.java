import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class searchEngine {
    private String sourceFile;
    private String indexFile;
    private int itemNum;
    private analyzeAnthology a;
    public HashMap<String, String> titile2url = new HashMap<>();


    searchEngine(String s, String i, int n) {
        sourceFile = s;
        indexFile = i;
        itemNum = n;
        a = new analyzeAnthology("src/anthology.bib");
        for (int j = 0; j < a.MyACLPaperInfo.length; j++)
            titile2url.put(a.MyACLPaperInfo[j].name, a.MyACLPaperInfo[j].url);
        File f = new File(i);
        if (f.exists()) {
            if (f.isDirectory()) {
                for (File file : f.listFiles()) {
                    file.delete();
                }
            } else {
                f.delete();
            }
        }
    }

    public void createIndex() {
        File f = new File(indexFile);
        IndexWriter iwr = null;
        try {
            //create a directory to store index
            Directory dir = FSDirectory.open(f);
            //创建分词器
            Analyzer analyzer = new IKAnalyzer();
            //索引写出工具的配置对象
            IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_4_10_0, analyzer);
            //创建索引的写出工具类。参数：索引的目录和配置信息
            iwr = new IndexWriter(dir, conf);
            File s = new File(sourceFile);
            String[] fileList = s.list();
            //创建摘要的索引
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].equals(".DS_Store"))
                    continue;
                File temp = new File(s, fileList[i]);
                if (!temp.isDirectory()) {
                    Document doc = getDocument(temp.getAbsolutePath());
                    //把文档交给IndexWriter
                    iwr.addDocument(doc);
                }
            }
            for (int i = 0; i < a.MyACLPaperInfo.length; i++) {
                ACLPaperInfo temp = a.MyACLPaperInfo[i];
                Document doc = new Document();
                if (temp.title != null) {
                    Field f1 = new TextField("Title", temp.name + ":" + temp.title, Field.Store.YES);
                    doc.add(f1);
                }
                if (temp.author != null) {
                    Field f2 = new TextField("Author", temp.name + ":" + temp.author, Field.Store.YES);
                    doc.add(f2);
                }
                if (temp.booktitle != null) {
                    Field f3 = new TextField("Booktitile", temp.name + ":" + temp.booktitle, Field.Store.YES);
                    doc.add(f3);
                }
                iwr.addDocument(doc);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            iwr.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Document getDocument(String fileName) throws IOException {
        Document doc = new Document();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        String abstractInfo = br.readLine();
        //第一个参数是字段的名字，第二个参数是字段存储的内容，第三个参数标注是否需要存储
        int l = fileName.split("/").length;
        Field f = new TextField("Abstract", fileName.split("/")[l - 1] + ":" + abstractInfo, Field.Store.YES);
        doc.add(f);
        return doc;
    }

    public ArrayList<String> searrch(String queryStr, String way) {
        ArrayList<String> results = new ArrayList<>();
        File f = new File(indexFile);
        try {
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(f)));
            Analyzer analyzer = new IKAnalyzer();
            QueryParser parser = new QueryParser(Version.LUCENE_4_10_0, way, analyzer);
            //输入lucene查询语句
            Query query = parser.parse(queryStr);
            //第二个参数指定需要显示的顶部记录的条数
            TopDocs hits = searcher.search(query, itemNum);
            //根据查询条件匹配出的记录总数
            for (ScoreDoc doc : hits.scoreDocs) {
                Document d = searcher.doc(doc.doc);
                results.add(d.get(way).split(":")[0]);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static void main(String[] args) throws IOException {
        //原文件，索引目录，查询条数
        searchEngine searchEngine = new searchEngine("abs", "index", 1);
        //创建索引
        searchEngine.createIndex();
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("1.作者    2.标题    3.摘要    4.会议 ");
            System.out.println("输入检索方式：");
            String way = input.nextLine();
            System.out.println("输入检索内容：");
            String index = input.nextLine();
            if (way.equals("1")) {
                way = "Author";
            } else if (way.equals("2")) {
                way = "Title";
            } else if (way.equals("3")) {
                way = "Abstract";
            } else {
                way = "Booktitile";
            }
            ArrayList<String> results = searchEngine.searrch(index, way);
            System.out.println("检索结果：");
            for (String d : results) {
                String url = searchEngine.titile2url.get(d);
                if (url != null)
                    ACLwebReader.getInformation(url, d, "search");
            }
        }
    }
}












