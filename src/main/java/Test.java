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
        String url = "http://127.0.0.1:8000/login/servers/941876412523094137/users/176414170646839296";
        String payload="{\"user_id\":\""+"176414170646839296"+"\",\"racistness\":\"0\",\"racistness\":\"0\",\"swear_count\":\"0\",\"gender\":\"unidentified\",\"penis_size\":\"0:0\"}";
        Server.sendRequest(url,payload, "PUT");
        System.out.println(Server.sendRequest(url, payload, "PUT"));
    }
}