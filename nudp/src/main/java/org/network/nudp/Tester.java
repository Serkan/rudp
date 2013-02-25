package org.network.nudp;

import java.io.IOException;
import java.net.InetAddress;

import org.network.nudp.file.util.Compressor;

public class Tester {

    /**
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        // byte[] data =
        // "idsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlidsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlidsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlidsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlidsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlidsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlidsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlidsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlidsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlidsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlidsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlidsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlidsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlidsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlidsşlfmsşflsadbgdfhmsiğepromgsdfgxzfsadfas sdfoşlıjsdafpoısjdfğpdsokfüğkdfapuefrğpısojdfö*ğwajemfrğpmjfpısodjfpoasdfjmpjuw*epmırjwe9  p0ıeswaopfkds ğdsakfsdop kfğp oksdap f pdsofkm pkdso mpodsk masdpokf mpo k posdkafpo k posadkf mp  pasdokf pds a fdsafo0pkewfpweokrsdpfkwerpkdfsdafasdfşlğikfaü,kğkera,seflşdsövüasdopföweşlödszipfoöwaexc"
        // .getBytes();
        // System.out.println(data.length);
        // byte[] returned = Compressor.zip(data);
        // System.out.println(returned.length);
        // System.out.println(Compressor.unzip(returned).length);

        InetAddress byName = InetAddress.getByName("127.0.0.1");
        System.out.println(byName.getHostAddress());

        //
        // System.out.println("Reading file");
        // BufferedReader in2 = new BufferedReader(new InputStreamReader(
        // new GZIPInputStream(new FileInputStream("test.gz"))));
        // String s;
        // while ((s = in2.readLine()) != null)
        // System.out.println(s);
    }
}
