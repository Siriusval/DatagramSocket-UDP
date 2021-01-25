package pr.tp2.udp.discovery;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Discovery {

	private static final String GROUP_IP = "225.0.4.1";
	private static final int PORT = 9999;
	private static final String TYPE_IAM = "IAM";
	private static final String TYPE_WHOIS = "WHOIS";
	private static final String TYPE_LEAVING = "LEAVING";

	public static void sendWhois(String id) {
		// Envoie un message Whois
		String msg = "WHOIS:"+id;
		sendMessage(msg);
	}

	public static void sendLeaving(String id) {
		// Envoie un message Leaving
		String msg = "LEAVING:"+id;
		sendMessage(msg);

	}


	public static void sendIAM(String id, String url) {
		// Envoie un message IAM
		String msg = "IAM:"+id+":"+url;
		sendMessage(msg);
	}

	private static void sendMessage(String msg) {
		byte [] contenuMessage = msg.getBytes(StandardCharsets.UTF_8);
		try{
			//Init socket
			MulticastSocket socketEmission = new MulticastSocket(PORT);
			//Create message
			DatagramPacket message = new DatagramPacket(contenuMessage, contenuMessage.length, InetAddress.getByName(GROUP_IP), PORT);
			//Send
			socketEmission.send(message);
			System.out.println("Sent :"+msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void listenAndReply() {
		// Réponds aux WHOIS si ID = ID
		// URL du service :
		String ID = "051005022";
		String URL = "https://istic.univ-rennes1.fr/";

		try{
			// Ecoute et affiche les évennements IAM,LEAVING
			InetAddress groupeIP = InetAddress.getByName(GROUP_IP);
			MulticastSocket socketReception  = new MulticastSocket(PORT);
			socketReception.joinGroup(groupeIP);

			//Init
			byte[] contenuMessage = new byte[1024];
			DatagramPacket message;

			while(true){
				// Reception du packet
				message = new DatagramPacket(contenuMessage, contenuMessage.length);
				socketReception.receive(message);
				String msg = new String(contenuMessage);
				System.out.println("Received msg :"+msg);

				//Analyze
				String [] splittedMsg = msg.split(":");

				if(splittedMsg.length < 2){
					throw new Exception("Error, wrong command");
				}

				//Answer
				else{
					String type = splittedMsg[0];
					String id = splittedMsg[1];

					if (type.equals(TYPE_WHOIS) && id.equals(ID)){
						sendIAM(ID,URL);
					}
				}

				Arrays.fill(contenuMessage, (byte) 0);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}





	}

	public static void main(String[] args) {
		String cmd = args[0], url = null, id = null;
		if (args.length > 1) {
			id = args[1];
		}
		if (args.length == 3) {
			url = args[2];
		}

		switch (cmd) {
		case "listen":
			listenAndReply();
			break;
		case "iam":
			sendIAM(id, url);
			break;
		case "leaving":
			sendLeaving(id);
			break;
		case "whois":
			sendWhois(id);
			break;
		default:
			System.out.println("Erreur de commande");
			break;

		}
	}
}
