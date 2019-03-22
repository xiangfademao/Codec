package io.kvh.media.demo;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * AMR文件常量
 */
public class AmrConstant {
    public static final String TAG = "AmrConstant";
    public final static int AMR_NB = 1;
    public final static int AMR_WB = 2;
    public final static byte FRAME_HEADER_LENGTH_NB = 6;//AMR文件头长度（AMR_NB是6，AMR_WB是9）
    public final static byte FRAME_HEADER_LENGTH_WB = 9;//AMR文件头长度（AMR_NB是6，AMR_WB是9）
    public final static int FRAME_DURATION = 20;//AMR每一帧的时长,单位毫秒（每帧20ms）
    public final static String HEADER_AMR_WB = "#!AMR-WB\n";//AMR_WB的头
    //AMR_NB编码对应的帧类型数组
    public final static int[] AMR_NB_PACKED_SIZE = {14, 15, 17, 19, 20, 22, 27, 32, 7, 7, 7, 7, 1, 1, 1, 2};
    //AMR 一共有16种编码方式， 0-7对应8种不同的编码方式， 8-15 用于噪音或者保留用。
    //帧类型数组（这里是AMR_WB编码对应的帧类型数组）
    public final static int[] AMR_WB_PACKED_SIZE = {18, 23, 33, 37, 41, 47, 51, 59, 61, 6, 0, 0, 0, 0, 1, 1};
    public final static String AMR_NB_SILENT_FRAME = "AMR_NB_SILENT_FRAME";//NB模式的静音帧
    public final static String AMR_WB_SILENT_FRAME_23 = "AMR_WB_SILENT_FRAME_23";//WB SDK 23以上模式的静音帧
    public final static String AMR_WB_SILENT_FRAME = "AMR_WB_SILENT_FRAME";//WB模式的静音帧


    /**
     * 读取文件头判断格式
     *
     * @param amrFile 音频文件
     * @return amr模式
     */
    public static int readHeader(File amrFile) {
        int mode = AMR_NB;
        FileInputStream fileInputStream = null;
        try {
            byte[] header = new byte[AmrConstant.FRAME_HEADER_LENGTH_WB];
            fileInputStream = new FileInputStream(amrFile);
            int readSize = fileInputStream.read(header);
            if (readSize != -1) {
                String headerMark = new String(header);
                if (HEADER_AMR_WB.equals(headerMark)) {
                    mode = AMR_WB;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(fileInputStream);
        }
        return mode;
    }

    /**
     * 读取amr文件的帧长度
     *
     * @param file amr文件
     * @return amr音频文件的帧长度
     */
    public static int readFrameLength(File file) {
        //AMR 一共有16种编码方式， 0-7对应8种不同的编码方式， 8-15 用于噪音或者保留用。
        //帧类型数组（这里是AMR_WB编码对应的帧类型数组）
        int amrMode = AmrConstant.readHeader(file);
        int[] packedSize = amrMode == AMR_NB ? AmrConstant.AMR_NB_PACKED_SIZE : AmrConstant.AMR_WB_PACKED_SIZE;
        RandomAccessFile randomAccessFile = null;
        try {
            int headerSize = amrMode == AMR_NB ? FRAME_HEADER_LENGTH_NB : FRAME_HEADER_LENGTH_WB;
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(headerSize);
            int packedPos;
            byte[] dataBuffer = new byte[1];// 初始数据值
            int readSize;
            readSize = randomAccessFile.read(dataBuffer, 0, 1);
            if (readSize == -1) {
                return 0;
            }
            packedPos = (dataBuffer[0] >> 3) & 0x0F;
            return packedSize[packedPos];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            CloseUtils.closeIO(randomAccessFile);
        }
        return 0;
    }

}
