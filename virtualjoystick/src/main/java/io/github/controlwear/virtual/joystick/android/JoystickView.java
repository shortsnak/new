package io.github.controlwear.virtual.joystick.android;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {


    /*
    INTERFACES
    */

    /**
     * Interface definition for a callback to be invoked when a
     * JoystickView's button is moved
     */
    public interface OnMoveListener {

        /**
         * Called when a JoystickView's button has been moved
         * @param angle current angle
         * @param strength current strength
         */
        void onMove(int angle, int strength, MotionEvent event);
    }


    /*
    CONSTANTS
    */
    public static final int AXIS_VERTICAL = 1;
    /** Default value for both directions (horizontal and vertical movement) */
    public static final int AXIS_BOTH = 0;
    public static final int AXIS_HORIZONTAL = -1;
    /**
     * Used to allow a slight move without cancelling MultipleLongPress
     */
    private static final int DEFAULT_DEADZONE = 10;

    /** Default color for button */
    private static final int DEFAULT_COLOR_BUTTON = Color.BLACK;

    /** Default color for border */
    private static final int DEFAULT_COLOR_BORDER = Color.TRANSPARENT;

    /** Default alpha for border */
    private static final int DEFAULT_ALPHA_BORDER = 255;

    /** Default background color */
    private static final int DEFAULT_BACKGROUND_COLOR = Color.TRANSPARENT;

    /** Default View's size */
    private static final int DEFAULT_SIZE = 200;

    /** Default border's width */
    private static final int DEFAULT_WIDTH_BORDER = 3;

    /** Default behavior to fixed center (not auto-defined) */
    private static final boolean DEFAULT_FIXED_CENTER = true;
    
    /** Default behavior to auto re-center button (automatically recenter the button) */
    private static final boolean DEFAULT_AUTO_RECENTER_BUTTON = true;
    
    /** Default behavior to button stickToBorder (button stay on the border) */
    private static final boolean DEFAULT_BUTTON_STICK_TO_BORDER = false;

    // DRAWING
    private final Paint mPaintCircleButton;
    private final Paint mPaintBorder;
    private final Paint mPaintBackground;

    private Drawable mButtonDrawable;

    /** Ratio use to define the size of the button */
    private float mButtonSizeRatio;

    /** Ratio use to define the size of the background */
    private float mBackgroundSizeRatio;

    // COORDINATE
    private int mPosX = 0;
    private int mPosY = 0;
    private int mCenterX = 0;
    private int mCenterY = 0;

    private int mFixedCenterX = 0;
    private int mFixedCenterY = 0;

    /** Used to adapt behavior whether it is auto-defined center (false) or fixed center (true) */
    private boolean mFixedCenter;

    /**
     * Used to adapt behavior whether the button is automatically re-centered (true)
     * when released or not (false)
     */
    private boolean mAutoReCenterButton;

    /**
     * Used to adapt behavior whether the button is stick to border (true) or
     * could be anywhere (when false - similar to regular behavior)
     */
    private boolean mButtonStickToBorder;


    /**
     * Used to enabled/disabled the Joystick. When disabled (enabled to false) the joystick button
     * can't move and onMove is not called.
     */
    private boolean mEnabled;

    // SIZE
    private int mButtonRadius;
    private float mBorderRadius;
    private int mBorderWidth, mBorderHeight;


    /** Alpha of the border (to use when changing color dynamically) */
    private int mBorderAlpha;

    /** Based on mBorderRadius but a bit smaller (minus half the stroke size of the border) */
    private float mBackgroundRadius;

    /**Based on mBorderWidth/Height but a bit smaller (minus half the stroke size of the border) */
    private float mBackgroundWidth, mBackgroundHeight;
    
    private boolean isRectangle;

    /** Listener used to dispatch OnMove event */
    private OnMoveListener mCallback;

    /** PointerID used to track the original pointer triggering the joystick */
    private int pointerID = -1;

    /** The deadzone for the joystick from 0 to 100%*/
    private int mDeadzone;


    /**
     * The allowed direction of the button is define by the value of this parameter:
     * - a negative value for horizontal axe
     * - a positive value for vertical axe
     * - zero for both axes
     */
    private int mAxisMotionType;

    /*
    * Detect if joystick has been pressed, even if strength and angle are 0
     */
    private boolean isPressed = false;
    
    /** axis to be centered */
    private int mAxisToCenter;


    /*
    CONSTRUCTORS
     */

    /**
     * Simple constructor to use when creating a JoystickView from code.
     * Call another constructor passing null to Attribute.
     * @param context The Context the JoystickView is running in, through which it can
     *        access the current theme, resources, etc.
     */
    public JoystickView(Context context) {
        this(context, null);
    }


    public JoystickView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Constructor that is called when inflating a JoystickView from XML. This is called
     * when a JoystickView is being constructed from an XML file, supplying attributes
     * that were specified in the XML file.
     * @param context The Context the JoystickView is running in, through which it can
     *        access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the JoystickView.
     */
    public JoystickView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);

        TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.JoystickView,
                defStyleAttr, defStyleRes
        );

        int buttonColor;
        int borderColor;
        int backgroundColor;
        int borderWidth;
        float _deadzone;
        try {
            buttonColor = styledAttributes.getColor(R.styleable.JoystickView_JV_buttonColor, DEFAULT_COLOR_BUTTON);
            borderColor = styledAttributes.getColor(R.styleable.JoystickView_JV_borderColor, DEFAULT_COLOR_BORDER);
            mBorderAlpha = styledAttributes.getInt(R.styleable.JoystickView_JV_borderAlpha, DEFAULT_ALPHA_BORDER);
            backgroundColor = styledAttributes.getColor(R.styleable.JoystickView_JV_backgroundColor, DEFAULT_BACKGROUND_COLOR);
            borderWidth = styledAttributes.getDimensionPixelSize(R.styleable.JoystickView_JV_borderWidth, DEFAULT_WIDTH_BORDER);
            mFixedCenter = styledAttributes.getBoolean(R.styleable.JoystickView_JV_fixedCenter, DEFAULT_FIXED_CENTER);
            mAutoReCenterButton = styledAttributes.getBoolean(R.styleable.JoystickView_JV_autoReCenterButton, DEFAULT_AUTO_RECENTER_BUTTON);
            mButtonStickToBorder = styledAttributes.getBoolean(R.styleable.JoystickView_JV_buttonStickToBorder, DEFAULT_BUTTON_STICK_TO_BORDER);
            mButtonDrawable = styledAttributes.getDrawable(R.styleable.JoystickView_JV_buttonImage);
            _deadzone = styledAttributes.getFraction(R.styleable.JoystickView_JV_deadzone, 100, 100, DEFAULT_DEADZONE);
            mEnabled = styledAttributes.getBoolean(R.styleable.JoystickView_JV_enabled, true);
            mButtonSizeRatio = styledAttributes.getFraction(R.styleable.JoystickView_JV_buttonSizeRatio, 1, 1, 0.25f);
            mBackgroundSizeRatio = styledAttributes.getFraction(R.styleable.JoystickView_JV_backgroundSizeRatio, 1, 1, 0.75f);
            mAxisMotionType = styledAttributes.getInteger(R.styleable.JoystickView_JV_axisMotion, AXIS_BOTH);
            mAxisToCenter = styledAttributes.getInteger(R.styleable.JoystickView_JV_axisToCenter, AXIS_BOTH);
            isRectangle = styledAttributes.getBoolean(R.styleable.JoystickView_JV_useRectangle, false);
            
        } finally {
            styledAttributes.recycle();
        }

        // Initialize the drawing according to attributes
        
        mDeadzone = Math.round(_deadzone);
        
        mPaintCircleButton = new Paint();
        mPaintCircleButton.setAntiAlias(true);
        mPaintCircleButton.setColor(buttonColor);
        mPaintCircleButton.setStyle(Paint.Style.FILL);

        mPaintBorder = new Paint();
        mPaintBorder.setAntiAlias(true);
        mPaintBorder.setColor(borderColor);
        mPaintBorder.setStyle(Paint.Style.STROKE);
        mPaintBorder.setStrokeWidth(borderWidth);

        if (borderColor != Color.TRANSPARENT) {
            mPaintBorder.setAlpha(mBorderAlpha);
        }

        mPaintBackground = new Paint();
        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setColor(backgroundColor);
        mPaintBackground.setStyle(Paint.Style.FILL);
    }

    private void initPosition() {
        // get the center of view to position circle
        mFixedCenterX = mCenterX = mPosX = getWidth() / 2;
        mFixedCenterY = mCenterY = mPosY = getHeight() / 2;
    }

    public boolean isVisible() {
        return getVisibility() == VISIBLE;
    }
    
    /**
     * Draw the background, the border and the button
     * @param canvas the canvas on which the shapes will be drawn
     */
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (isRectangle) {
            // Draw the rectangle background
            canvas.drawRect(
                    mFixedCenterX - mBackgroundWidth / 2f,
                    mFixedCenterY - mBackgroundHeight / 2f,
                    mFixedCenterX + mBackgroundWidth / 2f,
                    mFixedCenterY + mBackgroundHeight / 2f,
                    mPaintBackground);

            // Draw the rectangle border
            canvas.drawRect(
                    mFixedCenterX - mBorderWidth / 2f,
                    mFixedCenterY - mBorderHeight / 2f,
                    mFixedCenterX + mBorderWidth / 2f,
                    mFixedCenterY + mBorderHeight / 2f,
                    mPaintBorder);
        }else {
            // Draw the background
            canvas.drawCircle(mFixedCenterX, mFixedCenterY, mBackgroundRadius, mPaintBackground);

            // Draw the circle border
            canvas.drawCircle(mFixedCenterX, mFixedCenterY, mBorderRadius, mPaintBorder);
        }
        /*
        canvas.drawLine(mCenterX, mCenterY, mCenterX+mBorderWidth/2f, mCenterY+mBorderHeight /2f, mPaintBorder);//raio
        canvas.drawLine(mFixedCenterX, mFixedCenterY, mFixedCenterX+mBorderWidth/2f, mFixedCenterY, mPaintBorder);
        canvas.drawLine(mCenterX, mCenterY, mPosX,mPosY,mPaintBorder);
       */
        // Draw the button from image
        if (mButtonDrawable != null) {
            int x = mPosX + mFixedCenterX - mCenterX - mButtonRadius;
            int y = mPosY + mFixedCenterY - mCenterY - mButtonRadius;
            mButtonDrawable.setBounds(x, y, x + mButtonRadius * 2, y + mButtonRadius * 2);
            mButtonDrawable.draw(canvas);
        }
        // Draw the button as simple circle
        else {
            canvas.drawCircle(
                    mPosX + mFixedCenterX - mCenterX,
                    mPosY + mFixedCenterY - mCenterY,
                    mButtonRadius,
                    mPaintCircleButton
            );
        }
    }
    
    /**
     * This is called during layout when the size of this view has changed.
     * Here we get the center of the view and the radius to draw all the shapes.
     *
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldW Old width of this view.
     * @param oldH Old height of this view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        initPosition();

        // radius based on smallest size : height OR width
        int d = Math.min(w, h);
        mButtonRadius = (int) (d / 2 * mButtonSizeRatio);
        mBorderRadius = (d / 2f * mBackgroundSizeRatio);
        mBackgroundRadius = mBorderRadius - (mPaintBorder.getStrokeWidth() / 2);

        mBorderWidth = (int) (mAxisMotionType > AXIS_BOTH ? d * mButtonSizeRatio : w * mBackgroundSizeRatio);// if vertical
        mBorderHeight = (int) (mAxisMotionType < AXIS_BOTH ? d * mButtonSizeRatio : h * mBackgroundSizeRatio);// if horizontal
        mBackgroundWidth = mBorderWidth - mPaintBorder.getStrokeWidth() / 2;
        mBackgroundHeight = mBorderHeight - mPaintBorder.getStrokeWidth() / 2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // setting the measured values to resize the view to a certain width and height
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }


    private int measure(int measureSpec) {
        if (MeasureSpec.getMode(measureSpec) == MeasureSpec.UNSPECIFIED) {
            // if no bounds are specified return a default size (200)
            return DEFAULT_SIZE;
        } else {
            // As you want to fill the available space
            // always return the full available bounds.
            return MeasureSpec.getSize(measureSpec);
        }
    }


    /*
    USER EVENT
     */

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * Handle touch screen motion event. Move the button according to the
     * finger coordinate and detect longPress by multiple pointers only.
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        performClick();
        // if disabled we don't move the joystick
        if (!mEnabled) return false;

        // to move the button according to the finger coordinate
        // (or limited to one axe according to direction option
        int pointerIndex;
        if ((pointerIndex = event.findPointerIndex(pointerID)) != -1) {
            mPosY = mAxisMotionType < AXIS_BOTH ? mCenterY : (int) event.getY(pointerIndex); // if horizontal
            mPosX = mAxisMotionType > AXIS_BOTH ? mCenterX : (int) event.getX(pointerIndex); // if vertical
        }

        if (event.getAction() == MotionEvent.ACTION_POINTER_UP) {
            if (event.findPointerIndex(pointerID) == event.getActionIndex())
                event.setAction(MotionEvent.ACTION_UP);
        }

        if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
            if (pointerID == -1) event.setAction(MotionEvent.ACTION_DOWN);
        }

        // up
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            isPressed = false;
            // Reset the pointerID;
            pointerID = -1;

            // re-center the button or not (depending on settings)
            if (mAutoReCenterButton) {
                resetButtonPosition();
            }

            // if mAutoReCenterButton is false we will send the last strength and angle a bit
            // later only after processing new position HORIZONTAL and VERTICAL otherwise it could be above the border limit
        }

        // down
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // Check if the pointer is inside the original joystick zone
            if (isRectangle) {
                if (Math.abs(mFixedCenterX - event.getX()) > mBackgroundWidth / 2f + mPaintBorder.getStrokeWidth())
                    return false; // outside of the round joystick
                if (Math.abs(mFixedCenterY - event.getY()) > mBackgroundHeight / 2f + mPaintBorder.getStrokeWidth())
                    return false; // outside of the round joystick
            } else if (Math.hypot(Math.abs(mFixedCenterX - event.getX()), Math.abs(mFixedCenterY - event.getY())) > mBackgroundRadius + mPaintBorder.getStrokeWidth())
                return false; // outside of the round joystick

            isPressed = true;
            // Map the pointerID
            pointerID = event.getPointerId(event.getActionIndex());

            mPosY = mAxisMotionType < AXIS_BOTH ? mCenterY : (int) event.getY(); // if horizontal
            mPosX = mAxisMotionType > AXIS_BOTH ? mCenterX : (int) event.getX(); // if vertical
        }

        // handle first touch and long press with multiple touch only
        // when the first touch occurs we update the center (if set to auto-defined center)
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (!mFixedCenter) {
                mCenterX = mPosX;
                mCenterY = mPosY;
            }
        }

        double _maxRadius = mBorderRadius;
        float xPos = mPosX - mCenterX;
        float yPos = mPosY - mCenterY;
        
        if (isRectangle) {
            float Xb = mBorderWidth / 2f;
            float Yb = mBorderHeight / 2f;
            
            float _X = Math.abs(xPos);
            float _Y = Math.abs(yPos);
            
            //regra de três simples
            float Yc = (_Y * Xb) / _X;
            float Xc = (_X * Yb) / _Y;

            _maxRadius = Math.hypot(Math.min(Xb, Xc), Math.min(Yb, Yc));
        }
        
        double _radius = Math.hypot(xPos, yPos);

        // (_radius > mBorderRadius) means button is too far therefore we limit to border
        // (buttonStickBorder && _radius != 0) means wherever is the button we stick it to the border except when _radius == 0
        if (_radius > _maxRadius || (mButtonStickToBorder && _radius != 0)) {
            mPosX = (int) (xPos * _maxRadius / _radius + mCenterX);
            mPosY = (int) (yPos * _maxRadius / _radius + mCenterY);
        }

        // Events are instantaneous now
        notifyOnMove(event);

        // to force a new draw
        invalidate();

        return true;
    }

    /**
     * Check if a callback exists
     */
    private void notifyOnMove(MotionEvent event) {
        if (mCallback == null) return;

        mCallback.onMove(getAngle(), getStrength(), event);
    }

    /**
     * Reset the button position to the center.
     */
    public void resetButtonPosition() {
        mPosY = mAxisToCenter < AXIS_BOTH ? mPosY : mCenterY; // Axis positive, center vertical axe
        mPosX = mAxisToCenter > AXIS_BOTH ? mPosX : mCenterX; // Axis negative, center horizontal axe
    }
    
    /*
    GETTERS
     */

    /**
     * Process the angle following the 360° counter-clock protractor rules.
     * @return the angle of the button
     */
    public int getAngle() {
        int angle = (int) Math.round(Math.toDegrees(Math.atan2(mCenterY - mPosY, mPosX - mCenterX)));
        return angle < 0 ? angle + 360 : angle; // make it as a regular counter-clock protractor
    }

    /**
     * Process the strength as a percentage of the distance between the center and the border.
     * @return the strength of the button
     */
    public int getStrength() {
        int strength;

        if (isRectangle) {
            if (false) {

                float Xb = mBorderWidth / 2f;

                float _X = Math.abs(mPosX - mCenterX);

                float Yb = mBorderHeight / 2f;

                float _Y = Math.abs(mPosY - mCenterY);

                float Yc = (_Y * Xb) / _X;
                float Xc = (_X * Yb) / _Y;

                double _radius = Math.hypot(_X, _Y);
                double _maxRadius = Math.hypot(Math.min(Xb, Xc), Math.min(Yb, Yc));

                strength = (int) Math.round(_radius / _maxRadius * 100.0f);
            } else
                strength = Math.max(Math.abs(getDelX()), Math.abs(getDelY()));

        } else
            strength = (int) Math.round(100 * Math.hypot(mPosX - mCenterX, mPosY - mCenterY) / mBorderRadius);

        if (strength < mDeadzone) strength = 0;

        return strength;
    }

    public final static int NONE = 0;
    public final static int RIGHT_UP = 1;
    public static final int UP_RIGHT = 2;
    public static final int UP_LEFT = 3;
    public final static int LEFT_UP = 4;
    public final static int LEFT_DOWN = 5;
    public final static int DOWN_LEFT = 6;
    public final static int DOWN_RIGHT = 7;
    public final static int RIGHT_DOWN = 8;

    /**
     * @return return stick position
     * {@link JoystickView#NONE},
     * {@link JoystickView#RIGHT_UP},
     * {@link JoystickView#UP_RIGHT},
     * {@link JoystickView#UP_LEFT},
     * {@link JoystickView#LEFT_UP},
     * {@link JoystickView#LEFT_DOWN},
     * {@link JoystickView#DOWN_LEFT},
     * {@link JoystickView#DOWN_RIGHT},
     * {@link JoystickView#RIGHT_DOWN},
     */
    public int getPosition() {
        if ((mPosY - mCenterY) == 0 && (mPosX - mCenterX) == 0) {
            return 0;
        }
        /*
        int a = 0;
        if (getAngle() <= 0) {
            a = (getAngle() * -1) + 90;
        } else if (getAngle() > 0) {
            if (getAngle() <= 90) {
                a = 90 - getAngle();
            } else {
                a = 360 - (getAngle() - 90);
            }
        }
        Log.e("TAG", "getDirection: "+a + " getAngle: "+getAngle());
*/
    
        return getAngle() / 45 + 1;
    }

    /**
     * Return the current direction allowed for the button to move
     *
     * @return Actually return an integer corresponding to the direction:
     * - A negative value is horizontal axe,
     * - A positive value is vertical axe,
     * - Zero means both axes
     */
    public int getAxisMotion() {
        return mAxisMotionType;
    }

    /**
     * Return the state of the joystick. False when the button don't move.
     *
     * @return the state of the joystick
     */
    @Override
    public boolean isEnabled() {
        return mEnabled;
    }

    /*
     * Returns whether or not joystick is pressed, independent of angle/strength
     */
    public boolean isPressed() {
        return isPressed;
    }

    public boolean isRectangle() {
        return isRectangle;
    }

    /**
     * Return the size of the button (as a ratio of the total width/height)
     * Default is 0.25 (25%).
     *
     * @return button size (value between 0.0 and 1.0)
     */
    public float getButtonSizeRatio() {
        return mButtonSizeRatio * 100;
    }


    /**
     * Return the size of the background (as a ratio of the total width/height)
     * Default is 0.75 (75%).
     *
     * @return background size (value between 0.0 and 1.0)
     */
    public float getBackgroundSizeRatio() {
        return mBackgroundSizeRatio * 100;
    }


    /**
     * Return the current behavior of the auto re-center button
     *
     * @return True if automatically re-centered or False if not
     */
    public boolean isAutoReCenterButton() {
        return mAutoReCenterButton;
    }

    /**
     * Return the current behavior of the button stick to border
     *
     * @return True if the button stick to the border otherwise False
     */
    public boolean isButtonStickToBorder() {
        return mButtonStickToBorder;
    }

    public int getDelX() {
        if (isRectangle)
            return Math.round((mPosX - mCenterX) * 100.0f / (mBorderWidth / 2f));
        else
            return Math.round((mPosX - mCenterX) * 100.0f / mBorderRadius);
    }

    public int getDelY() {
        if (isRectangle)
            return Math.round((mPosY - mCenterY) * -100.0f / (mBorderHeight / 2.0f));
        else
            return Math.round((mPosY - mCenterY) * -100.0f / mBorderRadius);
    }

    /**
     * Return the relative HORIZONTAL coordinate of button center related
     * to top-left virtual corner of the border
     *
     * @return coordinate of HORIZONTAL (normalized between 0 and 100)
     */
    public int getNormalizedX() {
        if (getWidth() == 0) return 50;

        if (isRectangle)
            return Math.round((mPosX - (mCenterX - mBorderWidth / 2f)) * 100.0f / mBorderWidth);
        else
            return Math.round((mPosX - (mCenterX - mBorderRadius)) * 100.0f / (mBorderRadius * 2));
    }

    /**
     * Return the relative VERTICAL coordinate of the button center related
     * to top-left virtual corner of the border
     *
     * @return coordinate of VERTICAL (normalized between 0 and 100)
     */
    public int getNormalizedY() {
        if (getHeight() == 0) return 50;
        if (isRectangle)
            return Math.round(100 - (mPosY - (mCenterY - mBorderHeight / 2f)) * 100.0f / mBorderHeight);
        else
            return Math.round(100 - (mPosY - (mCenterY - mBorderRadius)) * 100.0f / (mBorderRadius * 2));
    }

    /**
     * Return the alpha of the border
     * @return it should be an integer between 0 and 255 previously set
     */
    public int getBorderAlpha() {
        return mBorderAlpha;
    }

    public int getDeadzone() {
        return mDeadzone;
    }

    /**
     * get axis to be centered
     */
    public int getAxisToCenter() {
        return mAxisToCenter;
    }
    /*
    SETTERS
     */

    /**
     * Sets the angle following the 360° counter-clock protractor rules.
     */
    public void setAngle(int angle) {
        mPosX = (int) Math.round((getStrength() * 0.01 * Math.cos(Math.toRadians(angle))) + mCenterX);
        mPosY = (int) Math.round((getStrength() * 0.01 * Math.sin(Math.toRadians(angle))) + mCenterY);
        invalidate();
    }

    /**
     * Sets the strength as a percentage of the distance between the center and the border.
     */
    public void setStrength(int strength) {
        mPosX = (int) Math.round((strength * 0.01 * Math.cos(Math.toRadians(getAngle()))) + mCenterX);
        mPosY = (int) Math.round((strength * 0.01 * Math.sin(Math.toRadians(getAngle()))) + mCenterY);
        invalidate();
    }

    /**
     * Set an image to the button with a drawable
     * @param d drawable to pick the image
     */
    public void setButtonDrawable(Drawable d) {
        mButtonDrawable = d;
    }

    /**
     * Set the button color for this JoystickView.
     * @param color the color of the button
     */
    public void setButtonColor(int color) {
        mPaintCircleButton.setColor(color);
        invalidate();
    }

    /**
     * Set the border color for this JoystickView.
     * @param color the color of the border
     */
    public void setBorderColor(int color) {
        mPaintBorder.setColor(color);
        if (color != Color.TRANSPARENT) {
            mPaintBorder.setAlpha(mBorderAlpha);
        }
        invalidate();
    }

    /**
     * Set the border alpha for this JoystickView.
     * @param alpha the transparency of the border between 0 and 255
     */
    public void setBorderAlpha(int alpha) {
        mBorderAlpha = alpha;
        mPaintBorder.setAlpha(alpha);
        invalidate();
    }


    /**
     * Set the background color for this JoystickView.
     * @param color the color of the background
     */
    @Override
    public void setBackgroundColor(int color) {
        mPaintBackground.setColor(color);
        invalidate();
    }


    /**
     * Set the border width for this JoystickView.
     * @param width the width of the border
     */
    public void setBorderWidth(int width) {
        mPaintBorder.setStrokeWidth(width);
        mBackgroundRadius = mBorderRadius - (width / 2.0f);

        mBackgroundWidth = mBorderWidth - (width / 2f);
        mBackgroundHeight = mBorderHeight - (width / 2f);

        invalidate();
    }


    /**
     * Register a callback to be invoked when this JoystickView's button is moved
     * @param callback The callback that will run
     */
    public void setOnMoveListener(OnMoveListener callback) {
        mCallback = callback;
    }

    /**
     * Set the joystick center's behavior (fixed or auto-defined)
     * @param fixedCenter True for fixed center, False for auto-defined center based on touch down
     */
    public void setFixedCenter(boolean fixedCenter) {
        // if we set to "fixed" we make sure to re-init position related to the width of the joystick
        if (fixedCenter) {
            initPosition();
        }
        mFixedCenter = fixedCenter;
        invalidate();
    }


    /**
     * Enable or disable the joystick
     * @param enabled False mean the button won't move and onMove won't be called
     */
    @Override
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
        super.setEnabled(enabled);

        if (mButtonDrawable == null) return;
        int[] state = new int[]{enabled ? android.R.attr.state_enabled : -android.R.attr.state_enabled};
        mButtonDrawable.setState(state);

    }


    /**
     * Set the joystick button size (as a fraction of the real width/height)
     * By default it is 25% (25).
     * @param newRatio between 0.0 and 100.0
     */
    public void setButtonSizeRatio(float newRatio) {
        if (newRatio > 0.0f & newRatio <= 100.0f) {
            mButtonSizeRatio = newRatio / 100;
        }
    }


    /**
     * Set the joystick button size (as a fraction of the real width/height)
     * By default it is 75% (75).
     * Not working if the background is an image.
     * @param newRatio between 0.0 and 100.0
     */
    public void setBackgroundSizeRatio(float newRatio) {
        if (newRatio > 0.0f & newRatio <= 100f) {
            mBackgroundSizeRatio = newRatio / 100;
        }
    }


    /**
     * Set the current behavior of the auto re-center button
     * @param autoReCenter True if automatically re-centered or False if not
     */
    public void setAutoReCenterButton(boolean autoReCenter) {
        mAutoReCenterButton = autoReCenter;

        if (mAutoReCenterButton)
            resetButtonPosition();

        invalidate();
    }


    /**
     * Set the current behavior of the button stick to border
     * @param b True if the button stick to the border or False (default) if not
     */
    public void setButtonStickToBorder(boolean b) {
        mButtonStickToBorder = b;
    }

    public void setRectangle(boolean useRectangle) {
        this.isRectangle = useRectangle;
        invalidate();
    }

    /**
     * Set the current authorized direction for the button to move
     * @param direction the value will define the authorized direction:
     *                  - any negative value (such as -1) for horizontal axe
     *                  - any positive value (such as 1) for vertical axe
     *                  - zero (0) for the full direction (both axes)
     */
    public void setAxisMotion(int direction) {
        mAxisMotionType = direction;
        onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());
        invalidate();
    }

    /**
     * Set the joystick deadzone from 0-100. Having no of full deadzone is not recommended
     * @param deadzone The deadzone, from 0-100. Strengths lower than it get reduced to 0.
     */
    public void setDeadzone(int deadzone) {
        if (deadzone < 0) deadzone = 0;
        if (deadzone > 100) deadzone = 100;
        mDeadzone = deadzone;
    }

    /**
     * set axis to be centered
     */
    public void setAxisToCenter(int axisToCenter) {
        this.mAxisToCenter = axisToCenter;
    }
}