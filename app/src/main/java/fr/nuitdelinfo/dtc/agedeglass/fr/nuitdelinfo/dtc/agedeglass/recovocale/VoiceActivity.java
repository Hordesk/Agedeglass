package fr.nuitdelinfo.dtc.agedeglass.fr.nuitdelinfo.dtc.agedeglass.recovocale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import fr.nuitdelinfo.dtc.agedeglass.R;
import fr.nuitdelinfo.dtc.barcodeeye.LaunchActivity;

public class VoiceActivity extends Activity {



    public static Context context;
    public static AudioManager mAudioManager;
    public static int mStreamVolume;

    private static Intent intent;
    public static SpeechRecognizer speechRecognizer;

    private static VoiceListener voiceListener;

    public static TextView principalText;
    public static TextView infoText;
    private static Activity voiceActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        initializePatternsRecognition();

        this.context = getApplicationContext();
        voiceActivity = this;


        voiceActivity.findViewById(R.id.popup).setVisibility(View.INVISIBLE);


        /*
         * Mise en place de police perso sur le txt
         */
        principalText = (TextView) findViewById(R.id.txtVoice);
        principalText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf"));


        infoText = (TextView) findViewById(R.id.notifText);
        infoText.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf"));



        initVoiceRecognizer();
    }

    private void initializePatternsRecognition() {
        VoiceGlobalVars.consultPatternsRecognition = new ArrayList<>();
        VoiceGlobalVars.addPatternsRecognition = new ArrayList<>();
        VoiceGlobalVars.removePatternsRecognition = new ArrayList<>();

        VoiceGlobalVars.consultPatternsRecognition.add(".*affich.*");;
        VoiceGlobalVars.consultPatternsRecognition.add(".*consult.*");
        VoiceGlobalVars.consultPatternsRecognition.add(".*donne.*");
        VoiceGlobalVars.consultPatternsRecognition.add(".*combien.*");

        VoiceGlobalVars.addPatternsRecognition.add(".*ajout.*");
        VoiceGlobalVars.addPatternsRecognition.add(".*stocker.*");  //Ne pas oublier qu'il peut gener au niveau des consignes.
        VoiceGlobalVars.addPatternsRecognition.add(".*mettre.*");
        VoiceGlobalVars.addPatternsRecognition.add(".*inser.*");

        VoiceGlobalVars.removePatternsRecognition.add(".*enlev.*");
        VoiceGlobalVars.removePatternsRecognition.add(".*retir.*");
        VoiceGlobalVars.removePatternsRecognition.add(".*supprim.*");
    }


    private void initVoiceRecognizer() {
        voiceListener = new VoiceListener();
        speechRecognizer = getSpeechRecognizer();
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 15);
        restartVoiceRecognizer();
    }

    public static void restartVoiceRecognizer() {
        speechRecognizer.startListening(intent);
    }

    public static SpeechRecognizer getSpeechRecognizer(){
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
            speechRecognizer.setRecognitionListener(voiceListener);
        }
        return speechRecognizer;
    }

    public static void sendVoiceResultToVoiceController(ArrayList<String> result){
        VoiceController voiceController = new VoiceController();
        voiceController.setActivity(voiceActivity);
        voiceController.beginVoiceRecognition(result, context);
    }


    public void startScan(Context context){
    }

}
