package G8;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.util.Date;
//import java.util.HashMap;


public class qparse_02 {
    public qparse_02() {}

    public static List<String> stopwords;
    public static void loadStopwords(String stop_dir) throws IOException {
        stopwords = Files.readAllLines(Paths.get(stop_dir));
    }
    public static void thirdParseQuery(String stop_dir, PrintWriter wrt, String mod2) throws IOException {

        loadStopwords(stop_dir);
        String newqueries = mod2;
        String descLine = "";
        String narrLine = "";
        String tempLine;
        int split_length;
        int counter = 0;
        BufferedReader in3 = null;
        if (newqueries != null) {
            try {
                in3 = Files.newBufferedReader(Paths.get(newqueries), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Invalid path to Query File.");
            }
        }

        String line2;
        while (true) {
            line2 = in3.readLine();
            if (line2 == null || line2.length() == 0) {
                break;
            } else {
                if (line2.startsWith("<desc>")) {
                    line2 = in3.readLine();
                    line2 = line2.toLowerCase();
                    line2=line2.replaceAll("u\\.s\\.","USA123");
                    descLine = line2;
                    descLine = descLine.trim();
                } else if (line2.startsWith("<narr>")) {
                    line2 = in3.readLine();
                    line2 = line2.toLowerCase();
                    line2=line2.replaceAll("i\\.e\\.,", "");
                    line2=line2.replaceAll("u\\.s\\.","USA123");
                    line2=line2.replaceAll("e\\.g\\.,","");
                    narrLine = "";
                    split_length = line2.split(";|\\.|, but").length;
                    for (int i = 0; i <= split_length - 1; i++) {
//                        System.out.println((line2.split(";|\\.|, but")[i]+"").trim());
                        tempLine = (line2.split(";|\\.|, but")[i] + "").trim();
                        if (tempLine.contains("not relevant")) {
                            continue;
                        } else if (!tempLine.contains("not relevant")) {
                            narrLine = narrLine + " " + tempLine;
                            narrLine = narrLine.trim();
                        }
                    }
                    tempLine = descLine + " " + narrLine;
                    tempLine = tempLine.toLowerCase();
                    tempLine = rem_stop_word(tempLine);
                    tempLine = tempLine.replaceAll("usa123","U.S.");
                    tempLine = tempLine.trim().replaceAll(" +", " ");
                    wrt.println("<query>\n" + tempLine);
                } else {
                    wrt.println(line2);
                }
            }
        } in3.close();
        File f= new File(mod2);
        f.delete();
    }

    public static String rem_stop_word (String original){
        String[] allWords = original.toLowerCase().split(" |\\.|\\?");
        StringBuilder builder = new StringBuilder();
        for (String word : allWords) {
            word=word.trim();
            if (!stopwords.contains(word)) {
                builder.append(word);
                if(word!=" "){
                builder.append(' ');
            }}
        }
        String result = builder.toString().trim();
        return result;
    }

}