package com.securedemo.myapplication;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.HydraTransportConfig;
import com.anchorfree.sdk.NotificationConfig;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.TransportConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.anchorfree.vpnsdk.vpnservice.VPNState;
import com.northghost.caketube.CaketubeTransport;
import com.northghost.caketube.OpenVpnTransportConfig;
import com.securedemo.myapplication.connection.Sample_Connection;
import com.securedemo.myapplication.utils.Intent_Pass_interface;
import com.securedemo.myapplication.utils.Parameter_Class;
import com.securedemo.myapplication.utils.Prefrences;
import com.securedemo.myapplication.utils.Server_Interface;
import com.securedemo.myapplication.utils.TraficLimitResponse;
import com.securedemo.myapplication.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends Activity {

    private static final String CHANNEL_ID = "Server_Master";
    public static Intent_Pass_interface intent_pass_interface1;
    public static Activity context;
    static UnifiedSDK unifiedSDK;
    static String Server_Key = "";
    static String Server_Password = "";

    public static void checkkthis(Activity context1, Intent_Pass_interface intent_pass_interface, boolean... doShowAds) {
        intent_pass_interface1 = intent_pass_interface;
        context = context1;

        Utils.country_List = Parameter_Class.countryLists;
        Prefrences.setCountry_list(Utils.country_List);

        // TODO :  ID PASS
        Prefrences.setServer_id(Parameter_Class.Server_Id);
        Prefrences.setServer_password(Parameter_Class.Server_password);


        // TODO : URL
        Prefrences.setUrl_type(Parameter_Class.url_Type);
        Prefrences.setUrl_default(Parameter_Class.Server_Url_Default);


        // TODO : SERVER CONNECTION
        Prefrences.setRendomserver(Parameter_Class.Server_random);
        Prefrences.setserver_Show(Parameter_Class.Server_Show);
        Prefrences.setdirect_connect(Parameter_Class.Server_Direct_Connect);


        // TODO : DEFAULT SERVER CONNECT
        Prefrences.set_server_short(Parameter_Class.Server_code);
        Prefrences.setserver_name(Parameter_Class.Server_name);
        Prefrences.setServer_image(Parameter_Class.Server_imageurl);


        Server_Connection();
    }

    public static void Server_Connection() {
        if (Prefrences.getserver_Show()) {
            Server_Initialize();
        } else {
            LoadAds();
        }
    }

    public static void Server_Initialize() {

        Server_Key = Prefrences.getServer_id();
        Server_Password = Prefrences.getServer_password();

        createNotificationChannel();

        ClientInfo clientInfo;

        if (Prefrences.getUrl_type()) {
            clientInfo = ClientInfo.newBuilder()
                    .addUrls(Parameter_Class.unknown_url_list)
                    .carrierId(Server_Key)
                    .build();
        } else {
            clientInfo = ClientInfo.newBuilder()
                    .addUrl(Prefrences.getUrl_default())
                    .carrierId(Server_Key)
                    .build();
        }

        List<TransportConfig> transportConfigList = new ArrayList<>();
        transportConfigList.add(HydraTransportConfig.create());
        transportConfigList.add(OpenVpnTransportConfig.tcp());
        transportConfigList.add(OpenVpnTransportConfig.udp());
        UnifiedSDK.update(transportConfigList, CompletableCallback.EMPTY);
        unifiedSDK = UnifiedSDK.getInstance(clientInfo);
        NotificationConfig notificationConfig = NotificationConfig.newBuilder()
                .title(context.getResources().getString(R.string.app_name))
                .channelId(CHANNEL_ID)
                .build();
        UnifiedSDK.update(notificationConfig);

        LoginToServer();
    }

    public static void LoginToServer() {
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSDK.getInstance().getBackend().login(authMethod, new com.anchorfree.vpnsdk.callbacks.Callback<User>() {
            @Override
            public void success(@NonNull User user) {
                Prefrences.setAura_user_id(user.getSubscriber().getId());
                LoginAPi_Token();
            }

            @Override
            public void failure(@NonNull VpnException e) {
                Prefrences.setserver_Show(false);
                LoadAds();
            }
        });

    }

    private static void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Server_Master";
            String description = "Server notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static void LoginAPi_Token() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-prod.northghost.com/partner/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Server_Interface apiInterface_local = retrofit.create(Server_Interface.class);
        Call<TraficLimitResponse> call = apiInterface_local.Call_Add_Trafic("login?login=" + Server_Key + "&password=" + Server_Password);
        call.enqueue(new Callback<TraficLimitResponse>() {
            @Override
            public void onResponse(Call<TraficLimitResponse> call, Response<TraficLimitResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().result.equals("OK")) {
                        Prefrences.setAccessToken(response.body().access_token);
                        IntentActivity();
                    } else {
                        IntentActivity();
                    }
                } else {
                    IntentActivity();
                }
            }

            @Override
            public void onFailure(Call<TraficLimitResponse> call, Throwable t) {
                IntentActivity();
            }
        });
    }

    private static void IntentActivity() {
        if (Prefrences.getserver_Show()) {
            if (Prefrences.getdirect_connect()) {
                AutoVNStart();
                return;
            }
        }
        LoadAds();
    }

    private static void AutoVNStart() {
        if (Prefrences.getRendomserver()) {
            Utils.setUpCountry();
        }
        ConnectVN();
    }

    private static void ConnectVN() {
        if (Prefrences.getisServerConnect()) {
            Parameter_Class.server_Start = true;
            status("connected");
        } else {
            prepareVpn();
        }
    }

    public static void status(String status) {
        if (status.equals("connect")) {
            Parameter_Class.server_Start = false;
            Prefrences.setisServerConnect(false);
        } else if (status.equals("connecting")) {
            Prefrences.setisServerConnect(false);
        } else if (status.equals("connected")) {
            Prefrences.setisServerConnect(true);
            LoadAds();
        }
    }

    private static void LoadAds() {

        if (Prefrences.getserver_Show()) {
            UnifiedSDK.getVpnState(new com.anchorfree.vpnsdk.callbacks.Callback<VPNState>() {
                @Override
                public void success(@NonNull VPNState vpnState) {
                    if (vpnState == VPNState.CONNECTED) {

                    } else {
                        Parameter_Class.server_Start = false;
                        Prefrences.setisServerConnect(false);
                    }

                    Pass_Activity();
                }

                @Override
                public void failure(@NonNull VpnException e) {
                    Parameter_Class.server_Start = false;
                    Prefrences.setisServerConnect(false);
                    Pass_Activity();

                }
            });
        } else {
            Pass_Activity();
        }
    }

    private static void prepareVpn() {
        if (!Parameter_Class.server_Start) {
            Utils.isConnectingToInternet(context, new Utils.OnCheckNet() {
                @Override
                public void OnCheckNet(boolean b) {
                    if (b) {

                        Intent intent = VpnService.prepare(context);
                        if (intent != null) {
                            context.startActivityForResult(intent, 1);
                        } else {
                            startServer();
                        }
                    } else {
                        intent_pass_interface1.onIntentpass(true);
                    }
                }
            });
        }
    }

    private static void startServer() {
        status("connecting");
        Server_Connecting();
    }

    public static void Server_Connecting() {

        isLoggedIn(new com.anchorfree.vpnsdk.callbacks.Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    List<String> fallbackOrder = new ArrayList<>();
                    fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                    fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
                    fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
                    //showConnectProgress();
                    List<String> bypassDomains = new LinkedList<>();
                    bypassDomains.add("*facebook.com");
                    bypassDomains.add("*wtfismyip.com");
                    UnifiedSDK.getInstance().getVPN().start(new SessionConfig.Builder()
                            .withReason(TrackingConstants.GprReasons.M_UI)
                            .withTransportFallback(fallbackOrder)
                            .withVirtualLocation(Prefrences.getServer_short().toLowerCase())
                            .withTransport(HydraTransport.TRANSPORT_ID)
                            .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains))
                            .build(), new CompletableCallback() {
                        @Override
                        public void complete() {
                            Log.d("MainActivity12", "complete");
                            Parameter_Class.server_Start = true;
                            status("connected");
                        }

                        @Override
                        public void error(@NonNull VpnException e) {
                            Log.d("MainActivity12", "error = " + e.getMessage());
                            status("connect");
                            Parameter_Class.server_Start = false;
                            if (e.getMessage().contains("TRAFFIC_EXCEED")) {
                                Set_Limit_size();
                            } else {
                                LoadAds();
                            }
                        }
                    });
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
                LoadAds();
            }
        });


    }

    public static void isLoggedIn(com.anchorfree.vpnsdk.callbacks.Callback<Boolean> callback) {
        UnifiedSDK.getInstance().getBackend().isLoggedIn(callback);
    }

    private static void Set_Limit_size() {
        int New_limit_traffic = 1000;
        long total_bytes = New_limit_traffic * 1048576;
        Delete_ApiCall(total_bytes);
    }

    private static void Delete_ApiCall(long total_bytes) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-prod.northghost.com/partner/subscribers/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Server_Interface mApiInterface = retrofit.create(Server_Interface.class);
        Call<TraficLimitResponse> call = mApiInterface.Call_Delete_Trafic(String.valueOf(Prefrences.getAura_user_id()) + "/traffic?access_token=" + Prefrences.getAccessToken());
        call.enqueue(new Callback<TraficLimitResponse>() {
            @Override
            public void onResponse(Call<TraficLimitResponse> call, Response<TraficLimitResponse> response) {
                if (response.isSuccessful()) {
                    Add_Trafic_size(total_bytes);
                } else {
                    LoadAds();
                }
            }

            @Override
            public void onFailure(Call<TraficLimitResponse> call, Throwable t) {
                LoadAds();
            }
        });
    }

    private static void Add_Trafic_size(long total_bytes) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-prod.northghost.com/partner/subscribers/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Server_Interface mApiInterface = retrofit.create(Server_Interface.class);
        Call<TraficLimitResponse> call = mApiInterface.Call_Add_Trafic(String.valueOf(Prefrences.getAura_user_id()) + "/traffic?access_token=" + Prefrences.getAccessToken() + "&traffic_limit=" + String.valueOf(total_bytes));
        call.enqueue(new Callback<TraficLimitResponse>() {
            @Override
            public void onResponse(Call<TraficLimitResponse> call, Response<TraficLimitResponse> response) {
                LoadAds();
            }

            @Override
            public void onFailure(Call<TraficLimitResponse> call, Throwable t) {
                LoadAds();
            }
        });
    }

    private static void Pass_Activity() {

        if (Prefrences.getisServerConnect()) {
            if (!Prefrences.getserver_Show()) {
                disconnectFromVnp();
            } else {
                //startActivity(new Intent(MainActivity.this, Start_Activity.class));
                //finish();
                intent_pass_interface1.onIntentpass(true);
            }
        } else {
            if (Prefrences.getserver_Show()) {
                Intent intent = new Intent(context, Sample_Connection.class);
                intent.putExtra("type_connection", "connection");
                context.startActivity(intent);
                context.finish();
            } else {
                if (Prefrences.getisServerConnect()) {
                    disconnectFromVnp();
                } else {
                    intent_pass_interface1.onIntentpass(true);
//                    startActivity(new Intent(MainActivity.this, Start_Activity.class));
//                    finish();
                }
            }
        }
    }

    public static void disconnectFromVnp() {
        UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                Parameter_Class.server_Start = false;
                Prefrences.setisServerConnect(false);

                intent_pass_interface1.onIntentpass(true);
//                startActivity(new Intent(MainActivity.this, Start_Activity.class));
//                finish();
            }

            @Override
            public void error(@NonNull VpnException e) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Utils.isConnectingToInternet(MainActivity.this, new Utils.OnCheckNet() {
                @Override
                public void OnCheckNet(boolean b) {
                    if (b) {
                        startServer();
                    } else {
                        intent_pass_interface1.onIntentpass(true);
                    }
                }
            });

        } else {
            intent_pass_interface1.onIntentpass(true);
//            if (!Preference.getComing_soon()) {
//                LoadNativeAds();
//            }
//            Ads_SplashAppOpen.Splash_OpenAppAds_Load( SplashActivity.this, new Ads_SplashAppOpen.OnFinishAds() {
//                @Override
//                public void onFinishAds(boolean b) {
//                    Main_Intent_Pass();
//                }
//            } );
//            Toast.makeText(Splash_Screen.this, "Permission Deny !! ", Toast.LENGTH_SHORT).show();
        }
    }

}