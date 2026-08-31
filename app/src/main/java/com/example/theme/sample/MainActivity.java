/*
 * Copyright (C) 2015 ChYK
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.theme.sample;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private static final String pkgTotal = "com.ss.launcher2";
	/**
	 * lowest required version of Total Launcher
	 */
	private static final int minVersion = 10100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.option_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menuApply) {
			boolean applicable;
			try {
				applicable = this.getPackageManager().getPackageInfo(pkgTotal, 0).versionCode >= minVersion;
			} catch (NameNotFoundException e) {
				applicable = false;
			}
			if (!applicable) {
				ApplyThemeDlgFragment df = new ApplyThemeDlgFragment();
				df.show(this.getFragmentManager(), "install_total_dlg");
				return true;
			}
			Intent intent = new Intent(Constants.ACTION_APPLY_THEME);
			intent.setComponent(Constants.COMPONENT_NAME);
			intent.putExtra(Constants.EXTRA_THEME_PACKAGE, this.getPackageName());
			intent.putExtra(Constants.EXTRA_THEME_ID, MyThemeProvider.THEME_IDS[0]);
			startActivityForResult(intent, R.string.apply);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == R.string.apply && resultCode == Activity.RESULT_OK) {
			// when applied the theme successfully
			this.finish();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setComponent(ComponentName.unflattenFromString("com.ss.launcher2/.MainActivity"));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			startActivity(intent);
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static Intent getMarketOpenIntent(String pname) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id="+pname));
		return intent;
	}
	
	///////////////////////////////////////////////////////////////////////////////
	public static class ApplyThemeDlgFragment extends DialogFragment {
		
		@SuppressWarnings("deprecation")
		public ApplyThemeDlgFragment() {}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
			b.setTitle(R.string.not_installed).setMessage(R.string.install_total);
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = getMarketOpenIntent(pkgTotal);
					startActivity(intent);
				}
			});
			b.setNegativeButton(android.R.string.no, null);
			return b.create();
		}
		
	}
	
}
