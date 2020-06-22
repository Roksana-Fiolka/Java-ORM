package com.ksiegarnia;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class CheckBoxList extends JList {
    protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);

    public CheckBoxList() {
        setCellRenderer(new CellRenderer());
        addMouseListener(getL());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private MouseAdapter getL() {
        return new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());

                if (index != -1) {
                    JCheckBox checkbox = (JCheckBox)
                            getElementAt(index);
                    getaVoid(checkbox);
                    repaint();
                                 }
                             }
                         };
    }

    private void getaVoid(JCheckBox checkbox) {
        checkbox.setSelected(
                !checkbox.isSelected());
    }

    private Object getElementAt(int index) {
        return getModel().getElementAt(index);
    }

    protected class CellRenderer implements ListCellRenderer {
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JCheckBox checkbox = (JCheckBox) value;
            checkbox.setBackground(isSelected ?
                    getSelectionBackground() : getBackground());
            checkbox.setForeground(isSelected ?
                    getSelectionForeground() : getForeground());
            checkbox.setEnabled(isEnabled());
            checkbox.setFont(getFont());
            checkbox.setFocusPainted(false);
            checkbox.setBorderPainted(true);
            checkbox.setBorder(isSelected ?
                    UIManager.getBorder(
                            "List.focusCellHighlightBorder") : noFocusBorder);
            return checkbox;
        }
    }
}
