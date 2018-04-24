/*
 * Copyright 2018  Geno Papashvili
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ge.simple.server.listener;

import ge.gnio.Packet;
import ge.gnio.exception.IncompatiblePacketException;
import ge.gnio.listener.PacketListener;
import ge.simple.server.Person;

public class SpeakBufferListener implements PacketListener<Person> {
    @Override
    public void readPacket(Person person, Packet packet) {
        try{
            Person target = person.getTarget();
            if(target.isConnected()){
                target.sendPacket(packet);
            }
        }catch (IncompatiblePacketException e){
            person.checkInetAddress();
        }
    }
}
