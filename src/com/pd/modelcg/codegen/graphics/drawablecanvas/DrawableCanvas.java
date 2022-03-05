package com.pd.modelcg.codegen.graphics.drawablecanvas;

import com.pd.modelcg.codegen.graphics.drawablecanvas.mousable.IMousable;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DrawableCanvas extends JPanel implements IDrawableCanvas, IMousable {
    public DrawableCanvas() {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        System.out.println("screen dimensions: " + screenDim);
        setPreferredSize(screenDim);
        this.width = screenDim.width;
        this.height = screenDim.height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.zBuffer = new ArrayList<>();
        this.mouseListeners = new ArrayList<>();
        this.mouseWheelListeners = new ArrayList<>();
        this.mouseMotionListeners = new ArrayList<>();

        super.addMouseWheelListener(this);
        super.addMouseListener(this);
        super.addMouseMotionListener(this);
    }

    public void init() {
    }

    public void reset() {
        this.zBuffer = new ArrayList<>();
    }

    public void addObject(IDrawable drawable) {
        zBuffer.add(drawable); //add to top of stack
    }

    public void removeObject(IDrawable drawable) {
        zBuffer.remove(drawable);
    }

    // Call this to redraw the buffer and re-paint it to the screen.
    // Ideally, this should only be called once every 1/60s.
    public void draw() {
        Graphics2D g = (Graphics2D) image.getGraphics();

        g.setPaint(BACKGROUND_COLOR);
        g.fillRect(0, 0, width, height);

        doDrawObjects(g, zBuffer);
        repaint();
    }

    protected void doDrawObjects(Graphics2D g, List<IDrawable> drawables) {
        final List<IDrawable> copy = new ArrayList<>(drawables);
        copy.forEach(it->it.draw(g));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image, 0, 0, BACKGROUND_COLOR, null);

        //System.out.println("Canvas paintComponent was called():" + System.currentTimeMillis());
    }

    public void addMouseListener(MouseListener listener) {
        mouseListeners.add(listener);
    }
    public void addMouseWheelListener(MouseWheelListener listener) {
        mouseWheelListeners.add(listener);
    }
    public void addMouseMotionListener(MouseMotionListener listener) {
        mouseMotionListeners.add(listener);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mouseListeners.forEach((listener) -> listener.mouseClicked(e));
    }
    @Override
    public void mousePressed(MouseEvent e) {
        mouseListeners.forEach((listener) -> listener.mousePressed(e));
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        mouseListeners.forEach((listener) -> listener.mouseReleased(e));
    }
    @Override
    public void mouseEntered(MouseEvent e) {
        mouseListeners.forEach((listener) -> listener.mouseEntered(e));
    }
    @Override
    public void mouseExited(MouseEvent e) {
        mouseListeners.forEach((listener) -> listener.mouseExited(e));
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseWheelListeners.forEach((listener) -> listener.mouseWheelMoved(e));
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        //System.out.println("mouse dragged");
        //note: event is not sent outside the boundaries of the canvas
        //mouseMotionListeners.forEach((listener) -> listener.mouseDragged(e));
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        //System.out.println("mouse moved");
        //note: event is not sent outside the boundaries of the canvas
        mouseMotionListeners.forEach((listener) -> listener.mouseMoved(e));
    }

    @Override
    public void mouseDragged(MouseEvent event, int deltaX, int deltaY) {
    }

    private final static Color BACKGROUND_COLOR = Color.lightGray;

    private final List<MouseListener>           mouseListeners;
    private final List<MouseWheelListener>      mouseWheelListeners;
    private final List<MouseMotionListener>     mouseMotionListeners;

    protected int                               width;
    protected int                               height;
    protected BufferedImage                     image;
    protected List<IDrawable>                   zBuffer;
}
