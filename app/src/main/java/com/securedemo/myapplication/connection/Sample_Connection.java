package com.securedemo.myapplication.connection;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.reporting.TrackingConstants;
import com.anchorfree.sdk.SessionConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.rules.TrafficRule;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.transporthydra.HydraTransport;
import com.bumptech.glide.Glide;
import com.northghost.caketube.CaketubeTransport;
import com.securedemo.myapplication.R;
import com.securedemo.myapplication.utils.Prefrences;
import com.securedemo.myapplication.utils.Server_Interface;
import com.securedemo.myapplication.utils.TraficLimitResponse;
import com.securedemo.myapplication.utils.Utils;
import com.tuyenmonkey.mkloader.MKLoader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Sample_Connection extends Activity {

    LinearLayout lnr_server_connect;
    ImageView iv_connection, iv_server_flag;

    TextView txt_server_status, tv_server_country;
    MKLoader mk_server_loader;

    LinearLayout rlt_disconnect_layout;
    RelativeLayout rlt_connect_layout;
    Server_Interface mApiInterface;

    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };

    public static String megabyteCount(long bytes) {
        return String.format(Locale.getDefault(), "%.0f", (double) bytes / 1024 / 1024);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_connection);


        lnr_server_connect = findViewById(R.id.lnr_server_connect);
        txt_server_status = findViewById(R.id.txt_server_status);
        mk_server_loader = findViewById(R.id.mk_server_loader);
        iv_connection = findViewById(R.id.iv_connection);
        iv_server_flag = findViewById(R.id.iv_server_flag);

        tv_server_country = findViewById(R.id.tv_server_country);
        rlt_disconnect_layout = findViewById(R.id.rlt_disconnect_layout);
        rlt_connect_layout = findViewById(R.id.rlt_connect_layout);


        if (Prefrences.getRendomserver()) {
            Utils.setUpCountry();
        }


        String type_connection = getIntent().getStringExtra("type_connection");
        if (Prefrences.getdirect_connect()) {

            if (type_connection.equals("connection")) {
                rlt_connect_layout.setVisibility(View.VISIBLE);
                rlt_disconnect_layout.setVisibility(View.GONE);
                if (isConnecting()) {
                    return;
                }
                if (txt_server_status.getText().toString().equals(getResources().getString(R.string.switch_off))) {
                    disconnectFromVnp(false);
                } else if (txt_server_status.getText().toString().equals(getResources().getString(R.string.switch_on))) {
                    prepareVpn();
                }

            } else {
                rlt_connect_layout.setVisibility(View.GONE);
                rlt_disconnect_layout.setVisibility(View.VISIBLE);
            }

        } else {
            rlt_connect_layout.setVisibility(View.GONE);
            rlt_disconnect_layout.setVisibility(View.VISIBLE);
        }

        Glide.with(Sample_Connection.this).load(Prefrences.getServer_image()).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).into(iv_server_flag);
        tv_server_country.setText(Prefrences.getserver_name());

        lnr_server_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnecting()) {
                    return;
                }
                if (txt_server_status.getText().toString().equals(getResources().getString(R.string.switch_off))) {
                    disconnectFromVnp(false);
                } else if (txt_server_status.getText().toString().equals(getResources().getString(R.string.switch_on))) {
                    prepareVpn();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Utils.isConnectingToInternet(Sample_Connection.this, new Utils.OnCheckNet() {
                @Override
                public void OnCheckNet(boolean b) {
                    if (b) {
                        startVpn();
                    } else {
                        finishAffinity();
                    }
                }
            });


        } else {
            Toast.makeText(Sample_Connection.this, "Permission Deny !! ", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isConnecting() {
        boolean isConnecting = false;

        if (txt_server_status.getText().toString().equals(Sample_Connection.this.getResources().getString(R.string.switch_off)) || txt_server_status.getText().toString().equals(Sample_Connection.this.getResources().getString(R.string.switch_on))) {
            isConnecting = false;
        } else {
            isConnecting = true;
            Toast.makeText(this, "Server connecting...", Toast.LENGTH_SHORT).show();
        }


        return isConnecting;
    }

    private void prepareVpn() {
        if (!Utils.server_Start) {
            Utils.isConnectingToInternet(Sample_Connection.this, new Utils.OnCheckNet() {
                @Override
                public void OnCheckNet(boolean b) {
                    if (b) {

                        Intent intent = VpnService.prepare(Sample_Connection.this);

                        if (intent != null) {
                            startActivityForResult(intent, 1);
                        } else {
                            startVpn();

                        }

                    } else {
                        finishAffinity();
                    }
                }
            });

        }
    }

    private void startVpn() {
        status("connecting");
        connectToVpn();
    }

    public void status(String status) {
        if (status.equals("connect")) {
            Utils.server_Start = false;
            Prefrences.setisServerConnect(false);
            txt_server_status.setText(getResources().getString(R.string.switch_on));
            mk_server_loader.setVisibility(View.GONE);
            iv_connection.setImageResource(R.drawable.ic_disonnect);

        } else if (status.equals("connecting")) {
            Prefrences.setisServerConnect(false);
            txt_server_status.setText("Connecting...\nPlease Wait!");
            mk_server_loader.setVisibility(View.VISIBLE);
        } else if (status.equals("connected")) {

            Prefrences.setisServerConnect(true);

            startActivity(new Intent(Sample_Connection.this, Privacy_Screen.class));
            finish();

            txt_server_status.setText(getResources().getString(R.string.switch_off));
            mk_server_loader.setVisibility(View.GONE);
            iv_connection.setImageResource(R.drawable.ic_connect);

        } else if (status.equals("tryDifferentServer")) {
            txt_server_status.setText("Try Different\nServer");
        } else if (status.equals("loading")) {
            txt_server_status.setText("Loading Server..");
        } else if (status.equals("invalidDevice")) {
            txt_server_status.setText("Invalid Device");
        } else if (status.equals("authenticationCheck")) {
            txt_server_status.setText("Authentication \n Checking...");
        }
    }

    public void isLoggedIn( com.anchorfree.vpnsdk.callbacks.Callback<Boolean> callback) {
        UnifiedSDK.getInstance().getBackend().isLoggedIn(callback);
    }

    public void connectToVpn() {
        isLoggedIn(new  com.anchorfree.vpnsdk.callbacks.Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    List<String> fallbackOrder = new ArrayList<>();
                    fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                    fallbackOrder.add(HydraTransport.TRANSPORT_ID);
                    fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_TCP);
                    fallbackOrder.add(CaketubeTransport.TRANSPORT_ID_UDP);
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

                            setStatus("CONNECTED");
                            startUIUpdateTask();
                        }

                        @Override
                        public void error(@NonNull VpnException e) {
                            setStatus("DISCONNECTED");
                            if (e.getMessage().contains("TRAFFIC_EXCEED")) {
                                startActivity(new Intent(Sample_Connection.this, Privacy_Screen.class));
                            } else if (e.getMessage().contains("Wrong state to call start")) {
                                Toast.makeText(Sample_Connection.this, "try again!", Toast.LENGTH_SHORT).show();
                                disconnectFromVnp(true);
                            }
                        }
                    });
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {
            }
        });
    }

    public void disconnectFromVnp(boolean isfromConnnecting) {
        UnifiedSDK.getInstance().getVPN().stop(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                status("connect");
                Utils.server_Start = false;
                Prefrences.setisServerConnect(false);
            }

            @Override
            public void error(@NonNull VpnException e) {

            }
        });
    }

    public void setStatus(String connectionState) {
        if (connectionState != null)
            switch (connectionState) {
                case "DISCONNECTED":
                    status("connect");
                    Utils.server_Start = false;
                    break;
                case "CONNECTED":
                    Utils.server_Start = true;
                    status("connected");
                    break;
                case "WAIT":
                    break;
                case "AUTH":
                    break;
                case "RECONNECTING":
                    status("connecting");
                    break;
                case "NONETWORK":
                    break;
            }

    }

    protected void startUIUpdateTask() {
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        mUIHandler.post(mUIUpdateRunnable);
    }

    private void checkRemainingTraffic() {
        UnifiedSDK.getInstance().getBackend().remainingTraffic(new com.anchorfree.vpnsdk.callbacks.Callback<RemainingTraffic>() {
            @Override
            public void success(RemainingTraffic remainingTraffic) {
                updateRemainingTraffic(remainingTraffic);
            }

            @Override
            public void failure(VpnException e) {
            }
        });
    }

    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        String trafficUsed = megabyteCount(remainingTrafficResponse.getTrafficUsed());
        String trafficLimit = megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";
        if (remainingTrafficResponse.getTrafficLimit() <= remainingTrafficResponse.getTrafficUsed()) {
            mUIHandler.removeCallbacks(mUIUpdateRunnable);
            Set_Limit_size();
        }
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

        mApiInterface = retrofit.create(Server_Interface.class);
        Call<TraficLimitResponse> call = mApiInterface.Call_Delete_Trafic(String.valueOf(Prefrences.getAura_user_id()) + "/traffic?access_token=" + Prefrences.getAccessToken());
        call.enqueue(new retrofit2.Callback<TraficLimitResponse>() {
            @Override
            public void onResponse(Call<TraficLimitResponse> call, Response<TraficLimitResponse> response) {
                if (response.isSuccessful()) {
                    Add_Trafic_size(total_bytes);
                } else {
                    Toast.makeText(Sample_Connection.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TraficLimitResponse> call, Throwable t) {
                Toast.makeText(Sample_Connection.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Add_Trafic_size(long total_bytes) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api-prod.northghost.com/partner/subscribers/")
                .addConverterFactory(GsonConverterFactory.create()) //Here we are using the GsonConverterFactory to directly convert json data to object
                .build();

        mApiInterface = retrofit.create(Server_Interface.class);
        Call<TraficLimitResponse> call = mApiInterface.Call_Add_Trafic(String.valueOf(Prefrences.getAura_user_id()) + "/traffic?access_token=" + Prefrences.getAccessToken() + "&traffic_limit=" + String.valueOf(total_bytes));
        call.enqueue(new retrofit2.Callback<TraficLimitResponse>() {
            @Override
            public void onResponse(Call<TraficLimitResponse> call, Response<TraficLimitResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Sample_Connection.this, "Please connect again vpn!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Sample_Connection.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TraficLimitResponse> call, Throwable t) {
                Toast.makeText(Sample_Connection.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {


        Glide.with(Sample_Connection.this).load(Prefrences.getServer_image()).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).into(iv_server_flag);
        tv_server_country.setText(Prefrences.getserver_name());

        checkRemainingTraffic();

        if (Prefrences.getisServerConnect()) {
            startUIUpdateTask();
            Prefrences.setisServerConnect(true);
            txt_server_status.setText(getResources().getString(R.string.switch_off));
            mk_server_loader.setVisibility(View.GONE);
            iv_connection.setImageResource(R.drawable.ic_connect);
        }

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}