package com.example.circlepackingview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Andrew Matuk on 16.1.2016
 * https://github.com/veonua/
 *
 * values from http://www.packomania.com/
 */

public class CirclePackingView extends View {
    private static final float SQRT_2 = (float) Math.sqrt(2);
    private static final float SQRT_3 = (float) Math.sqrt(3);
    private static final float SILVER_RATIO = 1+ SQRT_2;

    private static final float[] COORDINATES_1 = { 0, 0};
    private static final float[] COORDINATES_2 = {
            1-(1/ SILVER_RATIO),   0.0f,
            (SQRT_2 / SILVER_RATIO)-1,  0.0f
    };
    private static final float  _x3 = (SQRT_3 /(SQRT_2 + SQRT_3))-1;
    private static final float[] COORDINATES_3 = {
            _x3+0.5826183392f,  -0.6438813988f,
            1-(SQRT_2 /(SQRT_2 + SQRT_3)),   0.0f,
            _x3,  0.0f
    };

    // FIXME:wrong, copy-paste from COORDINATES_5
    private static final float[] COORDINATES_4 = {
            -0.075176296375555391000475331715f,   0.683099132137561605481093127186f,
            0.243810256576120044094861308920f,  -0.566707310534312245838260639042f,
            0.515511837678785839970087098763f,   0.212696996113075358075809441850f,
            -0.505456655669173835012569299809f,  0.0f
    };

    // from http://hydra.nat.uni-magdeburg.de/packing/ccir/txt/ccir5.txt
    private static final float[] COORDINATES_5 = {
            -0.346050887765097367199182146056f,  -0.697732321641936395624436036149f,
            -0.075176296375555391000475331715f,   0.683099132137561605481093127186f,
            0.243810256576120044094861308920f,  -0.566707310534312245838260639042f,
            0.515511837678785839970087098763f,   0.212696996113075358075809441850f,
            -0.505456655669173835012569299809f,  0.0f
    };
    private static final float[] COORDINATES_6 = {
            -0.497577255201574723523508961940f,  -0.643099798998648376619207540379f,
            -0.289466940261350579273699921662f,   0.676369734297064902141806388418f,
            0.636976515089411695669022779308f,  -0.227281197410151039737492179523f,
            0.062712210768053967973282624315f,  -0.623087539293366656304835415852f,
            0.357329075993728355546004517888f,   0.459541053988584717975143105102f,
            -0.542233847024456274103142128928f,  0.0f};

    private static final float[] COORDINATES_7 = {
            -0.572112869381301845672033091291f,  -0.606508360234788522221190885544f,
            0.110132637794595350222496641458f,   0.044394544185000824751502578235f,
            0.569660244738966376833272829444f,   0.429916336530146025507949865440f,
            0.644024318103801963705690765838f,  -0.182516670216501978401225128975f,
            -0.055703605402631200671590563950f,   0.627898000740234652250676753341f,
            0.034424872583556727700716427559f,  -0.594087513084205088750880314226f,
            -0.562640804014322208333600069293f,  0.0f};

    private static final float[] COORDINATES_8 = {
            -0.632330088683687509475888399476f,   0.569438488225021752439345923943f,
            -0.491950099106480111616344015774f,  -0.619710991595565915757703680137f,
            0.098209798467445592358461382984f,   0.048060085407890274013367543177f,
            0.519301451154555472172988062979f,   0.476465986217641244717485983045f,
            0.656455350841909223072312583065f,  -0.133624887442257910351091496029f,
            -0.120031758872090803657345790037f,   0.627027090692306857883195966751f,
            0.107046241969696613354119606340f,  -0.599966119286034114505391842394f,
            -0.582474787306559488603066379634f,  0.0f};

    private static final float[][] COORDINATES = {
            COORDINATES_1, COORDINATES_2, COORDINATES_3, COORDINATES_4, COORDINATES_5,
            COORDINATES_6, COORDINATES_7, COORDINATES_8};

    private static final float[] FILL_RATIO = {
            1,
            SILVER_RATIO,
            SQRT_2 + SQRT_3, 3.98f,
            4.5214802769723774026005212657f, 5.3509629902978928103576465111f, 6.0493784864906122826189130251f,
            6.7742666520666042223460221198f, 7.5590023770659136880140200905f, 8.3034681221114890787043811875f,
            9.0721258793824480148524528436f, 9.8653203041682594493162739373f, 10.5883262877291493139726188169f,
            11.3649775907780887853061779532f, 12.0755266028033386256838323924f, 12.8193115223202231613483007121f,
            13.5821239415627905430148345968f, 14.3249698909061328241475720219f, 15.0353524324138913429158350387f
    };

    static final private int[] colors = {
            0xFFF44336, 0xFF673AB7, 0xFF2196F3, 0xFF00BCD4, 0xFF4CAF50,
            0xFFCDDC39, 0xFFFFC107, 0xFFFF5722};
    private float circle_r;
    private int circle_num = 0;
    private final Paint paint = new Paint();
    private final Paint out_paint = new Paint();
    private final Matrix matrix = new Matrix();

    private Drawable[] drawables;

    @Override
    public void setRotation(float rotation) {
        if (this.rotation == rotation) return;

        this.rotation = rotation;
        matrix.setRotate(rotation);
        updateCoordinates();
        invalidate();
    }

    private void updateCoordinates() {
        if (circle_num<1) return;

        float[] c = COORDINATES[circle_num - 1];
        coordinates = new float[c.length];
        matrix.mapPoints(coordinates, c);
    }

    private float rotation;

    @SuppressWarnings("unused")
    public void setFill(float value) {
        this.fill_r = value;
        smallestRadius = circle_r / FILL_RATIO[circle_num - 1] * fill_r;
        invalidate();
    }

    private float fill_r;

    private float[] coordinates;
    private double smallestRadius;

    private void init(AttributeSet attrs) {
        out_paint.setAntiAlias(true);
        paint.setAntiAlias(true);
        out_paint.setStrokeWidth(3);
        out_paint.setStyle(Paint.Style.STROKE);
        out_paint.setColor(Color.BLACK);

        drawables=new Drawable[9];
        drawables[0] = getResources().getDrawable(R.drawable.ic_school_white_48dp);
        drawables[1] = getResources().getDrawable(R.drawable.ic_directions_bike_white_48dp);
        drawables[2] = getResources().getDrawable(R.drawable.ic_whatshot_white_48dp);
        drawables[3] = getResources().getDrawable(R.drawable.ic_cake_white_48dp);
        drawables[4] = getResources().getDrawable(R.drawable.ic_public_white_48dp);
        drawables[5] = getResources().getDrawable(R.drawable.ic_cake_white_48dp);
        drawables[6] = getResources().getDrawable(R.drawable.ic_cake_white_48dp);
        drawables[7] = getResources().getDrawable(R.drawable.ic_cake_white_48dp);
        drawables[8] = getResources().getDrawable(R.drawable.ic_cake_white_48dp);

        if (attrs == null) {
            setCircleNumber(1);
        }

        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.CirclePackingView);
        fill_r = a.getFloat(R.styleable.CirclePackingView_fill_rate, 1.0f);
        setRotation(a.getInt(R.styleable.CirclePackingView_angle, 0));
        setCircleNumber(a.getInt(R.styleable.CirclePackingView_number, isInEditMode() ? 5 : 0));
        a.recycle();
    }


    public CirclePackingView(Context context) {
        super(context);
        init(null);
    }

    public CirclePackingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CirclePackingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CirclePackingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        final int widthPadding = getPaddingLeft() + getPaddingRight();
        final int heightPadding = getPaddingTop() + getPaddingBottom();

        widthSize -= widthPadding;
        heightSize -= heightPadding;

        int circle_d = Math.min(widthSize, heightSize);
        circle_r = circle_d/2.0f;
        if (circle_num>0)
            smallestRadius = circle_r / FILL_RATIO[circle_num - 1] * fill_r;

        setMeasuredDimension(circle_d + widthPadding, circle_d + heightPadding);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float dx = getPaddingLeft() + circle_r;
        float dy = getPaddingTop() + circle_r;

        canvas.drawCircle(dx,dy, circle_r, out_paint);

        for (int i=0; i<circle_num; i++) {
            float cx = dx + coordinates[i * 2] * circle_r;
            float cy = dy + coordinates[i * 2+1] * circle_r;

            int ii = circle_num - i - 1;

            int i1 = i + 1;
            float rz = (float) (smallestRadius * Math.sqrt(i1));
            paint.setColor(colors[ii]);
            canvas.drawCircle(cx, cy, rz, paint);

            if (drawables==null) continue;
            rz*=0.8;
            drawables[ii].setBounds((int) (cx - rz), (int)(cy - rz),(int) (cx + rz), (int)(cy + rz));
            drawables[ii].draw(canvas);
        }
    }

    public void setCircleNumber(int circleNum) {
        if (this.circle_num == circleNum) return;
        this.circle_num = circleNum;
        if (circleNum>0) {
            smallestRadius = circle_r / FILL_RATIO[circle_num - 1] * fill_r;
            updateCoordinates();
        }
        invalidate();
    }
}

