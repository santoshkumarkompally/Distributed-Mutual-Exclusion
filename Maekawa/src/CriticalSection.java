import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CriticalSection extends Thread {

	ProcessInfo p;
	HashMap<Integer, NodeInfo> nodeInfo;
	ArrayList<Integer> quorumSet;
	int mynode, timestamp;
	Thread client;

	public CriticalSection(ProcessInfo p, ArrayList<Integer> quorumSet, HashMap<Integer, NodeInfo> nodeInfo, int mynode,
			int timestamp) {

		this.p = p;
		this.quorumSet = quorumSet;
		this.nodeInfo = nodeInfo;
		this.mynode = mynode;
		this.timestamp = timestamp;

	}

	public void run() {

		csEnter();
		csExit();

	}

	void csEnter() {
		try {
			Server.inCriticalSection = true;

			System.out.println("EnteringCS" + " " + mynode);

			// to generate exponential backoff time
			Double num;
			Double lambda = (double) Maekawa.csExec;
			Random rand = new Random();
			num = Math.log(1 - rand.nextDouble()) * (-lambda);

			// sleep this thread
			// CriticalSection.sleep(Maekawa.csExec * 10);
			CriticalSection.sleep(Maekawa.csExec);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void csExit() {

		System.out.println("ExitingCS" + " " + mynode);
		// send a release message to all the Quorum members
		for (Integer temp1 : quorumSet) {

			clientCall(temp1, nodeInfo.get(temp1).hostname, nodeInfo.get(temp1).portnumber,
					new ProcessInfo(timestamp, mynode, 2));
		}
		Server.inCriticalSection = false;
		Server.grantMsg = 0;
		Server.failedMessageRecieved = false;

		try {
			// to generate exponential backoff time
			Double num;
			Double lambda = (double) Maekawa.requestDelay;
			Random rand = new Random();
			num = Math.log(1 - rand.nextDouble()) * (-lambda);

			// CriticalSection.sleep(Maekawa.requestDelay * 10);
			CriticalSection.sleep(Maekawa.requestDelay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Maekawa.canEnterCS = true;
		Maekawa.requestedCriticalSection = false;

	}

	void clientCall(int node, String host, int port, ProcessInfo p) {
		client = new Thread(new Client(node, host, port, p));
		client.start();

	}

}
