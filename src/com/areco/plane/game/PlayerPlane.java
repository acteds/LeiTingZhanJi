package com.areco.plane.game;

import com.areco.plane.tools.Config;
import com.areco.plane.tools.GameTools;
import com.areco.plane.tools.PlaySound;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家飞机类
 *
 * @author aotmd
 * @version 1.0
 * @date 2020/6/20 14:38
 */
public class PlayerPlane extends FlyObject {
    /** 引擎尾部效果控制 */
    private boolean engineFlag;
    /** 定义存放火焰图片 */
    private BufferedImage engineImg = GameTools.getImageMap().get("p-f02.png");
    /** 移动标识*/
    private boolean up,down,left,right;
    /**飞机血量*/
    private int bloodVolume= Config.playerBloodVolume;
    /**窗口显示的血条宽度*/
    private int lastHP=150;
    /** 子弹类*/
    public List<Bullet> blist= new ArrayList<>();
    /** 子弹图片*/
    private BufferedImage bulletImg = GameTools.getImageMap().get("img_bullet.png");

    /**
     * 初始化飞机
     * @param img 飞机
     * @param point 坐标
     * @param width 宽度
     * @param height 高度
     */
    public PlayerPlane(BufferedImage img, Point point,int width,int height) {
        setImg(img);
        setPoint(point);
        setHeight(height);
        setWidth(width);
    }
    public int getLastHP() { return lastHP; }

    /**
     * 设置之前的血量条宽度
     * @param lastHP 之前的血量
     */
    public void setLastHP(int lastHP) {this.lastHP=lastHP;}
    public int getBloodVolume() {
        return bloodVolume;
    }
    public void setBloodVolume(int bloodVolume) {
        this.bloodVolume = bloodVolume;
    }

    /** 最近一次被击中的时间*/
    private long  hitTime = System.currentTimeMillis();
    /**
     * 无敌时间
     * @return 是否是无敌时间
     */
    private boolean invincible(){
        long nowTime = System.currentTimeMillis();
        boolean fire= nowTime - hitTime < Config.INVINCIBLE_TIME;
        if (!fire){
            hitTime = nowTime;
        }
        return fire;
    }
    /**玩家被击中*/
    @Override
    public void setLive(boolean live) {
        //为无敌时间则不掉血
        if (invincible()) {
            System.out.println("玩家处于被攻击后的无敌时间,免疫此次攻击");
            return;
        }
        //记录扣血前的血条
        lastHP=150*bloodVolume/ Config.playerBloodVolume;
        if (bloodVolume == 1) {
            //为了死亡后上方血量显示为0
            bloodVolume=0;
            super.setLive(live);
        } else {
            bloodVolume--;
        }
    }

    /**玩家飞机移动方法*/
    @Override
    public void move() {
        Point point=getPoint();
        if(up&&point.y>0){
            point.y-= Config.playerPlaneSpeed;
            System.out.println("上");
        }
        if(down&&point.y< Config.FRAME_HEIGHT- Config.playerPlaneHeight){
            point.y+= Config.playerPlaneSpeed;
            System.out.println("下");
        }
        if (left && point.x > 0) {
            point.x-= Config.playerPlaneSpeed;
            System.out.println("左");
        }
        if (right && point.x < Config.FRAME_WIDTH- Config.playerPlaneWidth) {
            point.x+= Config.playerPlaneSpeed;
            System.out.println("右");
        }
    }

    /**处理键盘按下和释放的方法
     *
     * @param e 事件源
     * @param playerRecharge 玩家大招充能
     */
    public void keyPressed(KeyEvent e, int playerRecharge){
        int keyCode=e.getKeyCode();
        switch (keyCode){
            case KeyEvent.VK_UP : up=true;break;
            case KeyEvent.VK_DOWN : down=true;break;
            case KeyEvent.VK_LEFT : left=true;break;
            case KeyEvent.VK_RIGHT : right=true;break;
            case KeyEvent.VK_A : missileFire();break;
            case KeyEvent.VK_S : superFire();break;
            case KeyEvent.VK_D : laserFire();break;
            case KeyEvent.VK_F : bigFire(playerRecharge);break;
            default:break;
        }
        move();
    }
    public void keyReleased(KeyEvent e){
        int keyCode=e.getKeyCode();
        switch (keyCode){
            case KeyEvent.VK_UP : up=false;break;
            case KeyEvent.VK_DOWN : down=false;break;
            case KeyEvent.VK_LEFT : left=false;break;
            case KeyEvent.VK_RIGHT : right=false;break;
            default:break;
        }
    }

    /**处理鼠标移动方法
     * @param x x方向的位置
     * @param y y方向的位置
     */
    public void mouseMoved(int x,int y){
        Point point=getPoint();
        point.x=x- Config.playerPlaneHeight/2-20;
        point.y=y- Config.playerPlaneWidth/2+20;
    }

    /** 绘制飞机,火焰效果,子弹 @param g */
    @Override
    public void draw(Graphics g) {
        if (isLive()) {
            //飞机绘制
            super.draw(g);
            //子弹效果绘制
            for (int i=0;i<blist.size();i++){
                Bullet bullet= blist.get(i);
                Point point=bullet.getPoint();
                if (!bullet.isLive()||point.y+bullet.getHeight()<0||point.x+bullet.getWidth()<0||point.y> Config.FRAME_HEIGHT||point.x> Config.FRAME_WIDTH){
                    blist.remove(i);
                } else {
                    bullet.setPlayerPlane(this);
                    bullet.setBlist(blist);
                    bullet.draw(g);
                }
            }
            //火焰效果
            if (engineFlag) {
                g.drawImage(engineImg.getSubimage(0, 0, 21, 50), getPoint().x + 57, getPoint().y + 80, null);
            } else {
                g.drawImage(engineImg.getSubimage(21, 0, 21, 50), getPoint().x + 57, getPoint().y + 85, null);
            }
            engineFlag=!engineFlag;
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

    /** 加强型射击 */
    public void superFire(){
        if (!bulletInterval(Config.BULLET_INTERVAL)) {return;}
        new PlaySound(Config.PROJECT_RESOURCES+"laser.mp3").start();
        Point point=getPoint();
        for (int i=0;i<2;i++){
            int angle=i*180;
            blist.add(new Bullet(bulletImg.getSubimage(334,172,16,32),new Point(point.x+59+55,point.y-25),16,32,1,angle));
            blist.add(new Bullet(bulletImg.getSubimage(334,172,16,32),new Point(point.x+59-55,point.y-25),16,32,1,angle));
            blist.add(new Bullet(bulletImg.getSubimage(334,172,16,32),new Point(point.x+59+22,point.y-25),16,32,1,angle));
            blist.add(new Bullet(bulletImg.getSubimage(334,172,16,32),new Point(point.x+59-22,point.y-25),16,32,1,angle));
        }
    }

    /** 导弹射击 */
    public  void missileFire(){
        if (!bulletInterval(Config.BULLET_INTERVAL*3)) {return;}
        new PlaySound(Config.PROJECT_RESOURCES+"destroyer_nuke_01.mp3").start();
        Point point=getPoint();
        blist.add(new Bullet(bulletImg.getSubimage(660, 678, 23, 69), new Point(point.x + 59, point.y - 60), 23, 69, 2));
    }
    /** 激光射击*/
    public void laserFire(){
        if (!bulletInterval((int) (Config.BULLET_INTERVAL*2.5))) {return;}
        new PlaySound(Config.PROJECT_RESOURCES+"destroyer_lazer3_01.mp3").start();
        Point point=getPoint();
        blist.add(new Bullet(bulletImg.getSubimage(496, 605, 73, 131), new Point(point.x-20 , point.y - 150), 73, 131, 3));
        blist.add(new Bullet(bulletImg.getSubimage(496, 605, 73, 131), new Point(point.x+getWidth()-50 , point.y - 150), 73, 131, 3));
    }

    /** 大招
     * @param playerRecharge 大招充能条
     */
    public void bigFire(int playerRecharge){
        if (playerRecharge == Config.playerChargingWidth) {
            //防止出界
            Point point=new Point(getPoint().x,getPoint().y);
            if (point.x-40<=0){
                point.x=40+1;
            }
            if (point.x + getWidth() - 30 >= Config.FRAME_WIDTH) {
                point.x=Config.FRAME_WIDTH-getWidth()-30-1;
            }
            blist.add(new Bullet(bulletImg.getSubimage(660, 682, 23, 65), new Point(point.x , point.y ), 23, 65, 25));
            blist.add(new Bullet(bulletImg.getSubimage(496, 605, 73, 131), new Point(point.x -40 , point.y - 150), 73, 131, 20));
            blist.add(new Bullet(bulletImg.getSubimage(772, 360, 100, 108), new Point(point.x+15 , point.y - 100), 100, 108, 21));
            blist.add(new Bullet(bulletImg.getSubimage(496, 605, 73, 131), new Point(point.x+getWidth()-30 , point.y - 150), 73, 131, 22));
            blist.add(new Bullet(bulletImg.getSubimage(660, 682, 23, 65), new Point(point.x+getWidth()-35 , point.y ), 23, 65, 26));
        }
    }
    /**增加方法判断子弹是否攻击了敌机*/
/*    public void hitAnEnemyPlane(List<EnemyPlane> enemyPlaneList) {
        for (Bullet bullet : blist) {
            Rectangle bulletRectangle=bullet.getRectangle();
            for (EnemyPlane enemyPlane : enemyPlaneList) {
                Rectangle rectangle=enemyPlane.getRectangle();
                if (enemyPlane.isLive()&&bulletRectangle.intersects(rectangle)) {
                    enemyPlane.setLive(false);
                    bullet.setLive(false);
                    System.out.println("敌人被击中");
                    break;
                }
            }
        }
    }*/
}
