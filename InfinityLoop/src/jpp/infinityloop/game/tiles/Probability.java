package jpp.infinityloop.game.tiles;

public class Probability {

    private double corner;
    private double edge;
    private double center;
    private double all;

    public Probability(double corner, double edge, double center, double all) {
        this.corner = corner;
        this.edge = edge;
        this.center = center;
        this.all = all;
    }

    public double getCorner() {
        return corner;
    }

    public void setCorner(double corner) {
        this.corner = corner;
    }

    public double getEdge() {
        return edge;
    }

    public void setEdge(double edge) {
        this.edge = edge;
    }

    public double getCenter() {
        return center;
    }

    public void setCenter(double center) {
        this.center = center;
    }

    public double getAll() {
        return all;
    }

    public void setAll(double all) {
        this.all = all;
    }

    public double get(Location location){
        switch (location){
            case CORNER: return getCorner();
            case EDGE: return getEdge();
            case CENTER: return getCenter();
            default:
                throw new IllegalArgumentException("No such location found at Probability.get");
        }
    }

    public void set(Location location, double prob){
        switch (location){
            case CORNER:
                setCorner(prob);
                break;
            case EDGE:
                setEdge(prob);
                break;
            case CENTER:
                setCenter(prob);
                break;
            default:
                throw new IllegalArgumentException("No such location found at Probability.set");
        }
    }
}


