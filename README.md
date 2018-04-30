# Network-Analysis
This program was originally submitted as a classwork assignment focusing on mastery of graphs and graph algorithms through practical implementation. The vertices in this particular network describe switches in a computer network and the edges either copper or fiber optic cables run between the switches. 

## Program Behavior
The terminal interface provides the user the following options in examining their input graph: retrieve the lowest latency path, determine whether the graph is copper-only connected, determine the max flow of data from one switch to another, retrieve the lowest average latency spanning tree for the graph, or determine if the graph is triconnected (that it would remain connected if two arbitrary vertices were to fail). 

## How to Run
To run this program, all files must reside in the same directory. Compile on the command line with 'javac NetworkAnalysis.java' and run with 'java NetworkAnalysis filename.txt', where filename.txt is the name of a file containing a graph description.
* 'network_data1.txt' is an example of a graph description where each line represents an edge in the graph; two integers specify the edge endpoints, followed by a string that represents the type of connection, the bandwidth in megabits per second, and the length of the cable in meters. All cables are in full duplex and represent connection in either direction simultaneously.
