package com.pd.modelcg.codegen.graphics.graphcanvas;

import com.pd.modelcg.codegen.graphics.draggablecanvas.Draggable;
import com.pd.modelcg.codegen.graphics.draggablecanvas.DraggableCanvas;
import com.pd.modelcg.codegen.graphics.draggablecanvas.Grid;
import com.pd.modelcg.codegen.graphics.util.LinesIntersect;

import java.awt.*;
import java.awt.event.MouseEvent;

abstract public class EndPoint extends Draggable {
    public static final int TOP=0, BOTTOM=1, RIGHT=2, LEFT=3;
    
    public EndPoint(Node node, GraphCanvas canvas) {
        this.node = node;
        this.canvas = canvas;
        this.hide = true;
    }

    //must call this after constructor
    public void initialize() {
        super.initialize(getPolygon());
    }
       
    @Override
    public void mouseClicked(MouseEvent event) {
        if (event.getClickCount() == 2) {//&& !event.isConsumed()) {
            link.mouseClicked(event);
            //event.consume();
        }
    }  
    
    @Override
    public void select() { 
        super.select();

        if (link != null) {
            link.setColor(getSelectedColor()); 
            link.setBorderColor(getSelectedBorderColor());
        }
    }
    
    @Override
    public void unselect() { 
        super.unselect();
        
        if (link != null) {
            link.setColor(getUnselectedColor()); 
            link.setBorderColor(getUnselectedBorderColor());
        }
    }
    
    //don't call this when loading from a file
    public void setDefaultLocation() {
        //default location is halfway on top line
        setLocationByPercent(TOP, 0.5);        
    }
            
    public GraphCanvas getCanvas() {
        return this.canvas;
    }
    
    public Node getNode() {
        return node;
    }
    
    public void setLink(Link link) {
        this.link = link;        
    }
    
    public void drawLink(Graphics2D g) {
        if (isVisible()) {
            link.draw(g);
        }
    } 

    //prevent an infinite loop when the link draw us
    public void drawEndPoint(Graphics2D g) { 
        if (!hide) {
            super.doDraw(g); 
        }
    } 
    
    //prevent canvas popup from removing endpoints without removing the link
    public void removeEndPoint(DraggableCanvas canvas) {
        super.remove(canvas);
    }
    
    @Override
    public void remove(DraggableCanvas canvas) { 
        link.remove(canvas);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        link.setVisible(visible);
    }

    //draw the whole link when we're moved around
    @Override
    protected void doDraw(Graphics2D g) {
        drawLink(g);
    }    
    
    //----------------------------------------------------------------
    
    public int getLocation() {
        return location;
    }
    
    public double getPercent() {
        return percent;
    }
    
    public void setLocationByPercent(int location, double percent) {
        this.location = location;
        assert (percent >= 0.0 && percent <= 1.0) : "setLocationByPercent(): invalid percent " + percent;
        this.percent = percent;
        setPosition(calculatePosition());
    }

    //this is called when a node is resized to make sure all the endpoints are
    //still on the node
    public void resetPosition() {
        setLocationByPercent(location, percent);
    }
        
    @Override
    public void mouseReleased(MouseEvent event) {
        //use getPosition() not event.getX/getY to make sure the center of the 
        //endpoint is checked not the position of the mouse
        if (node.containsIgnoreEndPoints(getPosition().x, getPosition().y)) {
            //change position on same node
            setLocationByXY(getPosition().x, getPosition().y);
        } else {
            //find new node to move to
            Node n = canvas.getNodeAtPosition(getPosition().x, getPosition().y);
            Node other = link.getOtherEndPoint(this).getNode();

            if (n  != null && relocateToNode(node, n, other)) {
                setLocationByXY(getPosition().x, getPosition().y);
            } else {
                //stay at original node
                Point pos = calculatePosition();
                setPosition(pos);
            }
        }
        
        canvas.drawObjects();
    }
    
    //only call this if this endpoint is connected to another endpoint by a link
    private void setLocationByXY(int x, int y) {
        //check for connections to self
        if (this.getLocation() == this.link.getOtherEndPoint(this).getLocation()) {
            int left = node.getPolygon().xpoints[0];
            int right = node.getPolygon().xpoints[1];
            int width = right - left;
            
            int top = node.getPolygon().ypoints[0];
            int bottom = node.getPolygon().ypoints[3];
            int height = bottom - top;
            
            switch (this.getLocation()) {
                case TOP:
                case BOTTOM:
                    if (x >= left && x <= right) {
                        setLocationByPercent(getLocation(), ((double)x - left)/width);
                    }
                    break;                
                case RIGHT:
                default: //case LEFT
                    if (y >= top && y <= bottom) {
                        setLocationByPercent(getLocation(), ((double)y - top)/height);
                    }
            }
        } else {
            relocateTo(x, y);
            setPosition(calculatePosition());
        }               
    }

    //subclass overrides to manage endpoint movement and rejection of movement
    public boolean relocateToNode(Node sourceNode, Node targetNode, Node otherNode) {
        //move endpoint to new node
        node.endPoints.remove(this);
        this.node = targetNode;
        this.node.endPoints.add(this);
        
        return true;
    }
    
    @Override
    public void translate(int deltaX, int deltaY) {
        super.translate(deltaX, deltaY);
        
        posX += deltaX;
        posY += deltaY;
    }
    
    @Override
    public void snapToGrid(Grid grid) {
        if (grid.snapToGrid()) {
            Point pos = calculatePosition();
            setPosition(grid.snap(pos.x, pos.y));
        }
    }

    public Point getPosition() {
        return new Point(posX, posY);
    }
        
    public void setPosition(Point pos) {
        posX = pos.x;
        posY = pos.y;
    }

    private Point calculatePosition() {
        if (location == TOP) {
            int width = node.getPolygon().xpoints[1] - node.getPolygon().xpoints[0];
            posX = node.getPolygon().xpoints[0] + (int)(width*percent);
            posY = node.getPolygon().ypoints[0];
        } else if (location == BOTTOM) {
            int width = node.getPolygon().xpoints[2] - node.getPolygon().xpoints[3];
            posX = node.getPolygon().xpoints[3] + (int)(width*percent);
            posY = node.getPolygon().ypoints[3];
        } else if (location == RIGHT) {
            int height = node.getPolygon().ypoints[2] - node.getPolygon().ypoints[1];
            posX = node.getPolygon().xpoints[1];
            posY = node.getPolygon().ypoints[1] + (int)(height*percent);
        } else { //location == LEFT
            int height = node.getPolygon().ypoints[3] - node.getPolygon().ypoints[0];
            posX = node.getPolygon().xpoints[0];
            posY = node.getPolygon().ypoints[0] + (int)(height*percent);
        }

        return new Point(posX, posY);
    }

    private void relocateTo(int e1PosX, int e1PosY) {
        int e2PosX = link.getOtherEndPoint(this).getPosition().x;
        int e2PosY = link.getOtherEndPoint(this).getPosition().y;

        int width = node.getPolygon().xpoints[1] - node.getPolygon().xpoints[0];
        int height = node.getPolygon().ypoints[2] - node.getPolygon().ypoints[1];
        
        //check top edge
        if (LinesIntersect.DO_INTERSECT == LinesIntersect.check(e1PosX, e1PosY, e2PosX, e2PosY,
                node.getPolygon().xpoints[0], node.getPolygon().ypoints[0], node.getPolygon().xpoints[1], node.getPolygon().ypoints[1])) {            
            //setLocation(TOP, (LinesIntersect.x - node.xpoints[0])/width);
            setLocationByPercent(TOP, ((double)e1PosX - node.getPolygon().xpoints[0])/width);

        //check bottom edge
        } else if (LinesIntersect.DO_INTERSECT == LinesIntersect.check(e1PosX, e1PosY, e2PosX, e2PosY, 
                node.getPolygon().xpoints[3], node.getPolygon().ypoints[3], node.getPolygon().xpoints[2], node.getPolygon().ypoints[2])) {
            //setLocation(BOTTOM, (LinesIntersect.x - node.xpoints[0])/width);
            setLocationByPercent(BOTTOM, ((double)e1PosX - node.getPolygon().xpoints[0])/width);

        //check right edge
        } else if (LinesIntersect.DO_INTERSECT == LinesIntersect.check(e1PosX, e1PosY, e2PosX, e2PosY,
                node.getPolygon().xpoints[1], node.getPolygon().ypoints[1], node.getPolygon().xpoints[2], node.getPolygon().ypoints[2])) {            
            //setLocation(RIGHT, (LinesIntersect.y - node.ypoints[1])/height);
            setLocationByPercent(RIGHT, ((double)e1PosY - node.getPolygon().ypoints[1])/height);
            
        //check left edge
        } else if (LinesIntersect.DO_INTERSECT == LinesIntersect.check(e1PosX, e1PosY, e2PosX, e2PosY,
                node.getPolygon().xpoints[0], node.getPolygon().ypoints[0], node.getPolygon().xpoints[3], node.getPolygon().ypoints[3])) {
            //setLocation(LEFT, (LinesIntersect.y - node.ypoints[0])/height);
            setLocationByPercent(LEFT, ((double)e1PosY - node.getPolygon().ypoints[0])/height);

        }
    } 
    
    @Override
    public Polygon getPolygon() {
        Polygon rect = new Polygon();
        rect.npoints = 4;
        rect.xpoints = new int[4];
        rect.ypoints = new int[4];

        //create a rectangle around the current position
        Point pos = getPosition();
        rect.xpoints[0] = pos.x-4; rect.ypoints[0] = pos.y-4;
        rect.xpoints[1] = pos.x+4; rect.ypoints[1] = pos.y-4;
        rect.xpoints[2] = pos.x+4; rect.ypoints[2] = pos.y+4;
        rect.xpoints[3] = pos.x-4; rect.ypoints[3] = pos.y+4;

        return rect;
    }
        
    protected Node              node;        
    protected int               posX, posY;
    
    protected int               location;
    protected double            percent;
    
    protected GraphCanvas       canvas;
    protected boolean           hide;    
    protected Link              link;
}
