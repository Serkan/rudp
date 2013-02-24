package org.network.nudp.file.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Data compressor utility.
 * 
 */
public final class Compressor {

    /**
     * 
     * Hidden constructor.
     */
    private Compressor() {
    }

    public static byte[] zip(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        ByteArrayOutputStream baOut = new ByteArrayOutputStream();
        BufferedOutputStream out = new BufferedOutputStream(
                new GZIPOutputStream(baOut));
        System.out.println("Writing file");
        int c;
        while ((c = bais.read()) != -1) {
            out.write(c);
        }
        bais.close();
        out.close();
        return baOut.toByteArray();
    }

    public static byte[] unzip(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        BufferedInputStream in = new BufferedInputStream(new GZIPInputStream(
                bais));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int nRead = 0;
        while ((nRead = in.read()) != -1) {
            out.write(nRead);
        }
        out.close();
        bais.close();
        in.close();
        return out.toByteArray();
    }

}
