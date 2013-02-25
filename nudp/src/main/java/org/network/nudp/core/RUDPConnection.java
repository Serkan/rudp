package org.network.nudp.core;

import java.net.InetAddress;

public class RUDPConnection {

    private DataWrapper dataWrapper;

    private boolean isClose;

    private RUDPHandler handler;

    public RUDPConnection(RUDPHandler handler) {
        this.handler = handler;
        dataWrapper = new DataWrapper();
    }

    public void handle(InetAddress senderAddress, byte[] data) {
        handler.handle(senderAddress, data);
    }

    /**
     * @return the dataWrapper
     */
    public DataWrapper getDataWrapper() {
        return dataWrapper;
    }

    /**
     * @return the isClose
     */
    public boolean isClose() {
        return isClose;
    }

    /**
     * @param isClose the isClose to set
     */
    public void setClose(boolean isClose) {
        this.isClose = isClose;
    }

}
