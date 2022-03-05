package com.pd.modelcg.codegen.graphics.draggablecanvas;

import java.awt.*;

public class Grid {
    public static final Color DEFAULT_GRID_COLOR = Color.CYAN;
    public static final int DEFAULT_GRID_SPACING = 15;

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.spacing = DEFAULT_GRID_SPACING;
        this.showGrid = true;
        this.snapToGrid = true;
    }

    public boolean showGrid() {
        return this.showGrid;
    }
    public void showGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public boolean snapToGrid() {
        return this.snapToGrid;
    }
    public void snapToGrid(boolean snapToGrid) {
        this.snapToGrid = snapToGrid;
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
    }

    public int getSpacing() {
        return this.spacing;
    }

    public void draw(Graphics2D g) {
        if (!showGrid)
            return;

        g.setPaint(DEFAULT_GRID_COLOR);

        for (int i=spacing; i<width; i++)
            if (i%spacing == 0)
                g.drawLine(i, 0, i, height);
        for (int j=spacing; j<height; j++)
            if (j%spacing == 0)
                g.drawLine(0, j, width, j);
    }

    public Point snap(int x, int y) {
        if (!snapToGrid)
            return new Point(x,y);

        int mid = spacing / 2;
        int deltaX = (x > 0) ? x % spacing : 0;
        int deltaY = (y > 0) ? y % spacing : 0;

        int newx = x;
        int newy = y;

        if (deltaX < mid) //snap left
            newx -= deltaX;
        else //snap right
            newx += spacing - deltaX;

        if (deltaY < mid) //snap up
            newy -= deltaY;
        else //snap down
            newy += spacing - deltaY;

        //don't go out of bounds
        if (newx > width)
            newx = width;
        if (newy > height)
            newy = height;

        return new Point(newx, newy);
    }

    private int         width;
    private int         height;
    private int         spacing;
    private boolean     showGrid;
    private boolean     snapToGrid;
}
