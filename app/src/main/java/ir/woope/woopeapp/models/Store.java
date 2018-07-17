package ir.woope.woopeapp.models;

/**
 * Created by Hamed on 6/10/2018.
 */

public class Store {
    private String name;
    private String point;
    private int numOfSongs;
    private int thumbnail;

    public Store() {
    }

    public Store(String name,String point, int numOfSongs, int thumbnail) {
        this.name = name;
        this.point = point;
        this.numOfSongs = numOfSongs;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumOfSongs() {
        return numOfSongs;
    }

    public void setNumOfSongs(int numOfSongs) {
        this.numOfSongs = numOfSongs;
    }

    public int getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(int thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
