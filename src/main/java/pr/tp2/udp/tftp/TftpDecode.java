package pr.tp2.udp.tftp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class TftpDecode {

	public static void main(String[] args) throws SocketException {
		// Attends sur le port 6969
		int serverPort = 6969;
		DatagramSocket serverSocket =new DatagramSocket(serverPort);

		try {
			int packetNumber = 1;
			byte buffer[] = new byte[100];
			// Boucle
			while(true){

				// Reception du packet
				DatagramPacket reception = new DatagramPacket(buffer, buffer.length);
				serverSocket.receive(reception);

				// Affichage du packet
				System.out.println("PACKET NUMBER "+packetNumber+" :");
				//Whole buffer
				System.out.println("Whole buffer :");
				affiche(buffer);
				System.out.println();

				//Truncated buffer
				System.out.println("Truncated buffer :");
				int length = reception.getLength();
				byte [] copy = Arrays.copyOfRange(buffer,0,length);
				affiche(copy);
				System.out.println();

				// Attention à ne pas afficher plus d'informations que nécessaire.
				// ?
				// Décodage du packet
				System.out.println("Decoded packet :");

				decodeRequest(reception);
				System.out.println();

				Arrays.fill(buffer, (byte) 0);
				packetNumber++;
				System.out.println("----------------------------------------");
			}
		}
		catch(Exception e){
			System.out.println("Error : "+e.getMessage());
		}
		finally {
			serverSocket.close();
			System.out.println("Server closed.");

		}

	}

	public static void affiche(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			if (i % 16 == 0) {
				System.out.println("\n");
			}
			System.out.printf("%02x ", bytes[i]);
		}
	}

	public static void decodeRequest(DatagramPacket p) {
		System.out.printf("Type : %s, fichier : %s, mode %s", "RRQ", "test.txt", "ascii");
	}
}
