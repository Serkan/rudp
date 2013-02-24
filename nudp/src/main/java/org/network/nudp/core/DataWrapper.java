package org.network.nudp.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Holds data with sequence numbers.
 * 
 */
public class DataWrapper {

    private Map<Integer, Byte[]> packetBuffer;

    private int length;

    /**
     * 
     * Default constructor.
     */
    public DataWrapper() {
        this.length = 0;
        packetBuffer = new HashMap<Integer, Byte[]>();
    }

    /**
     * Adds data to cache.
     * 
     * @param sequenceNumber
     * @param section byte array of section
     */
    public void submitSequence(int sequenceNumber, byte[] section) {
        int sectionLength = section.length;
        length += sectionLength;
        Byte[] array = new Byte[sectionLength];
        for (int i = 0; i < sectionLength; i++) {
            array[i] = section[i];
        }
        packetBuffer.put(sequenceNumber, array);
    }

    /**
     * size of cache.
     */
    public int size() {
        return length;
    }

    /**
     * Returns byte array of whole received data.
     */
    public byte[] byteArray() {
        byte[] data = new byte[length];
        Set<Integer> keys = packetBuffer.keySet();
        for (Integer key : keys) {
            Byte[] baObj = packetBuffer.get(key);
            byte[] baPri = new byte[baObj.length];
            for (int i = 0; i < baObj.length; i++) {
                baPri[i] = baObj[i];
            }
            System.arraycopy(baPri, 0, data, key == 1 ? 0 : (key - 1)
                    * Constants.FRAME_SIZE, baObj.length);
        }
        return data;
    }
}
