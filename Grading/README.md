# CanvasExpand

CanvasExpand is a tool to expand Canvas project archives. 

To run from source, see the sample main method in ExpanderTools.

To run via Eclipse, import the project into your Eclipse workspace, then
run the CanvasExpand class. If you provide no arguments it will start the GUI.
Optionally, set up a Run Configuration that includes the path to your 
zip archive and output directory as below.

To run as a standalone application, use the jar file. It can be run as a command line program as:
```
java -jar CanvasExpand.jar input_archive output_directory
```
Or with a simple GUI by just running the jar file:
```
java -jar CanvasExpand.jar
```
