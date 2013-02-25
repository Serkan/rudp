package org.network.nudp.core;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Server impl of reliable {@link DatagramSocket}.<br>
 * It useses proxy design pattern use {@link DatagramSocket} methods and hides
 * from external usages.
 * 
 */
public class ServerRUDPSocket {

    private final DatagramSocket socket;

    private Map<InetAddress, RUDPConnection> connections;

    /**
     * 
     * Constructor for receive data.
     * 
     * @param port port to run on
     * @throws SocketException
     */
    public ServerRUDPSocket(int port) throws SocketException {
        socket = new DatagramSocket(port);
        connections = new WeakHashMap<InetAddress, RUDPConnection>();
    }

    /**
     * Listens the specified port and reads the stream until receive whole data.
     * 
     * @throws IOException
     */
    public void listen(RUDPHandler handler) throws IOException {
        while (true) {
            byte[] packet = new byte[Constants.FRAME_SIZE
                    + Constants.SEQUENCE_SIZE + Constants.LENGTH_SIZE + 1];
            DatagramPacket received = new DatagramPacket(packet, packet.length);
            socket.receive(received);
            InetAddress senderAddress = received.getAddress();
            int senderPort = received.getPort();
            byte[] innerPacket = packet;
            ByteBuffer buffer = null;
            int index = 0;
            // first one byte packet meta data
            byte metaData = innerPacket[0];
            /*
             * check first two bit (packet type indicator)
             * 
             * 00-> data packet 11-> end of stream
             */
            if (metaData == Constants.DATA_PACKET && isValid(senderAddress)) {
                byte[] baSequence = new byte[Constants.SEQUENCE_SIZE];
                byte[] baLength = new byte[Constants.LENGTH_SIZE];
                // second four byte for sequence number
                index += Constants.FLAG_SET_SIZE;
                System.arraycopy(innerPacket, index, baSequence, 0,
                        Constants.SEQUENCE_SIZE);
                buffer = ByteBuffer.wrap(baSequence);
                int sequence = buffer.getInt();
                // third four byte for length of data section
                index += Constants.SEQUENCE_SIZE;
                System.arraycopy(innerPacket, index, baLength, 0,
                        Constants.LENGTH_SIZE);
                buffer = ByteBuffer.wrap(baLength);
                int length = buffer.getInt();
                // extract data from packet
                index += Constants.LENGTH_SIZE;
                byte[] data = new byte[length];
                // when trying get actual data from datagram, if we get wrong
                // size
                // of
                // data because of
                // corruption then it must be resend current sequence data
                byte[] acknowledge = new byte[Constants.ACK_PACKET_SIZE];
                // send a positive ack packet
                acknowledge[0] = Constants.POS_ACK_PACKET;
                try {
                    System.arraycopy(innerPacket, index, data, 0, length);
                } catch (IndexOutOfBoundsException ex) {
                    // send a negative ack packet and request the packet
                    acknowledge[0] = Constants.NEG_ACK_PACKET;
                }
                System.arraycopy(baSequence, 0, acknowledge, 1,
                        Constants.SEQUENCE_SIZE);
                DatagramPacket p = new DatagramPacket(acknowledge,
                        Constants.ACK_PACKET_SIZE, senderAddress, senderPort);
                socket.send(p);
                if (acknowledge[0] == Constants.NEG_ACK_PACKET) {
                    continue;
                }
                // we assume we did not get any exception after this
                RUDPConnection connection = connections.get(senderAddress);
                connection.getDataWrapper().submitSequence(sequence, data);
            } else if (metaData == Constants.HANDSHAKE_PACKET_TYPE) {
                byte[] baMsg = new byte[Constants.HELO_MSG_SIZE];
                System.arraycopy(innerPacket, 1, baMsg, 0, 4);
                String msg = new String(baMsg);
                if (msg.equals(Constants.HELO_MSG)) {
                    byte[] baOleh = Constants.OLEH_MSG.getBytes();
                    DatagramPacket olehPacket = new DatagramPacket(baOleh,
                            baOleh.length, senderAddress, senderPort);
                    socket.send(olehPacket);
                    RUDPConnection connection = new RUDPConnection(handler);
                    connections.put(senderAddress, connection);
                }
            } else if (metaData == Constants.EOF_PACKET
                    && isValid(senderAddress)) {
                RUDPConnection connection = connections.get(senderAddress);
                connection.handle(senderAddress, connection.getDataWrapper()
                        .byteArray());
            } else if (metaData == Constants.CLOSE) {
                RUDPConnection connection = connections.get(senderAddress);
                connection.setClose(true);
            }
        }
    }

    private boolean isValid(InetAddress senderAddress) {
        RUDPConnection connection = connections.get(senderAddress);
        return !(connection == null || connection.isClose());
    }
}
