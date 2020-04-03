package com.luz.comment.utils;

import com.madgag.gif.fmsware.AnimatedGifEncoder;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.io.FileOutputStream;
import java.io.IOException;
public class GifUtils {
    /**
     * 默认每截取一次跳过多少帧（默认：2）
     */
    private static final Integer DEFAULT_MARGIN = 2;
    /**
     * 默认帧频率（默认：10）
     */
    private static final Integer DEFAULT_FRAME_RATE = 10;

    /**
     * 截取视频指定帧生成gif,存储路径同级下
     *
     *            视频文件
     * @param startFrame
     *            开始帧
     * @param frameCount
     *            截取帧数
     * @param frameRate
     *            帧频率（默认：2）
     * @param margin
     *            每截取一次跳过多少帧（默认：10）
     * @throws IOException
     *
     */
    public static String buildGif(String filePath,String outPath, int startFrame, int frameCount, Integer frameRate, Integer margin)
            throws IOException {
        if (margin == null) {
            margin = DEFAULT_MARGIN;
        }
        if (frameRate == null) {
            frameRate = DEFAULT_FRAME_RATE;
        }
        // gif存储路径
        String gifPath = filePath.substring(0, outPath.lastIndexOf(".")) + ".gif";
        // 输出文件流
        FileOutputStream targetFile = new FileOutputStream(gifPath);
        // 读取文件
        FFmpegFrameGrabber ff = new FFmpegFrameGrabber(filePath);
        Java2DFrameConverter converter = new Java2DFrameConverter();
        // 无限期的循环下去、注意，此参数设置必须在下面for循环之前，即在添加第一帧数据之前
        AnimatedGifEncoder en = new AnimatedGifEncoder();
        en.setRepeat(0);
        ff.start();
        try {
            Integer videoLength = ff.getLengthInFrames();
            // 如果用户上传视频极短,不符合自己定义的帧数取值区间,设置合法区间
            if (startFrame > videoLength || (startFrame + frameCount * margin) > videoLength) {
                startFrame = videoLength / 5;
                frameCount = (videoLength - startFrame - 5) / margin;
            }
            ff.setFrameNumber(startFrame);
            en.setFrameRate(frameRate);
            en.start(targetFile);
            for (int i = 0; i < frameCount; i++) {
                en.addFrame(converter.convert(ff.grabFrame()));
                ff.setFrameNumber(ff.getFrameNumber() + margin);
            }
            en.finish();
        } finally {
            ff.stop();
            ff.close();
        }
        return gifPath;
    }

}
