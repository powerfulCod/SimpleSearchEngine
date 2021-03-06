package SearchComponents;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.text.DecimalFormat;
import java.util.*;


public class SearchEngine {

	private static NaiveInvertedIndex index;
	private static List<String> fileNames;
	private static int gloTermLength;
	private static List<String> results;
	private static Path curPath;
	
	public static void tempmain(Path setPath) throws IOException{
		
		final Path currentWorkingPath = setPath;
		curPath = setPath;
		// the Positional index
		//final NaiveInvertedIndex index = new NaiveInvertedIndex();
		index = new NaiveInvertedIndex();

		// the list of file names that were processed
		//final List<String> fileNames = new ArrayList<String>();
		fileNames = new ArrayList<String>();

		// This is our standard "walk through all .txt files" code.
		Files.walkFileTree(currentWorkingPath, new SimpleFileVisitor<Path>() {
			int mDocumentID  = 0;

			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) {
				// make sure we only process the current working directory
				if (currentWorkingPath.equals(dir)) {
					return FileVisitResult.CONTINUE;
				}
				return FileVisitResult.SKIP_SUBTREE;
			}

			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) {
				// only process .txt files
				if (file.toString().endsWith(".txt")) {
					// we have found a .txt file; add its name to the fileName list,
					// then index the file and increase the document ID counter.
					System.out.println("Indexing file " + file.getFileName() + " DocID " + mDocumentID);
					fileNames.add(file.getFileName().toString());
					indexFile(file.toFile(), index, mDocumentID);
					mDocumentID++;
				}
				return FileVisitResult.CONTINUE;
			}

			// don't throw exceptions if files are locked/other errors occur
			public FileVisitResult visitFileFailed(Path file,
					IOException e) {

				return FileVisitResult.CONTINUE;
			}

		});
		index.finalize();
		printResults(index, fileNames);
		printStatistics(index, fileNames);
	}
	

	/**
	   Indexes a file by reading a series of tokens from the file, treating each 
	   token as a term, and then adding the given document's ID to the inverted
	   index for the term.
	   @param file a File object for the document to index.
	   @param index the current state of the index for the files that have already
	   been processed.
	   @param docID the integer ID of the current document, needed when indexing
	   each term from the document.
	 */
	private static void indexFile(File file, NaiveInvertedIndex index, 
			int docID) {
		// Construct a SimpleTokenStream for the given File.
		// Read each token from the stream and add it to the index.
		try {
			AdvancedTokenStream readFile = new AdvancedTokenStream(file);
			//Each file will have a new term position counter
			int termPos = 0;
			while(readFile.hasNextToken()){
				/*if hyphenanted token, remove hyphen and create a token, than split
				 *original token into two tokens without a hyphen
				 */
				String curToken;
				if(readFile.isHyphenatedToken()){
					System.out.println("In hypen");
					curToken = readFile.nextHyphenToken();
					//Remove hyphens from token, then add type and term to their respective index
					String noHypenToken = curToken.replaceAll(" ", "");
					index.addType(noHypenToken, docID);
					index.addTerm(noHypenToken, docID, termPos);
					termPos++;
					//Steps to process original token into two tokens without hyphen
					AdvancedTokenStream readHyphenToken = new AdvancedTokenStream(curToken);
					while(readHyphenToken.hasNextToken()){
						curToken = readHyphenToken.nextToken();
						index.addType(curToken, docID);
						index.addTerm(PorterStemmer.processToken(curToken), docID, termPos);
						termPos++;
					}
				}else{
					curToken = readFile.nextToken();
					index.addType(curToken, docID);
					String stemmedToken = PorterStemmer.processToken(curToken);
					index.addTerm(stemmedToken, docID, termPos);
					termPos++;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void printResults(NaiveInvertedIndex index, 
			List<String> fileNames) {

		// TO-DO: print the inverted index.
		// Retrieve the dictionary from the index. (It will already be sorted.)
		// For each term in the dictionary, retrieve the postings list for the
		// term. Use the postings list to print the list of document names that
		// contain the term. (The document ID in a postings list corresponds to 
		// an index in the fileNames list.)

		// Print the postings list so they are all left-aligned starting at the
		// same column, one space after the longest of the term lengths. Example:
		// 
		// as:      document0 document3 document4 document5
		// engines: document1
		// search:  document2 document4  

		String[] termsList;		// list of terms
		int maxTermLength = 0;   // length of the longest term

		// find the length of the longest term
		termsList = index.getDictionary();
		for(int i = 0; i < termsList.length; i++){
			if(termsList[i].length() > maxTermLength){
				maxTermLength = termsList[i].length();
			}
		}
		gloTermLength = maxTermLength;
		// print out the terms and the respective postings
		for(int j = 0; j < termsList.length; j++){
			String currentLine = "";
			List<Integer> postings = index.getPostings(termsList[j]);

			// insert term
			int spacesNeeded = maxTermLength + 1 - termsList[j].length();

			// insert spaces to align postings
			currentLine = currentLine + termsList[j] + ":";
			while(spacesNeeded > 0){
				currentLine = currentLine + " ";
				spacesNeeded--;
			}

			// insert associated documents
			for(int k = 0; k < postings.size(); k++){
				Integer docIndex = postings.get(k);	// get the document ID
				List<Integer> positions = index.getTermPositions(termsList[j], docIndex);
				
				currentLine = currentLine + fileNames.get(docIndex) + "<";
				for(int n = 0; n < positions.size(); n++){
					currentLine = currentLine + positions.get(n) + ", ";
				}
				currentLine = currentLine.substring(0, (currentLine.length()-2));
				currentLine = currentLine + "> ";
			}
			currentLine = currentLine.trim();

			System.out.println(currentLine);
		}
	}
	
	private static void printStatistics(NaiveInvertedIndex index, List<String> fileNames){
		double[] topFreq = index.getTopTermFreq();
		DecimalFormat numForm = new DecimalFormat("#.00");
		
		System.out.println("Number of Types: " + index.getNumTypes());
		System.out.println("Number of Terms: " + index.getNumTerms());
		System.out.println("Average Number of Documents per Posting: " + numForm.format(index.getAvgPosts()));
		System.out.print("The document frequencies of the top 10 terms are: ");
		for(int x = 0; x < 10; x++){
			System.out.print(numForm.format(topFreq[x] * 100) + "% ");
		}
		System.out.println();
		System.out.println("The approximate total memory requirements is: " + index.getTotalIndexSize() + " bytes");
		
	}
	
	
	public static String getStatistics(){
		String stats = new String();
		DecimalFormat numForm = new DecimalFormat("#.00");
		double[] topFreq = index.getTopTermFreq();
		stats = "Number of Types: " + index.getNumTypes() + "\n";
		stats = stats + "Number of Terms: " + index.getNumTerms() + "\n";
		stats = stats + "Average Number of Documents per Posting: " + numForm.format(index.getAvgPosts()) + "\n";
		stats = stats + "The document frequencies of the top 10 terms are: ";
		for(int x = 0; x < 10; x++){
			stats = stats + numForm.format(topFreq[x] * 100) + "% ";
		}
		stats = stats + "\n";
		stats = stats + "The approximate total memory requirements is: " + index.getTotalIndexSize() + " bytes" + "\n\n";
		return stats;
	}
	
	//Stub for Query Processing
	public static void processQuery(String word){
		word = word.toLowerCase();
		word = PorterStemmer.processToken(word);
		System.out.println("Processing word: " + word);
		List<Integer> postings = index.getPostings(word);
		results = new ArrayList<String>();
		String currentLine = "";
		System.out.printf("%-" + gloTermLength + "s %s",word+":", "");
		if(postings != null){
			for(int docID : postings){
				List<Integer> positions = index.getTermPositions(word, docID);
				results.add(fileNames.get(docID));
				currentLine = fileNames.get(docID) + "<";
				for(int posID = 0; posID < positions.size(); posID++){
					currentLine = currentLine + positions.get(posID) + ",";
				}
				currentLine = currentLine.substring(0, currentLine.length()-1);
				currentLine = currentLine + ">";
				currentLine = currentLine.trim();
				System.out.print(currentLine + " ");
			}
			System.out.println("");
		}else{
			System.out.println("");
		}

	}

	public static List<String> getqueryResult(){
		return results;
	}
	
	public static Path getPath(){
		return curPath;
	}

}
