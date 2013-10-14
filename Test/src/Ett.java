
public class Ett {
	public static void main(String[] args) {
		Tre t = new Tre("hello");
		Tva t2 = new Tva(t);
		System.out.println(t.name);
	}
}
