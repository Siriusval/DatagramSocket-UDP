package pr.tp2.udp.discovery;

public class Discovery {

	public static void sendWhois(String id) {
		// Envoie un message Whois
	}

	public static void sendLeaving(String id) {
		// Envoie un message Leaving
	}

	public static void sendIAM(String id, String url) {
		// Envoie un message IAM
	}

	public static void listenAndReply() {
		// Ecoute et affiche les évennements IAM,LEAVING

		// Réponds aux WHOIS si ID = ID
		// URL du service :
		String ID = "051005022";
		String URL = "https://istic.univ-rennes1.fr/";

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
