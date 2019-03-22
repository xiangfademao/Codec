package io.kvh.media.demo;


import android.util.SparseArray;


import java.io.IOException;
import java.io.InputStream;

import io.kvh.media.amr.AmrDecoder;


/**
 * 把AMR音频文件解码成PCM数据流，从中读取音频音量信息
 */
public class AmrFileDecoder implements Runnable {

    public static final String TAG = "AmrFileDecoder";
    private Thread mDecodeThread;
    private InputStream mInputStream;
    private byte[] readBuffer;

    // 20 ms second
    // 0.02 x 8000 x 2 = 320;160 short
    private int pcmFrameSize;
    private boolean isRunning;
    private int headerSize;
    private int encoder;

    public AmrFileDecoder(int encoder) {
        if (encoder == AmrDecoder.AMR_NB) {
            pcmFrameSize = 160;
            headerSize = AmrConstant.FRAME_HEADER_LENGTH_NB;
        } else {
            pcmFrameSize = 320;
            headerSize = AmrConstant.FRAME_HEADER_LENGTH_WB;//WB头是9个字节
        }
        this.encoder = encoder;
    }

    /**
     * 开始
     *
     * @param inputStream 音频流
     */
    public void start(InputStream inputStream) {
        if (isRunning) {
            return;
        }
        isRunning = true;
        AmrDecoder.init(encoder);
        readBuffer = new byte[4096];
        mInputStream = inputStream;
        //amr file has 6 bytes header: "23 21 41 4D 52 0A" => "#!amr.", so skip here
        try {
            mInputStream.skip(headerSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mDecodeThread = new Thread(this);
        mDecodeThread.start();
    }

    /**
     * 停止
     */
    public void stop() {
        if (!isRunning) {
            return;
        }
        mDecodeThread.interrupt();
        AmrDecoder.exit();
        isRunning = false;
    }

    private SparseArray<byte[]> frameBufferMap = new SparseArray<>();

    /**
     * 根据帧长度提供不同的buffer
     *
     * @param frameLength 帧长度
     * @return byte数组
     */
    private byte[] getFrameBuffer(int frameLength) {
        byte[] buffer = frameBufferMap.get(frameLength);
        if (buffer == null) {
            buffer = new byte[frameLength];
            frameBufferMap.put(frameLength, buffer);
        }
        return buffer;
    }


    @Override
    public void run() {
        try {
            short[] pcmFrame = new short[pcmFrameSize];
            int[] packedSize = encoder == AmrDecoder.AMR_NB ? AmrConstant.AMR_NB_PACKED_SIZE : AmrConstant.AMR_WB_PACKED_SIZE;
            int readSize;
            int writeIndex = 0;
            int frameLength = 0;
            byte[] amrFrame = null;
            while (isRunning && (readSize = mInputStream.read(readBuffer)) != -1) {
                int bufferIndex = 0;
                //计算出帧头的下标
                while (bufferIndex < readSize) {
                    if (writeIndex == 0) {
                        int packedPosition = (readBuffer[bufferIndex] >> 3) & 0x0F;
                        frameLength = packedSize[packedPosition];////跳过的字节数
                        amrFrame = getFrameBuffer(frameLength);
                    }
                    while (writeIndex < frameLength && bufferIndex < readSize) {
                        amrFrame[writeIndex] = readBuffer[bufferIndex];
                        writeIndex++;
                        bufferIndex++;
                    }
                    if (writeIndex == frameLength) {
                        writeIndex = 0;
                        AmrDecoder.decode(amrFrame, pcmFrame);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
            try {
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
