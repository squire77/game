package com.pd.modelcg.codegen.graphics.drawablecanvas;

import java.awt.*;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

public interface IDrawable extends MouseMotionListener {
    boolean validate(int deltaX, int deltaY, int width, int height);
    Rectangle2D getBounds2D();
    boolean contains(int x, int y);
    boolean intersects(IDrawable region);

    Polygon getPolygon();
    int width();
    int height();
    Point getCenter(); // starts at (0,0) then updates on translate()

    void draw(Graphics2D g);
    void translate(int deltaX, int deltaY);
    void rotate(int angle);
    void scale(int deltaX, int deltaY);

    Color getColor();
    void setColor(Color color);
    Color getBackgroundColor();
    void setBackgroundColor(Color color);
    Color getBorderColor();
    void setBorderColor(Color color);
    boolean isVisible();
    void setVisible(boolean visible);
    void setDrawFill(boolean drawFill);
    void setDrawBorder(boolean drawBorder);
}
