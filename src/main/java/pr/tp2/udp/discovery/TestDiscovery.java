package pr.tp2.udp.discovery;

public class TestDiscovery {

	public static void main(String[] args) throws InterruptedException {
		Runnable listener = () -> {
			Discovery.handleListen();
		};
		new Thread(listener).start();

		Discovery.handleWhois("051005022");
		Discovery.handleIAM("tftp", "127.0.0.1:6969");

		Thread.sleep(10000);

	}
}
