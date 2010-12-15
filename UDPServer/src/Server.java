import java.net.*;

public class Server {
	
	public static void main(String[] args) {
		
		final int port = 2000;
		byte[] buf = new byte[10];
		
		System.out.println("Started listener UDP on port " + port);		
		
		try {

			DatagramSocket socket = new DatagramSocket(port);

			DatagramPacket reply = new DatagramPacket( buf, 10 );

			byte[] buffer;
			String result;
			
			while(true){
				socket.receive(reply);

				result = "";
				buffer = reply.getData();
				for (int i = 0; i < buffer.length; i++){
					result += Byte.toString(buffer[i]);
				}

				System.out.print("Ricevuto un pacchetto: ");
				System.out.println(result);

				if ((int)(Math.random()*10) > 2){
					
					result = "";
					buffer = reply.getData();
					for (int i = 0; i < buffer.length; i++){
						result += Byte.toString(buffer[i]);
					}
					System.out.println("Invio ACK: " + result);
					
					socket.send(reply);
				}
				else{
					System.out.println("Ops, mi sono perso l'ACK..");
				}

			}

			//socket.close();

		}catch(Exception e){
			System.out.println(e.toString());
		}

	}

}
