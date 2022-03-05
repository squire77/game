package com.pd.modelcg.codegen.graphics.draggablecanvas;

import com.pd.modelcg.codegen.graphics.drawablecanvas.IDrawable;

import java.awt.event.MouseEvent;

public interface IDraggable extends ISelectable, IDrawable {
    void snapToGrid(Grid grid);
    void remove(DraggableCanvas canvas);

    //this indicates whether to treat the object as stand-alone or part of a group
    boolean isDepthLevelOne();

    //this is the main course so I copied it from IMousable as a reminder
    void mouseDragged(MouseEvent event, int deltaX, int deltaY);
}