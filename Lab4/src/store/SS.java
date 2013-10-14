package store;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import format.CONLLCorpus;
import format.Constants;
import format.Word;

public class SS {

	List<List<Word>> sentenceList;
	public SS(List<List<Word>> sentenceList) {
		// TODO Auto-generated constructor stub
		this.sentenceList = sentenceList;
	}
	
	public void countSS() {
		HashMap<SSPair, Integer> frequencyMap = new HashMap<SSPair, Integer>();
		for(List<Word> list : sentenceList) {
			for(Word w1 : list) {
				if(w1.getDeprel().equals("SS")) {
					Word w2 = list.get(w1.getHead());
					SSPair p = new SSPair(w1, w2);
					Integer freq = frequencyMap.get(p);
					if(freq == null) {
						freq = 0;
					} 
					frequencyMap.put(p, ++freq);
				}
			}
		}
			
        ValueComparator bvc =  new ValueComparator(frequencyMap);
        TreeMap<SSPair,Integer> sorted_map = new TreeMap<SSPair,Integer>(bvc);
        sorted_map.putAll(frequencyMap);
        int totalPairs = 0;
		int count = 0;
		for(SSPair p : sorted_map.keySet()) {
			count++;
			int val = frequencyMap.get(p);
			totalPairs += val;
			if(count < 5) {
				System.out.println(p + ": " + val);
			}
		}
		System.out.println("Total amount of pairs: " + totalPairs);
	}
	
	private class ValueComparator implements Comparator<SSPair> {

	    Map<SSPair, Integer> base;
	    public ValueComparator(Map<SSPair, Integer> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(SSPair a, SSPair b) {
	        if (base.get(a) > base.get(b)) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	
	public static void main(String[] args) throws IOException {
		File trainingSet = new File(Constants.TRAINING_SET);
        CONLLCorpus trainingCorpus = new CONLLCorpus();
        List<List<Word>> sentenceList;
        sentenceList = trainingCorpus.loadFile(trainingSet);
        SS s = new SS(sentenceList);
        s.countSS();
	}

}
