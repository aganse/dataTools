/** DataSeries.java */

package edu.washington.apl.aganse.dataTools;

import java.util.*;
import java.text.DecimalFormat;
import java.io.*;

/**
 * DataSeries - a Vector class to hold 2D dataseries points and provide
 * statistical information about them like mean, stdev, regression lines, etc.
 * Dataseries are easily displayed in interactive plot via DataPlotWindow class.<BR>
 * Note the Point subclass which holds the x,y values; the DataSeries is made
 * up of these Points, and the Point class may also be used publically to cast
 * a separate single-point variable, such a temp-var used in comparisions.<BR>
 * Per convention, x is the independent variable, and is the variable used for
 * sorting.<BR>
 * @author <A HREF="mailto:aganse@apl.washington.edu">Andy Ganse</A>,<BR>
 * <A HREF="http://www.apl.washington.edu">Applied Physics Laboratory</A>,<BR>
 * <A HREF="http://www.washington.edu">University of Washington</A>.
 * @version 18 Sep 2015 (initial version 24 Jan 2000)
 * @see <A HREF="DataPlotWindow.html">DataPlotWindow</A>
 * @see <A HREF="DepthDataPlotWindow.html">DepthDataPlotWindow</A>
 * @see <A HREF="SingleRegressionLine.html">SingleRegressionLine</A>
 * @see <A HREF="DoubleRegressionLine.html">DoubleRegressionLine</A>
 * @see <A HREF="TripleRegressionLine.html">TripleRegressionLine</A>
 */
public class DataSeries extends Vector {

    private int numPts=0;
    private double min_x, min_y, max_x, max_y;
    private double sum_x=0;
    private double sum_y=0;
    private double sum_xx=0;
    private double sum_yy=0;
    private double sum_xy=0;
    private double[] dYdX;

    /** Create an empty DataSeries */
    public DataSeries() {}

    /** Create a DataSeries initially filled with the given point, as
     *  <CODE>new DataSeries(x0,y0)</CODE>.  Yes this is a hack(!) */
    public DataSeries(double a, double b) {
	add(a,b);
    }

    /** Create a DataSeries initially filled with the given couple of points, as
     *  <CODE>new DataSeries(x0,y0,x1,y1)</CODE>.  Yes this is a hack(!) */
    public DataSeries(double a, double b, double c, double d) {
	add(a,b); add(c,d);
    }

    /**
     * Point objects which make up the DataSeries in a Vector - note the Point
     * class may be used publically to cast a separate single Point object,
     * such a temp object used in comparisions.
     * (Currently the Points are only 2D, but they will hopefully expand in the
     * future to more dimensions, with optional nulls for dependent variables
     * as long as one exists).
     * @see <A HREF="http://java.sun.com/docs/books/tutorial/collections/interfaces/order.html">Sun Java Tutorial/Object Ordering</A> about the sorting.
    */
    public static class Point implements Comparable {
   	private Double x, y;
	/** Both X and Y values are required in all datapoints.
	 *  (In a future version of DataSeries with more Point dimensions,
	 *  not all dependent variables will be required to have data in them).
	 */
    	public Point(double x, double y) {
    	    this.x = new Double(x);
    	    this.y = new Double(y);
    	}
	/** Returns the X value for this Point. */
	public double getX() {
	    return this.x.doubleValue();
	}
	/** Returns the Y value for this Point */
	public double getY() {
	    return this.y.doubleValue();
	}
	/** Returns a String representation of datapoint like "12.0, 1.45" */
	public String toString() {
	    return x.doubleValue()+"  "+y.doubleValue();
	}
	/** Used to implement sorting - note compares only x axis (indep var)*/
	public boolean equals(Object o) {
	    if (!(o instanceof Point))
		return false;
	    Point p = (Point)o;
	    return p.x.equals(x);
	}
	/** Used to implement sorting - note compares only x axis (indep var)*/
	public int hashCode() {
	    return x.hashCode();
	}
	/** Used to implement sorting - note compares only x axis (indep var)*/

	public int compareTo(Object o) {
	    Point p = (Point)o;
	    return x.compareTo(p.x);
	}
    }

    /** Adds a preconfigured DataSeries.Point object to DataSeries */
    public void add(Point p) {
	add(p.getX(), p.getY());
    }

    /** Adds a datapoint to dataseries from integer values */
    public void add(int x, int y) {
	add((double)x, (double)y);
    }

    /** Adds a datapoint to dataseries from double values */
    public void add(double x, double y) {
   	addElement(new Point(x,y));
    	numPts++;
	if (numPts==1) {
	    min_x = max_x = x;
	    min_y = max_y = y;
	}
	else {
	    if (x<min_x) min_x=x;
	    else if (x>max_x) max_x=x;
	    if (y<min_y) min_y=y;
	    else if (y>max_y) max_y=y;
	}
	sum_x+=x;
	sum_y+=y;
	sum_xx+=x*x;
	sum_yy+=y*y;
	sum_xy+=x*y;
    }


	/** load dataseries points from 2-column ascii data file */
	public void loadFromFile() {
		loadFromFile("default.dat");
	}


	/** load dataseries points from 2-column ascii data file */
	public void loadFromFile(String filename) {
		StringTokenizer tokenizer;
		double x,y;
		String dataFileLine;
		BufferedReader inFile = null;
		
		// Open the file for reading
		try{
			inFile = new BufferedReader( new FileReader(filename) );
		}
		catch (IOException e) {
			// Open Failed
			System.out.println( "Error opening data file " + filename + "\n" + e.toString() );
		    System.exit(1);
		}

		clear();
		try {
		    while( (dataFileLine=inFile.readLine()) != null &&
				   dataFileLine.charAt(0) != '>' ) {
				tokenizer = new StringTokenizer( dataFileLine );
				x = Double.parseDouble( tokenizer.nextToken() );
				y = Double.parseDouble( tokenizer.nextToken() );
				add(x,y);
		    }
		} catch(FileNotFoundException err) {
		    System.out.println(
							   "DataSeries.loadFromFile: can't find file profiles.dat");
		    System.exit(1);
		} catch(IOException err) {
		    System.out.println(
							   "DataSeries.loadFromFile: i/o trouble with file profiles.dat");
		    System.exit(1);
		}
	}
	

    /** Returns x-value (as double) at specified index */
    public double getX(int index) {
	Point tmp = (Point)get(index);
	return tmp.getX();
    }

    /** Returns y-value (as double) at specified index */
    public double getY(int index) {
	Point tmp = (Point)get(index);
	return tmp.getY();
    }

    /** Returns first x-value (as double) in dataseries */
    public double getFirstX() {
	Point tmp = (Point)firstElement();
	return tmp.getX();
    }

    /** Returns first y-value (as double) in dataseries */
    public double getFirstY() {
	Point tmp = (Point)firstElement();
	return tmp.getY();
    }

    /** Returns last x-value (as double) in dataseries */
    public double getLastX() {
	Point tmp = (Point)lastElement();
	return tmp.getX();
    }

    /** Returns last y-value (as double) in dataseries */
    public double getLastY() {
	Point tmp = (Point)lastElement();
	return tmp.getY();
    }

    /** Returns mean x-value of datapoints */
    public double getXmean() {
	return sum_x / numPts;
    }

    /** Returns mean y-value of datapoints */
    public double getYmean() {
	return sum_y / numPts;
    }

    /** Returns number of datapoints in dataseries */
    public int getNumPts() {
	return size();
    }

    /** Returns an array of all the X values of the points in the dataseries */
    public double[] xToArray() {
	double[] xTmp = new double[size()];
	int i=0;
	Point p;
	for(Enumeration e=this.elements(); e.hasMoreElements(); ) {
	    p=(Point)e.nextElement();
	    xTmp[i++]=p.getX();
	}
	return xTmp;
    }

    /** Returns a Vector of all the X values of the points in the dataseries */
    public Vector xToVect() {
	Vector xTmp = new Vector();
	Point p;
	for(Enumeration e=this.elements(); e.hasMoreElements(); ) {
	    p=(Point)e.nextElement();
	    xTmp.add(new Double(p.getX()));
	}
	return xTmp;
    }

    /** Returns an array of all the X values times -1 (useful when x=depth) */
    public double[] xToNegArray() {
	double[] xTmp = new double[size()];
	int i=0;
	Point p;
	for(Enumeration e=this.elements(); e.hasMoreElements(); ) {
	    p=(Point)e.nextElement();
	    xTmp[i++]= - p.getX();
	}
	return xTmp;
    }

    /** Returns an array of all the Y values of the points in the dataseries */
    public double[] yToArray() {
	double[] yTmp = new double[size()];
	int i=0;
	Point p;
	for(Enumeration e=this.elements(); e.hasMoreElements(); ) {
	    p=(Point)e.nextElement();
	    yTmp[i++]=p.getY();
	}
	return yTmp;
    }

    /** Returns a Vector of all the Y values of the points in the dataseries */
    public Vector yToVect() {
	Vector yTmp = new Vector();
	Point p;
	for(Enumeration e=this.elements(); e.hasMoreElements(); ) {
	    p=(Point)e.nextElement();
	    yTmp.add(new Double(p.getY()));
	}
	return yTmp;
    }

    /** Returns a DataSeries object with the subset of the data specified by
        minInd and maxInd, both inclusive.  Note maxInd must be < numPts. */
    public DataSeries subSeries(int minInd, int maxInd) {
	if(minInd<0 || maxInd>numPts-1)
	    System.out.println("DataSeries:subSeries: bad range, should add " +
			       "an exception here...");
	DataSeries dataSubSet = new DataSeries();
	Point p;
	int i=0;
	for(Enumeration e=this.elements(); e.hasMoreElements(); ) {
	    p=(DataSeries.Point)e.nextElement();
	    if(i>=minInd && i<=maxInd) dataSubSet.add(p);
	    i++;
	}
	return dataSubSet;
    }

    /** Returns an array of varying size which specifies the stdDev of the
     *  residuals and some other parameters of the best linear regression line
     *  (single, dual, or triple) fitted to the data, based on a default
     *  tolerance value for the standard deviation of the residuals
     *  <I>(current default stdDevTol=10.0)</I>.
     *  See <CODE>bestRegressionLine(double stdDevTol)</CODE> for details. */
    public double[] bestRegressionLine() {
	return bestRegressionLine(10.0,10.0,10.0);
    }

    /** Returns an array of varying size which specifies the stdDev of the
     *  residuals and some other parameters of the best linear regression line
     *  (single, dual, or triple) fitted to the data, based on the specified
     *  tolerance value (stdDevTol) for the standard deviation of the
     *  residuals.  Returned array contains:<BR>
     *  <CODE><SMALL>
     *  a[0] = stdDev of the residuals of the fitted line (always in array)<BR>
     *  a[1] = x1 of 1st fitted line (if any line fitted at all)<BR>
     *  a[2] = y1 of 1st fitted line (if any line fitted at all)<BR>
     *  a[3] = x2 of 1st fitted line (if any line fitted at all)<BR>
     *  a[4] = y2 of 1st fitted line (if any line fitted at all)<BR>
     *  a[5] = x3 of second fitted line (if at least two lines fitted)<BR>
     *  a[6] = y3 of second fitted line (if at least two lines fitted)<BR>
     *  a[7] = x4 of third fitted line (if three lines fitted)<BR>
     *  a[8] = y4 of third fitted line (if three lines fitted)<BR>
     *  </SMALL></CODE><BR>
     *  Recommended usage:<BR>
     *  for <CODE>double[] foo = data.bestRegressionLine(10.0)</CODE>, check
     *  <CODE>foo.length()</CODE> to know which array elements to use. */
    public double[] bestRegressionLine(double stdDevTol1, double stdDevTol2,
				       double stdDevTol3) {
	double line[];
	if( numPts>1 ) {
	    SingleRegressionLine singleRegLine= new SingleRegressionLine(this);
	    if (singleRegLine.getSigma() <= stdDevTol1) {
			line = new double[5];  // = sigmaResids, x1, y1, x2, y2
			line[0]=singleRegLine.getSigma();
			line[1]=min_x;
			line[2]=singleRegLine.getSlope()*min_x+singleRegLine.getYint();
			line[3]=max_x;
			line[4]=singleRegLine.getSlope()*max_x+singleRegLine.getYint();
			return line;
		} else if( numPts>3 ) {
	        DoubleRegressionLine dualRegLine =
				new DoubleRegressionLine(this);
	        if (dualRegLine.getAvgSigma() <= stdDevTol2) {
	            line = new double[7];
	            line[0]=dualRegLine.getAvgSigma();
	            line[1]=min_x;
				line[2]=dualRegLine.getSlope1()*min_x+
					dualRegLine.getYint1();
				line[3]=dualRegLine.getX1();
				line[4]=dualRegLine.getSlope1()*dualRegLine.getX1() +
					dualRegLine.getYint1();
	            line[5]=max_x;
				line[6]=dualRegLine.getSlope2()*max_x+
					dualRegLine.getYint2();
				return line;
	        } else { // bad fit for dualRegLine and enough pts for triRegLine
				TripleRegressionLine triRegLine =
					new TripleRegressionLine(this);
				if (triRegLine.getAvgSigma() <= stdDevTol3) {
					line = new double[9];
					line[0]=triRegLine.getAvgSigma();
					line[1]=min_x;
					line[2]=triRegLine.getSlope1()*min_x+
						triRegLine.getYint1();
					line[3]=triRegLine.getX1();
					line[4]=triRegLine.getSlope1()*triRegLine.getX1() +
						triRegLine.getYint1();
					line[5]=triRegLine.getX2();
					line[6]=triRegLine.getSlope2()*triRegLine.getX2() +
						triRegLine.getYint2();
					line[7]=max_x;
					line[8]=triRegLine.getSlope3()*max_x+
						triRegLine.getYint3();
					return line;
				} else {  // too bad a fit even for tripleRegLine
					line = new double[1];
					line[0] = triRegLine.getAvgSigma();
					return line;
				}
			}
		}
	    else {  // <7 points, ie only enough pts to fit a singleRegLine,
			// but too bad a for it; referring object will detect this
			// by noticing there's only one element in regLine[].
			line = new double[1];
			line[0] = singleRegLine.getSigma();
			return line;
	    }
	}
	line = new double[0];
	System.out.println("error: only one point - no regLine");
	return line;  // if got here it's a bad value - something went wrong,
	              // (hence the empty array 'line'), sometime should add
	              // an exception thrower here 
    }
	
    /** Returns corrected sum of squares of x values */
    public double getSxx() {
	return sum_xx-sum_x*sum_x/numPts;
    }

    /** Returns corrected sum of squares of y values */
    public double getSyy() {
	return sum_yy-sum_y*sum_y/numPts;
    }

    /** Returns... (whattya call this thing?) */
    public double getSxy() {
	return sum_xy-sum_x*sum_y/numPts;
    }

    /** Returns standard deviation (sigma) of y values
     *  (requires 2 or more pts) */
    public double getSigmaY() {
	return (sum_yy-sum_y*sum_y/numPts)/(numPts-1);
    }

    /** Returns minimum x value */
    public double getMinX() {
	return min_x;
    }

    /** Returns minimum y value */
    public double getMinY() {
	return min_y;
    }

    /** Returns maximum x value */
    public double getMaxX() {
	return max_x;
    }

    /** Returns maximum y value */
    public double getMaxY() {
	return max_y;
    }

    /** Returns sum of x values */
    public double getSumX() {
	return sum_x;
    }

    /** Returns sum of y values */
    public double getSumY() {
	return sum_y;
    }

    /** Returns the centroid (center of mass) in the x-axis */
    public double getCentroid() {
	return sum_xy/sum_y;
    }

    /** Returns the peak width at the centroid (center of mass) - I need to
     *  verify the units of the result here, see source... */
    public double getCentroidPeakWidth() {
	Point p;
	double sum_tmp=0.;
	for(Enumeration e=this.elements(); e.hasMoreElements(); ) {
	    p=(Point)e.nextElement();
	    sum_tmp += p.getY()*(p.getX()-sum_xy/sum_x);
	}
	return sum_tmp/sum_x;
    }

    /** Rearranges the datapoints into sorted order based on x value */
    public void sort() {
	Collections.sort(this);
    }

    /** Multiplies -1 by each X value in the dataseries.  Useful when dealing with
     *  depths on that axis */
    public DataSeries negateX() {
	DataSeries output = new DataSeries();
	Point p;
	for(Enumeration e=this.elements(); e.hasMoreElements(); ) {
	    p=(DataSeries.Point)e.nextElement();
	    output.add(-1*p.getX(),p.getY());
	}
	output.sort();
	return output;
    }

    /** Interpolates the current dataseries into a new dataseries spaced at
     *  the given increment - note the data has to get sorted in the process.
     *  Based on getSoundSpeed(z) from P. Brodsky's SoundSpeedProfile class.*/
    public DataSeries resample(double incr) {
	sort();  // must sort for interpolation scheme to work (lowest first)
	DataSeries output = new DataSeries();
	int numpts = getNumPts();
	double newx;
	double tmpx[] = xToArray();
	double tmpy[] = yToArray();

	/** Compute linear gradients.  There are (numpts-1) gradients.
	 *   The ith gradient is valid from x[i] to x[i+1]. */
 	double dYdX[] = new double[numpts-1];
	for( int i=0; i<numpts-1; i++ ) {
	    dYdX[i] = ( tmpy[i+1] - tmpy[i] ) / ( tmpx[i+1] - tmpx[i] );
	}

	for( int i=0; i<(int)((tmpx[numpts-1]-tmpx[0])/incr)+1; i++ ) {
	    newx = tmpx[0] + (i*incr);  // new x-value
	    // Find appropriate (bracketing) X region
	    for( int j=0; j<numpts-1; j++ ) {
		if( tmpx[j]<=newx && newx<=tmpx[j+1] ) {
		    // Interpolate
		    output.add( newx, tmpy[j] + (newx-tmpx[j])*dYdX[j] );
		}
	    }
	}
	return output;
    }

    /** Compute linear gradients.  There are (numpts-1) gradients.
     *  This MUST be run before interpolateY(x) can be called (they're
     *  separated so interpolateY(x) can be called multiple times efficiently).
     *  The ith gradient is valid from x[i] to x[i+1]. */
    public void computeLinearGradients() {
	sort();  // must sort for gradient computation to work (lowest first)
	int numpts = getNumPts();
	double x;
	double tmpx[] = xToArray();
	double tmpy[] = yToArray();
 	dYdX = new double[numpts-1];
	for( int i=0; i<numpts-1; i++ ) {
	    dYdX[i] = ( tmpy[i+1] - tmpy[i] ) / ( tmpx[i+1] - tmpx[i] );
	}
    }

    /** Interpolates a y-value from the dataseries given x-value.
     *  WARNING: Requires computeLinearGradient() to be called first!  (This
     *  is so that interpolateY(x) can be called multiple times efficiently.)
     *  Based on getSoundSpeed(z) from P. Brodsky's SoundSpeedProfile class.*/
    public double interpolateY(double x) {
	sort();  // must sort for interpolation scheme to work (lowest first)
	int numpts = getNumPts();
	double newY=9999.0;
	double tmpx[] = xToArray();
	double tmpy[] = yToArray();

	// Find appropriate (bracketing) X region
	for( int j=0; j<numpts-1; j++ ) {
	    if( tmpx[j]<=x && x<=tmpx[j+1] ) {
		// Interpolate
		newY = tmpy[j] + (x-tmpx[j])*dYdX[j];
	    }
	}
	return newY;
    }

    /** Clears the DataSeries object of all Points and resets all statistical
     *  calculation parameters */
    public void clear() {
	super.clear();
	numPts=0;
	sum_x=0;
    	sum_y=0;
    	sum_xx=0;
    	sum_yy=0;
	sum_xy=0;
    }

    /** Returns a String representation of dataseries, as a column of its
     *  x,y values.  Beware, this could be a long column. */
    public String toString() {
	Point p;
	String tmp = new String();
	//tmp=tmp+"data("+this.numPts+"pts)\n------------\n";
	// print column of x,y values :
	for(Enumeration e=this.elements(); e.hasMoreElements(); ) {
	    p=(Point)e.nextElement();
	    tmp=tmp+p+"\n";
	}
	return tmp;
    }

    /** Adds a predefined set of datapoints to the dataseries.
     *  The current example data is a somewhat textbook-like soundspeed
     *  profile of 26 points, in meters (depth) on the x axis, and meters per
     *  second (soundspeed) on the y axis. */
    public void addExampleData() {
        add(0,1521.056);
        add(10,1521.216);
        add(20,1521.392);
        add(30,1521.544);
        add(50,1521.778);
        add(75,1522.053);
        add(100,1522.365);
        add(125,1522.724);
        add(150,1523.088);
        add(175,1523.452);
        add(200,1520.988);
        add(250,1518.591);
        add(300,1516.331);
        add(400,1510.893);
        add(500,1504.725);
        add(600,1490.624);
        add(700,1484.948);
        add(800,1481.632);
        add(900,1480.082);
        add(1000,1480.073);
        add(1100,1480.659);
        add(1200,1481.702);
        add(1300,1482.554);
        add(1400,1483.432);
        add(1500,1484.551);
        add(1750,1487.682);
        add(2000,1491.028);
    }

}
