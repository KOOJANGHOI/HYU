package network;

import java.io.*;
import java.net.*;

// Server code
public class Server {
	public static void main(String[] args) {
		/* initialize */
		int port = 0;
		String filename = null;

		/* argument : port address, filename */
		if (args.length == 2) {
			port = Integer.parseInt(args[0]);
			filename = args[1];
		} else {
			/* argument error */
			System.out.println("Usage: args must be [port address] [filename]");
			System.exit(0);
		}
		new server(port, filename); // create server object
	}

	static class server {
		/* initialize */
		ServerSocket server = null;
		Socket client = null;
		String filename = null;
		long startTime = 0;

		/* argument : port address , filename */
		server(int port, String filename) {
			try {
				/* create server's socket */
				server = new ServerSocket(port);
				System.out.println("Socket Created at port:[" + port + "]");
				System.out.println("Wating Connection");

				/* waiting for client */
				client = server.accept();

				/* connection established */
				System.out.println("Client " + client.getInetAddress() + "is connected");

				/* time at file transfer */
				startTime = System.currentTimeMillis();

				/*
				 * create FileReceiver object argument : socket , filename , time at file
				 * transfer
				 */
				FileReceiver fr = new FileReceiver(client, filename, startTime);

				/* run */
				fr.start();
			} catch (Exception e) {
				System.out.println("Error: " + e);
				System.exit(0);
			}
		}
	}

	static class FileReceiver extends Thread {
		/* initialize */
		Socket socket = null;
		DataInputStream dis = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		String filename = null;
		long startTime = 0;
		int state = 0;

		/* argument : socket , filename , time at file transfer */
		public FileReceiver(Socket socket, String filename, long startTime) {
			this.socket = socket;
			this.filename = filename;
			this.startTime = startTime;
		}

		@Override
		public void run() {
			try {
				dis = new DataInputStream(socket.getInputStream());

				/* file creation and create OutputStream */
				File f = new File(filename);
				fos = new FileOutputStream(f);
				bos = new BufferedOutputStream(fos);

				/* record byte date while receiving */
				int len;
				int size = 4096;
				byte[] data = new byte[size];
				while ((len = dis.read(data)) != -1) {
					state++;
					if (state % 10000 == 0) {
						System.out.println("Receiving..." + state / 10000);
					}
					bos.write(data, 0, len);
				}
				
				/* time at end of file receive */
				long endTime = System.currentTimeMillis();
				System.out.println("Elapsed Time(s) : " + (endTime - startTime) / 1000.0);
				
				/* close all */
				bos.flush();
				bos.close();
				fos.close();
				dis.close();
				System.out.println("Success[Receiving]!!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
