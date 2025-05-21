package com.red.rpc.compress.impl;

import cn.hutool.core.util.ZipUtil;
import com.red.rpc.compress.Compress;

import java.util.Objects;

/**
 * @author red
 * @date 2025/5/21
 * @description
 */
public class GzipCompress implements Compress {
    @Override
    public byte[] compress(byte[] data) {
        if (Objects.isNull(data) || data.length == 0) {
            return data;
        }
        return ZipUtil.gzip(data);
    }

    @Override
    public byte[] decompress(byte[] data) {
        if (Objects.isNull(data) || data.length == 0) {
            return data;
        }
        return ZipUtil.unGzip(data);
    }
}
