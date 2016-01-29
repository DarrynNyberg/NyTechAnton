package nybergtechnology.com.webuildapps.Utility;

/**
 * Created by Administrator on 1/28/2016.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by Administrator on 12/25/2015.
 */
public class CircleTransformClass {

    Bitmap source;
    public CircleTransformClass(Bitmap source, int radius){
        this.source = BitmapUtil.createScaledBitmap(source, radius, radius, BitmapUtil.ScalingLogic.CROP);
//        source.recycle();
    }

    public Bitmap transform() {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);


        float r = (size) / 2f;
        canvas.drawCircle(r, r, r, paint);

        squaredBitmap.recycle();

        return bitmap;
    }


    public Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public Bitmap transformWithArcBorder(String strColor, int degree){

        int strokeWidth = 30;

        Bitmap output = getCircleBitmap(source);

        Bitmap bitmap = Bitmap.createBitmap(output.getWidth()+strokeWidth*2, output.getHeight()+strokeWidth*2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawBitmap(output, strokeWidth, strokeWidth, null);

        Paint paintBorder = new Paint();
        paintBorder.setColor(Color.parseColor(strColor));
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setAntiAlias(true);
        paintBorder.setStrokeWidth(strokeWidth);

        RectF oval = new RectF(strokeWidth/2, strokeWidth/2, canvas.getWidth()-strokeWidth/2, canvas.getHeight()-strokeWidth/2);
        canvas.drawArc(oval, 270, degree, false, paintBorder);

        return bitmap;
    }


    public Bitmap transformWithCircleBorder() {
        int strokeWidth = 4;

        Bitmap circleBitmap = getCircleBitmap(source);

        Bitmap bitmap = Bitmap.createBitmap(circleBitmap.getWidth()+strokeWidth*2, circleBitmap.getHeight()+strokeWidth*2, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(circleBitmap, strokeWidth, strokeWidth, null);


        Paint paintBorder = new Paint();
        paintBorder.setColor(Color.parseColor(Constant.CIRCLE_OUTLINE_COLOR));
        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setAntiAlias(true);
        paintBorder.setStrokeWidth(strokeWidth);

        canvas.drawCircle(canvas.getWidth()/2, canvas.getHeight()/2, circleBitmap.getHeight()/2, paintBorder);


        return bitmap;
    }

}
