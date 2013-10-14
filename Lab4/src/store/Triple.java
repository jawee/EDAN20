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

public class Triple {

	List<List<Word>> sentenceList;

	public Triple(List<List<Word>> sentenceList) {
		// TODO Auto-generated constructor stub
		this.sentenceList = sentenceList;
	}

	public void countSS() {
		HashMap<TripleStore, Integer> frequencyMap = new HashMap<TripleStore, Integer>();
		for (List<Word> list : sentenceList) {
			for (Word w1 : list) {
				if (w1.getDeprel().equals(Constants.SUBJECT)) {
					Word w2 = list.get(w1.getHead());
					for(Word w3 : list) {
						if(w3.getDeprel().equals(Constants.OBJECT)) {
							if(w3.getHead() == w2.getId()) {
								TripleStore p = new TripleStore(w1, w2, w3);
								Integer freq = frequencyMap.get(p);
								if (freq == null) {
									freq = 0;
								}
								frequencyMap.put(p, ++freq);
							}
						}
					}
				}
			}
		}
		
		int largest = 0;
		for(TripleStore p : frequencyMap.keySet()) {
			int val = frequencyMap.get(p);
			if(val > largest) {
				largest = val;
			}
		}
		
		System.out.println(largest);

		ValueComparator bvc = new ValueComparator(frequencyMap);
		TreeMap<TripleStore, Integer> sorted_map = new TreeMap<TripleStore, Integer>(bvc);
		sorted_map.putAll(frequencyMap);
		int totalPairs = 0;
		int count = 0;
		for (TripleStore p : sorted_map.keySet()) {
			count++;
			int val = frequencyMap.get(p);
			totalPairs += val;
			if (count < 5) {
				System.out.println(p + ": " + val);
			}
		}
		System.out.println("Total amount of pairs: " + totalPairs);
	}

	private class ValueComparator implements Comparator<TripleStore> {

		Map<TripleStore, Integer> base;

		public ValueComparator(Map<TripleStore, Integer> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(TripleStore a, TripleStore b) {
			if (base.get(a) > base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
	}

	public static void main(String[] args) throws IOException {
		File trainingSet = new File(Constants.TRAINING_SET);
		File trainingSetFr = new File(Constants.TRAINING_SET_FR);
		File trainingSetEs = new File(Constants.TRAINING_SET_ES);
		CONLLCorpus trainingCorpus = new CONLLCorpus();
		List<List<Word>> sentenceList;
		sentenceList = trainingCorpus.loadFile(trainingSetEs);
		Triple s = new Triple(sentenceList);
		s.countSS();
	}

}