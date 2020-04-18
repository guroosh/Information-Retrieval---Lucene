# Lucene-Information-Retrieval-2.0
## CS7IS3: Group Project
### A study on Lucene Search Engine for TREC-8 Retreival Task
#### Group 8 for Information Retrieval.
#### Team Name: You-Know-Who

This project uses Intellij Workspace with Maven configured and Travis-CI.

A text search engine based on Apache Lucene was planned, implemented and iteratively improved over the course of several months. 

The text collection trec-45 was used which documents from the Financial Times Limited (1991, 1992, 1993, 1994), the Federal Register (1994), the Foreign Broadcast Information Service (1996) and the Los Angeles Times (1989, 1990). Most of the documents are news articles.

## Running the Project

This project uses Intellij Workspace with Maven configured and Travis-CI.

1. Clone the repository.

2. Go to the directory to build the maven project.

```
cd ~/../Lucene-Information-Retrieval-2/
```

3. Do a Maven clean install.

```
mvn clean install
```

4. You need to Maven Execute the program with these given parameters in the given order.

```
mvn exec:java -Dexec.mainClass="g8app.main" -Dexec.args= "[-query QUERY_FILE_PATH] [-savequery PATH_TO_SAVE_NEW_QUERY_FILE] [-stopdir STOPWORDS_FILE_PATH] [-output output_file]"
```
- ```[-query]```      Path to the "topics" query file.
- ```[-savequery]```  Path to save the parsed and processed query file.
- ```[-stopdir]```    Path to the "stopwords.txt" file.
- ```[-output]```     Path to where the output search results to be saved at.
- ```[-score]```      Score determines the which Similarity Index you want to use.

##### ```[-score]``` Parameter Values
- 0 for ClassicSimilarity
- 1 for BM25Similarity
- 2 for BooleanSimilarity
- 3 for LMDirichletSimilarity
- 4 for LMJelinekMercerSimilarity (Lambda=0.6)
- 5 for AxiomaticF1Exp
- 6 for AxiomaticF1Log
- 7 for AxiomaticF2Exp
- 8 for AxiomaticF2Log

## Evaluating the Output (TREC Eval)

Once you're done with generating the ```output.txt``` file you are ready to compare with the TREC Eval file.

1. Change the directory to the folder which contains the binary file of Trec Eval.

```
cd ~/../Lucene-Information-Retrieval-2/trec_eval-9.0.7
```

2. ```make``` to get an executable for TREC Eval.

```
make
```

3. Now run the TREC_EVAL against your output.

```
./trec_eval ~/../Lucene-Information-Retrieval-2/Files/qrels.assignment2.part1 ~/../Lucene-Information-Retrieval-2/Files/output.txt
```
