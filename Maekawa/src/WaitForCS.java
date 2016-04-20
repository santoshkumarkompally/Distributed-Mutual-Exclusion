
public class WaitForCS extends Thread {

	public void run() {
		// Waits till requestedCriticalSection is false
		System.out.println("stuck here in wait for CS");
		// try {
		// sleep(10);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

}
