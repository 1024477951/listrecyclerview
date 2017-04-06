package cn.com.listwebview.demo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import cn.com.listwebview.demo.utils.DensityUtil;

/**
 * Created by LiuZhen on 2017/3/24.
 */
public class TwinklingRefreshLayout extends FrameLayout{

    private String TAG = "TwinklingRefreshLayout";
    private int downY;// 按下时y轴的偏移量
    private final static float RATIO = 3f;
    //头部的高度
    protected int mHeadHeight;
    //头部layout
    protected FrameLayout mHeadLayout;//头部父容器
    private HeaderView mHeadView;//头部
    protected FrameLayout mFootLayout;//头部父容器
    private ImageView ivArrow_left,ivArrow_right; //头布局的剪头
    private ProgressBar mProgressBar; // 底布局的进度条
    private Animation upAnimation;// 向上旋转的动画
    private Animation downAnimation;// 向下旋转的动画

    private final int DOWN_PULL_REFRESH = 0;// 下拉刷新状态
    private final int RELEASE_REFRESH = 1;// 松开刷新
    private final int REFRESHING = 2;// 正在刷新中
    private final int END = 3;// 正在刷新中
    private int currentState = DOWN_PULL_REFRESH;// 头布局的状态: 默认为下拉刷新状态
    private ListView list;//子节点中的listview视图
    private LayoutParams listParam,footParam;//用于控制下拉动画展示
    private boolean isLoadingMore = false;// 是否进入加载状态，防止多次重复的启动
    private boolean isStart = false;//表示正在加载刷新中，还没停止
    private boolean isTop = false,isBottom = false;

    public TwinklingRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public TwinklingRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwinklingRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TwinklingRefreshLayout, defStyleAttr, 0);
        try {

            mHeadHeight = a.getDimensionPixelSize(R.styleable.TwinklingRefreshLayout_tr_head_height, DensityUtil.dp2px(context, 40));
        } finally {
            a.recycle();
        }
        addHeader();
        init();
    }

    private void addHeader() {
        FrameLayout headViewLayout = new FrameLayout(getContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,mHeadHeight);
        this.addView(headViewLayout,layoutParams);

        mHeadLayout = headViewLayout;
    }

    private void init(){
        initAnimation();
    }

    @Override
    protected void onFinishInflate() {//布局加载成xml时触发
        super.onFinishInflate();

        if (mHeadView == null) setHeaderView(new HeaderView(getContext()));
        setFootView();
        if (list == null) {
            View child = getChildAt(1);
            if (child instanceof ListView){
                list = (ListView)child;
            }
            listParam = (LayoutParams) list.getLayoutParams();
//            listParam.setMargins(0, -mHeadHeight, 0, 0);
//            list.setLayoutParams(listParam);
            list.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return onFingerTouch(event);
                }
            });
            list.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    isTop = false;
                    isBottom = false;
                    //判断顶部底部
                    if (firstVisibleItem == 0) {
                        Log.d(TAG, "滚动到顶部");
                        isTop = true;
                        isBottom = false;
                    } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
                        Log.d(TAG, "滚动到底部");
                        isTop = false;
                        isBottom = true;
                    }
                }
            });
        }
    }

    /**
     * 设置头部View
     */
    public void setHeaderView(final HeaderView headerView) {
        if (headerView != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    mHeadLayout.removeAllViewsInLayout();
                    mHeadLayout.addView(headerView.getView());

                    View view = LayoutInflater.from(getContext()).inflate(R.layout.item_head_progress,null);
//                    mProgressBar = (ProgressBar) view.findViewById(R.id.pb_listview_header);
                    ivArrow_left = (ImageView) view.findViewById(R.id.iv_listview_header_arrow_left) ;
                    ivArrow_right = (ImageView) view.findViewById(R.id.iv_listview_header_arrow_right) ;
                    mHeadLayout.addView(view);
                }
            });
            mHeadView = headerView;
        }
    }

    /**
     * 设置尾部View
     */
    public void setFootView() {
        footParam = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,mHeadHeight);
        FrameLayout footViewLayout = new FrameLayout(getContext());//底部布局
        this.addView(footViewLayout,footParam);
        this.mFootLayout = footViewLayout;
        mFootLayout.setBackgroundColor(Color.BLACK);
        footParam.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        footParam.setMargins(0,0,0,-mHeadHeight);
        mProgressBar = new ProgressBar(getContext(),null,android.R.attr.progressBarStyleSmallInverse);
        mFootLayout.addView(mProgressBar);
    }


    public boolean onFingerTouch(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN :
                currentState = REFRESHING;
                downY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE :
                if (!isTop && !isBottom)//没有到顶，无需计算操作
                    break;
                int moveY = (int) ev.getY();
                int diff = (int) (((float)moveY - (float)downY) / RATIO);
//                int paddingTop = -mHeadLayout.getHeight() + diff;
                int paddingTop = diff;
                if (diff>0 && isTop) {
                    //向下滑动多少后开始启动刷新
                    if (paddingTop >= 200 && currentState == DOWN_PULL_REFRESH) { // 完全显示了.
                        Log.i(TAG, "松开刷新 RELEASE_REFRESH");
                        currentState = RELEASE_REFRESH;
                        refreshHeaderView();
                        start();
                    } else if (currentState == REFRESHING) { // 没有显示完全
                        Log.i(TAG, "下拉刷新 DOWN_PULL_REFRESH");
                        currentState = DOWN_PULL_REFRESH;
                        refreshHeaderView();
                    }
                    if (paddingTop <= 400 && !isStart) {//已经处于运行刷新状态的时候禁止设置
                        listParam.setMargins(0, paddingTop, 0, 0);
                        list.setLayoutParams(listParam);
                    }

                }else if (isBottom){
                    //限制上滑时不能超过底部的宽度，不然会超出边界
                    if (paddingTop <= -50 && paddingTop >= -mHeadHeight && !isStart) {//已经处于运行刷新状态的时候禁止设置
                        listParam.setMargins(0, 0, 0, -paddingTop);
                        footParam.setMargins(0,0,0,-paddingTop-mHeadHeight);
                        list.setLayoutParams(listParam);
                    }
                    if (paddingTop <= -mHeadHeight)
                        isLoadingMore = true;
                }
                Log.i(TAG,"paddingTop "+paddingTop);
                break;
            case MotionEvent.ACTION_UP :

                if (isLoadingMore){
                    isLoadingMore = false;
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            Log.i(TAG, "停止 END");
//                            currentState = END;
                            refreshHeaderView();
                            listParam.setMargins(0, 0, 0, 0);
                            footParam.setMargins(0,0,0,-mHeadHeight);
                            list.setLayoutParams(listParam);
                            stop();
                        }
                    },2000);
                }else{
                    if (!isStart){
                        // 隐藏头布局
                        listParam.setMargins(0, 0,0,0);
                        footParam.setMargins(0,0,0,-mHeadHeight);
                        list.setLayoutParams(listParam);
                    }
                }
                Log.i(TAG, "松开 REFRESHING");
                currentState = REFRESHING;
                break;
            default :
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 初始化动画
     */
    private void initAnimation() {
		/*
         * Animation.RELATIVE_TO_SELF   相对于自身的动画
         * Animation.RELATIVE_TO_PARENT 相对于父控件的动画
         * 0.5f，表示在控件自身的 x，y的中点坐标处，为动画的中心。
         *
         * 设置动画的变化速率
         * setInterpolator(newAccelerateDecelerateInterpolator())：先加速，后减速
         * setInterpolator(newAccelerateInterpolator())：加速
         * setInterpolator(newDecelerateInterpolator())：减速
         * setInterpolator(new CycleInterpolator())：动画循环播放特定次数，速率改变沿着正弦曲线
         * setInterpolator(new LinearInterpolator())：匀速
         */
        upAnimation = new RotateAnimation(0f, -180f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        upAnimation.setInterpolator(new LinearInterpolator());
        upAnimation.setDuration(700);
        upAnimation.setFillAfter(true); // 动画结束后, 停留在结束的位置上

        downAnimation = new RotateAnimation(-180f, -360f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        downAnimation.setInterpolator(new LinearInterpolator());//这句话可以不写，默认匀速
        downAnimation.setDuration(700);
        downAnimation.setFillAfter(true); // 动画结束后, 停留在结束的位置上
    }

    /**
     * 根据currentState刷新头布局的状态
     */
    private void refreshHeaderView() {
        switch (currentState) {
            case DOWN_PULL_REFRESH : // 下拉刷新状态
//                tvState.setText("下拉刷新");
                ivArrow_left.startAnimation(downAnimation); // 执行向下旋转
                ivArrow_right.startAnimation(downAnimation); // 执行向下旋转
                break;
            case RELEASE_REFRESH : // 松开刷新状态
//                tvState.setText("松开刷新");
                ivArrow_left.startAnimation(upAnimation);// 执行向上旋转
                ivArrow_right.startAnimation(upAnimation);// 执行向上旋转
                break;
            case REFRESHING : // 正在刷新中状态
                ivArrow_left.clearAnimation();
                ivArrow_right.clearAnimation();
//                tvState.setText("正在刷新中...");
                break;
            default :
                break;
        }
    }

    public void start(){
        isLoadingMore = true;
        isStart = true;
        mHeadView.onPullingDown(0);
        mHeadView.startAnim();
    }

    public void stop(){
        isLoadingMore = false;
        isStart = false;
        mHeadView.reset();
    }

}
