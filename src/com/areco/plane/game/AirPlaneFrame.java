package com.areco.plane.game;

import com.areco.plane.tools.BackgroundMusic;
import com.areco.plane.tools.Config;
import com.areco.plane.tools.GameTools;
import com.areco.plane.tools.PlaySound;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.*;
import java.util.List;

/**
 * 主窗体类
 * @author aotmd
 * @date 2020-6-20 8:45:00
 */
public class AirPlaneFrame extends JFrame {
    /** 游戏绘制的缓冲图 */
    private Image mainImage;
    /** 地图 */
    private BufferedImage mapImage;
    /** 设置关卡 */
    private int level=1;
    /** 设置地图的坐标 */
    private Point mapPoint=new Point(0,-68);
    /**玩家飞机图片*/
    private BufferedImage playerPlaneImage;
    /**玩家飞机类*/
    private PlayerPlane playerPlane;
    /** 敌机飞机图片*/
    private BufferedImage enemyPlaneImage;
    /** 敌机飞机类类*/
    private List<EnemyPlane> enemyPlaneList= new ArrayList<>();
    /** 爆炸集 */
    private List<Explosion> explosionList =new ArrayList<>();
    /** 玩家得分*/
    private int score=0;
    /** 玩家大招充能*/
    private int playerRecharge=0;
    /** 玩家大招上一次增加之前的充能*/
    private int lastPlayerRecharge=0;
    /** 玩家开启大招时,得到到的能量将被缓冲,缓冲标记*/
    private boolean bigEnergyBuffer=false;
    /** 玩家开启大招时无敌的临时变量*/
    private int playerInvincible;
    /** 缓冲能量*/
    private int bufferEnergy=0;
    /** 记录玩家开启大招的时间*/
    private long bigFire=0;
    /**玩家血量闪烁间隔*/
    private int flicker=0;
    /** 关卡开始时间*/
    private long timing=System.currentTimeMillis();
    /** 特殊boss出现*/
    private boolean boss=false;
    /** boss血量*/
    private int bossBloodVolume;

    /**
     * 程序入口
     * @param args 命令行参数
     */
    public static void main(String[] args) {

        GameTools.setUIFont();
        new AirPlaneFrame();
    }
    /**构造方法*/
    public AirPlaneFrame() throws HeadlessException {
        this.setTitle("雷霆战机");
        this.setSize(Config.FRAME_WIDTH, Config.FRAME_HEIGHT);
        //窗口位置居中
        this.setLocationRelativeTo(null);
        //禁止调整窗口大小
        this.setResizable(false);
        //设置用户在此框架上启动关闭时默认执行的操作。
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //初始化地图
        mapImage=GameTools.getImageMap().get("img_bg_level_"+level+".jpg");
        //初始化boss血量
        bossBloodVolume=Config.BOSS_BLOOD_VOLUME[level-1];
        //初始化玩家飞机
        initializePlayerPlane();
        //初始化敌人飞机
        initializeEnemyPlane();
        //开始绘制图形
        new PaintThread().start();
        //开启声音循环
        new BackgroundMusic(Config.PROJECT_RESOURCES+"action_world1_01.mp3").start();
        //键盘事件注册
        addKeyListener(new KeyListener() {
            @Override public void keyTyped(KeyEvent keyEvent) { }
            @Override public void keyPressed(KeyEvent keyEvent) {
                //是移动按钮
                boolean moveButton=false;
                int keyCode=keyEvent.getKeyCode();
                switch (keyCode){
                    case KeyEvent.VK_UP :
                    case KeyEvent.VK_DOWN :
                    case KeyEvent.VK_LEFT :
                    case KeyEvent.VK_RIGHT :moveButton=true;break;
                    default:break;
                }
                //玩家释放大招时只能移动,并且大招释放完毕后也有固定的冷却时间(默认冷却时间为大招的时间)
                if (System.currentTimeMillis()-bigFire>Config.playerDuration*2*1000||moveButton) {
                    playerPlane.keyPressed(keyEvent,playerRecharge);
                    if (keyEvent.getKeyCode()== KeyEvent.VK_F&&playerRecharge==Config.playerChargingWidth) {
                        playerRecharge=0;
                        bufferEnergy=0;
                        bigEnergyBuffer=true;
                        bigFire=System.currentTimeMillis();
                        playerInvincible=Config.playerPlaneNarrow;
                        Config.playerPlaneNarrow=500;
                    }
                }
            }
            @Override public void keyReleased(KeyEvent keyEvent) { playerPlane.keyReleased(keyEvent); }
        });
        //鼠标移动事件注册
        addMouseMotionListener(new MouseMotionListener() {
            @Override public void mouseDragged(MouseEvent mouseEvent) { this.mouseMoved(mouseEvent); }
            @Override public void mouseMoved(MouseEvent mouseEvent) {
                playerPlane.mouseMoved(mouseEvent.getX(),mouseEvent.getY());
            }
        });
        //窗口可见性
        this.setVisible(true);
    }


    /** 初始化敌机 */
    private void initializeEnemyPlane() {
        enemyPlaneImage=GameTools.getImageMap().get("img_plane_enemy.png");
        Random random=new Random();
        for (int i = 0; i < Config.NUMBER_OF_ENEMY_AIRCRAFT+(level-1)*Config.GROWTH_RATE; i++) {
           int ex=random.nextInt(Config.FRAME_WIDTH-101-1);
           int ey=-random.nextInt(Config.FRAME_HEIGHT)-78;
            randomAircraft(ex, ey);
        }
    }
    /**敌机类型随机初始化*/
    private void randomAircraft(int ex, int ey) {
        switch (new Random().nextInt(3+level)+level-1){
            case 0 :enemyPlaneList.add(new EnemyPlane(level,enemyPlaneImage.getSubimage(265, 474, 101, 78), new Point(ex, ey), 101, 78));break;
            case 1 :enemyPlaneList.add(new EnemyPlane(level,enemyPlaneImage.getSubimage(162, 476, 103, 73), new Point(ex, ey), 103, 73));break;
            case 2 :enemyPlaneList.add(new EnemyPlane(level,enemyPlaneImage.getSubimage(2, 483, 100, 74), new Point(ex, ey), 100, 74));break;
            case 3 :enemyPlaneList.add(new EnemyPlane(level,enemyPlaneImage.getSubimage(3, 559, 89, 80), new Point(ex, ey), 89, 80));break;
            case 4 :enemyPlaneList.add(new EnemyPlane(level,enemyPlaneImage.getSubimage(277, 552, 70, 62), new Point(ex, ey), 70, 62));break;
            case 5 :enemyPlaneList.add(new EnemyPlane(level,enemyPlaneImage.getSubimage(367, 440, 113, 81), new Point(ex, ey), 113, 81));break;
            case 6 :enemyPlaneList.add(new EnemyPlane(level,enemyPlaneImage.getSubimage(366, 523, 100, 77), new Point(ex, ey), 100, 77));break;
            case 7 :enemyPlaneList.add(new EnemyPlane(level,enemyPlaneImage.getSubimage(104, 551, 99, 70), new Point(ex, ey), 99, 70));break;
            default:break;
        }
    }

    /**玩家飞机初始化*/
    private void initializePlayerPlane() {
        playerPlaneImage=GameTools.getImageMap().get("plane_player.png");
        playerPlane=new PlayerPlane(playerPlaneImage.getSubimage(0,0, Config.playerPlaneWidth, Config.playerPlaneHeight),new Point((Config.FRAME_WIDTH- Config.playerPlaneWidth)/2,550), Config.playerPlaneWidth, Config.playerPlaneHeight);
        //移动鼠标到特定位置
        // 屏幕尺寸规格
        Dimension di = Toolkit.getDefaultToolkit().getScreenSize();
        try { new Robot().mouseMove(di.width/2,  di.height/2+245); } catch (AWTException e) { e.printStackTrace(); }
        //隐藏鼠标指针
        Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(0, 0, new int[0], 0, 0));
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(image,new Point(0, 0), null));
    }
    /** 玩家子弹击中敌机*/
    private void hitAnEnemyPlane(){
        List<Bullet> blist = playerPlane.blist;
        for (Bullet bullet : blist) {
            Point point = bullet.getPoint();
            int width = bullet.getWidth();
            int height = bullet.getHeight();
            for (EnemyPlane enemyPlane : enemyPlaneList) {
                Point pointE = enemyPlane.getPoint();
                int widthE = enemyPlane.getWidth();
                int heightE = enemyPlane.getHeight();
                if (enemyPlane.isLive()
                        && pointE.y + heightE >= point.y + Config.ENEMY_PLANE_NARROW
                         && pointE.y<=point.y+height - Config.ENEMY_PLANE_NARROW
                        && pointE.x+widthE >= point.x + Config.ENEMY_PLANE_NARROW
                        && pointE.x <= point.x+width - Config.ENEMY_PLANE_NARROW) {
                    bulletAttackPower(enemyPlane,bullet.getType());
                    //如果只有boss则获取血量并显示出来
                    if (boss&&enemyPlaneList.size()==1) {
                        bossBloodVolume=enemyPlane.getBloodVolume();
                    }
                    //为激光武器则穿透
                    switch (bullet.getType()) {
                        case 3: case 6: case 20: case 21: case 22: case 25: case 26: break;
                        default:bullet.setLive(false);
                    }
                    //敌人已被摧毁
                    enemyIsDestroyed(enemyPlane);
                    break;
                }
            }
        }
        //玩家大招时间结束,将缓冲的分数重新写入,加了额外时间让子弹飞出
        if (System.currentTimeMillis()-bigFire>Config.playerDuration*1000+1100&&bigEnergyBuffer){
            bigEnergyBuffer=false;
            playerRecharge+=bufferEnergy;
            bufferEnergy=0;
            Config.playerPlaneNarrow=playerInvincible;
        }
    }
    /**敌人被摧毁的各项数值判定*/
    private void enemyIsDestroyed(EnemyPlane enemyPlane) {
        if (!enemyPlane.isLive()) {
            //击败boss
            if (enemyPlane.isBoss()) {
                boss=false;
                score+= Config.BOSS_SCORE;
                playerPlane.setBloodVolume(playerPlane.getBloodVolume()+10);
                level++;
                timing=System.currentTimeMillis();
                Config.playerBloodVolume+=Config.BOSS_REWARD;
                Config.BULLET_INTERVAL-=20;
                mapImage= GameTools.getImageMap().get("img_bg_level_"+level+".jpg");
                Config.INVINCIBLE_TIME+=300;
                if (level != 6) {
                    bossBloodVolume=Config.BOSS_BLOOD_VOLUME[level-1];
                }
            }
            new PlaySound(Config.PROJECT_RESOURCES+"explosion4_01.mp3").start();
            score++;
            //玩家飞机充能
            if (playerRecharge<Config.playerChargingWidth){
                //当玩家使用大招时,用不同的方式处理
                if (!bigEnergyBuffer) {
                    //记录之前的充能
                    lastPlayerRecharge=playerRecharge;
                    if (enemyPlane.isBoss()) {
                        playerRecharge += Config.BOSS_SCORE*Config.playerChargingWidth/Config.playerRecharge;
                    } else {
                        playerRecharge +=Config.playerChargingWidth/Config.playerRecharge;
                    }
                    if (playerRecharge > Config.playerChargingWidth) {
                        playerRecharge =Config.playerChargingWidth;
                    }
                } else {
                    System.out.println("开始缓冲分数"+bufferEnergy+",当前能量:"+playerRecharge);
                    if (enemyPlane.isBoss()) {
                        bufferEnergy += Config.BOSS_SCORE*Config.playerChargingWidth/Config.playerRecharge;
                    } else {
                        bufferEnergy +=Config.playerChargingWidth/Config.playerRecharge;
                    }
                    if (bufferEnergy > Config.playerChargingWidth) {
                        bufferEnergy =Config.playerChargingWidth;
                    }
                }
            }
            System.out.println("敌人被摧毁");
            explosionList.add(new Explosion(enemyPlane.getPoint(),enemyPlane.getWidth(),enemyPlane.getHeight()));
        }
    }

    /** 我方子弹攻击力方法*/
    private void bulletAttackPower(EnemyPlane enemyPlane,int type){
        //如果是boss并且当前场上有其他飞机则无敌
        if (boss && enemyPlaneList.size() != 1 && enemyPlane.getBloodVolume() == Config.BOSS_BLOOD_VOLUME[level-1]) {
            return;
        }
        int atk;
        switch (type) {
            case 1 :atk=(int) (1+(level-1)*0.4);break;
            case 2 :atk=10+level*3;break;
            case 3 :atk= (int) (1+(level-1)*0.4);break;
            case 6 :atk=1;break;
            default:atk=0;break;
        }
        for (int i = 0; i < atk; i++) {
            enemyPlane.setLive(false);
        }
    }
    /** 敌人子弹击中玩家*/
    private void hitThePlayer() {
        Point point = playerPlane.getPoint();
        int width = playerPlane.getWidth();
        int height = playerPlane.getHeight();
        for (EnemyPlane enemyPlane : enemyPlaneList) {
            List<Bullet> bulletList = enemyPlane.bulletList;
            for (Bullet bullet : bulletList) {
                Point pointB = bullet.getPoint();
                int widthB = bullet.getWidth();
                int heightB = bullet.getHeight();
                if (playerPlane.isLive() &&
                        pointB.y + heightB >= point.y + Config.playerPlaneNarrow &&
                        pointB.y <= point.y + height - Config.playerPlaneNarrow &&
                        pointB.x + widthB >= point.x + Config.playerPlaneNarrow &&
                        pointB.x <= point.x + width - Config.playerPlaneNarrow) {
                    System.out.println("玩家被击中");
                    bulletAttackPower(playerPlane,bullet.getType());
                    bullet.setLive(false);
                    /**玩家死亡,添加爆炸*/
                    if (!playerPlane.isLive()) {
                        explosionList.add(new Explosion(playerPlane.getPoint(),playerPlane.getWidth(),playerPlane.getHeight()));
                    }
                }
            }
        }
    }
    /**敌方子弹攻击力方法*/
    private void bulletAttackPower(PlayerPlane playerPlane,int type){
        int atk;
        switch (type) {
            /*大型子弹*/
            case 5 :atk=5;break;
            default:atk=1;break;
        }
        int temp=Config.INVINCIBLE_TIME;
        if (type == 5) {
            Config.INVINCIBLE_TIME=0;
        }
        for (int i = 0; i < atk; i++) {
            playerPlane.setLive(false);
        }
        Config.INVINCIBLE_TIME=temp;
    }

    /**绘图方法通过repaint();调用,根据代码先后具有图层覆盖 @param g */
    @Override
    public void paint(Graphics g) {
        if (mainImage == null) {
            //解决拖延问题,创建缓冲图
            mainImage = this.createImage(Config.FRAME_WIDTH, Config.FRAME_HEIGHT);
        }
        mapPoint.y += 1;
        if (mapPoint.y > Config.FRAME_HEIGHT) {
            mapPoint.y = -68;
        }
        //获取缓冲图片的画笔
        Graphics mainImageGs=mainImage.getGraphics();
        //绘制地图
        mainImageGs.drawImage(mapImage,mapPoint.x,mapPoint.y,this);
        //让背景图片收尾相连
        mainImageGs.drawImage(mapImage,mapPoint.x,mapPoint.y-768,this);
        //绘制敌机
        drawAllEnemyPlanes(mainImageGs);
        //检测子弹是否击中敌人
        hitAnEnemyPlane();
        //检测子弹是否击中玩家
        hitThePlayer();
        //绘制玩家飞机
        playerPlane.draw(mainImageGs);
        //绘制飞机爆炸
        drawExplosion(mainImageGs);
        //绘制得分,血量
        drawScore(mainImageGs);
        //将图片绘制到窗口中
        g.drawImage(mainImage,0,0,this);
    }
    /** 绘制飞机被摧毁后的爆炸*/
    private void drawExplosion(Graphics mainImageGs){
        for (int i = 0; i < explosionList.size(); i++) {
            Explosion explosion = explosionList.get(i);
            if (explosion.isLive()) {
                explosion.draw(mainImageGs);
            } else {
                explosionList.remove(i);
            }
        }
    }

    /** 绘制所有敌机,当敌机小于指定值时添加敌机 @param mainImageGs g */
    private void drawAllEnemyPlanes(Graphics mainImageGs) {
        //当敌机小于3时添加敌机
        if (enemyPlaneList.size() < Config.NUMBER_OF_ENEMY_AIRCRAFT +(level-1)*Config.GROWTH_RATE-2&&!boss) {
            initializeEnemyPlane();
        }
        //当过了指定时间后生成boss
        if (Config.BOSS_TIME[level - 1] * 1000 < System.currentTimeMillis() - timing&&!boss) {
            boss=true;
            //玩家无敌时间减小
            Config.INVINCIBLE_TIME-=300;
            addBoss();
        }

        //绘制所有的敌机,当敌机死亡并子弹也出界时或超出地图范围时删除敌机
        for (int i = 0; i < enemyPlaneList.size(); i++) {
            EnemyPlane enemyPlane = enemyPlaneList.get(i);
            if (!enemyPlane.isLive() && enemyPlane.bulletList.size() == 0 || enemyPlane.getPoint().y > Config.FRAME_HEIGHT) {
                enemyPlaneList.remove(i);
            } else {
                //绘制飞机
                enemyPlane.draw(mainImageGs);
                //开启高智商飞机,路径跟踪
                enemyPlane.trackMove(playerPlane);
            }
        }
    }
    /**绘制boss*/
    private void addBoss(){
        switch (level){
            case 1:enemyPlaneList.add(new EnemyPlane(enemyPlaneImage.getSubimage(190, 339, 175, 134), new Point((Config.FRAME_WIDTH-175)/2, 50), 175, 134,level));break;
            case 2:enemyPlaneList.add(new EnemyPlane(enemyPlaneImage.getSubimage(259, 204, 192, 133), new Point((Config.FRAME_WIDTH-192)/2, 50), 192, 133,level));break;
            case 3:enemyPlaneList.add(new EnemyPlane(enemyPlaneImage.getSubimage(2, 232, 186, 130), new Point((Config.FRAME_WIDTH-186)/2, 50), 186, 130,level));break;
            case 4:enemyPlaneList.add(new EnemyPlane(enemyPlaneImage.getSubimage(262, 2, 246, 201), new Point((Config.FRAME_WIDTH-246)/2, 20), 246, 201,level));break;
            case 5:enemyPlaneList.add(new EnemyPlane(enemyPlaneImage.getSubimage(2, 2, 259, 196), new Point((Config.FRAME_WIDTH-259)/2, 25), 259, 196,level));break;
            default:break;
        }
    }

    /** 绘制得分,血量,能量条,BOSS,实现渐变性掉血效果 @param mainImageGs 画笔 */
    private void drawScore(Graphics mainImageGs) {
        Font f = new Font("微软雅黑", Font.PLAIN, 16);
        Color color = new Color(0xffffff);
        String txt = "当前分数:" + score + ",当前血量:" + playerPlane.getBloodVolume()+",第"+level+"关";
        GameTools.drawClearText((Graphics2D) mainImageGs, txt, 20, 58, color, f);
        mainImageGs.drawRect(20,68,150,20);
        mainImageGs.setColor(new Color(0x18FF21));
        // 透明度
        float alpha = 0.8f;
        ((Graphics2D) mainImageGs).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,alpha));
        int width=150*playerPlane.getBloodVolume()/ Config.playerBloodVolume;
        //血量低于30%将变为红色
        flicker++;
        if (playerPlane.getBloodVolume()*1.0/ Config.playerBloodVolume<=0.3&&flicker<Config.FPS*Config.playerBloodVolumeFlashes){
            mainImageGs.setColor(new Color(0xFF3A09));
        }
        if (flicker>=Config.FPS*Config.playerBloodVolumeFlashes*2){
            flicker=0;
        }
        //渐变式掉线
        if (playerPlane.getLastHP() > width) {
            playerPlane.setLastHP(playerPlane.getLastHP()-1);
            mainImageGs.fillRect(20, 68, playerPlane.getLastHP(), 20);
        } else if (playerPlane.getLastHP() < width) {
            playerPlane.setLastHP(playerPlane.getLastHP()+1);
            mainImageGs.fillRect(20, 68, playerPlane.getLastHP(), 20);
        } else {
            mainImageGs.fillRect(20, 68, width, 20);
        }
        txt ="HP";
        GameTools.drawClearText((Graphics2D) mainImageGs, txt, 20, 83, color, f);
        // 大招充能
        mainImageGs.setColor(new Color(0x000000));
        mainImageGs.drawRect(20,98,Config.playerChargingWidth,20);
        // 大招满了改变颜色
        if (playerRecharge == Config.playerChargingWidth) {
            txt ="按F键使用大招";
            GameTools.drawClearText((Graphics2D) mainImageGs, txt, 20, 143, color, f);
            mainImageGs.setColor(new Color(0xB67EF4));
        } else {
            mainImageGs.setColor(new Color(0xBCFF11));
        }
        // 渐变式大招充能
        if (lastPlayerRecharge < playerRecharge) {
            lastPlayerRecharge++;
            mainImageGs.fillRect(20, 98, lastPlayerRecharge, 20);
        } else if (lastPlayerRecharge> playerRecharge) {
            lastPlayerRecharge--;
            mainImageGs.fillRect(20, 98, lastPlayerRecharge, 20);
        } else {
            mainImageGs.fillRect(20, 98, playerRecharge, 20);
        }
        txt ="MP";
        GameTools.drawClearText((Graphics2D) mainImageGs, txt, 20, 113, color, f);
        //boss血量
        if (boss&&enemyPlaneList.size()==1){
            GameTools.drawClearText((Graphics2D) mainImageGs, "BOSS", Config.FRAME_WIDTH-50, 48, color, f);
            mainImageGs.setColor(new Color(0x000000));
            mainImageGs.drawRect(Config.FRAME_WIDTH-45,68,30,600);
            mainImageGs.setColor(new Color(0x760C78));
            double bossHealthRatio=bossBloodVolume*1.0/ Config.BOSS_BLOOD_VOLUME[level-1];
            if (bossHealthRatio<=0.3){
                mainImageGs.setColor(new Color(0xFF3A09));
            }
            mainImageGs.fillRect(Config.FRAME_WIDTH-45, (int) (68+(1-bossHealthRatio)*600), 30, (int) (600*bossHealthRatio));
            GameTools.drawClearText((Graphics2D) mainImageGs, "HP", Config.FRAME_WIDTH-40, 660, color, f);
        }
        //玩家武器冷却提示
        long now=System.currentTimeMillis();
        if (now-bigFire>Config.playerDuration*1000&&now-bigFire<=Config.playerDuration*1000*2){
            int time= (int) (Config.playerDuration-(now-bigFire-Config.playerDuration*1000)/1000);
            GameTools.drawClearText((Graphics2D) mainImageGs, "武器冷却中,剩余"+time+"秒", 20, 143, new Color(0xFF3A09), f);
        }
    }

    /** 玩家死亡并且爆炸结束后,游戏结束窗口弹出 */
    private void gameOver() {
        if (!playerPlane.isLive()&&explosionList.size()==0) {
            int res = JOptionPane.showConfirmDialog(null, "你被击毁了！最终得分:" + score + ",是否重新开始", "系统信息", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                restart();
            } else {
                System.exit(0);
            }
        }
        if (level>=6){
            int res = JOptionPane.showConfirmDialog(null, "恭喜你通关了游戏！最终得分:" + score + ",是否重新开始", "系统信息", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                restart();
            } else {
                System.exit(0);
            }
        }
    }
    /** 重新开始*/
    private void restart(){
        enemyPlaneList.clear();
        boss=false;
        Config.playerBloodVolume-=(level-1)* Config.BOSS_REWARD;
        Config.BULLET_INTERVAL+=(level-1)*10;
        score = 0;
        playerRecharge=0;
        timing=System.currentTimeMillis();
        level=1;
        bossBloodVolume=Config.BOSS_BLOOD_VOLUME[level-1];
        mapImage=GameTools.getImageMap().get("img_bg_level_"+level+".jpg");
        Config.playerPlaneNarrow=playerInvincible;
        initializePlayerPlane();
    }
    /** 循环改变图像位置,设置帧数 */
    class PaintThread extends Thread {
        @Override
        public void run() {
            while (true) {
                //游戏结束判定
                gameOver();
                repaint();
                try { sleep(1000/ Config.FPS); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }
}
