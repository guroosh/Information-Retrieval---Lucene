package G8;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

/** Deals with the analyzer part of the project */
public class EnhancedCustomAnalyzer extends Analyzer implements CustomAnalyzerInterface {

    /** Constructs a custom analyzer object.
     *  Currently simply the EnglishAnalyzer, but will be extended here.
     *  @author Stefan Spirkl <spirkl@logos-gmbh.com>
     *  @return instance of Analyzer
     */
    @Override
    public Analyzer EnhancedCustomAnalzer() {
        return new EnglishAnalyzer();
    }

    /** Provides  access to the source, a Reader Consumer and an instance of TokenFilter
     *  @author Stefan Spirkl <spirkl@logos-gmbh.com>
     *  @return currently null.
    */
    @Override
    protected TokenStreamComponents createComponents(String s) {
        return null;
    }
}
