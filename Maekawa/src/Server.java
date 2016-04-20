import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Server extends Thread {

	int mynode, port;
	HashMap<Integer, NodeInfo> nodeInfo;
	ArrayList<Integer> quorumSet;
	CompareTime c;
	Thread client;
	Thread enterCS;
	static int grantMsg;
	ServerSocket serversocket;
	Socket clientsocket;

	PriorityQueue<ProcessInfo> pq = null;
	ProcessInfo p;
	static boolean failedMessageRecieved;
	static boolean inCriticalSection;

	public Server(int mynode, int port, HashMap<Integer, NodeInfo> nodeInfo, ArrayList<Integer> quorumSet,
			CompareTime c) {
		this.mynode = mynode;
		this.port = port;
		this.nodeInfo = nodeInfo;
		this.quorumSet = quorumSet;
		this.c = c;

		grantMsg = 0;

		pq = new PriorityQueue<ProcessInfo>(20, c);
		// failedMessageRecieved = false;
		failedMessageRecieved = true;
		inCriticalSection = false;
	}

	public void run() {

		try {
			serversocket = new ServerSocket(port);

			// listenSocket
			while (true) {

				clientsocket = serversocket.accept();

				// Send requests to its Quorum members

				InputStream input = clientsocket.getInputStream();
				ObjectInputStream oinput = new ObjectInputStream(input);

				try {
					p = (ProcessInfo) oinput.readObject();

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				lamport(p);
				if (p.msgIdentifier == 0) {
					// Received Request Message
					pq.add(p);

//					if (inCriticalSection == false) {
				if (pq.size() == 1 ) {
						clientCall(p.nodeid, nodeInfo.get(p.nodeid).hostname, nodeInfo.get(p.nodeid).portnumber,
								new ProcessInfo(Maekawa.timestamp, mynode, 1));
					} else {
						// look for the top of Priority queue
						ProcessInfo temp = pq.poll();

						if (temp.equals(p)) {
							// if the top of priority queue is same as the
							// object that was received
							// Send inquire message
							if (pq.size() > 0) {
								ProcessInfo temp1 = pq.peek();
								pq.add(temp);
								clientCall(temp1.nodeid, nodeInfo.get(temp1.nodeid).hostname,
										nodeInfo.get(temp1.nodeid).portnumber,
										new ProcessInfo(Maekawa.timestamp, mynode, 3));
							}
						} else {
							// if top of priority queue is not same as the
							// object that was received
							// Send Failed message
							pq.add(temp);
							// System.out.println("sending Fail msg" + " from
							// nodeid : " + mynode + " to :" + p.nodeid);
							clientCall(p.nodeid, nodeInfo.get(p.nodeid).hostname, nodeInfo.get(p.nodeid).portnumber,
									new ProcessInfo(Maekawa.timestamp, mynode, 5));

						}
					}
//				}else{
//					
//					// why should we care if we are in cs or not?
//					
//				}
				} else if (p.msgIdentifier == 1) {
					// Received Grant Message
					grantMsg++;
					// if received grant messages from all quorum members, enter
					// critical section

					if (grantMsg == quorumSet.size()) {

						Maekawa.canEnterCS = false;

						enterCS = new Thread(new CriticalSection(p, quorumSet, nodeInfo, mynode, Maekawa.timestamp));
						enterCS.start();
					}

				} else if (p.msgIdentifier == 2) {
					// Received Release Message

					if (!pq.isEmpty()) {
						// pq.poll();
						// that request may not be necessarily on top of the
						// queue in case some one with a lower time stamp
						// generated some request and sent it.
						// So we have to remove the element from the queue.
						pq.remove(p);

						if (pq.size() > 0) {

							ProcessInfo temp = pq.peek();
							// send grant message to the node at the top of
							// priority queue
							clientCall(temp.nodeid, nodeInfo.get(temp.nodeid).hostname,
									nodeInfo.get(temp.nodeid).portnumber,
									new ProcessInfo(Maekawa.timestamp, mynode, 1));

						}
					}

				} else if (p.msgIdentifier == 3) {
					// Received Inquire Message
					// if (failedMessageRecieved == true || inCriticalSection ==
					// false) {
					// changed the OR condition to AND
					// if (failedMessageRecieved == true && inCriticalSection ==
					// false) {
					// if (inCriticalSection == false) {
					// yield immediately
					clientCall(p.nodeid, nodeInfo.get(p.nodeid).hostname, nodeInfo.get(p.nodeid).portnumber,
							new ProcessInfo(Maekawa.timestamp, mynode, 4));
					grantMsg--;
					// }

				} else if (p.msgIdentifier == 4) {
					// Received Yield Message
					// Send grant to top of the queue.
					ProcessInfo temp = pq.peek(); // get the top of the queue.
					clientCall(temp.nodeid, nodeInfo.get(temp.nodeid).hostname, nodeInfo.get(temp.nodeid).portnumber,
							new ProcessInfo(Maekawa.timestamp, mynode, 1));

				} else if (p.msgIdentifier == 5) {
					// Received Failed Message
					failedMessageRecieved = true;

				}

			}
		}

		catch (IOException e) {
			System.out.println("Read Failed");
			e.printStackTrace();
		}

	}

	// to call client
	void clientCall(int node, String host, int port, ProcessInfo p) {
		client = new Thread(new Client(node, host, port, p));
		client.start();

	}

	void lamport(ProcessInfo p) {
		if (p.timestamp < Maekawa.timestamp) {
			Maekawa.timestamp += 1;
		} else {
			Maekawa.timestamp = p.timestamp + 1;
		}
	}

}
