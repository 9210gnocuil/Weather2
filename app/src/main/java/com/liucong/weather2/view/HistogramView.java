package com.liucong.weather2.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.liucong.weather2.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liucong on 2017/3/12.
 *
 * 柱形图
 * 用户需要传进来两个数据
 * 一个是数值数据
 * 一个是文字描述数组
 * 可以考虑将两个数据封装成对象 然后传进来集合
 *
 * 自定义控件步骤：
 * 1，定义属性，制作attrs.xml文件
 * 2，重写构造方法【1，2，3参数，重写三参数】
 * 3，重写onDraw()方法，绘制View
 * 4，重写onMeasure()方法
 * 5，添加控件的响应事件
 *
 *
 * 目前默认数据都能显示完全，还有几个问题：
 * 1.当控件是指定大小(小于屏幕宽度的时候) 应该将底部的文字缩小 根据dpi来进行设置
 * 2.各个画笔的颜色，应该暴露方法让用户设置，
 * 3.虚线问题， 这个需要让用户选择是否显示 虚线颜色
 * 4.两个点之间的连线，同理需要让用户选择是否显示 连线颜色
 * 5.是否显示xy坐标轴 坐标轴颜色可以设置
 * 6.是否显示点旁边的数值，这个值的颜色让用户自己设置
 * 7.点的那个圆的半径 让用户设置 点的颜色
 *
 * 还有需要画上坐标轴的箭头
 */

public class HistogramView extends View {

    private static final String TAG = "HistogramView";

    //需要用户传进来的数据，用户在坐标系中显示
    private ArrayList<Integer> dataList;
    //max 这个需要用户传进来 用于计算在y轴单位上的单位值
    private int max;

    //将用户传进来的数据进行计算后转换成坐标并存放到集合中
    private List<Point> pointList;
    //这个是用来存放x轴下面的文字，将其封装成对象存入集合
    private List<Point> bottomTextPosition;

    private List<String> bottomTextList;

    //上下文环境
    private Context ctx;

    //单位宽高，两点之间的x轴方向距离
    //单位宽在这个View测量完毕后就可以计算出来
    private int baseWidth;
    //单位高 需要先计算底部显示的文字的高度后才能计算
    private int baseHeight;

    private int tvHeight;
    private int tvWidth;


    //画点到点之间的线的画笔
    private Paint drawPoint2PointLine;

    //画底部文字的画笔
    private Paint drawBottomText;

    //画数据的画笔
    private Paint drawDateText;

    //画显示在坐标里面的点的画笔
    private Paint drawPoint;

    //画x轴上面的刻度点的画笔
    private Paint drawXAxisPoint;

    //画x轴线的画笔
    private Paint drawXAxisLine;

    //画虚线的画笔
    private Paint drawImaginaryLine;


    //设置底部文字大小 单位为dp
    private int bottomTextSize = 16;
    private int dataTextSize = 12;

    //设置线的粗细度 默认为2sp
    private int lineWidth = 2;

    private int alpha = 178;

    //画笔的颜色
    //相邻两个坐标点之间的连线颜色
    private int colorDrawPoint2Point;
    //底部文字的颜色
    private int colorDrawBottemText;
    //显示的数据旁的文字的颜色
    private int colorDrawDateText;
    //坐标中的数据点的圆的颜色
    private int colorDrawPoint;
    //x轴的线的颜色
    private int colorDrawXAxisLine;
    //x轴上的刻度点的颜色
    private int colorDrawXAxisPoint;
    //画虚线的颜色 这个虚线是指从数据点到x轴之间的距离连线
    private int colorDrawImaginaryLine;


    //默认都显示
    //xy轴是否需要显示
    private boolean isAxisAvailable = true;
    //是否显示相邻数据点之间的连线
    private boolean isPoint2PointLineAvailable = true;
    //是否需要显示底部文字
    private boolean isBottemTextAvailable = true;
    //是否需要显示数据文字
    private boolean isDateTextAvailable = true;
    //是否需要虚线
    private boolean isImaginaryLineAvailable = true;
    //

    public HistogramView(Context context) {
        this(context,null);
    }

    public HistogramView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HistogramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ctx = context;
        init();
    }

    //初始化
    private void init() {
        initPaintColors();
        initPaints();
        initData();
    }

    //初始化数据
    private void initData() {
        dataList = new ArrayList<Integer>();
        dataList.add(12);
        dataList.add(6);
        dataList.add(15);
        dataList.add(5);
        dataList.add(24);
        dataList.add(3);
        dataList.add(22);

        bottomTextList = new ArrayList<String>();
        bottomTextList.add("04:00");
        bottomTextList.add("07:00");
        bottomTextList.add("10:00");
        bottomTextList.add("13:00");
        bottomTextList.add("16:00");
        bottomTextList.add("19:00");
        bottomTextList.add("22:00");

        max = 25;

        //初始化底部text的中心坐标
        bottomTextPosition = new ArrayList<Point>();
    }
    
    private void initPaintColors(){
        colorDrawPoint2Point = Color.WHITE;
        colorDrawBottemText = Color.WHITE;
        colorDrawDateText = Color.WHITE;
        colorDrawPoint = Color.WHITE;
        colorDrawXAxisLine = Color.WHITE;
        colorDrawXAxisPoint = Color.WHITE;
        colorDrawImaginaryLine = Color.WHITE;
    }

    private void initPaints() {

        lineWidth = DisplayUtils.sp2px(ctx,2);

        //两点之间
        drawPoint2PointLine = new Paint();
        drawPoint2PointLine.setStrokeWidth(lineWidth);
        drawPoint2PointLine.setColor(colorDrawPoint2Point);
        drawPoint2PointLine.setAlpha(alpha);

        //x轴线
        drawXAxisLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawXAxisLine.setStrokeWidth(lineWidth);
        drawXAxisLine.setColor(colorDrawXAxisLine);
        drawXAxisLine.setAlpha(alpha);

        //画底部文字的画笔
        //抗锯齿
        drawBottomText = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawBottomText.setColor(colorDrawBottemText);
        drawBottomText.setAlpha(alpha);
        //画文字的笔设定大小为16sp
        drawBottomText.setTextSize(DisplayUtils.sp2px(ctx,bottomTextSize));//px   --TextView.setTextSize(30);//sp

        //画数据文字的笔
        drawDateText = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawDateText.setColor(colorDrawDateText);
        drawDateText.setAlpha(alpha);
        //画文字的笔设定大小为16sp
        drawDateText.setTextSize(DisplayUtils.sp2px(ctx,dataTextSize));//px   --TextView.setTextSize(30);//sp

        //画数据圆点的画笔
        drawPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawPoint.setStyle(Paint.Style.FILL);
        drawPoint.setColor(colorDrawPoint);
        drawPoint.setAlpha(alpha);

        //画坐标上的点的画笔
        drawXAxisPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawXAxisPoint.setStyle(Paint.Style.FILL);
        drawXAxisPoint.setColor(colorDrawXAxisPoint);
        drawXAxisPoint.setAlpha(alpha);

        //画虚线的画笔
        drawImaginaryLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        drawImaginaryLine.setStrokeWidth(lineWidth);
        drawImaginaryLine.setColor(colorDrawImaginaryLine);
        drawImaginaryLine.setAlpha(alpha);
    }

    //根据传进来的数据计算出对应的坐标值
    private List<Point> computeEachPointCoordinate(ArrayList<Integer> dataList, int max) {
        List<Point> pointList = new ArrayList<Point>();

        int y = getHeight()-tvHeight-30;

        int tx ;
        int ty ;

        for(int i=0;i<dataList.size();i++){
            tx = ((i+1)*baseWidth);
            ty = y-baseHeight*dataList.get(i);
            Log.i("PointCoordinate", "tx="+tx+" "+"    ty="+ty);
            Log.i("PointCoordinate", "dataist="+dataList.toString());
            pointList.add(new Point(tx,ty));
        }
        return pointList;
    }


    private void drawLineFunc(Canvas canvas,Point start,Point end,Paint paint){
        canvas.drawLine(start.x,start.y,end.x,end.y,paint);
    }

    //画文字
    private void drawTextFunc(Canvas canvas,String str,int startX,int startCenterY,Paint drawText){
        //经过下面处理就基本是从(300,500)开始的文字中间差不多是300
        Paint.FontMetrics fontMetrics = drawText.getFontMetrics();
        float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
        float offY = fontTotalHeight / 2 - fontMetrics.bottom;
        float newY = startCenterY + offY;
        canvas.drawText(str, startX, newY, drawText);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //传进去的文字讲道理 高度应该相同，宽度可能不同
        // 这里先简单地去拿第一串文字来测量高度
        //只有在画文字的时候需要拿到对应字符串的宽度，在循环里进行计算
        String drawStr = bottomTextList.get(0);
        //计算底部文字的宽高
        int[] wh = computeTextWidth(drawBottomText, drawStr);
        tvHeight = wh[1];


        //第一个点在0.5(Y轴) 1，第一个数据，2，第二个数据 照此类推
        baseWidth = getWidth()/(dataList.size()+1);
        baseHeight = (getHeight()-tvHeight-30)/max;

        //画两个坐标轴
        canvas.drawLine(baseWidth/2,getHeight()-tvHeight-30,getWidth(),getHeight()-tvHeight-30,drawXAxisLine);
        canvas.drawLine(baseWidth/2,0,baseWidth/2,getHeight()-tvHeight-30,drawXAxisLine);

        //画上箭头
        // y轴：终点(baseWidth/2,0)  起点(baseWidth/2+-3,0)
        // x轴: 终点(getHeight(),getHeight()-tvHeight-30)  起点getHeight(),getHeight()-tvHeight-30)
        //Y轴箭头
        canvas.drawLine(baseWidth/2+DisplayUtils.dip2px(ctx,6),DisplayUtils.dip2px(ctx,15),baseWidth/2,0,drawXAxisLine);
        canvas.drawLine(baseWidth/2-DisplayUtils.dip2px(ctx,6),DisplayUtils.dip2px(ctx,15),baseWidth/2,0,drawXAxisLine);

        //X轴箭头
        //起点(getWidth(),getHeight()-tvHeight-30) 终点 (getWidth()+-15,getHeight()-tvHeight-30-6)
        canvas.drawLine(getWidth()-DisplayUtils.dip2px(ctx,15),getHeight()-tvHeight-30-DisplayUtils.dip2px(ctx,6),getWidth(),getHeight()-tvHeight-30,drawXAxisLine);
        canvas.drawLine(getWidth()-DisplayUtils.dip2px(ctx,15),getHeight()-tvHeight-30+DisplayUtils.dip2px(ctx,6),getWidth(),getHeight()-tvHeight-30,drawXAxisLine);

        //根据传进来的数据 计算出每个点的坐标
        pointList = computeEachPointCoordinate(dataList, max);

        for (int i=0;i<dataList.size();i++){

            //如果设置不画线 则直接跳过
            if (isPoint2PointLineAvailable) {
                if(i+1<pointList.size()) {
                    // 在每相邻的两个点上画上线
                    drawLineFunc(canvas, pointList.get(i), pointList.get(i + 1), drawPoint2PointLine);
                }
            }
            //在点上画上圆点 醒目
            canvas.drawCircle(pointList.get(i).x,pointList.get(i).y,10, drawPoint);

            if(isImaginaryLineAvailable) {
                //画虚线 点到X轴之间的虚线
                drawImaginaryLine(canvas, pointList.get(i).x, pointList.get(i).y, pointList.get(i).x, getHeight() - tvHeight - 30, 10, drawImaginaryLine);
            }

            if(isBottemTextAvailable) {
                //在坐标轴上面画上对应刻度的信息 第三个参数-10 修正位置
                int tempTvWidth = computeTextWidth(drawBottomText, bottomTextList.get(i))[0];

                //画底边文字
                drawTextFunc(canvas,bottomTextList.get(i),((i+1)*baseWidth-tempTvWidth/2),getHeight()-tvHeight/2-10,drawBottomText);
            }
            if(isAxisAvailable) {
                //在坐标轴上面画上刻度点
                canvas.drawCircle((i + 1) * baseWidth, getHeight() - tvHeight - 30, 3, drawXAxisPoint);
            }
            if(isDateTextAvailable) {
                //在每个点旁边画上对应的天气度数
                drawTextFunc(canvas, dataList.get(i) + "°", pointList.get(i).x + computeTextWidth(drawDateText, dataList.get(i) + "`")[0] / 2, pointList.get(i).y, drawDateText);
            }
        }
    }

    //画虚线 思路是每间隔一段距离画直线
    private void drawImaginaryLine(Canvas canvas,int startX,int startY,int endX,int endY,int gap,Paint drawImaginaryLine){
        //记录每次虚线的起始地址。
        int tstartY;
        int tendY ;

        for(int i=0;;i++){
            //计算虚线的起始位置
            tstartY = startY+gap*(2*i+1);
            tendY = startY+gap*2*(i+1);

            //如果还没画到终点 就一直画
           if(tendY<endY) {
               canvas.drawLine(startX, tstartY, startX, tendY, drawImaginaryLine);
           }else{
               //画到终点了就停止画了。
               return;
           }
       }
    }

    //计算文本的宽高
    private int[] computeTextWidth(Paint drawText,String str){
        int[] wh = new int[2];
        Rect rect = new Rect();
        drawText.getTextBounds(str, 0, str.length(), rect);
        wh[0] = rect.width();
        wh[1] = rect.height();
        return wh;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);

        setMeasuredDimension(width,height);
    }
    //测量宽度 这里 即使设定了wrap_content,还是将宽度设置为父控件大小
    private int measureWidth(int widthMeasureSpec) {

        int result = 0;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        switch (mode){
            case MeasureSpec.UNSPECIFIED:
                //不确定
                break;
            case MeasureSpec.AT_MOST:
                //至多 wrap_content
                result = size;
                break;
            case MeasureSpec.EXACTLY:
                //match_parent、50dp
                result = size;
                break;
        }
        return  result;
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        switch (mode){
            case MeasureSpec.UNSPECIFIED:
                //不确定
                break;
            case MeasureSpec.AT_MOST:
                //至多 wrap_content
                result = size;
                break;
            case MeasureSpec.EXACTLY:
                //match_parent、50dp
                result = size;
                break;
        }
        return  result;
    }


    /**
     * 将所需要的数据传进来
     * 设置颜色和某些连线的显示需要在这个方法之前调用 不然无法生效
     * @param dataList 显示在坐标上面的点的数据集合
     * @param max 传进来的数据里面的最大值(最好整数 好分配空间)
     */
    public void setData(ArrayList<Integer> dataList,ArrayList<String> bottomTextList,int max){

        this.dataList = dataList;

        this.bottomTextList = bottomTextList;
        this.max = max;

        //数据传进来了后就要刷新数据了
        invalidate();
    }

    /**
     * 设置底部文字颜色
     * 需要在setData()之前调用 {@link #setData}
     * @param colorDrawBottemText
     */
    public void setColorDrawBottemText(int colorDrawBottemText) {
        this.colorDrawBottemText = colorDrawBottemText;
    }

    /**
     * 设置虚线颜色
     * 需要在setData()之前调用 {@link #setData}
     * @param colorDrawImaginaryLine
     */
    public void setColorDrawImaginaryLine(int colorDrawImaginaryLine) {
        this.colorDrawImaginaryLine = colorDrawImaginaryLine;
    }

    /**
     * 设置相邻点的连线的颜色
     * 需要在setData()之前调用 {@link #setData}
     * @param colorDrawPoint2Point
     */
    public void setColorDrawPoint2Point(int colorDrawPoint2Point) {
        this.colorDrawPoint2Point = colorDrawPoint2Point;
    }

    /**
     * 设置xy轴的颜色
     * 需要在setData()之前调用 {@link #setData}
     * @param colorDrawXAxisLine
     */
    public void setColorDrawXAxisLine(int colorDrawXAxisLine) {
        this.colorDrawXAxisLine = colorDrawXAxisLine;
    }

    /**
     * 设置数据文字的颜色
     * 需要在setData()之前调用 {@link #setData}
     * @param colorDrawDateText
     */
    public void setColorDrawDateText(int colorDrawDateText) {
        this.colorDrawDateText = colorDrawDateText;
    }

    /**
     * 设置数据圆点的颜色
     * 需要在setData()之前调用 {@link #setData}
     * @param colorDrawPoint
     */
    public void setColorDrawPoint(int colorDrawPoint) {
        this.colorDrawPoint = colorDrawPoint;
    }

    /**
     * 设置x坐标轴上面的刻度点的颜色
     * 需要在setData()之前调用 {@link #setData}
     * @param colorDrawXAxisPoint
     */
    public void setColorDrawXAxisPoint(int colorDrawXAxisPoint) {
        this.colorDrawXAxisPoint = colorDrawXAxisPoint;
    }


    /**
     * 是否显示xy轴 默认显示
     * 需要在setData()之前调用 {@link #setData}
     * @param axisAvailable
     */
    public void setAxisAvailable(boolean axisAvailable) {
        isAxisAvailable = axisAvailable;
    }

    /**
     * 是否显示相邻数据点之间的连线 默认显示
     * 需要在setData()之前调用 {@link #setData}
     * @param point2PointLineAvailable
     */
    public void setPoint2PointLineAvailable(boolean point2PointLineAvailable) {
        isPoint2PointLineAvailable = point2PointLineAvailable;
    }

    /**
     * 是否需要显示数据文字 默认显示
     * 需要在setData()之前调用 {@link #setData}
     * @param dateTextAvailable
     */
    public void setDateTextAvailable(boolean dateTextAvailable) {
        isDateTextAvailable = dateTextAvailable;
    }

    /**
     * 是否需要显示底部文字 默认显示
     * 需要在setData()之前调用 {@link #setData}
     * @param bottemTextAvailable
     */
    public void setBottemTextAvailable(boolean bottemTextAvailable) {
        isBottemTextAvailable = bottemTextAvailable;
    }

    /**
     * 是否需要显示虚线 默认显示
     * 需要在setData()之前调用 {@link #setData}
     * @param imaginaryLineAvailable
     */
    public void setImaginaryLineAvailable(boolean imaginaryLineAvailable) {
        isImaginaryLineAvailable = imaginaryLineAvailable;
    }


    /**
     * 设置底部文字大小 单位为dp 默认为16dp
     * @param bottomTextSize
     */
    public void setBottomTextSize(int bottomTextSize) {
        this.bottomTextSize = bottomTextSize;
    }

    /**
     * 设置数据文字大小 单位同样为dp 默认为12dp
     * @param dataTextSize
     */
    public void setDataTextSize(int dataTextSize) {
        this.dataTextSize = dataTextSize;
    }
}
