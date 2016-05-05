package com.example.wifisample;

import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	TextView titleText;
	TextView mainText;
	WifiManager mainWifi;
	WifiReceiver receiverWifi;
	List<ScanResult> wifiList;
	StringBuilder sb = new StringBuilder();
	Context context = this;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		titleText = (TextView) findViewById(R.id.title);
		mainText = (TextView) findViewById(R.id.mainText);
		mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		receiverWifi = new WifiReceiver();
		registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		if (!mainWifi.isWifiEnabled()) {
			mainText.setText("isWifiEnabled failed\n");
			return;
		}

		// 지금접속된거 뿌려주고
		WifiInfo wifiInfo = mainWifi.getConnectionInfo();
		String connectionInfo = wifiInfo.getSSID() + " // " + wifiInfo.getNetworkId() + " // " + wifiInfo.getBSSID();
		titleText.setText(connectionInfo);

		mainWifi.startScan();
		mainText.setText("\nStarting Scan...\n");

		findViewById(R.id.save).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				WifiConfiguration wifiConfig = new WifiConfiguration();
				// wifiConfig.SSID="test";
				// wifiConfig.BSSID = "00:0C:41:F5:B0:08";
				EditText ssid = (EditText) findViewById(R.id.ssid);
				EditText bssid = (EditText) findViewById(R.id.bssid);
				EditText pass = (EditText) findViewById(R.id.password);
				String password = "\"" + pass.getText() + "\"";
				wifiConfig.SSID = "\"" + ssid.getText() + "\"";
				wifiConfig.BSSID = bssid.getText() + "";
				wifiConfig.hiddenSSID = false;
				// wifiConfig.preSharedKey =password;
				// wifiConfig.priority = 1;

				wifiConfig.allowedKeyManagement.clear();
				wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

				wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

				wifiConfig.allowedPairwiseCiphers.clear();
				wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
				wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

				wifiConfig.allowedAuthAlgorithms.clear();
				wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);

				// Protocols
				wifiConfig.allowedProtocols.clear();
				wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
				wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

				wifiConfig.wepTxKeyIndex = 0;
				wifiConfig.wepKeys = new String[] { password, password, password, password };

				wifiConfig.status = WifiConfiguration.Status.ENABLED;
				WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				wifi.setWifiEnabled(true);
				int netId = wifi.addNetwork(wifiConfig);
				wifi.updateNetwork(wifiConfig);
				wifi.enableNetwork(netId, true);
				wifi.saveConfiguration();

				Log.d("ONCLICK", wifiConfig.SSID + "  " + wifiConfig.BSSID + "  " + password + "  " + netId);
			}
		});

		// findViewById(R.id.connectionbtn).setOnClickListener(new
		// OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// /////////
		// String ssid =
		// "\""+((EditText)findViewById(R.id.ssid)).getText()+"\"";
		// int ntid=0;
		// String n=null;
		// WifiConfiguration atwifi=null;
		// List<WifiConfiguration> a = mainWifi.getConfiguredNetworks();
		// for(int i =0 ; i < a.size();i++){
		// WifiConfiguration rr = a.get(i);
		// if(rr.SSID.equals(ssid)){
		// n+=rr.SSID+" // "+rr.BSSID+" // "+rr.networkId+"\n";
		// ntid++;
		// atwifi=rr;
		// }
		// Log.d("cnt",i+""+ntid);
		// }
		// WifiManager wifi = (WifiManager)
		// getSystemService(Context.WIFI_SERVICE);
		// wifi.enableNetwork(atwifi.networkId, true);
		// AndroidUtility.showMessage(context, "configredNetworks",
		// atwifi.wepTxKeyIndex+"  "+atwifi.wepKeys[atwifi.wepTxKeyIndex]);
		// }
		// });

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Refresh");
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		mainWifi.startScan();
		mainText.setText("Starting Scan");
		return super.onMenuItemSelected(featureId, item);
	}

	protected void onPause() {
		unregisterReceiver(receiverWifi);
		super.onPause();
	}

	protected void onResume() {
		registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		super.onResume();
	}

	boolean sw = true;
	boolean subsw = true;

	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {

			String ssid = "KDT-2";
			//
			// ScanResult atwifiinfo = null;
			sb = new StringBuilder();
			wifiList = mainWifi.getScanResults();
			for (int i = 0; i < wifiList.size(); i++) {
				ScanResult result = (wifiList.get(i));

				// if(result.SSID.equals(ssid)){
				sb.append(i + "\t : ");
				sb.append(result.toString());
				sb.append("\n\n");
				// atwifiinfo=result;
				// }

			}
			mainText.setText(sb);

			// WifiConfiguration newwifi = new WifiConfiguration();
			// newwifi.SSID="\""+atwifiinfo.SSID+"\"";
			// newwifi.BSSID=atwifiinfo.BSSID;
			// newwifi.wepTxKeyIndex=0;
			// newwifi.wepKeys=new String[]{"kdt01","kdt01","kdt01","kdt01"};
			// newwifi.status=WifiConfiguration.Status.CURRENT;
			// // newwifi.networkId=18;
			// mainWifi.setWifiEnabled(true);
			// int nt=mainWifi.addNetwork(newwifi);
			// newwifi.networkId=nt;
			// Log.d("NTID",newwifi.SSID+"nt"+nt);
			// mainWifi.enableNetwork(nt, true);
			// sw=false;
			//
			//
			//
			//
			// if(subsw || atwifiinfo!=null){
			//
			// String n=null;
			// List<WifiConfiguration> a = mainWifi.getConfiguredNetworks();
			// WifiConfiguration atwifi=null;
			// for(int i =0 ; i < a.size();i++){
			// WifiConfiguration rr = a.get(i);
			//
			//
			// Log.d("--- ",rr.SSID+" // "+rr.BSSID+" // "+rr.networkId+"  // "+rr.wepTxKeyIndex+"   "+rr.wepKeys[rr.wepTxKeyIndex]);
			// if(rr.SSID.equals("\""+ssid+"\"")){
			// // rr.SSID = atwifiinfo.SSID;
			// rr.BSSID = atwifiinfo.BSSID;
			// rr.wepTxKeyIndex = 0;
			// rr.wepKeys = newwifi.wepKeys;
			// mainWifi.setWifiEnabled(true);
			// mainWifi.enableNetwork(rr.networkId, true);
			// AndroidUtility.showMessage(context, "커넥션",
			// rr.SSID+"   "+rr.BSSID+"  "+rr.wepKeys[
			// rr.wepTxKeyIndex]+"    "+rr.status+"   "+rr.networkId);
			//
			// subsw=false;
			// }
			// }
			//
			// }

		}
	}
}
