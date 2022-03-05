package com.pd.modelcg.codegen.graphics.drawablecanvas;

import com.pd.modelcg.codegen.graphics.drawablecanvas.mousable.Mouseable;
import com.pd.modelcg.codegen.graphics.util.PolygonSplit;
import com.pd.modelcg.codegen.graphics.util.Primitives;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class Drawable extends Mouseable implements IDrawable {
    public enum DrawType { Default, GeneralPath }
    public static class DrawProperties {}

    public DrawType             drawType;
    public DrawProperties       drawProperties;

    public Drawable(Polygon polygon) {
        this(polygon, DrawType.Default, new DrawProperties());
    }

    public Drawable(Polygon polygon, DrawType dType, DrawProperties dProps) {
        if (polygon == null)
            throw new IllegalArgumentException("new Drawable requires a non-null Polygon");

        init(dType, dProps, polygon);
    }

    //only a subclass can call these constructors, and if it does,
    //initialize(Polygon) must be called after the constructor is called otherwise you have a null Polygon and incoherence
    protected Drawable() {
        init(DrawType.Default, new DrawProperties(), null);
    }
    protected Drawable(DrawType dType, DrawProperties dProps) {
        init(dType, dProps, null);
    }

    private void init(DrawType dType, DrawProperties dProps, Polygon polygon) {
        this.drawType = dType;
        this.drawProperties = dProps;
        this.isVisible = true;
        this.drawFill = true;
        this.drawBorder = true;
        this.rotationCache = new RotationCache(polygon);

        initialize(polygon);
    }

    public void initialize(Polygon polygon) {
        if (polygon == null)
            throw new IllegalArgumentException("new Drawable requires a non-null Polygon");

        setPolygon(polygon);
        rotationCache.resetCache();
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(Color color){
        this.backgroundColor = color;
    }

    public Color getBorderColor() {
        return this.borderColor;
    }

    public void setBorderColor(Color color){
        this.borderColor = color;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public void setDrawFill(boolean drawFill) {
        this.drawFill = drawFill;
    }

    public void setDrawBorder(boolean drawBorder) {
        this.drawBorder = drawBorder;
    }

    //not allowed to modify polygon once it's set
    //but it will get auto-updated as we move around
    private void setPolygon(Polygon polygon) {
        this.polygon = polygon;
        rotationCache.resetCache();
    }

    //subclass may override getPolygon() to provide a dynamically moving or
    //changing polygon

    public Polygon getPolygon() {
        return this.polygon;
    }

    public int width() {
        return getPolygon().xpoints[1] - getPolygon().xpoints[0];
    }

    public int height() {
        return getPolygon().ypoints[2] - getPolygon().ypoints[1];
    }

    public Point getCenter() { return center; }

    //*** containment and intersection are performed on this.getPolygon() which
    //*** may be a different shape that is based on Drawable.polygon

    public boolean validate(int deltaX, int deltaY, int width, int height) {
        boolean result = true;

        int left = getPolygon().xpoints[0];
        int right = getPolygon().xpoints[0];
        int top = getPolygon().ypoints[0];
        int bottom = getPolygon().ypoints[0];

        //find the boundaries
        for (int i=1; i<getPolygon().npoints; i++) {
            if (getPolygon().xpoints[i] < left)
                left = getPolygon().xpoints[i];
            if (getPolygon().xpoints[i] > right)
                right = getPolygon().xpoints[i];
            if (getPolygon().ypoints[i] < top)
                top = getPolygon().ypoints[i];
            if (getPolygon().ypoints[i] > bottom)
                bottom = getPolygon().ypoints[i];
        }

        if ((left + deltaX < 0) || (right + deltaX > width) || (top + deltaY < 0) || (bottom + deltaY > height)) {
            result = false;
        }

        return result;
    }

    public Rectangle2D getBounds2D() { return getPolygon().getBounds2D(); }

    public boolean contains(int x, int y) { //return myContains(getPolygon(), x, y); }
        //this appears to be broken!
        return getPolygon().contains(x, y); }

    public boolean intersects(IDrawable region) { return getPolygon().intersects(region.getBounds2D()); }

    private boolean myContains(Polygon p, int x, int y) {
        int a1 = polygon.xpoints[0];
        int b1 = polygon.ypoints[0];

        int n = polygon.npoints;

        for (int i=1; i<=n; i++)
        {
            int a2 = polygon.xpoints[i%n];
            int b2 = polygon.ypoints[i%n];

            if (PolygonSplit.isPointAboveLine(a1, b1, a2, b2, x, y) != 0) {
                return false;
            }
        }

        return true;
    }

    //*** drawing occurs on this.getPolygon() which may be a different shape
    //*** that is based on Drawable.polygon

    final public void draw(Graphics2D g) {
        if (isVisible) {
            doDraw(g);
        }
    }

    //subclass may override doDraw(), but is required to use getPolygon()
    //since Drawable.polygon is private
    protected void doDraw(Graphics2D g) {
        if (drawFill) {
            g.setPaint(getColor());
        } else {
            g.setPaint(backgroundColor);
        }

        g.fill(getPolygon());

        if (drawBorder) {
            g.setPaint(getBorderColor());
            g.draw(getPolygon());
        }
    }

    //*** transformations are performed on Drawable.polygon

    public void translate(int deltaX, int deltaY) {
        transX += deltaX;
        transY += deltaY;

        center.x += deltaX;
        center.y += deltaY;

        setPolygon(Primitives.translatePolygon(polygon, deltaX, deltaY));
    }

    public void rotate(int newAngle) {
        //normalize the angle
        angle = (angle + newAngle) % 360;
        if (angle < 0) {
            angle += 360;
        }

        polygon = Primitives.translatePolygon(rotationCache.getRotation(angle), transX, transY);
    }

    public void scale(int deltaX, int deltaY) {
        //not supported
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    private static class RotationCache {
        public RotationCache(Polygon polygon) {
            this.polygon = polygon;
        }

        public void resetCache() {
            this.originalPolygon = copyPolygon(polygon);

            rotations[0] = this.originalPolygon;
        }

        private Polygon copyPolygon(Polygon original) {
            Polygon copy = new Polygon();

            for (int i=0; i<original.npoints; i++) {
                copy.addPoint(original.xpoints[i], original.ypoints[i]);
            }

            return copy;
        }

        public Polygon getRotation(int angle) {
            // cache the rotation
            // note: cache must be reset if polygon changes
            if (rotations[angle] == null) {
                //polygon center is presumed to be (0,0)
                rotations[angle] = Primitives.rotatePolygon(originalPolygon, angle, 0, 0);
                //System.out.println("angle=" + angle);
            }

            return rotations[angle];
        }

        private final Polygon polygon;
        private Polygon originalPolygon;
        private final Polygon[] rotations = new Polygon[360];
    }

    private int transX = 0;
    private int transY = 0;

    private Polygon polygon;

    private Color color;
    private Color backgroundColor;
    private Color borderColor;

    private boolean isVisible;
    private boolean drawFill;
    private boolean drawBorder;

    private RotationCache rotationCache;

    protected Point center = new Point();
    protected int angle = 0;
}
