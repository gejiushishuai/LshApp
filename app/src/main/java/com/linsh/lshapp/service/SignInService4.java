package com.linsh.lshapp.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.linsh.utilseverywhere.tools.AccessibilityHelper;
import com.linsh.utilseverywhere.AppUtils;
import com.linsh.utilseverywhere.LogUtils;
import com.linsh.utilseverywhere.ScreenUtils;
import com.linsh.utilseverywhere.ShellUtils;
import com.linsh.utilseverywhere.ToastUtils;
import com.linsh.lshapp.model.bean.SignIn;
import com.linsh.lshapp.mvp.home.yingmao.SignInHelper;

import java.io.DataOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SignInService4 extends AccessibilityService {

    private Handler mHandler;
    private AccessibilityHelper mHelper;
    private String lastPacName;
    private String lastClzName;
    private List<SignIn> mSignIns;
    private int curIndex = -1;
    private boolean error;
    private boolean isRoot;
    private boolean isWorking;

    public void onCreate() {
        super.onCreate();
        LogUtils.i("onCreate");
        if (mHelper == null) {
            mHelper = new AccessibilityHelper(this);
            mHandler = new Handler();
            isRoot = getRoot();
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        isWorking = true;
    }

    /**
     * 监听窗口变化的回调
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i("LshLog", "onAccessibilityEvent: PackageName=" + event.getPackageName() + "   className=" + event.getClassName());
        switch (event.getEventType()) {
            //当窗口的状态发生改变时
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                onStateChanged(event);
                break;
        }
    }

    @Override
    public void onInterrupt() {
        LogUtils.i("onInterrupt");
    }

    private void onStateChanged(AccessibilityEvent event) {
        String packageName = event.getPackageName().toString();
        String className = event.getClassName().toString();

        SignIn signIn = getCurSignIn();
        if (signIn == null || signIn.getState() == SignIn.STATE_SIGNED) {
            return;
        }
        if (isRoot) {
            signInWithRoot(signIn, packageName, className);
        } else {
            signInWithoutRoot(signIn, packageName, className);
        }
        lastPacName = packageName;
        lastClzName = className;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void signInWithoutRoot(SignIn signIn, String packageName, String className) {
        switch (signIn.getClient()) {
            //// 电信营业厅 ////
            case DianXinYYT:
                if (className.equals("com.ct.client.MainActivity")) {
                    ToastUtils.show("点击 ->【签到送流量】");
                } else if (className.equals("com.ct.client.common.webview.OnlineBusinessWebkitActivity")) {
                    if (mHelper.findFirstNodeInfoByText("我的签到") != null) {
                        // 签到页
                        signIn.setState(SignIn.STATE_SIGNED);
                        updateSignInDb(signIn);
                        ToastUtils.show("签到成功 (不成功的话请反馈!)");
                        signInNextClient();
                    }
                }
                break;
            //// 中国移动 ////
            case CnMobile:
                if (className.equals("com.leadeon.cmcc.view.tabs.AppTabFragment")) {
                    // 主页 -> 跳转签到页
                    ToastUtils.show("点击 ->【签到】悬浮窗");
                } else if (className.equals("com.leadeon.cmcc.view.mine.html5.CommonHtml5Activity")) {
                    // 签到页 -> 点击签到
                    if (mHelper.findFirstNodeInfoByText("成长值") != null) {
                        mHandler.postDelayed(() -> {
                            signIn.setState(SignIn.STATE_SIGNED);
                            updateSignInDb(signIn);
                            ToastUtils.show("签到成功 (不成功的话请反馈!)");
                            signInNextClient();
                        }, 2000);
                    }
                }
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void signInWithRoot(SignIn signIn, String packageName, String className) {
        mHandler.removeCallbacksAndMessages(null);
        switch (signIn.getClient()) {
            //// 电信营业厅 ////
            case DianXinYYT:
                if (className.equals("com.ct.client.MainActivity")) {
                    // 主页 -> 点击签到
                    mHandler.postDelayed(() -> {
                        if (error) {
                            ToastUtils.show("我好像找不到签到按钮了, 你自己来吧...");
                            return;
                        }
                        AccessibilityNodeInfo nodeInfo = mHelper.findFirstNodeInfoByViewId("com.ct.client:id/shan");
                        if (nodeInfo != null) {
                            Rect rect = new Rect();
                            nodeInfo.getBoundsInScreen(rect);
                            int x = rect.left + (rect.right - rect.left) / 2;
                            int y = rect.top + (rect.bottom - rect.top) * 2 / 3;
                            ShellUtils.execCmd("input tap " + x + " " + y, true);
                        }
                    }, 1000);
                } else if (className.equals("com.ct.client.common.webview.OnlineBusinessWebkitActivity")) {
                    if (mHelper.findFirstNodeInfoByText("我的签到") != null || mHelper.findFirstNodeInfoByText("签到送流量") != null) {
                        // 签到页
                        signIn.setState(SignIn.STATE_SIGNED);
                        updateSignInDb(signIn);
                        ToastUtils.show("签到成功 (不成功的话请反馈!)");
                        signInNextClient();
                    } else {
                        ToastUtils.show("等等... 是不是进错页面了?");
                    }
                } else if (className.equals("com.ct.client.SwitchUserActivity")) {
                    // 登陆页
                    signIn.setState(SignIn.STATE_UNSIGNED);
                    ToastUtils.show("等等... 好像需要登录!");
                } else if (className.equals("com.ct.client.common.webview.CommWebkitActivity")
                        || className.equals("com.ct.client.recharge.ltepackage.BuyLtePackageActivity")
                        || className.equals("com.hg.activity.HGProxyFragmentActivity")
                        || className.equals("com.hg.activity.HGProxyActivity")) {
                    ToastUtils.show("等等... 好像进错页面了!");
                    error = true;
                }
                break;
            //// 中国移动 ////
            case CnMobile:
                if ("com.greenpoint.android.mc10086.activity".equals(packageName) && "android.widget.FrameLayout".equals(className)) {
                    // 主页广告 -> 返回键关闭
                    AccessibilityNodeInfo nodeInfo = mHelper.findFirstNodeInfoByViewId("com.greenpoint.android.mc10086.activity:id/ad_image");
                    if (nodeInfo != null) {
                        ShellUtils.execCmd("input keyevent 4", true);
                    }
                } else if (className.equals("com.leadeon.cmcc.view.tabs.AppTabFragment")) {
                    // 主页 -> 跳转签到页
                    AccessibilityNodeInfo nodeInfo = mHelper.findFirstNodeInfoByViewId("com.greenpoint.android.mc10086.activity:id/drag_img");
                    if (nodeInfo != null) {
                        mHandler.postDelayed(() -> {
                            Rect rect = new Rect();
                            nodeInfo.getBoundsInScreen(rect);
                            LogUtils.i("点击签到悬浮窗");
                            int x = rect.left + (rect.right - rect.left) / 2;
                            int y = rect.top + (rect.bottom - rect.top) / 2;
                            ShellUtils.execCmd("input tap " + x + " " + y, true);
                        }, 3000);
                    }
                } else if (className.equals("com.leadeon.cmcc.view.mine.html5.CommonHtml5Activity")) {
                    // 签到页 -> 点击签到
                    if (mHelper.findFirstNodeInfoByText("成长值") != null) {
                        mHandler.postDelayed(() -> {
                            int x = ScreenUtils.getScreenWidth() / 2;
                            int y = ScreenUtils.getScreenHeight() / 2;
                            ShellUtils.execCmd("input tap " + x + " " + y, true);

                            signIn.setState(SignIn.STATE_SIGNED);
                            updateSignInDb(signIn);
                            ToastUtils.show("签到成功 (不成功的话请反馈!)");
                            signInNextClient();
                        }, 3000);
                    }
                } else if (className.equals("com.leadeon.sdk.view.UserLoginActivity")) {
                    signIn.setState(SignIn.STATE_UNSIGNED);
                    ToastUtils.show("等等... 好像需要登录!");
                }
                break;
            default:
                break;
        }
    }

    private void updateSignInDb(SignIn signIn) {
        SignInHelper.refreshState(signIn);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isWorking) {
            boolean root = intent.getBooleanExtra("root", false);
            if (root && !isRoot) {
                isRoot = getRoot();
            }
            Serializable serializable = intent.getSerializableExtra("sign_in");
            if (serializable != null && serializable instanceof SignIn) {
                SignIn signIn = (SignIn) serializable;
                if (mSignIns == null) {
                    mSignIns = new ArrayList<>();
                } else {
                    mSignIns.clear();
                }
                mSignIns.add(signIn);
                signIn(0);
            }
        } else {
            ToastUtils.show("服务启动失败, 请检查!");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private SignIn getCurSignIn() {
        if (mSignIns != null && curIndex < mSignIns.size()) {
            return mSignIns.get(curIndex);
        }
        return null;
    }

    private void signInNextClient() {
        signIn(curIndex++);
    }

    private void signIn(int index) {
        error = false;
        if (mSignIns != null && index < mSignIns.size()) {
            curIndex = index;
            SignIn signIn = mSignIns.get(index);
            switch (signIn.getClient()) {
                case DianXinYYT:
                    AppUtils.launchApp("com.ct.client");
                    break;
                case CnMobile:
                    AppUtils.launchApp("com.greenpoint.android.mc10086.activity");
                    break;
                default:
                    break;
            }
        } else {
            curIndex = -1;
        }
    }

    public synchronized boolean getRoot() {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "
                    + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}