package com.pd.modelcg.codegen.graphics.graphcanvas;

import com.pd.modelcg.codegen.graphics.draggablecanvas.Grid;

import java.awt.*;
import java.util.ArrayList;

public class Graph {
    public Graph() {
        this.links = new ArrayList<Link>();
        this.nodes = new ArrayList<Node>();
    }   

    public void snapToGrid(Grid grid) {
        if (grid.snapToGrid()) {
            for (Node n: nodes)
                n.snapToGrid(grid);
            for (Link l: links)
                l.snapToGrid(grid);
        }
    }

    public void draw(Graphics2D g) {
        for (Node n: nodes)
            n.draw(g);
        for (Link l: links)
            l.draw(g);
    }

    public java.util.List<Node> getNodes() {
        return nodes;
    }
    
    public java.util.List<Link> getLinks() {
        return links;
    }
    
    public void addLink(Link link) {
        this.links.add(link);
    }
    
    public void addNode(Node node) {
        this.nodes.add(node);
    }
    
    public Link removeLink(Link link) {
        return (this.links.remove(link)) ? link : null;
    }
    
    public Node removeNode(Node node) {
        return (this.nodes.remove(node)) ? node : null;
    }

    private java.util.List<Link> links;
    private java.util.List<Node> nodes;
}
