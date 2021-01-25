package pr.tp2.udp.tftp;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class TftpPutServeur {

	public static void main(String[] args) throws SocketException {
		// Attends sur le port 6969
		int serverPort = 6969;
		DatagramSocket serverSocket =new DatagramSocket(serverPort);

		try {
			short packetNumber = 1;
			byte buffer[] = new byte[100];
			// Boucle
			while(true){

				// Reception du packet
				DatagramPacket reception = new DatagramPacket(buffer, buffer.length);
				serverSocket.receive(reception);

				//Truncated buffer
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

				// Envoyer acquittement
				sendAck(serverSocket, (short) packetNumber, reception);
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
	//SocketAddress -> DatagramPacket
	public static void sendAck(DatagramSocket server, short seqNumber, DatagramPacket dstAddr) throws IOException {

		// Construire le paquet avec les bonnes informations
		ByteBuffer byteBuffer = ByteBuffer.allocate(4);
		byteBuffer.putShort((short) 4); //code for ack : 4, https://tools.ietf.org/html/rfc1350
		byteBuffer.putShort(seqNumber);

		// afficher le tableau de bytes envoyé
		byte [] buffer = byteBuffer.array();
		System.out.println("Buffer to send :");
		affiche(buffer);
		System.out.println();

		// Envoyer le paquet à la bonnes addresses
		DatagramPacket paquetEnvoie = new DatagramPacket(buffer, buffer.length, new InetSocketAddress(dstAddr.getAddress(), dstAddr.getPort()));
		server.send(paquetEnvoie);
		System.out.println("Send "+seqNumber+" to "+dstAddr.getAddress()+":"+dstAddr.getPort());
		System.out.println("----------------------------------------");


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
