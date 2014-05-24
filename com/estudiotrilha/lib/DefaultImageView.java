package com.estudiotrilha.lib;

import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.estudiotrilha.inevent.R;

public class DefaultImageView extends ImageView
{
	private String SCHEMA = "http://schemas.android.com/apk/lib/com.estudiotrilha.lib";
	private int borderWidth;
	private int canvasSize;
	private Bitmap image;
	private Paint paint;
	private Paint paintBorder;
	private String defaultText = "";
	
	private Context context;

    private Random gerador = new Random();

	public DefaultImageView(final Context context) {
		this(context, null);
	}

	public DefaultImageView(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.pictureStyle);
	}

	public DefaultImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		this.context = context;
		
		// init paint
		paint = new Paint();
		paint.setAntiAlias(true);

		paintBorder = new Paint();
		paintBorder.setAntiAlias(true);

		// load the styled attributes and set their properties
		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Picture, defStyle, 0);

		if(attributes.getBoolean(R.styleable.Picture_border, true)) {
		//	setBorderWidth(attributes.getColor(R.styleable.Picture_border_width, 4));
		//	setBorderColor(attributes.getInt(R.styleable.Picture_border_color, ));
		}

		//if(attributes.getBoolean(R.styleable.Picture_shadow, false))
			addShadow();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NewTextView);

        try {
        	defaultText = attrs.getAttributeValue(SCHEMA, "defaultText");
        	
            invalidate();
            requestLayout();
	   	} finally {
	   		a.recycle();
   		}
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
		this.requestLayout();
		this.invalidate();
	}

	public void setBorderColor(int borderColor) {
		if (paintBorder != null)
			paintBorder.setColor(borderColor);
		this.invalidate();
	}

	public void addShadow() {
		setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
		paintBorder.setShadowLayer(3.0f, 0.0f, 2.0f, 0x55000000);
	}

	@Override
	public void onDraw(Canvas canvas) {
		// load the bitmap
		BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getDrawable();
		if (bitmapDrawable != null)
			image = bitmapDrawable.getBitmap();
		else
			image = drawTextToBitmap(context, defaultText, true);

		// init shader
		if (image != null) {

			canvasSize = canvas.getWidth();
			if(canvas.getHeight()<canvasSize)
				canvasSize = canvas.getHeight();

			BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(image, canvasSize, canvasSize, false), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			paint.setShader(shader);

			// circleCenter is the x or y of the view's center
			// radius is the radius in pixels of the cirle to be drawn
			// paint contains the shader that will texture the shape
			int circleCenter = (canvasSize - (borderWidth * 2)) / 2;
			canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, ((canvasSize - (borderWidth * 2)) / 2) + borderWidth - 4.0f, paintBorder);
			canvas.drawCircle(circleCenter + borderWidth, circleCenter + borderWidth, ((canvasSize - (borderWidth * 2)) / 2) - 4.0f, paint);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// The parent has determined an exact size for the child.
			result = specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			// The child can be as large as it wants up to the specified size.
			result = specSize;
		} else {
			// The parent has not imposed any constraint on the child.
			result = canvasSize;
		}

		return result;
	}

	private int measureHeight(int measureSpecHeight) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpecHeight);
		int specSize = MeasureSpec.getSize(measureSpecHeight);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			// The child can be as large as it wants up to the specified size.
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = canvasSize;
		}

		return (result + 2);
	}

    public Bitmap drawTextToBitmap(Context gContext, String gText, boolean highDef)
    {
		  Resources resources = gContext.getResources();
		  float scale = resources.getDisplayMetrics().density;
		  
		  Bitmap bitmap;
		  
		  if(highDef)
			  bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);  //creates bmp
		  else
			  bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);  //creates bmp
		 
		  Canvas canvas = new Canvas(bitmap);
		  // new antialised Paint
		  Paint bg = new Paint();
		  bg.setColor(0x222222);
		  bg.setStyle(Paint.Style.FILL);
		  
		  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		  // text color - #3D3D3D
		  paint.setColor(Color.rgb(255, 255, 255));
		  // text size in pixels
		  
		  if(highDef)
			  paint.setTextSize((int) (80 * scale));
		  else
			  paint.setTextSize((int) (45 * scale));
		  
		  //paint.setTypeface(Typeface.createFromAsset(gContext.getAssets(), "fonts/roboto_regular.ttf"));
		  // text shadow
		  paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
		 
		  // draw text to the Canvas center
		  Rect bounds = new Rect();
		  paint.getTextBounds(gText, 0, gText.length(), bounds);
		  int x = (bitmap.getWidth() - bounds.width())/2;
		  int y = (bitmap.getHeight() + bounds.height())/2;
		 
		  int color = gerador.nextInt(10);
		  
		  int R = 0, G = 0, B = 0;
		  
		  switch(color)
		  {
		  	case 0:
			  	R = 39;
			  	G = 174;
			  	B = 96;
			  	break;
		  	case 1:
				R = 39;
				G = 174;
				B = 96;
				break;
		  	case 2:
				R = 41;
				G = 128;
				B = 185;
			break;
			case 3:
				R = 211;
				G = 84;
				B = 0;
			break;
			case 4:
				R = 192;
				G = 57;
				B = 43;
			break;
			case 5:
				R = 142;
				G = 68;
				B = 173;
			break;
			case 6:
				R = 44;
				G = 62;
				B = 80;
			break;
			case 7:
				R = 243;
				G = 156;
				B = 18;
			break;
			case 8:
				R = 127;
				G = 140;
				B = 141;
			break;
			case 9:
				R = 22;
				G = 160;
				B = 133;
			break;
		  }
		  
		  canvas.drawARGB(0xFF, R, G, B);
		  canvas.drawText(gText, x, y, paint);
		 
		  return bitmap;
	}
}