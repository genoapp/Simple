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

package ge.simple.util;

import android.os.Looper;

import java.util.concurrent.CountDownLatch;

/**
 * Created by freya on 4/8/18.
 */

public class DoBackground {


   private Looper looper;

   private CountDownLatch latch = new CountDownLatch(1);

    public DoBackground(){
        Thread t = new Thread(this::run);
        t.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void run(){
        Looper.prepare();
        looper = Looper.myLooper();
        latch.countDown();
        Looper.loop();
    }

    public Looper getLooper() {
        return looper;
    }
}
