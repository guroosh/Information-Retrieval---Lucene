package G8;

import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;

import java.io.*;

public class CreateSynonymMap {
    public static SynonymMap create() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("Files/dict/data.adj")));
        String line;
        SynonymMap.Builder builder = new SynonymMap.Builder(true);
        while ((line = br.readLine()) != null) {
//            System.out.println(line);
            String arr[] = line.split(" ");
            int len = arr.length;
            String one = arr[4];
//            System.out.print(one);
            for (int i = 6; i < len; i = i + 2) {
                if (!arr[i].replaceAll("[0-9]+", "").equals("")) {
//                    System.out.print(" " + arr[i]);
//                    builder.add(new CharsRef(one.toLowerCase()), new CharsRef(arr[i].toLowerCase()), true);
                } else {
                    break;
                }
            }
//            System.out.println();
        }
        br = new BufferedReader(new FileReader(new File("Files/dict/data.adv")));
        builder = new SynonymMap.Builder(true);
        while ((line = br.readLine()) != null) {
//            System.out.println(line);
            String arr[] = line.split(" ");
            int len = arr.length;
            String one = arr[4];
            boolean flg = false;
            for (int i = 6; i < len; i = i + 2) {
                if (!arr[i].replaceAll("[0-9]+", "").equals("")) {
//                    System.out.print(one + " " + arr[i]);
//                    builder.add(new CharsRef(one.toLowerCase()), new CharsRef(arr[i].toLowerCase()), true);
                    flg = true;
                } else {
                    break;
                }
            }
//            if (flg)
//                System.out.println();
        }

        br = new BufferedReader(new FileReader(new File("Files/dict/data.noun")));
        builder = new SynonymMap.Builder(true);
        while ((line = br.readLine()) != null) {
//            System.out.println(line);
            String arr[] = line.split(" ");
            int len = arr.length;
            String one = arr[4];
            boolean flg = false;
            for (int i = 6; i < len; i = i + 2) {
                if (!arr[i].replaceAll("[0-9]+", "").equals("")) {
//                    System.out.print(one + " " + arr[i]);
//                    builder.add(new CharsRef(one.toLowerCase()), new CharsRef(arr[i].toLowerCase()), true);
                    flg = true;
                } else {
                    break;
                }
            }
//            if (flg)
//                System.out.println();
        }

        br = new BufferedReader(new FileReader(new File("Files/dict/data.verb")));
        builder = new SynonymMap.Builder(true);
        while ((line = br.readLine()) != null) {
//            System.out.println(line);
            String arr[] = line.split(" ");
            int len = arr.length;
            String one = arr[4];
            boolean flg = false;
            for (int i = 6; i < len; i = i + 2) {
                if (!arr[i].replaceAll("[0-9]+", "").equals("")) {
//                    System.out.print(one + " " + arr[i]);
//                    builder.add(new CharsRef(one.toLowerCase()), new CharsRef(arr[i].toLowerCase()), true);
                    flg = true;
                } else {
                    break;
                }
            }
//            if (flg)
//                System.out.println();
        }

        br = new BufferedReader(new FileReader(new File("Files/dict/adj.exc")));
        builder = new SynonymMap.Builder(true);
        while ((line = br.readLine()) != null) {
//            System.out.println(line);
            String arr[] = line.split(" ");
            int len = arr.length;
            String one = arr[0];
            boolean flg = false;
            builder.add(new CharsRef(one.toLowerCase()), new CharsRef(arr[1].toLowerCase()), true);
//            System.out.println(one + " " + arr[1]);
        }

        br = new BufferedReader(new FileReader(new File("Files/dict/adv.exc")));
        builder = new SynonymMap.Builder(true);
        while ((line = br.readLine()) != null) {
//            System.out.println(line);
            String arr[] = line.split(" ");
            int len = arr.length;
            String one = arr[0];
            boolean flg = false;
            builder.add(new CharsRef(one.toLowerCase()), new CharsRef(arr[1].toLowerCase()), true);
//            System.out.println(one + " " + arr[1]);
        }

        br = new BufferedReader(new FileReader(new File("Files/dict/verb.exc")));
        builder = new SynonymMap.Builder(true);
        while ((line = br.readLine()) != null) {
//            System.out.println(line);
            String arr[] = line.split(" ");
            int len = arr.length;
            String one = arr[0];
            boolean flg = false;
            builder.add(new CharsRef(one.toLowerCase()), new CharsRef(arr[1].toLowerCase()), true);
//            System.out.println(one + " " + arr[1]);
        }

        br = new BufferedReader(new FileReader(new File("Files/dict/noun.exc")));
        builder = new SynonymMap.Builder(true);
        while ((line = br.readLine()) != null) {
//            System.out.println(line);
            String arr[] = line.split(" ");
            int len = arr.length;
            String one = arr[0];
            boolean flg = false;
            builder.add(new CharsRef(one.toLowerCase()), new CharsRef(arr[1].toLowerCase()), true);
//            System.out.println(one + " " + arr[1]);
        }

        builder.add(new CharsRef("one"), new CharsRef("first"), true);
        builder.add(new CharsRef("one"), new CharsRef("alpha"), true);
        builder.add(new CharsRef("one"), new CharsRef("beguine"), true);
        return builder.build();
    }

    public static void main(String args[]) throws IOException {
        create();
    }
}
