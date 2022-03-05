package com.pd.modelcg.codegen.graphics.draggablecanvas;

import com.pd.modelcg.codegen.graphics.drawablecanvas.mousable.IMousable;

import java.awt.*;

public interface ISelectable extends IMousable {
    boolean isSelected();
    void select();
    void unselect();

    Color getSelectedColor();
    Color getUnselectedColor();
    void setSelectedColor(Color color);
    void setUnselectedColor(Color color);

    Color getSelectedBorderColor();
    Color getUnselectedBorderColor();
    void setSelectedBorderColor(Color color);
    void setUnselectedBorderColor(Color color);
}
