public enum Shape {
    CIRCLE, SQUARE, TRIANGLE;

    public String toString() {
	String word = name();
	return word.charAt(0) + word.substring(1).toLowerCase();
    }
}
