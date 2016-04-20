import java.util.ArrayList;
import java.util.HashMap;

public class Maekawa {
	Thread server;
	Thread client;
	static int requestDelay, csExec, numReq;
	static boolean canEnterCS, requestedCriticalSection;
	static HashMap<Integer, NodeInfo> nodeInfo = new HashMap<>();
	static ArrayList<Integer> quorumSet = new ArrayList<Integer>();
	static int requests;
	static int timestamp;
	static int nodeid;

	public static void main(String[] args) {
		Maekawa m = new Maekawa();
		nodeInfo = new HashMap<>();
		quorumSet = new ArrayList<Integer>();
		canEnterCS = true;
		requests = 0;
		timestamp = 0;
		int mynode = 0, node_count, port;
		String hostname;
		CompareTime c = new CompareTime();
		requestedCriticalSection = false;
		// Input the values from command line
		node_count = Integer.parseInt(args[0]);
		nodeid = Integer.parseInt(args[1]);
		requestDelay = Integer.parseInt(args[4]);
		csExec = Integer.parseInt(args[5]);
		numReq = Integer.parseInt(args[6]);
		String str = args[2];
		String[] val = new String[node_count];
		val = str.split("#");

		for (int i = 0; i < node_count; i++) {
			String[] det = new String[3];
			det = val[i].split(" ");
			mynode = Integer.parseInt(det[0]);
			/*
			 * InetAddress address; (try { address =
			 * InetAddress.getByName(det[1]); hostname =
			 * address.getHostAddress(); // System.out.println("hostname is : "
			 * + hostname); port = Integer.parseInt(det[2]);
			 * nodeInfo.put(mynode, new NodeInfo(mynode, hostname, port)); }
			 * catch (UnknownHostException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); }
			 */

			hostname = det[1];
			port = Integer.parseInt(det[2]);
			nodeInfo.put(mynode, new NodeInfo(mynode, hostname, port));

		}

		String[] q = args[3].split(" ");
		for (int i = 0; i < q.length; i++) {
			quorumSet.add(Integer.parseInt(q[i]));
		}

		m.serverCall(nodeid, nodeInfo.get(nodeid).portnumber, nodeInfo, quorumSet, c);

		m.csRequest(m);

	}

	// to call server and set to listening mode
	void serverCall(int mynode, int port, HashMap<Integer, NodeInfo> nodeInfo, ArrayList<Integer> quorumSet,
			CompareTime c) {
		server = new Thread(new Server(mynode, port, nodeInfo, quorumSet, c));
		server.start();
	}

	// Method to send requests for Critical Sections
	synchronized void csRequest(Maekawa m) {
		int count = 0;
		while (requests < 500) {
			System.out.println("The node count is : " + count + " and node id is : " + nodeid);
			if ((canEnterCS == true) && requestedCriticalSection == false) {
				requestedCriticalSection = true;

				for (int i : quorumSet) {

					m.clientCall(i, nodeInfo.get(i).hostname, nodeInfo.get(i).portnumber,
							new ProcessInfo(timestamp, nodeid, 0));

				}

				while (requestedCriticalSection == true) {
			//		System.out.println("still here : nodeid" + nodeid + " no of grants : " + Server.grantMsg);
					// Thread WaitForCS = new Thread();
					try {
						WaitForCS.sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				requests++;
				count++;
				// System.out.println("The request count is : " + requests);
			}

		}
	}

	// to send client requests
	void clientCall(int node, String host, int port, ProcessInfo p) {
		client = new Thread(new Client(node, host, port, p));
		client.start();

	}

}
