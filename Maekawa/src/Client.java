
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client extends Thread {

	Socket socket;
	String filename;
	File file;
	String host;
	int port, requestNode, mynode;
	ProcessInfo p;

	public Client(int mynode, String host, int port, ProcessInfo p) {
		this.mynode = mynode;
		this.host = host;
		this.port = port;
		this.p = p;

	}

	public void run() {
		try {
			// logger();
			boolean connected = false;
			while (connected != true) {
				try {

					socket = new Socket(host, port);
					connected = true;
				} catch (IOException ex) {

				}
			}

			OutputStream output = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(output);
			ProcessInfo pInfo = p;
			oos.writeObject(pInfo);

			oos.close();
			output.close();
			socket.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void logger() {
		String msg = null;
		switch (p.msgIdentifier) {
		case 0:
			msg = "Request";
			break;
		case 1:
			msg = "Grant";
			break;
		case 2:
			msg = "Release";
			break;
		case 3:
			msg = "Inquire";
			break;
		case 4:
			msg = "Yield";
			break;
		case 5:
			msg = "Failed";
			break;

		}

		System.out.println(p.timestamp + " " + msg + " FROM " + p.nodeid + " TO  " + mynode);

	}

}
