package com.model;

import java.io.Serializable;

/**
 * 保存首页数据的类
 * Created by 孤月悬空 on 2016/1/29.
 */
public class DataOfDay implements Serializable {
    private int currentEnergy;          //当前颈动量
    private int targetEnergy;           //目标颈动量
    private String dateNum;             //日期
    private int exerciseAngle;          //颈动角
    private int exerciseAmount;         //运动量
    private int energyConsumption;      //能量消耗
    private int lowerTime;              //低头时间
    private int rightTime;              //右倾时间
    private int leftTime;               //左倾时间

    public int getCurrentEnergy() {
        return currentEnergy;
    }

    public void setCurrentEnergy(int currentEnergy) {
        this.currentEnergy = currentEnergy;
    }

    public int getTargetEnergy() {
        return targetEnergy;
    }

    public void setTargetEnergy(int targetEnergy) {
        this.targetEnergy = targetEnergy;
    }

    public String getDateNum() {
        return dateNum;
    }

    public void setDateNum(String dateNum) {
        this.dateNum = dateNum;
    }

    public int getExerciseAngle() {
        return exerciseAngle;
    }

    public void setExerciseAngle(int exerciseAngle) {
        this.exerciseAngle = exerciseAngle;
    }

    public int getExerciseAmount() {
        return exerciseAmount;
    }

    public void setExerciseAmount(int exerciseAmount) {
        this.exerciseAmount = exerciseAmount;
    }

    public int getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(int energyConsumption) {
        this.energyConsumption = energyConsumption;
    }

    public int getLowerTime() {
        return lowerTime;
    }

    public void setLowerTime(int lowerTime) {
        this.lowerTime = lowerTime;
    }

    public int getRightTime() {
        return rightTime;
    }

    public void setRightTime(int rightTime) {
        this.rightTime = rightTime;
    }

    public int getLeftTime() {
        return leftTime;
    }

    public void setLeftTime(int leftTime) {
        this.leftTime = leftTime;
    }

    @Override
    public String toString() {
        return "DataOfDay{" +
                "currentEnergy=" + currentEnergy +
                ", targetEnergy=" + targetEnergy +
                ", dateNum='" + dateNum + '\'' +
                ", exerciseAngle=" + exerciseAngle +
                ", exerciseAmount=" + exerciseAmount +
                ", energyConsumption=" + energyConsumption +
                ", lowerTime=" + lowerTime +
                ", rightTime=" + rightTime +
                ", leftTime=" + leftTime +
                '}';
    }
}
