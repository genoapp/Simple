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

package ge.simple.app.parent;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import ge.gnio.Packet;
import ge.gnio.Server;
import ge.simple.server.Person;
import ge.simple.server.listeners.CallAnswer;
import ge.simple.server.listeners.CallRequest;
import ge.simple.server.listeners.FreeListener;
import ge.simple.server.listeners.InputBuffer;
import ge.simple.services.CallService;
import ge.simple.util.DoBackground;

/**
 * Created by freya on 4/8/18.
 */

public class SimpleApplication extends Application {

    private static final String TAG = SimpleApplication.class.getSimpleName();

    private SimpleCompatActivity currentActivity;
    private Person person = null;
    private Handler backgroundHandler;
    private Handler mainHandler;
    private Server<Person> server;


    private CallService callService;


    @Override
    public void onCreate() {
        super.onCreate();



        try {
            server = new Server<>(Person.class, 10 * 1024, Executors.newFixedThreadPool(1), null);
            server.addPacketListener(25, new CallRequest());
            server.addPacketListener(30, new CallAnswer());
            server.addPacketListener(40, new InputBuffer());
            server.addPacketListener(-1, new FreeListener());


            DoBackground background = new DoBackground();
            backgroundHandler = new Handler(background.getLooper());
            mainHandler = new Handler(Looper.getMainLooper());

            callService = new CallService(this);


            //connection  manager
            backgroundHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (person == null || !person.isConnected()) {
                        try {
                            person = server.connect(new InetSocketAddress("85.114.245.107", 9090));
                            person.setApplicationContext(SimpleApplication.this);
                            person.sendPacket(new Packet(10).writeString(person.getNumber()).flip());
                        } catch (IOException e) {
                            Log.i(TAG, e.getMessage());
                        }
                    }
                    backgroundHandler.postDelayed(this, 1000);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Handler getBackgroundHandler() {
        return backgroundHandler;
    }

    public Handler getMainHandler() {
        return mainHandler;
    }

    public void setCurrentActivity(SimpleCompatActivity currentActivity) {
        this.currentActivity = currentActivity;
    }


    public SimpleCompatActivity getCurrentActivity() {
        return currentActivity;
    }


    @Override
    public void onTerminate() {
        if (server != null) {
            server.close();
            Log.i(TAG, "App terminated");
        }
        super.onTerminate();
    }

    public Person getPerson() {
        return person;
    }

    public CallService getCallService() {
        return callService;
    }
}
