package G8;


import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Indexer {

    public static BufferedWriter bw;
    private static IndexWriter indexWriter;

    private static void indexLATimes() throws IOException, ParserConfigurationException, SAXException {
        File files[] = new File("Files/docs/latimes/").listFiles();
        assert files != null;
        for (File file : files) {
            List<InputStream> streams = Arrays.asList(
                    new ByteArrayInputStream("<root>".getBytes()),
                    new FileInputStream(file),
                    new ByteArrayInputStream("</root>".getBytes())
            );
            if (!file.getAbsolutePath().contains("read")) {
                InputStream is = new SequenceInputStream(Collections.enumeration(streams));
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
                NodeList children = doc.getDocumentElement().getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Element e1 = (Element) child;
                        String docNum = e1.getElementsByTagName("DOCNO").item(0).getTextContent().trim();
                        String docId = e1.getElementsByTagName("DOCID").item(0).getTextContent().trim();
                        String date = ((Element) e1.getElementsByTagName("DATE").item(0)).getElementsByTagName("P").item(0).getTextContent().trim();
                        String section = "";
                        try {
                            section = ((Element) e1.getElementsByTagName("SECTION").item(0)).getElementsByTagName("P").item(0).getTextContent().trim();
                        } catch (NullPointerException ignored) {

                        }
                        String length = "";
                        try {
                            length = ((Element) e1.getElementsByTagName("LENGTH").item(0)).getElementsByTagName("P").item(0).getTextContent().trim();
                        } catch (NullPointerException ignored) {
                        }
                        Element textElement = (Element) e1.getElementsByTagName("TEXT").item(0);
                        Element graphicElement = (Element) e1.getElementsByTagName("GRAPHIC").item(0);
                        Element headlineElement = (Element) e1.getElementsByTagName("HEADLINE").item(0);
                        StringBuilder text = new StringBuilder();
                        StringBuilder graphic = new StringBuilder();
                        StringBuilder headline = new StringBuilder();
                        if (textElement != null) {
                            NodeList textList = textElement.getElementsByTagName("P");
                            for (int j = 0; j < textList.getLength(); j++) {
                                text.append(textList.item(j).getTextContent().replace("\n", "").trim()).append(" ");
                            }
                        }
                        if (graphicElement != null) {
                            NodeList graphicList = graphicElement.getElementsByTagName("P");
                            for (int j = 0; j < graphicList.getLength(); j++) {
                                graphic.append(graphicList.item(j).getTextContent().replace("\n", "").trim()).append(" ");
                            }
                        }
                        if (headlineElement != null) {
                            NodeList headlineList = headlineElement.getElementsByTagName("P");
                            for (int j = 0; j < headlineList.getLength(); j++) {
                                headline.append(headlineList.item(j).getTextContent().replace("\n", "").trim()).append(" ");
                            }
                        }
                        if (text.toString().equals("") && graphic.toString().equals("") && headline.toString().equals("")) {
//                            System.out.println(docNum);
                        }
//                        System.out.println(docNum + "\t" + text + ", " + graphic + ", " + headline);
                        try {
                            bw.write(docNum + "\t" + text.substring(0, 50) + ", " + graphic.substring(0, 50) + ", " + headline.substring(0, 50));
                            bw.newLine();
                        } catch (StringIndexOutOfBoundsException ignored) {

                        }
                        org.apache.lucene.document.Document luceneDocument = new org.apache.lucene.document.Document();
                        luceneDocument.add(new TextField("id", docNum, Field.Store.YES));
                        luceneDocument.add(new TextField("text", text + " " + graphic + " " + headline, Field.Store.YES));
                        luceneDocument.add(new StringField("filename", file.getAbsolutePath(), Field.Store.YES));
                        indexWriter.addDocument(luceneDocument);
                    }
                }
            }
        }
    }

    private static void indexFT() throws IOException, ParserConfigurationException, SAXException {
        File files[] = new File("Files/docs/ft/").listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                File innerFiles[] = file.listFiles();
                assert innerFiles != null;
                for (File file2 : innerFiles) {
                    List<InputStream> streams = Arrays.asList(
                            new ByteArrayInputStream("<root>".getBytes()),
                            new FileInputStream(file2),
                            new ByteArrayInputStream("</root>".getBytes())
                    );
                    if (!file.getAbsolutePath().contains("read")) {
                        InputStream is = new SequenceInputStream(Collections.enumeration(streams));
                        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
                        NodeList children = doc.getDocumentElement().getChildNodes();
                        for (int i = 0; i < children.getLength(); i++) {
                            Node child = children.item(i);
                            if (child.getNodeType() == Node.ELEMENT_NODE) {
                                Element e1 = (Element) child;
                                String docNum = e1.getElementsByTagName("DOCNO").item(0).getTextContent().trim();

                                String text = "";
                                try {
                                    text = e1.getElementsByTagName("TEXT").item(0).getTextContent().replace("\n", " ").trim();
                                } catch (NullPointerException ignored) {

                                }
                                String headline = "";
                                try {
                                    headline = e1.getElementsByTagName("HEADLINE").item(0).getTextContent().replace("\n", " ").trim();
                                } catch (NullPointerException ignored) {

                                }
                                if (text.equals("") && headline.equals("")) {
//                                    System.out.println(docNum);
                                }
//                                System.out.println(docNum + "\t" + text + ", " + headline);
                                try {
                                    bw.write(docNum + "\t" + text.substring(0, 50) + ", " + headline.substring(0, 50));
                                    bw.newLine();
                                } catch (StringIndexOutOfBoundsException ignored) {

                                }
                                org.apache.lucene.document.Document luceneDocument = new org.apache.lucene.document.Document();
                                luceneDocument.add(new TextField("id", docNum, Field.Store.YES));
                                luceneDocument.add(new TextField("text", text + " " + headline, Field.Store.YES));
                                luceneDocument.add(new StringField("filename", file2.getAbsolutePath(), Field.Store.YES));
                                indexWriter.addDocument(luceneDocument);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void indexFR94() throws IOException, ParserConfigurationException, SAXException {
        File files[] = new File("Files/docs/fr94/").listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                File innerFiles[] = file.listFiles();
                assert innerFiles != null;
                for (File file2 : innerFiles) {
                    String contents = new String(Files.readAllBytes(Paths.get(file2.getPath())));
                    contents = contents.replaceAll("&hyph;", "-");
                    contents = contents.replaceAll("&.*?;", " ");
                    InputStream inputStream = new ByteArrayInputStream(contents.getBytes(Charset.forName("UTF-8")));
                    List<InputStream> streams = Arrays.asList(
                            new ByteArrayInputStream("<root>".getBytes()),
                            inputStream,
                            new ByteArrayInputStream("</root>".getBytes())
                    );
                    if (!file.getAbsolutePath().contains("read")) {
                        InputStream is = new SequenceInputStream(Collections.enumeration(streams));
                        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
                        NodeList children = doc.getDocumentElement().getChildNodes();
                        for (int i = 0; i < children.getLength(); i++) {
                            Node child = children.item(i);
                            if (child.getNodeType() == Node.ELEMENT_NODE) {
                                Element e1 = (Element) child;
                                String docNum = e1.getElementsByTagName("DOCNO").item(0).getTextContent().trim();

                                String text = "";
                                try {
                                    text = e1.getElementsByTagName("TEXT").item(0).getTextContent().replace("\n", " ").trim();
                                } catch (NullPointerException ignored) {

                                }

                                if (text.equals("")) {
//                                    System.out.println(docNum);
                                }
//                                System.out.println(docNum + "\t" + text);
                                try {
                                    bw.write(docNum + "\t" + text.substring(0, 50));
                                    bw.newLine();
                                } catch (StringIndexOutOfBoundsException ignored) {

                                }
                                org.apache.lucene.document.Document luceneDocument = new org.apache.lucene.document.Document();
                                luceneDocument.add(new TextField("id", docNum, Field.Store.YES));
                                luceneDocument.add(new TextField("text", text, Field.Store.YES));
                                luceneDocument.add(new StringField("filename", file2.getAbsolutePath(), Field.Store.YES));
                                indexWriter.addDocument(luceneDocument);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void indexFBIS() throws IOException, ParserConfigurationException, SAXException {
        File files[] = new File("Files/docs/fbis/").listFiles();
        assert files != null;
        for (File file : files) {

            String contents = new String(Files.readAllBytes(Paths.get(file.getPath())));

            contents = contents.replaceAll("&.*?;", "");
            contents = contents.replaceAll(" P=[0-9]+", "");
            contents = contents.replaceAll(" ID=[A-Z0-9]*-[A-Z0-9]*-[A-Z0-9]*-[A-Z0-9]*", "");
            contents = contents.replaceAll("<3>", "");
            contents = contents.replaceAll("</3>", "");
            contents = contents.replaceAll("&-\\|", "");
            contents = contents.replaceAll("\\|amp;", "");
            contents = contents.replaceAll("&\\|", "");
            contents = contents.replaceAll("\\|yen;", "");
            InputStream inputStream = new ByteArrayInputStream(contents.getBytes(Charset.forName("UTF-8")));
            List<InputStream> streams = Arrays.asList(
                    new ByteArrayInputStream("<root>".getBytes()),
                    inputStream,
                    new ByteArrayInputStream("</root>".getBytes())
            );
            if (!file.getAbsolutePath().contains("read")) {
                InputStream is = new SequenceInputStream(Collections.enumeration(streams));
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
                NodeList children = doc.getDocumentElement().getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);
                    if (child.getNodeType() == Node.ELEMENT_NODE) {
                        Element e1 = (Element) child;
                        String docNum = e1.getElementsByTagName("DOCNO").item(0).getTextContent().trim();
                        String text = "";
                        try {
                            text = e1.getElementsByTagName("TEXT").item(0).getTextContent().replace("\n", " ").trim();
                        } catch (NullPointerException ignored) {

                        }
                        if (text.equals("")) {
//                            System.out.println(docNum);
                        }
//                        System.out.println(docNum + "\t" + text);
                        try {
                            bw.write(docNum + "\t" + text.substring(0, 50));
                            bw.newLine();
                        } catch (StringIndexOutOfBoundsException ignored) {

                        }
                        org.apache.lucene.document.Document luceneDocument = new org.apache.lucene.document.Document();
                        luceneDocument.add(new TextField("id", docNum, Field.Store.YES));
                        luceneDocument.add(new TextField("text", text, Field.Store.YES));
                        luceneDocument.add(new StringField("filename", file.getAbsolutePath(), Field.Store.YES));
                        indexWriter.addDocument(luceneDocument);
                    }
                }
            }
        }
    }

    // add buildAllIndex method, it is the same as the main method blow, just more convenient for other classes
    public static void buildAllIndex()throws IOException, ParserConfigurationException, SAXException{
        System.out.println("Deleting old index");
        deleteOldIndex();
        FSDirectory dir = FSDirectory.open(Paths.get("index/"));
        IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
//        IndexWriterConfig config = new IndexWriterConfig(new MyAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        indexWriter = new IndexWriter(dir, config);

        bw = new BufferedWriter(new FileWriter(new File("testFile.txt")));

        System.out.println("Starting LATimes");
        indexLATimes();
        System.out.println("Starting FT");
        indexFT();
        System.out.println("Starting FR94");
        indexFR94();
        System.out.println("Starting FBIS");
        indexFBIS();

        bw.close();
        indexWriter.close();
        System.out.println("Index created");
    }

    public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException {
        System.out.println("Deleting old index");
        deleteOldIndex();
        FSDirectory dir = FSDirectory.open(Paths.get("index/"));
        IndexWriterConfig config = new IndexWriterConfig(new EnglishAnalyzer());
//        IndexWriterConfig config = new IndexWriterConfig(new MyAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        indexWriter = new IndexWriter(dir, config);

        bw = new BufferedWriter(new FileWriter(new File("testFile.txt")));

        System.out.println("Starting LATimes");
        indexLATimes();
        System.out.println("Starting FT");
        indexFT();
        System.out.println("Starting FR94");
        indexFR94();
        System.out.println("Starting FBIS");
        indexFBIS();

        bw.close();
        indexWriter.close();
        System.out.println("Index created");
    }

    private static void deleteOldIndex() {
        File index = new File("index/");
        if (!index.exists()) {
            index.mkdir();
        }
        String[] entries = index.list();
        assert entries != null;
        for (String s : entries) {
            File currentFile = new File(index.getPath(), s);
            currentFile.delete();
        }
    }

}



/*
<DOC>
<DOCNO> LA020589-0014 </DOCNO>
<DOCID> 14271 </DOCID>
<DATE>
<P>
Correction Appended
</P>
</DATE>
<CORRECTION-DATE>
<P>
March 26, 1989, Sunday, Home Edition
</P>
</CORRECTION-DATE>
<CORRECTION>
<P>
FOR THE RECORD
</P>
<P>
Jean Bryant, director of the Ralph W. Miller Golf Library in Industry Hills, is
the widow of Bill Bryant, who was general manager of the Industry Hills
Recreation and Conference Center. -- The Editors
</P>
</CORRECTION>
</DOC>
*/
