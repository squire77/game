package com.pd.modelcg.codegen.graphics.graphcanvas;

import com.pd.modelcg.codegen.graphics.draggablecanvas.DraggableCanvas;
import com.pd.modelcg.codegen.graphics.draggablecanvas.IDraggable;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class GraphCanvas extends DraggableCanvas {
    public GraphCanvas() {
        this.graph = new Graph();
    }
    
    //must be called after calling constructor
    @Override
    public void init() {
        super.init();
    }           
    
    @Override
    public void reset() {
        super.reset();
        this.graph = new Graph();
    }
    
    public Node getNodeAtPosition(int x, int y) {
        // select objects from the top down in the z-buffer (in case they overlap)
        if(zBuffer.size() > 0) {
            for(int i=zBuffer.size() - 1; i >= 0; i--) {
                IDraggable next = zBuffer.get(i);
                if( next instanceof Node && ((Node) next).containsIgnoreEndPoints(x, y))
                    return (Node) next;
            }
        }

        return null;
    }
    
    @Override
    protected void doDrawObjects(Graphics2D g) {
        grid.draw(g);
        graph.snapToGrid(grid);
        graph.draw(g);
    }           
    
    @Override
    protected boolean isGroupSelectable(IDraggable d) {
        //add only nodes to the group
        if (d instanceof Node) {
            return true;
        }
        
        return false;
    }
    
    @Override
    protected void highlightNodeAndLinks(IDraggable d) {   
        super.highlightNodeAndLinks(d);
        
        for (EndPoint ep: ((Node) d).endPoints) {
            //make sure both nodes are in the group
            Node node = ep.link.getOtherEndPoint(ep).node;
            
            if (groupSelectionBounds.contains(node.getPolygon().xpoints[0], node.getPolygon().ypoints[0]) &&
                groupSelectionBounds.contains(node.getPolygon().xpoints[2], node.getPolygon().ypoints[2])) {
                ep.setColor(ep.getSelectedColor());
                ep.setBorderColor(ep.getSelectedBorderColor());
                ep.link.setColor(ep.link.getSelectedColor());
                ep.link.setBorderColor(ep.link.getSelectedBorderColor());
            }
        }
    }
    
    @Override
    protected void unhighlightNodeAndLinks(IDraggable d) { 
        super.unhighlightNodeAndLinks(d);
        
        for (EndPoint ep: ((Node) d).endPoints) {
            ep.setColor(ep.getUnselectedColor());
            ep.setBorderColor(ep.getUnselectedBorderColor());
            ep.link.setColor(ep.link.getUnselectedColor());
            ep.link.setBorderColor(ep.link.getUnselectedBorderColor());
        }
    }
    
    //*** methods for reading and writing the graphcanvas ********************
      
    public void writeGraph(String graphFileNameAbsolutePath) {
        StringBuilder strBuf = new StringBuilder();        

        if (!graph.getNodes().isEmpty()) {
            for (Node n: graph.getNodes()) {                                                
                strBuf.append(n.getPolygon().xpoints[0]);
                strBuf.append(",");
                strBuf.append(n.getPolygon().ypoints[0]);            
                
                //let sub-class write data
                doWriteNodeData(n, strBuf);
                
                strBuf.append("\n");
            }
        } else {
            strBuf.append("<empty>");
        }
        
        strBuf.append(":\n");
                      
        if (!graph.getLinks().isEmpty()) {
            for (Link l: graph.getLinks()) {
                strBuf.append(getNodeIndex(l.getEndPoint1()));
                strBuf.append(",");
                strBuf.append(getNodeIndex(l.getEndPoint2()));
                strBuf.append(",");
                strBuf.append(l.getEndPoint1().getLocation());
                strBuf.append(",");
                strBuf.append(l.getEndPoint1().getPercent());    
                strBuf.append(",");
                strBuf.append(l.getEndPoint2().getLocation());
                strBuf.append(",");
                strBuf.append(l.getEndPoint2().getPercent()); 
                
                //let sub-class write data
                doWriteLinkData(l, strBuf);             
                
                strBuf.append("\n");
            }  
        } else {
            strBuf.append("<empty>");
        }
        
        try {
            writeFile(graphFileNameAbsolutePath, strBuf.toString());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Unable to write graphcanvas: " + graphFileNameAbsolutePath, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void writeFile(String fileName, String textToSave) throws IOException {
        if (fileName == null)
            throw new IOException( "filename is null");
        if (textToSave == null)
            throw new IOException("textToSave is null");

        FileWriter fw     = new FileWriter(fileName);
        BufferedWriter bw = new BufferedWriter(fw);

        fw.write( textToSave );

        bw.close();
        fw.close();
    }

    public void readGraph(String graphFileNameAbsolutePath) {
        long graphStartTime = System.currentTimeMillis();
        
        this.reset(); // clear all for new graphcanvas()

        String graphDesc;
        try {
            graphDesc = readFile(graphFileNameAbsolutePath);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Unable to open graphcanvas: " + graphFileNameAbsolutePath, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //graphData[0] : Node descriptors
        //graphData[1] : Link descriptors
        String[] graphData = graphDesc.split(":");
        if (graphData.length < 2) {
            JOptionPane.showMessageDialog(null,
                    "Invalid graphcanvas file: " + graphFileNameAbsolutePath, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        graphData[0] = graphData[0].trim();
        graphData[1] = graphData[1].trim();
        
        String[] nodeData = graphData[0].split("\n");
        if (!nodeData[0].startsWith("<")) {
            for (int i=0; i<nodeData.length; i++) {
                String[] nodeElements = nodeData[i].split(",");
                doCreateNode(nodeElements);                                
            }
        }
        
        String[] linkData = graphData[1].split("\n"); 
        if (!linkData[0].startsWith("<")) {               
            for (int i=0; i<linkData.length; i++) {
                String[] linkElements = linkData[i].split(",");
                doCreateLink(linkElements);
            }        
        }
        
        long graphTotalTime = System.currentTimeMillis() - graphStartTime;        
        System.out.println("Time to load com.pd.modelcg.codegen.graphics: " + graphTotalTime/1000 + "." + graphTotalTime%1000);
        
        long drawStartTime = System.currentTimeMillis();
        
        //refresh after snapToGrid() occurs
        drawObjects();
        
        long drawTotalTime = System.currentTimeMillis() - drawStartTime;        
        System.out.println("Draw time: " + drawTotalTime/1000 + "." + drawTotalTime%1000);
    }

    static String readFile(String fileName) throws IOException {
        if (fileName == null)
            throw new IOException( "filename is null");

        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder buffer = new StringBuilder();

        String nextLine;
        while ((nextLine = br.readLine()) != null)
            buffer.append(nextLine).append("\n");

        //Remove final newline
        if (buffer.length() > 0 && buffer.charAt(buffer.length()-1) == '\n')
            buffer.deleteCharAt( buffer.length() - 1 );

        br.close();
        fr.close();

        return buffer.toString();
    }

    protected void doWriteNodeData(Node n, StringBuilder strBuf) {
    }
    
    protected void doWriteLinkData(Link l, StringBuilder strBuf) {             
    }
    
    protected void doCreateNode(String[] nodeElements) {
    }
    
    protected void doCreateLink(String[] linkElements) {
    }      

    private int getNodeIndex(EndPoint ep) {
        for (int i=0; i<graph.getNodes().size(); i++) {
            if (ep.getNode() == graph.getNodes().get(i)) {
                return i;
            }
        }
        JOptionPane.showMessageDialog(null,
                "Unable to write graphcanvas, unknown node found", "Error", JOptionPane.ERROR_MESSAGE);
        return -1;
    }
    
    protected Graph             graph;   
}
