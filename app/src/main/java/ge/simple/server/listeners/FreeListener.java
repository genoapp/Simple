package ge.simple.server.listeners;

import android.util.Log;

import ge.gnio.Packet;
import ge.gnio.listener.PacketListener;
import ge.simple.server.Person;

public class FreeListener implements PacketListener<Person> {

    private static final String TAG = FreeListener.class.getSimpleName();
    @Override
    public void readPacket(Person person, Packet packet) {
        person.sendPacket(packet);
        Log.v(TAG,"server update ");
    }
}
