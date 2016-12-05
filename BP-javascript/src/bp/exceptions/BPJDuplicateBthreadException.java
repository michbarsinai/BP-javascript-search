package bp.exceptions;

import bp.bprogram.runtimeengine.BThreadSyncSnapshot;

@SuppressWarnings("serial")
public class BPJDuplicateBthreadException extends BPJException {

	BThreadSyncSnapshot existing;
	
	public BPJDuplicateBthreadException(BThreadSyncSnapshot existing) {
		this.existing = existing;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BPJDuplicateBthreadException [name=" + existing +"]";
	}
	
	

}
