package summary;

/**
 * Created by Z8 on 4/16/2017.
 */
public class ProcessStringToSummary {

    public static String summarizeString(String input){
        Summarizer.MAX_SENTENCES = 2;
        GooglePageRank.DAMPING_FACTOR = 0.85;
        GooglePageRank.MAX_ITERATIONS = 6;
        Summarizer summarizer = new Summarizer();
        summarizer.preProcessDocument(input);
        summarizer.buildGraph();
        summarizer.invokePageRanking();
        return summarizer.displaySummary();
    }
}
