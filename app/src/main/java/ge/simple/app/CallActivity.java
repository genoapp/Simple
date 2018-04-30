/*
 *    Copyright  2018 Geno Papashvili
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ge.simple.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ge.simple.R;
import ge.simple.app.parent.SimpleCompatActivity;
import ge.simple.util.SimplePermission;

import static ge.simple.services.CallService.CALL_REQUEST_OUT;


public class CallActivity extends SimpleCompatActivity {


    @SuppressWarnings("unused")
    private static final String TAG = CallActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int result = checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (result == PackageManager.PERMISSION_GRANTED) {
                permissionRecordGranted();
            } else {
                requestPermissions(SimplePermission.PERMISSIONS, hashCode());
            }
        } else {
            permissionRecordGranted();
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == hashCode()){
            for (int i = 0; i < permissions.length; i++) {
                if(permissions[i].equals(Manifest.permission.RECORD_AUDIO)){
                    if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        permissionRecordGranted();
                    }else{
                        getApplicationContext().getCallService().end();
                        finish();
                    }
                }
            }
        }
    }

    private void permissionRecordGranted() {

        setContentView(R.layout.call_activity);

        String number = getIntent().getStringExtra("number");

        int request = getIntent().getIntExtra("request", CALL_REQUEST_OUT);

        TextView numberView = findViewById(R.id.number_view);
        numberView.setText(number);


        FloatingActionButton answerBut = findViewById(R.id.answer_button);
        FloatingActionButton cancelBut = findViewById(R.id.cancel_button);

        if (request == CALL_REQUEST_OUT) {
            answerBut.setVisibility(View.GONE);


            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cancelBut.getLayoutParams();

            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            cancelBut.setLayoutParams(params);


            getApplicationContext().getBackgroundHandler().post(()->{
                byte lvl = getApplicationContext().getCallService().call(number, request);

                String message = "";
                switch (lvl) {
                    case -1:
                        message = "Client is offline";
                        break;
                    case 0:
                        message = "Client is busy";
                        break;
                }
                if (lvl != 1) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        } else {
            RelativeLayout.LayoutParams aParams = (RelativeLayout.LayoutParams) answerBut.getLayoutParams();

            aParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            aParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            answerBut.setLayoutParams(aParams);

            RelativeLayout.LayoutParams cParams = (RelativeLayout.LayoutParams) cancelBut.getLayoutParams();

            cParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            cParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            cancelBut.setLayoutParams(cParams);
        }

        cancelBut.setOnClickListener(v -> {
            getApplicationContext().getCallService().end();
            finish();
        });
        answerBut.setOnClickListener(v-> getApplicationContext().getCallService().answer());
    }

}