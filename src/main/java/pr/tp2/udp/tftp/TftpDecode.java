package pr.tp2.udp.tftp;



import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * TFTP/UDP server
 * - Create a server
 * - Get the message
 * - Truncate the buffer to get important data
 * - decode it
 * - print it in console
 *
 * Protocol definition
 * https://tools.ietf.org/html/rfc1350
 */
public class TftpDecode {

	/**
	 * Enum for TFTP Opcodes
	 */
	private enum OPCODE {
		RRQ,//Read request
		WRQ,//Write request
		DATA,//Data
		ACK,//Acknowledgment
		ERROR //Error
	}


	/**
	 * Return Opcode from a number
	 * @param code, the opcode number
	 * @return, the Enum OPCODE
	 */
	private static OPCODE getOperationFromCode(int code){
		switch (code){
			case 1:
				return OPCODE.RRQ;
			case 2:
				return OPCODE.WRQ;
			case 3:
				return OPCODE.DATA;
			case 4:
				return OPCODE.ACK;
			case 5:
				return OPCODE.ERROR;
		}
		return null;
	}

	/**
	 * Main method
	 * @param args, _
	 * @throws SocketException if error while creating server
	 */
	public static void main(String[] args) throws SocketException {
		// Attends sur le port 6969
		int serverPort = 6969;
		DatagramSocket serverSocket =new DatagramSocket(serverPort);

		try {
			int packetNumber = 1;
			byte[] buffer = new byte[100];
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
				// Attention à ne pas afficher plus d'informations que nécessaire.
				System.out.println("Truncated buffer :");
				int length = reception.getLength();
				byte [] copy = Arrays.copyOfRange(buffer,0,length);
				affiche(copy);
				System.out.println();

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
			e.printStackTrace();
		}
		finally {
			serverSocket.close();
			System.out.println("Server closed.");

		}

	}

	/**
	 * Print a byte array in console (hex)
	 * @param bytes, the byte array to print
	 */
	public static void affiche(byte[] bytes) {
		for (int i = 0; i < bytes.length; i++) {
			if (i % 16 == 0) {
				System.out.println("\n");
			}
			System.out.printf("%02x ", bytes[i]);
		}
	}


	/**
	 * Decode request
	 * Print the message of a packet in a string
	 * @param p, the TFTP packet to decode
	 */
	public static void decodeRequest(DatagramPacket p) throws Exception {
		/*
		 opcode  operation
            1     Read request (RRQ)
            2     Write request (WRQ)
            3     Data (DATA)
            4     Acknowledgment (ACK)
            5     Error (ERROR)
		 */

		//Get data
		byte [] dataUntrimmed = p.getData();

		//Trim
		int packetLength = p.getLength();
		byte [] packetData = Arrays.copyOfRange(dataUntrimmed,0,packetLength);

		//Use byte buffer for easier manipulation
		ByteBuffer byteBuffer = ByteBuffer.wrap(packetData);

		//GET OPCODE
		short opcode = byteBuffer.getShort(); //0001
		System.out.println("Opcode:"+opcode); //1

		//Handle differnetly for each opCode
		switch (getOperationFromCode(opcode)){
			case RRQ:
			case WRQ:
				decodeTFTPRQ(packetLength, byteBuffer, opcode);
				break;
			case DATA:
				decodeTFTPData(packetLength, byteBuffer);
				break;
			case ACK:
				decodeTFTPAck(byteBuffer);
				break;
			case ERROR:
				decodeTFTPError(packetLength,byteBuffer);
				break;
			default:
				throw new Exception("Error in opcode");
		}
	}

	/**
	 * Helper method for decodeRequest
	 * decode from the buffer:
	 * - block number
	 * @param byteBuffer, the byte buffer
	 */
	private static void decodeTFTPAck( ByteBuffer byteBuffer) {
    /*
				2 bytes     2 bytes
				---------------------
				| Opcode |   Block #  |
				---------------------
				Figure 5-3: ACK packet
	 */
		//GET BlockNumber
		int block = byteBuffer.getShort();
		System.out.println("Block # :"+block);

		System.out.printf("Type : %s, Block # : %s", OPCODE.ACK.toString(), block);
	}

	/**
	 * Helper method for decodeRequest
	 * decode from the buffer :
	 * - block number
	 * - data
	 * @param packetLength , the length of the TFTP package
	 * @param byteBuffer, the byte buffer
	 */
	private static void decodeTFTPData(int packetLength, ByteBuffer byteBuffer) {
		/*
			  		2 bytes     2 bytes      n bytes
                   ----------------------------------
                  | Opcode |   Block #  |   Data     |
                   ----------------------------------

                        Figure 5-2: DATA packet
			 */

		//GET BlockNumber
		int block = byteBuffer.getShort();
		System.out.println("Block # :"+block);

		//GET data
		byte [] dataBytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), packetLength); //until the end
		String data = new String(dataBytes);
		System.out.println("Data :"+data);


		System.out.printf("Type : %s, Block # : %s, Data : %s", OPCODE.DATA.toString(), block, data);
	}

	/**
	 * Helper method for decodeRequest
	 * decode from the buffer :
	 * - filename
	 * - mode
	 * @param packetLength , the length of the TFTP package
	 * @param byteBuffer, the byte buffer
	 * @param opcode, 1 if read, 2 if write
	 */
	private static void decodeTFTPRQ(int packetLength, ByteBuffer byteBuffer, short opcode) {
			/*
			2 bytes     string    1 byte     string   1 byte
            ------------------------------------------------
           | Opcode |  Filename  |   0  |    Mode    |   0  |
            ------------------------------------------------

                       Figure 5-1: RRQ/WRQ packet
          Source : https://tools.ietf.org/html/rfc1350
		 */

		//GET FILENAME
		//find next zero
		int offset = getNextZeroIndex(Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), packetLength));

		//substring
		byte [] filenameByte = new byte[offset];
		byteBuffer.get(filenameByte);
		String filename = new String(filenameByte);
		System.out.println("Filename:"+filename); //test.html

		//GET MODE
		//find next 0
		byte [] modeBytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), packetLength); //until the end
		String mode = new String(modeBytes);
		System.out.println("Mode:"+mode); //RRQ

		//Print
		//System.out.printf("Type : %s, fichier : %s, mode %s", "RRQ", "test.txt", "ascii"); //Test
		System.out.printf("Type : %s, fichier : %s, mode %s", getOperationFromCode(opcode).toString(), filename, mode);
	}

	/**
	 * Helper method for decodeRequest
	 * decode from the buffer :
	 * - errorCode
	 * - errorMsg
	 * @param packetLength , the length of the TFTP package
	 * @param byteBuffer, the byte buffer
	 */
	private static void decodeTFTPError(int packetLength, ByteBuffer byteBuffer) {
		/*
			 	2 bytes     2 bytes      string    1 byte
               -----------------------------------------
              | Opcode |  ErrorCode |   ErrMsg   |   0  |
               -----------------------------------------

                        Figure 5-4: ERROR packet
			 */

		//GET ErrorCode
		int errorCode = byteBuffer.getShort();
		System.out.println("Error code :"+errorCode);

		byte [] errMsgBytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), packetLength); //until the end
		String errMsg = new String(errMsgBytes);
		System.out.println("ErrMsg :"+errMsg);

		System.out.printf("Type : %s, Error code : %s, ErrMsg : %s", OPCODE.ERROR.toString(), errorCode,errMsg);
	}


	/**
	 * Helper method for decodeTFTPRQ
	 * The packet contains 3 infos separated with a zero-byte
	 * Finding the next zeroByte is important to extract data from it
	 *
	 * @param byteArray the array to search in
	 *
	 * @return the index of the zeroByte
	 */
	private static int getNextZeroIndex(byte [] byteArray){
		for (int i = 0; i< byteArray.length; i++){
			if(byteArray[i] == (byte) 0){
				return i;
			}
		}
		return -1;
	}


}
