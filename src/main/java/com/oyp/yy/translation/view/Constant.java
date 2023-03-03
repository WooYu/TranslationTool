package com.oyp.yy.translation.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;

/**
 * <p>date: Created by A18086 on 2019/10/17.</p>
 * <p>desc: </p>
 */
public class Constant {

    public static final int H_GAP = 6;
    public static final int V_GAP = 6;
    public static final int MARGIN = 6;
    public static final int PADDING = 6;
    public static final int WINDOW_WIDTH = 800;
    public static final int ROW = 7;
    public static final int WINDOW_HEIGHT = 420;   // ROW * 60

    public static final int PANEL_HEIGHT = 60;
    public static final Dimension DIMENSION_LABEL = new Dimension(140, PANEL_HEIGHT);
    public static final Dimension DIMENSION_BUTTON = new Dimension(80, PANEL_HEIGHT);
    public static final Dimension DIMENSION_PANEL = new Dimension(WINDOW_WIDTH, PANEL_HEIGHT);
    public static final Border BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);

    public static final String FONT_NAME = "Microsoft JhengHei";
    public static final int FONT_SIZE_16 = 16;
    public static final int FONT_SIZE_18 = 18;
    public static final Font FONT16 = new Font(FONT_NAME, Font.BOLD, FONT_SIZE_16);
    public static final Font FONT18 = new Font(FONT_NAME, Font.BOLD, FONT_SIZE_18);

    public static final String DEFAULT_PATH = FileSystemView.getFileSystemView().getHomeDirectory().getPath();

    public static final int FUN_TYPE_ADD = 1;
    public static final int FUN_TYPE_GENERATE = 2;
    public static final int FUN_TYPE_MERGE = 3;
    public static final int FUN_TYPE_COMPARE = 4;

    public static final int PROJECT_FIRST = 1;
    public static final int PROJECT_SECOND = 2;

}
