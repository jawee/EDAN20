package store;

import format.Word;

public class TripleStore {
	public Word w1,w2,w3;

	public TripleStore(Word w1, Word w2, Word w3) {
		this.w1 = w1;
		this.w2 = w2;
		this.w3 = w3;
	}

	@Override
	public int hashCode() {
		return (w1.getForm().toLowerCase() + w2.getForm().toLowerCase() + w3.getForm().toLowerCase()).hashCode();
	}
	
	@Override
    public boolean equals(Object ssp) {
        String signature1 = w1.getForm().toLowerCase() + w2.getForm().toLowerCase() + w3.getForm().toLowerCase();
        String signature2 = ((TripleStore) ssp).w1.getForm().toLowerCase() + ((TripleStore) ssp).w2.getForm().toLowerCase() + ((TripleStore) ssp).w3.getForm().toLowerCase();
        return signature1.equals(signature2);
    }
	
	public String toString() {
		return w1.getForm().toString() + " " + w2.getForm().toString() + " " + w3.getForm().toString();
	}
	
}
