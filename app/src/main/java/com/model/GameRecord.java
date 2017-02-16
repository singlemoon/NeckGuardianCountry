package com.model;

/**
 * Created by 孤月悬空 on 2016/3/10.
 */
public class GameRecord {
    private String show_time;
    private String game_time;
    private String game_name;
    private int game_use_time;
    private int completeness;

    public String getShow_time() {
        return show_time;
    }

    public void setShow_time(String show_time) {
        this.show_time = show_time;
    }

    public String getGame_time() {
        return game_time;
    }

    public void setGame_time(String game_time) {
        this.game_time = game_time;
    }

    public String getGame_name() {
        return game_name;
    }

    public void setGame_name(String game_name) {
        this.game_name = game_name;
    }

    public int getGame_use_time() {
        return game_use_time;
    }

    public void setGame_use_time(int game_use_time) {
        this.game_use_time = game_use_time;
    }

    public int getCompleteness() {
        return completeness;
    }

    public void setCompleteness(int completeness) {
        this.completeness = completeness;
    }

    @Override
    public String toString() {
        return "GameRecord{" +
                "show_time='" + show_time + '\'' +
                ", game_time='" + game_time + '\'' +
                ", game_name='" + game_name + '\'' +
                ", game_use_time=" + game_use_time +
                ", completeness=" + completeness +
                '}';
    }
}
