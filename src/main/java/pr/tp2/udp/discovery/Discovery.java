package pr.tp2.udp.discovery;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Discovery class
 * Service that allows to :
 * - Listen to commands on multicast server (listenAndReply)
 * - send WHOIS cmd to get the ip of a device (by id)
 * - send IAM cmd to send back ip of a device
 * - send LEAVING cmd to say device is not online anymore
 */
public class Discovery {

	/** IP of multicast group*/
	private static final String GROUP_IP = "225.0.4.1";
	/** Port of multicast group*/
	private static final int PORT = 9999;

	/**
	 * Types of available cmd
	 */
	private enum TYPE {
		IAM,
		WHOIS,
		LEAVING
	}

	/**
	 * Send a WHOIS message
	 * @param id, id of device
	 */
	public static void sendWhois(String id) {
		// Envoie un message Whois
		String msg = TYPE.WHOIS.toString()+":"+id;
		sendMessage(msg);
	}

	/**
	 * Send a LEAVING message
	 * @param id, id of device
	 */
	public static void sendLeaving(String id) {
		// Envoie un message Leaving
		String msg = TYPE.LEAVING.toString()+":"+id;
		sendMessage(msg);

	}

	/**
	 * Send a IAM message
	 * @param id, id of device
	 * @param url, url of the device
	 */
	public static void sendIAM(String id, String url) {
		// Envoie un message IAM
		String msg = TYPE.IAM.toString()+":"+id+":"+url;
		sendMessage(msg);
	}

	/**
	 * Helper function for sendWhois, sendIAM, send Leaving
	 * Convert a msg to byte array and send it on multicast network
	 * @param msg, the message to send
	 */
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

	/**
	 * Simulate a multicast server
	 * listen all messages, and answer to WHOIS if id is correct
	 */
	public static void listenAndReply() {
		// Réponds aux WHOIS si ID = ID
		// URL du service :
		String ID = "051005022";
		String URL = "https://istic.univ-rennes1.fr/";

		try{
			// Ecoute et affiche les évenements IAM,LEAVING
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
				System.out.println("Received msg : "+msg);

				//Analyze
				String [] splittedMsg = msg.split(":");

				if(splittedMsg.length < 2){
					throw new Exception("Error, wrong command");
				}

				//Answer
				else{
					String type = splittedMsg[0].trim();
					String id = splittedMsg[1].trim();

					if (type.equals(TYPE.WHOIS.toString()) && id.equals(ID)){
						sendIAM(ID,URL);
					}
				}

				Arrays.fill(contenuMessage, (byte) 0);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}





	}

	/**
	 * Main method, to try cmds
	 * @param args, _
	 */
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
