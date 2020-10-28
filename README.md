# EC-ElGamal-textencryptor
 A demo to encrypt text using EC-ElGamal
 
 > Written in Java using the Bouncy Castle cryptography library to encrypt a typed text or text file (sample/text.txt can be used) to points on a NIST-specified 192, 224, 256 or 384-bit elliptic curve. Originally produced as a coursework submission, but slightly improved since. It uses a command line interface but graphically shows the points in the Galois-field once the process is complete.
 
 ### How to use:
 - Select the curve
 - Choose between text or file input
 - Enter text ot file path
 - Enjoy
 
 ### You get the following output:
 - Curve parameters, according to the selected curve.
 - A randomly generated privater and public key pair.
 - The text converted to a byte-array, shown in a hexadecimal format.
 - This byte-array sliced up to as many bits as necessary and padded to fit inside the coordinates of one or more points. This step also includes the mapping to ECPoints using the technique linked below.
 - The ECPairs gathered after encrypting these points.
 - The ECPoints after decryption.
 - The reconstructed byte-arrays from these points. (again using the mapping detailed in the paper below)
 - The fully concatenated byte-array containing the entire text in hexadecimal format.
 - The text itself.
 
### Notes
 
 As Bouncy Castle does not have a way (either that, or I could not find it) to map text into ECPoints, I have implemented a way of mapping text into the X-coordinate of ECPoints using the proposed message mapping scheme in [this article](https://onlinelibrary.wiley.com/doi/pdf/10.1002/sec.1702) on pages 5368-5369.
 
 It also does not allow the creation of ECPoints with custom coordinates, I have created a custom subclass which lets me do just that, although the points created witht his class do not evaluate as valid ECPoints, but that does not seem to cause any issues.
 
 
