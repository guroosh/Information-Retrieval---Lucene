package G8;

import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;

import java.io.*;

public class CreateSynonymMap {
    public static SynonymMap create() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("Files/synonyms.txt")));
        String line;
        SynonymMap.Builder builder = new SynonymMap.Builder(true);
        while ((line = br.readLine()) != null) {
//            System.out.println(line);
            String arr[] = line.split(",");
            int len = arr.length;
            String one = arr[0];
            for (int i = 1; i < len; i++) {
                builder.add(new CharsRef(one), new CharsRef(arr[i]), true);
            }
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
