package me.priyesh.pebblesms;

import android.graphics.Color;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.Random;

public class LetterDrawable {

    private static final String[] COLORS = {
            "#F44336", "#E91E63", "#9C27B0",
            "#673AB7", "#FF5722", "#009688",
            "#4CAF50", "#2196F3", "#3F51B5"
    };

    private static int randomColor() {
        return Color.parseColor(COLORS[new Random().nextInt(COLORS.length)]);
    }

    public static TextDrawable get(String name) {
        return TextDrawable.builder().buildRound(name.substring(0, 1), randomColor());
    }
}
