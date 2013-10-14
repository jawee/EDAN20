package store;


import format.Word;

public class SSPair {
	public Word w1, w2;
	
	public SSPair(Word w1, Word w2) {
		this.w1 = w1;
		this.w2 = w2;
	}

	@Override
	public int hashCode() {
		return (w1.getForm().toLowerCase() + w2.getForm().toLowerCase()).hashCode();
	}
	
	@Override
    public boolean equals(Object ssp) {
        String signature1 = w1.getForm().toLowerCase() + w2.getForm().toLowerCase();
        String signature2 = ((SSPair) ssp).w1.getForm().toLowerCase() + ((SSPair) ssp).w2.getForm().toLowerCase();
        return signature1.equals(signature2);
    }
	
	public String toString() {
		return w1.getForm().toString() + " " + w2.getForm().toString();
	}


}
