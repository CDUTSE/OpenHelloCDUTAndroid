package com.emptypointer.hellocdut.widget;

/**
 * Created by Sequarius on 2015/11/25.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;
public class CircleImageView  extends ImageView{
    private final int DEFAULT_WIDTH = 60;

    private Bitmap mBitmap;
    /** 图片转换后的drawable **/
    private ShapeDrawable circleDrawable;
    /** 边框转换后的drawable **/
    private ShapeDrawable borderDrawable;

    /** 图像的四角圆弧半径，当设置为-1时或者大于图片显示大小一半时，则会显示为圆形 **/
    private float circleRadiu=10;
    /** 边框颜色 **/
    private int borderColor = Color.BLUE;
    /** 边框大小 **/
    private int borderWidth;

    private boolean isCircle;

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (getDrawable() == null || getDrawable().getIntrinsicWidth() == 0
                || getDrawable().getIntrinsicHeight() == 0) {
            return;
        }
        if (borderDrawable != null) {
            borderDrawable.draw(canvas);
        }
        if (circleDrawable != null) {
            canvas.save();
            canvas.translate(borderWidth, borderWidth);
            circleDrawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        initDrawable();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        initDrawable();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        initDrawable();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        mBitmap = getBitmapFromDrawable(getDrawable());
        initDrawable();
    }

    /***
     * 生成边框和圆角图片drawable
     */
    private void initDrawable() {
        if (mBitmap == null || mBitmap.getWidth() == 0
                || mBitmap.getHeight() == 0) {
            return;
        }
        // 当角半径设置为-1时，并且控件要求显示的大小为正方形，则图片和边框显示后的效果为圆形
        if (isCircle) {
            if (getWidth() != 0 && getHeight() != 0
                    && getWidth() == getHeight()) {
                circleRadiu = getWidth();
            } else {
                circleRadiu = 0;
            }
        }

        initBorderDrawable();
        initCircleDrawable();

        invalidate();

    }

    /***
     * 生成边框Drawalbe
     */
    private void initBorderDrawable() {
        // 当边框大小不为0时，进行边框的生成转换
        if (borderWidth != 0) {
            // 如果角半径不为0，则边框的角半径应该加上边框的大小
            float borderRadiu = circleRadiu == 0 ? 0 : circleRadiu
                    + borderWidth;
            // 设置四角半径集合
            float[] borderRadius = new float[] { borderRadiu, borderRadiu,
                    borderRadiu, borderRadiu, borderRadiu, borderRadiu,
                    borderRadiu, borderRadiu };
            borderDrawable = new ShapeDrawable(new RoundRectShape(borderRadius,
                    null, null));
            borderDrawable.getPaint().setColor(borderColor);
            borderDrawable.getPaint().setAntiAlias(true);
            // 设置边框显示大小
            borderDrawable.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    /***
     * 生成圆角图片drawable
     */
    private void initCircleDrawable() {
        float[] circleRadius = new float[] { circleRadiu, circleRadiu,
                circleRadiu, circleRadiu, circleRadiu, circleRadiu,
                circleRadiu, circleRadiu };
        // 图片显示的大小范围应该是图片进行放大或缩小后的控件要求的大小
        int boundWidth = (int) ((float) getWidth() - borderWidth * 2);
        int boundHeight = (int) ((float) getHeight() - borderWidth * 2);

        circleDrawable = new ShapeDrawable(new RoundRectShape(circleRadius,
                null, null));
        BitmapShader bitmapShader = new BitmapShader(mBitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        circleDrawable.getPaint().setShader(bitmapShader);
        circleDrawable.setBounds(0, 0, boundWidth, boundHeight);

        // 将图片进行放大或缩小适应控件大小
        if (getWidth() != 0 && getHeight() != 0) {
            Matrix matrix = new Matrix();
            matrix.setScale(((float) boundWidth) / mBitmap.getWidth(),
                    ((float) boundHeight) / mBitmap.getHeight());
            bitmapShader.setLocalMatrix(matrix);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        initDrawable();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof TransitionDrawable) {
            Drawable tdrawable = ((TransitionDrawable) drawable).getDrawable(1);
            if (tdrawable != null) {
                drawable = tdrawable;
            }
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }


        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(DEFAULT_WIDTH, DEFAULT_WIDTH,
                        Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /***
     * 设置角半径
     *
     * @param circleRadiu
     *            当角半径设置为-1时，并且控件大小为正方形，则图片和边框显示后的效果为圆形
     *            如果半径小于正方形大小的一半，则会显示圆角矩形，超过一半，则为圆形
     */
    public void setCircleRadiu(float circleRadiu) {
        if (circleRadiu == -1) {
            isCircle = true;
            circleRadiu = 0;
        } else {
            isCircle = false;
        }
        this.circleRadiu = circleRadiu;
        initDrawable();
    }

    /***
     * 设置边框颜色
     *
     * @param borderColor
     */
    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        initDrawable();
    }

    /***
     * 设置边框大小
     *
     * @param borderWidth
     */
    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        initDrawable();
    }

    public void setCircle(boolean isCircle) {
        this.isCircle = isCircle;
    }

    @Override
    protected void onDetachedFromWindow() {
        // TODO Auto-generated method stub
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        super.onDetachedFromWindow();
    }
}
