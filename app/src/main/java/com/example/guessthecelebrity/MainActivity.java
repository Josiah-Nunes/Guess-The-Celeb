package com.example.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    int chosenCeleb=0;
    Random rand = new Random();
    ImageView imageView;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    String[] answers=new String[4];
    int locationOfCorrectAnswer=0;

    ArrayList<String> celebsUrls=new ArrayList<String>();
    ArrayList<String> celebsNames=new ArrayList<String>();

    public void answer(View view) {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {
            Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
        }
         else
        {
            Toast.makeText(getApplicationContext(), "Wrong! It was "+celebsNames.get(chosenCeleb) , Toast.LENGTH_SHORT).show();
        }
        newQuestion();
    }


    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>
    {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream in = httpURLConnection.getInputStream();
                Bitmap bitmap= BitmapFactory.decodeStream(in);
                return bitmap;

            } catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }

    public class Download extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try
            {
                url=new URL(urls[0]);

                urlConnection=(HttpURLConnection) url.openConnection();

                InputStream in=urlConnection.getInputStream();

                InputStreamReader reader=new InputStreamReader(in);

                int data=reader.read();

                while(data!=-1)
                {
                    char current=(char) data;
                    result+=current;
                    data=reader.read();
                }

                return result;


            }catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }

        }
    }

    public void newQuestion() {

        Download task = new Download();
        String result = null;
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"listedArticle\">");

            Pattern p = Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while (m.find()) {
                celebsUrls.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while (m.find()) {
                celebsNames.add(m.group(1));
            }


            chosenCeleb = rand.nextInt(celebsUrls.size());
            ImageDownloader imageTask = new ImageDownloader();
            Bitmap celebImage = imageTask.execute(celebsUrls.get(chosenCeleb)).get();
            imageView.setImageBitmap(celebImage);

        }
            catch (Exception e)
            {
                e.printStackTrace();

            }
        }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        button4=findViewById(R.id.button4);



        try {


            locationOfCorrectAnswer=rand.nextInt(4);
            int wrongAnswerLocation;

            for(int i=0;i<4;i++)
            {
                if(i==locationOfCorrectAnswer)
                {
                    answers[i]=celebsNames.get(chosenCeleb);
                }
                else
                {
                    wrongAnswerLocation=rand.nextInt(celebsUrls.size());

                    while(wrongAnswerLocation==i)
                    {
                        wrongAnswerLocation=rand.nextInt(celebsUrls.size());
                    }
                    answers[i]=celebsNames.get(wrongAnswerLocation);
                }
            }
            newQuestion();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}