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

package ge.simple.server;

import ge.gnio.AbstractClient;
import ge.gnio.Server;

import java.nio.channels.SelectionKey;
import java.util.Optional;
import java.util.stream.Stream;

public class Person extends AbstractClient<Person> {


    private String number;
    private Person target;

    public void checkInetAddress() {

    }



    public String getNumber(){
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setTarget(Person person) {
        this.target = person;
    }

    public Person getTarget() {
        return target;
    }


    public Optional<Person> findPersonByNumber(String number){
        Stream<Person> people = getServer().getClients();
        return people
                .filter(p -> p.getNumber() != null && p.getNumber().equals(number))
                .findAny();
    }

    @Override
    public void initial() {

    }
}
