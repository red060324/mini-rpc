package com.red.rpc.compress;

/**
 * @author red
 * @date 2025/5/21
 * @description
 */
public interface Compress {
    /**
     * 压缩
     *
     * @param data 待压缩字节数组
     * @return byte[]
     */
    byte[] compress(byte[] data);

    /**
     * 解压缩
     *
     * @param data 待解压缩字节数组
     * @return byte[]
     */
    byte[] decompress(byte[] data);
}
