package com.srinikethandev.rtcv2;

import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class VoIPServer {

    private static final String TAG = "VoIPServer";
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_STEREO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
    private static final int PORT = 5000;

    private ServerSocket serverSocket;
    private AudioTrack audioTrack;

    public void start() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    serverSocket = new ServerSocket(PORT);
                    while (!isCancelled()) {
                        Socket socket = serverSocket.accept();
                        new ClientThread(socket).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error accepting client connection: " + e.getMessage());
                }
                return null;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                try {
                    if (serverSocket != null) {
                        serverSocket.close();
                    }
                    if (audioTrack != null) {
                        audioTrack.stop();
                        audioTrack.release();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error closing server socket: " + e.getMessage());
                }
            }
        }.execute();
    }

    private class ClientThread extends AsyncTask<Void, Void, Void> {
        private final Socket socket;

        public ClientThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                audioTrack = new AudioTrack(AudioTrack.MODE_STREAM, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE, AudioTrack.MODE_STREAM);
                audioTrack.play();
                DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                byte[] buffer = new byte[BUFFER_SIZE];
                while (!isCancelled()) {
                    int bytesRead = inputStream.read(buffer, 0, BUFFER_SIZE);
                    audioTrack.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error receiving audio data: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            try {
                if (socket != null) {
                    socket.close();
                }
                if (audioTrack != null) {
                    audioTrack.stop();
                    audioTrack.release();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error closing client socket: " + e.getMessage());
            }
        }
    }
}

