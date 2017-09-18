package com.linsh.lshapp.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.tools.FloatingViewManager;
import com.linsh.lshapp.view.ImportWechatFloatingView;
import com.linsh.lshutils.tools.LshAccessibilityHelper;
import com.linsh.lshutils.utils.Basic.LshStringUtils;

import java.util.ArrayList;
import java.util.List;

public class Test5Service extends AccessibilityService {
    public static final String COMMAND = "COMMAND";
    public static final String COMMAND_CLOSE = "COMMAND_CLOSE";
    public static final String COMMAND_OPEN = "COMMAND_OPEN";

    FloatingViewManager mFloatingViewManager;
    LshAccessibilityHelper mHelper;

    public void onCreate() {
        super.onCreate();
        Log.i("LshLog", "onCreate: 服务启动");
        if (mFloatingViewManager == null) {
            mFloatingViewManager = new FloatingViewManager();
            mFloatingViewManager.setView(new ImportWechatFloatingView(this));
        }
        if (mHelper == null) {
            mHelper = new LshAccessibilityHelper(this);
        }
    }

    @Override
    protected void onServiceConnected() {
        Log.i("LshLog", "onServiceConnected: ");
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

    private void onStateChanged(AccessibilityEvent event) {
        String packageName = event.getPackageName().toString();
        String className = event.getClassName().toString();
        // 详细资料界面
        if (className.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
            String name = null;
            List<Type> types = new ArrayList<>();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                AccessibilityNodeInfo info = mHelper.findFirstNodeInfoByViewId("com.tencent.mm:id/n_");// 备注名
                if (info != null) {
                    name = info.getText().toString().trim();
                }
                info = mHelper.findFirstNodeInfoByViewId("com.tencent.mm:id/ah8"); // 微信号
                if (info != null) {
                    String wechatId = info.getText().toString().replaceAll("微信号:", "").trim();
                    if (wechatId.length() > 0) {
                        types.add(new Type("微信号", wechatId, true));
                    }
                }
                List<String> allText = mHelper.findAllText("com.tencent.mm:id/ia"); // 地区
                for (int i = 0; i < allText.size(); i++) {
                    String text = allText.get(i);
                    if ("地区".equals(text) && i + 1 < allText.size()) {
                        types.add(new Type("地址", allText.get(i + 1)));
                        break;
                    }
                }
                info = mHelper.findFirstNodeInfoByViewId("com.tencent.mm:id/c_c"); // 描述
                if (info != null) {
                    String desc = info.getText().toString().trim();
                    if (desc.length() > 0) {
                        types.add(new Type("备注", desc));
                    }
                }
            }
            if (LshStringUtils.isNotAllEmpty(name) && types.size() > 0) {
                RxBus.getDefault().post(new WechatContactEvent(name, types));
            }
        } else {
            // 防止点击保存后执行
            if (packageName.equals("com.linsh.lshapp")) return;
            if (packageName.equals("com.tencent.mm") && className.startsWith("android")) return;
            RxBus.getDefault().post(new WechatContactEvent(null, null));
        }
    }

    @Override
    public void onInterrupt() {
        Log.i("LshLog", "onInterrupt: ");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.i("LshLog", "onDestroy");
    }

    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2) {
        Log.i("LshLog", "onStartCommand");
        String data = paramIntent.getStringExtra(COMMAND);
        if (data != null) {
            if ((data.equals(COMMAND_OPEN))) {
                if (mFloatingViewManager == null) {
                    mFloatingViewManager = new FloatingViewManager();
                    mFloatingViewManager.setView(new ImportWechatFloatingView(this));
                }
            } else if ((data.equals(COMMAND_CLOSE))) {
                if (mFloatingViewManager != null) {
                    mFloatingViewManager.removeView();
                    mFloatingViewManager = null;
                }
            }
        }
        return super.onStartCommand(paramIntent, paramInt1, paramInt2);
    }

    public static class WechatContactEvent {
        private final String mName;
        private final List<Type> mTypes;

        public WechatContactEvent(String name, List<Type> types) {
            this.mName = name;
            this.mTypes = types;
        }

        public String getName() {
            return this.mName;
        }

        public List<Type> getTypes() {
            return this.mTypes;
        }
    }

    public static class Type {
        public String type;
        public String value;
        public boolean selected;
        public boolean need;

        public Type(String type, String value) {
            this.type = type;
            this.value = value;
        }

        public Type(String type, String value, boolean cancel) {
            this.type = type;
            this.value = value;
            this.need = cancel;
        }
    }
}