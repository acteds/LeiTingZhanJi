package com.areco.plane.game;

import com.areco.plane.tools.Config;
import com.areco.plane.tools.PlaySound;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

/**
 * 子弹类
 * @author aotmd
 * @version 1.0
 * @date 2020/6/21 9:06
 */
public class Bullet extends FlyObject {
    private int type,angle=-1;
    /**子弹方向*/
    private boolean direction;
    /**玩家实时坐标中心*/
    private Point playerPointCenter;
    /** 子弹加速下降*/
    private boolean speedUp=false;
    /** 大招开始时间*/
    private  long bigFire=0;
    /**用以释放大招的子弹类*/
    public List<Bullet> blist;
    /**玩家飞机*/
    private PlayerPlane playerPlane;

    public int getType() {
        return type;
    }

    /***
     * 设置玩家的坐标中心
     * @param playerPointCenter 玩家坐标中心
     */
    public void setPlayerPointCenter(Point playerPointCenter) {
        this.playerPointCenter = playerPointCenter;
    }

    /**
     * 设置玩家的坐标
     * @param playerPlane 玩家类
     */
    public void setPlayerPlane(PlayerPlane playerPlane) { this.playerPlane = playerPlane; }

    /**
     * 设置子弹集合
     * @param blist 子弹集合
     */
    public void setBlist(List<Bullet> blist) { this.blist = blist; }

    public Bullet(BufferedImage img, Point point, int width, int height, int type){
        setImg(img);
        setPoint(point);
        setHeight(height);
        setWidth(width);
        this.type=type;
        //11号子弹随机选择方向移动
        if (type==11){
            if (new Random().nextInt(2) == 0) {
                direction = true;
            } else {
                direction = false;
            }
        }
        //为大招则记录初始化时间
        if (type >= 20) {
            bigFire=System.currentTimeMillis();
        }
    }
    public Bullet(BufferedImage img, Point point,int width,int height,int type, int angle){
        setImg(img);
        setPoint(point);
        setHeight(height);
        setWidth(width);
        this.type=type;
        this.angle=angle;
    }

    /** 子弹移动方法 */
    @Override
    public void move() {
        Point point = getPoint();
        if (type == 1) {
            point.y -= Config.playerPlaneSpeed * 5;
            if (angle >= 0) {
                point.x = (int) (point.x + Math.cos(angle / 180.0 * Math.PI) * 5);
                point.y = (int) (point.y - Math.sin(angle / 180.0 * Math.PI) * -20);
            }
        } else if (type == 2) {
            point.y -= Config.playerPlaneSpeed * 1;
        } else if (type == 3) {
            point.y -= Config.playerPlaneSpeed * 8;
        } else if (type == 4) {
            point.y += Config.playerPlaneSpeed * 1;
        } else if (type == 5) {
            point.y += Config.playerPlaneSpeed * 2;
        } else if (type == 6) {
            point.y -= Config.playerPlaneSpeed * 8;
        } else if (type == 11){
            if (direction) {
                point.x -= new Random().nextInt(2);
            } else {
                point.x += new Random().nextInt(2);
            }
            point.y += Config.playerPlaneSpeed * 1;
        } else if (type == 12) {
            if (playerPointCenter.x-5>point.x){
                point.x += new Random().nextInt(2);
            } else if (playerPointCenter.x+5 < point.x) {
                point.x -= new Random().nextInt(2);
            } else {
                speedUp=true;
            }
            if (speedUp) {
                point.y+=Config.playerPlaneSpeed * 1;
            }
            point.y += Config.playerPlaneSpeed * 1;
        } else if (type == 13) {
            if (playerPointCenter.x-10>point.x){
                point.x += new Random().nextInt(5);
            } else if (playerPointCenter.x+10 < point.x) {
                point.x -= new Random().nextInt(5);
            } else {
                speedUp=true;
            }
            if (speedUp) {
                point.y+=Config.playerPlaneSpeed * 1;
            }
            point.y += Config.playerPlaneSpeed * 1;
        } else if (type == 20||type==21||type ==22) {
            //在大招释放范围内
            switch (type){
                case 20 :point.x=playerPlane.getPoint().x-40;point.y=playerPlane.getPoint().y-150;break;
                case 21 :point.x=playerPlane.getPoint().x+15;point.y=playerPlane.getPoint().y-100;break;
                case 22 :point.x=playerPlane.getPoint().x+playerPlane.getWidth()-30;point.y=playerPlane.getPoint().y-150;break;
                default:break;
            }
            //防止出界
            if (point.x<=0) {point.x=1;}
            if (point.x+getWidth()>=Config.FRAME_WIDTH) {point.x=Config.FRAME_WIDTH-getWidth()-1;}
            if (System.currentTimeMillis() - bigFire <= Config.playerDuration * 1000) {
                //如果间隔没有,调用自身的静态复制
                if (bulletInterval(Config.BULLET_INTERVAL)) {
                    new PlaySound(Config.PROJECT_RESOURCES+"destroyer_lazer3_01.mp3").start();
                    blist.add(new Bullet(getImg(), new Point(getPoint().x, getPoint().y), getWidth(), getHeight(), 6));
                }
            } else {setLive(false);}
        } else if (type == 25||type==26){
            switch (type){
                case 25 :point.x=playerPlane.getPoint().x-50;point.y=playerPlane.getPoint().y;break;
                case 26 :point.x=playerPlane.getPoint().x+playerPlane.getWidth()+15;point.y=playerPlane.getPoint().y;break;
                default:break;
            }
            //防止出界
            if (point.x<=0) {point.x=1;}
            if (point.x+getWidth()>=Config.FRAME_WIDTH) {point.x=Config.FRAME_WIDTH-getWidth()-1;}
            long spacing=System.currentTimeMillis() - bigFire;
            if ( spacing<= Config.playerDuration * 1000) {
                //如果间隔没有,调用自身的静态复制
                if (bulletInterval(Config.BULLET_INTERVAL/2)) {
                    new PlaySound(Config.PROJECT_RESOURCES+"destroyer_nuke_01.mp3").start();
                    blist.add(new Bullet(getImg(), new Point(getPoint().x, getPoint().y), getWidth(), getHeight(), 1, (int) spacing));
                }
            } else {setLive(false);}
        }
    }
    /** 最近一次射击的时间*/
    private long fireTime = System.currentTimeMillis();

    /**
     * 射击间隔限制
     * @return 是否大于间隔
     */
    private boolean bulletInterval(int m){
        long nowTime = System.currentTimeMillis();
        boolean fire= nowTime - fireTime > m;
        if (fire){
            fireTime = nowTime;
        }
        return fire;
    }
}