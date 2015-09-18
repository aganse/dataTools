# Makefile for dataTools - just creates javadoc files, actual source use is in
# apps MultiRegressLines, RayDemo, SpheRayDemo

doc: DataSeries.java DataPlotWindow.java DepthDataPlotWindow.java
	javadoc -d doc -author -version *.java

clean:
	\rm -rf doc
