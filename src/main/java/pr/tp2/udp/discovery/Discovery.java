package pr.tp2.udp.discovery;

public class Discovery {

	public static void handleWhois(String id) {
		// Envoie un message Whois
	}

	public static void handleLeaving(String id) {
		// Envoie un message Leaving
	}

	public static void handleIAM(String id, String url) {
		// Envoie un message IAM
	}

	public static void handleListen() {
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
			handleListen();
			break;
		case "iam":
			handleIAM(id, url);
			break;
		case "leaving":
			handleLeaving(id);
			break;
		case "whois":
			handleWhois(id);
			break;
		default:
			System.out.println("Erreur de commande");
			break;

		}
	}
}
