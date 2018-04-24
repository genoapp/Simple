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

package ge.simple.server;


import java.nio.channels.SelectionKey;
import java.util.Random;

import ge.gnio.AbstractClient;
import ge.gnio.Server;
import ge.simple.app.parent.SimpleApplication;

/**
 * Created by freya on 4/8/18.
 */

public class Person extends AbstractClient<Person> {

    private SimpleApplication applicationContext;

    private String number;

    public Person(Server<Person> server, SelectionKey selectionKey) {
        super(server, selectionKey);
        number = Integer.toString(new Random().nextInt()).substring(1);

    }

    public void setApplicationContext(SimpleApplication applicationContext) {
        this.applicationContext = applicationContext;
    }

    public String getNumber() {
        return number;
    }

    public SimpleApplication getApplicationContext() {
        return applicationContext;
    }
}
