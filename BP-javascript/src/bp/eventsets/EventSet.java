package bp.eventsets;


public interface EventSet {
	/**
	 * Implementation of the set membership function.
	 * 
	 * @param o  A candidate object to be tested for matching the criteria of  the set.
	 * @return true if the object matches the criteria of the set.
	 */
	public boolean contains(Object o);

}
