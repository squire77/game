package com.pd.modelcg.codegen.graphics.graphcanvas;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class NodeWithAdornments extends Node {
    public NodeWithAdornments(Graph graph, Polygon polygon) {
        super(graph, polygon);
        this.adornments = new ArrayList<Adornment>();
    }

    public void addControl(Adornment adornment) {
        adornments.add(adornment);
    }


    @Override
    public void translate(int deltaX, int deltaY) {
        super.translate(deltaX, deltaY);

        //adornments are not explicitly on the canvas so we delegate to them directly
        for (Adornment c: adornments)
            c.translate(deltaX, deltaY);
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        //adornments are not explicitly on the canvas so we delegate to them directly
        for (Adornment c: adornments) {
            if (c.contains(event.getX(), event.getY())) {
                c.mouseReleased(event);
                return;
            }
        }

        super.mouseReleased(event);
    }

    @Override
    public void mousePressed(MouseEvent event) {
        //adornments are not explicitly on the canvas so we delegate to them directly
        for (Adornment c: adornments) {
            if (c.contains(event.getX(), event.getY())) {
                c.mousePressed(event);
                return;
            }
        }

        super.mousePressed(event);
    }

    @Override
    public void mouseDragged(MouseEvent event, int deltaX, int deltaY) {
        //adornments are not explicitly on the canvas so we delegate to them directly
        for (Adornment c: adornments) {
            if (c.contains(event.getX(), event.getY())) {
                c.mouseDragged(event, deltaX, deltaY);
                return;
            }
        }

        super.mouseDragged(event, deltaX, deltaY);
    }

    private java.util.List<Adornment> adornments;
}
