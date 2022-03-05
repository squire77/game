package com.pd.modelcg.codegen.graphics.drawablecanvas.mousable;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseMotionListener;

public interface IMousable extends MouseListener, MouseWheelListener, MouseMotionListener  {
    void mouseDragged(MouseEvent event, int deltaX, int deltaY);
}
