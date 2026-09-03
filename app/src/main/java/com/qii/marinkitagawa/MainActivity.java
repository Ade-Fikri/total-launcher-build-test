/*
 * Copyright (C) 2015 ChYK
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

package com.qii.marinkitagawa;

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
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private static final String pkgTotal = "com.ss.launcher2";

    /**
     * lowest required version of Total Launcher
     */
    private static final int minVersion = 10100;
  

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
      
          getWindow().setFlags(
                  android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                  android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
          );
      
          getWindow().getDecorView().setSystemUiVisibility(
                  View.SYSTEM_UI_FLAG_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                  | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                  | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
          );
      
          setContentView(R.layout.activity_main);
      
          Button applyButton = (Button) findViewById(R.id.applyButton);
      
          applyButton.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  applyTheme();
              }
          });
      }

  
    private void applyTheme() {
        boolean applicable;

        try {
            applicable = getPackageManager()
                    .getPackageInfo(pkgTotal, 0)
                    .versionCode >= minVersion;
        } catch (NameNotFoundException e) {
            applicable = false;
        }

        if (!applicable) {
            ApplyThemeDlgFragment df = new ApplyThemeDlgFragment();
            df.show(getFragmentManager(), "install_total_dlg");
            return;
        }

        Intent intent = new Intent(Constants.ACTION_APPLY_THEME);
        intent.setComponent(Constants.COMPONENT_NAME);
        intent.putExtra(
                Constants.EXTRA_THEME_PACKAGE,
                getPackageName()
        );
        intent.putExtra(
                Constants.EXTRA_THEME_ID,
                MyThemeProvider.THEME_IDS[0]
        );

        startActivityForResult(intent, R.string.apply);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        if (requestCode == R.string.apply
                && resultCode == Activity.RESULT_OK) {

            // when applied the theme successfully
            finish();

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(
                    ComponentName.unflattenFromString(
                            "com.ss.launcher2/.MainActivity"
                    )
            );
            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            );

            startActivity(intent);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Intent getMarketOpenIntent(String pname) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + pname));
        return intent;
    }

    ///////////////////////////////////////////////////////////////////////////////
    public static class ApplyThemeDlgFragment extends DialogFragment {

        @SuppressWarnings("deprecation")
        public ApplyThemeDlgFragment() {}

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder b = new AlertDialog.Builder(getActivity());

            b.setTitle(R.string.not_installed)
                    .setMessage(R.string.install_total);

            b.setPositiveButton(
                    android.R.string.yes,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(
                                DialogInterface dialog,
                                int which) {

                            Intent intent =
                                    getMarketOpenIntent(pkgTotal);

                            startActivity(intent);
                        }
                    }
            );

            b.setNegativeButton(
                    android.R.string.no,
                    null
            );

            return b.create();
        }
    }
}