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

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.CapitalizationFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.tartarus.snowball.ext.EnglishStemmer;

import static org.apache.lucene.analysis.standard.StandardAnalyzer.ENGLISH_STOP_WORDS_SET;

public class g8app {
    private static String queryfield = "text";
    private static int MAX_RESULTS = 1000;

    private static String queries = null;
    private static String savequery = null;
    private static String stop_dir = null;
    private static String output_file = null;

    public g8app() {}

    /* Constructor with parameters */
    public g8app(final String queries, final String savequery, final String stop_dir, final String output_file) {
        g8app.queries = queries;
        g8app.savequery = savequery;
        g8app.stop_dir = stop_dir;
        g8app.output_file = output_file;
    }

    public static void main(String[] args) throws IOException {
        // ../Files path of the same folder present in our repository

        String usage = "[-queries QUERY_FILE_PATH] [-savequery PATH_TO_SAVE_NEW_QUERY_FILE] [-stopdir STOPWORDS_FILE_PATH] [-ouput output_file]";
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

        }

        if (queries == null || savequery == null || stop_dir == null || output_file == null) {
            System.err.println("Usage: " + usage);
            System.exit(1);
        }

        /*
         * SAMPLE ARGUMENTS String queries = "../Files/topics" String savequery =
         * "../Files/" String stop_dir = "../Files/stopwords.txt"
         *
         * -query ../Desktop/IdeaProjects/IRG8/Files/topics/ -savequery
         * ../Desktop/IdeaProjects/IRG8/Files/
         * -stopdir../Desktop/IdeaProjects/IRG8/Files/stopwords.txt
         *
         */

        String mod1 = savequery + "filename.txt";
        String mod2 = savequery + "simplified_query.txt";
        // Notice the lack of / or \. Path must be given accordingly for 'savequery'.
        PrintWriter out = new PrintWriter(mod1);
        PrintWriter out2 = new PrintWriter(mod2);
        PrintWriter out3 = new PrintWriter(savequery + "final_query.txt");
        // Notice the lack of / or \. Path must be given accordingly for 'savequery'.
        qparse_01 init_parse = new qparse_01();
        init_parse.firstParseQuery(queries, out);
        out.close();
        init_parse.secondParseQuery(mod1, out2);
        out2.close();
        System.out.println("Initial Parsing Done.");
        qparse_02.thirdParseQuery(stop_dir, out3, mod2);
        out3.close();
        System.out.println("Final Parsing Done.");

        try {
            Indexer.buildAllIndex();
        } catch (Exception e) {
            System.out.println("fail to build index");
            e.printStackTrace();
        }
        try {
            query("./index", savequery + "final_query.txt", output_file);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("fail to query");
        }
    }

    /*
     * parseQueries parse all the queries into an arraylist, assuming: query file
     * only contains num and query, and query string only has one line content
     */
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

    public static void query(String indexdir, String parsedQuery, String outputfn) throws IOException, ParseException {
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
        Analyzer analyzer = getEnhancedCustomAnalyzer();
        // open index
        directory = FSDirectory.open(Paths.get(indexdir));
        ireader = DirectoryReader.open(directory);

        IndexSearcher isearcher = new IndexSearcher(ireader);
        isearcher.setSimilarity(new BM25Similarity());

        FileWriter output = null;

        output = new FileWriter(outputfn);

        QueryParser parser = new QueryParser(queryfield, analyzer);

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
                output.write(parseid + " Q0 " + hitDoc.get("id") + " " + i + " " + hits[i].score + " STANDARD" + "\n");
            }

        }
        output.close();
        directory.close();
    }

    /** Provides access to the source, a Reader Consumer and an instance of TokenFilter
     *  @author Stefan Spirkl <spirkl@logos-gmbh.com>
     */
    static Analyzer getEnhancedCustomAnalyzer() {
        return new EnglishAnalyzer();
    }

    /** Provides access to the source, a Reader Consumer and an instance of TokenFilter
     *  @author Stefan Spirkl <spirkl@logos-gmbh.com>
     */
    static Analyzer getEnhancedCustomAnalyzer2() {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String s) {
                System.out.println("createComponents called.");
                final Tokenizer tokenizer = new StandardTokenizer();
                TokenStream tokenStream = new StandardFilter(tokenizer);
                tokenStream = new LowerCaseFilter(tokenStream);
                tokenStream = new TrimFilter(tokenStream);
                tokenStream = new FlattenGraphFilter(new WordDelimiterGraphFilter(tokenStream, WordDelimiterGraphFilter.SPLIT_ON_NUMERICS |
                        WordDelimiterGraphFilter.GENERATE_WORD_PARTS | WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS |
                        WordDelimiterGraphFilter.PRESERVE_ORIGINAL , null));
                //tokenStream = new FlattenGraphFilter(new SynonymGraphFilter(tokenStream, createSynonymMap(), true));
                tokenStream = new StopFilter(tokenStream, ENGLISH_STOP_WORDS_SET);
                tokenStream = new SnowballFilter(tokenStream, new EnglishStemmer());
                return new TokenStreamComponents(tokenizer, tokenStream);
            }
        };
    }

    /** Provides access to the source, a Reader Consumer and an instance of TokenFilter
     *  @author Stefan Spirkl <spirkl@logos-gmbh.com>
     */
    static Analyzer getEnhancedCustomAnalyzer3() {
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                final StandardTokenizer src = new StandardTokenizer();
                TokenStream result = new StandardFilter(src);
                result = new LowerCaseFilter(result);
                result = new StopFilter(result,  StandardAnalyzer.STOP_WORDS_SET);
                result = new PorterStemFilter(result);
                result = new CapitalizationFilter(result);
                return new TokenStreamComponents(src, result);
            }
        };
    }

}
