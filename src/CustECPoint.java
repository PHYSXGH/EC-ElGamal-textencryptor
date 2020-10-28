//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//



import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.*;

import java.math.BigInteger;
import java.util.Hashtable;

public class CustECPoint extends ECPoint {
    protected static final ECFieldElement[] EMPTY_ZS = new ECFieldElement[0];
    protected ECCurve curve;
    protected ECFieldElement x;
    protected ECFieldElement y;
    protected ECFieldElement[] zs;
    protected Hashtable preCompTable;

    public CustECPoint(ECCurve ecCurve, ECFieldElement ecFieldElement, ECFieldElement ecFieldElement1) {
        super(ecCurve, ecFieldElement, ecFieldElement1);
    }

    @Override
    protected boolean satisfiesCurveEquation() {
        return false;
    }

    @Override
    protected ECPoint detach() {
        return null;
    }

    @Override
    protected boolean getCompressionYTilde() {
        return false;
    }

    @Override
    public ECPoint add(ECPoint ecPoint) {
        return null;
    }

    @Override
    public ECPoint negate() {
        return null;
    }

    @Override
    public ECPoint subtract(ECPoint ecPoint) {
        return null;
    }

    @Override
    public ECPoint twice() {
        return null;
    }
}
