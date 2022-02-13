import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test{
    public static void main(String[] args) throws IOException, InterruptedException {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:8000/login/servers/delete/942510084527890482/"))
                    //.uri(URI.create(Main.URLAddress+"login/servers/delete/942143366005669998/"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Token 735eee31ab2dc4cbe6ad7047153d1fececa0d33d")
                    .DELETE()
                    .build();
            HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            System.out.println(response.statusCode());
            System.out.println(response.body());
            //HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        }catch(Exception e){
            e.printStackTrace();
            //return null;
        }
    }
}