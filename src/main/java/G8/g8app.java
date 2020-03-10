package G8;


import java.io.IOException;
import java.io.PrintWriter;

public class g8app {
    private g8app() {}
    public static void main(String[] args) throws IOException {
        // ../Files path of the same folder present in our repository

        String queries = null;
        String savequery = null;
        String usage = "[-queries QUERY_FILE_PATH] [-savequery PATH_TO_SAVE_NEW_QUERY_FILE]";
        for(int i=0;i<args.length;i++) {
            if("-query".equals(args[i])){
                queries = args[i+1];
                i++;
            }
            else if ("-savequery".equals(args[i])){
                savequery = args[i+1];
                i++;
            }
        }

        if (queries == null || savequery == null) {
            System.err.println("Usage: " + usage);
            System.exit(1);
        }

        /* SAMPLE ARGUMENTS
        String queries = "../Files/topics"
        String savequery = "../Files"

        -query ../Desktop/IdeaProjects/IRG8/Files/topics/
        -savequery ../Desktop/IdeaProjects/IRG8/Files/

         */

        String mod1 = savequery+"filename.txt";
        //Notice the lack of / or \. Path must be given accordingly for 'savequery'.
        PrintWriter out = new PrintWriter(mod1);
        PrintWriter out2 = new PrintWriter(savequery+"simplified_query.txt");
        //Notice the lack of / or \. Path must be given accordingly for 'savequery'.
        qparse_01 init_parse = new qparse_01();
        init_parse.firstParseQuery(queries,out);
        out.close();
        init_parse.secondParseQuery(mod1,out2);
        out2.close();
        System.out.println("Initial Parsing Done.");
    }
}
