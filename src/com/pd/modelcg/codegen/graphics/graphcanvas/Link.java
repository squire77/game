package com.pd.modelcg.codegen.graphics.graphcanvas;

import com.pd.modelcg.codegen.graphics.draggablecanvas.Draggable;
import com.pd.modelcg.codegen.graphics.draggablecanvas.DraggableCanvas;
import com.pd.modelcg.codegen.graphics.draggablecanvas.Grid;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Link extends Draggable {
    public enum LineStyle { SOLID, DASHED }   
    
    public Link(Graph graph, EndPoint ep1, EndPoint ep2) {        
        this.lineStyle = LineStyle.SOLID;
        this.graph = graph;
        this.ep1 = ep1;
        this.ep2 = ep2;
        this.node1 = ep1.getNode();
        this.node2 = ep2.getNode();
        
        node1.endPoints.add(ep1);
        node2.endPoints.add(ep2); 
        
        //use default border color as color for the link
        setSelectedColor(DEFAULT_SELECTED_BORDER_COLOR);
        setUnselectedColor(DEFAULT_UNSELECTED_BORDER_COLOR);
        
        //start off with the right color since we changed it
        unselect();
    }
    
    //must call this after constructor
    public void initialize() {
        super.initialize(getPolygon());
        
        ep1.setLink(this);
        ep2.setLink(this);
        graph.addLink(this);        
    }
    
    @Override
    public void select() { 
        super.select();

        ep1.setColor(getSelectedColor()); 
        ep1.setBorderColor(getSelectedBorderColor());       
        ep2.setColor(getSelectedColor()); 
        ep2.setBorderColor(getSelectedBorderColor()); 
    }
    
    @Override
    public void unselect() { 
        super.unselect();
        
        ep1.setColor(getUnselectedColor()); 
        ep1.setBorderColor(getUnselectedBorderColor());
        ep2.setColor(getUnselectedColor()); 
        ep2.setBorderColor(getUnselectedBorderColor());
    }
    
    public EndPoint getEndPoint1() {
        return ep1;
    }
    
    public EndPoint getEndPoint2() {
        return ep2;
    }
    
    public EndPoint getOtherEndPoint(EndPoint ep) {
        return (ep == ep1) ? ep2 : ep1;
    }    
        
    public void setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }
    
    @Override
    public void remove(DraggableCanvas canvas) {
        ep1.removeEndPoint(canvas);
        ep2.removeEndPoint(canvas);
        
        node1.endPoints.remove(ep1);
        node2.endPoints.remove(ep2);
        
        //remove from canvas after we remove from graphcanvas else we'll get redrawn
        graph.removeLink(this);
        super.remove(canvas);
    }
        
    //can't move links around, just the end points
    @Override public boolean validate(int deltaX, int deltaY, int width, int height) { return true; }

    //we must say our shape does not contain the point if either of the endpoint
    //shapes contains the point
    @Override
    public boolean contains(int x, int y) { 
        if (ep1.contains(x, y) || ep2.contains(x, y)) {
            return false;
        }
        
        return getPolygon().contains(x, y); 
    }
    
    @Override public void mouseDragged(MouseEvent event, int deltaX, int deltaY) {}

    @Override
    public void snapToGrid(Grid grid) {
        ep1.snapToGrid(grid);
        ep2.snapToGrid(grid);
    }

    @Override
    protected void doDraw(Graphics2D g) {
        g.setPaint(getColor()); // since we're overriding, we must set the color too!
        
        switch (lineStyle) {
            case DASHED:
                g.setStroke(DASHED_STROKE);
                break;
            default:
                g.setStroke(SOLID_STROKE);
        }
                    
        //check for connection back to self
        if (ep1.getLocation() == ep2.getLocation()) {
            switch (ep1.getLocation()) {
                case EndPoint.TOP: 
                    g.drawLine(ep1.getPosition().x, ep1.getPosition().y,
                                ep1.getPosition().x, ep2.getPosition().y-30);
                    g.drawLine(ep2.getPosition().x, ep1.getPosition().y,
                                ep2.getPosition().x, ep2.getPosition().y-30);
                    g.drawLine(ep1.getPosition().x, ep1.getPosition().y-30,
                                ep2.getPosition().x, ep2.getPosition().y-30);                    
                    break;
                case EndPoint.BOTTOM:
                    g.drawLine(ep1.getPosition().x, ep1.getPosition().y,
                                ep1.getPosition().x, ep2.getPosition().y+30);
                    g.drawLine(ep2.getPosition().x, ep1.getPosition().y,
                                ep2.getPosition().x, ep2.getPosition().y+30);
                    g.drawLine(ep1.getPosition().x, ep1.getPosition().y+30,
                                ep2.getPosition().x, ep2.getPosition().y+30); 
                    break;
                case EndPoint.RIGHT:
                    g.drawLine(ep1.getPosition().x, ep1.getPosition().y,
                                ep2.getPosition().x+30, ep1.getPosition().y);
                    g.drawLine(ep1.getPosition().x, ep2.getPosition().y,
                                ep2.getPosition().x+30, ep2.getPosition().y);
                    g.drawLine(ep1.getPosition().x+30, ep1.getPosition().y,
                                ep2.getPosition().x+30, ep2.getPosition().y);
                    break; 
                default: //EndPoint.LEFT
                    g.drawLine(ep1.getPosition().x, ep1.getPosition().y,
                                ep2.getPosition().x-30, ep1.getPosition().y);
                    g.drawLine(ep1.getPosition().x, ep2.getPosition().y,
                                ep2.getPosition().x-30, ep2.getPosition().y);
                    g.drawLine(ep1.getPosition().x-30, ep1.getPosition().y,
                                ep2.getPosition().x-30, ep2.getPosition().y);
            }
        } else {
            g.drawLine(ep1.getPosition().x, ep1.getPosition().y,
                       ep2.getPosition().x, ep2.getPosition().y);
        }
        
        g.setStroke(SOLID_STROKE);
        
        //draw endpoint second to overwrite line
        ep1.drawEndPoint(g);
        ep2.drawEndPoint(g);
    }
    
    @Override
    public Polygon getPolygon() {
        Point pos1 = ep1.getPosition();
        Point pos2 = ep2.getPosition();
        Polygon rect = new Polygon();
        if (Math.abs(pos1.y-pos2.y) > Math.abs(pos1.x-pos2.x)) {//mostly vertical
            rect.addPoint(pos1.x-2, pos1.y);
            rect.addPoint(pos1.x+2, pos1.y);
            rect.addPoint(pos2.x+2, pos2.y);
            rect.addPoint(pos2.x-2, pos2.y);
        } else {//mostly horizontal
            rect.addPoint(pos1.x, pos1.y-2);
            rect.addPoint(pos1.x, pos1.y+2);
            rect.addPoint(pos2.x, pos2.y+2);
            rect.addPoint(pos2.x, pos2.y-2);
        }
        return rect;
    }    
    
    final static BasicStroke SOLID_STROKE = new BasicStroke(1);
    final static float DASH[] = {10.0f};
    final static BasicStroke DASHED_STROKE = new BasicStroke(1.0f,
                                                      BasicStroke.CAP_BUTT,
                                                      BasicStroke.JOIN_MITER,
                                                      10.0f, DASH, 0.0f);
    
    private LineStyle  lineStyle;
    
    protected Graph    graph;    
    protected EndPoint ep1;
    protected EndPoint ep2;
    protected Node     node1;
    protected Node     node2;
}
