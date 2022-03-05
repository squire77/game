package com.pd.modelcg.codegen.graphics.graphcanvas;

import com.pd.modelcg.codegen.graphics.util.Primitives;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

abstract public class Adornment {
    //must call this after calling the constructor
    public Adornment initialize() {
        this.polygon = getPolygon();
        return this;
    }
    
    public Rectangle2D getBounds2D() { return getPolygon().getBounds2D(); }
    public boolean contains(int x, int y) { return getPolygon().contains(x, y); }
    
    public void translate(int deltaX, int deltaY) {     
        polygon = Primitives.translatePolygon(polygon, deltaX, deltaY);
    }        

    public void mouseReleased(MouseEvent event) {}
    public void mousePressed(MouseEvent event) {}
    public void mouseDragged(MouseEvent event, int deltaX, int deltaY) {}
    
    abstract public Polygon getPolygon();
    
    private Polygon polygon;
}
