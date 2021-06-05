package com.areco.plane.game;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 抽象类
 * @author aotmd
 * @version 1.0
 * @date 2020/6/20 14:31
 */
public abstract class FlyObject {
    /**飞机坐标*/
    private Point point;
    /**飞机宽度*/
    private int width;
    /**飞机高度*/
    private int height;
    /**飞机图片*/
    private BufferedImage img;
    /**飞机是否存活*/
    private boolean live=true;
    public Point getPoint() { return point; }public void setPoint(Point point) { this.point = point; }public int getWidth() { return width; }public void setWidth(int width) { this.width = width; }public int getHeight() { return height; }public void setHeight(int height) { this.height = height; }public BufferedImage getImg() { return img; }public void setImg(BufferedImage img) { this.img = img; }public boolean isLive() { return live; }public void setLive(boolean live) { this.live = live; }
    /**飞机移动方法*/
    public abstract void move();
    public void draw(Graphics g){
        if (live) {
            g.drawImage(img,point.x,point.y,null);
            move();
        }
    }
   /** 确定子弹和敌机的位置,确定两者的交集*/
/*    public Rectangle getRectangle(){
        //内缩,移去透明边
        return new Rectangle(point.x+10,point.y+10,width-10,height-10);
    }*/
}
