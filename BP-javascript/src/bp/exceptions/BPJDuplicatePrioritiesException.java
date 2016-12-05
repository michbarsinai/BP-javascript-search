package bp.exceptions;

import bp.bprogram.runtimeengine.BThreadSyncSnapshot;

@SuppressWarnings("serial")
public class BPJDuplicatePrioritiesException extends BPJException {

	BThreadSyncSnapshot existing;
	BThreadSyncSnapshot newBT;
	
	public BPJDuplicatePrioritiesException(BThreadSyncSnapshot existing, BThreadSyncSnapshot newBT) {
		this.existing = existing;
		this.newBT = newBT;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BPJDuplicatePrioritiesException [existing=" + existing + " new=" + newBT +"]";
	}
	
	

}
