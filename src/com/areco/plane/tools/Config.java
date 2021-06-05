package com.areco.plane.tools;

/**
 * 配置类
 * @author aotmd
 * @version 1.0
 * @date 2020/6/21 20:25
 */
public class Config {
    public static final String PROJECT_RESOURCES ="src/resource/";
    /** 地图宽带*/
    public static final int FRAME_WIDTH = 512;
    /** 地图高度*/
    public static final int FRAME_HEIGHT = 700;
    /** 屏幕刷新率*/
    public static final int FPS=60;
    /** 每关boss出现的时间s*/
    public static final int[] BOSS_TIME =new int[]{60,60,60,60,60};
//    public static final int[] BOSS_TIME =new int[]{1,1,1,1,1};
    /** boss血量*/
    public static final int[] BOSS_BLOOD_VOLUME =new int[]{500,800,1000,1500,2500};
//    public static final int[] BOSS_BLOOD_VOLUME =new int[]{20,20,20,20,20};
    /** 击败boss的血量奖励*/
    public static final int BOSS_REWARD =10;
    /** 击败boss的分数奖励*/
    public static final int BOSS_SCORE =20;
    /** boss间歇性快速移动 默认间隔:13s*/
    public static final int BOSS_FAST_MOBILE =13;
    /** 敌人开火频率 ?/s (0-60)*/
    public static final int ENEMY_FIRE_FREQUENCY =1;
    /** 敌机数量 */
    public static final int NUMBER_OF_ENEMY_AIRCRAFT = 5;
    /** 敌机血量*/
    public static final int ENEMY_BLOOD_VOLUME =10;
    /**敌机判定范围缩小*/
    public static final int ENEMY_PLANE_NARROW = 20;
    /**每关敌人密度增长幅度*/
    public static final int GROWTH_RATE =1;

    /** 子弹射击间隔限制:ms*/
    public static int BULLET_INTERVAL= 200;
    /** 玩家被击中后的无敌时间:ms*/
    public static  int INVINCIBLE_TIME =500;
    /** 玩家初始血量上限*/
    public static int playerBloodVolume=20;
    /**玩家飞机宽度*/
    public static int playerPlaneWidth = 133;
    /**玩家飞机高度*/
    public static int playerPlaneHeight = 88;
    /**玩家飞机移动速度*/
    public static int playerPlaneSpeed = 5;
    /**玩家飞机判定范围缩小*/
    public static int playerPlaneNarrow = 40;
    /** 玩家大招充能屏幕显示宽度,应大于等于充能分数,并且此参数应能被充能分数整除*/
    public static int playerChargingWidth =240;
    /**玩家大招充能 分数*/
    public static int playerRecharge =40;
    /** 玩家大招持续时间 s*/
    public static int playerDuration =5;
    /**玩家血量闪烁间隔 s*/
    public static int playerBloodVolumeFlashes=1;
}
