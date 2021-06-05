package com.areco.plane.game;

import com.areco.plane.tools.GameTools;

import java.awt.*;

/**
 * 爆炸类
 * @author aotmd
 * @version 1.0
 * @date 2020/6/22 14:45
 */
public class Explosion extends FlyObject {
    private int flag=0;
    private String name="largeBurst"+flag+".gif";
    @Override
    public void move() {
        flag++;
        if (flag >= 16) {
            setLive(false);
            return;
        }
        name="largeBurst"+flag+".gif";
        setImg(GameTools.getImageMap().get(name).getSubimage(0,0,94,94));
    }
    public Explosion(Point point, int width, int height){
        setImg(GameTools.getImageMap().get(name).getSubimage(0,0,94,94));
        setPoint(new Point(point.x,point.y));
        setWidth(width);
        setHeight(height);
    }
}
