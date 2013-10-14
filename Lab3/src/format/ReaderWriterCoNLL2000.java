package format;

import java.io.*;
import java.util.*;
import mlchunker.MLChunker;

/**
 *
 * @author pierre
 */
// The class to load a CoNLL 2000 corpus.
public class ReaderWriterCoNLL2000 {

    private List<List<WordCoNLL2000>> sentenceList;

    public List<List<WordCoNLL2000>> loadFile(File file) throws IOException {
        sentenceList = new ArrayList<List<WordCoNLL2000>>();
        List<WordCoNLL2000> sentence = new ArrayList<WordCoNLL2000>();

        WordCoNLL2000 curWord;

        int sentenceCount = 0;
        int lineCount = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            lineCount++;
            curWord = makeWord(line);
            if (curWord != null) { // A word
                sentence.add(curWord);
            } else { // An empty line denoting a new sentence
                sentenceList.add(sentence);
                sentence = new ArrayList<WordCoNLL2000>();
                sentenceCount++;
            }
            if ((lineCount % 50000) == 0) {
                System.out.println("Read: " + lineCount + " lines, " + sentenceCount + " sentences.");
            }
        }
        reader.close();
        System.out.println("Read: " + lineCount + " lines, " + sentenceCount + " sentences.");
        return sentenceList;
    }

    // Splits the input line of the CoNLL 2000 file into three fields: word, part of speech, and chunk tag.
    private WordCoNLL2000 makeWord(String line) {
        String[] wordD = line.split(" ");
        WordCoNLL2000 word = null;
        // Both the training and test sets have the three fields.
        if (wordD.length == 3) {
            word = new WordCoNLL2000(wordD[0], wordD[1], wordD[2]);
        }
        return word;
    }

    public void printFile(List<List<WordCoNLL2000>> sentenceList) {
        for (List<WordCoNLL2000> sentence : sentenceList) {
            for (WordCoNLL2000 word : sentence) {
                System.out.print(word.getForm() + " ");
            }
            System.out.println("");
        }
    }

    public void saveFile(File file, List<List<WordCoNLL2000>> sentenceList) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

        for (int i = 0; i < sentenceList.size(); i++) {
            for (int j = 0; j < (sentenceList.get(i)).size(); j++) {
                writer.write((sentenceList.get(i)).get(j).getForm() + " ");
                writer.write((sentenceList.get(i)).get(j).getPpos() + " ");
                writer.write((sentenceList.get(i)).get(j).getChunk() + "\n");
            }
            if (i != sentenceList.size()) {
                writer.write("\n");
            }
        }
        writer.close();
    }

    // This method saves the training data in the ARFF format. It adds the header.
    public void saveARFF(File file, List<List<WordCoNLL2000>> sentenceList) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
        writer.write(Constants.ARFF_HEADER);
        for (List<WordCoNLL2000> sentence : sentenceList) {
        	sentence.add(0, (new WordCoNLL2000("BOS", "BOS")).setChunk("BOS"));
        	sentence.add((new WordCoNLL2000("EOS", "EOS")).setChunk("EOS"));
        	Iterator<WordCoNLL2000> itr = sentence.iterator();
        	WordCoNLL2000 first = itr.next();
        	WordCoNLL2000 word = itr.next();
        	while(itr.hasNext()) {
        		WordCoNLL2000 end = itr.next();
        		if (first.getPpos().equals(",")) {
        			writer.write("\",\", ");
        		} else if (first.getPpos().equals("''")) {
        			writer.write("\"''\", ");
        		} else {
        			writer.write(first.getPpos() + ", ");
        		}
        		
        		if (word.getPpos().equals(",")) {
        			writer.write("\",\", ");
        		} else if (word.getPpos().equals("''")) {
        			writer.write("\"''\", ");
        		} else {
        			writer.write(word.getPpos() + ", ");
        		}
        		
        		if (end.getPpos().equals(",")) {
        			writer.write("\",\", ");
        		} else if (end.getPpos().equals("''")) {
        			writer.write("\"''\", ");
        		} else {
        			writer.write(end.getPpos() + ", ");
        		}
        		writer.write(word.getChunk() + "\n");
        		first = word;
        		word = end;
        	}        		
        }

        writer.close();
    }

    List<WordCoNLL2000> getSentence(int index) {
        return sentenceList.get(index);
    }

    List<List<WordCoNLL2000>> getSentenceList() {
        return sentenceList;
    }

    int getSize() {
        return sentenceList.size();
    }
}
