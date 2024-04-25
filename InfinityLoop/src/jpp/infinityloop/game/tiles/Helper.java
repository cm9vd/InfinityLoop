package jpp.infinityloop.game.tiles;

public class Helper implements Comparable<Helper>{

    TileType tileType;
    double probability;

    public Helper(TileType tileType, double probability) {
        this.tileType = tileType;
        this.probability = probability;
    }

    public TileType getTileType() {
        return tileType;
    }

    public double getProbability() {
        return probability;
    }

   /* @Override
    public int compareTo(Object o) {
        Helper helper = (Helper) o;
        int a = (int) probability*100;
        int b = (int) helper.probability*100;
        return a-b;
    }*/

    @Override
    public int compareTo(Helper helper) {
        int a = (int) Math.round(probability*100);
        int b = (int) Math.round(helper.probability*100);

        return b-a;
    }
}
