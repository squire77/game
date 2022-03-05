package com.pd.modelcg.codegen.graphics.drawablecanvas;

import java.awt.event.MouseWheelListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public interface IDrawableCanvas {
    void init();
    void reset();

    int getWidth();
    int getHeight();

    void addObject(IDrawable object);
    void removeObject(IDrawable object);
    void draw();

    void addMouseListener(MouseListener listener);
    void addMouseWheelListener(MouseWheelListener listener);
    void addMouseMotionListener(MouseMotionListener listener);
}
