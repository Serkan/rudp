package org.network.nudp.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;

import org.network.nudp.core.RUDPHandler;
import org.network.nudp.core.ServerRUDPSocket;
import org.network.nudp.file.util.Compressor;

public class receiveFile {

    public static void main(String[] args) throws IOException {
        String arg0 = null;
        Integer listenPort = null;
        listenPort = 7777;
        try {
            arg0 = args[0];
            if (arg0 != null) {
                listenPort = Integer.parseInt(arg0);
            }
        } catch (NumberFormatException e) {
            warning();
        } catch (IndexOutOfBoundsException e) {
            warning();
        }
        // Start server
        ServerRUDPSocket server = new ServerRUDPSocket(listenPort);
        server.listen(new RUDPHandler() {

            @Override
            public void handle(final InetAddress senderAddress,
                    final byte[] data) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        byte[] baFileName = new byte[FileTransferConstants.FILE_NAME_SIZE];
                        byte[] baFileSize = new byte[FileTransferConstants.FILE_LENGTH_SIZE];
                        System.arraycopy(data, 0, baFileName, 0,
                                FileTransferConstants.FILE_NAME_SIZE);
                        System.arraycopy(data,
                                FileTransferConstants.FILE_NAME_SIZE,
                                baFileSize, 0,
                                FileTransferConstants.FILE_LENGTH_SIZE);
                        String fileName = new String(baFileName);
                        fileName = fileName.substring(
                                fileName.lastIndexOf("\\") + 1,
                                fileName.length());
                        fileName = fileName.trim();
                        int period = fileName.lastIndexOf(".");
                        fileName = senderAddress.getHostAddress() + "_"
                                + fileName.substring(0, period - 1) + "_alindi"
                                + fileName.substring(period);
                        String name = FileTransferConstants.DOWNLOAD_DIR
                                + fileName;
                        try {
                            File file = new File(name);
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            FileOutputStream stream = new FileOutputStream(file);
                            int contentLength = data.length - 264;
                            byte[] content = new byte[contentLength];
                            System.arraycopy(data, 264, content, 0,
                                    contentLength);
                            byte[] unzipped = Compressor.unzip(content);
                            stream.write(unzipped, 0, unzipped.length);
                            stream.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                thread.run();
            }
        });
    }

    private static void warning() {
        System.out.println("Kullanım şekli : " + "'receiveFile <port>' ,"
                + "port belirtmemeniz durumunda"
                + " varsayılan olarak 7777 portu " + "kullanılacaktır");
    }
}
