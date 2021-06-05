package com.areco.plane.game;

import com.areco.plane.tools.Config;
import com.areco.plane.tools.GameTools;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 敌人飞机类
 * @author aotmd
 * @version 1.0
 * @date 2020/6/21 14:08
 */
public class EnemyPlane extends FlyObject {
    /** 飞机移动速度*/
    private int spendX,spendY;
    /**飞机血量*/
    private int bloodVolume;
    /**敌机子弹集合*/
    public List<Bullet> bulletList=new ArrayList<>();
    /**子弹图片*/
    private  BufferedImage bufferedImage= GameTools.getImageMap().get("ballBlue.gif");
    /**高智商飞机标志*/
    private boolean highIQ=false;
    /**玩家坐标中心*/
    private Point playerPoint;
    @Override
    public void setLive(boolean live) {
        if (bloodVolume == 1) {
            super.setLive(live);
        } else {
            bloodVolume--;
        }
    }

    /**用以获得boss血量
     * @return 血量
     */
    public int getBloodVolume() {
        return bloodVolume;
    }
    /**boss所在关卡*/
    private int level;
    /**是否为boss*/
    private boolean boss=false;

    public boolean isBoss() {
        return boss;
    }

    /**
     * boss初始化
     * @param img 飞机图片
     * @param point 飞机坐标
     * @param width 飞机宽度
     * @param height 飞机高度
     * @param level 第几关boss
     */
    public EnemyPlane(BufferedImage img, Point point, int width, int height, int level){
        setImg(img);
        setPoint(point);
        setHeight(height);
        setWidth(width);
        this.level=level;
        boss=true;
        highIQ=true;
        spendX++;
        if (level>=4){
            spendX++;
        }
        bloodVolume=Config.BOSS_BLOOD_VOLUME[level-1];
        bufferedImage= GameTools.getImageMap().get("img_bullet_180.png");
    }

    /***
     * 普通敌机血量初始化
     * @param level 关卡等级
     * @param img 飞机图片
     * @param point 飞机坐标
     * @param width 飞机宽度
     * @param height 飞机高度
     */
    public EnemyPlane(int level,BufferedImage img, Point point, int width, int height){
        this.level=level;
        bloodVolume= (int) (0.5*(level-1)*Config.ENEMY_BLOOD_VOLUME+Config.ENEMY_BLOOD_VOLUME);
        setImg(img);
        setPoint(point);
        setHeight(height);
        setWidth(width);
        Random random = new Random();
        spendX= random.nextInt(3)-1;
        spendY= random.nextInt(2)+2;
        //当走直线时加速移动
        if (spendY==2&&spendX==0){
            spendY++;
        }
        //高智商判断
        if (random.nextInt(3) == 0) {
            highIQ=true;
            spendX++;
        }
    }
    /** 移动类 */
    @Override
    public void move() {
        Point point = getPoint();
        point.y+=spendY;
        point.x+=spendX;
        //当到达地图边界时反向移动
        if (point.x<=0||point.x> Config.FRAME_WIDTH-getWidth()){
            spendX=-spendX;
        }
        //间歇性随机移动
        if (boss){
            if (new Random().nextInt(Config.FPS*Config.BOSS_FAST_MOBILE)==0) {
                highIQ=!highIQ;
                if (!highIQ){
                    if (spendX >= 0) {
                        spendX++;
                    } else {
                        spendX--;
                    }
                } else {
                    if (spendX > 0) {
                        spendX--;
                    } else {
                        spendX++;
                    }
                }
            }
        }
        //自动开火
        autoFire();
    }

    /**高智商跟踪型移动,以及跟踪子弹初始化
     * @param playerPlane 玩家飞机类
     */
    public void trackMove(PlayerPlane playerPlane){
        if (!highIQ) {return;}
        //初始化玩家中心坐标
        playerPoint=new Point(playerPlane.getPoint().x+playerPlane.getWidth()/2,playerPlane.getPoint().y+playerPlane.getHeight()/2);
        final Point point=playerPlane.getPoint();
        final int width=playerPlane.getWidth();
        if (getPoint().x+getWidth()/2<=point.x&&spendX<0){
            spendX=-spendX;
        }
        if (getPoint().x+getWidth()/2>=point.x+width&&spendX>0){
            spendX=-spendX;
        }
    }

    /** 自动开火 */
    public void autoFire() {
        Random random = new Random();
        //开火频率有效性判定
        int firingFrequency;
        if (Config.ENEMY_FIRE_FREQUENCY <= 0) {
            firingFrequency = 1 << 16;
        } else {
            firingFrequency= Config.FPS/ Config.ENEMY_FIRE_FREQUENCY;
        }
        //增加45关射击频率
        if (level>2){firingFrequency/=level-2;}
        if (firingFrequency == 0) {firingFrequency=1;}
        //如果敌机已在界面中出现
        if (getPoint().y>0&&random.nextInt(firingFrequency)==0&&!boss){
            bulletList.add(new Bullet(bufferedImage,new Point(getPoint().x+getWidth()/2-5,getPoint().y+getHeight()),20,20,4));
        }
        //boss子弹生成
        if (boss){
            switch (level) {
                case 1:
                    if (random.nextInt(firingFrequency)<=1) {
                        if (random.nextInt(2)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(264,242,30,108),new Point(getPoint().x+20,getPoint().y+getHeight()-25),30,108,4));
                        }
                        if (random.nextInt(2)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(264,242,30,108),new Point(getPoint().x+getWidth()-50,getPoint().y+getHeight()-25),30,108,4));
                        }
                        if (random.nextInt(8)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(668,0,57,182),new Point(getPoint().x+getWidth()/2-25,getPoint().y+getHeight()-25),57,182,5));
                        }
                    }
                    break;
                case 2:
                    if (random.nextInt(firingFrequency)<=1) {
                        if (random.nextInt(2)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(183,268,68,68),new Point(getPoint().x-20,getPoint().y+getHeight()-50),68,68,11));
                        }
                        if (random.nextInt(2)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(183,268,68,68),new Point(getPoint().x+getWidth()-40,getPoint().y+getHeight()-50),68,68,11));
                        }
                        if (random.nextInt(32)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(426,0,128,104),new Point(getPoint().x+getWidth()/2-60,getPoint().y+getHeight()-25),57,182,5));
                        }
                    }
                    break;
                case 3:
                    if (random.nextInt(firingFrequency)<=2) {
                        if (random.nextInt(2)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(849,547,19,56),new Point(getPoint().x+10,getPoint().y+getHeight()-30),19,56,11));
                        }
                        if (random.nextInt(2)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(849,547,19,56),new Point(getPoint().x+getWidth()-30,getPoint().y+getHeight()-30),19,56,11));
                        }
                        if (random.nextInt(32)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(483,107,73,132),new Point(getPoint().x+getWidth()/2-35,getPoint().y+getHeight()-25),73,132,5));
                        }
                    }
                    break;
                case 4:
                    if (random.nextInt(firingFrequency)<=1) {
                        if (random.nextInt(2)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(254,963,17,59),new Point(getPoint().x+10,getPoint().y+getHeight()-50),17,59,12));
                        }
                        if (random.nextInt(2)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(254,963,17,59),new Point(getPoint().x+getWidth()-30,getPoint().y+getHeight()-50),17,59,12));
                        }
                        if (random.nextInt(2)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(689,800,53,52),new Point(getPoint().x+getWidth()/2-30,getPoint().y+getHeight()),53,52,11));
                        }
                    }
                    break;
                case 5:
                    if (random.nextInt(firingFrequency)<=1) {
                        if (random.nextInt(2)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(257,762,56,66),new Point(getPoint().x-10,getPoint().y+getHeight()-60),56,66,13));
                            bulletList.add(new Bullet(bufferedImage.getSubimage(542,655,64,65),new Point(getPoint().x+30,getPoint().y+getHeight()-60),64,65,11));
                        }
                        if (random.nextInt(2)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(542,655,64,65),new Point(getPoint().x+getWidth()-95,getPoint().y+getHeight()-60),64,65,11));
                            bulletList.add(new Bullet(bufferedImage.getSubimage(257,762,56,66),new Point(getPoint().x+getWidth()-50,getPoint().y+getHeight()-60),56,66,13));
                        }
                        if (random.nextInt(32)==0){
                            bulletList.add(new Bullet(bufferedImage.getSubimage(898,778,126,246),new Point(getPoint().x+getWidth()/2-60,getPoint().y+getHeight()-150),53,52,5));
                        }
                    }
                    break;
                default:break;
            }
        }
    }
    /**重写,先画子弹*/
    @Override
    public void draw(Graphics g) {
        for (int i=0;i<bulletList.size();i++){
            Bullet bullet = bulletList.get(i);
            Point point=bullet.getPoint();
            //子弹出界,或死亡
            if (point.y > Config.FRAME_HEIGHT || !bullet.isLive()) {
                bulletList.remove(i);
            } else {
                //设置玩家坐标中心
                bullet.setPlayerPointCenter(playerPoint);
                bullet.draw(g);
            }
        }
        super.draw(g);
    }
}
