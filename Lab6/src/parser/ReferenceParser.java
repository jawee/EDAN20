package parser;

/**
 *
 * @author Pierre Nugues
 */
import format.ARFFData;
import format.CONLLCorpus;
import format.Constants;
import format.Word;
import guide.Features;
import java.util.*;
import java.io.File;
import java.io.IOException;

public class ReferenceParser {

	Stack<Word> stack;
	List<Word> queue;
	List<Word> wordList;
	List<String> transitionList;
	List<Features> featureList;
	List<Word> depGraph;

	ReferenceParser(List<Word> wordList) {
		stack = new Stack<Word>();
		queue = new ArrayList<Word>(wordList);
		this.wordList = wordList;
		depGraph = new ArrayList<Word>();
	}

	private void doLeftArc() {
		depGraph.add(stack.pop());
	}

	private void doRightArc() {
		depGraph.add(queue.get(0));
		stack.push(queue.remove(0));
	}

	private void doReduce() {
		stack.pop();
	}

	private void doShift() {
		stack.push(queue.remove(0));
	}

	private boolean oracleLeftArc() {
		boolean oracleLeftArc = false;
		if (!stack.empty()) {
			if (stack.peek().getHead() == queue.get(0).getId()) {
				// System.out.println(queue.get(0).getForm() + "  --> " +
				// stack.peek().getForm());
				oracleLeftArc = true;
			}
		}
		// Constraint: top of the stack has no head in the graph
		// This means that it is not already in the graph.
		// In reference parsing, this should always be satisfied
		// We double-check it
		if (oracleLeftArc) {
			oracleLeftArc = canLeftArc();
		}
		return oracleLeftArc;
	}

	private boolean canLeftArc() {
		boolean canLeftArc = true;
		if (stack.empty()) {
			return false;
		}

		// Constraint: top of the stack has no head in the graph
		// This means that it is not already in the graph.
		// In reference parsing, this should always be satisfied
		for (int i = 0; i < depGraph.size(); i++) {
			if (depGraph.get(i).getId() == stack.peek().getId()) {
				canLeftArc = false;
				break;
			}
		}
		return canLeftArc;
	}

	private boolean oracleRightArc() {
		boolean oracleRightArc = false;
		if (!stack.empty()) {
			if (stack.peek().getId() == queue.get(0).getHead()) {
				// System.out.println(stack.peek().getForm() + "  --> " +
				// queue.get(0).getForm());
				oracleRightArc = true;
			}
		}
		return oracleRightArc;
	}

	private boolean oracleReduce() {
		boolean oracleReduce = false;

		for (int i = 0; i < stack.size(); i++) {
			if (queue.get(0).getHead() == stack.get(i).getId()) {
				oracleReduce = true;
				break;
			}
			if (stack.get(i).getHead() == queue.get(0).getId()) {
				oracleReduce = true;
				break;
			}
		}

		// Constraint: top of the stack has a head somewhere is the graph
		// This garantees that the graph is connected
		// Here this means that it is already in the graph
		// In reference parsing, this should always be satisfied for projective
		// graphs
		if (oracleReduce) {
			oracleReduce = canReduce();
		}
		return oracleReduce;
	}

	private boolean canReduce() {
		boolean canReduce = false;
		if (stack.empty()) {
			return false;
		}

		// Constraint: top of the stack has a head somewhere is the graph
		// This guarantees that the graph is connected
		// Here this means that it is already in the graph
		// In reference parsing, this should always be satisfied for projective
		// graphs
		for (int i = 0; i < depGraph.size(); i++) {
			if (depGraph.get(i).getId() == stack.peek().getId()) {
				canReduce = true;
				break;
			}
		}
		return canReduce;
	}

	private Features extractFeatures() {
		Features feats;
		String topPostagStack = "nil";
		String secondPostagStack = "nil";
		String firstPostagQueue = "nil";
		String secondPostagQueue = "nil";
		String posFollowing = "nil";
		String posBefore = "nil";
		// COMPLETE HERE THE CODE TO EXTRACT FEATURES
		if (stack.size() > 1) {
			topPostagStack = stack.peek().getPostag();
			secondPostagStack = stack.get(stack.size()-2).getPostag();
		} else if (!stack.isEmpty()) {
			topPostagStack = stack.peek().getPostag();
		} 
		
		Word nextWord = null;
		Word wordBefore = null;
		if(!stack.isEmpty()) {
			int i = stack.peek().getId()+1;
			int j = stack.peek().getId()-1;
			for(Word w : wordList) {
				if(w.getId() == i) {
					nextWord = w;
				}
				if(w.getId() == j) {
					wordBefore = w;
				}
			}
		}
		
		
		if(nextWord != null) {
			posFollowing = nextWord.getPostag();
		}
		if(wordBefore != null) {
			posBefore = wordBefore.getPostag();
		}
		
		if (queue.size() > 1) {
			firstPostagQueue = queue.get(0).getPostag();
			secondPostagQueue = queue.get(1).getPostag();
		} else if (!queue.isEmpty()) {
			firstPostagQueue = queue.get(0).getPostag();
		}
		
//		System.out.println(topPostagStack + " " + secondPostagStack + " " + firstPostagQueue + " " + secondPostagQueue + " " + canLeftArc() + " " + canReduce());
		
//		feats = new Features(topPostagStack, secondPostagStack, firstPostagQueue, secondPostagQueue, canLeftArc(), canReduce());
		feats = new Features(topPostagStack, secondPostagStack, firstPostagQueue, secondPostagQueue, posFollowing, posBefore, canLeftArc(), canReduce());
		return feats;
	}

	// A sanity check about the action sequence.
	private boolean equalGraphs() {
		boolean equals = false;
		List<Word> temp = new ArrayList<Word>(wordList);

		wordList.remove(0); // we remove the root word

		for (int i = 0; i < wordList.size(); i++) {
			equals = false;
			for (int j = 0; j < depGraph.size(); j++) {
				if (wordList.get(i).getId() == depGraph.get(j).getId()) {
					if (wordList.get(i).getHead() == depGraph.get(j).getHead()) {
						equals = true;
					}
					break;
				}
			}
			if (equals == false) {
				break;
			}
		}
		wordList = temp;
		return equals;
	}

	public List<String> getActionList() {
		return transitionList;
	}

	public List<Features> getFeatureList() {
		return featureList;
	}

	public List<Word> getQueue() {
		return queue;
	}

	public Stack<Word> getStack() {
		return stack;
	}

	public int parse() {
		transitionList = new ArrayList<String>();
		featureList = new ArrayList<Features>();
		while (!queue.isEmpty()) {
//			System.out.println(queue.get(0));
			featureList.add(extractFeatures());
			// COMPLETE HERE THE CODE TO DETERMINE THE ACTION

			if (oracleRightArc()) {
				doRightArc();
//				transitionList.add("ra."+ depGraph.get(depGraph.size()-1).getDeprel());
				transitionList.add("ra");
			} else if (oracleLeftArc()) {
				doLeftArc();
//				transitionList.add("la."+depGraph.get(depGraph.size()-1).getDeprel());
				transitionList.add("la");
			} else if (oracleReduce()) {
				doReduce();
				transitionList.add("re");
			} else {
				doShift();
				transitionList.add("sh");
			}

		}
		emptyStack(transitionList, featureList);

		boolean printCntOper = false;
		if (printCntOper) {
//			System.out.println("#words: " + wordList.size() + " \tStack: " + stack.size() + "\tTransition count: " + transitionList.size());
		}

		// Final test to check if the hand-annotated graph and reference-parsed
		// graph are equal.
		// If they are different, this is probably due to nonprojective links
		if (equalGraphs() == true) {
			// Correct action sequence:
			// Hand-annotated graph and reference-parsed graph are equal.
			return 0;
		} else {
			// Could not find the correct sequence!!!:
			// The graphs are not equal.
			return -1;
		}
	}

	public void printActions() {
		for (int i = 0; i < wordList.size(); i++) {
			System.out.print(wordList.get(i).getForm() + " ");
		}
		System.out.println();
		for (int i = 0; i < transitionList.size(); i++) {
			System.out.print(transitionList.get(i) + " ");
		}
		System.out.println();
	}

	// emptyStack should only leave the root in the stack if the graph is
	// projective and well-formed
	public int emptyStack(List<String> transitionList,
			List<Features> featureList) {
		while (stack.size() > 1) {
			featureList.add(extractFeatures());
			if (canReduce()) {
				doReduce();
				transitionList.add("re");
			} else {
				return -1;
			}
		}
		return 0;
	}

	public static void main(String[] args) throws IOException {
		File trainingSet = new File(Constants.TRAINING_SET);
		File arff = new File(Constants.ARFF_FILE);
		CONLLCorpus trainingCorpus = new CONLLCorpus();
		ARFFData arffData = new ARFFData();

		List<List<Word>> sentenceList;

		ReferenceParser refParser = null;

		List<String> transitionList = new ArrayList<String>();
		List<Features> featureList = new ArrayList<Features>();

		if (trainingSet.exists()) {
			System.out.println("Loading file...");
		} else {
			System.out.println("File does not exist, exiting...");
			return;
		}

		sentenceList = trainingCorpus.loadFile(trainingSet);

		System.out.println("Parsing the sentences...");
		for (int i = 0; i < sentenceList.size(); i++) {
			int parseSuccess;
			refParser = new ReferenceParser(sentenceList.get(i));
			// The parser returns 0 or -1
			// indicating if the parse is successful or not
			// Failed parses should be discarded.

			parseSuccess = refParser.parse();
			refParser.printActions();
			if (parseSuccess != -1) {
				featureList.addAll(refParser.getFeatureList());
				transitionList.addAll(refParser.getActionList());
			}
		}
		arffData.saveFeatures(arff, featureList, transitionList);
	}
}
