/** DepthDataPlotWindow.java */

package edu.washington.apl.aganse.dataTools;

import ptolemy.plot.*;
import edu.washington.apl.aganse.ptolemyUpdates.plot.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*; 

/** Displays the dataseries in a graphical plot window, with the
 *  X (independent) axis vertical for use in depth profile plots.
 *  Currently uses the PtPlot component from
 *  <A HREF="http:ptolemy.eecs.berkeley.edu">PtolemyII</A> (Berkeley), v2.0,
 *  with some modifications by me and Pete Brodsky (APL-UW) as found in
 *  package edu.washington.apl.aganse.ptolemyUpdates.
 * @author <A HREF="mailto:aganse@apl.washington.edu">Andy Ganse</A>,<BR>
 * <A HREF="http://www.apl.washington.edu">Applied Physics Laboratory</A>,<BR>
 * <A HREF="http://www.washington.edu">University of Washington</A>.
 * @version 18 Sep 2015 (initial version 25 Oct 2002)
 */
public class DepthDataPlotWindow extends DataPlotWindow {
    /** creates a separate new window frame with a plot of the data
     *  from the DataSeries in it, but the X (independent) axis is 
     *  vertical for use in depth profile plots */
    public DepthDataPlotWindow(DataSeries data) {
        super(data.yToArray(), data.xToNegArray());
    }

    /** Add a series of data to the plot, in a color of its own, with
     *  connected points */
    public void addDataSeries(DataSeries data) {
        thePlot.setNextDataColor(_colors[dataseriesnum-1]);
        thePlot.addPoints(dataseriesnum, data.yToArray(), data.xToNegArray(), true );
        dataseriesnum++;
    }

}
