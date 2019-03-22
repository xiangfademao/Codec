package io.kvh.media.amr;

public abstract class IAmrDecoder {
    long state;

    /**
     * 初始化底层
     *
     * @return 指针
     */
    abstract void initDecoder();

    /**
     * 退出 释放底层
     */
    abstract void exitDecoder();

    /**
     * amr数据转为pcm数据
     *
     * @param in  amr帧数据
     * @param out pcm帧数据
     */
    abstract void decodeData(byte[] in, short[] out);
}
