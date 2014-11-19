package project.ui;

import com.thybase.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.TabHost;

public class AnimationTabHost extends TabHost {

	private boolean isOpenAnimation;
	private int mTabCount;
	private Animation slideLeftIn;
	private Animation slideRightIn;
	private Animation slideLeftOut;
	private Animation slideRightOut;

	public Animation initAnimation() {
		Animation animation = null;
		animation = new TranslateAnimation(0 * 0, 0 * 0, 0, 0);

		animation.setFillAfter(true);
		animation.setDuration(300);
		return animation;
	}

	public AnimationTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		isOpenAnimation = false;
		slideLeftIn = AnimationUtils.loadAnimation(context,
				R.anim.slide_left_in);
		slideLeftOut = AnimationUtils.loadAnimation(context,
				R.anim.slide_left_out);
		slideRightIn = AnimationUtils.loadAnimation(context,
				R.anim.slide_right_in);
		slideRightOut = AnimationUtils.loadAnimation(context,
				R.anim.slide_right_out);
	}

	public void setOpenAnimation(boolean isOpenAnimation) {
		this.isOpenAnimation = isOpenAnimation;
	}

	public boolean setTabAnimation(int[] animationResIDs) {
		return false;
	}

	public int getTabCount() {
		return mTabCount;
	}

	public void addTab(TabSpec tabSpec) {
		mTabCount++;
		super.addTab(tabSpec);
	}

	public void setCurrentTab1(int index) {

		if (null != getCurrentView()) {
			if (isOpenAnimation) {
			}
		}
		super.setCurrentTab(index);
		if (isOpenAnimation) {
		}

	}

	@Override
	public void setCurrentTab(int index) {
		// index为要切换到的tab页索引，currentTabIndex为现在要当前tab页的索引
		int currentTabIndex = getCurrentTab();

		// 设置当前tab页退出时的动画
		if (null != getCurrentView()) {// 第一次进入MainActivity时，getCurrentView()取得的值为空
			/*if (currentTabIndex == (mTabCount - 1) && index == 0) {// 处理边界滑动
				getCurrentView().startAnimation(slideLeftOut);
			} else if (currentTabIndex == 0 && index == (mTabCount - 1)) {// 处理边界滑动
				getCurrentView().startAnimation(slideRightOut);
			} else*/ 
			if (index > currentTabIndex) {// 非边界情况下从右往左fleep
				getCurrentView().startAnimation(slideLeftOut);
			} else if (index < currentTabIndex) {// 非边界情况下从左往右fleep
				getCurrentView().startAnimation(slideRightOut);
			}
		}

		super.setCurrentTab(index);

		// 设置即将显示的tab页的动画
		/*if (currentTabIndex == (mTabCount - 1) && index == 0) {// 处理边界滑动
			getCurrentView().startAnimation(slideLeftIn);
		} else if (currentTabIndex == 0 && index == (mTabCount - 1)) {// 处理边界滑动
			getCurrentView().startAnimation(slideRightIn);
		} else*/ 
		if (index < currentTabIndex) {// 非边界情况下从右往左fleep
			getCurrentView().startAnimation(slideLeftIn);
		} else if (index > currentTabIndex) {// 非边界情况下从左往右fleep
			getCurrentView().startAnimation(slideRightIn);
		}
	}
}
