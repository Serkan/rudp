package org.network.nudp.core;

import java.net.InetAddress;

public interface RUDPHandler {

    void handle(InetAddress senderAddress, byte[] data);

}
