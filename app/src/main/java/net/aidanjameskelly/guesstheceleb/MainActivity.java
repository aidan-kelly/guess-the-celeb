package net.aidanjameskelly.guesstheceleb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    //initialize our public variables
    Random rand;
    int celebNumber;
    String websiteHTML;
    ArrayList<Celeb> celebList;

    ImageView imageView;
    DownloadImage downloadImage;

    //our celeb class.
    //url: a String that represents an image of the celeb
    //celebName: a String containing the characters of the celeb's name
    public class Celeb{
        String url;
        String celebName;

        public  Celeb(String inUrl, String inCeleb){
            url = inUrl;
            celebName = inCeleb;
        }

    }


    //Downloads the HTML of the website.
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

    public class DownloadImage extends  AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            try{
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();

                Bitmap image = BitmapFactory.decodeStream(in);

                return image;


            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
        }

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);

        DownloadHTML downloadHTML = new DownloadHTML();
        downloadImage = new DownloadImage();
        websiteHTML = "";

        try{
            websiteHTML = downloadHTML.execute("http://www.posh24.se/kandisar").get();
        }catch (Exception e){
            e.printStackTrace();
        }

        rand = new Random();
        celebList = new ArrayList<Celeb>();

        //we use a regex to find all the pictures of celebs and their names
        Pattern p = Pattern.compile("<img src=\"(.*?)\" alt=\"(.*?)\"/>");
        Matcher m = p.matcher(websiteHTML);

        //when we find a match we create a new Celeb object and add it to our list
        while(m.find()){
            Celeb toAddCeleb = new Celeb(m.group(1), m.group(2));
            celebList.add(toAddCeleb);
        }

        newCeleb();


    }

    //randomly chooses a new celeb and updates the image.
    public void newCeleb(){
        celebNumber = rand.nextInt(81);
        Celeb celeb = celebList.get(celebNumber);
        Bitmap image;
        try{
            image = downloadImage.execute(celeb.url).get();
        }catch (Exception e){
            e.printStackTrace();
            image = null;
        }

        imageView.setImageBitmap(image);


    }

}
