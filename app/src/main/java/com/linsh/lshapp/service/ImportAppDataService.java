package com.linsh.lshapp.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.linsh.utilseverywhere.tools.AccessibilityHelper;
import com.linsh.utilseverywhere.RegexUtils;
import com.linsh.utilseverywhere.StringUtils;
import com.linsh.lshapp.Rx.RxBus;
import com.linsh.lshapp.tools.FloatingViewManager;
import com.linsh.lshapp.view.ImportWechatFloatingView;

import java.util.ArrayList;
import java.util.List;

public class ImportAppDataService extends AccessibilityService {
    public static final String COMMAND = "COMMAND";
    public static final String COMMAND_CLOSE = "COMMAND_CLOSE";
    public static final String COMMAND_OPEN = "COMMAND_OPEN";

    private FloatingViewManager mFloatingViewManager;
    private AccessibilityHelper mHelper;
    private String curName;

    public void onCreate() {
        super.onCreate();
        Log.i("LshLog", "onCreate: 服务启动");
        if (mFloatingViewManager == null) {
            mFloatingViewManager = new FloatingViewManager();
            mFloatingViewManager.setView(new ImportWechatFloatingView(this));
        }
        if (mHelper == null) {
            mHelper = new AccessibilityHelper(this);
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
        // 微信详细资料界面
        if (className.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
            curName = null;
            List<Type> types = new ArrayList<>();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                AccessibilityNodeInfo info = mHelper.findFirstNodeInfoByViewId("com.tencent.mm:id/n_");// 备注名
                if (info != null) {
                    curName = info.getText().toString().trim();
                }
                info = mHelper.findFirstNodeInfoByViewId("com.tencent.mm:id/ah8"); // 微信号
                if (info != null) {
                    String wechatId = info.getText().toString().replaceAll("微信号:", "").trim();
                    if (wechatId.length() > 0) {
                        types.add(new Type("微信号", wechatId, true));
                    }
                }
                List<String> allText = mHelper.findAllTexts("com.tencent.mm:id/ia"); // 地区
                for (int i = 0; i < allText.size(); i++) {
                    String text = allText.get(i);
                    if ("地区".equals(text) && i + 1 < allText.size()) {
                        types.add(new Type("地址", allText.get(i + 1).replaceAll(" ", "").trim()));
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
            if (StringUtils.isNotAllEmpty(curName)) {
                RxBus.getDefault().post(new WechatContactEvent(curName, types));
            }
        } else if (className.equals("com.tencent.mobileqq.activity.FriendProfileCardActivity")) {
            // QQ个人信息界面
            curName = null;
            List<Type> types = new ArrayList<>();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                AccessibilityNodeInfo info = mHelper.findFirstNodeInfoByViewId("com.tencent.mobileqq:id/common_xlistview"); // 备注名
                if (info != null) {
                    List<String> descriptions = mHelper.findAllContentDescriptions(info);
                    for (String description : descriptions) {
                        if (description.matches("昵称:.+")) {
                            curName = description.replaceAll("昵称:", "").trim();
                            break;
                        }
                    }
                }
                info = mHelper.findFirstNodeInfoByViewId("com.tencent.mobileqq:id/info"); // QQ号
                if (info != null) {
                    String qqId = info.getText().toString().trim();
                    if (qqId.matches("\\d+")) {
                        types.add(new Type("QQ号", qqId, true));
                    } else if (qqId.matches(".+\\(\\d+\\)")) {
                        types.add(new Type("QQ号", qqId.replaceAll(".+\\(", "").replaceAll("\\)", ""), true));
                    }
                }
            }
            if (StringUtils.isNotAllEmpty(curName)) {
                RxBus.getDefault().post(new WechatContactEvent(curName, types));
            }
        } else if (className.equals("com.alibaba.android.user.profile.v2.UserProfileActivity")) {
            // 钉钉个人信息界面
            curName = null;
            List<Type> types = new ArrayList<>();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                AccessibilityNodeInfo info = mHelper.findFirstNodeInfoByViewId("com.alibaba.android.rimet:id/user_header_full_name");// 姓名
                if (info != null) {
                    curName = info.getText().toString().trim();
                }
                info = mHelper.findFirstNodeInfoByViewId("com.alibaba.android.rimet:id/user_mobile_info_content_tv"); // 电话
                if (info != null) {
                    String qqId = info.getText().toString().trim();
                    qqId = qqId.replaceAll("[+]\\d{2}-", "");
                    if (qqId.matches("[\\d+-]+")) {
                        types.add(new Type("电话", qqId, true));
                    }
                }
                List<String> allTexts = mHelper.findAllTexts();
                for (int i = 0; i < allTexts.size(); i++) {
                    String text = allTexts.get(i);
                    if (text.contains("邮箱")) {
                        if (i + 1 < allTexts.size()) {
                            String email = allTexts.get(i + 1);
                            if (RegexUtils.isEmail(email)) {
                                types.add(new Type("邮箱", email));
                            }
                        }
                    } else if (text.equals("部门")) {
                        if (i + 1 < allTexts.size()) {
                            types.add(new Type("工作", allTexts.get(i + 1), "部门"));
                        }
                    } else if (text.equals("职位")) {
                        if (i + 1 < allTexts.size()) {
                            types.add(new Type("工作", allTexts.get(i + 1), "职位"));
                        }
                    }
                }
            }
            if (StringUtils.isNotAllEmpty(curName)) {
                RxBus.getDefault().post(new WechatContactEvent(curName, types));
            }
        } else {
            if (className.startsWith("android.app.")) return; // 防止点击界面控件而发生变化
            if (className.startsWith("android.widget.")) return;
            if (className.startsWith("com.tencent.smtt.webkit.adapter")) return;
            if (className.equals("com.tencent.mobileqq.activity.QQBrowserActivity")) {
                RxBus.getDefault().post(new WechatContactEvent(curName, new ArrayList<>()));
                return;
            }
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
        public String typeDetail;
        public String describe;
        public boolean selected;
        public boolean need;

        public Type(String type, String typeDetail) {
            this.type = type;
            this.typeDetail = typeDetail;
        }

        public Type(String type, String typeDetail, String describe) {
            this.type = type;
            this.typeDetail = typeDetail;
            this.describe = describe;
        }

        public Type(String type, String typeDetail, boolean cancel) {
            this.type = type;
            this.typeDetail = typeDetail;
            this.need = cancel;
        }
    }
}