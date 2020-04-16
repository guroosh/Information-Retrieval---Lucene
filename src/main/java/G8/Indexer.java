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
import java.util.HashMap;
import java.util.List;

public class Indexer {

    public static BufferedWriter bw;
    private static IndexWriter indexWriter;
    public static BufferedWriter docfields;
    private static HashMap<String,String> fields = new HashMap<String,String>(); // to store all
    // fields


    private static void indexDocs(Document doc,String filename)
            throws IOException, ParserConfigurationException, SAXException {

        NodeList children = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element e1 = (Element) child;
                NodeList nodeList = e1.getElementsByTagName("*");
                //NodeList nodeList = e1.getChildNodes();
                org.apache.lucene.document.Document luceneDocument = new org.apache.lucene.document.Document();
                for (int idx = 0; idx < nodeList.getLength(); idx++) {
                    // Get element
                    Element element =  (Element)nodeList.item(idx);
                    String tagname = element.getNodeName();
                    /*if (tagname == "P") {// skip P tag, because P is already included in its parent tag
                        continue;
                    }*/
                    //System.out.println(tagname+"--"+element.getParentNode().getNodeName());
                    if(element.getParentNode().getNodeName() != "DOC"){
                        continue;
                    }
                    String tagvalue = e1.getElementsByTagName(element.getNodeName()).item(0).getTextContent().trim();
                    luceneDocument.add(new TextField(tagname, tagvalue, Field.Store.YES));
                    fields.put(tagname, filename+e1.getElementsByTagName("DOCNO").item(0).getTextContent());
                }
                luceneDocument.add(new StringField("filename", filename, Field.Store.YES));
                indexWriter.addDocument(luceneDocument);
            }
        }


    }

    private static void indexLATimes() throws IOException, ParserConfigurationException, SAXException {
        File files[] = new File("Files/docs/latimes/").listFiles();
        assert files != null;
        for (File file : files) {
            List<InputStream> streams = Arrays.asList(new ByteArrayInputStream("<root>".getBytes()),
                    new FileInputStream(file), new ByteArrayInputStream("</root>".getBytes()));
            if (!file.getAbsolutePath().contains("read")) {
                InputStream is = new SequenceInputStream(Collections.enumeration(streams));
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
                indexDocs(doc,file.getAbsolutePath());
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
                    List<InputStream> streams = Arrays.asList(new ByteArrayInputStream("<root>".getBytes()),
                            new FileInputStream(file2), new ByteArrayInputStream("</root>".getBytes()));
                    if (!file.getAbsolutePath().contains("read")) {
                        InputStream is = new SequenceInputStream(Collections.enumeration(streams));
                        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
                        indexDocs(doc,file.getAbsolutePath());
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
                    List<InputStream> streams = Arrays.asList(new ByteArrayInputStream("<root>".getBytes()),
                            inputStream, new ByteArrayInputStream("</root>".getBytes()));
                    if (!file.getAbsolutePath().contains("read")) {
                        InputStream is = new SequenceInputStream(Collections.enumeration(streams));
                        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
                        indexDocs(doc,file.getAbsolutePath());
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
            List<InputStream> streams = Arrays.asList(new ByteArrayInputStream("<root>".getBytes()), inputStream,
                    new ByteArrayInputStream("</root>".getBytes()));
            if (!file.getAbsolutePath().contains("read")) {
                InputStream is = new SequenceInputStream(Collections.enumeration(streams));
                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
                indexDocs(doc,file.getAbsolutePath());
            }
        }
    }

    // add buildAllIndex method, it is the same as the main method blow, just more
    // convenient for other classes
    public static HashMap<String,String> buildAllIndex() throws IOException, ParserConfigurationException, SAXException {
        System.out.println("Deleting old index");
        deleteOldIndex();
        FSDirectory dir = FSDirectory.open(Paths.get("index/"));
        IndexWriterConfig config = new IndexWriterConfig(new CustomEnglishAnalyzer());
        // IndexWriterConfig config = new IndexWriterConfig(new MyAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        indexWriter = new IndexWriter(dir, config);

        bw = new BufferedWriter(new FileWriter(new File("testFile.txt")));
        docfields = new BufferedWriter(new FileWriter(new File("fields.txt")));

        System.out.println("Starting LATimes");
        indexLATimes();
        System.out.println("Starting FT");
        indexFT();
        System.out.println("Starting FR94");
        indexFR94();
        System.out.println("Starting FBIS");
        indexFBIS();
        fields.put("filename", "");// filename is a default field for all docs
        docfields.write(String.join(",", fields.keySet()));

        docfields.close();
        bw.close();
        indexWriter.close();
        System.out.println("Index created");
        return fields;
    }

    public static void main(String args[]) throws IOException, ParserConfigurationException, SAXException {
        System.out.println("Deleting old index");
        deleteOldIndex();
        FSDirectory dir = FSDirectory.open(Paths.get("index/"));
        IndexWriterConfig config = new IndexWriterConfig(new CustomEnglishAnalyzer());
        // IndexWriterConfig config = new IndexWriterConfig(new MyAnalyzer());
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
 * <DOC> <DOCNO> LA020589-0014 </DOCNO> <DOCID> 14271 </DOCID> <DATE> <P>
 * Correction Appended </P> </DATE> <CORRECTION-DATE> <P> March 26, 1989,
 * Sunday, Home Edition </P> </CORRECTION-DATE> <CORRECTION> <P> FOR THE RECORD
 * </P> <P> Jean Bryant, director of the Ralph W. Miller Golf Library in
 * Industry Hills, is the widow of Bill Bryant, who was general manager of the
 * Industry Hills Recreation and Conference Center. -- The Editors </P>
 * </CORRECTION> </DOC>
 */