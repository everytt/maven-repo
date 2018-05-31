package com.evevry.tt.library;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.every.tt.RecentCallActivity;
import com.every.tt.SendingSmsActivity;
import com.every.tt.SmsChatActivity;
import com.every.tt.TtWebViewActivity;
import com.every.tt.callactivity.OutgoingCallActivity;
import com.every.tt.manager.BusManager;
import com.every.tt.manager.GsonManager;
import com.every.tt.manager.PreferenceManager;
import com.every.tt.manager.PushManager;
import com.every.tt.manager.RealmManager;
import com.every.tt.model.Call;
import com.every.tt.model.ChatRoom;
import com.every.tt.model.Sms;
import com.every.tt.model.User;
import com.every.tt.network.JavascriptInterface;
import com.every.tt.service.TtService;
import com.every.tt.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import io.realm.Realm;

public class GlobalT {
    public static final int REQUEST_GLOBAL_T_PERMISSION = 1241;

    public enum Nation {
        INDONESIA, PHILIPPINES, MYANMAR, VIETNAM
    }

    public static final String ACTION_CHECK_SERVICE = "com.every.tt.action_check_service";
    public static final String ACTION_START_SERVICE = "com.every.tt.action_start_service";
    public static final String ACTION_INSERT_CALL_LOG = "com.every.tt.action_insert_call_log";
    public static final String ACTION_INSERT_SMS_LOG = "com.every.tt.action_insert_sms_log";
    public static final String ACTION_UPDATE_SMS_LOG = "com.every.tt.action_update_sms_log";
    public static final String ACTION_REGISTER_RESPONSE = "com.every.tt.action_register_response";

    public static final String EXTRA_KEY_SMS_JSON = "com.every.tt.extra_key_sms_json";
    public static final String EXTRA_KEY_CALL_JSON = "com.every.tt.extra_key_call_json";

    public static final String EXTRA_KEY_USER_JSON = "com.every.tt.extra_key_user_json";
    public static final String EXTRA_KEY_SUCCESS = "com.every.tt.extra_key_success";
    public static final String EXTRA_KEY_ERROR = "com.every.tt.extra_key_error";

    public static final String IMSI_PREFIX_INNI = "510";
    public static final String IMSI_PREFIX_PHILL = "515";
    public static final String IMSI_PREFIX_MYANMAR = "414";

    private static final String FILE_NAME = "nation_data.json";
    private static final String PARAM_APP_VERSION = "app_version";

    private static GlobalT mInstance;

    private GlobalT.Nation mNation;

    private String mUrlServiceTerms = "";
    private String mUrlFindingPw = "";

    private String mUrlCharge = "";
    private String mUrlPromotion = "";
    private String mUrlSetting = "";
    private String mUrlBilling = "";

    private TimeZone mTimeZone = TimeZone.getTimeZone("UTC");
    private Locale mLocale = Locale.ENGLISH;

    private String[] mPublicKey = {};

    private String mCountryNumber = "";

    private int mServiceCreatedId = 199;
    private int mServiceCheckId = 101;

    private String mAppCode = "";

    private boolean mHasToUseFacebook = true;

    private String mIpAddress = "";
    private int mPort = 0;

    private boolean mHasToShowLog = false;
    private boolean mIsServiceTurnOn = false;
    private String mPackageName;
    private String mAppVersion;
    private String mTag = "Tt";
    private String mNationImsiPrefix;
    private ArrayList<String> mAgencyImsiPrefixes;

    private boolean mIsNoLongerUse = false;

    private static Context mContext;

    public static GlobalT getInstance() {
        if (mInstance == null) {
            mInstance = new GlobalT(mContext);
        }

        return mInstance;
    }

    private GlobalT(Context context) {
        if (context == null) {
            return;
        }
        String json = PreferenceManager.getInstance(context).getGlobalTSetting();
        if (TextUtils.isEmpty(json)) {
            return;
        }

        Data data = GsonManager.getGson().fromJson(json, Data.class);

        Log.e(getClass().getSimpleName(), "Saved DATA: " + GsonManager.getGson().toJson(data));
        mNation = data.mNation;
        mUrlServiceTerms = data.mUrlServiceTerms;
        mUrlFindingPw = data.mUrlFindingPw;

        mUrlCharge = data.mUrlCharge;
        mUrlPromotion = data.mUrlPromotion;
        mUrlSetting = data.mUrlSetting;
        mUrlBilling = data.mUrlBilling;

        mHasToShowLog = data.mHasToShowLog;
        mIsServiceTurnOn = data.mIsServiceTurnOn;

        mTimeZone = TimeZone.getTimeZone(data.mTimeZone);
        mLocale = Locale.ENGLISH;

        mPublicKey = data.mPublicKey;

        mCountryNumber = data.mCountryNumber;

        mServiceCreatedId = data.mServiceCreatedId;
        mServiceCheckId = data.mServiceCheckId;

        mAppCode = data.mAppCode;

        mHasToUseFacebook = data.mHasToUseFacebook;

        mIpAddress = data.mIpAddress;
        mPort = data.mPort;

        mTag = data.mTag;
        mPackageName = data.packageName;
        mAppVersion = data.appVersion;
        mNationImsiPrefix = data.nationImsiPrefix;
        mAgencyImsiPrefixes = data.agencyImsiPrefixes;
        mIsNoLongerUse = data.isNoLongerUse;
    }

    public static void init(Context context) {
        PushManager.createChannel(context);

        RealmManager.init(context);

        if (GlobalT.getInstance().hasToShowLog()) {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable e) {
                    RealmManager.newInstance().writeLog("[TT] Exception: " + Log.getStackTraceString(e));
                }
            });
        }
    }

    public static GlobalT with(Context context) {
        mContext = context;
        mInstance = new GlobalT(mContext);
        mInstance.mPackageName = context.getPackageName();
        mInstance.mAppVersion = Util.getAppVersion(context);
        return getInstance();
    }

    public static void onFcmMessageReceived(String data) {
        BusManager.post(new TtService.Request(TtService.Request.REQUEST_PARSE_FCM_MESSAGE, data));
    }

    public static void onTokenRefresh(Context context, String token) {
        PreferenceManager.getInstance(context).setRegistrationId(token);

        BusManager.post(new TtService.Request(TtService.Request.REQUEST_UPDATE_FCM_KEY));
    }

    public static boolean isFcmTokenEmpty(Context context) {
        return TextUtils.isEmpty(PreferenceManager.getInstance(context).getRegistrationId());
    }

    public static void saveFcmToken(Context context, String fcmToken) {
        PreferenceManager.getInstance(context).setRegistrationId(fcmToken);
    }

    public GlobalT nation(GlobalT.Nation nation) {
        mNation = nation;
        try {
            JSONObject nationDataObj = new JSONObject(loadJSONFromAsset(mContext));
            String dataObj = nationDataObj.getString(String.valueOf(mNation));
            GlobalT.NationData nationData = GsonManager.getGson().fromJson(dataObj, GlobalT.NationData.class);
            urlServiceTerms(nationData.urlServiceTerms);
            urlFindingPw(nationData.urlFindingPw);
            urlCharge(nationData.urlCharge);
            urlPromotion(nationData.urlPromotion);
            urlSetting(nationData.urlSetting);
            urlBilling(nationData.urlBilling);
            timeZone(TimeZone.getTimeZone(nationData.timeZone));
            countryNumber(nationData.countryNumber);
            appCode(nationData.appCode);
            ipAddress(nationData.ipAddress);
            port(nationData.port);
            nationImsiPrefix(nationData.nationImsiPrefix);
            agencyImsiPrefixes(nationData.agencyImsiPrefixes);

            int serviceId = Integer.valueOf(nationData.agencyImsiPrefixes.get(0));
            serviceCreatedId(serviceId);
            serviceCheckId(serviceId + 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public GlobalT.Nation nation() {
        return mNation;
    }

    public GlobalT tag(String tag) {
        mTag = tag;
        return this;
    }

    public String tag() {
        return mTag;
    }

    public String urlFindingPw() {
        return makeUrlWithAppVersion(mUrlFindingPw);
    }

    public GlobalT urlFindingPw(String url) {
        mUrlFindingPw = url;
        return this;
    }

    public String urlServiceTerms() {
        return makeUrlWithAppVersion(mUrlServiceTerms);
    }

    public GlobalT urlServiceTerms(String url) {
        mUrlServiceTerms = url;
        return this;
    }

    public String urlCharge() {
        return makeUrlWithAppVersion(mUrlCharge);
    }

    public GlobalT urlCharge(String url) {
        mUrlCharge = url;
        return this;
    }

    public String urlPromotion() {
        return makeUrlWithAppVersion(mUrlPromotion);
    }

    public GlobalT urlPromotion(String url) {
        mUrlPromotion = url;
        return this;
    }

    public String urlSetting() {
        return makeUrlWithAppVersion(mUrlSetting);
    }

    public GlobalT urlSetting(String url) {
        mUrlSetting = url;
        return this;
    }

    public GlobalT urlBilling(String url) {
        mUrlBilling = url;
        return this;
    }

    public String urlBilling() {
        return makeUrlWithAppVersion(mUrlBilling);
    }

    public boolean hasToShowLog() {
        return mHasToShowLog;
    }

    public GlobalT hasToShowLog(boolean hasToShowLog) {
        mHasToShowLog = hasToShowLog;
        return this;
    }

    public TimeZone timeZone() {
        return mTimeZone;
    }

    public GlobalT timeZone(TimeZone zone) {
        mTimeZone = zone;
        return this;
    }

    public Locale locale() {
        return mLocale;
    }

    public GlobalT locale(Locale locale) {
        mLocale = locale;
        return this;
    }

    public String[] publicKey() {
        return mPublicKey;
    }

    public GlobalT publicKey(String[] key) {
        mPublicKey = key;
        return this;
    }

    public String countryNumber() {
        return mCountryNumber;
    }

    public GlobalT countryNumber(String number) {
        mCountryNumber = number;
        return this;
    }

    public int serviceCreatedId() {
        return mServiceCreatedId;
    }

    private GlobalT serviceCreatedId(int id) {
        mServiceCreatedId = id;
        return this;
    }

    public int serviceCheckId() {
        return mServiceCheckId;
    }

    private GlobalT serviceCheckId(int id) {
        mServiceCheckId = id;
        return this;
    }

    public String appCode() {
        return mAppCode;
    }

    public GlobalT appCode(String code) {
        mAppCode = code;
        return this;
    }

    public GlobalT ipAddress(String ipAddress) {
        this.mIpAddress = ipAddress;
        return this;
    }

    public String ipAddress() {
        return mIpAddress;
    }

    public GlobalT port(int port) {
        mPort = port;
        return this;
    }

    public int port() {
        return mPort;
    }

    public GlobalT hasToUseFacebook(boolean hasToUse) {
        mHasToUseFacebook = hasToUse;
        return this;
    }

    public boolean hasToUseFacebook() {
        return mHasToUseFacebook;
    }

    public boolean isServiceTurnOn() {
        return mIsServiceTurnOn;
    }

    public void isServiceTurnOn(boolean isServiceTurnOn) {
        mIsServiceTurnOn = isServiceTurnOn;
    }

    public GlobalT nationImsiPrefix(String prefix) {
        mNationImsiPrefix = prefix;
        return this;
    }

    public String nationImsiPrefix() {
        return mNationImsiPrefix;
    }

    public GlobalT agencyImsiPrefixes(ArrayList<String> prefixList) {
        mAgencyImsiPrefixes = prefixList;
        return this;
    }

    public ArrayList<String> agencyImsiPrefixes() {
        return mAgencyImsiPrefixes;
    }

    public String packageName() {
        return mPackageName;
    }

    public String appVersion() {
        return mAppVersion;
    }

    public boolean isNoLongerUse() {
        return mIsNoLongerUse;
    }

    public void save(Activity withPermission) {
        if (withPermission != null) {
            save();

            requestPermissions(withPermission, Util.checkPermissions(withPermission));
        } else {
            save();
        }
    }

    public void save() {
        if (mContext != null) {
            GlobalT.Data data = new GlobalT.Data();
            data.mNation = mNation;
            data.mUrlServiceTerms = mUrlServiceTerms;
            data.mUrlFindingPw = mUrlFindingPw;

            data.mUrlCharge = mUrlCharge;
            data.mUrlPromotion = mUrlPromotion;
            data.mUrlSetting = mUrlSetting;
            data.mUrlBilling = mUrlBilling;

            data.mHasToShowLog = mHasToShowLog;
            data.mIsServiceTurnOn = mIsServiceTurnOn;

            data.mTimeZone = mTimeZone.getID();

            data.mPublicKey = mPublicKey;

            data.mCountryNumber = mCountryNumber;

            data.mServiceCreatedId = mServiceCreatedId;
            data.mServiceCheckId = mServiceCheckId;

            data.mAppCode = mAppCode;

            data.mHasToUseFacebook = mHasToUseFacebook;

            data.mIpAddress = mIpAddress;
            data.mPort = mPort;

            data.mTag = mTag;
            data.packageName = mPackageName;
            data.appVersion = mAppVersion;
            data.nationImsiPrefix = mNationImsiPrefix;
            data.agencyImsiPrefixes = mAgencyImsiPrefixes;
            data.isNoLongerUse = mIsNoLongerUse;

            PreferenceManager.getInstance(mContext).setGlobalTSetting(GsonManager.getGson().toJson(data));
        }
    }

    private boolean requestPermissions(Activity a, String[] permissionArray) {
        if (permissionArray != null && permissionArray.length > 0) {

            ActivityCompat.requestPermissions(a, permissionArray, REQUEST_GLOBAL_T_PERMISSION);
            return true;
        }
        return false;
    }

    public boolean isInitialized(GlobalT.Failure failure) {
        if (mContext == null || mNation == null) {
            if (failure != null) {
                failure.onFail("Error: GlobalT is not initialized.");
            }
            return false;
        }
        return true;
    }

    public void register(String uid) {
        if (isInitialized(null)) {
            BusManager.post(new TtService.Request(TtService.Request.REQUEST_REGISTER_UID, uid));
        }
    }

    public void start() {
        if (isInitialized(null)) {
            mIsNoLongerUse = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mContext.startForegroundService(new Intent(mContext, TtService.class));
            } else {
                mContext.startService(new Intent(mContext, TtService.class));
            }
        }
    }

    public void stop() {
        if (isInitialized(null)) {
            mIsNoLongerUse = true;
            mContext.stopService(new Intent(mContext, TtService.class));
        }
    }

    public String userSerial() {
        String userSerial = null;
        if (isInitialized(null)) {
            Realm realm = Realm.getDefaultInstance();
            User user = RealmManager.newInstance().loadUser(realm);
            userSerial = user != null ? user.getAppImsi() : null;
            realm.close();
        }
        return userSerial;
    }

    /**
     * phoneNumber 를 가진 상대방에게 전화를 걸 수 있는 Activity 를 실행합니다.
     *
     * @param context     context
     * @param phoneNumber 전화를 걸 상대방의 휴대폰 번호
     * @param failure     에러콜백
     */
    public static void call(@NonNull Context context, @NonNull String phoneNumber, @NonNull GlobalT.Failure failure) {
        if (GlobalT.getInstance().isInitialized(failure)) {
            Realm realm = Realm.getDefaultInstance();
            if (isUserValid(realm, failure)) {
                User user = RealmManager.newInstance().loadUser(realm);
                Intent intent = new Intent(context, OutgoingCallActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                        | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra(OutgoingCallActivity.EXTRA_KEY_PHONE_NUMBER, phoneNumber);
                intent.putExtra(OutgoingCallActivity.EXTRA_KEY_USER_ID, user.getId());
                intent.putExtra(OutgoingCallActivity.EXTRA_KEY_USER_SIM_IMSI, user.getLocalPhoneNumber().get(0).getSimImsi());
                context.startActivity(intent);
            }
            realm.close();
        }
    }

    /**
     * phoneNumber 를 가진 상대방에게 문자를 보낼 수 있는 Activity 를 실행합니다.
     *
     * @param context     context
     * @param phoneNumber 문자를 보낼 상대방의 휴대폰 번호
     * @param failure     에러콜백
     */
    public static void sendSms(@NonNull Context context, @NonNull String phoneNumber, @NonNull GlobalT.Failure failure) {
        if (GlobalT.getInstance().isInitialized(failure)) {
            Realm realm = Realm.getDefaultInstance();
            if (isUserValid(realm, failure)) {
                Intent intent = new Intent(context, SendingSmsActivity.class);
                intent.putExtra(SendingSmsActivity.EXTRA_KEY_NUMBER, phoneNumber);
                context.startActivity(intent);
            }
            realm.close();
        }
    }

    /**
     * phoneNumber 를 가진 상대방과 주고 받은 문자를 리스트를 볼 수 있는 Activity 를 실행합니다.
     *
     * @param context     context
     * @param phoneNumber 문자를 주고 받은 상대방의 휴대폰 번호
     * @param failure     에러콜백
     */
    public static void showChatRoom(@NonNull Context context, @NonNull String phoneNumber, @NonNull GlobalT.Failure failure) {
        if (GlobalT.getInstance().isInitialized(failure)) {
            Realm realm = Realm.getDefaultInstance();
            if (isUserValid(realm, failure)) {
                ChatRoom room = RealmManager.newInstance().findChatRoom(realm, phoneNumber);
                if (room != null) {
                    Intent intent = new Intent(context, SmsChatActivity.class);
                    intent.putExtra(SmsChatActivity.EXTRA_KEY_NUMBER, room.getPhoneNumber());
                    intent.putExtra(SmsChatActivity.EXTRA_KEY_FRIEND_NAME, room.getName());
                    intent.putExtra(SmsChatActivity.EXTRA_KEY_FRIEND_ID, room.getFid());

                    context.startActivity(intent);
                } else {
                    failure.onFail("Error: There is no chat room which has phone number.");
                }
            }
            realm.close();
        }
    }

    /**
     * 현재 대화하고 있는 모든 문자방을 보여주는 Activity 를 실행
     *
     * @param context context
     * @param failure 에러콜백
     */
    public static void showChatRoomList(@NonNull Context context, @NonNull GlobalT.Failure failure) {
        showTtActivity(context, TtWebViewActivity.VIEW_SMS, failure);
    }

    /**
     * 현재까지 기록된 통화 목록을 보여주는 Activity 를 실행
     *
     * @param context context
     * @param failure 에러콜백
     */
    public static void showRecentCallList(@NonNull Context context, @NonNull GlobalT.Failure failure) {
        if (GlobalT.getInstance().isInitialized(failure)) {
            Realm realm = Realm.getDefaultInstance();
            if (isUserRegistered(realm, failure)) {
                context.startActivity(new Intent(context, RecentCallActivity.class));
            }
            realm.close();
        }
    }

    /**
     * 상품을 선택할 수 있는 페이지(Promo page)를 보여주는 Activity 를 실행
     *
     * @param context context
     * @param failure 에러콜백
     */
    public static void showPromo(@NonNull Context context, @NonNull GlobalT.Failure failure) {
        showTtActivity(context, TtWebViewActivity.VIEW_PROMO, failure);
    }

    /**
     * 결제할 수 있는 페이지(TopUp page)를 보여주는 Activity 를 실행
     *
     * @param context context
     * @param failure 에러콜백
     */
    public static void showCharge(@NonNull Context context, @NonNull GlobalT.Failure failure) {
        showTtActivity(context, TtWebViewActivity.VIEW_CHARGE, failure);
    }

    /**
     * 설정 페이지(ETC)를 보여주는 Activity 를 실행
     *
     * @param context context
     * @param failure 에러콜백
     */
    public static void showEtc(@NonNull Context context, @NonNull GlobalT.Failure failure) {
        showTtActivity(context, TtWebViewActivity.VIEW_ETC, failure);
    }

    private static void showTtActivity(@NonNull Context context, int view, @NonNull GlobalT.Failure failure) {
        if (GlobalT.getInstance().isInitialized(failure)) {
            Realm realm = Realm.getDefaultInstance();
            if (isUserRegistered(realm, failure)) {
                Intent intent = new Intent(context, TtWebViewActivity.class);
                intent.putExtra(TtWebViewActivity.EXTRA_KEY_VIEW, view);
                context.startActivity(intent);
            }
            realm.close();
        }
    }

    /**
     * ACTION_CHECK_SERVICE 에 대한 일을 처리하는 메소드입니다.
     * <p>
     * 현재 서비스가 정상 작동하는지 체킹하며, 정상 작동하지 않을시 재시작합니다.
     *
     * @param context context
     * @param intent  Receiver 로 부터 받은 intent
     */
    public static void handleCheckingBroadcast(@NonNull Context context, @NonNull Intent intent) {
        if (GlobalT.ACTION_CHECK_SERVICE.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, TtService.class);
            serviceIntent.putExtra(TtService.EXTRA_KEY_ACTION, TtService.ACTION_CHECK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }

    /**
     * ACTION_START_SERVICE 에 대한 일을 처리하는 메소드입니다.
     * <p>
     * 사용자가 앱을 강제 종료 등을 했을 때 받을 수 있는 broadcast 로,
     * 서비스를 재시작해야합니다.
     *
     * @param context context
     * @param intent  Receiver 로 부터 받은 intent
     */
    public static void handleRestartBroadcast(@NonNull Context context, @NonNull Intent intent) {
        if (GlobalT.ACTION_START_SERVICE.equals(intent.getAction())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, TtService.class));
            } else {
                context.startService(new Intent(context, TtService.class));
            }
        }
    }

    /**
     * ACTION_INSERT_CALL_LOG 에 대한 데이터를 파싱해 Call 객체를 반환합니다.
     *
     * @param intent Receiver 로 부터 받은 intent
     * @return 데이터 파싱이 성공일 경우 Call 객체가, 실패할 경우 null 이 반환됩니다.
     */
    public static Call parseCallLog(@NonNull Intent intent) {
        if (GlobalT.ACTION_INSERT_CALL_LOG.equals(intent.getAction())) {
            String json = intent.getStringExtra(EXTRA_KEY_CALL_JSON);
            return GsonManager.getGson().fromJson(json, Call.class);
        }
        return null;
    }

    /**
     * ACTION_INSERT_SMS_LOG 에 대한 데이터를 파싱해 Sms 객체를 반환합니다.
     *
     * @param intent Receiver 로 부터 받은 intent
     * @return 데이터 파싱이 성공일 경우 Sms 객체가, 실패할 경우 null 이 반환됩니다.
     */
    public static Sms parseSmsLog(@NonNull Intent intent) {
        if (GlobalT.ACTION_INSERT_SMS_LOG.equals(intent.getAction())) {
            String json = intent.getStringExtra(EXTRA_KEY_SMS_JSON);
            return GsonManager.getGson().fromJson(json, Sms.class);
        }
        return null;
    }

    /**
     * ACTION_UPDATE_SMS_LOG 에 대한 데이터를 파싱해 Sms 객체를 반환합니다.
     * <p>
     * 문자를 전송 후, 전송 성공이나 실패 등 state 가 업데이트되었을 때 받을 수 있는 broadcast 입니다.
     *
     * @param intent Receiver 로 부터 받은 intent
     * @return 데이터 파싱이 성공일 경우 Sms 객체가, 실패할 경우 null 이 반환됩니다.
     */
    public static Sms parseUpdatedSmsLog(@NonNull Intent intent) {
        if (GlobalT.ACTION_UPDATE_SMS_LOG.equals(intent.getAction())) {
            String json = intent.getStringExtra(EXTRA_KEY_SMS_JSON);
            return GsonManager.getGson().fromJson(json, Sms.class);
        }
        return null;
    }

    /**
     * 저장되어 있는 User 의 정보 중 sim 과 관련된 정보를 업데이트 합니다.
     *
     * @param mobile  휴대폰 번호
     * @param imsi    sim 의 imsi
     * @param expiry  sim 의 expired time
     * @param failure 에러 콜백
     */
    public static void updateUserInfo(String mobile, String imsi, String expiry, @NonNull GlobalT.Failure failure) {
        if (GlobalT.getInstance().isInitialized(failure)) {
            Realm realm = Realm.getDefaultInstance();
            if (isUserRegistered(realm, failure)) {
                RealmManager.newInstance().updateUserPhoneNumber(mobile, imsi, expiry);
                BusManager.getInstance().post(new JavascriptInterface.UpdateUser(mobile, imsi, expiry));
            }
            realm.close();
        }
    }

    private static boolean isUserValid(Realm realm, @NonNull GlobalT.Failure failure) {
        User user = RealmManager.newInstance().loadUser(realm);
        if (!isUserRegistered(realm, failure)) {
            return false;
        } else if (TextUtils.isEmpty(user.getId()) || user.getLocalPhoneNumber().size() == 0) {
            failure.onFail("Error: User information is wrong, please re-register.");
            return false;
        }
        return true;
    }

    private static boolean isUserRegistered(Realm realm, @NonNull GlobalT.Failure failure) {
        User user = RealmManager.newInstance().loadUser(realm);
        if (user == null) {
            failure.onFail("Error: There is no User who is registered.");
            return false;
        }
        return true;
    }

    private String makeUrlWithAppVersion(String url) {
        String urlWithVersion = url;
        if (!TextUtils.isEmpty(urlWithVersion)) {
            if (urlWithVersion.contains("?")) {
                urlWithVersion += "&" + PARAM_APP_VERSION + "=" + appVersion();
            } else {
                urlWithVersion += "?" + PARAM_APP_VERSION + "=" + appVersion();
            }
        }

        return urlWithVersion;
    }

    private String loadJSONFromAsset(Context context) {
        String json = "{}";
        try {
            InputStream is = context.getAssets().open(FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ignored) {
        }
        return json;
    }

    private class Data {
        private GlobalT.Nation mNation;
        private String mUrlServiceTerms;
        private String mUrlFindingPw;

        private String mUrlCharge;
        private String mUrlPromotion;
        private String mUrlSetting;
        private String mUrlBilling;

        private boolean mHasToShowLog;
        private boolean mIsServiceTurnOn;

        private String mTimeZone;

        private String[] mPublicKey;

        private String mCountryNumber;

        private int mServiceCreatedId;
        private int mServiceCheckId;

        private String mAppCode;

        private boolean mHasToUseFacebook;

        private String mIpAddress;
        private int mPort;
        private String mTag;
        private String packageName;
        private String appVersion;
        private String nationImsiPrefix;
        private ArrayList<String> agencyImsiPrefixes;
        private boolean isNoLongerUse;
    }

    private class NationData {
        String urlServiceTerms;
        String urlFindingPw;
        String urlCharge;
        String urlPromotion;
        String urlSetting;
        String urlBilling;
        String timeZone;
        String countryNumber;
        String appCode;
        String ipAddress;
        int port;
        String nationImsiPrefix;
        ArrayList<String> agencyImsiPrefixes;
    }

    public interface Failure {
        void onFail(String error);
    }
}
