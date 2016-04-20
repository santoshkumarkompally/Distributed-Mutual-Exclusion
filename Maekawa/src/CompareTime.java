import java.util.Comparator;

public class CompareTime implements Comparator<ProcessInfo> {

	@Override
	public int compare(ProcessInfo o1, ProcessInfo o2) {
		if (o1.timestamp < o2.timestamp) {
			return -1;
		} else if (o1.timestamp > o2.timestamp) {
			return 1;
		}
		return o1.nodeid - o2.nodeid;

	}

}
