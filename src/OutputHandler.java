import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.ECPair;
import org.bouncycastle.math.ec.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class OutputHandler {
    //show curve parameters and keys
    public static void showProperties(X9ECParameters ecParams, BigInteger privateKey, ECPoint publicKey) {
        System.out.println();
        System.out.println("CURVE PARAMETERS:\n");

        System.out.println("For equation Y^2 = X^3 + aX + b mod p");
        System.out.println("and order = n / cofactor = h / base point = g\n");

        System.out.println("a: " + ecParams.getCurve().getA());
        System.out.println("b: " + ecParams.getCurve().getB());
        System.out.println("p: " + ((ECCurve.Fp) ecParams.getCurve()).getQ().toString(16) + "\n"); //or q, as it's used by BC

        System.out.println("n: " + ecParams.getN().toString(16));
        System.out.println("h: " + ecParams.getH().toString(16));
        System.out.println("g's X coord.: " + ecParams.getG().getAffineXCoord());
        System.out.println("g's Y coord.: " + ecParams.getG().getAffineYCoord() + "\n");

        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println();

        System.out.println("Private key: " + privateKey.toString(16) + "\n");

        System.out.println("Public key's X coord.: " + publicKey.normalize().getAffineXCoord().toString());
        System.out.println("Public key's Y coord.: " + publicKey.normalize().getAffineYCoord().toString());

        System.out.println();
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    //prints a message in multiple lines with a maximum line length
    public static void printMessage(String message) {
        String[] lines = message.split("(?<=\\G.{146})");
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println();
    }

    //prints out the message points from a list of ECPoints
    public static void showMessagePoints(ArrayList<ECPoint> messagePoints) {
        for (ECPoint point : messagePoints
        ) {
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("X: " + point.getAffineXCoord().toString() + "\n" + "Y: " + point.getAffineYCoord().toString());
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------\n");
    }

    //prints out the message point pairs from a list of ECPairs
    public static void showMessagePointPairs(ArrayList<ECPair> messagePointPairs) {
        for (ECPair pointPair : messagePointPairs
        ) {
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println("X: " + pointPair.getX().getAffineXCoord().toString() + "\n" + "Y: " + pointPair.getX().getAffineYCoord().toString());
            System.out.println("X: " + pointPair.getY().getAffineXCoord().toString() + "\n" + "Y: " + pointPair.getY().getAffineYCoord().toString());
        }
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------\n");
    }

    //prints a byte array in multiple lines with a maximum width
    public static void printByteArray(byte[] bytes, int sectionLength) {
        int index = 0;
        ArrayList<byte[]> segments = new ArrayList<byte[]>();

        //split up the byte array to multiple arrays //last bit is missing
        while (bytes.length > sectionLength) {
            byte[] temp = Arrays.copyOfRange(bytes,0, sectionLength);
            segments.add(temp);
            bytes = Arrays.copyOfRange(bytes, sectionLength, bytes.length);
        }

        //add the remainder to the list
        segments.add(bytes);

        //print the individual arrays
        for (byte[] segment : segments) {
            ArrayList<String> stringList = new ArrayList<>();
            for (byte byteValue : segment
            ) {
                //add 128, as the range of byteValue is -128 to 127, so negative values will be avoided for the presentation ONLY
                int intValue = Integer.parseInt(String.valueOf(byteValue)) + 128;
                stringList.add(Integer.toString(intValue,16));
            }
            System.out.println(concatStringList(stringList));
        }
        System.out.println();
    }

    //converts a list of strings to a long String, such that it can be printed without commas
    public static String concatStringList(ArrayList<String> stringList) {
        String hex = "";
        for (String str : stringList) {
            hex = hex.concat(str + " ");
        }
        return hex;
    }

}
