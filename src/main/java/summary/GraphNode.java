package summary;

import java.util.*;
import java.util.concurrent.*;

/**
 * This is a node in the graph. This is closely tailored to represent a sentence
 * as a node with all its properties and methods to twiddle with its behaviour.
 */
public class GraphNode {
	public int index;
	public Double pageRank;// //This is importance given to the sentence.
	public String sentence;
	public ConcurrentMap<String, TFandISF> words;
	public Integer maxF;// /Maximum term count.
	public ConcurrentMap<String, TCandISC> freqs;// //This is from Summarizer.
	public int numSentences;// This is N
	public int sentenceLength;
	// public String wrds[];
	ArrayList<String> wordSentence = new ArrayList<String>();

	public GraphNode() {
		words = new ConcurrentHashMap<String, TFandISF>();
		maxF = 0;
	}

	public GraphNode(int index) {
		this.index = index;
		maxF = 0;
	}

	public GraphNode(int index, String sentence) {
		this.index = index;
		this.sentence = sentence;
		words = new ConcurrentHashMap<String, TFandISF>();
		getWordsFromSentence(sentence);
		maxF = 0;
	}

	public GraphNode(int index, String sentence,
			ConcurrentMap<String, TCandISC> freqs) {
		this.index = index;
		this.sentence = sentence;
		words = new ConcurrentHashMap<String, TFandISF>();
		getWordsFromSentence(sentence);
		this.freqs = freqs;
		maxF = 0;
	}

	// Truyen mang chu tu cua tung cau sang
	public GraphNode(int index, String sentence,
			ConcurrentMap<String, TCandISC> freqs, int numSentences,
			ArrayList<String> wordSentence) {
		this.index = index;
		this.sentence = sentence;
		words = new ConcurrentHashMap<String, TFandISF>();
		// this.wrds = wrds;
		this.wordSentence = wordSentence;
		getWordsFromSentence(sentence);
		this.freqs = freqs;
		maxF = 0;
		this.numSentences = numSentences;

	}

	// //////Tính TF và ISF//////
	public void preprocessNode() {
		this.sentenceLength = wordSentence.size();
		Iterator<String> it = this.words.keySet().iterator();
		while (it.hasNext()) {// ///Find maximum frequency, tần số lớn nhất của
								// từ i trong câu j
			String wrd = it.next();
			TCandISC tcIsc = freqs.get(wrd);
			if (tcIsc.tc > maxF)
				maxF = new Integer(tcIsc.tc);
			else
				;
		}
		it = this.words.keySet().iterator();

		while (it.hasNext()) {// /////TF and ISF.
			String wrd = it.next();
			// Double tf = ((double)freqs.get(wrd).tc /maxF);
//			 System.out.println(freqs.get(wrd).tc+" "+wrd);
			Double tf = ((double) (freqs.get(wrd).tc) / this.sentenceLength);
			// Double isf = Math.log10(((double) numSentences/
			// freqs.get(wrd).isc));
			Double isf = 1 + Math.log((double) 2);
			// Double isf = ((double)numSentences/freqs.get(wrd).isc);
			// words.put(wrd, tfIsf);
			words.get(wrd).setTF(tf);
			words.get(wrd).setISF(isf);
			// Util.INFO("Node" + index + " " + tfIsf);
		}
	}

	// public void displayNode() {
	// Util.INFO(index + "");
	// for (int i = 0;i < words.size();i++) {
	//
	// }
	// }

	public void getWordsFromSentence(String sent) {
		// String wrds[] = sent.split(" ");
		this.sentenceLength = wordSentence.size();
		for (int i = 0; i < this.sentenceLength; i++) {// /Only unique words go
														// into this.
			this.words.put(wordSentence.get(i), new TFandISF());
		}
	}

	@Override
	public String toString() {
		return index + "";
	}

}
