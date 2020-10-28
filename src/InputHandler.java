import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class InputHandler {

    public static String getMessage() {
        System.out.println();
        System.out.println("Choose between direct [T]ext input or a [F]ile path:");
        Scanner terminal = new Scanner(System.in);
        String instruction = terminal.nextLine();

        if (instruction.equals("T") || instruction.equals("t")) {
            System.out.println("Enter message to be encrypted:");
            String message = terminal.nextLine();
            return message;
        } else if (instruction.equals("F") || instruction.equals("f")) {
            System.out.println("Enter file path:");
            String path = terminal.nextLine();
            try {
                return getFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid command entered");
            return getMessage();
        }
        return null;
    }

    private static String getFile(String path) throws IOException {
        String message = "";
        message = new String(Files.readAllBytes(Paths.get(path)));
        return message;
    }

    public static X9ECParameters getCurve() {
        System.out.println("Choose from the following NIST specified curves");
        System.out.println("P-192 / P-224 / P-256 / P-384");
        Scanner terminal = new Scanner(System.in);
        String instruction = terminal.nextLine();
        X9ECParameters params = null;

        switch (instruction) {
            case "P-192" :
                params = NISTNamedCurves.getByName("P-192");
                System.out.println("Using the NIST specified secp192r1 curve");
                break;
            case "P-224" :
                params = NISTNamedCurves.getByName("P-224");
                System.out.println("Using the NIST specified secp224r1 curve");
                break;
            case "P-256" :
                params = NISTNamedCurves.getByName("P-256");
                System.out.println("Using the NIST specified secp256r1 curve");
                break;
            case "P-384" :
                params = NISTNamedCurves.getByName("P-384");
                System.out.println("Using the NIST specified secp384r1 curve");
                break;
            default:
                System.out.println("Incorrect name entered");
                return getCurve();
        }
        return params;
    }

}
