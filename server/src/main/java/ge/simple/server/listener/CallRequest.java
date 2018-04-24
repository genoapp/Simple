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
import ge.gnio.listener.CallablePacketListener;
import ge.gnio.listener.PacketListener;
import ge.simple.server.Person;

import java.util.Optional;

public class CallRequest implements PacketListener<Person> {
    @Override
    public void readPacket(Person person, Packet packet) {
        try {
            String number = packet.readString();

            Optional<Person> target =  person.findPersonByNumber(number);


            byte lvl = -1;
            if(target.isPresent()){
                try {
                    lvl = target.get().sendPacket(new Packet(25).writeString(person.getNumber()).flip(), new CallRequestTarget(), 4000);
                }catch (NullPointerException ignore){
                    //ignore
                }

                if(lvl == 1){
                    person.setTarget(target.get());
                    target.get().setTarget(person);
                }
            }
            person.sendPacket(packet.clear().writeByte(lvl).flip());


        } catch (IncompatiblePacketException e) {
            person.checkInetAddress();
        }
    }

    class CallRequestTarget implements CallablePacketListener<Person,Byte>{

        @Override
        public Byte readPacket(Person person, Packet packet) {
            byte lvl = -1;
            try{
                lvl = packet.readByte();
            }catch (IncompatiblePacketException e){
                person.checkInetAddress();
            }
            return lvl;
        }
    }
}
