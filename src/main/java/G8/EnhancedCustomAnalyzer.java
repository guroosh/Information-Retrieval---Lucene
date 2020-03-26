package G8;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.*;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.lucene.analysis.core.StopAnalyzer.*;
import static org.apache.lucene.analysis.standard.StandardAnalyzer.*;
import static org.apache.lucene.analysis.standard.StandardAnalyzer.ENGLISH_STOP_WORDS_SET;

public class EnhancedCustomAnalyzer implements CustomAnalyzerInterface {

    Analyzer analyzer = new Analyzer() {



    /** Constructs a custom analyzer object. Basically the default constructor.
     *  @author Stefan Spirkl <spirkl@logos-gmbh.com>
     *  @return instance of Analyzer
     */

    /** Provides access to the source, a Reader Consumer and an instance of TokenFilter
     *  @author Stefan Spirkl <spirkl@logos-gmbh.com>
     *  @return currently null.
    */
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
        // tokenStream = new FlattenGraphFilter(new SynonymGraphFilter(tokenStream, createSynonymMap(), true));
        tokenStream = new StopFilter(tokenStream, ENGLISH_STOP_WORDS_SET);

        tokenStream = new SnowballFilter(tokenStream, new EnglishStemmer());

        return new TokenStreamComponents(tokenizer, tokenStream);
    }

    private SynonymMap createSynonymMap() {
        SynonymMap synMap = new SynonymMap(null, null, 0);
        try {
            BufferedReader countries = new BufferedReader(new FileReader( "/DataSet/countries.txt"));

            final SynonymMap.Builder builder = new SynonymMap.Builder(true);
            String country = countries.readLine();

            while(country != null) {
                builder.add(new CharsRef("country"), new CharsRef(country), true);
                builder.add(new CharsRef("countries"), new CharsRef(country), true);
                country = countries.readLine();
            }

            synMap = builder.build();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getLocalizedMessage() + "occurred when trying to create synonym map");
        }
        return synMap;
    }
};

}
