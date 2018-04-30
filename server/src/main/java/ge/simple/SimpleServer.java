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

package ge.simple;

import ge.gnio.Server;
import ge.simple.server.Filter;
import ge.simple.server.Person;
import ge.simple.server.listener.*;
import ge.simple.server.PendingClose;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class SimpleServer {


    public static void main(String[] args) throws IOException {
        Server<Person> server = new Server<>(Person.class, 13000,Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()),10,new Filter());

        server.addPacketListener(5,new NumberList());
        server.addPacketListener(10,new InitialService());
        server.addPacketListener(20,new CallRequest());
        server.addPacketListener(30,new CallAnswer());
        server.addPacketListener(40,new SpeakBufferListener());

        server.runLater(new PendingClose(server));

        server.open(new InetSocketAddress(9090));
        System.out.println("server start");
    }
}
