package pr.tp2.udp.discovery;

/**
 * Test Discovery class and methods
 * Start server and send a Whois, then an Iam
 * @see Discovery
 */
public class TestDiscovery {

	public static void main(String[] args) throws InterruptedException {
		Runnable listener = Discovery::listenAndReply;
		new Thread(listener).start();

		//Delay to let server launch
		Thread.sleep(1000);

		//Test 1
		Discovery.sendWhois("051005022");
		Thread.sleep(1000);

		//Test 2
		Discovery.sendIAM("tftp", "127.0.0.1:6969");
		Thread.sleep(1000);

		//Test 3
		Discovery.sendLeaving("067e6162");
		Thread.sleep(1000);



	}
}
