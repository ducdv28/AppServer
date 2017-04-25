package summary;

import java.io.*;
import java.text.Normalizer;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import jvnsegmenter.CRFSegmenter;
import jvnsensegmenter.JVnSenSegmenter;

//class MyRunnable implements Runnable {
//
//	private String fName;
//	private int cnt;
//
//	public MyRunnable(int cnt, String name) {
//		fName = name;
//		this.cnt = cnt;
//
//	}
//
//	public void run() {
//		long startTime = System.currentTimeMillis();
//		Summarizer summarizer = new Summarizer();
//		summarizer.preProcessDocument(fName);
//		summarizer.buildGraph();
//		summarizer.invokePageRanking();
//		// System.out.println(summarizer.getSummary());
//		Summarizer.allSummary[cnt] = summarizer.getSummary();
//		summarizer.displaySummary();
//		long endTime = System.currentTimeMillis();
//		long totalTime = endTime - startTime;
//		System.out.println("Time: " + totalTime);
//	}
//}

public class Summarizer {
	public static String fileName;
	public static int MAX_SENTENCES;
	public GooglePageRank pageRank;
	public LinkedList<GraphNode> sentences;
	public ConcurrentMap<String, TCandISC> freqs;
	public ConcurrentMap<String, Double> similarityGraph;
	public static String[] allSummary = new String[8];

	public Summarizer() {
		sentences = new LinkedList<GraphNode>();
		// freqs = new ConcurrentHashMap<String, TCandISC>();// Words and Term
		// Frequency &Inv
		// sentence
		// frequency
		similarityGraph = new ConcurrentHashMap<String, Double>();
		pageRank = new GooglePageRank(similarityGraph);
	}

	public static char removeAccent(char t) { // Bo dau tieng viet
		String s = t + "";
		String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(temp).replaceAll("").charAt(0);
	}

	// Tien xu ly//
	public void preProcessDocument(String fileName) {

		// Xu ly tu dung
		// Doc danh sach tu dung
		HashMap<Character, ArrayList<String>> stopWords = new HashMap<Character, ArrayList<String>>();
		String fi = "lib/StopWords.txt";
		BufferedReader br1 = null;

		String li = "";
		try {
			br1 = new BufferedReader(new FileReader(fi));
			while ((li = br1.readLine()) != null) {
				li = li.trim();

				char temp = Summarizer.removeAccent(li.charAt(0));
				if (stopWords.containsKey(temp)) {
					stopWords.get(temp).add(li);
				} else {
					ArrayList<String> listWords = new ArrayList<String>();
					listWords.add(li);
					stopWords.put(temp, listWords);
				}
			}
			br1.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Doc file
//		BufferedReader br = null;
//		try {
//			br = new BufferedReader(new FileReader(fileName));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
		String line = "";
		int sentenceCounter = 1;

		 //Khai bao thu vien tach cau, tach tu
		 JVnSenSegmenter sd = new JVnSenSegmenter();
		 sd.init("models/jvnsensegmenter");

		 CRFSegmenter ws = new CRFSegmenter();
		 ws.init("models/jvnsegmenter");


//		try {
//
//			while ((line = br.readLine()) != null) {
				line = fileName;
				System.out.println(fileName);
				System.out.println(line);
				line = line.trim();
//				if (line.length() == 0)
//					continue;
				line = Util.removeMultipleSpaces(line);
				line = line.replaceAll("\"", "");
				line = line.replaceAll(",", "");
				// Tach cau
				System.out.println("--------Tách câu, tách từ");
				 String splitSentences[] = sd.senSegment(line).split("\n");
				// Cach 2
//				String splitSentences[] = jVnTextPro.senSegment(line).split(
//						"\n");

				// String splitSentences[] = line.split("\\.");

				int numSentences = splitSentences.length;
				// System.out.println(numSentences);
				// Create graph nodes and find term frequencies.
				for (String entry : splitSentences) {
					freqs = new ConcurrentHashMap<String, TCandISC>();
					// System.out.println(entry);
					// allSent.add(entry);

					// Tach tu
					// String wrds[] = entry.split(" ");
					// String wrds[] = ws.segmenting(entry).split(" ");
					// Cach 2
					ArrayList<String> wordSentence = new ArrayList<String>();
//					String wrds[] = jVnTextPro.wordSegment(entry).split(" ");
					String wrds[] = ws.segmenting(entry).split(" ");
					for (int i = 0; i < wrds.length; i++) {
						wordSentence.add(wrds[i]);
					}
					// Loại từ dừng
					for (int i = 0; i < wordSentence.size(); i++) {
						// System.out.print(wrds[i]+" ");
						if (wordSentence.get(i).length() > 0) {
							char c = Summarizer.removeAccent(wordSentence
									.get(i).charAt(0));
							if (stopWords.containsKey(c)) { // neu stopwords
															// chua
															// ki tu dau tien
															// cua tu do
								if (stopWords.get(c).contains(
										wordSentence.get(i)))
									// neu stopword chua tu do
									// wrds[i] = "st";
									wordSentence.remove(wordSentence.get(i));

							}
						}
					}

					// System.out.print("Da loai ");
					// Tinh tan suat cua tu trong tap cac tu da tach cua 1 cau
					for (int k = 0; k < wordSentence.size(); k++) {
						// System.out.print(wrds[k]+ " ");
						String word = wordSentence.get(k);
						// System.out.print(word+" ");
						TCandISC in = null;
						if (freqs.containsKey(word)) {
							in = freqs.get(word);
							freqs.get(word).setTc(in.getTc() + 1);
						} else {
							in = new TCandISC();
							in.tc = 1;
							freqs.put(word, in);
						}

					}
					
					for (int i = 0; i < wordSentence.size(); i++) {
						for(int j = i+1; j<wordSentence.size(); j++){
							if(wordSentence.get(i).contains(wordSentence.get(j)))
								wordSentence.remove(wordSentence.get(j));
						}
					}
					
					// Add vao do thi
					GraphNode gNode = new GraphNode(sentenceCounter++, entry,
							this.freqs, numSentences, wordSentence);

					sentences.add(gNode);
				}

				// ///Find inverse sentence frequencies/////
				// Tinh tan so nghich dao cua tu trong ca van ban
				Iterator<String> it = freqs.keySet().iterator();
				while (it.hasNext()) {
					String word = it.next();
					int counter = 0;
					for (GraphNode node : sentences) {
						if (node.words.containsKey(word)) { // If the sentence
															// contains the
															// word
							counter++;
							// System.out.println(counter);
						}
					}
					// TCandISC in = null;
					if (freqs.containsKey(word)) {
						// in = freqs.get(word);
						freqs.get(word).setISC(counter);
					}
				}

			}
//			br.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	// ////////////Xay dung do thi /////////////////
	public void buildGraph() {
		for (int i = 0; i < sentences.size(); i++) {
			GraphNode node = sentences.get(i);
			// System.out.println(i +"_1 "+ node.sentenceLength);
			node.preprocessNode(); // Tinh TF va ISF cua tung tu
		}
		for (int i = 0; i < sentences.size(); i++) {// Tinh do tuong dong
													// Luu vao do thi
			GraphNode src = sentences.get(i);
			String srcWrds[] = src.wordSentence.toArray(new String[0]);
			// Mang chua tu cua cau i

			for (int j = i + 1; j < sentences.size(); j++) {
				GraphNode des = sentences.get(j);
				int leastIndex = (src.sentenceLength < des.sentenceLength) ? src.sentenceLength
						: des.sentenceLength; // Do dai cau be hon
				int maxIndex = (src.sentenceLength < des.sentenceLength) ? des.sentenceLength
						: src.sentenceLength; // Do dai cau lon hon
				// --------

				String desWrds[] = des.wordSentence.toArray(new String[0]);
				// Mang chua tu cua cau j

				// ---------------------sai
				Double srcWgts[] = new Double[maxIndex];// Mang chua tan suat
														// cua tung tu trong cau
														// i
				Double desWgts[] = new Double[maxIndex];// Mang chua tan suat
														// cua tung tu trong cau
														// j
				Double tmpSrcWgts[] = new Double[maxIndex];// Mang chua tan suat
															// tu giong nhau cua
															// cau src
				Double tmpDesWgts[] = new Double[maxIndex];// Mang chua tan suat
															// tu giong nhau cua
															// cau des
//				for(int g = 0; g<src.sentenceLength; g++){
//					System.out.println(srcWrds[g]);
//					
//				}
//				for(int g = 0; g<des.sentenceLength; g++){
//					System.out.println(desWrds[g]);
//					
//				}
				for (int k = 0; k < leastIndex; k++) {
					TFandISF srcTfIsf = src.words.get(srcWrds[k]);
					TFandISF desTfIsf = des.words.get(desWrds[k]);
					srcWgts[k] = srcTfIsf.tf * srcTfIsf.isf;
					// Trong so cua tu thu k voi cau i
					desWgts[k] = desTfIsf.tf * desTfIsf.isf;
					// Trong so cua tu thu k voi cau j
					tmpSrcWgts[k] = tmpDesWgts[k] = 0.0;
				}

				if (src.sentenceLength < des.sentenceLength) { // Cau dai hon
																// thi phai tinh
																// phan dai hon
					for (int k = leastIndex; k < maxIndex; k++) {
						TFandISF desTfIsf = des.words.get(desWrds[k]);
						desWgts[k] = desTfIsf.tf * desTfIsf.isf;
						srcWgts[k] = 0.0;
						tmpSrcWgts[k] = 0.0;
						tmpDesWgts[k] = 0.0;
					}
				}
				if (src.sentenceLength > des.sentenceLength) {// Cau dai hon thi
																// phai tinh
																// phan dai hon
					for (int k = leastIndex; k < maxIndex; k++) {
						TFandISF srcTfIsf = src.words.get(srcWrds[k]);
						srcWgts[k] = srcTfIsf.tf * srcTfIsf.isf;
						desWgts[k] = 0.0;
						tmpDesWgts[k] = 0.0;
						tmpSrcWgts[k] = 0.0;
					}
				}

				// Tinh tu so
				double Numerator = 0.0;
				for (int k = 0; k < srcWrds.length; k++) {
					TFandISF srcTfIsf = src.words.get(srcWrds[k]);
					for (int q = 0; q < desWrds.length; q++) {
						TFandISF desTfIsf = des.words.get(desWrds[q]);
						if (srcWrds[k].equalsIgnoreCase(desWrds[q])) {
							srcWgts[k] = (Double) srcWgts[k] - srcTfIsf.tf
									* Math.log(2);
							desWgts[q] = (Double) desWgts[q] - desTfIsf.tf
									* Math.log(2);
							tmpSrcWgts[k] = srcWgts[k];
							tmpDesWgts[q] = desWgts[k];
							Numerator += (double) (srcWgts[k] * desWgts[q]);
//							System.out.println(Numerator+"Nummmmmmm");
						}
					}
				}
//				System.out.println(Numerator+"Nummmmmmm");
				// Tinh mau so

				double Denominator = 0.0;
				if (src.sentenceLength <= des.sentenceLength) {
					Denominator += Util.getRootOfSumOfSquares(srcWgts);
					Denominator *= Util.getRootOfSumOfSquares(tmpDesWgts);
				} else {
					Denominator += Util.getRootOfSumOfSquares(desWgts);
					Denominator *= Util.getRootOfSumOfSquares(tmpSrcWgts);
				}
//				System.out.println(Denominator+"Dessssssssssssss");

				// Tinh do tuong dong giua 2 cau//
				// double simil = (double) Util.getDotProduct(srcWgts, desWgts)
				// / (Util.getRootOfSumOfSquares(srcWgts) * Util
				// .getRootOfSumOfSquares(desWgts));
				double simil;
				if(Numerator == 0 || Denominator == 0) simil = 0.0;
				else simil = Numerator / Denominator;
//				System.out.println(simil);
				similarityGraph.put(src.index + "-" + des.index, simil);

//				if (simil >= 0.0)
//					Util.INFO(src.index + "-" + des.index + "  " + simil);
			}
		}
		for (int i = 1; i <= sentences.size(); i++) {
			similarityGraph.put(i + "" + i, 0.0);// Do tuong dong cau voi chinh
													// no bang 0
		}
	}

	// ////Chay thuat toan page rank.
	public void invokePageRanking() {
		this.pageRank.numNodesInGraph = sentences.size();
		this.pageRank.setSentence(sentences);
		this.pageRank.runPageRankAlgorithm();
	}

	// /////In ra man hinh ban tom tat
	public String displaySummary() {
		System.out.println("--------Tóm tắt!!!");
		Iterator<Integer> iter = pageRank.sortedSentences.iterator();
		StringBuilder builder= new StringBuilder();
		while (iter.hasNext()) {
			int index = iter.next();
			GraphNode node = sentences.get(index - 1);
			Util.INFO(node.sentence);
			builder.append(node.sentence);
		}
		return builder.toString();
	}

	// //////Cho ban tom tat don van ban vao mot mang de xu ly tiep
	public String getSummary() {
		Iterator<Integer> iter = pageRank.sortedSentences.iterator();
		String temp = "";
		while (iter.hasNext()) {
			int index = iter.next();
			GraphNode node = sentences.get(index - 1);
			temp += node.sentence.replaceAll("\\s\\.", ".\\ ");
		}
		return temp;
	}
}