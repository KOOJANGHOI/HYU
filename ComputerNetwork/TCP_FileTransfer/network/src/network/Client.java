package network;

import java.io.*;
import java.net.*;

// Client code
public class Client {
	public static void main(String[] args) {
		/* initialize */
		int port = 0;
		InetAddress ip = null;
		String filename = "";

		/* argument : IP address , port address , filename */
		if (args.length == 3) {
			port = Integer.parseInt(args[1]);
			filename = args[2];
			try {
				ip = InetAddress.getByName(args[0]);
			} catch (UnknownHostException e) { // If UnknownHost , Exception handling
				System.out.println("Error on port[" + port + "]");
				e.printStackTrace();
			}
		} else {
			/* argument error */
			System.out.println("Error: args must be [IP address] [port address] [filename]");
			System.exit(0);
		}
		new client(ip, port, filename); // create client object
	}

	static class client {
		/* initialize */
		Socket client = null; // client's socket
		InetAddress ip = null; // IP address
		int port = 0; // port address
		String filename = null; // filename to be sent

		/* argument : IP address , port address , filename */
		client(InetAddress ip, int port, String filename) {
			this.ip = ip;
			this.port = port;
			this.filename = filename;
		
			try {
				/* create client's socket */
				client = new Socket(ip, port);
				System.out.println("Connection Start!!");

				/* 
				 * create FileSender object 
				 * argument : socket , filename
				 * */
				FileSender fs = new FileSender(client, filename);
				/* run */
				fs.start();

			} catch (Exception e) {
				System.out.println("Error: " + e);
				System.exit(0);
			}
		}
	}

	static class FileSender extends Thread {
		/* initialize */
		Socket socket = null;
		DataOutputStream dos = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		String filename = null;
		int state = 0;

		/* argument : socket , filename */
		public FileSender(Socket socket, String filename) {
			this.socket = socket;
			this.filename = filename;
			try {
				/* create DataOutputStream for file transfer */
				dos = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				System.out.println("Error: " + e);
			}
		}

		@Override
		public void run() {
			try {
				/* file transfer while reading contents */
				File f = new File(filename);
				fis = new FileInputStream(f);
				bis = new BufferedInputStream(fis);

				int len = 0;
				int size = 4096;
				byte[] data = new byte[size];
				while ((len = bis.read(data)) != -1) {
					state++;
					if (state % 10000 == 0) {
						System.out.println("Sending..." + state / 10000);
					}
					dos.write(data, 0, len);
				}

				/* close all */
				dos.flush();
				dos.close();
				bis.close();
				fis.close();
				System.out.println("Success[Sending]!!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
