package com.areco.plane.tools;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * 音乐循环播放类(mp3)
 *
 * @author aotmd
 * @version 1.0
 * @date 2020/6/22 19:01
 */
public class BackgroundMusic extends Thread {
    String location;

    public BackgroundMusic(String location) {
        this.location = location;
    }

    @Override
    public void run() {
        File file = new File(location);
        while (true) {
            try {
                BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file));
                new Player(buffer).play();
            } catch (FileNotFoundException | JavaLayerException e) {
                e.printStackTrace();
            }
        }
    }
}
