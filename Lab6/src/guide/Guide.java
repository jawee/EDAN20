package guide;

import format.Word;
import wekaglue.WekaGlue;
import parser.ParserState;

/**
 *
 * @author Pierre Nugues
 */
public abstract class Guide {

    WekaGlue wekaModel;
    ParserState parserState;

    Guide(WekaGlue wekaModel, ParserState parserState) {
        this.wekaModel = wekaModel;
        this.parserState = parserState;
    }

    public abstract String predict();

    Features extractFeatures() {
    	Features feats;
		String topPostagStack = "nil";
		String secondPostagStack = "nil";
		String firstPostagQueue = "nil";
		String secondPostagQueue = "nil";
		String posFollowing = "nil";
		String posBefore = "nil";
		// COMPLETE HERE THE CODE TO EXTRACT FEATURES
		if (parserState.stack.size() > 1) {
			topPostagStack = parserState.stack.peek().getPostag();
			secondPostagStack = parserState.stack.get(parserState.stack.size()-2).getPostag();
		} else if (!parserState.stack.isEmpty()) {
			topPostagStack = parserState.stack.peek().getPostag();
		} 
		
		Word nextWord = null;
		Word wordBefore = null;
		if(!parserState.stack.isEmpty()) {
			int i = parserState.stack.peek().getId()+1;
			int j = parserState.stack.peek().getId()-1;
			for(Word w : parserState.wordList) {
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
		
		if (parserState.queue.size() > 1) {
			firstPostagQueue = parserState.queue.get(0).getPostag();
			secondPostagQueue = parserState.queue.get(1).getPostag();
		} else if (!parserState.queue.isEmpty()) {
			firstPostagQueue = parserState.queue.get(0).getPostag();
		}

//        feats = new Features(topPostagStack, queue.get(0).getPostag());
//        feats = new Features(topPostagStack, queue.get(0).getPostag(), canLeftArc(), canReduce());
//        feats = new Features(topPostagStack, secondPostagStack, parserState.queue.get(0).getPostag(), secondPostagQueue, parserState.canLeftArc(), parserState.canReduce());
        feats = new Features(topPostagStack, secondPostagStack, firstPostagQueue, secondPostagQueue, posFollowing, posBefore, parserState.canLeftArc(), parserState.canReduce());
        return feats;
    }
}
