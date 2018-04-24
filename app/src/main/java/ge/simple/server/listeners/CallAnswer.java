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

package ge.simple.server.listeners;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import ge.gnio.Packet;
import ge.gnio.exception.IncompatiblePacketException;
import ge.gnio.listener.PacketListener;
import ge.simple.app.CallActivity;
import ge.simple.server.Person;


public class CallAnswer implements PacketListener<Person> {

    @SuppressWarnings("unused")
    private static final String TAG =CallAnswer.class.getSimpleName();
    @Override
    public void readPacket(Person person, Packet packet) {
        try{
            if(packet.readBoolean()){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(person.getApplicationContext()
                            .getCurrentActivity()
                            .checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        person.getApplicationContext().getCallService().answer();
                    }else{
                       end(person);
                    }
                }else{
                    person.getApplicationContext().getCallService().answer();
                }
            }else{
                end(person);
            }
        }catch (IncompatiblePacketException ignored){

        }
    }


    public void end(Person person){
        person.getApplicationContext().getCallService().end();
        if(person.getApplicationContext().getCurrentActivity() instanceof CallActivity){
            person.getApplicationContext().getCurrentActivity().finish();
        }
    }
}
