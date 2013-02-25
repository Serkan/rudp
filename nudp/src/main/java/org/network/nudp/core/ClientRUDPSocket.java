package org.network.nudp.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

/**
 * Client impl of reliable {@link DatagramSocket}.<br>
 * It useses proxy design pattern use {@link DatagramSocket} methods and hides
 * from external usages.
 * 
 */
public class ClientRUDPSocket {

    private final DatagramSocket socket;

    private InetAddress destAddress;

    private int destPort;

    /**
     * 
     * Constructor for send data.
     * 
     * @param port
     * @param laddr
     * @throws IOException
     */
    public ClientRUDPSocket(int port, InetAddress laddr) throws IOException {
        socket = new DatagramSocket();
        socket.setSoTimeout(Constants.SOCKET_TIMEOUT);
        handShake(port, laddr);
        this.destAddress = laddr;
        this.destPort = port;
    }

    /**
     * It gurantees remote destination is reachable and correctly answers the
     * requests.
     * 
     * @param port destination port
     * @param laddr destination ip adress
     * @throws IOException
     */
    private void handShake(int port, InetAddress laddr) throws IOException {
        // send helo
        byte[] baHelo = new byte[Constants.HELO_MSG_SIZE + 1];
        baHelo[0] = Constants.HANDSHAKE_PACKET_TYPE;
        System.arraycopy(Constants.HELO_MSG.getBytes(), 0, baHelo, 1, 4);
        DatagramPacket heloPacket = new DatagramPacket(baHelo, baHelo.length,
                laddr, port);
        socket.send(heloPacket);
        byte[] oleh = new byte[4];
        DatagramPacket olehPacket = new DatagramPacket(oleh, oleh.length);
        socket.receive(olehPacket);
        String msg = new String(oleh);
        if (!msg.equals(Constants.OLEH_MSG)) {
            throw new HandShakeException();
        }
    }

    /**
     * It seperates given data sections size of {@link Constants.FRAME_SIZE} and
     * send them with header which contains sequence number, length of sending
     * data. Waits for acknowledgement after sent a section of given data.
     * 
     * @param data data that will be sent
     * @throws IOException
     */
    public void sendBytes(byte[] data) throws IOException {
        int length = data.length;
        int dataSectionLength = 0;
        byte[] remainBuffer = null;

        int remain = length % Constants.FRAME_SIZE;
        int packetcount = 0;
        if (remain != 0 && length < Constants.FRAME_SIZE) {
            dataSectionLength = length;
            packetcount = 1;
        } else if (remain == 0) {
            dataSectionLength = Constants.FRAME_SIZE;
            packetcount = length / Constants.FRAME_SIZE;
        } else if (remain != 0 && length > Constants.FRAME_SIZE) {
            dataSectionLength = Constants.FRAME_SIZE;
            packetcount = length / Constants.FRAME_SIZE;
            remainBuffer = new byte[remain];
        }
        int cursor = 0;
        for (int p = 1; p <= packetcount;) {
            prepareAndSendPacket(data, dataSectionLength, p, cursor);
            // recevive ack if its positive increment cursor
            byte[] baAck = new byte[Constants.ACK_PACKET_SIZE];
            DatagramPacket ackDatagram = new DatagramPacket(baAck,
                    Constants.ACK_PACKET_SIZE);
            boolean timeout = false;
            try {
                socket.receive(ackDatagram);
            } catch (SocketTimeoutException ex) {
                timeout = true;
            }
            byte[] ack = ackDatagram.getData();
            byte metaData = ack[0];
            if (!timeout && metaData == Constants.POS_ACK_PACKET) {
                // move cursor
                cursor += dataSectionLength;
                // move sequence number
                p++;
            }
        }
        if (remainBuffer != null) {
            prepareAndSendPacket(data, remainBuffer.length, packetcount + 1,
                    cursor);
        }
        // prepare eof packet
        byte[] ba = new byte[1];
        ba[0] = Constants.EOF_PACKET;
        DatagramPacket packet = new DatagramPacket(ba, 1, destAddress, destPort);
        socket.send(packet);
    }

    /**
     * It adds header info and send to destination
     * 
     * @param data whole hiven data
     * @param dataSectionLength length of section that will be sent
     * @param sequenceNumber sequence
     * @param cursor index to move the data
     * @throws IOException
     */
    private void prepareAndSendPacket(byte[] data, int dataSectionLength,
            int sequenceNumber, int cursor) throws IOException {
        byte[] packet = new byte[dataSectionLength + Constants.SEQUENCE_SIZE
                + Constants.LENGTH_SIZE + 1];
        int index = 0;
        // set meta data
        packet[index] = Constants.DATA_PACKET;
        // set sequence number
        index++;
        byte[] baSequence = ByteBuffer.allocate(Constants.SEQUENCE_SIZE)
                .putInt(sequenceNumber).array();
        System.arraycopy(baSequence, 0, packet, index, Constants.SEQUENCE_SIZE);
        // set length
        index += Constants.SEQUENCE_SIZE;
        byte[] baLength = ByteBuffer.allocate(Constants.LENGTH_SIZE)
                .putInt(dataSectionLength).array();
        System.arraycopy(baLength, 0, packet, index, Constants.LENGTH_SIZE);
        // set data section
        index += Constants.LENGTH_SIZE;
        System.arraycopy(data, cursor, packet, index, dataSectionLength);
        DatagramPacket sendDatagram = new DatagramPacket(packet, packet.length,
                destAddress, destPort);
        socket.send(sendDatagram);
    }

    public void close() throws IOException {
        byte[] ba = new byte[1];
        ba[0] = Constants.CLOSE;
        DatagramPacket packet = new DatagramPacket(ba, 1, destAddress, destPort);
        socket.send(packet);
        socket.close();
    }

}
