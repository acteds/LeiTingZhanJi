package com.areco.plane.tools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 工具类
 *
 * @author aotmd
 * @version 1.0
 * @date 2020/6/20 9:05
 */
public class GameTools {
    private static Map<String, BufferedImage> imageMap = new HashMap<>(16);
    /**
     * 获取图片资源Map
     *
     * @return map
     */
    public static Map<String, BufferedImage> getImageMap() {
        if (imageMap == null || imageMap.isEmpty()) {
            imageMap = new HashMap<>(16);
            initialization();
        }
        return imageMap;
    }
    public static void initialization() {
        File file = new File(Config.PROJECT_RESOURCES);
        readFile(file);
    }
    /**
     * 初始化资源文件
     *
     * @param file 文件或文件路径
     */
    private static void readFile(File file) {
        if (file.isDirectory()) {
            File[] listFile = file.listFiles();
            for (File f : listFile) {
                readFile(f);
            }
        } else {
            String fileName = file.getName();
            int lastPoint=fileName.lastIndexOf(".");
            lastPoint=lastPoint!=-1?lastPoint:0;
            String type = fileName.substring(lastPoint);
            if (".jpg".equalsIgnoreCase(type) || ".gif".equalsIgnoreCase(type) || ".png".equalsIgnoreCase(type)) {
                try {
                    imageMap.put(fileName, ImageIO.read(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(file.getName());
            }
        }
    }

    /** 窗体字体设置 */
    public static void setUIFont() {
        Font f = new Font("宋体", Font.PLAIN, 12);
        String names[] = {"Label", "CheckBox", "PopupMenu", "MenuItem", "CheckBoxMenuItem",
                "JRadioButtonMenuItem", "ComboBox", "Button", "Tree", "ScrollPane",
                "TabbedPane", "EditorPane", "TitledBorder", "Menu", "TextArea",
                "OptionPane", "MenuBar", "ToolBar", "ToggleButton", "ToolTip",
                "ProgressBar", "TableHeader", "Panel", "List", "ColorChooser",
                "PasswordField", "TextField", "Table", "Label", "Viewport",
                "RadioButtonMenuItem", "RadioButton", "DesktopPane", "InternalFrame"
        };
        for (String item : names) {
            UIManager.put(item + ".font", f);
        }
    }

    /**
     * 绘制清楚的文本
     * @param mainImageGs 2D画笔
     * @param txt 要绘制的文本
     * @param x 绘制的x位置
     * @param y 绘制的y位置
     * @param color 绘制的颜色
     * @param font 要绘制的文本的字体
     */
    public static void drawClearText(Graphics2D mainImageGs,String txt,int x,int y,Color color,Font font) {
        Graphics2D g2d= mainImageGs;
        g2d.setFont(font);
        //设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //阴影颜色
        g2d.setPaint(new Color(0, 0, 0, 64));
        //先绘制阴影
        g2d.drawString(txt, x, y);
        //正文颜色
        g2d.setPaint(color);
        //用正文颜色覆盖上去
        g2d.drawString(txt, x, y);
    }
}
