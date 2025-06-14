
/**
====================================================================感知哈希算法PLUS版具体实现=========================================================================
*/
package com.picweb.service;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
@Service
public class ImagepHashService {
    /**
     * 输入 BufferedImage，输出高维 hash 值
     */
    public static String perceptualHash(BufferedImage img) {
        String pHash = computePHash(img);      // 255 位结构感知，16x16 DCT
        String colorBits = computeColorBits(img); // 9 位颜色主色，提取 RGB 主色 → 添加颜色感知（9 位）
        /*String aHash = computeAHash(img); */     // 64 位亮度 hash
        return pHash + colorBits;      // 共计 255+9 位
    }

    // --- 1. pHash（16x16 DCT） ---
    private static String computePHash(BufferedImage img) {
        BufferedImage gray = resizeToGray(img, 32, 32);

        double[][] vals = new double[32][32];
        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 32; y++) {
                int rgb = gray.getRGB(x, y) & 0xff;
                vals[x][y] = rgb;
            }
        }

        double[][] dct = applyDCT(vals);

        double total = 0;
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                if (x == 0 && y == 0) continue;
                total += dct[x][y];
            }
        }
        double avg = total / 255;

        StringBuilder hash = new StringBuilder();
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                if (x == 0 && y == 0) continue;
                hash.append(dct[x][y] > avg ? "1" : "0");
            }
        }
        return hash.toString(); // 255 bits
    }

    // --- 2. 主色 RGB编码（9位）---
    private static String computeColorBits(BufferedImage img) {
        long rSum = 0, gSum = 0, bSum = 0;
        int width = img.getWidth();
        int height = img.getHeight();
        int total = width * height;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = img.getRGB(x, y);
                rSum += (rgb >> 16) & 0xff;
                gSum += (rgb >> 8) & 0xff;
                bSum += rgb & 0xff;
            }
        }

        int rAvg = (int) (rSum / total);
        int gAvg = (int) (gSum / total);
        int bAvg = (int) (bSum / total);

        return String.format("%3s", Integer.toBinaryString(rAvg >> 5)).replace(' ', '0') +
                String.format("%3s", Integer.toBinaryString(gAvg >> 5)).replace(' ', '0') +
                String.format("%3s", Integer.toBinaryString(bAvg >> 5)).replace(' ', '0');
    }

    // --- 3. 平均哈希（aHash 64位）---
    private static String computeAHash(BufferedImage img) {
        BufferedImage gray = resizeToGray(img, 8, 8);
        int[] pixels = new int[64];
        int total = 0;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int grayVal = gray.getRGB(x, y) & 0xff;
                pixels[y * 8 + x] = grayVal;
                total += grayVal;
            }
        }
        int avg = total / 64;
        StringBuilder hash = new StringBuilder();
        for (int val : pixels) {
            hash.append(val >= avg ? "1" : "0");
        }
        return hash.toString();
    }

    // --- DCT 2D ---
    private static double[][] applyDCT(double[][] input) {
        int N = 32;
        double[][] output = new double[N][N];
        for (int u = 0; u < N; u++) {
            for (int v = 0; v < N; v++) {
                double sum = 0.0;
                for (int i = 0; i < N; i++) {
                    for (int j = 0; j < N; j++) {
                        sum += input[i][j] *
                                Math.cos(((2 * i + 1) * u * Math.PI) / (2 * N)) *
                                Math.cos(((2 * j + 1) * v * Math.PI) / (2 * N));
                    }
                }
                double cu = (u == 0) ? 1 / Math.sqrt(2) : 1.0;
                double cv = (v == 0) ? 1 / Math.sqrt(2) : 1.0;
                output[u][v] = 0.25 * cu * cv * sum;
            }
        }
        return output;
    }

    // --- 灰度缩放 ---
    private static BufferedImage resizeToGray(BufferedImage img, int width, int height) {
        Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = resized.createGraphics();
        g.drawImage(scaled, 0, 0, null);
        g.dispose();
        return resized;
    }
}


