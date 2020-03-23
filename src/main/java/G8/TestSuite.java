package G8;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


class TestSuite {

    /** Executes a full test run and generates output file.
     *  Mainly added for convenience to run it from IDE.
     *  @author Stefan Spirkl <spirkl@logos-gmbh.com>
     */
    @Test
    private void buildAndMeasureMAP() throws IOException {
        /* given */
        final String queries = "C:\\Users\\resistance\\Desktop\\IR2\\IR2\\Files\\topics";
        final String savequery ="C:\\Users\\resistance\\Desktop\\IR2\\IR2\\Files";
        final String stop_dir = "C:\\Users\\resistance\\Desktop\\IR2\\IR2\\Files\\stopwords.txt";
        final String output_file = "C:\\Users\\resistance\\Desktop\\IR2\\IR2\\Files\\outputs.txt";

        /* when */
        g8app testInstance = new g8app(queries, savequery, stop_dir, output_file);
        String[] param = new String[0];
        g8app.main(param);
    }
}




