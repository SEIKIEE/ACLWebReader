import java.io.*;

public class analyzeAnthology {
    public ACLPaperInfo[] MyACLPaperInfo = new ACLPaperInfo[53745];

    analyzeAnthology(String path) {
        //count the byte number of the file
        File file = new File(path);
        Long fileLength = file.length();
        byte[] fileContent = new byte[fileLength.intValue()];
        try {
            //read file into bytes
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            //transform byte into String
            String content = new String(fileContent);
            String[] papers = content.split("}\n");
            for (int i = 0; i < papers.length; i++) {
                String[] lines = papers[i].split(",\n");
                String[] info = new String[9];
                int a = 0;
                //obtain the basic information
                if (lines[0].startsWith("@article")) {
                    a = 1;
                }
                for (int j = 1; j < lines.length; j++) {
                    if (lines[j].startsWith("    title = ")) {
                        info[0] = lines[j].substring(13, lines[j].length() - 1);
                    } else if (lines[j].startsWith("    author = ")) {
                        info[1] = lines[j].substring(14, lines[j].length() - 1);
                    } else if (lines[j].startsWith("    month = ")) {
                        info[2] = lines[j].substring(12, lines[j].length());
                    } else if (lines[j].startsWith("    year = ")) {
                        info[3] = lines[j].substring(12, lines[j].length() - 1);
                    } else if (lines[j].startsWith("    address = ")) {
                        info[4] = lines[j].substring(15, lines[j].length() - 1);
                    } else if (lines[j].startsWith("    publisher = ")) {
                        info[5] = lines[j].substring(17, lines[j].length() - 1);
                    } else if (lines[j].startsWith("    url = ")) {
                        info[6] = lines[j].substring(11, lines[j].length() - 1);
                    } else if (lines[j].startsWith("    pages = ")) {
                        info[7] = lines[j].substring(13, lines[j].length() - 1);
                    } else if (lines[j].startsWith("    booktitle = ")) {
                        info[8] = lines[j].substring(17, lines[j].length() - 1);
                    }
                }
                MyACLPaperInfo[i] = new ACLPaperInfo(info, a);
            }
            in.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
