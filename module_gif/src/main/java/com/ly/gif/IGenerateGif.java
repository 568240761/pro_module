package com.ly.gif;

import java.io.File;

/**
 * Created by LanYang on 2019/5/4
 */

public interface IGenerateGif {

    /**
     * gif文件生成成功函数
     *
     * @param image gif文件
     */
    void success(File image);
}
