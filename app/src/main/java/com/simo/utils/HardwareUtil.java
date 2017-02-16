package com.simo.utils;

import java.lang.*;

/**
 * Created by Ghosted on 2016/2/29.
 * 使用时需先通过HardwareUtil.setZ0初始化正常脖子和水平面的夹角
 * HardwareUtil.setDeltaPrecision初始化允许的误差值
 * HardwareUtil.setMaxValueY HardwareUtil.setMaxValueZ
 * 然后通过构造方法传入z（第一个数据）, y（第二个数据）, x（第三个数据）的值
 */
public class HardwareUtil {

    /**
     * 工具
     * 位置状态有低头、左倾、仰头、右倾、以及其他四组混合数据
     * @return HardwareUtil.DITOU 等
     */
    public String getPosition() {

        if(
                (equal(y, 90) || equal(y, -90)) &&
                (equal(z, 90) || equal(z, -90))
          ) return STANDARD;

        if(equal(y, 90) || equal(y, -90))
            if(z > 0) return DITOU;
            else return YANGTOU;


        if(equal(z, 90) || equal(z, -90))
            if(y > 0) return ZUOQIN;
            else return YOUQIN;


        if(z > 0)
            if(y > 0) return DITOUZUOQIN;
            else return DITOUYOUQIN;

        else {
            if(y > 0) return YANGTOUZUOQIN;
            else return YANGTOUYOUQIN;
        }


    }

    /**
     * 工具
     * 转换为角度
     * @return 0为摆幅过小或过大，-1为代码逻辑错误
     * 以x轴负半轴为基线（0度线），顺时针旋转形成的圆，其半径与基线的夹角
     */
    public int toCoordinate() {

        //摆幅过小
        if(getPosition().equals(HardwareUtil.STANDARD)) return 0;
        //摆幅过大
        if(Math.abs(z) < (90 - maxValueZ) || Math.abs(y) < (90 - maxValueY)) return 0;


        int angle;

        //谁更接近90度，就使用谁的值加入到环中
        if(z > 0 && y > 0) {
            if((90 - z) < (90 - y)) {
                angle = (int)((90-z)*45/maxValueZ);
            } else {
                angle = (int)(45+(45-(90-y)*45/maxValueY));
            }
        } else if(z > 0 && y < 0) {
            if((90 - z) > (90 + y)) {
                angle = (int)(90+(90+y)*45/maxValueY);
            } else {
                angle = (int)(135+(45-(90-z)*45/maxValueZ));
            }
        } else if (z < 0 && y < 0) {
            if((90 + z) < (90 + y)) {
                angle = (int)(180+(90+z)*45/maxValueZ);
            } else {
                angle = (int)(180+45+(45-(90+y)*45/maxValueY));
            }
        } else {
            if((90 + z) > (90 - y)) {
                angle = (int)(270+(90-y)*45/maxValueY);
            } else {
                angle = (int)(270+45+(45-(90+z)*45/maxValueZ));
            }
        }

        return angle;
    }


    /**
     * 初始化
     * 构造方法
     */
    public HardwareUtil(double z, double y, double x) {
        this.z = z;
        this.y = y;
        this.x = x;
        changeZ();
        check();
    }

    //////////////////////私有方法和GetSet方法/////////////////////////////

    private static double z0 = 90;
    private static double deltaPrecision = 0;
    private static double maxValueZ = 60;
    private static double maxValueY = 60;


    private double z;
    private double y;
    private double x;
    public static String DITOU = "100";
    public static String YANGTOU = "-100";
    public static String ZUOQIN = "010";
    public static String YOUQIN = "0-10";
    public static String DITOUZUOQIN = "110";
    public static String DITOUYOUQIN = "1-10";
    public static String YANGTOUZUOQIN = "-110";
    public static String YANGTOUYOUQIN = "-1-10";
    public static String STANDARD = "000";


    private void check() {}

    //把z转化为调整后的数值
    private void changeZ() {

        if(HardwareUtil.z0 == 90 || HardwareUtil.z0 == -90) {
            return;
        } else if(z == z0) {
            z = 90;
            return;
        }  else {
            if(z < 0) {
                z = -90 + (90 - HardwareUtil.z0) + (90 - (-z));
                if( z > 0 ) {
                    z = -1;
                }
            } else if(HardwareUtil.z0 - z > 0) {
                z = 90 - (HardwareUtil.z0 - z);
                if( z > 0 ) {
                    z = 1;
                }
            } else {
                z = -90 - (HardwareUtil.z0 - z);
                if( z > 0 ) {
                    z = -1;
                }
            }

        }

        if(z > 90 || z < -90) {
            System.out.printf("数据转换错误");
        }

    }


    private boolean equal(double a, double b) {
        return (Math.abs(a - b) <= deltaPrecision);
    }

    public static double getZ0() {
        return HardwareUtil.z0;
    }

    /**
     * 初始化
     * 设定脖子正常的值（因为人坐正时，脖子角度不一定恰好与地面垂直）
     * @param z0 （0~90）(90 为默认)
     */
    public static void setZ0(double z0) {

        if(z0 <= 0 || z0 >90) System.out.printf("z0不能为负");
        else HardwareUtil.z0 = z0;

    }

    /**
     * 允许的误差，默认值为0，即即使用户左倾了1度，也认定为左倾
     * @param deltaPrecision 允许忽略的度数
     */
    public static void setDeltaPrecision(double deltaPrecision) {
        HardwareUtil.deltaPrecision = deltaPrecision;
    }

    /**
     * 正常用户脖子旋转时，可能存在椭圆形旋转问题
     * 特殊值，很有用，预留备用
     * @param maxValueY 。。
     */
    public static void setMaxValueY(double maxValueY) {
        HardwareUtil.maxValueY = maxValueY;
    }

    /**
     * 特殊值，很有用，预留备用
     * @param maxValueZ 。。
     */
    public static void setMaxValueZ(double maxValueZ) {
        HardwareUtil.maxValueZ = maxValueZ;
    }
}
