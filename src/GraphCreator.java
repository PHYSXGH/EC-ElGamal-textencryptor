import java.awt.Color;
import java.math.BigInteger;
import java.text.*;
import java.util.ArrayList;
import javax.swing.JFrame;

import de.erichseifert.gral.data.DataSeries;
import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.axes.AxisRenderer;
import de.erichseifert.gral.plots.legends.AbstractLegend;
import de.erichseifert.gral.plots.legends.Legend;
import de.erichseifert.gral.plots.legends.ValueLegend;
import de.erichseifert.gral.plots.lines.DefaultLineRenderer2D;
import de.erichseifert.gral.plots.lines.LineRenderer;
import de.erichseifert.gral.plots.points.PointRenderer;
import de.erichseifert.gral.ui.InteractivePanel;
import org.bouncycastle.asn1.nist.NISTNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.ECPair;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECPoint;

import java.text.Format;

public class GraphCreator extends JFrame {

    //for the base point of the curve
    X9ECParameters curve;

    //the public key
    ECPoint publicKey;

    //the unencrypted message points
    ArrayList<ECPoint> messagePoints = new ArrayList<>();

    //the encrypted message point pairs
    ArrayList<ECPair> encryptedPoints = new ArrayList<>();

    public GraphCreator() {
    }

    public void setCurve(X9ECParameters curve) {
        this.curve = curve;
    }

    public void setPublicKey(ECPoint publicKey) {
        this.publicKey = publicKey;
    }

    public void setMessagePoints(ArrayList<ECPoint> messagePoints) {
        this.messagePoints = messagePoints;
    }

    public void setEncryptedPoints(ArrayList<ECPair> encryptedPoints) {
        this.encryptedPoints = encryptedPoints;
    }

    public void drawGraph() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);

        //create data tables
        DataTable basePoint = new DataTable(BigInteger.class, BigInteger.class);
        DataSource basePointSource = new DataSeries("Generator Point", basePoint, 0, 1);
        basePoint.add(curve.getG().getXCoord().toBigInteger(), curve.getG().getYCoord().toBigInteger());

        DataTable publicKey = new DataTable(BigInteger.class, BigInteger.class);
        DataSource publicKeySource = new DataSeries("Public Key", publicKey, 0, 1);
        publicKey.add(this.publicKey.getXCoord().toBigInteger(), this.publicKey.getYCoord().toBigInteger());

        DataTable messagePoints = new DataTable(BigInteger.class, BigInteger.class);
        DataSource messagePointsSource = new DataSeries("Message Points", messagePoints, 0, 1);
        for (ECPoint messagePoint : this.messagePoints
             ) {
            messagePoints.add(messagePoint.getXCoord().toBigInteger(), messagePoint.getYCoord().toBigInteger());
        }

        DataTable encryptedPoints = new DataTable(BigInteger.class, BigInteger.class);
        DataSource encryptedPointsSource = new DataSeries("Encrypted Points", encryptedPoints, 0, 1);
        for (ECPair encryptedPair : this.encryptedPoints
             ) {
            encryptedPoints.add(encryptedPair.getX().getXCoord().toBigInteger(), encryptedPair.getX().getYCoord().toBigInteger());
            encryptedPoints.add(encryptedPair.getY().getXCoord().toBigInteger(), encryptedPair.getY().getYCoord().toBigInteger());
        }

        //create plot for the data
        XYPlot plot = new XYPlot(basePointSource, publicKeySource, messagePointsSource, encryptedPointsSource);
        getContentPane().add(new InteractivePanel(plot));

        //set colours
        plot.getPointRenderers(basePointSource).get(0).setColor(new Color(23,23,42));

        plot.getPointRenderers(publicKeySource).get(0).setColor(new Color(80,101,211));

        plot.getPointRenderers(messagePointsSource).get(0).setColor(new Color(53,170,24));

        plot.getPointRenderers(encryptedPointsSource).get(0).setColor(new Color(225,91,55));

        //legends?
        plot.setLegendVisible(true);

        AbstractLegend legend = (AbstractLegend) plot.getLegend();

        //tweak the axis
        AxisRenderer rendererY = plot.getAxisRenderer(XYPlot.AXIS_Y);
        Format format = new DecimalFormat("0.#####E0");
        rendererY.setTickLabelFormat(format);
        AxisRenderer rendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
        rendererX.setTickLabelFormat(format);
    }

    public static void main(String[] args) {
        GraphCreator frame = new GraphCreator();
        frame.setVisible(true);
    }
}