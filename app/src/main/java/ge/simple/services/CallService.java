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

package ge.simple.services;

import android.annotation.TargetApi;
import android.media.*;
import android.os.Build;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.util.Log;
import ge.gnio.Packet;
import ge.simple.R;
import ge.simple.app.parent.SimpleApplication;

public class CallService{

    @SuppressWarnings("unused")
    private static final String TAG = CallService.class.getSimpleName();

    public static final int CALL_REQUEST_OUT = 0;
    public static final int CALL_REQUEST_IN = 1;

    private int currentRequest = -1;

    private final SimpleApplication context;

    private final MediaPlayer ringtonePlayer;
    private final MediaPlayer zoommerPlayer;
    private  AudioTrack input;
    private  AudioRecord output;

    private static final int SAMPLE_RATE = 8000;
    private static final int BUFFER_SIZE = 12000;


    private volatile boolean isIOStart = false;
    private boolean busy = false;


    private boolean ringtoneStart =false;
    private boolean zoommerStart = false;





    public CallService(SimpleApplication context) {
        this.context = context;

        this.ringtonePlayer = MediaPlayer.create(context, R.raw.ringtone);
        this.ringtonePlayer.setOnCompletionListener((m)->{
            if(ringtoneStart){
                m.start();
            }
        });
        this.zoommerPlayer = MediaPlayer.create(context,R.raw.zum);
        this.zoommerPlayer.setOnCompletionListener((m)->{
            if (zoommerStart){
                m.start();
            }
        });
    }

    private void startIO() {
        if(!isIOStart) {
            isIOStart = true;

            input = audioTrack();
            input.play();

            output = audioRecord();
            WriteLooper writeLooper  = new WriteLooper();
            context.getBackgroundHandler().post(writeLooper);
            output.startRecording();
            writeLooper.start();
        }
    }

    private void stopIO() {
        if(isIOStart) {
            isIOStart = false;

            input.pause();
            input.stop();
            input.release();
            input = null;

            output.stop();
            output.release();
            output = null;
        }
    }


    private AudioRecord audioRecord(){
       return new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
    }

    private AudioTrack audioTrack(){


       return new AudioTrack(new AudioAttributes.Builder()
                        .setLegacyStreamType(AudioManager.STREAM_VOICE_CALL)
                        .build(),
                new AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build(),
                BUFFER_SIZE,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE);

                  /* return new AudioTrack(AudioManager.STREAM_VOICE_CALL,
                    SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    BUFFER_SIZE, AudioTrack.MODE_STREAM);*/
    }


    public void writeInput(ByteBuffer buffer){
        if(isIOStart) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                input.write(buffer, buffer.limit(), AudioTrack.WRITE_NON_BLOCKING);
            } else {
                byte[] bytes = new byte[buffer.limit()];
                buffer.get(bytes);
                input.write(bytes, 0,bytes.length);
            }
            input.flush();
        }
    }


    public byte call(String number,int request) {
        currentRequest = request;
        byte lvl = -1;

        if(context.getPerson() != null && context.getPerson().isConnected()){
            if(!busy){
                if(request == CALL_REQUEST_OUT){
                    try{

                        lvl = context.getPerson().sendPacket(new Packet(20).writeString(number).flip(),
                                (person, packet) -> packet.readByte(),
                                5000);

                    }catch (NullPointerException ignored){

                    }

                    if(lvl == 1){
                        startZoommer();
                        busy = true;
                    }
                }
                else if(request == CALL_REQUEST_IN){
                    startRingtone();
                    busy = true;
                    lvl = 1;
                }
            }else{
                lvl = 0;
            }
        }

        return lvl;
    }

    public void end(){
        stopZoommer();
        stopRingtone();
        stopIO();

        if(context.getPerson() != null && context.getPerson().isConnected()){
            Packet packet = new Packet(30).writeBoolean(false).flip();
            context.getPerson().sendPacket(packet);
        }
        busy = false;
    }



    public void answer(){
        stopZoommer();
        stopRingtone();
        startIO();
        if(currentRequest == CALL_REQUEST_IN &&
                context.getPerson() != null &&
                context.getPerson().isConnected()){
            context.getPerson().sendPacket(new Packet(30).writeBoolean(true).flip());
        }
    }


    private  void stopZoommer(){
        if(zoommerStart) {
            zoommerStart = false;
            while (zoommerPlayer.isPlaying()) {
                zoommerPlayer.stop();
            }
        }
    }

    private  void stopRingtone(){
        if(ringtoneStart) {
            ringtoneStart = false;
            while (ringtonePlayer.isPlaying()) {
                ringtonePlayer.stop();
            }
        }
    }
    private void startZoommer(){
        try {
            zoommerPlayer.stop();
            zoommerPlayer.prepare();
            zoommerPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        zoommerStart = true;
    }

    private void startRingtone(){
        try {
            ringtonePlayer.stop();
            ringtonePlayer.prepare();
            ringtonePlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ringtoneStart = true;
    }


    private class WriteLooper implements Runnable{
        private  final Packet packet = new Packet(40);
        private  final byte[] recordBuffer = new byte[BUFFER_SIZE];
        private  boolean start = false;

        public void start(){
            start = true;
        }

        @Override
        public void run() {
            if(output != null && isIOStart && context.getPerson() != null && context.getPerson().isConnected()){
                if(start) {
                    output.read(recordBuffer, 0, BUFFER_SIZE);

                    packet.clear();
                    packet.write(recordBuffer);
                    packet.flip();

                    context.getPerson().sendPacket(packet);
                }
                context.getBackgroundHandler().post(this);
            }
        }
    }

/*    @SuppressWarnings("unused")
    private static byte[] compress(byte[] uncompressedData) {
        ByteArrayOutputStream bos = null;
        GZIPOutputStream gzipOS = null;
        try {
            bos = new ByteArrayOutputStream(uncompressedData.length);
            gzipOS = new GZIPOutputStream(bos);
            gzipOS.write(uncompressedData);
            gzipOS.close();
            return bos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                assert gzipOS != null;
                gzipOS.close();
                bos.close();
            }
            catch (Exception ignored) {
            }
        }
        return new byte[]{};
    }


    @SuppressWarnings("unused")
    private byte[] decompress(byte[] compressedData) {
        ByteArrayInputStream bis = null;
        ByteArrayOutputStream bos = null;
        GZIPInputStream gzipIS = null;

        try {
            bis = new ByteArrayInputStream(compressedData);
            bos = new ByteArrayOutputStream();
            gzipIS = new GZIPInputStream(bis);

            int len;
            while((len = gzipIS.read(gzDeBuffer)) != -1){
                bos.write(gzDeBuffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                assert gzipIS != null;
                gzipIS.close();
                bos.close();
                bis.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new byte[]{};
    }*/


}