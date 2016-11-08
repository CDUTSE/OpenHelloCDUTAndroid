package com.emptypointer.hellocdut.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;


import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPApplication;
import com.emptypointer.hellocdut.customer.EPJsonObjectRequest;
import com.emptypointer.hellocdut.customer.EPJsonResponseListener;
import com.emptypointer.hellocdut.customer.EPPagerAdapter;
import com.emptypointer.hellocdut.customer.EPProgressDialog;
import com.emptypointer.hellocdut.model.DataCache;
import com.emptypointer.hellocdut.model.UserInfo;
import com.emptypointer.hellocdut.service.CacheService;
import com.emptypointer.hellocdut.service.EPSecretService;
import com.emptypointer.hellocdut.service.EPUpdateService;
import com.emptypointer.hellocdut.ui.account.LoginActivity;
import com.emptypointer.hellocdut.ui.account.ProfileActivity;
import com.emptypointer.hellocdut.ui.account.RelativeAccountActivity;
import com.emptypointer.hellocdut.ui.basic.SettingActivity;
import com.emptypointer.hellocdut.utils.DialogButtonClickListner;
import com.emptypointer.hellocdut.utils.UIUtils;
import com.emptypointer.hellocdut.utils.VolleyUtil;
import com.emptypointer.hellocdut.widget.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final String CACHE_SCHEDULED_TASK = "cache_scheduled_task";
    private static final String PRE_LAST_NOTIFY_ID = "pre_last_notify_id";
    private ViewPager viewPager;
    private TabLayout tbIndicator;
    private EPPagerAdapter mPagerAdapter;
    private TextView tvNickName, tvUsername;
    private ImageView  imAvatar;




    private BroadcastReceiver broadcastReceiver;

    private LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        EPApplication.getInstance().pushActivity(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        viewPager = (ViewPager) findViewById(R.id.container);
        View headerView = View.inflate(this, R.layout.nav_header_main, null);
        imAvatar =
                (ImageView) headerView.findViewById(R.id.imAvatar);
        ((CircleImageView)imAvatar).setCircleRadiu(-1);
        tvUsername = (TextView) headerView.findViewById(R.id.tvUsername);
        tvNickName = (TextView) headerView.findViewById(R.id.tvNickname);


        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.addHeaderView(headerView);

        navigationView.setNavigationItemSelectedListener(this);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(FunctionFragment.newInstance());
//        fragments.add(mContactFragment);
//        fragments.add(mConversationFragment);
//        List<String> title= Arrays.asList(getResources().getStringArray(R.array.indicator_main));
        List<String> title = Arrays.asList("校园");
        mPagerAdapter = new EPPagerAdapter(getSupportFragmentManager(), title, fragments);


        viewPager.setAdapter(mPagerAdapter);
        tbIndicator = (TabLayout) findViewById(R.id.tbIndicator);
        tbIndicator.setupWithViewPager(viewPager);
        tbIndicator.setTabMode(TabLayout.MODE_FIXED);
        UserInfo info = UserInfo.getInstance(this);
        updateAvatar(info);
        tvNickName.setText(info.getNickName());
        tvUsername.setText(getString(R.string.str_format_account, info.getUserName()));
        imAvatar.setOnClickListener(this);
        for (int i = 0; i < tbIndicator.getTabCount(); i++) {
            TabLayout.Tab tab = tbIndicator.getTabAt(i);
            View tabView = mPagerAdapter.getTabView(i, this);
            tab.setCustomView(tabView);
        }
        tbIndicator.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.tvTitle)).setTextColor(getResources().getColor(R.color.colorPrimary));
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((TextView) tab.getCustomView().findViewById(R.id.tvTitle)).setTextColor(getResources().getColor(R.color.color_half_blank));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        // 注册群组和联系人监听
//        EMHelper.getInstance().registerGroupAndContactListener();
//        registerBroadcastReceiver();
        tbIndicator.setVisibility(View.GONE);

    }

//    private void registerBroadcastReceiver() {
//        broadcastManager = LocalBroadcastManager.getInstance(this);
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
//        intentFilter.addAction(Constant.ACTION_GROUP_CHANAGED);
//        broadcastReceiver = new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                updateUnreadLabel();
//                updateUnreadAddressLable();
//                if (viewPager.getCurrentItem() == 2) {
//                    // 当前页面如果为聊天历史页面，刷新此页面
//                    if (mConversationFragment != null) {
//                        mConversationFragment.refresh();
//                    }
//                } else if (viewPager.getCurrentItem() == 1) {
//                    if (mContactFragment != null) {
//                        mContactFragment.refresh();
//                    }
//                }
//                String action = intent.getAction();
//                if (action.equals(Constant.ACTION_GROUP_CHANAGED)) {
//                    if (EaseCommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
//                        GroupsActivity.instance.onResume();
//                    }
//                }
//            }
//        };
//        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
//    }

    private void updateAvatar(UserInfo info) {
        String imageURL = info.getImageURL();
        if (!TextUtils.isEmpty(imageURL)) {
//            imAvatar.setImageURI(Uri.parse(imageURL));
            ImageLoader.getInstance().displayImage(imageURL, imAvatar,EPApplication.getInstance().getDisplayImageOptions());
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.navRelativeAcount:
                startActivity(new Intent(this, RelativeAccountActivity.class));
                break;
            case R.id.navSetting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.navLogout:
                UIUtils.showCommonDialog(this, getString(R.string.logout), "退出后将无法收到来自你好理工的消息!", new DialogButtonClickListner() {
                    @Override
                    public void onclick(int index) {
                        logOut();
                    }
                }, null);

                break;
            default:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void logOut() {


        final EPProgressDialog dialog = new EPProgressDialog();
        CacheService.getInstance(this).cleanCache();
        getSharedPreferences(com.emptypointer.hellocdut.utils.Constant.SHARED_PERFERENCR_CATCH, MODE_PRIVATE).edit().clear();
        getSharedPreferences(com.emptypointer.hellocdut.utils.Constant.SHARED_PERFERENCR_SETTING, MODE_PRIVATE).edit().clear();
        dialog.show(getSupportFragmentManager(), null);
        UserInfo.getInstance(MainActivity.this).setToken("");
        dialog.dismiss();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
//        EMHelper.getInstance().logout(false, new EMCallBack() {
//            @Override
//            public void onSuccess() {
//                UserInfo.getInstance(MainActivity.this).setToken("");
//                dialog.dismiss();
//                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intent);
//                MainActivity.this.finish();
//            }
//
//            @Override
//            public void onError(int i, String s) {
//                UIUtils.makeSnake(MainActivity.this, getString(R.string.str_format_erro, s));
//                dialog.dismiss();
//            }
//
//            @Override
//            public void onProgress(int i, String s) {
//
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imAvatar:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
        }
    }


//    private void refreshUIWithMessage() {
//        runOnUiThread(new Runnable() {
//            public void run() {
//                // 刷新bottom bar消息未读数
//                updateUnreadLabel();
//                if (viewPager.getCurrentItem() == 3) {
//                    // 当前页面如果为聊天历史页面，刷新此页面
//                    if (mConversationFragment != null) {
//                        mConversationFragment.refresh();
//                    }
//                }
//            }
//        });
//    }


    /***
     * 好友变化listener
     *
     */
//    public class EPEMContactListener implements EMContactListener {
//
//        @Override
//        public void onContactAdded(List<String> usernameList) {
//            // 保存增加的联系人
//            Map<String, EaseUser> localUsers = EMHelper.getInstance().getContactList();
//            Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
//            for (String username : usernameList) {
//                EaseUser user = new EaseUser(username);
//                // 添加好友时可能会回调added方法两次
//                if (!localUsers.containsKey(username)) {
//                    mUserDao.saveContact(user);
//                }
//                toAddUsers.put(username, user);
//            }
//            localUsers.putAll(toAddUsers);
//            // 刷新ui
//            if (viewPager.getCurrentItem() == 1)
//                mContactFragment.refresh();
//
//        }
//
//        @Override
//        public void onContactDeleted(final List<String> usernameList) {
//            // 被删除
//            Map<String, EaseUser> localUsers = EMHelper.getInstance().getContactList();
//            for (String username : usernameList) {
//                localUsers.remove(username);
//                mUserDao.deleteContact(username);
//                mInviteMessgeDao.deleteMessage(username);
//            }
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    // 如果正在与此用户的聊天页面
//                    String st10 = getResources().getString(R.string.have_you_removed);
//                    if (ChatActivity.activityInstance != null
//                            && usernameList.contains(ChatActivity.activityInstance.getToChatUsername())) {
//                        UIUtils.makeSnake(MainActivity.this, ChatActivity.activityInstance.getToChatUsername() + st10);
//                        ChatActivity.activityInstance.finish();
//                    }
//                    updateUnreadLabel();
//                    // 刷新ui
//                    mContactFragment.refresh();
//                    mConversationFragment.refresh();
//                }
//            });
//
//        }
//
//        @Override
//        public void onContactInvited(String username, String reason) {
//
//            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
//            List<InviteMessage> msgs = mInviteMessgeDao.getMessagesList();
//
//            for (InviteMessage inviteMessage : msgs) {
//                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
//                    mInviteMessgeDao.deleteMessage(username);
//                }
//            }
//            // 自己封装的javabean
//            InviteMessage msg = new InviteMessage();
//            msg.setFrom(username);
//            msg.setTime(System.currentTimeMillis());
//            msg.setReason(reason);
//            Log.d(TAG, username + "请求加你为好友,reason: " + reason);
//            // 设置相应status
//            msg.setStatus(InviteMessage.InviteMesageStatus.BEINVITEED);
//            notifyNewIviteMessage(msg);
//
//        }
//
//        @Override
//        public void onContactAgreed(String username) {
//            List<InviteMessage> msgs = mInviteMessgeDao.getMessagesList();
//            for (InviteMessage inviteMessage : msgs) {
//                if (inviteMessage.getFrom().equals(username)) {
//                    return;
//                }
//            }
//            // 自己封装的javabean
//            InviteMessage msg = new InviteMessage();
//            msg.setFrom(username);
//            msg.setTime(System.currentTimeMillis());
//            Log.d(TAG, username + "同意了你的好友请求");
//            msg.setStatus(InviteMessage.InviteMesageStatus.BEAGREED);
//            notifyNewIviteMessage(msg);
//
//        }
//
//        @Override
//        public void onContactRefused(String username) {
//
//            // 参考同意，被邀请实现此功能,demo未实现
//            Log.d(username, username + "拒绝了你的好友请求");
//        }
//
//    }

    /**
     * 获取未读申请与通知消息
     *
     * @return
     */
//    public int getUnreadAddressCountTotal() {
//        int unreadAddressCountTotal = 0;
//        unreadAddressCountTotal = mInviteMessgeDao.getUnreadMessagesCount();
//        return unreadAddressCountTotal;
//    }

    /**
     * 刷新申请与通知消息数
     */
//    public void updateUnreadAddressLable() {
//        runOnUiThread(new Runnable() {
//            public void run() {
//                int count = getUnreadAddressCountTotal();
//                if (count > 0) {
//                    mPagerAdapter.getBageView(1).setText(String.valueOf(count));
//                    mPagerAdapter.getBageView(1).show();
//                } else {
//                    mPagerAdapter.getBageView(1).hide();
//                }
//            }
//        });
//
//    }

//    /**
//     * 保存提示新消息
//     *
//     * @param msg
//     */
//    private void notifyNewIviteMessage(InviteMessage msg) {
//        saveInviteMsg(msg);
//        // 提示有新消息
//        EMHelper.getInstance().getNotifier().viberateAndPlayTone(null);
//
//        // 刷新bottom bar消息未读数
//        updateUnreadAddressLable();
//        // 刷新好友页面ui
//        if (viewPager.getCurrentItem() == 1)
//            mContactFragment.refresh();
//    }

//    private void saveInviteMsg(InviteMessage msg) {
//        // 保存msg
//        mInviteMessgeDao.saveMessage(msg);
//        //保存未读数，这里没有精确计算
//        mInviteMessgeDao.saveUnreadMessageCount(1);
//    }




    @Override
    protected void onStop() {
//        EMChatManager.getInstance().unregisterEventListener(this);
        //   EMHelper sdkHelper = EMHelper.getInstance();
        // sdkHelper.popActivity(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        //updateAvatar(UserInfo.getInstance(this));
        // unregister this event listener when this activity enters the
        // background
//        updateUnreadLabel();
//        updateUnreadAddressLable();
        //    EMHelper sdkHelper = EMHelper.getInstance();
        //  sdkHelper.pushActivity(this);

        // register the event listener when enter the foreground
//        EMChatManager.getInstance().registerEventListener(this,
        //              new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage, EMNotifierEvent.Event.EventOfflineMessage, EMNotifierEvent.Event.EventConversationListChanged});
//        sdkHelper.notifyForRecevingEvents();
        super.onResume();
        CacheService cacheService = CacheService.getInstance(this);
        DataCache cache = cacheService.getDataCache(CACHE_SCHEDULED_TASK);
        long time = 0;
        if (cache != null) {
            time = Long.valueOf(cache.getDate());
        }
        long timediffer = System.currentTimeMillis() - time;
        Log.i(TAG, "timediffer==" + timediffer);
        if (timediffer > 86400000) {
            EPUpdateService.getInstance(this).cheakVersionBack();
            cacheService.setDataCache(CACHE_SCHEDULED_TASK,
                    String.valueOf(System.currentTimeMillis()));
            loadNotifyFromServer();
        }
        updateAvatar(UserInfo.getInstance(this));
    }

    private void loadNotifyFromServer() {

        final SharedPreferences preferences = getSharedPreferences(com.emptypointer.hellocdut.utils.Constant.SHARED_PERFERENCR_SETTING, Context.MODE_PRIVATE);
        final Long recentID = preferences.getLong(PRE_LAST_NOTIFY_ID, Long.MIN_VALUE);
        Map<String, String> params = new HashMap<>();
        params.put("action", "loginNotify");
        params.put("user_name", EPSecretService.encryptByPublic(UserInfo.getInstance(this).getUserName()));
        params.put("user_login_token", EPSecretService.encryptByPublic(UserInfo.getInstance(this).getToken()));
        EPJsonObjectRequest request = new EPJsonObjectRequest(this, params, false, new EPJsonResponseListener(this) {
            @Override
            public void onResponse(JSONObject response) {
                super.onResponse(response);
                if (!result) {
                    return;
                }
                final long id = response.getLong("id");
                if (id == recentID) {
                    return;
                }
                String content = response.getString("content");
                boolean hasURL = response.getBoolean("has_url");
                if (!hasURL) {
                    UIUtils.showCommonDialog(MainActivity.this, getString(R.string.prompt), content, null, null, "知道了", "");
                } else {
                    final String url = response.getString("return_url");
                    UIUtils.showCommonDialog(MainActivity.this, getString(R.string.prompt), content, new DialogButtonClickListner() {
                        @Override
                        public void onclick(int index) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        }
                    }, null, "了解详情", "知道了");
                }
            }
        });
        request.setTag(this);
        VolleyUtil.getQueue(this).add(request);
    }

    @Override
    protected void onDestroy() {
//        unregisterBroadcastReceiver();
        EPApplication.getInstance().removeActivity(this);
        VolleyUtil.getQueue(this).cancelAll(this);
        super.onDestroy();
    }

    private void unregisterBroadcastReceiver() {
//        broadcastManager.unregisterReceiver(broadcastReceiver);
    }
}
