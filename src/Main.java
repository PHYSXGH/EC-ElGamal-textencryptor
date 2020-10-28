import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.*;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.math.ec.*;

import java.math.BigInteger;
import java.security.*;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        //EC-DSA
        System.out.println("This is v1.4 of the ECC Encryption application by PHYSGH.\n");

        /* get the curve */
        X9ECParameters ecParams = InputHandler.getCurve();
        //copy the spec to a different type for the encryptor and decryptor objects
        ECDomainParameters ecDomainPars = new ECDomainParameters(ecParams.getCurve(), ecParams.getG(), ecParams.getN(), ecParams.getH());

        /* generate the private key */
        SecureRandom randGen = new SecureRandom(); //add option for costom seed
        BigInteger privateKey = new BigInteger(ecParams.getCurve().getFieldSize(),randGen);

        /* public key = base/generator point ^ private key */
        ECPoint publicKey = ecParams.getG().multiply(privateKey);

        //private key's parameters
        ECPrivateKeyParameters privKP = new ECPrivateKeyParameters(privateKey, ecDomainPars);

        //public key's parameters
        ECPublicKeyParameters publKP = new ECPublicKeyParameters(publicKey, ecDomainPars);

        //the encryptor object //note that it only has access to the public key
        ECElGamalEncryptor encryptor = new ECElGamalEncryptor();
        encryptor.init(publKP);

        //the decryptor object //note that it can access the private key
        ECElGamalDecryptor decryptor = new ECElGamalDecryptor();
        decryptor.init(privKP);

        //create and initialize the MessageMapper
        MessageMapper mapper = new MessageMapper((ECCurve.Fp) ecParams.getCurve());

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //show the properties of the curve
        OutputHandler.showProperties(ecParams, privateKey, publicKey);

        /* get the message */
        //String message = "こんにちは、世界！"; //non-ascii chars could cause an overflow issue, which has been resolved
        String message = InputHandler.getMessage();
        System.out.println("Message to be encrypted:\n");
        OutputHandler.printMessage(message);

        /* map the message to valid points on the elliptic curve */
        ArrayList<ECPoint> messagePoints = mapper.mapMessage(message);

        System.out.println("The message is stored in the " + messagePoints.size() + " point(s) below:\n");

        OutputHandler.showMessagePoints(messagePoints);

        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------ENCRYPTING--------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------\n");

        System.out.println("Using the public key below:");
        System.out.println("X: " + publicKey.getXCoord());
        System.out.println("Y: " + publicKey.getYCoord() + "\n");

        ArrayList<ECPair> encryptedMessage = new ArrayList<>();

        for (ECPoint point : messagePoints
             ) {
            encryptedMessage.add(encryptor.encrypt(point));
        }

        System.out.println("The following point pairs are obtained after encryption:\n");

        OutputHandler.showMessagePointPairs(encryptedMessage);

        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------DECRYPTING--------------------------------------------------------------------");
        System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------\n");

        System.out.println("Using the private key below:");
        System.out.println(privateKey.toString(16) + "\n");

        ArrayList<ECPoint> decryptedMessage = new ArrayList<>();

        for (ECPair pair : encryptedMessage
             ) {
            decryptedMessage.add(decryptor.decrypt(pair));
        }

        System.out.println("The following points are obtained after decryption:\n");

        OutputHandler.showMessagePoints(decryptedMessage);

        String remappedMessage = mapper.getMessage(decryptedMessage);

        System.out.println("The reconstructed message is:\n");

        OutputHandler.printMessage(remappedMessage);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //the graphics

        GraphCreator graph = new GraphCreator();

        graph.setCurve(ecParams);

        graph.setPublicKey(publicKey);

        graph.setMessagePoints(messagePoints);

        graph.setEncryptedPoints(encryptedMessage);

        graph.drawGraph();

        graph.setVisible(true);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /*
        //list the available curves
        System.out.println("Available curves:\n");

        int counter = 0;
        for (Enumeration e = NISTNamedCurves.getNames(); e.hasMoreElements(); ) {
            if (counter == 3) {
                counter = -1;
                System.out.println(e.nextElement().toString());
            } else {
                System.out.print(e.nextElement().toString() + "    ");
            }
            counter++;
        }
         */

        //System.out.println(ecParams.getCurve().getClass());

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    }
}
