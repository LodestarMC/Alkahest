package team.lodestar.alkahest.core.render;


import net.minecraft.util.FastColor;
import team.lodestar.lodestone.helpers.util.Color;

public class ColorHelper {
    public static float FACTOR = 0.7f;
    public static Color getColor(int decimal) {
        int red = FastColor.ARGB32.red(decimal);
        int green = FastColor.ARGB32.green(decimal);
        int blue = FastColor.ARGB32.blue(decimal);
        return new Color(red, green, blue);
    }
    public static int hexToARGB(String hex) {
        Long intval = Long.decode(hex.replaceFirst("#", "0x"));
        long i = intval.intValue();

        int a = (int) (i >> 24) & 0xFF;
        int r = (int) (i >> 16) & 0xFF;
        int g = (int) (i >> 8) & 0xFF;
        int b = (int) (i) & 0xFF;

        return ((a & 0xFF) << 24) |
                ((b & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((r & 0xFF));
    }

    public static Color darker(Color color) {
        return new Color(Math.max((int)(color.getRed()  *FACTOR), 0),
                Math.max((int)(color.getGreen()*FACTOR), 0),
                Math.max((int)(color.getBlue() *FACTOR), 0),
                color.getAlpha());
    }

    public static Color brighter(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int)(1.0/(1.0-FACTOR));
        if ( r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        return new Color(Math.min((int)(r/FACTOR), 255),
                Math.min((int)(g/FACTOR), 255),
                Math.min((int)(b/FACTOR), 255),
                alpha);
    }
}
