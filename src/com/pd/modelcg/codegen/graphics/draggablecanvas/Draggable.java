package com.pd.modelcg.codegen.graphics.draggablecanvas;

import java.awt.*;
import java.awt.event.MouseEvent;

public class Draggable extends Selectable implements IDraggable {
    public Draggable(Polygon polygon) {
        super(polygon);
    }

    public Draggable(Polygon polygon, Color selCol, Color unselCol,
                     DrawType dType, DrawProperties dProps) {
        super(polygon, selCol, unselCol, dType, dProps);
    }

    //only a subclass can call this constructor, and if it does,
    //initialize(Polygon) must be called after the constructor is called
    protected Draggable() {
    }

    @Override
    public void initialize(Polygon polygon) {
        super.initialize(polygon);
    }

    public void mouseDragged(MouseEvent event, int deltaX, int deltaY) {
        translate(deltaX, deltaY);
    }

    public void snapToGrid(Grid grid) {
        if (grid.snapToGrid()) {
            for(int i=0; i<getPolygon().npoints; i++) {
                Point snapped = grid.snap(getPolygon().xpoints[i], getPolygon().ypoints[i]);
                getPolygon().xpoints[i] = snapped.x;
                getPolygon().ypoints[i] = snapped.y;
            }
        }
    }

    public void remove(DraggableCanvas canvas) {
        canvas.removeObject(this);
    }

    public boolean isDepthLevelOne() {
        return true;
    }
}
