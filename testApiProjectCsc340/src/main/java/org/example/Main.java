package org.example;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("Hello world!");

        //Setting up transfer object for easy json access from api
        Transcript transcript = new Transcript();
        //transcript.setAudio_url("https://github.com/johnmarty3/JavaAPITutorial/raw/main/Thirsty.mp4");
        transcript.setAudio_url("https://github.com/johnmarty3/JavaAPITutorial/blob/main/Thirsty.mp4?raw=true");

        Gson gson = new Gson();
        String jsonRequest = gson.toJson(transcript);
        System.out.println(jsonRequest);
        //This will request to the api
        //Gives api auth code and specific uril
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("Authorization", "e3c8fb4cf6624d73aa575edf6536200e")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();
        HttpClient httpClient = HttpClient.newHttpClient();

        //This will actually send the post request
        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(postResponse.body());

        //This will store the response
        transcript = gson.fromJson(postResponse.body(), Transcript.class);
        System.out.println("?"+transcript.getId());

        //Lets construct the get request to get the actual audio transcriptiopn
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript/"+transcript.getId()))
                .header("Authorization", "e3c8fb4cf6624d73aa575edf6536200e")
                .build();

        System.out.println("!"+transcript.getId());
        //Waiting until api process is complete
        while(true){
            //Building again
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            //Storage
            transcript = gson.fromJson(getResponse.body(), Transcript.class);
            System.out.println(transcript.getStatus());
            if("completed".equals(transcript.getStatus())||"error".equals(transcript.getStatus())){
                break;
            }
            //Wait a second for the process to complete
            Thread.sleep(1000);
        }
        System.out.println("Complete!");
        System.out.println(transcript.getText());











    }
}