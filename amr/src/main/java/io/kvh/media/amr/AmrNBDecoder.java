package io.kvh.media.amr;

/**
 * Created by kv.h on 14/11/21.
 */
public class AmrNBDecoder extends IAmrDecoder {

    static {
        System.loadLibrary("amr-codec");
    }

    AmrNBDecoder() {

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


    @Override
    public void initDecoder() {
        state = init();
    }

    @Override
    public void exitDecoder() {
        exit(state);
    }

    @Override
    public void decodeData(byte[] in, short[] out) {
        decode(state, in, out);
    }
}
