package com.securedemo.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.securedemo.myapplication.connection.Sample_Connection;
import com.securedemo.myapplication.utils.CountryList;
import com.securedemo.myapplication.utils.Prefrences;
import com.securedemo.myapplication.utils.Server_Interface;
import com.securedemo.myapplication.utils.TraficLimitResponse;
import com.securedemo.myapplication.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import unified.vpn.sdk.AuthMethod;
import unified.vpn.sdk.ClientInfo;
import unified.vpn.sdk.CompletableCallback;
import unified.vpn.sdk.HydraTransport;
import unified.vpn.sdk.HydraTransportConfig;
import unified.vpn.sdk.OpenVpnTransport;
import unified.vpn.sdk.OpenVpnTransportConfig;
import unified.vpn.sdk.SdkNotificationConfig;
import unified.vpn.sdk.SessionConfig;
import unified.vpn.sdk.TrackingConstants;
import unified.vpn.sdk.TrafficRule;
import unified.vpn.sdk.TransportConfig;
import unified.vpn.sdk.UnifiedSdk;
import unified.vpn.sdk.User;
import unified.vpn.sdk.VpnException;
import unified.vpn.sdk.VpnState;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "Server_Master";
    UnifiedSdk unifiedSDK;
    String Server_Key = "";
    String Server_Password = "";
    List<String> unknown_url_list = new ArrayList<>();
    ArrayList<CountryList> countryLists =  new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unknown_url_list.add("https://d2isj403unfbyl.cloudfront.net");
        unknown_url_list.add("https://backend.northghost.com");
        unknown_url_list.add("https://api-stage.northghost.com");
        unknown_url_list.add("https://api-prod.northghost.com");
        unknown_url_list.add("https://api-dev.northghost.com");
        unknown_url_list.add("https://developer.aura.com");

        CountryList countryList =  new CountryList();
        countryList.setName("United States");
        countryList.setCode("US");
        countryList.setCuntryimages("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.amazon.in%2FGeneric-American-Large-Banner-3FT-13008750MG%2Fdp%2FB019Q0VVNO&psig=AOvVaw24Bb9v8opS7vtKyN6o9H53&ust=1676542686286000&source=images&cd=vfe&ved=2ahUKEwj837Orppf9AhWyQGwGHfO2A9YQjRx6BAgAEAo");

        countryLists.add(countryList);


        Prefrences.setServer_id("touchvpn");
        Prefrences.setServer_password("true");
        Prefrences.setRendomserver(true);
        Prefrences.setserver_Show(true);
        Prefrences.setdirect_connect(false);

        Prefrences.setUrl_type(true);
        Prefrences.setUrl_default("https://backend.northghost.com");

        Utils.country_List = countryLists;
        Prefrences.setCountry_list(Utils.country_List);

        Prefrences.set_server_policy("<html>\\r\\n\\r\\n<body>\\r\\n    <div>\\r\\n        <h3 style=\\\"font-family: monospace\\\">Privacy Policy</h3>\\r\\n    </div>\\r\\n    <div class=\\\"col-md-1 col-sm-1\\\"></div>\\r\\n    <div class=\\\"col-md-10 col-sm-10\\\">\\r\\n        <div class=\\\"text-inter\\\">\\r\\n            <p style=\\\"font-family: monospace\\\">When you use Our applications on Android, This Privacy Policy describes the information collected by us and how we use that information. Your privacy is important to us. Sometimes we need information to provide services that you request.\\r\\n            </p>\\r\\n\\r\\n            <h4 style=\\\"font-family: monospace\\\">Personal Information:</h4>\\r\\n            <p style=\\\"font-family: monospace\\\"><b>Take Photos and Videos:</b> This permission allows us to use your device&#8217;s camera to take photos / videos and turn ON/OFF Camera Flash.<br><br>\\r\\n                <b>Full network access:</b> This permission is used to access the device&#8217;s network for certain functions including receiving update notifications or accessing app classification labels.<br><br>\\r\\n                <b>Connect and disconnect from Wi-Fi:</b> This permission is used in settings and notification toolbar in order to connect and disconnect from Wi-Fi.<br><br>\\r\\n                <b>Read Google service configuration:</b> This information is used to acquire advertising ID. Provide users with better advertising service by using such anonymous ID.<br><br>\\r\\n                <b>Expand/collapse status bar:</b> This permission is used for the gesture feature of User System to expand and collapse the status bar.<br><br>\\r\\n                <b>Measure app storage space:</b> This permission is used to acquire the amount of storage space used by an application.<br><br>\\r\\n                <b>Modify system settings:</b> This permission is used in settings, in order to switch or adjust ringtone, vibration and brightness level of the screen.<br><br>\\r\\n                <b>Photos / Media Files:</b> Modify or delete the contents of your Storage.</p><br>\\r\\n            <p style=\\\"font-family: monospace\\\"> <b>You give us To avail services on this app, you are required to provide the following information for better experience.</b><br></br>\\r\\n\\r\\n                <b>(A). Age and Gender:</b> </br>For the purpose of delivery personalized and targeted ads through third party ad network providers if consented by the user.</br>\\r\\n                </br>\\r\\n\\r\\n                <b>All required information you provide will be protect by us, We do not share your personal information(Age & Gender) by anyone. </b></p>\\r\\n\\r\\n\\r\\n\\r\\n            <h4 style=\\\"font-family: monospace\\\">Non- Personal Information :</h4>\\r\\n            <p style=\\\"font-family: monospace\\\">We may collect and use non-personal information in the following circumstances. To have a better understanding in user&#8217;s behaviour, solve problems in products and services, improve our products, services and advertising, we may collect\\r\\n                non-personal information such as installed Other Applications name and package name, the data of instal.<br> We also collect unique device GCM token for Notification purpose. If non-personal information is combined with personal information,\\r\\n                We treat the combined information as personal information for the purposes of this Privacy Policy.<br>\\r\\n\\r\\n                <b>Information we get from your use of our services  :</b> We may collect information about the services that you use and how you use them, such as when you view and interact with our content. We may collect device-specific information.We\\r\\n                will not share that information with third parties. <br>\\r\\n\\r\\n                <b>Unique Application numbers :</b> Certain services include a unique application number. This number and information about your installation (for example, the operating system type and application version number) may be sent to us when\\r\\n                you install or uninstall that service or when that service periodically contacts our servers, such as for automatic updates.</p>\\r\\n\\r\\n            <h4 style=\\\"font-family: monospace\\\">Advertisement in App:</h4>\\r\\n            <p style=\\\"font-family: monospace\\\">We use Google Admob for advertisements in our Applications. There could be errors in the programming and sometime programming errors may cause unwanted side effects. </br>\\r\\n                </br>\\r\\n                <b>Ad Network Privacy Policy :</b>\\r\\n\\r\\n                </br>\\r\\n                <ul>\\r\\n                    <li><a style=\\\"font-family: monospace\\\" href=\\\"https://support.google.com/admob/answer/6128543?hl=en\\\" target=\\\"_blank\\\">ADMOB Privacy</a></br>\\r\\n                    </li>\\r\\n                </ul>\\r\\n\\r\\n                <p style=\\\"font-family: monospace\\\"> We are very concerned about safeguarding the confidentiality of your information Please be aware that no security measures that we take to protect your information is absolutely guaranteed to avoid unauthorized access or use of your Non-Personal\\r\\n                    Information which is impenetrable. We haven't any Intention to Copy or use Others Product use and Access in company&#8217;s Application.<br> We are occasionally update this privacy statement. When we do so, we will also revise the\\r\\n                    \\\"last modified\\\" date of the privacy statement.<br> If any query about this privacy policy do not use our service.<br></br>\\r\\n\\r\\n                    Thank You...</p>\\r\\n\\r\\n                <p style=\\\"font-family: monospace\\\"><b>Last Modified : 27-Dec-2021</b></p>\\r\\n\\r\\n\\r\\n\\r\\n        </div>\\r\\n\\r\\n    </div>\\r\\n    <div class=\\\"col-md-1 col-sm-1\\\" />\\r\\n\\r\\n\\r\\n\\r\\n    </div>\\r\\n\\r\\n</html>");

        Server_Connection();
    }

    public void Server_Connection() {
        if (Prefrences.getserver_Show()) {
            Server_Initialize();
        } else {
            LoadAds();
        }
    }


    public void Server_Initialize() {


        Server_Key = Prefrences.getServer_id();
        Server_Password = Prefrences.getServer_password();

        createNotificationChannel();

        ClientInfo clientInfo;

        if (Prefrences.getUrl_type()) {
            clientInfo = ClientInfo.newBuilder()
                    .addUrls(unknown_url_list)
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
        UnifiedSdk.update(transportConfigList, CompletableCallback.EMPTY);
        unifiedSDK = UnifiedSdk.getInstance(clientInfo);
        SdkNotificationConfig notificationConfig = SdkNotificationConfig.newBuilder()
                .title(getResources().getString(R.string.app_name))
                .channelId(CHANNEL_ID)
                .build();
        UnifiedSdk.update(notificationConfig);

        LoginToServer();
    }

    public void LoginToServer() {
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSdk.getInstance().getBackend().login(authMethod, new unified.vpn.sdk.Callback<User>() {
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Server_Master";
            String description = "Server notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void LoginAPi_Token() {
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

    private void IntentActivity() {
        if (Prefrences.getserver_Show()) {
            if (Prefrences.getdirect_connect()) {
                AutoVNStart();
                return;
            }
        }
        LoadAds();
    }

    private void AutoVNStart() {
        if (Prefrences.getRendomserver()) {
            Utils.setUpCountry();
        }
        ConnectVN();
    }

    private void ConnectVN() {
        if (Prefrences.getisServerConnect()) {
            Utils.server_Start = true;
            status("connected");
        } else {
            prepareVpn();
        }
    }

    public void status(String status) {
        if (status.equals("connect")) {
            Utils.server_Start = false;
            Prefrences.setisServerConnect(false);
        } else if (status.equals("connecting")) {
            Prefrences.setisServerConnect(false);
        } else if (status.equals("connected")) {
            Prefrences.setisServerConnect(true);
            LoadAds();
        }
    }


    private void LoadAds() {

        if (Prefrences.getserver_Show()) {
            UnifiedSdk.getVpnState(new unified.vpn.sdk.Callback<VpnState>() {
                @Override
                public void success(@NonNull VpnState vpnState) {
                    if (vpnState == VpnState.CONNECTED) {

                    } else {
                        Utils.server_Start = false;
                        Prefrences.setisServerConnect(false);
                    }

                    Pass_Activity();
                }

                @Override
                public void failure(@NonNull VpnException e) {
                    Utils.server_Start = false;
                    Prefrences.setisServerConnect(false);
                    Pass_Activity();

                }
            });
        } else {
            Pass_Activity();
        }
    }



    private void prepareVpn() {
        if (!Utils.server_Start) {
            Utils.isConnectingToInternet(MainActivity.this, new Utils.OnCheckNet() {
                @Override
                public void OnCheckNet(boolean b) {
                    if (b) {

                        Intent intent = VpnService.prepare(MainActivity.this);
                        if (intent != null) {
                            startActivityForResult(intent, 1);
                        } else {
                            startServer();
                        }
                    } else {
                        finishAffinity();
                    }
                }
            });
        }
    }

    private void startServer() {
        status("connecting");
        Server_Connecting();
    }

    public void Server_Connecting() {
        isLoggedIn(new unified.vpn.sdk.Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    List<String> fallbackOrder = new ArrayList<>();
                    fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                    fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_TCP);
                    fallbackOrder.add(OpenVpnTransport.TRANSPORT_ID_UDP);
                    List<String> bypassDomains = new LinkedList<>();
                    bypassDomains.add("*facebook.com");
                    bypassDomains.add("*wtfismyip.com");
                    UnifiedSdk.getInstance().getVpn().start(new SessionConfig.Builder()
                            .withReason(TrackingConstants.GprReasons.M_UI)
                            .withTransportFallback(fallbackOrder)
                            .withVirtualLocation(Prefrences.getServer_short().toLowerCase())
                            .withTransport(HydraTransport.TRANSPORT_ID)
                            .addDnsRule(TrafficRule.Builder.bypass().fromDomains(bypassDomains))
                            .build(), new CompletableCallback() {
                        @Override
                        public void complete() {
                            Utils.server_Start = true;
                            status("connected");
                        }

                        @Override
                        public void error(@NonNull VpnException e) {

                            status("connect");
                            Utils.server_Start = false;

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

    public void isLoggedIn(unified.vpn.sdk.Callback<Boolean> callback) {
        UnifiedSdk.getInstance().getBackend().isLoggedIn(callback);
    }

    private void Set_Limit_size() {
        int New_limit_traffic = 1000;
        long total_bytes = New_limit_traffic * 1048576;
        Delete_ApiCall(total_bytes);
    }

    private void Delete_ApiCall(long total_bytes) {
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

    private void Add_Trafic_size(long total_bytes) {

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

    private void Pass_Activity() {

        if (Prefrences.getisServerConnect()) {
            if (!Prefrences.getserver_Show()) {
                disconnectFromVnp();
            } else {
                startActivity(new Intent(MainActivity.this, Start_Activity.class));
                finish();
            }
        } else {
            if (Prefrences.getserver_Show()) {
                Intent intent = new Intent(MainActivity.this, Sample_Connection.class);
                intent.putExtra("type_connection", "connection");
                startActivity(intent);
                finish();
            } else {
                if (Prefrences.getisServerConnect()) {
                    disconnectFromVnp();
                } else {
                    startActivity(new Intent(MainActivity.this, Start_Activity.class));
                    finish();
                }
            }
        }
    }

    public void disconnectFromVnp() {
        UnifiedSdk.getInstance().getVpn().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                Utils.server_Start = false;
                Prefrences.setisServerConnect(false);

                startActivity(new Intent(MainActivity.this, Start_Activity.class));
                finish();
            }

            @Override
            public void error(@NonNull VpnException e) {
            }
        });
    }

}