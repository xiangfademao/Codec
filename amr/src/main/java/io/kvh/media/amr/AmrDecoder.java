package io.kvh.media.amr;

public class AmrDecoder {
    public static final int AMR_NB = 1;
    public static final int AMR_WB = 2;
    private static IAmrDecoder amrDecoder;

    /**
     * 初始化
     *
     * @param encoder AMR编码 AMR_NB或者AMR_WB
     */
    public static void init(int encoder) {
        switch (encoder) {
            case AMR_NB:
                amrDecoder = new AmrNBDecoder();
                break;
            case AMR_WB:
                amrDecoder = new AmrWBDecoder();
                break;
            default:
                return;
        }
        amrDecoder.initDecoder();
    }

    /**
     * 把amr帧数据转换为pcm数据
     *
     * @param in  amr帧数据
     * @param out pcm帧数据
     */
    public static void decode(byte[] in, short[] out) {
        if (amrDecoder == null) {
            throw new IllegalStateException("未初始化!");
        }
        amrDecoder.decodeData(in, out);
    }

    /**
     * 退出
     */
    public static void exit() {
        if (amrDecoder == null) {
            return;
        }
        amrDecoder.exitDecoder();
        amrDecoder = null;
    }
}
