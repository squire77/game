package org.pdi.game;

import com.pd.modelcg.codegen.graphics.drawablecanvas.DrawableCanvas;

import javax.swing.*;
import java.awt.*;

public class GameConsole extends JFrame {
    public GameConsole(Game game) {
        super(FRAME_TITLE);

        setLayout(null);
        Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenDimensions.width - DEFAULT_FRAME_SIZE_WIDTH)/2, (screenDimensions.height - DEFAULT_FRAME_SIZE_HEIGHT)/2);

        //https://stackoverflow.com/questions/1783793/java-difference-between-the-setpreferredsize-and-setsize-methods-in-compone
        Dimension defaultSize = new Dimension(DEFAULT_FRAME_SIZE_WIDTH, DEFAULT_FRAME_SIZE_HEIGHT);
        setSize(defaultSize);
        setPreferredSize(defaultSize);

        canvas = new DrawableCanvas();
        canvas.init();
        setCanvasSize(defaultSize);
        getContentPane().add(canvas);

//        addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent event) {
//                setCanvasSize(new Dimension(getWidth(), getHeight()));
//                repaint();
//            }
//        });

        game.setup(canvas);

        canvas.draw();
    }

    private void printDimensions() {
        if (canvas != null) {
            System.out.println("Canvas size: " + new Dimension(canvas.getWidth(), canvas.getHeight()));
        }
        System.out.println("Console size: " + new Dimension(this.getWidth(), this.getHeight()));
    }

    private void setCanvasSize(Dimension canvasSize) {
        canvas.setSize(canvasSize);
        canvas.setPreferredSize(canvasSize);
        canvas.revalidate();
        //printDimensions();
    }

    private static final String         FRAME_TITLE = "Space Invaders";
    private static final int            DEFAULT_FRAME_SIZE_WIDTH = 800;
    private static final int            DEFAULT_FRAME_SIZE_HEIGHT = 690;

    private final DrawableCanvas        canvas;
}
