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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.widget.ListView;

import ge.simple.R;
import ge.simple.app.parent.SimpleCompatActivity;
import ge.simple.services.CallService;
import ge.simple.util.NumbersListAdapter;

public class MainActivity extends SimpleCompatActivity {

    NumbersListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        ListView listNumbers = findViewById(R.id.list_numbers);

        //start and stop method implemented this activity
        adapter = new NumbersListAdapter(getApplicationContext());

        listNumbers.setAdapter(adapter);

        listNumbers.setOnItemClickListener((parent, view, position, id) -> {
            if(parent.getAdapter() instanceof NumbersListAdapter) {
                NumbersListAdapter adapter = (NumbersListAdapter) parent.getAdapter();

                Intent intent = new Intent(getApplicationContext(),CallActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("number", adapter.getItem(position));
                intent.putExtra("request", CallService.CALL_REQUEST_OUT);
                startActivity(intent);
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((v)->{
            Intent intent = new Intent(this,DialActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.start();
    }


    @Override
    protected void onStop() {
        super.onStop();
        adapter.stop();
    }
}
