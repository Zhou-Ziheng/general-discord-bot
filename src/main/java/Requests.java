import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Requests {
    static String token = "63630bec6dfceb0895251422b0d2ce32401165f1";
    public static void sendPutRequest(String requestUrl, String payload) {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Token " + token)
                    .PUT(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            //HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        }catch(Exception e){
            e.printStackTrace();
            //return null;
        }
    }
    public static void sendDeleteRequest(String requestUrl) {
        try {
            System.out.println(requestUrl);
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Token " + token)
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
    public static void sendPostRequest(String requestUrl, String payload) {
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Token " + token)
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<?> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            //HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        }catch(Exception e){
            e.printStackTrace();
            //return null;
        }
    }

    public static HttpResponse<java.lang.String> sendGetRequest(String requestUrl) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }

    }
}
