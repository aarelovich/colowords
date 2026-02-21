package org.colowords;

import android.graphics.Color;
import java.util.ArrayList;
import java.util.List;

public class ContrastPicker {
    // --- Public API ---
    public static int pickHighContrastColor(int[] colors) {

        List<Integer> candidates = buildCandidates();
        double bestScore = -1.0;
        int bestColor = Color.BLACK;

        for (int c : candidates) {
            double score = minContrastAgainstList(c, colors);
            if (score > bestScore) {
                bestScore = score;
                bestColor = c;
            }
        }
        return bestColor;
    }

    // If you want a "guarantee" style check:
    // Returns null if no candidate meets threshold (e.g., 4.5)
    public static Integer pickColorMeetingThreshold(int[] colors, double minContrastThreshold) {
        List<Integer> candidates = buildCandidates();
        double bestScore = -1.0;
        Integer best = null;

        for (int c : candidates) {
            double score = minContrastAgainstList(c, colors);
            if (score >= minContrastThreshold && score > bestScore) {
                bestScore = score;
                best = c;
            }
        }
        return best;
    }

    // --- Scoring ---
    private static double minContrastAgainstList(int candidate, int[] colors) {
        double min = Double.POSITIVE_INFINITY;
        for (int c : colors) {
            double cr = contrastRatio(candidate, c);
            if (cr < min) min = cr;
        }
        return min;
    }

    private static double contrastRatio(int a, int b) {
        double la = relativeLuminance(a);
        double lb = relativeLuminance(b);
        double l1 = Math.max(la, lb);
        double l2 = Math.min(la, lb);
        return (l1 + 0.05) / (l2 + 0.05);
    }

    // --- Luminance ---
    private static double relativeLuminance(int color) {
        double r = srgbToLinear(Color.red(color) / 255.0);
        double g = srgbToLinear(Color.green(color) / 255.0);
        double b = srgbToLinear(Color.blue(color) / 255.0);
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    private static double srgbToLinear(double c) {
        if (c <= 0.04045) return c / 12.92;
        return Math.pow((c + 0.055) / 1.055, 2.4);
    }

    // --- Candidate generation ---
    private static List<Integer> buildCandidates() {
        ArrayList<Integer> list = new ArrayList<>();

        // Always include these
        list.add(Color.BLACK);
        list.add(Color.WHITE);

        // Small RGB grid (fast + surprisingly effective)
        int[] steps = new int[] { 0, 32, 64, 96, 128, 160, 192, 224, 255 };
        for (int r : steps) {
            for (int g : steps) {
                for (int b : steps) {
                    list.add(Color.rgb(r, g, b));
                }
            }
        }

        // HSL sweep: many hues, several lightness levels, decent saturation
        float[] hues = new float[] { 0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330 };
        float[] lightness = new float[] { 0.10f, 0.18f, 0.26f, 0.34f, 0.42f, 0.50f, 0.58f, 0.66f, 0.74f, 0.82f, 0.90f };
        float sat = 0.85f;

        for (float h : hues) {
            for (float l : lightness) {
                list.add(hslToColor(h, sat, l));
            }
        }
        return list;
    }

    // HSL -> RGB (no alpha)
    private static int hslToColor(float h, float s, float l) {
        float c = (1.0f - Math.abs(2.0f * l - 1.0f)) * s;
        float hp = (h % 360.0f) / 60.0f;
        float x = c * (1.0f - Math.abs(hp % 2.0f - 1.0f));

        float r1 = 0, g1 = 0, b1 = 0;
        if (0 <= hp && hp < 1) { r1 = c; g1 = x; b1 = 0; }
        else if (1 <= hp && hp < 2) { r1 = x; g1 = c; b1 = 0; }
        else if (2 <= hp && hp < 3) { r1 = 0; g1 = c; b1 = x; }
        else if (3 <= hp && hp < 4) { r1 = 0; g1 = x; b1 = c; }
        else if (4 <= hp && hp < 5) { r1 = x; g1 = 0; b1 = c; }
        else if (5 <= hp && hp < 6) { r1 = c; g1 = 0; b1 = x; }

        float m = l - c / 2.0f;
        int r = clamp255((r1 + m) * 255.0f);
        int g = clamp255((g1 + m) * 255.0f);
        int b = clamp255((b1 + m) * 255.0f);
        return Color.rgb(r, g, b);
    }

    private static int clamp255(float v) {
        if (v < 0) return 0;
        if (v > 255) return 255;
        return Math.round(v);
    }
}
