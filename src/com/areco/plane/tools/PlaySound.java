package com.areco.plane.tools;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 播放音乐类(mp3)
 * @author aotmd
 * @version 1.0
 * @date 2020/6/22 20:15
 */
public class PlaySound extends Thread{
    String location;
    public PlaySound(String location) {
        this.location = location;
    }
    @Override
    public void run() {
        File file=new File(location);
            try {
                BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file));
                new Player(buffer).play();
            } catch (FileNotFoundException | JavaLayerException e) {
                e.printStackTrace();
            }
    }
}
