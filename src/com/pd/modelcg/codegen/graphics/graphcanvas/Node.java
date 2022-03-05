package com.pd.modelcg.codegen.graphics.graphcanvas;

import com.pd.modelcg.codegen.graphics.draggablecanvas.Draggable;
import com.pd.modelcg.codegen.graphics.draggablecanvas.DraggableCanvas;
import com.pd.modelcg.codegen.graphics.draggablecanvas.Grid;
import com.pd.modelcg.codegen.graphics.draggablecanvas.IDraggable;

import java.awt.*;
import java.util.ArrayList;

public class Node extends Draggable {
    public java.util.List<EndPoint> endPoints;
    
    public Node(Graph graph, Polygon polygon) {
        super(polygon);
        this.graph = graph;               
        this.endPoints = new ArrayList<EndPoint>();
    }
    
    //must call this after constructor
    public void initialize() {
        //Note: no need to call super.initialize(polygon) since we initialized
        //      the polygon in the constructor
        
        graph.addNode(this);
    }    
    
    @Override
    public void remove(DraggableCanvas canvas) {
        //remove all links
        while (!endPoints.isEmpty()) {
            endPoints.get(0).link.remove(canvas);
        }
        
        //remove from canvas after we remove from graphcanvas else we'll get redrawn
        graph.removeNode(this);
        super.remove(canvas);                
    }
            
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);      
        
        for (EndPoint e: endPoints)
            e.setVisible(visible);
    }

    @Override
    public void snapToGrid(Grid grid) {
        if (grid.snapToGrid()) {
            super.snapToGrid(grid);

            for (EndPoint e: endPoints)
                e.snapToGrid(grid);
        }
    }

    @Override
    protected void doDraw(Graphics2D g) {
        super.doDraw(g);       
        
        for (EndPoint e: endPoints)
            e.drawLink(g);
    }

    @Override
    public void translate(int deltaX, int deltaY) {
        super.translate(deltaX, deltaY);

        for (IDraggable e: endPoints)
            e.translate(deltaX, deltaY);
    }

    //we must say our shape does not contain the point if any of the endpoints contains it
    @Override
    public boolean contains(int x, int y) { 
        for (EndPoint ep: endPoints) {
            if (ep.contains(x, y)) {
                return false;
            }
        }
        
        return containsIgnoreEndPoints(x, y); 
    }
    
    //check if we contain the point regardless if an endpoint contains the point
    public boolean containsIgnoreEndPoints(int x, int y) {
        return getPolygon().contains(x, y) || 
                
                //Java's contains() does not include the edges and we do
                getPolygon().xpoints[0] == x || getPolygon().xpoints[1] == x ||
                getPolygon().ypoints[1] == y || getPolygon().ypoints[2] == y;
    }
        
    protected Graph                       graph;
}
