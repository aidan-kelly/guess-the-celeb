package net.aidanjameskelly.guesstheceleb;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    Random rand;
    int celebNumber;
    String websiteHTML;

    public class DownloadHTML extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection =(HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){
                    char current = (char)data;
                    result += current;
                    data = reader.read();
                }

                return result;




            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadHTML downloadHTML = new DownloadHTML();
        websiteHTML = "";

        try{
            websiteHTML = downloadHTML.execute("http://www.posh24.se/kandisar").get();
        }catch (Exception e){
            e.printStackTrace();
        }

        rand = new Random();


        Pattern p = Pattern.compile("<img src=\"(.*?)\" alt=\"(.*?)\"/>");
        Matcher m = p.matcher(websiteHTML);

        while(m.find()){
            System.out.println(m.group(1));
        }

    }

    public void newCeleb(){
        celebNumber = rand.nextInt(87) + 1;
    }

}
