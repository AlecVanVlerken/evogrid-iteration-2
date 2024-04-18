package util;

public class Pair<T, U> {
	
	/**
	 * @invar | first != null
	 * @invar | second != null
	 */
	private T first;
	private U second;
	
	/**
     * @post | result != null
     */
	public T getFirst() {
		return first;
	}
	
	/**
     * @post | result != null
     */
	public U getSecond() {
		return second;
	}
	
	/**
     * Initializes a new Pair object.
     *
     * @throws IllegalArgumentException | first == null || second == null
     * @post | this.getFirst() == first
     * @post | this.getSecond() == second
     */
	public Pair(T first, U second) {
		if (first == null || second == null) {throw new IllegalArgumentException(); }
		this.first = first;
		this.second = second;
	}
	
	/**
	 * @mutates | this
     * @pre | first != null
     * @post | this.getFirst() == first
     */
	public void setFirst(T first) {
		this.first = first;
	}
	
	/**
	 * @mutates | this
     * @pre | second != null
     * @post | this.getSecond() == second
     */
	public void setSecond(U second) {
		this.second = second;
	}
}
