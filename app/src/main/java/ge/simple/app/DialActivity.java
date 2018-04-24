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

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import ge.simple.R;
import ge.simple.app.parent.SimpleCompatActivity;
import ge.simple.services.CallService;

/**
 * Created by freya on 4/8/18.
 */

public class DialActivity extends SimpleCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = DialActivity.class.getSimpleName();
    TextView callNumber;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dial_activity);
        callNumber = findViewById(R.id.number_view);
        FloatingActionButton callButton = findViewById(R.id.call_button);

        callButton.setOnClickListener(v -> {
            String number = callNumber.getText().toString().trim();
            if(!number.isEmpty()){
                Intent intent = new Intent(getApplicationContext(),CallActivity.class);
                intent.putExtra("number",number);
                intent.putExtra("request", CallService.CALL_REQUEST_OUT);
                startActivity(intent);
            }
        });

        ImageButton backspace = findViewById(R.id.backspace);
        backspace.setOnClickListener((v)->{
           String number =  callNumber.getText().toString();
            if(!number.isEmpty()){
                callNumber.setText(number.substring(0,number.length() -1));
            }
        });

    }

    public void  onNumberClick(View v){
       if(v instanceof  Button){
           Button b = (Button) v;

           String display =callNumber.getText().toString();
           display +=b.getText().toString();
           callNumber.setText(display);
       }
    }
}
