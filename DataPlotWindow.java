/** DataplotWindow.java */

package edu.washington.apl.aganse.dataTools;
import ptolemy.plot.*;
import edu.washington.apl.aganse.ptolemyUpdates.plot.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*; 

/** Displays the dataseries in a graphical, interactive plot window.
 *  Currently uses the PtPlot component from
 *  <A HREF="http:ptolemy.eecs.berkeley.edu">PtolemyII</A> (Berkeley), v2.0,
 *  with some modifications by me and Pete Brodsky (APL-UW) as found in
 *  package edu.washington.apl.aganse.ptolemyUpdates.
 * @author <A HREF="mailto:aganse@apl.washington.edu">Andy Ganse</A>,<BR>
 * <A HREF="http://www.apl.washington.edu">Applied Physics Laboratory</A>,<BR>
 * <A HREF="http://www.washington.edu">University of Washington</A>.
 * @version 18 Sep 2015 (initial version 25 Oct 2002)
 * @see <A HREF="DepthDataPlotWindow.html">DepthDataPlotWindow</A>
*/
public class DataPlotWindow {
    XPlot thePlot = new XPlot();
    int dataseriesnum = 0;
    /** creates a separate new window frame with a plot of the data
     *  from the DataSeries in it */
    public DataPlotWindow(DataSeries data) {
        try {
            String marks = "dots";
            thePlot.addPoints(0, data.xToArray(), data.yToArray(), false );
            thePlot.setMarksStyle(marks, 0);
            dataseriesnum++;
        } catch (Exception e) {
            System.out.println("DataPlotWindow: Error Setting Plot : " + e);
            System.exit(1);
        }
        addFrame();
    }
    public DataPlotWindow(double x[], double y[]) {
        try {
            String marks = "dots";
            thePlot.addPoints(0, x, y, false );
            thePlot.setMarksStyle(marks, 0);
            dataseriesnum++;
        } catch (Exception e) {
            System.out.println("DataPlotWindow: Error Setting Plot : " + e);
            System.exit(1);
        }
        addFrame();
    }
    /** Place the plot object in a window frame */
    protected void addFrame() {
        Frame myFrame = new Frame("DataPlotWindow");
        myFrame.addWindowListener(new WL());
        myFrame.add(thePlot, BorderLayout.CENTER);
        myFrame.setSize(400,400);
        myFrame.setVisible(true);        
    }
    /** To close the DataPlotWindow from its window menu */
    protected class WL extends WindowAdapter {
        public void windowClosing (WindowEvent e) {
            System.exit(0);
        }
    }
    
    /** Set default colors, ordered by data set.
     * Copied verbatim from class PlotBox.java, as it's a protected var in that
     * class and is in a different package... */
    static protected Color[] _colors = {
        new Color(0xff0000),   // red
        new Color(0x0000ff),   // blue
        new Color(0x00aaaa),   // cyan-ish
        new Color(0x000000),   // black
        new Color(0xffa500),   // orange
        new Color(0x53868b),   // cadetblue4
        new Color(0xff7f50),   // coral
        new Color(0x45ab1f),   // dark green-ish
        new Color(0x90422d),   // sienna-ish
        new Color(0xa0a0a0),   // grey-ish
        new Color(0x14ff14),   // green-ish
    };
    
    /** Add a series of data to the plot, in a color of its own, with
     *  connected points */
    public void addDataSeries(DataSeries data) {
        thePlot.setNextDataColor(_colors[dataseriesnum-1]);
        thePlot.addPoints(dataseriesnum, data.xToArray(), data.yToArray(), true );
        dataseriesnum++;
    }

    /** Set the title to the plot */
    public void setTitle(String mytitle) {
        thePlot.setTitle(mytitle);
    }
    /** Set the x-axis label for the plot */
    public void setXlabel(String xlabel) {
        thePlot.setXLabel(xlabel);
    }
    /** Set the y-axis label for the plot */
    public void setYlabel(String ylabel) {
        thePlot.setYLabel(ylabel);
    }

    //thePlot.setButtons(true);

}
