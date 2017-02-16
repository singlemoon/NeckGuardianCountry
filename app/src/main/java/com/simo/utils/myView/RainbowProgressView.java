package com.simo.utils.myView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 孤月悬空 on 2016/2/17.
 */
public class RainbowProgressView extends View {
    private float progress = 360;
    private int max = 360;//为了使动画更加流畅  动画是由(progress+1)形成

    /**
     * 环/画笔 的路径宽度
     */
    private int pathWidth = 45;
    /**
     * 默认圆的半径
     */
    private int radius = 50;

    private int width;
    private int height;

    /**
     * 梯度渐变的填充颜色
     */
    /*private int[] arcColors = new int[]{0xFF599cd1, 0xFF7d70b8, 0xFFc8417b,
            0xFFe35a61, 0xFFe98d42, 0xFFdabf4a,
            0xFFdabf4a, 0xFF4defc8, 0xFF47c8db, 0xFF599cd1};*/
    private int[] arcColors = new int[]{
            0xFF53BAEA, 0xFFF260A2, 0xFFF3C74C, 0xFF5CF5D9, 0xFF53BAEA
    };
    /**
     * 阴影颜色组
     */
    private int[] shadowsColors = new int[]{0xFF111111, 0x00AAAAAA, 0x00AAAAAA};
    // 轨迹绘制点
    private Paint pathPaint = null;
    // 绘制填充
    private Paint fillArcPaint = null;
    private RectF oval;
    // 灰色轨迹
    private int pathColor = Color.WHITE;//0xFFF0EEDF
    private int pathBorderColor = 0xFFD2D1C4;

    //    // 指定了光源的方向和环境光强度来添加浮雕效果
//    private EmbossMaskFilter emboss = null;
//    // 设置光源的方向
//    float[] direction = new float[]{1, 1, 1};
//    // 设置环境光亮度
//    float light = 0.4f;
//    // 选择要应用的反射等级
//    float specular = 6;
//    // 向 mask应用一定级别的模糊
//    float blur = 3.5f;
//    // 指定了一个模糊的样式和半径来处理 Paint 的边缘
//    private BlurMaskFilter mBlur = null;
    // view重绘的标记
    private boolean reset = false;

    //[end]---参数-----------------------------------------------------
    public RainbowProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);//是否抗锯齿
        pathPaint.setFlags(Paint.ANTI_ALIAS_FLAG);// 帮助消除锯齿
        pathPaint.setStyle(Paint.Style.STROKE);// 设置中空的样式
        pathPaint.setDither(true);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);

        fillArcPaint = new Paint();
        fillArcPaint.setAntiAlias(true);// 设置是否抗锯齿
        fillArcPaint.setFlags(Paint.ANTI_ALIAS_FLAG);// 帮助消除锯齿
        fillArcPaint.setStyle(Paint.Style.STROKE);//中空的样式
        fillArcPaint.setDither(true);
        fillArcPaint.setStrokeJoin(Paint.Join.ROUND);

        oval = new RectF();
//        emboss = new EmbossMaskFilter(direction, light, specular, blur);
//        mBlur = new BlurMaskFilter(30, BlurMaskFilter.Blur.OUTER);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (reset) {
            canvas.drawColor(Color.TRANSPARENT);
            reset = false;
        }
        this.width = getMeasuredWidth();
        this.height = getMeasuredHeight();
        this.pathWidth = (getMeasuredHeight() / 2) / 4;
        this.radius = getMeasuredHeight() / 2 - pathWidth;
//[start]------渐变圆环白色底环------------------------
        pathPaint.setColor(pathColor);// 画笔
        pathPaint.setStrokeWidth(pathWidth);//圆环边框宽度
//        pathPaint.setMaskFilter(emboss);//   浮雕效果
        int outerCircleRadius = (this.height / 2) - pathWidth / 2;//外环半径
        //[绘制]-渐变圆环底色圆	 在中心的地方画个半径为r的白色圆  半径与渐变圆环相同
        canvas.drawCircle(this.width / 2, this.height / 2, outerCircleRadius - 1f, pathPaint);
        pathPaint.setStrokeWidth(0.5f);// 底色圆环边线//宽度
        pathPaint.setColor(pathBorderColor);//
        //[绘制]-渐变圆环底色圆边线-玻璃效果边 外
        canvas.drawCircle(this.width / 2, this.height / 2, height / 2 - 0.5f, pathPaint);
        //[绘制]-渐变圆环底色圆边线-玻璃效果边 内
        canvas.drawCircle(this.width / 2, this.height / 2, (this.height / 2) - pathWidth - 0.5f, pathPaint);
//[end]------渐变圆环底环------------------------

//[start]------[渐变圆环]用来调颜色      定义环形颜色  填充  按照圆心扫描绘制-------------------------
        SweepGradient sweepGradient = new SweepGradient(this.width / 2,
                this.height / 2, arcColors, null);

        fillArcPaint.setShader(sweepGradient);//着色器
//        fillArcPaint.setMaskFilter(mBlur);// 模糊效果

        fillArcPaint.setStrokeCap(Paint.Cap.ROUND);// 设置线类型,圆边
        fillArcPaint.setStrokeWidth(pathWidth);//圆环边框宽度
        //定义 渐变圆环 形状与大小    [详见文档-关于drawArc()]
        oval.set(this.width / 2 - this.height / 2 + (pathWidth / 2) + 0.5f,                  //矩形左方位置	自身总宽度的一半减去自身高度的一半,加上圆环描边的宽度
                0 + (pathWidth / 2 + 0.5f),                                          //矩形上方位置	自身顶部x轴位置减去圆环宽度的一半
                this.width - (this.width / 2 - this.height / 2) - (pathWidth / 2) - 1.5f,//矩形右方位置	自身宽度减去(总宽度的一半减去 高的一半即圆环半径)减去圆环宽度一半减去圆环边框总宽度
                this.height - (pathWidth / 2) - 0.5f);                              //矩形下方位置	高度减去圆环宽度一半减去圆环边框
        //[绘制]-渐变圆环  第一个参数为:定义形状和大小，第二个参数为：圆环起始角度/开始的地方，第三个为跨的角度/扫描角度/圆环显示的百分比，第四个为true的时候是实心，false的时候为空心
        canvas.drawArc(oval, -90, progress, false,
                fillArcPaint);
//[end]------[渐变圆环] 定义环形颜色  填充--------------------------

//[start]------中心白色圆------------------------
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE); //设置填满
        p.setAntiAlias(true);            //设置画笔的锯齿效果。 true是去除，大家一看效果就明白了
        RectF oval = new RectF();        //定义形状和大小
//        BlurMaskFilter mBlur = new BlurMaskFilter(19, BlurMaskFilter.Blur.NORMAL);
//        p.setMaskFilter(mBlur);
//        EmbossMaskFilter embossemboss = new EmbossMaskFilter(direction, light, specular, blur); // 添加浮雕效果
//        p.setMaskFilter(embossemboss);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.WHITE);
        //[绘制]-实心圆
        canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, this.height / 2 - pathWidth - 20, p);//
        canvas.drawCircle(this.getWidth() / 2, this.getHeight() / 2, this.height / 2 - pathWidth - 20 + 0.5f, pathPaint);
//[end]----------------------------------------
    }

    /**
     * 描述：获取圆的半径
     *
     * @return
     * @throws
     */
    public int getRadius() {
        return radius;
    }

    /**
     * 描述：设置圆的半径
     *
     * @param radius
     * @throws
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public float getProgress() {
        return progress / 3.6f;
    }

    /**
     * 描述：设置圆的进度
     *
     * @return
     */
    public void setProgress(float progress) {
        this.progress = progress * 3.6f;
        this.invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, height);
    }
}

