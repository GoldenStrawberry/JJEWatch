package so.wih.android.jjewatch.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

/**
 * ==============================================
 * Created by HuWei on 2016/12/20.
 *   表盘
 * @GitHub : https://github.com/GoldenStrawberry
 * @blog : http://blog.csdn.net/hnkwei1213
 * ===============================================
 */

public class MyView extends View {

    private int mWidth;
    private int mHeight;
    private Paint mCirclePaint;
    private int circleRadius ;
    private Paint mPaintLine;
    private Paint mPaintText;
    private Calendar mCalendar;
    public static final int what_update = 100;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case what_update :
                    mCalendar=Calendar.getInstance();//这一句必须有，如果没有就不刷新时间
                    invalidate();//重绘界面 会调用onDraw方法
                    handler.sendEmptyMessageDelayed(what_update,1000);
                    break;
            }
        }
    };

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context context) {
        super(context);
        init();
    }

    /**
     * 初始化控件
     */
    private void init() {
        //圆半径
        circleRadius =  300 ;
        //画圆的画笔
        mCirclePaint = new Paint();
        mCirclePaint.setColor(Color.GREEN);
        mCirclePaint.setStrokeWidth(5);//设置绘制的笔画宽度
        mCirclePaint.setStyle(Paint.Style.STROKE); //空心

        //画刻度的画笔
        mPaintLine = new Paint();
        mPaintLine.setColor(Color.BLACK);
        mPaintLine.setStrokeWidth(5);

        //画数字的画笔
        mPaintText = new Paint();
        mPaintText.setColor(Color.GRAY);
        mPaintText.setTextSize(30);
        mPaintText.setStrokeWidth(5);
        mPaintText.setTextAlign(Paint.Align.CENTER); //文字居中

        //发送消息
        handler.sendEmptyMessage(what_update);
        //获取时间
        mCalendar = Calendar.getInstance();
    }

    /*
     * 当一个view 从创建对象，到显示在屏幕中，的几个重要步骤:
     *
     * 1- 测量控件大小
     * 		onMeasure
     * 2- 指定控件位置
     * 		onLayout
     * 3- 绘制控件的内容
     * 		onDraw
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 我们要做的就是设置测量大小，即，view想要的大小
        mWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        mHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制大圆
        canvas.drawCircle(mWidth/2, mHeight/2, circleRadius, mCirclePaint);
        //小圆
        canvas.drawCircle(mWidth / 2, mHeight / 2, 5, mCirclePaint);

        //绘制刻度
        for (int i = 1; i <=12 ; i++) {
            //大刻度
            canvas.save(); //保存当前矩阵并将其存储到私有堆栈上
            float degrees = 360/12*i ;
            canvas.rotate(degrees,mWidth/2, mHeight/2);
            canvas.drawLine(mWidth / 2 ,(mHeight / 2-circleRadius) ,
                    mWidth / 2 ,(mHeight / 2-circleRadius+20) ,mPaintLine);
            canvas.drawText(""+i,mWidth/2,(mHeight / 2-circleRadius+50),mPaintText);
            /**
             * 这个调用是为了平衡save()，用于移除自上次保存时的矩阵/剪辑状态的所有修改。
             * 不能比svae()调用的次数多
             */
            canvas.restore();

            //小刻度
            canvas.save(); //保存当前矩阵并将其存储到私有堆栈上
            float degrees2 = 360/12*(i-1)+15 ;
            canvas.rotate(degrees2,mWidth/2, mHeight/2);
            canvas.drawLine(mWidth / 2 ,(mHeight / 2-circleRadius) ,
                    mWidth / 2 ,(mHeight / 2-circleRadius+10) ,mPaintLine);
//            canvas.drawText(""+i,mWidth/2,(mHeight / 2-circleRadius+50),mPaintText);
            /**
             * 这个调用是为了平衡save()，用于移除自上次保存时的矩阵/剪辑状态的所有修改。
             * 不能比svae()调用的次数多
             */
            canvas.restore();
        }

        //绘制时针、分针和秒针
        int hour = mCalendar.get(Calendar.HOUR);
        int minute = mCalendar.get(Calendar.MINUTE);
        int second = mCalendar.get(Calendar.SECOND);
        //小时的角度
        float hourDegree = 360 / 12f * hour ;
        canvas.save();//这个方法和restore配合用于表盘归位
        mPaintLine.setStrokeWidth(20);
        mPaintLine.setColor(Color.MAGENTA);
        canvas.rotate(hourDegree, mWidth / 2, mHeight / 2);//以表盘中心为中心旋转现在分钟数*（360度/60分）
        canvas.drawLine(mWidth / 2 ,mHeight / 2 , mWidth / 2 ,(mHeight / 2 - circleRadius+200) ,mPaintLine);
        canvas.restore();//表盘归于原位
        //分钟的角度
        float minuteDegree = 360 / 60f * minute ;
        canvas.save();//这个方法和restore配合用于表盘归位
        mPaintLine.setStrokeWidth(15);
        canvas.rotate(minuteDegree, mWidth / 2, mHeight / 2);//以表盘中心为中心旋转现在分钟数*（360度/60分）
        canvas.drawLine(mWidth / 2 ,mHeight / 2 , mWidth / 2 ,(mHeight / 2 - circleRadius+150) ,mPaintLine);
        canvas.restore();//表盘归于原位
        //秒针的角度
        float sedondDegree = 360 /60f * second ;
        canvas.save();//这个方法和restore配合用于表盘归位
        mPaintLine.setStrokeWidth(10);
        canvas.rotate(sedondDegree, mWidth / 2, mHeight / 2);//以表盘中心为中心旋转现在分钟数*（360度/60分）
        canvas.drawLine(mWidth / 2 ,mHeight / 2 , mWidth / 2 ,(mHeight / 2 - circleRadius+100) ,mPaintLine);
        canvas.restore();//表盘归于原位


    }
}
