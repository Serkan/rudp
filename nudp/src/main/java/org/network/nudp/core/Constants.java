package org.network.nudp.core;

/**
 * Core functionality constants
 */
public final class Constants {

    // first byte of packet, flag may indicate packet type or etc..
    public static final int FLAG_SET_SIZE = 1;

    // packet sequnce number, sequence numbers are int values thus they are 4
    // byte
    public static final int SEQUENCE_SIZE = 4;

    // length of data section, lengths are int values thus they are 4 byte
    public static final int LENGTH_SIZE = 4;

    // handshake msg length
    public static final int HELO_MSG_SIZE = 4;

    // handshake packet type
    public static final int HANDSHAKE_PACKET_TYPE = 32;

    // first message of handshake by client
    public static final String HELO_MSG = "HELO";

    // first message of handshake by server
    public static final String OLEH_MSG = "OLEH";

    // window size
    public static final int FRAME_SIZE = 1024;

    // positive ack packet indicates data received succesfully
    public static final byte POS_ACK_PACKET = 96;

    // negative ack packet indicates data can not received
    public static final byte NEG_ACK_PACKET = 64;

    // indicates data packet type
    public static final byte DATA_PACKET = 0;

    // indicates end of data packet type
    public static final byte EOF_PACKET = (byte) 192;

    // size of ack packet type
    public static final int ACK_PACKET_SIZE = SEQUENCE_SIZE + 1;

    // socket ti̇me out
    public static final int SOCKET_TIMEOUT = 5000;

    public static final byte CLOSE = (byte) 255;

    /**
     * Hidden constructor.
     */
    private Constants() {
    }
}
