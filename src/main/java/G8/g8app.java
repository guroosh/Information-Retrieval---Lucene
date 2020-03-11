package G8;


import java.io.IOException;
import java.io.PrintWriter;

public class g8app {
    private g8app() {}
    public static void main(String[] args) throws IOException {
        // ../Files path of the same folder present in our repository

        String queries = null;
        String savequery = null;
        String stop_dir = null;
        String usage = "[-queries QUERY_FILE_PATH] [-savequery PATH_TO_SAVE_NEW_QUERY_FILE] [-stopdir STOPWORDS_FILE_PATH]";
        for(int i=0;i<args.length;i++) {
            if("-query".equals(args[i])){
                queries = args[i+1];
                i++;
            }
            else if ("-savequery".equals(args[i])){
                savequery = args[i+1];
                i++;
            }
            else if("-stopdir".equals(args[i])){
                stop_dir = args[i+1];
                i++;
            }
        }

        if (queries == null || savequery == null || stop_dir ==null) {
            System.err.println("Usage: " + usage);
            System.exit(1);
        }

        /* SAMPLE ARGUMENTS
        String queries = "../Files/topics"
        String savequery = "../Files/"
        String stop_dir = "../Files/stopwords.txt"

        -query ../Desktop/IdeaProjects/IRG8/Files/topics/
        -savequery ../Desktop/IdeaProjects/IRG8/Files/
        -stopdir../Desktop/IdeaProjects/IRG8/Files/stopwords.txt

         */

        String mod1 = savequery+"filename.txt";
        String mod2 = savequery+"simplified_query.txt";
        //Notice the lack of / or \. Path must be given accordingly for 'savequery'.
        PrintWriter out = new PrintWriter(mod1);
        PrintWriter out2 = new PrintWriter(mod2);
        PrintWriter out3 = new PrintWriter(savequery+"final_query.txt");
        //Notice the lack of / or \. Path must be given accordingly for 'savequery'.
        qparse_01 init_parse = new qparse_01();
        init_parse.firstParseQuery(queries,out);
        out.close();
        init_parse.secondParseQuery(mod1,out2);
        out2.close();
        System.out.println("Initial Parsing Done.");
        qparse_02.thirdParseQuery(stop_dir,out3,mod2);
        out3.close();
        System.out.println("Final Parsing Done.");
    }
}
