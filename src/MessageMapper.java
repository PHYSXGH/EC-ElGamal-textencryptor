import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class MessageMapper {

    private ECCurve.Fp curve;
    private int fieldSize;

    //initialize the curve’s parameters
    public MessageMapper(ECCurve.Fp curve) {
        //set the curve and other variables
        this.curve = curve;
        fieldSize = curve.getFieldSize();
    }

    //the main mapping algorithm that does the segmentation
    public ArrayList<ECPoint> mapMessage(String message) {

        //convert message using UTF-8 character mappings
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        //UTF-16 version
        //byte[] bytes2 = message.getBytes(StandardCharsets.UTF_16);

        System.out.println("The following lines contain the UTF-8 encoded plaintext with each line corresponding to a point:\n");
        //printByteArray(bytes);
        OutputHandler.printByteArray(bytes, (fieldSize-8)/8);

        //an ArrayList to store the segment(s)
        ArrayList<byte[]> byteArrays = new ArrayList<>();

        //break it up to segments of length (fieldSize-8)/8 and pad the last 8 bits
        if (bytes.length > (fieldSize-8)/8) {
            System.out.println("The message needs to be broken up to multiple substrings, as its length exceeds what can be encoded in a single point\n");

            //a loop to break up the array into segments
            while (bytes.length > (fieldSize-8)/8) {
                //create a new array for the segment
                byte[] segment = new byte[(fieldSize)/8];

                //copy part of the original array to that segment
                System.arraycopy(bytes,0,segment,0,(fieldSize-8)/8);

                //remove the copied segment from the original array
                byte[] newBytes = new byte[bytes.length-(fieldSize-8)/8];
                System.arraycopy(bytes,(fieldSize-8)/8,newBytes,0,bytes.length-(fieldSize-8)/8);
                bytes = newBytes;

                //add segment to the byteArrays
                byteArrays.add(segment);

                //for debugging purposes only
                //System.out.println("Segment added");
                //System.out.println("Leftover length: " + bytes.length);
            }
        }

        //do the last bit here with padding, the padding will use SPACES 'hex=80'
        byte[] segment = new byte[(fieldSize)/8];
        System.arraycopy(bytes,0,segment,0,bytes.length);
        byteArrays.add(segment);

        //for debugging purposes
        //System.out.println("Segment added");

        //print the segments and convert them to ECPoints
        System.out.println("The following arrays show the SEGMENTED and PADDED version of the plaintext and the ECPoints which contain these segments\n");

        //create an array to store the message points
        ArrayList<ECPoint> messagePoints = new ArrayList<>();

        //print the segments and convert them to points
        for (byte[] byteArray : byteArrays
        ) {
            printByteArray(byteArray);
            ECPoint point = null;
            point = mapToPoint(byteArray);
            messagePoints.add(point);
        }

        return messagePoints;
    }

    //get the message back from a list of points
    public String getMessage(ArrayList<ECPoint> messagePoints) {
        //a list to store the x-coordinates *in the original order*
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        System.out.println("The reconstructed byte arrays are:\n");

        for (ECPoint messagePoint : messagePoints
             ) {
            //convert the x-coordinate to a String
            BigInteger encoded = messagePoint.getXCoord().toBigInteger();
            //then that String to a byte[]
            byte[] bytes = encoded.toByteArray();

            //IMPORTANT
            //if the length of the byte array is (fieldsize+8)/8, then the first [80] needs to be removed, otherwise it's fine.

            //I have no idea what's causing it, probably an overflow, and I get a 33-byte array when decrypting a point
            //that originally contained a 32-bit array if there are special characters contained in the string which
            //usually have large values.

            if (bytes.length == (fieldSize + 8) / 8) {
                bytes = Arrays.copyOfRange(bytes, 1, bytes.length);
            }

            //print the byte array
            printByteArray(bytes);
            //create an empty byte[]
            byte[] unpaddedBytes = new byte[(fieldSize-8)/8];
            //copy the text to teh new byte[] in order to remove padding (last 8 bits)
            System.arraycopy(bytes,0,unpaddedBytes,0,(fieldSize-8)/8);
            //add byte[] to the output
            try {
                output.write(unpaddedBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        }

        //convert ByteArrayOutputStream to a byte[]
        byte[] messageBytes = output.toByteArray();

        System.out.println("Trimmed and concatenated:\n");

        OutputHandler.printByteArray(messageBytes, (fieldSize-8)/8);

        //convert byte[] to String
        String message = new String(messageBytes, StandardCharsets.UTF_8);

        //.trim() removes leading and trailing whitespace and tabs from the String
        return message.trim();
    }

    //convert the byte[] to a binary/hex array for presentational purposes ONLY
    public void printByteArray(byte[] bytes) {
        ArrayList<String> stringList = new ArrayList<>();
        for (byte byteValue : bytes
        ) {
            //add 128, as the range of byteValue is -128 to 127, so negative values will be avoided for the presentation ONLY
            int intValue = Integer.parseInt(String.valueOf(byteValue)) + 128;
            stringList.add(Integer.toString(intValue,16));
        }

        //byte[] length
        System.out.println("Length of byte[]: " + bytes.length);

        //show the hex values
        System.out.println("Message in hex form, encoded using UTF-8: ");
        System.out.println(OutputHandler.concatStringList(stringList));
    }

    public ECPoint mapToPoint(byte[] bytes) {
        //convert the byte[] to a BigInteger
        BigInteger initialX = new BigInteger(1, bytes, 0, fieldSize/8); // par 1 to denote polarity, offset and length (in bytes) are not necessary
        String hexMsgCoord = initialX.toString(16);
        //System.out.println("Initial X-coordinate of Pm that contains the message in the " + (bytes.length - 1) + " most significant bytes: " + hexMsgCoord);

        //get the curve's parameters
        ECFieldElement a = curve.getA();
        ECFieldElement b = curve.getB();

        //a new variable to store the X-coordinate's value as a BigInteger
        BigInteger xInt = initialX.subtract(BigInteger.ONE);//subtract one, as the loop will add one before the first try

        //an ECPoint using the initial X-coordinate
        //REQUIRES THE CURVE TO BE OVER FIELD F(p)
        ECFieldElement x = new ECFieldElement.Fp(curve.getQ(), xInt);

        //the predicted Y coordinate, initialized as null
        ECFieldElement y = null;

        //a temporary value, which represents (x^3 + ax + b) mod p
        ECFieldElement temp = null;

        //Y^2.sqrt(); //null if there is no sqrt

        System.out.println("---PROBING---");

        while (y == null) {
            //increment the X-coordinate by 1
            x = x.addOne();

            //some progress indicator
            System.out.println("X: " + x);

            //calculate Y
            temp = x.multiply(x).multiply(x).add(a.multiply(x)).add(b);
            y = temp.sqrt();
        }

        System.out.println("valid with");
        System.out.println("Y: " + y + "\n");

        /*
        //a quick check to ensure that the point is indeed valid
        ECFieldElement lhs = y.multiply(y);
        ECFieldElement rhs = x.multiply(x).multiply(x).add(a.multiply(x)).add(b);
        //System.out.println("lhs: " + lhs);
        //System.out.println("rhs: " + rhs);
        boolean pointIsOnCurve = lhs.equals(rhs);
        System.out.println("On Curve: " + pointIsOnCurve);
         */

        //WARNING
        //After creating the CustECPoint object from the two ECFieldElement objects, .isValid() will evaluate to FALSE,
        //even though the point is correct and does lie on the curve. That is due to my modification of the ECPoint class.

        ECPoint encodedMessagePoint = new CustECPoint(curve,x,y);

        return encodedMessagePoint;
    }

}
