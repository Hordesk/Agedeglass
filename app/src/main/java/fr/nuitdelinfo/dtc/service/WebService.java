package fr.nuitdelinfo.dtc.service;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by leomouly_bodeveix on 05/12/2014.
 */
public class WebService {
    private static String host = "http://nixitup.com/webservice.php?";
    /*
    public static void main(String [ ] args)
    {
        WebService.ajouterPatient("Edouard","Clement", "M", "B", 32, 99);
        WebService.ajouterPatient("Edoufqfqfzard","Clement", "M", "B", 32, 99);
    }
    */
    /**
     *
     * @param nomMedoc le nom du medoc
     * @return la qté de stock du medoc. -1 si le nom du medoc existe pas...
     */
    public static int getStock(String nomMedoc) {
        if(nomMedocExiste(nomMedoc)) {
            String url = host + "action=1" + "&nomDuMedoc=" + nomMedoc;
            String res = attackWS(url);
            System.out.println("Appel de getStock res=" + res);
            return Integer.parseInt(res);
        } else {
            return -1;
        }
    }

    /**
     *
     * @param idMedoc
     * @param qte qté a ajouter
     * @return true si OK, false si nom du medoc inexistant
     */
    public static boolean ajouterStock(int idMedoc, int qte)
    {
        if(idMedocExiste(idMedoc))
        {
            String url = host + "action=2"+ "&idMedoc=" + idMedoc+ "&qte=" + qte;
            attackWS(url);
            return true;
        } else
        {
            return false;
        }
    }

    /**
     @return false if FAILED (pas assez de stock OU medoc inexistant) ou TRUE si OK
     */
    public static boolean retirerStock(int idMedoc, int qte)
    {
        if(idMedocExiste(idMedoc))
        {
            String url = host + "action=3"+ "&idMedoc=" + idMedoc+ "&qte=" + qte;
            int resInt = Integer.parseInt(attackWS(url));
            if(resInt == -1)
            {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public static boolean idMedocExiste(int idMedoc)
    {
        String url = host + "action=4"+ "&idMedoc=" + idMedoc;
        int resInt = Integer.parseInt(attackWS(url));
        if(resInt == 0)
        {
            return true;
        } else if (resInt < 0)
        {
            return false;
        } else  {
            return false;
        }
    }

    public static boolean nomMedocExiste(String nomDuMedoc)
    {
        String url = host + "action=5"+ "&nomDuMedoc=" + nomDuMedoc;
        int resInt = Integer.parseInt(attackWS(url));
        if(resInt == 0)
        {
            return true;
        } else if (resInt < 0)
        {
            return false;
        } else {
            return false;
        }
    }

    /**
     *
     * @param nom
     * @param prenom
     * @param sexe
     * @param groupeS
     * @param poids
     * @param taille
     * @return true si le patient a été ajouté , false si il existe déjà :)
     */
    public static boolean ajouterPatient(String nom, String prenom, String sexe, String groupeS, int poids, int taille)
    {
        String url = host + "action=6"+ "&nom=" + nom+ "&prenom=" + prenom+ "&sexe=" + sexe + "&groupeS=" + groupeS + "&poids=" + poids+ "&taille=" + taille;
        int resInt = Integer.parseInt(attackWS(url));
        if(resInt == -1)
        {
            return false;
        }
        return true;
    }


    public static String attackWS(String url) {
        try {
            Log.d("", url);

            return new AsyncTask<String, Void, String>(){


                @Override
                protected String doInBackground(String... params) {
                    try {
                        return new Scanner(new URL(params[0]).openStream(), "UTF-8").useDelimiter("\\A").next();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            }.execute(url).get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR ATTACKING WS :/";
    }
}
