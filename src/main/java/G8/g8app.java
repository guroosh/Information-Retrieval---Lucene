package G8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

public class g8app {
    private static String queryfield = "text";
    private static int MAX_RESULTS = 1000;

    private g8app() {
    }

    public static void main(String[] args) throws IOException {
        // ../Files path of the same folder present in our repository

        String queries = null;
        String savequery = null;
        String stop_dir = null;
        String output_file = null;
        String score_me = null;
        String usage = "[-query QUERY_FILE_PATH] [-savequery PATH_TO_SAVE_NEW_QUERY_FILE] [-stopdir STOPWORDS_FILE_PATH] [-output output_file] [-score set_similarity]";
        for (int i = 0; i < args.length; i++) {
            if ("-query".equals(args[i])) {
                queries = args[i + 1];
                i++;
            } else if ("-savequery".equals(args[i])) {
                savequery = args[i + 1];
                i++;
            } else if ("-stopdir".equals(args[i])) {
                stop_dir = args[i + 1];
                i++;
            } else if ("-output".equals(args[i])) {
                output_file = args[i + 1];
            }
            else if ("-score".equals(args[i])) {
                score_me=args[i+1];
                i++;
            }

        }

        if (score_me == null) {
            score_me = "7";
        }

        if (queries == null || savequery == null || stop_dir == null || output_file == null) {
            System.err.println("Usage: " + usage);
            System.exit(1);
        }

        /*
         * SAMPLE ARGUMENTS
         * String queries = "../Files/topics"
         * String savequery = "../Files/"
         * String stop_dir = "../Files/stopwords.txt"
         * String output = "../Files/output.txt"
         *
         * -query ../Desktop/IdeaProjects/IRG8/Files/topics/
         * -savequery ../Desktop/IdeaProjects/IRG8/Files/
         * -stopdir ../Desktop/IdeaProjects/IRG8/Files/stopwords.txt
         * -output ../Desktop/IdeaProjects/IRG8/Files/output.txt
         * -score 1
         */

        String mod1 = savequery + "filename.txt";
        String mod2 = savequery + "simplified_query.txt";
        // Notice the lack of / or \. Path must be given accordingly for 'savequery'.
        PrintWriter out = new PrintWriter(mod1);
        PrintWriter out2 = new PrintWriter(mod2);
        PrintWriter out3 = new PrintWriter(savequery + "final_query.txt");
        // Notice the lack of / or \. Path must be given accordingly for 'savequery'.
        queryparse1 init_parse = new queryparse1();
        init_parse.firstParseQuery(queries, out);
        out.close();
        init_parse.secondParseQuery(mod1, out2);
        out2.close();
        System.out.println("Initial Parsing Done.");
        queryparse2.thirdParseQuery(stop_dir, out3, mod2);
        out3.close();
        System.out.println("Final Parsing Done.");

        try {
            Indexer.buildAllIndex();
        } catch (Exception e) {
            System.out.println("fail to build index");
            e.printStackTrace();
        }

        try {
            query("./index", savequery + "final_query.txt", output_file, score_me);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("fail to query");
        }

    }

    /*
     * parseQueries parse all the queries into an arraylist, assuming: query file
     * only contains num and query, and query string only has one line content
     */

    public static String[] getFields() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("fields.txt")));
        String[] res = br.readLine().split(",");
        br.close();
        return res;
    }

    public static ArrayList<HashMap<String, String>> parseQueries(String parsedQuery) throws IOException {
        ArrayList<HashMap<String, String>> res = new ArrayList<HashMap<String, String>>();
        File f = new File(parsedQuery);
        FileReader freader = new FileReader(f);
        BufferedReader br = new BufferedReader(freader);
        HashMap<String, String> item = new HashMap<String, String>();
        String line = "";
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            if (line.startsWith("<") && line.endsWith(">")) {
                item.put(line, br.readLine());
                item.put(br.readLine(), br.readLine());
                res.add(item);
                item = new HashMap<String, String>();
            }
        }
        freader.close();
        return res;
    }

    public static void query(String indexdir, String parsedQuery, String outputfn, String score_me) throws IOException, ParseException {
        ArrayList<HashMap<String, String>> queries = null;
        try {
            queries = parseQueries(parsedQuery);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("fail to parse query file.");
            return;
        }

        Directory directory = null;
        DirectoryReader ireader = null;
        Analyzer analyzer = new CustomEnglishAnalyzer();
        // open index
        directory = FSDirectory.open(Paths.get(indexdir));
        ireader = DirectoryReader.open(directory);

        int setScore = 0;
        setScore = Integer.parseInt(score_me);

        IndexSearcher isearcher = new IndexSearcher(ireader);

        if(setScore == 0) isearcher.setSimilarity(new ClassicSimilarity());
        if(setScore == 1) isearcher.setSimilarity(new BM25Similarity());
        if(setScore == 2) isearcher.setSimilarity(new BooleanSimilarity());
        if(setScore == 3) isearcher.setSimilarity(new LMDirichletSimilarity());
        if(setScore == 4) isearcher.setSimilarity(new LMJelinekMercerSimilarity((float) 0.6));
        if(setScore == 5) isearcher.setSimilarity(new AxiomaticF1EXP());
        if(setScore == 6) isearcher.setSimilarity(new AxiomaticF1LOG());
        if(setScore == 7) isearcher.setSimilarity(new AxiomaticF2EXP());
        if(setScore == 8) isearcher.setSimilarity(new AxiomaticF2LOG());


        FileWriter output = null;

        output = new FileWriter(outputfn);

        // QueryParser parser = new QueryParser(queryfield, analyzer);
        String[] fields = getFields();
        System.out.println("all indexed fields:"+String.join(",", fields));
        HashMap<String,Float> boosts = new HashMap<String,Float>();

        for (String s: fields) {
            boosts.put(s,2f);
        }
        boosts.put("TEXT",10f);
        /*boosts.put("HEADER",10f);
        boosts.put("HEADLINE",10f);
        boosts.put("GRAPHIC",10f);
        boosts.put("PROFILE",10f);
        boosts.put("SUBJECT",10f);
        boosts.put("H3",10f);
        boosts.put("H4",10f);*/

        System.out.println(boosts);
        QueryParser parser = new MultiFieldQueryParser(fields, analyzer,boosts);
        //QueryParser parser = new MultiFieldQueryParser(new String[]{"TEXT","HEADER","HEADLINE","GRAPHIC","PROFILE","SUBJECT","H3","H4"}, analyzer,boosts);

        for (HashMap<String, String> qr : queries) {
            String parseid = qr.get("<num>");
            String parsetext = qr.get("<query>");
            if (parsetext == null || parseid == null) {
                System.out.printf("ERROR: fail to get parse text %v %v", parsetext, parseid);
                continue;
            }
            parsetext = QueryParser.escape(parsetext);

            Query query = parser.parse(parsetext);
            ScoreDoc[] hits = isearcher.search(query, MAX_RESULTS).scoreDocs;

            // Print the results
            System.out.println("Documents: " + hits.length);
            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = isearcher.doc(hits[i].doc);
                // System.out.prioutputntln(i + ") " + hitDoc.get("id") + " " + hits[i].score);
                output.write(parseid + " Q0 " + hitDoc.get("DOCNO") + " " + i + " " + hits[i].score + " STANDARD" + "\n");
            }

        }
        output.close();
        directory.close();

    }
}