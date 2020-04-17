CS7IS3- Information Retrieval-2
Team:Group 8.
Team Name: You-Know-Who
README file for the Lucene Program.

SSH COMMAND
********************************************
ssh -i cs7is3.pem ubuntu@ec2-54-164-216-81.compute-1.amazonaws.com
********************************************

The maven project has been deployed.
Five steps has been mentioned below to run the program successfully. All these commands have been tested in the AWS instance successfully.

1. Please go to the directory to build the maven project.

********************************************
cd ~/CS7IS3/IR2/
********************************************

2. Please do mvn clean install.

********************************************
mvn clean install
********************************************

PARAMETERS

3. Once you go to the Jar file location you run the program with these given parameters in the order.

mvn exec:java -Dexec.mainClass="g8app.main" -Dexec.args= "[-query QUERY_FILE_PATH] [-savequery PATH_TO_SAVE_NEW_QUERY_FILE] [-stopdir STOPWORDS_FILE_PATH] [-output output_file]"
[-query] 	Path to the "topics" query file.
[-savequery]	Path to save the parsed and processed query file.
[-stopdir] 	Path to the "stopwords.txt" file.
[-output]	Path to where the output search results to be saved at.
[-score]	Score determines the which Similarity Index you want to use.

		0 for ClassicSimilarity
		1 for BM25Similarity (Submission 02)
		2 for BooleanSimilarity
		3 for LMDirichletSimilarity
		4 for LMJelinekMercerSimilarity (Lambda=0.6)
		5 for AxiomaticF1Exp
		6 for AxiomaticF1Log
		7 for AxiomaticF2Exp (Submission 01- Best MAP Score)
		8 for AxiomaticF2Log

(***THIS ONE WORKS AND PLEASE WAIT FOR A WHILE (-----TAKES AROUND 6 MINS-----), AS IT CREATES INDEX ***):

SUBMISSION-01
********************************************
mvn exec:java -Dexec.mainClass="G8.g8app" -Dexec.args="-query Files/topics -savequery Files/ -stopdir Files/stopwords.txt -output Files/output.txt -score 7"
********************************************

SUBMISSION-02
********************************************
mvn exec:java -Dexec.mainClass="G8.g8app" -Dexec.args="-query Files/topics -savequery Files/ -stopdir Files/stopwords.txt -output Files/output.txt -score 1"
********************************************

TREC_EVAL

4. Once you're done with generating the 'output.txt' file you are ready to compare with the TREC Eval file. To do that change the directory to the folder which contains the binary file of Trec Eval, as below.

********************************************
cd ~/CS7IS3/IR2/trec_eval-9.0.7
********************************************

5. Now you to run the TREC_EVAL file, run the below code.

********************************************
./trec_eval ~/CS7IS3/IR2/Files/qrels.assignment2.part1 ~/CS7IS3/IR2/Files/output.txt
********************************************