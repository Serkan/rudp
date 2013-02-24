package org.network.nudp.core;

import java.net.SocketException;

/**
 * Thrown by client in case of destination unreachable.
 * 
 */
public class HandShakeException extends SocketException {

    private static final long serialVersionUID = 129291755002289519L;

    public HandShakeException() {
        super();
    }
}
