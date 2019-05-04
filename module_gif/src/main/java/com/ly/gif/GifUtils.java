package com.ly.gif;

import com.madgag.gif.fmsware.AnimatedGifEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GifUtils {

    /**
     * 用原图片路径内多张图片生成gif图片
     *
     * @param file     原图片路径
     * @param path     gif图片存放路径
     * @param name     gif文件名
     * @param callback gif文件生成成功回调监听
     */
    public static void generateGif(File file, File path, String name, IGenerateGif callback) {
        if (file.exists() && file.listFiles() != null)
            generateGif(file.listFiles(), path, name, callback);
        else
            throw new IllegalArgumentException("原图片路径不存在");
    }

    /**
     * 用原图片数组生成gif图片
     *
     * @param file     原图片数组
     * @param path     gif图片存放路径
     * @param name     gif文件名
     * @param callback gif文件生成成功回调监听
     */
    public static void generateGif(File[] file, File path, String name, IGenerateGif callback) {
        if (file.length > 0) {
            try {
                BufferedImage[] images = new BufferedImage[file.length];
                for (int index = 0; index < file.length; index++) {
                    images[index] = ImageIO.read(file[index]);
                }

                if (!path.exists()) path.mkdirs();

                File image = new File(path.getAbsolutePath() + "/" + name);
                image.createNewFile();

                AnimatedGifEncoder encoder = new AnimatedGifEncoder();
                //设置循环模式,0为无限循环
                encoder.setRepeat(0);
                encoder.start(new FileOutputStream(image));
                for (BufferedImage img : images) {
                    encoder.setDelay(500);
                    encoder.addFrame(img);
                }
                encoder.finish();

                if (callback != null) callback.success(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            throw new IllegalArgumentException("原图片数组长度为0");
    }
}
