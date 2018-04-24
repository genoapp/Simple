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

import android.content.Intent;

import ge.gnio.Packet;
import ge.gnio.exception.IncompatiblePacketException;
import ge.gnio.listener.PacketListener;
import ge.simple.app.CallActivity;
import ge.simple.server.Person;
import ge.simple.services.CallService;

public class CallRequest implements PacketListener<Person> {

    @SuppressWarnings("unused")
    private static final String TAG = CallRequest.class.getSimpleName();

    @Override
    public void readPacket(Person person, Packet packet) {
        try{
            String number = packet.readString();
            byte lvl = person.getApplicationContext().getCallService().call(number, CallService.CALL_REQUEST_IN);
            person.sendPacket(packet.clear().writeByte(lvl).flip());
            if(lvl == 1){
                Intent intent = new Intent(person.getApplicationContext(),CallActivity.class);
                intent.putExtra("number",number);
                intent.putExtra("request",CallService.CALL_REQUEST_IN);
                person.getApplicationContext().getCurrentActivity().startActivity(intent);
            }
        }catch (IncompatiblePacketException ignore){

        }
    }

}