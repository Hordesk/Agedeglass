package fr.nuitdelinfo.dtc.agedeglass.fr.nuitdelinfo.dtc.agedeglass.recovocale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import java.text.Normalizer;
import java.util.ArrayList;

import fr.nuitdelinfo.dtc.agedeglass.R;
import fr.nuitdelinfo.dtc.barcodeeye.scan.CaptureActivity;
import fr.nuitdelinfo.dtc.service.WebService;
import fr.nuitdelinfo.dtc.barcodeeye.LaunchActivity;


public class VoiceController {
    private Activity activity;
    private ArrayList<String> resultTab = new ArrayList<>();

    private TextView txtTest ;

    public void setActivity(Activity activity) {
        this.activity = activity;
        txtTest = (TextView) activity.findViewById(R.id.notifText);
    }

    public void beginVoiceRecognition(ArrayList<String> result, Context context){
        setVoiceRecognitionResults(result);
        appVoiceNavigation(context);
    }

    public void setVoiceRecognitionResults(ArrayList<String> result){
        resultTab = result;
        for (String s : resultTab){
            s = removeDiacriticalMarks(s);
        }
    }
    public static String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public void appVoiceNavigation(Context context){

        if(!VoiceGlobalVars.okLunettes){
            VoiceActivity.principalText.setText("Dites OK Lunettes...");

        }
        else {
            VoiceActivity.principalText.setText("Enoncez la requête");

        }

        String nomMedoc = "";
        for(String unResultat : resultTab){
            /*
             * Vérifications avant reconnaissance
             */
            if (unResultat.matches(".*ok.*lunette.*")){
                VoiceGlobalVars.okLunettes = true;
            }
            if (unResultat.matches(".*top.*lunette.*")){
                VoiceGlobalVars.okLunettes = false;
            }

            /*
             * Parsing du resultat
             */
            if (VoiceGlobalVars.okLunettes){

                for(String ordre : VoiceGlobalVars.consultPatternsRecognition){
                    if (unResultat.matches(ordre + ".* de .*")){
                        String[] medoc = unResultat.split(" ");
                        for(int i=0; i<medoc.length; ++i){
                            if(medoc[i].equals("de")){
                                nomMedoc = medoc[i+1];

                            }
                        }


                        nomMedoc = removeDiacriticalMarks(nomMedoc).toLowerCase();
                        int nbMedoc = WebService.getStock(nomMedoc);
                        if(nbMedoc == -1){
                            txtTest.setText("Il n'y a pas de médicament du nom de " + nomMedoc);

                            VoiceActivity.principalText.setText("Je n'ai rien trouvé.");
                        } else {
                            txtTest.setText("Il vous reste " + nbMedoc + " " + nomMedoc);
                        }

                        activity.findViewById(R.id.popup).setVisibility(View.VISIBLE);
                        android.os.Handler handler = new android.os.Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                activity.findViewById(R.id.popup).setVisibility(View.INVISIBLE);
                            }
                        }, 2000);

                        VoiceActivity.principalText.setText("Enoncez la requête");

                        //VoiceActivity.principalText.setText("getStock(" + nomMedoc+ ");");

                        return;
                    }

                }





                //activity.findViewById(R.id.popup).setVisibility(View.INVISIBLE);

               // activity.findViewById(R.id.voice_activity).startAnimation(invertSlide);

                if (unResultat.matches(".*scan.*")  || unResultat.matches(".*lire.*") || unResultat.matches(".*code.*")){


                    Intent inent = new Intent(context, LaunchActivity.class);
                    inent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(inent);


            }

            }

        }
    }


}
