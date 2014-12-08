package fr.nuitdelinfo.dtc.agedeglass.fr.nuitdelinfo.dtc.agedeglass.recovocale;

import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;


public class VoiceListener implements RecognitionListener {

    private static String TAG = "VoiceListener";

    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "onReadyForSpeech");
    }

    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
        if (!VoiceGlobalVars.okLunettes){

            VoiceActivity.principalText.setText("Dites OK Lunettes...");
        }
        else{
            VoiceActivity.principalText.setText("En écoute...");

        }


    }

    public void onRmsChanged(float rmsdB) {
    }

    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG, "onBufferReceived");
    }

    public void onEndOfSpeech() {
        Log.d(TAG, "onEndofSpeech");
    }

    public void onError(int error) {
        Log.d(TAG, "error " + error);
        VoiceActivity.getSpeechRecognizer().cancel();
        VoiceActivity.restartVoiceRecognizer();

        if (VoiceGlobalVars.okLunettes) {
            VoiceActivity.principalText.setText("Je n'ai pas compris");
        }

    }

    public void onResults(Bundle results) {
        Log.d(TAG, "onResults " + results);
        ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        for (int i = 0; i < data.size(); i++) {
            Log.d(TAG, "result " + data.get(i));
        }
        VoiceActivity.principalText.setText("Enoncez la requête");

        Log.d(TAG, "results: " + String.valueOf(data.size()));
        VoiceActivity.sendVoiceResultToVoiceController(data);
        VoiceActivity.getSpeechRecognizer().cancel();
        VoiceActivity.restartVoiceRecognizer();
    }

    public void onPartialResults(Bundle partialResults) {
        Log.d(TAG, "onPartialResults");
    }

    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "onEvent " + eventType);
    }
}