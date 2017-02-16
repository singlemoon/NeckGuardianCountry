package com.model;

/**
 * 用户类
 * Created by 孤月悬空 on 2015/12/23.
 */
public class User {
    private int rankNumber;
    private String pic;

    private String nickName;
    private String answerMax;
    private String carMax;
    private String neckMax;
    private String counts;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRankNumber() {
        return rankNumber;
    }

    public void setRankNumber(int rankNumber) {
        this.rankNumber = rankNumber;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAnswerMax() {
        return answerMax;
    }

    public void setAnswerMax(String answerMax) {
        this.answerMax = answerMax;
    }

    public String getCarMax() {
        return carMax;
    }

    public void setCarMax(String carMax) {
        this.carMax = carMax;
    }

    public String getNeckMax() {
        return neckMax;
    }

    public void setNeckMax(String neckMax) {
        this.neckMax = neckMax;
    }

    public String getCounts() {
        return counts;
    }

    public void setCounts(String counts) {
        this.counts = counts;
    }

    @Override
    public String toString() {
        return "User{" +
                "rankNumber=" + rankNumber +
                ", pic='" + pic + '\'' +
                ", nickName='" + nickName + '\'' +
                ", answerMax='" + answerMax + '\'' +
                ", carMax='" + carMax + '\'' +
                ", neckMax='" + neckMax + '\'' +
                ", counts='" + counts + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
