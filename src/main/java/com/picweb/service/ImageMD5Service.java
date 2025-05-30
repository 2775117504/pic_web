package com.picweb.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * ======================================计算图片的MD5值=======================================
 */
@Service
public class ImageMD5Service {
    public String calculateMD5(MultipartFile file) throws IOException {
        return DigestUtils.md5Hex(file.getInputStream());
    }
}
