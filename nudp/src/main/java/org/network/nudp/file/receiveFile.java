package org.network.nudp.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.network.nudp.core.ServerRUDPSocket;
import org.network.nudp.file.util.Compressor;

public class receiveFile {

    public static void main(String[] args) throws IOException {
        String arg0 = null;
        try {
            arg0 = args[0];
        } catch (IndexOutOfBoundsException e) {
            System.out
                    .println("Kullanım şekli : 'receiveFile <port>' ,"
                            + "port belirtmemeniz durumunda varsayılan olarak 7777 portu kullanılacaktır");
        }
        Integer listenPort = null;
        listenPort = 7777;
        if (arg0 != null) {
            listenPort = Integer.parseInt(arg0);
        }
        // Start server
        ServerRUDPSocket server = new ServerRUDPSocket(listenPort);
        byte[] metaData = server.receiveBytes();
        byte[] baFileName = new byte[FileTransferConstants.FILE_NAME_SIZE];
        byte[] baFileSize = new byte[FileTransferConstants.FILE_LENGTH_SIZE];
        System.arraycopy(metaData, 0, baFileName, 0,
                FileTransferConstants.FILE_NAME_SIZE);
        System.arraycopy(metaData, FileTransferConstants.FILE_NAME_SIZE,
                baFileSize, 0, FileTransferConstants.FILE_LENGTH_SIZE);
        String fileName = new String(baFileName);
        long filesize = ByteBuffer.wrap(baFileSize).getLong();
        long temp = 0;
        fileName = fileName.substring(fileName.lastIndexOf("\\") + 1,
                fileName.length());
        fileName = fileName.trim();
        String name = FileTransferConstants.DOWNLOAD_DIR + fileName + "alindi";
        File file = new File(name);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream stream = new FileOutputStream(file);
        while (temp != filesize) {
            byte[] data = server.receiveBytes();
            byte[] unzipped = Compressor.unzip(data);
            temp += unzipped.length;
            stream.write(unzipped, 0, unzipped.length);
        }
        stream.close();

    }

}
