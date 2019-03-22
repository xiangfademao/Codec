package io.kvh.media.amr;

/**
 * AMR_WB转换为pcm
 */
public class AmrWBDecoder extends IAmrDecoder {
    AmrWBDecoder() {

    }

    /**
     * 初始化底层
     *
     * @return 指针
     */
    public native long init();


    /**
     * 退出 释放底层
     *
     * @param state 指针
     */
    public native void exit(long state);

    /**
     * @param state 指针
     * @param in    amr帧数据
     * @param out   pcm帧数据
     */
    public native void decode(long state, byte[] in, short[] out);

    static {
        System.loadLibrary("amr-codec");
    }

    @Override
    public void initDecoder() {
        state = init();
    }

    @Override
    void exitDecoder() {
        exit(state);
    }

    @Override
    void decodeData(byte[] in, short[] out) {
        decode(state, in, out);
    }
}
