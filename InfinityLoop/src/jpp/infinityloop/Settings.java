package jpp.infinityloop;

public class Settings {
    private static Settings ourInstance = new Settings();

    public static Settings getInstance() {
        return ourInstance;
    }

    private int minWidth = 8; // 8
    private int maxWidth = 12; // 12

    private int minHeight = 8; // 8
    private int maxHeight = 12; // 12

    private Settings() {
    }


    public int getMinWidth() {
        return minWidth;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public String[] getColors1() {
        String[] colors1 = new String[4];
        // blue
        colors1[0] = "#b3daff";     // background
        colors1[1] = "#004280";     // tile
        colors1[2] = "#001f4d";     // solved background old: 002e4d
        colors1[3] = "#00ffff";     // solved tile
        return colors1;
    }

    public String[] getColors2() {
        String[] colors2 = new String[4];
        // green
        colors2[0] = "#bef5a3";
        colors2[1] = "#00b300";
        colors2[2] = "#003300";
        colors2[3] = "#1aff1a"; // old: 00ff00

        return colors2;
    }

    public String[] getColors3() {
        String[] colors3 = new String[4];
        // gold/brown
        colors3[0] = "#f8ecb3";
        colors3[1] = "#b38600";
        colors3[2] = "#1a1300"; //old: 332500
        colors3[3] = "#ffd11a";

        return colors3;
    }

    public String[] getColors4() {
        String[] colors3 = new String[4];
        // purple
        colors3[0] = "#ffb3ff";
        colors3[1] = "#990099"; // old: 800080
        colors3[2] = "#330033"; // old: 4d004d
        colors3[3] = "#ff1aff";

        return colors3;
    }

    // inverted Colors for experimental

    public String[] getExp1() {
        String[] colors1 = new String[4];
        // blue
        colors1[0] = "#001f4d";     // background
        colors1[1] = "#00ffff";     // tile
        colors1[2] = "#b3daff";     // solved background old: 002e4d
        colors1[3] = "#004280";     // solved tile
        return colors1;
    }

    public String[] getExp2() {
        String[] colors2 = new String[4];
        // green
        colors2[0] = "#003300";
        colors2[1] = "#1aff1a";
        colors2[2] = "#bef5a3";
        colors2[3] = "#00b300";

        return colors2;
    }

    public String[] getExp3() {
        String[] colors3 = new String[4];
        // gold/brown
        colors3[0] = "#1a1300";
        colors3[1] = "#ffd11a";
        colors3[2] = "#f8ecb3"; //old: 332500
        colors3[3] = "#b38600";

        return colors3;
    }

    public String[] getExp4() {
        String[] colors3 = new String[4];
        // purple
        colors3[0] = "#330033";
        colors3[1] = "#ff1aff"; // old: 800080
        colors3[2] = "#ffb3ff"; // old: 4d004d
        colors3[3] = "#990099";

        return colors3;
    }

}
