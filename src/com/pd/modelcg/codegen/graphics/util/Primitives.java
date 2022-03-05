package com.pd.modelcg.codegen.graphics.util;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Primitives {
    public static void wrapToWindow(boolean _wrap, int _startX, int _startY, int _lengthX, int _lengthY) {
        startX = _startX;
        startY = _startY;
        endX = _startX + _lengthX;
        endY = _startY + _lengthY;
        lengthX = _lengthX;
        lengthY = _lengthY;
    }

    public static Polygon translatePolygon(Polygon polygon, int transX, int transY) {
        Polygon newPolygon = new Polygon();

        for (int i = 0; i < polygon.npoints; i++) {
            newPolygon.addPoint(polygon.xpoints[i] + transX, polygon.ypoints[i] + transY);
        }

        return newPolygon;
    }

    public static Polygon rotatePolygon(Polygon polygon, double angle, int transX, int transY) {
        AffineTransform at = new AffineTransform();
        at.rotate(Math.toRadians(angle));
        Polygon newPolygon = new Polygon();
        Point point = new Point();

        for (int i = 0; i < polygon.npoints; i++) {
            at.transform(new Point(polygon.xpoints[i] - transX, polygon.ypoints[i] - transY), point);
            newPolygon.addPoint(point.x + transX, point.y + transY);
        }

        return newPolygon;
    }

    private static Polygon copyPolygon(Polygon polygon) {
        Polygon newPolygon = new Polygon();

        for (int i = 0; i < polygon.npoints; i++) {
            newPolygon.addPoint(polygon.xpoints[i], polygon.ypoints[i]);
        }

        return newPolygon;
    }

    public static void drawPolygon(Graphics2D g, Polygon polygon) {
        int x1 = polygon.xpoints[polygon.npoints - 1];
        int y1 = polygon.ypoints[polygon.npoints - 1];

        for (int i = 0; i < polygon.npoints; i++) {
            int x2 = polygon.xpoints[i];
            int y2 = polygon.ypoints[i];
            line(g, x1, y1, x2, y2);
            x1 = x2;
            y1 = y2;
        }
    }

    public static void fillPolygon(Graphics2D g, Polygon _polygon) {
        int xi[] = new int[20];
        double slope[] = new double[20];

        drawPolygon(g, _polygon);

        //don't keep adding a point to the same polygon over and over cause
        //we're passing pointers around, not copies
        Polygon polygon = copyPolygon(_polygon);
        polygon.addPoint(_polygon.xpoints[0], _polygon.ypoints[0]);

        int n = _polygon.npoints;

        for (int i = 0; i < n; i++) {
            int dy = polygon.ypoints[i + 1] - polygon.ypoints[i];
            int dx = polygon.xpoints[i + 1] - polygon.xpoints[i];

            if (dy == 0) slope[i] = 1.0;
            if (dx == 0) slope[i] = 0.0;

            if (dy != 0 && dx != 0) /*- calculate inverse slope -*/ {
                slope[i] = (float) dx / dy;
            }
        }

        for (int y = 0; y < 1000; y++) {
            int k = 0;
            for (int i = 0; i < n; i++) {
                if ((polygon.ypoints[i] <= y && polygon.ypoints[i + 1] > y) ||
                        (polygon.ypoints[i] > y && polygon.ypoints[i + 1] <= y)) {
                    xi[k] = (int) (polygon.xpoints[i] + slope[i] * (y - polygon.ypoints[i]));
                    k++;
                }
            }

            for (int j = 0; j < k - 1; j++) /*- Arrange x-intersections in order -*/
                for (int i = 0; i < k - 1; i++) {
                    if (xi[i] > xi[i + 1]) {
                        int temp = xi[i];
                        xi[i] = xi[i + 1];
                        xi[i + 1] = temp;
                    }
                }

            for (int i = 0; i < k; i += 2) {
                line(g, xi[i], y, xi[i + 1] + 1, y);
            }
        }
    }

    // draw a line from point x1,y1 into x2,y2
    public static void line(Graphics2D g, int x1, int y1, int x2, int y2) {
        // if point x1, y1 is on the right side of point x2, y2, change them
        if ((x1 - x2) > 0) {
            line(g, x2, y2, x1, y1);
            return;
        }
        // test inclination of line
        // function Math.abs(y) defines absolute value y
        if (Math.abs(y2 - y1) > Math.abs(x2 - x1)) {
            // line and y axis angle is less then 45 degrees
            // thats why go on the next procedure
            steepline(g, y1, x1, y2, x2);
            return;
        }
        // line and x axis angle is less then 45 degrees, so x is guiding
        // auxiliary variables
        int x = x1, y = y1, sum = x2 - x1, Dx = 2 * (x2 - x1), Dy = Math.abs(2 * (y2 - y1));
        int prirastokDy = ((y2 - y1) > 0) ? 1 : -1;
        // draw line
        for (int i = 0; i <= x2 - x1; i++) {
            point(g, x, y);
            x++;
            sum -= Dy;
            if (sum < 0) {
                y = y + prirastokDy;
                sum += Dx;
            }
        }
    }

    private static void steepline(Graphics2D g, int x3, int y3, int x4, int y4) {
        // if point x3, y3 is on the right side of point x4, y4, change them
        if ((x3 - x4) > 0) {
            steepline(g, x4, y4, x3, y3);
            return;
        }

        int x = x3, y = y3, sum = x4 - x3, Dx = 2 * (x4 - x3), Dy = Math.abs(2 * (y4 - y3));
        int prirastokDy = ((y4 - y3) > 0) ? 1 : -1;

        for (int i = 0; i <= x4 - x3; i++) {
            point(g, y, x);
            x++;
            sum -= Dy;
            if (sum < 0) {
                y = y + prirastokDy;
                sum += Dx;
            }
        }
    }

    public static void point(Graphics2D g, int x, int y) {
        g.drawLine(x, y, x, y);
    }

    public static void splitOnBorder(Polygon polygon, java.util.List<Polygon> list, int level) {
        boolean top = false;
        boolean right = false;
        boolean bottom = false;
        boolean left = false;

        for (int i = 0; i < polygon.npoints; i++) {
            if (polygon.ypoints[i] < startY) {
                top = true;
                break;
            }
            if (polygon.xpoints[i] > endX) {
                right = true;
                break;
            }
            if (polygon.ypoints[i] > endY) {
                bottom = true;
                break;
            }
            if (polygon.xpoints[i] < startX) {
                left = true;
                break;
            }
        }

        //top
        if (top && PolygonSplit.DO_INTERSECT == PolygonSplit.splitPolygonWithLine(polygon, startX - 500, startY, endX + 500, startY)) {
            if (level == 2) {
                list.add(PolygonSplit.bottom);
                list.add(translatePolygon(PolygonSplit.top, 0, lengthY));
            } else {
                Polygon pTop = PolygonSplit.top;
                splitOnBorder(PolygonSplit.bottom, list, 2);
                splitOnBorder(translatePolygon(pTop, 0, lengthY), list, 2);
            }

            return;
        }

        //right
        if (right && PolygonSplit.DO_INTERSECT == PolygonSplit.splitPolygonWithLine(polygon, endX, startY - 500, endX, endY + 500)) {
            if (level == 2) {
                list.add(PolygonSplit.bottom);
                list.add(translatePolygon(PolygonSplit.top, -lengthX, 0));
            } else {
                Polygon pTop = PolygonSplit.top;
                splitOnBorder(PolygonSplit.bottom, list, 2);
                splitOnBorder(translatePolygon(pTop, -lengthX, 0), list, 2);
            }

            return;
        }

        //bottom
        if (bottom && PolygonSplit.DO_INTERSECT == PolygonSplit.splitPolygonWithLine(polygon, endX + 500, endY, startX - 500, endY)) {
            if (level == 2) {
                list.add(PolygonSplit.bottom);
                list.add(translatePolygon(PolygonSplit.top, 0, -lengthY));
            } else {
                Polygon pTop = PolygonSplit.top;
                splitOnBorder(PolygonSplit.bottom, list, 2);
                splitOnBorder(translatePolygon(pTop, 0, -lengthY), list, 2);
            }

            return;
        }

        //left
        if (left && PolygonSplit.DO_INTERSECT == PolygonSplit.splitPolygonWithLine(polygon, startX, endY + 500, startX, startY - 500)) {
            if (level == 2) {
                list.add(PolygonSplit.bottom);
                list.add(translatePolygon(PolygonSplit.top, lengthX, 0));
            } else {
                Polygon pTop = PolygonSplit.top;
                splitOnBorder(PolygonSplit.bottom, list, 2);
                splitOnBorder(translatePolygon(pTop, lengthX, 0), list, 2);
            }

            return;
        }

        //polygon must lie inside the window
        list.add(polygon);
    }

    private static int startX;
    private static int startY;
    private static int endX;
    private static int endY;
    private static int lengthX;
    private static int lengthY;
}
