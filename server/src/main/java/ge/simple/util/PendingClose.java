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

package ge.simple.util;

import ge.gnio.Packet;
import ge.gnio.Server;
import ge.simple.server.Person;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class PendingClose implements Runnable {

    Server<Person> server;

    public PendingClose(Server<Person> server) {
        this.server = server;
    }

    @Override
    public void run() {
       Packet packet = new Packet(-1).writeBoolean(true).flip();
        Stream<Person> stream = server.getClients();
        AtomicInteger closed = new AtomicInteger();
        Iterator<Person> people = stream.iterator();
        while(people.hasNext()){
            Person p = people.next();
            if(p.getLastReadTime() != 0) {
                if (p.getLastReadTime() + 60000 < Calendar.getInstance().getTimeInMillis()) {
                    p.close();
                    closed.getAndIncrement();
                } else if (p.getLastReadTime() + 45000 < Calendar.getInstance().getTimeInMillis()) {
                    p.sendPacket(packet);
                }
            }
        }
        System.out.println("PendingClose: "+ closed.get());
        server.runLater(this,10000);
    }
}
