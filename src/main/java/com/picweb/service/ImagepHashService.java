package com.picweb.service;

import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
===================================================感知哈希算法具体实现==============================================
*/
@Service
public class ImagepHashService {

    public static String perceptualHash(BufferedImage image) throws Exception {
        // Step 1: 缩放为 32x32 灰度图
        BufferedImage resized = resize(image, 32, 32);

        // Step 2: 转换为灰度矩阵
        double[][] pixels = toGrayScaleMatrix(resized);

        // Step 3: 应用 DCT 变换
        double[][] dct = applyDCT(pixels);

        // Step 4: 提取左上角 8x8 的低频系数（跳过 DC 系数）
        double[] coefficients = new double[63];
        int index = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == 0 && j == 0) continue;
                coefficients[index++] = dct[i][j];
            }
        }

        // Step 5: 计算平均值代替中位数
        double sum = 0;
        for (double c : coefficients) {
            sum += c;
        }
        double avg = sum / coefficients.length;

        // Step 6: 构建哈希值
        StringBuilder hash = new StringBuilder();
        for (double c : coefficients) {
            hash.append(c > avg ? '1' : '0');
        }

        return hash.toString();
    }


    private static BufferedImage resize(BufferedImage img, int width, int height) {
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        scaled.getGraphics().drawImage(img.getScaledInstance(width, height, java.awt.Image.SCALE_AREA_AVERAGING), 0, 0, null);
        return scaled;
    }

    private static double[][] toGrayScaleMatrix(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        double[][] matrix = new double[w][h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                matrix[x][y] = (r + g + b) / 3.0;
            }
        }
        return matrix;
    }

    private static double[][] applyDCT(double[][] matrix) {
        int size = matrix.length;
        double[][] result = new double[size][size];
        double c0 = 1.0 / Math.sqrt(2);

        for (int u = 0; u < size; u++) {
            for (int v = 0; v < size; v++) {
                double sum = 0;
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        sum += matrix[i][j]
                                * Math.cos(Math.PI * u * (i + 0.5) / size)
                                * Math.cos(Math.PI * v * (j + 0.5) / size);
                    }
                }

                double cu = (u == 0) ? c0 : 1.0;
                double cv = (v == 0) ? c0 : 1.0;
                result[u][v] = (cu * cv * sum) / size;
            }
        }
        return result;
    }
}
