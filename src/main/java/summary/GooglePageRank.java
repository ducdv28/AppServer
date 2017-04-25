package summary;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * PR(Vi) = (1-d) + d*(Sum Wji * PR(Vj) / (Sum Wkj)) where Vj = In(Vi) and Vk =
 * Out(Vj)
 */
public class GooglePageRank {
	public static Double DAMPING_FACTOR;// This is d
	public static Integer MAX_ITERATIONS;// If this is 0 then algorithm is run
	public ConcurrentMap<String, Double> similarityGraph;
	public ConcurrentMap<Integer, Double> ranks;
	public Map<Double, Integer> sortedRankSentences;// /Sorted based on rank
													// numbers
	public Set<Integer> sortedSentences;// /Sorted based on rank numbers
	public int numNodesInGraph;
	public LinkedList<GraphNode> sentences;

	public void setSentence(LinkedList<GraphNode> sentences) {

		this.sentences = sentences;
	}

	public GooglePageRank() {
		this.similarityGraph = new ConcurrentHashMap<String, Double>();// Ex:
																		// edgename=12
		this.ranks = new ConcurrentHashMap<Integer, Double>();
		this.sortedRankSentences = new TreeMap<Double, Integer>(
				Collections.reverseOrder());
		this.sortedSentences = new TreeSet<Integer>();
	}

	public GooglePageRank(ConcurrentMap<String, Double> similarityGraph) {
		this.similarityGraph = similarityGraph;// Ex: edgename=12
		this.ranks = new ConcurrentHashMap<Integer, Double>();
		this.sortedRankSentences = new TreeMap<Double, Integer>(
				Collections.reverseOrder());
		this.sortedSentences = new TreeSet<Integer>();
	}

	// /////////This simply is an implementation of the page rank learning
	// algorithm
	// /////////The graph of sentences is the input to this method//////////////
	public void runPageRankAlgorithm() {
		initializeRanks();
		if (MAX_ITERATIONS == 0) {// //////Run until convergence.
			boolean converged = false;
			while (!converged) {
			}
		} else {// /////////Dung sau khi den gioi han.
			System.out.println("--------Lặp đến khi hội tụ");
			// System.out.println("dddddddddddddddddd"+ numNodesInGraph);
			for (int cnt = 0; cnt < MAX_ITERATIONS; cnt++) {
				ConcurrentMap<Integer, Double> tmpranks = new ConcurrentHashMap<Integer, Double>();
				for (int i = 1; i <= numNodesInGraph; i++) {
					Double sum = 0.0;

					for (int j = 1; j <= numNodesInGraph; j++) {
						if (j == i)
							continue;
						Double Wji = (j < i) ? similarityGraph.get(j + "-" + i)
								: similarityGraph.get(i + "-" + j);
						if (Wji == 0)
							continue;
						Double pageRankVj = ranks.get(j);
						Double denSum = 0.0;

						for (int k = 1; k <= numNodesInGraph; k++) {
							if (k == j)
								continue;
							Double Wjk = (j < k) ? similarityGraph.get(j + "-"
									+ k) : similarityGraph.get(k + "-" + j);
//							System.out.println(Wjk);
							denSum += Wjk;
						}
						// if(denSum == 0) continue;
						sum += (Wji * pageRankVj / (double) denSum);
						
					}
					double rnk = (1 - DAMPING_FACTOR) + DAMPING_FACTOR * sum;
//					System.out.println(rnk);
					// ranks.remove(i);
					// ranks.put(i, rnk);
					tmpranks.put(i, rnk);
				}
				// diplayRanks(tmpranks);
				updateRanks(tmpranks);
				// diplayRanks(ranks);
			}

		}
		// Loại bỏ 1 câu trong trong cặp câu có độ tương đồng cao//

		// System.out.print(sentences.get(0).sentenceLength+"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		for (int i = 1; i <= numNodesInGraph; i++) {
			GraphNode src = sentences.get(i - 1);
			for (int j = i + 1; j <= numNodesInGraph; j++) {
				GraphNode des = sentences.get(j - 1);
				if (j == i)
					continue;
				else {
					Double Wji = (j < i) ? similarityGraph.get(j + "-" + i)
							: similarityGraph.get(i + "-" + j);
					// System.out.println("qqqqqqq "+Wji);
					if (Wji > 0.8) {
						if (src.sentenceLength < des.sentenceLength)
							ranks.remove(i);
						else
							ranks.remove(j);
					}
				}
			}
		}

		// //////////////
//		 System.out.println("pppapapapapapaapa  "+ranks.size());
		// int i = 1;i <= ranks.size();i++
		for (Integer i : ranks.keySet()) {
			sortedRankSentences.put(ranks.get(i), i);
		}

//		Util.DIPLAYMAP(sortedRankSentences);
		Iterator<Integer> iter = sortedRankSentences.values().iterator();
		int max = 0;
		while (iter.hasNext() && max++ < Summarizer.MAX_SENTENCES) {
			sortedSentences.add(iter.next());
		}
		System.out.println("--------Sắp xếp theo rank, và lấy "
				+ sortedSentences.size() + " câu");
//		 Util.DIPLAYMAP(sortedRankSentences);
	}

	// //////Khoi tao //////
	public void initializeRanks() {
		for (int i = 1; i <= numNodesInGraph; i++) {
			ranks.put(i, 1.0);
		}
	}

	// ////Update all the ranks with new ones /////////////
	public void updateRanks(ConcurrentMap<Integer, Double> newRanks) {
		ranks.clear();
		for (int i = 1; i <= numNodesInGraph; i++) {
			ranks.put(i, newRanks.get(i));
		}
	}

	// ///////In ra man hinh rank moi cau sau moi lan lap/////////////
	public void diplayRanks(ConcurrentMap<Integer, Double> newRanks) {
		for (int i = 1; i <= numNodesInGraph; i++) {
			Util.INFO(i + " " + newRanks.get(i));
		}
	}
}