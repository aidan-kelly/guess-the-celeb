package net.aidanjameskelly.guesstheceleb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
    int[] gameState;
    String websiteHTML;
    ArrayList<Celeb> celebList;
    ImageView imageView;
    Button button1;
    Button button2;
    Button button3;
    Button button4;

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

            //set up the result string
            String result = "";

            try {

                //create a connection to the url
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection =(HttpURLConnection) url.openConnection();

                //create an input stream from the connection
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                //reads the data as it comes in
                int data = reader.read();
                while(data != -1){

                    //updates our result string
                    char current = (char)data;
                    result += current;

                    //reads next char
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

                //creates a connection to the URL provided.
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();

                //decodes the input stream into a bitmap
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

        //set up
        imageView = findViewById(R.id.imageView);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        DownloadHTML downloadHTML = new DownloadHTML();
        websiteHTML = "";
        gameState = new int[] {2,2,2,2};

        //attempts to download the HTML
        try{
            websiteHTML = downloadHTML.execute("http://www.posh24.se/kandisar").get();
        }catch (Exception e){
            e.printStackTrace();
        }

        //init the random and celebList
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

        //call newCeleb to fill image
        newCeleb();


    }

    //randomly chooses a new celeb and updates the image.
    public void newCeleb(){
        DownloadImage downloadImage = new DownloadImage();
        gameState= new int[] {2,2,2,2};

        //generate new number and grab that celeb
        celebNumber = rand.nextInt(81);
        gameState[rand.nextInt(4)] = 1;
        Celeb celeb = celebList.get(celebNumber);

        //attempts to download the celeb image
        Bitmap image;
        try{
            image = downloadImage.execute(celeb.url).get();
        }catch (Exception e){
            e.printStackTrace();
            image = null;
        }

        //sets the imageview to our celeb picture
        imageView.setImageBitmap(image);

        for(int i = 0; i < 4; i++){

            int randNum = -1;
            while(true){
                if(randNum != -1 && randNum != celebNumber){
                    break;
                }else{
                    randNum = rand.nextInt(81);
                }
            }

            Celeb c = celebList.get(randNum);


            if(gameState[i] == 1){
                if(i==0){
                    button1.setText(celeb.celebName);
                }else if(i==1){
                    button2.setText(celeb.celebName);
                }else if(i==2){
                    button3.setText(celeb.celebName);
                }else if(i==3){
                    button4.setText(celeb.celebName);
                }
            }else if(i == 0){
                button1.setText(c.celebName);
            }else if(i == 1){
                button2.setText(c.celebName);
            }else if(i == 2){
                button3.setText(c.celebName);
            }else if(i == 3){
                button4.setText(c.celebName);
            }
        }

    }

    public void onClick(View view){
        int guess = Integer.parseInt(view.getTag().toString());

        if(gameState[guess] == 1){
            Toast.makeText(this, "Correct guess!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Wrong guess!", Toast.LENGTH_SHORT).show();
        }
        newCeleb();
    }

}
