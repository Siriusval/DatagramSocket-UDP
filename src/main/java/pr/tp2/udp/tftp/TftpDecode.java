package pr.tp2.udp.tftp;

import java.net.DatagramPacket;

public class TftpDecode {

	public static void main(String[] args) {
		// Attends sur le port 6969

		// Boucle

		// Reception du packet
		DatagramPacket p = null;

		// Affichage du packet

		// Attention à ne pas afficher plus d'informations que nécessaire.

		// Décodage du packet
		decodeRequest(p);
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
