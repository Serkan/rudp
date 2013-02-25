package org.network.nudp.file;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

import org.network.nudp.core.ClientRUDPSocket;
import org.network.nudp.file.util.Compressor;

public class sendFile {

	public static void main(String[] args) throws IOException {
		InetAddress address = InetAddress.getByName("127.0.0.1");
		Integer listenPort = 7777;
		String fileName = "D:\\mail.jar";
		// try {
		// String ipport = args[0];
		// String[] destInfo = ipport.split(":");
		// listenPort = Integer.parseInt(destInfo[0]);
		// address = InetAddress.getByName(destInfo[1]);
		// fileName = args[1];
		// } catch (IndexOutOfBoundsException e) {
		// System.out
		// .println("Kullanım şekli : 'sendFile <alıcı_ip>:<alıcı_port> <dosya adı>'");
		// }
		ClientRUDPSocket sender = null;
		try {
			sender = new ClientRUDPSocket(listenPort, address);
		} catch (SocketTimeoutException e) {
			System.err
					.println("Connection can not be establish, make sure receiver program running on destination.");
			System.exit(0);

		}
		File file = new File(fileName);
		long fileSize = file.length();
		byte[] baMetaData = new byte[264];
		System.arraycopy(fileName.getBytes(), 0, baMetaData, 0,
				fileName.getBytes().length);
		System.arraycopy(ByteBuffer.allocate(8).putLong(fileSize).array(), 0,
				baMetaData, 256, 8);
		// first send file's meta data
		sender.sendBytes(baMetaData);
		FileInputStream stream = new FileInputStream(file);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int nRead = 0;
		while ((nRead = stream.read()) != -1) {
			baos.write(nRead);
		}
		sender.sendBytes(Compressor.zip(baos.toByteArray()));
		stream.close();
		sender.close();
	}
}
