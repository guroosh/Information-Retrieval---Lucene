package G8;

/*
WHAT THIS CLASS DOES?

It has two functions which simplifies the query file.
The original query file has,
* empty new lines
* topics (whose words are anyways repeated in description and narrative)
* Not a single line narrative or description

All these are corrected and the two functions together outputs a simplified file.

In future if we decide to use topics, we can just duplicate what we've done for,
narrative and description in the 2nd function to obtain that without,
much changes to the code.
 */


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.util.Date;
//import java.util.HashMap;

public class qparse_01 {

    public qparse_01() {}

    public static void main(String[] args) throws IOException {

    }

        public void firstParseQuery(String queries, PrintWriter wrt) throws IOException {

            String nextLine = null;
            int counter = 0;
            BufferedReader in = null;
            if (queries != null) {
                try {
                    in = Files.newBufferedReader(Paths.get(queries), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Invalid path to Query File.");
                }
            }
//        System.out.print(in);
            String line = in.readLine();
            System.out.println("Seq 1 Parsing in Process");
            while (true) {
//            System.out.print(line);
                if (line == null || line.length() == -1) {
//                    System.out.println("Loop0");
                    break;
                }

                line = line.trim();
                if (line.length() == 0) {
//                    System.out.println("Loop-1");
                    break;
                }

                if (line.substring(0, 5).equals("<top>")) {
                    line = in.readLine();
//                    System.out.println("Loop1");
                    counter = 1;
                    nextLine = "";
                    do {
                        line = in.readLine();
                        if (line == null) {
                            break;
                        }
                        if (line.length() == 0) {
                            line = in.readLine();
                        }
                        nextLine = nextLine + "\n" + line;
//                        System.out.print(counter);
//                        System.out.println("");
//                        System.out.print(nextLine);
                        if (line == null) {
//                        System.out.print(nextLine);
//                            System.out.println("Loop3");
                            break;
                        }
                    } while (!line.trim().equals("/<top>"));
//                out.println(nextLine);
                }
                wrt.println(nextLine);
            }
            in.close();
        }

    public void secondParseQuery(String mod1, PrintWriter wrt) throws IOException {
        String nextLine2 = "";
        int counter2 = 0;
        BufferedReader in2 = null;
        if (mod1 != null) {
            try {
                in2 = Files.newBufferedReader(Paths.get(mod1), StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.out.println("Invalid path to Query File");
                e.printStackTrace();
            }
        }
        System.out.println("Seq 2 Parsing in Process.");
        String line2 = in2.readLine();
        int flag=0; // For Number
        int flag2=0; // For Description
        int flag3=0; // For Narrative
        while(true){
            line2=in2.readLine();
            if(line2 == null || line2.length() == 0){
                break;
            }
            else{
                line2 = line2.trim();
                if(flag==0){
                    flag++;
//                    System.out.print(line2);
                    if (line2.substring(0, 5).equals("<num>")){
                        nextLine2 = nextLine2+"<num>"+"\n"+line2.substring(line2.length()-3)+"\n";

                        wrt.print(nextLine2);
//                        System.out.print(nextLine2);
                        nextLine2="";
                    }
                }
                else {
                    if (line2.substring(0, 5).equals("<num>")) {
                        nextLine2 = nextLine2+"\n"+ "<num>" + "\n" + line2.substring(line2.length() - 3) + "\n";

                        wrt.print(nextLine2);
//                        System.out.print(nextLine2);
                        nextLine2 = "";
                    }
                    if (line2.startsWith("<desc>")) {
                        nextLine2 = nextLine2 + "<desc>" + "\n";

                        wrt.print(nextLine2);
//                        System.out.print(nextLine2);
                        nextLine2 = "";

                        while (true) {
                            line2 = in2.readLine();
                            if (line2.startsWith("<")) {
                                flag2=0;
                                break;
                            } else {
                                {
                                    if(flag2==0){
                                        line2 = line2.trim();
                                        nextLine2 = nextLine2 + line2;
                                        flag2++;
                                    }
                                    else{
                                        line2 = line2.trim();
                                        nextLine2 = nextLine2 +" "+ line2;
                                    }

                                }
//                                System.out.print(nextLine2);
                                wrt.print(nextLine2);
                                nextLine2 = "";
                            }
                        }
                    }
                    if (line2.startsWith("<narr>")) {
                        nextLine2 = nextLine2 +"\n"+ "<narr>" + "\n";

//                        System.out.print(nextLine2);
                        wrt.print(nextLine2);
                        nextLine2 = "";

                        while (true) {
                            line2 = in2.readLine();
                            if (line2.startsWith("<")) {
                                flag3=0;
                                break;
                            } else {
                                {
                                    if(flag3==0){
                                        line2 = line2.trim();
                                        nextLine2 = nextLine2+line2;
                                        flag3++;
                                    }
                                    else{
                                        line2 = line2.trim();
                                        nextLine2 = nextLine2 +" "+line2;
                                    }
                                }
//                                System.out.print(nextLine2);
                                wrt.print(nextLine2);
                                nextLine2 = "";
                            }
                        }
                    }
                }
            }
        }
        in2.close();
        File f= new File(mod1);
        f.delete();
    }
}

