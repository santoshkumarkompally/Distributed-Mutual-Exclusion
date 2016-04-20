import java.io.Serializable;

public class ProcessInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int timestamp;
	int nodeid;
	int msgIdentifier;

	public ProcessInfo(int timestamp, int nodeid, int msgIdentifier) {

		this.timestamp = timestamp;
		this.nodeid = nodeid;
		this.msgIdentifier = msgIdentifier;
	}

	@Override
	public boolean equals(Object obj) {

		ProcessInfo p = (ProcessInfo) obj;

		if (nodeid == p.nodeid) {
			return true;
		} else {
			return false;
		}
	}
}
