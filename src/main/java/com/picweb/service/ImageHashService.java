package com.picweb.service;

import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
@Service   /**用于标记一个类是 Spring 组件，表示该类是 Spring 容器中的一个Bean，可以注入到其他Bean中。
 /**
 * ===================================感知哈希算法，返回图片的哈希值===========================================
 */

public class ImageHashService {

    public static String averageHash(BufferedImage image) {
        /**
         *  缩放图片为 8x8
         */

        BufferedImage resizedImage = resize(image, 8, 8);
        /**
         *转换为灰度图并计算平均像素值
         */
        long total = 0;
        int[][] grayPixels = new int[8][8];

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int pixel = resizedImage.getRGB(x, y);
                int red = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8) & 0xFF;
                int blue = pixel & 0xFF;
                int gray = (red + green + blue) / 3;
                grayPixels[y][x] = gray;
                total += gray;
            }
        }
        /**
         *计算平均值
         */
        long average = total / 64;
        /**
         *
         */
        // 构建哈希值
        StringBuilder hash = new StringBuilder();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                hash.append(grayPixels[y][x] >= average ? '1' : '0');
            }
        }

        return hash.toString();
    }

    /**
     * 图像缩放工具方法
     */
    private static BufferedImage resize(BufferedImage img, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        resized.getGraphics().drawImage(img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        return resized;
    }

    /**
     * 将二进制字符串转换为十六进制表示（可选）
     */
    public static String binaryStringToHex(String binaryStr) {
        if (binaryStr.length() != 64) throw new IllegalArgumentException("输入必须是64位二进制字符串");

        // 每4位一组转成十六进制
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < 64; i += 4) {
            String group = binaryStr.substring(i, i + 4);
            int value = Integer.parseInt(group, 2);
            hex.append(Integer.toHexString(value));
        }
        return hex.toString();
    }

    /**
     * 计算两个哈希值之间的汉明距离（差异位数）
     */
    public static int hammingDistance(String hash1, String hash2) {
        if (hash1.length() != hash2.length()) {
            throw new IllegalArgumentException("两个哈希长度不一致");
        }

        int distance = 0;
        for (int i = 0; i < hash1.length(); i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }
}

