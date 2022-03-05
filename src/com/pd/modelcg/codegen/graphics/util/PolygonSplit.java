package com.pd.modelcg.codegen.graphics.util;

import java.awt.*;

public class PolygonSplit {
    public static final int DO_INTERSECT = 1;
    public static final int DONT_INTERSECT = 0;

    public static Polygon top; // this is right for a vertical split
    public static Polygon bottom; // this is left for a vertical split

    public static int splitPolygonWithLine(Polygon polygon, int x1, int y1, int x2, int y2) {
        int a1 = polygon.xpoints[0];
        int b1 = polygon.ypoints[0];

        Polygon p1 = new Polygon();
        Polygon p2 = new Polygon();

        p1.addPoint(a1, b1);
        int side1 = isPointAboveLine(x1, y1, x2, y2, a1, b1);

        int n = polygon.npoints;

        for (int i = 1; i <= n; i++) {
            int a2 = polygon.xpoints[i % n];
            int b2 = polygon.ypoints[i % n];
            int side2 = isPointAboveLine(x1, y1, x2, y2, a2, b2);

            if (LinesIntersect.DO_INTERSECT == LinesIntersect.check(a1, b1, a2, b2, x1, y1, x2, y2)
                    && side2 != 0 && side2 == -side1) {
                p1.addPoint((int) LinesIntersect.x, (int) LinesIntersect.y);
                p2.addPoint((int) LinesIntersect.x, (int) LinesIntersect.y);
                p2.addPoint(a2, b2);

                //swap sides
                Polygon p3 = p1;
                p1 = p2;
                p2 = p3;
            } else {
                p1.addPoint(a2, b2);
            }

            a1 = a2;
            b1 = b2;

            //don't reset the side if it's on the line
            if (side2 != 0) {
                side1 = side2;
            }
        }

        top = p2;
        bottom = p1;

        //check for top and bottom
        for (int i = 0; i < p1.npoints; i++) {
            if (isPointAboveLine(x1, y1, x2, y2, p1.xpoints[i], p1.ypoints[i]) > 0) {
                top = p1;
                bottom = p2;
                break;
            }
        }

        if (top.npoints == 0 || bottom.npoints == 0) {
            return DONT_INTERSECT;
        } else {
            return DO_INTERSECT;
        }
    }

    public static int isPointAboveLine(int x1, int y1, int x2, int y2, int a, int b) {
        int crossProduct = (b - y1) * (x2 - x1) - (y2 - y1) * (a - x1);

        //we have to flip the sign since the screen is flipped
        return (crossProduct < 0) ? 1 : (crossProduct > 0) ? -1 : 0;
    }
}
