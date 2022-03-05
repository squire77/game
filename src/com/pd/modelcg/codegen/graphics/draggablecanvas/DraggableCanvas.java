package com.pd.modelcg.codegen.graphics.draggablecanvas;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

public class DraggableCanvas extends JPanel implements MouseWheelListener, MouseInputListener {
    public DraggableCanvas() {
        this.disableDrawing = false;

        this.savedSelection = new ArrayList<>();
        this.groupSelection = new ArrayList<>();
        this.groupSelectionEnabled = false;
        this.groupSelectionBounds = new Rectangle();
        this.groupSelectionStart = new Point();
        this.groupSelectionEnd = new Point();

        //create a background grid
        this.grid = new Grid(width, height);
        this.grid.snapToGrid(true); //enable snap to grid
        this.grid.showGrid(true); //enable show grid
        this.selection = null;
        this.zBuffer = new ArrayList<>();
    }

    //must be called after calling constructor
    //also, call drawObjects() paint the background and optional grid
    public void init() {
        createObjectPopupMenu();
        createBackgroundPopupMenu();
        createGroupSelectionPopupMenu();
        addMouseWheelListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void reset() {
        this.disableDrawing = false;
        this.selection = null;
        this.zBuffer = new ArrayList<>();
    }

    //optimize the drawing speed
    public void disableDrawing() {
        this.disableDrawing = true;
    }
    public void enableDrawing() {
        this.disableDrawing = false;
    }

    public int getGridSpacing() {
        return grid.getSpacing();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(image, 0, 0, BACKGROUND_COLOR, null);

        if (groupSelectionActive) {
            Graphics2D g2d = (Graphics2D) g;

            for (IDraggable d: groupSelection) {
                d.draw(g2d);
            }

            g2d.setColor(Color.BLUE);
            g2d.setStroke(DASHED_STROKE);
            g2d.drawRect(groupSelectionBounds.x, groupSelectionBounds.y,
                    groupSelectionBounds.width, groupSelectionBounds.height);

            return;
        }

        //draw active selection over the image
        if (selection != null) {
            selection.draw((Graphics2D)g);
        }
    }

    public void showGrid(boolean visible) {
        grid.showGrid(visible);
        drawObjects();
    }

    public void snapToGrid(boolean snap) {
        grid.snapToGrid(snap);
        drawObjects();
    }

    public void addObject(Object object) {
        IDraggable draggable = (IDraggable) object;
        zBuffer.add(draggable); //add to top of stack

        //draw new object
        draggable.snapToGrid(grid);

        if (!disableDrawing) {
            draggable.draw((Graphics2D)image.getGraphics());
        }

        repaint();
    }

    // modify Draggable subclasses Remove() method, not this method
    // only Draggable.remove() should call this method
    final public void removeObject(IDraggable object) {
        zBuffer.remove(object);
        drawObjects(); //remove object from image
    }

    final public void drawObjects() {
        if (!disableDrawing) {
            Graphics2D g = (Graphics2D)image.getGraphics();

            g.setPaint(BACKGROUND_COLOR);
            g.fillRect(0, 0, width, height);

            grid.draw(g);
            doDrawObjects(g);

            if (groupSelectionEnabled) {
                g.setColor(Color.BLUE);
                g.setStroke(DASHED_STROKE);
                g.drawRect(groupSelectionBounds.x, groupSelectionBounds.y,
                        groupSelectionBounds.width, groupSelectionBounds.height);
            }

            repaint(); // this is necessary to re-paint the canvas, for example if something gets removed
        }
    }

    protected void doDrawObjects(Graphics2D g) {
        for (IDraggable d: zBuffer) {
            d.snapToGrid(grid);
            d.draw(g);
        }
    }

    public java.util.List<IDraggable> getAllObjectsAtPosition(int x, int y) {
        List<IDraggable> objects = new ArrayList<>();
        for (IDraggable d: zBuffer)
            if(d.contains(x, y))
                objects.add(d);
        return objects;
    }

    public IDraggable getObjectAtPosition(int x, int y) {
        // select objects from the top down in the z-buffer (in case they overlap)
        if(zBuffer.size() > 0) {
            for(int i=zBuffer.size() - 1; i >= 0; i--) {
                IDraggable next = zBuffer.get(i);
                if( next.contains(x, y))
                    return next;
            }
        }

        return null;
    }

    public void selectObjectAtPosition(int x, int y) {
        IDraggable obj = getObjectAtPosition(x, y);
        updateSelection(obj);
    }

    private void updateSelection(IDraggable newSelection) {
        if(selection != null)
            selection.unselect();

        selection = newSelection;

        if(newSelection != null)
            newSelection.select();
    }

    protected void createObjectPopupMenu() {
        objectPopup = new PopupMenu();
        add(objectPopup);

        MenuItem remove = new MenuItem("Remove");
        objectPopup.add(remove);

        remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(selection != null) {
                    selection.remove(DraggableCanvas.this);
                    selection = null;
                }
            }
        } );
    }

    protected void createBackgroundPopupMenu() {
        backgroundPopup = new PopupMenu();
        add(backgroundPopup);

        MenuItem showGridM = new MenuItem("Show Grid");
        showGridM.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                grid.showGrid(true);
                drawObjects();
            }
        } );

        MenuItem hideGridM = new MenuItem("Hide Grid");
        hideGridM.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                grid.showGrid(false);
                drawObjects();
            }
        } );

        MenuItem enableGridM = new MenuItem("Enable Snap-to-Grid");
        enableGridM.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                grid.snapToGrid(true);
                drawObjects();
            }
        } );

        MenuItem disableGridM = new MenuItem("Disable Snap-to-Grid");
        disableGridM.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                grid.snapToGrid(false);
                drawObjects();
            }
        } );

        backgroundPopup.add(showGridM);
        backgroundPopup.add(hideGridM);
        backgroundPopup.add(enableGridM);
        backgroundPopup.add(disableGridM);
    }

    protected void createGroupSelectionPopupMenu() {
        groupSelectionPopup = new PopupMenu();
        add(groupSelectionPopup);
    }

    private void popupEvent(MouseEvent event) {
        if (event.isPopupTrigger()) {
            IDraggable object = getObjectAtPosition(event.getX(), event.getY());
            if(object != null) {
                objectPopup.show(this, event.getX(), event.getY());
            } else {
                if (groupSelectionEnabled && groupSelectionBounds.contains(event.getPoint())) {
                    groupSelectionPopup.show(this, event.getX(), event.getY());
                    return;
                }

                backgroundPopup.show(this, event.getX(), event.getY());
            }
        }
    }

    public void mouseEntered(MouseEvent event) {
        if (selection != null) {
            selection.mouseEntered(event);
        }

        repaint();
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
            if (selection != null) {
                int angle = e.getWheelRotation() * 5;
                selection.rotate(angle);
                drawObjects();
            }
        }

        repaint();
    }

    public void mouseMoved(MouseEvent event) {
        if (selection != null) {
            selection.mouseMoved(event);
        }

        repaint();
    }

    public void mouseExited(MouseEvent event) {
        if (selection != null) {
            selection.mouseExited(event);
        }

        repaint();
    }

    public void mouseClicked(MouseEvent event) { //mouse up and down
        if(selection != null) {
            selection.mouseClicked(event);
        } else {
            clearGroupSelection();
            drawObjects();
        }

        repaint();
    }

    public void mousePressed(MouseEvent event) { //mouse down
        startPos = event.getPoint();

        if (groupSelectionEnabled && groupSelectionBounds.contains(event.getPoint())) {
            this.groupSelectionActive = true;

            for (IDraggable d: groupSelection) {
                d.mousePressed(event);
                d.setVisible(false);
            }

            //erase selection before we drag it around; also, erase bounding box
            groupSelectionEnabled = false;
            drawObjects();
            groupSelectionEnabled = true;

            for (IDraggable d: groupSelection) {
                d.setVisible(true);
            }

            return;
        }

        selectObjectAtPosition(event.getX(), event.getY());

        if (selection != null) {
            selection.mousePressed(event);

            //erase selection before we drag it around
            selection.setVisible(false);
            drawObjects();
            selection.setVisible(true);
        } else {
            drawObjects(); //refresh everything, e.g. highlighting and endpoint visibility

            //check for group selection (rubberband)
            groupSelectionEnabled = true;
            groupSelectionStart.x = event.getX();
            groupSelectionStart.y = event.getY();
        }

        repaint();
    }

    public void mouseDragged(MouseEvent event) {
        if (startPos == null) {
            return;
        }

        int deltaX = event.getX() - (int)startPos.getX();
        int deltaY = event.getY() - (int)startPos.getY();

        if (groupSelectionActive) {
            if (validateGroupSelectionBounds(deltaX, deltaY, width, height)) {
                for (IDraggable d: groupSelection) {
                    //only drag non-grouped objects since grouped objects are assumed to move together
                    if (d.isDepthLevelOne()) {
                        d.mouseDragged(event, deltaX, deltaY);
                    }
                }

                groupSelectionBounds.x += deltaX;
                groupSelectionBounds.y += deltaY;

                startPos = event.getPoint();
                repaint();
            }

            return;
        }

        if(selection != null) {
            //make sure we don't move the object off the viewable screen
            if (selection.validate(deltaX, deltaY, width, height)) {
                selection.mouseDragged(event, deltaX, deltaY);
                startPos = event.getPoint();
                repaint();
            }
        } else {
            if (groupSelectionEnabled) {
                groupSelectionEnd.x = event.getX();
                groupSelectionEnd.y = event.getY();
                updateGroupSelectionBoundingRectangle();
                drawObjects();
            }
        }
    }

    public void mouseReleased(MouseEvent event) { //mouse up
        popupEvent(event);

        if (groupSelectionActive) {
            for (IDraggable d: groupSelection) {
                d.mouseReleased(event);
                d.snapToGrid(grid); //draw selection back onto the image
            }

            clearGroupSelection();
            drawObjects();
            startPos = null;
            return;
        }

        if(selection != null) {
            selection.mouseReleased(event);
            selection.snapToGrid(grid); //draw selection back onto the image
            drawObjects();
        } else {
            if (groupSelectionEnabled) {
                clearGroupSelection();
                updateGroupSelection();

                if (!groupSelection.isEmpty()) {
                    this.groupSelectionEnabled = true;

                    //save off the selection for use by pop-up actions
                    savedSelection.clear();
                    for (IDraggable d: this.groupSelection) {
                        //only add items of depth 1
                        if (d.isDepthLevelOne()) {
                            savedSelection.add(d);
                        }
                    }
                }

                drawObjects();
            }
        }

        repaint();
        startPos = null;
    }

    protected void clearGroupSelection() {
        //reset color back to normal
        for (IDraggable d: this.groupSelection) {
            unhighlightNodeAndLinks(d);
        }

        this.groupSelection.clear();
        this.groupSelectionEnabled = false;
        this.groupSelectionActive = false;
    }

    protected void updateGroupSelection() {
        for (IDraggable d: zBuffer) {
            if (isGroupSelectable(d) &&
                    groupSelectionBounds.contains(d.getPolygon().xpoints[0], d.getPolygon().ypoints[0]) &&
                    groupSelectionBounds.contains(d.getPolygon().xpoints[2], d.getPolygon().ypoints[2])) {
                this.groupSelection.add(d);

                //highlight color of selected objects
                highlightNodeAndLinks(d);
            }
        }
    }

    public boolean validateGroupSelectionBounds(int deltaX, int deltaY, int width, int height) {
        boolean result = true;

        int left = groupSelectionBounds.x;
        int right = groupSelectionBounds.x + deltaX;
        int top = groupSelectionBounds.y;
        int bottom = groupSelectionBounds.y + deltaY;

        if ((left + deltaX < 0) || (right + deltaX > width) ||
                (top + deltaY < 0) || (bottom + deltaY > height)) {
            result = false;
        }

        return result;
    }

    protected void updateGroupSelectionBoundingRectangle() {
        int startX;
        int rectWidth;

        if (groupSelectionStart.x < groupSelectionEnd.x) {
            startX = groupSelectionStart.x;
            rectWidth = groupSelectionEnd.x - startX;
        } else {
            startX = groupSelectionEnd.x;
            rectWidth = groupSelectionStart.x - startX;
        }

        int startY;
        int rectHeight;

        if (groupSelectionStart.y < groupSelectionEnd.y) {
            startY = groupSelectionStart.y;
            rectHeight = groupSelectionEnd.y - startY;
        } else {
            startY = groupSelectionEnd.y;
            rectHeight = groupSelectionStart.y - startY;
        }

        groupSelectionBounds.x = startX;
        groupSelectionBounds.y = startY;
        groupSelectionBounds.width = rectWidth;
        groupSelectionBounds.height = rectHeight;
    }

    //overridden by subclass
    protected boolean isGroupSelectable(IDraggable d) {
        return false;
    }

    //overridden by subclass
    protected void highlightNodeAndLinks(IDraggable d) {
        d.setColor(d.getSelectedColor());
        d.setBorderColor(d.getSelectedBorderColor());
    }

    //overridden by subclass
    protected void unhighlightNodeAndLinks(IDraggable d) {
        d.setColor(d.getUnselectedColor());
        d.setBorderColor(d.getUnselectedBorderColor());
    }

    final public static Color BACKGROUND_COLOR = Color.lightGray;
    final static float DASH[] = {10.0f};
    final static BasicStroke DASHED_STROKE = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER,
            10.0f, DASH, 0.0f);

    protected int                           width;
    protected int                           height;
    protected BufferedImage                 image;

    private boolean                         disableDrawing;

    protected PopupMenu                     objectPopup;
    protected PopupMenu                     backgroundPopup;
    protected PopupMenu                     groupSelectionPopup;

    protected java.util.List<IDraggable>    savedSelection;
    protected java.util.List<IDraggable>    groupSelection;
    protected boolean                       groupSelectionEnabled;
    protected boolean                       groupSelectionActive;
    protected Rectangle                     groupSelectionBounds;
    protected Point                         groupSelectionStart;
    protected Point                         groupSelectionEnd;

    protected Point                         startPos;
    protected IDraggable                    selection;
    protected Grid                          grid;

    protected java.util.List<IDraggable>    zBuffer;
}
