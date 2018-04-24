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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ge.gnio.Packet;
import ge.gnio.exception.IncompatiblePacketException;
import ge.gnio.listener.CallablePacketListener;
import ge.simple.R;
import ge.simple.app.parent.SimpleApplication;
import ge.simple.server.Person;

public class NumbersListAdapter extends BaseAdapter {

    private SimpleApplication context;
    private List<String> list;
    private Runnable updateRunnable;
    private boolean run = false;

    public NumbersListAdapter(SimpleApplication context) {
        this.context = context;
        this.list = new ArrayList<>();
        this.updateRunnable = new UpdateDoBackground();
    }

    private void update(){
        if(context.getPerson() != null && context.getPerson().isConnected()) {
            List<String> list = context.getPerson().sendPacket(new Packet(5).writeBoolean(true).flip(), new ReceiverListNumbers(), 5000);
            if (list != null) {
                this.list = list;
                notifyDataSetInvalidated();
            }
        }
    }

    private class UpdateDoBackground implements Runnable{

        @Override
        public void run() {
            update();
            if(run) {
                context.getMainHandler().postDelayed(this, 15000);
            }
        }
    }

    public void start(){
        run = true;
        context.getMainHandler().post(updateRunnable);
    }

    public void stop(){
        run = false;
        context.getBackgroundHandler().removeCallbacks(updateRunnable);
    }

    private class ReceiverListNumbers implements CallablePacketListener<Person,List<String>>{

        @Override
        public List<String> readPacket(Person person, Packet packet) {
            List<String> lists = null;
            try{
                int length = packet.readInt();
                String[] numbers = new String[length];
                for(int i = 0; i < length;i++){
                    numbers[i] = Integer.toString(packet.readInt());
                }
                lists = Arrays.asList(numbers);
            }catch (IncompatiblePacketException ignored){
                //ignored
            }
            return lists;
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_number,parent,false);
        }

        TextView numberView = convertView.findViewById(R.id.number_view);
        numberView.setText(getItem(position));
        return convertView;
    }
}
