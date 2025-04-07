package net.eson.audio.utils;

/**
 * @author Eson
 * @date 2025年04月03日 21:19
 */

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ColorsUtil {
    private static final int SAMPLING_STEP = 4; // 采样步长（平衡性能与精度）
    private static final double EPSILON = 0.008856; // LAB转换常量
    private static final double KAPPA = 903.3;       // LAB转换常量

    public static String[] calculateDominantAndAccentColors(File imageFile) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);
        if (image == null) throw new IOException("Unsupported image format");

        Collection<LabColor> colors = sampleValidPixels(image);
        return clusterColors(colors);
    }

    /**
     * 智能像素采样（跳过透明和边缘噪声）
     */
    private static Collection<LabColor> sampleValidPixels(BufferedImage image) {
        Collection<LabColor> samples = new java.util.ArrayList<>(1000);
        int width = image.getWidth();
        int height = image.getHeight();

        for (int x = SAMPLING_STEP; x < width - SAMPLING_STEP; x += SAMPLING_STEP) {
            for (int y = SAMPLING_STEP; y < height - SAMPLING_STEP; y += SAMPLING_STEP) {
                int rgb = image.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xFF;
                if (alpha < 220) continue; // 过滤半透明像素

                samples.add(new LabColor(rgb));
            }
        }
        return samples;
    }

    /**
     * 执行颜色聚类分析
     */
    private static String[] clusterColors(Collection<LabColor> colors) {
        if (colors.isEmpty()) return new String[]{"#C8C8C8", "#C8C8C8"}; // 默认返回灰色

        KMeansPlusPlusClusterer<LabColor> clusterer = new KMeansPlusPlusClusterer<>(10, 50);
        Collection<CentroidCluster<LabColor>> clusters = clusterer.cluster(colors);

        List<LabColor> sortedClusters = clusters.stream()
                .sorted(Comparator.comparingInt(c -> -c.getPoints().size())) // 按簇大小降序
                .map(ColorsUtil::calculateCentroid)
//                .filter(LabColor::isVibrantColor) // 过滤非鲜艳颜色
                .collect(Collectors.toList());

        if (sortedClusters.isEmpty()) return new String[]{"#C8C8C8", "#C8C8C8"}; // 默认返回灰色

        // 选择主色（最强色）
        LabColor dominantColor = sortedClusters.get(0);

        // 选择副色：与主色相近的颜色（从其他簇选择）
        LabColor accentColor = sortedClusters.size() > 1 ? sortedClusters.get(1) : dominantColor;

        return new String[]{dominantColor.toHexString(), accentColor.toHexString()};
    }

    /**
     * 计算簇的LAB空间质心
     */
    private static LabColor calculateCentroid(Cluster<LabColor> cluster) {
        double sumL = 0, sumA = 0, sumB = 0;
        for (LabColor color : cluster.getPoints()) {
            sumL += color.L;
            sumA += color.a;
            sumB += color.b;
        }
        int size = cluster.getPoints().size();
        return new LabColor(sumL / size, sumA / size, sumB / size);
    }

    /**
     * LAB颜色空间实现（完整转换逻辑）
     */
    static class LabColor implements Clusterable {
        final double L, a, b;

        // 从RGB构造
        LabColor(int rgb) {
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;

            double[] xyz = rgbToXyz(red, green, blue);
            double[] lab = xyzToLab(xyz[0], xyz[1], xyz[2]); // 分别传递X, Y, Z分量

            this.L = lab[0];
            this.a = lab[1];
            this.b = lab[2];
        }

        // 从LAB值构造
        LabColor(double L, double a, double b) {
            this.L = L;
            this.a = a;
            this.b = b;
        }

        /**
         * 判断是否为鲜艳颜色（Apple风格过滤）
         */
        boolean isVibrantColor() {
            return L > 25 && L < 85 &&
                    (Math.abs(a) > 15 || Math.abs(b) > 15) &&
                    (Math.sqrt(a*a + b*b) > 25); // 色度阈值
        }

        /**
         * 转换回RGB字符串
         */
        String toRgbString() {
            int[] rgb = labToRgb(L, a, b);
            return String.format("rgb(%d,%d,%d)", rgb[0], rgb[1], rgb[2]);
        }

        /**
         * 转换为十六进制字符串
         */
        String toHexString() {
            int[] rgb = labToRgb(L, a, b);
            return String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
        }

        @Override
        public double[] getPoint() {
            return new double[]{L, a, b};
        }

        /**
         * RGB转XYZ（精确转换）
         */
        private static double[] rgbToXyz(int r, int g, int b) {
            // sRGB伽马校正
            double red = gammaExpand(r / 255.0);
            double green = gammaExpand(g / 255.0);
            double blue = gammaExpand(b / 255.0);

            // 转换矩阵（D65白点）
            double x = red * 0.4124564 + green * 0.3575761 + blue * 0.1804375;
            double y = red * 0.2126729 + green * 0.7151522 + blue * 0.0721750;
            double z = red * 0.0193339 + green * 0.1191920 + blue * 0.9503041;

            return new double[]{x * 100, y * 100, z * 100};
        }

        /**
         * XYZ转LAB（标准D65白点）
         */
        private static double[] xyzToLab(double x, double y, double z) {
            // D65标准白点值
            double refX = 95.047;
            double refY = 100.000;
            double refZ = 108.883;

            // 标准化处理
            double varX = pivotXyz(x / refX);
            double varY = pivotXyz(y / refY);
            double varZ = pivotXyz(z / refZ);

            // 计算LAB值
            double L = Math.max(0, 116 * varY - 16);
            double a = 500 * (varX - varY);
            double b = 200 * (varY - varZ);

            return new double[]{L, a, b};
        }

        /**
         * LAB转RGB（带颜色裁剪）
         */
        private static int[] labToRgb(double L, double a, double b) {
            // 转换到XYZ
            double varY = (L + 16) / 116;
            double varX = a / 500 + varY;
            double varZ = varY - b / 200;

            double x = pivotXyzReverse(varX) * 0.95047; // D65白点
            double y = pivotXyzReverse(varY) * 1.00000;
            double z = pivotXyzReverse(varZ) * 1.08883;

            // 转换矩阵（逆矩阵）
            double red = x *  3.2404542 + y * -1.5371385 + z * -0.4985314;
            double green = x * -0.9692660 + y *  1.8760108 + z *  0.0415560;
            double blue = x *  0.0556434 + y * -0.2040259 + z *  1.0572252;

            // 伽马压缩
            red = gammaCompress(red);
            green = gammaCompress(green);
            blue = gammaCompress(blue);

            // 裁剪到[0,255]
            int r = (int) Math.round(clamp(red, 0, 1) * 255);
            int g = (int) Math.round(clamp(green, 0, 1) * 255);
            int bVal = (int) Math.round(clamp(blue, 0, 1) * 255);

            return new int[]{r, g, bVal};
        }

        // 辅助函数
        private static double gammaExpand(double c) {
            return c > 0.04045 ? Math.pow((c + 0.055) / 1.055, 2.4) : c / 12.92;
        }

        private static double gammaCompress(double c) {
            return c > 0.0031308 ? 1.055 * Math.pow(c, 1/2.4) - 0.055 : 12.92 * c;
        }

        private static double pivotXyz(double t) {
            return t > EPSILON ? Math.pow(t, 1/3.0) : (KAPPA * t + 16) / 116;
        }

        private static double pivotXyzReverse(double t) {
            return t > 0.206893034 ? Math.pow(t, 3) : (t - 16 / 116.0) / KAPPA;
        }

        private static double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }
    }
}
