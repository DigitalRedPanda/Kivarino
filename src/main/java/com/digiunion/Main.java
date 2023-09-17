package com.digiunion;

import com.digiunion.kick.model.Channel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.activej.eventloop.Eventloop;
import io.activej.http.AsyncHttpClient;
import io.activej.http.HttpRequest;
import lombok.extern.java.Log;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

import static com.digiunion.kick.util.KickEndpoints.CHANNELS;
import static io.activej.http.HttpHeaders.ACCEPT_ENCODING;
import static io.activej.http.HttpHeaders.USER_AGENT;

/**
 * Main is the class where the project runs javafx within the main method
 */
@Log

public final class Main {



    /**
     * examines concurrency implementation with CompletableFutures
     * @param args: String[]
     */
    public static void main(@Nullable String[] args) throws NoSuchAlgorithmException, IOException, InterruptedException {
//        try {
////            val hClient = HttpClient.newBuilder()
//////                .proxy(ProxySelector.of(new InetSocketAddress(8080)))
////                .version(HttpClient.Version.HTTP_2)
////                .build();
////            val response = hClient.send(HttpRequest.newBuilder(URI.create(CHANNELS.url.concat("digital-red-panda"))).GET().build(), HttpResponse.BodyHandlers.ofString());
////            System.out.println(response.body());
//        } catch (IOException | InterruptedException e) {
//            log.severe(e.toString());
//        }
//        val clientR = HttpClient.newBuilder().sslContext(SSLContext.getDefault()).build();
//        val request = HttpRequest.newBuilder(URI.create(CHANNELS.url.concat("digital-red-panda"))).headers(HttpHeaders.USER_AGENT.toString(), "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36").header(HttpHeaders.ACCEPT_ENCODING.toString(), "application/json").build();
//        val rResponse = clientR.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println(rResponse.body());
//        val client = new KickClient();
//        try {
//            val channels = Database.instance.getAllChannels().stream().map(Channel::slug).toList();
////            client.getAllChannels(channels).forEach(System.out::println);
//            client.getChannel("theprimeagen").thenAccept(System.out::println);
//            System.out.println(client.getChannelSync("theprimeagen"));
//        } catch (SQLException e) {
//            log.severe("could not fetch digital-red-panda; " + e.getMessage());
//        }
//        System.exit(0);
//        val serializer = SerializerBuilder.create().build(Channel.class);
        val eventloop = Eventloop.create();
        val mapper = new ObjectMapper();
        val ssl = SSLContext.getDefault();
        val client = AsyncHttpClient.create(eventloop).withSslEnabled(ssl, eventloop);
        val resultt = eventloop.submit(()->
            client.request(HttpRequest.get(CHANNELS.url.concat("sadmadladsalman"))
                    .withHeader(USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/117.0")
                    .withHeader(ACCEPT_ENCODING,""))
                .then(lPlusRatio -> lPlusRatio.loadBody())
                .map(body -> body.getString(StandardCharsets.UTF_8))
                .whenComplete((result, exceptione) -> log.info("%s has been fetched".formatted("aboSalman")))
                .whenException(exception -> log.severe("could not fetch %s; %s".formatted("aboSalman", exception.getMessage())))
        ).thenApply(aboSalman -> {
            try {
                return mapper.readValue(aboSalman, Channel.class);
            } catch (JsonProcessingException e) {
                log.severe("could not parse aboSalman; " + e.getMessage());
                return "aboSalman";
            }
        }).thenAccept(System.out::println);
        eventloop.run();
//        System.out.println("Headers:");
//        channel2.join().getHeaders().forEach((lmao) -> System.out.printf("%s: %s,\n", lmao.getKey(), lmao.getValue()));
//        System.out.println("Cookies:");
//        channel2.join().getCookies().forEach((name, value)-> System.out.printf("%s: %s,\n", name, value));
//        val httpClient = HttpClient.newBuilder().sslContext(SSLContext.getDefault()).build();
//        val request = java.net.http.HttpRequest.newBuilder(URI.create(CHANNELS.url.concat("digital-red-panda"))).GET()
//            .headers("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/117.0",
//                "Content-Type","text/html").build();
//        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//        System.out.println(response);
//        val channel11 = channel1.join();
//        val channell22 = channel2.join();
//        System.out.println("me: " + channel11.toString() + "jilly: " + channell22.toString());
//        val client = new KickClient();
//        val eventloop = Eventloop.create().withCurrentThread();
//        val dnsClient = RemoteAsyncDnsClient.create(eventloop).withDnsServerAddress(new InetSocketAddress(8080)).withTimeout(Duration.of(5, TimeUnit.SECONDS.toChronoUnit()));
//        val client = AsyncHttpClient.create(eventloop).withDnsClient(dnsClient);
//        val promise = client.request(HttpRequest.get(CHANNELS.url.concat("digital-red-panda")));
//        val response = promise.async().getResult().getCode();
//        System.out.println(response);
//        val mapper = new ObjectMapper();
//        Token token;
//        val clientR = KickClient.getRClient();
//        val defaultHeaders = new String[]{
//            "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
//            "Accept-Encoding", "gzip, deflate, br",
//            "Accept-Language", "en-US,en;q=0.9",
//            "Authorization", "",
//            "Connection", "keep-alive",
//            "Host", "kick.com",
//            "sec-ch-ua", "\"Google Chrome\";v=\"111\", \"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"111\"",
//            "sec-ch-ua-mobile", "?0",
//            "sec-ch-ua-platform", "Windows",
//            "sec-fetch-dest", "document",
//            "sec-fetch-mode", "navigate",
//            "sec-fetch-site", "none",
//            "sec-fetch-user", "?1",
//            "upgrade-insecure-requests", "1",
//            "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36",
//            "X-XSRF-TOKEN", ""};
//        val authHeaders = new String[]{
//            "authority", "kick.com",
//            "accept", "application/json, text/plain, */*",
//            "accept-language", "en-US",
//            "authorization", "Bearer ",
//            "content-type", "application/json",
//            "origin", "https://kick.com",
//            "referer", "https://kick.com/",
//            "sec-ch-ua", """
//                           "Google Chrome";v="111", "Not(A:Brand";v="8", "Chromium";v="111""
//                       """,
//            "sec-ch-ua-mobile", "?0",
//            "sec-ch-ua-platform", "\"Windows\"",
//            "sec-fetch-dest", "empty",
//            "sec-fetch-mode", "cors",
//            "sec-fetch-site", "same-origin",
//            "user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (HTML, like Gecko) Chrome/111.0.0.0 Safari/537.36",
//            "XSRF-TOKEN", ""};
//        try(val response = clientR.newCall(new Request.Builder().headers(Headers.of(defaultHeaders)).url(BASE_URL.url).get().build()).execute()) {
//           response.request().headers().forEach(System.out::println);
//           authHeaders[29] =  response.headers().values("set-cookie").get(2).split("[=;]")[1];
//           authHeaders[7] += authHeaders[29];
//       }
//       try(val response = clientR.newCall(new Request.Builder().headers(Headers.of(authHeaders)).url(BASE_URL.url.concat("kick-token-provider")).get().build()).execute()) {
//           System.out.println(authHeaders[7] = "Bearer " + response.headers().values("set-cookie").get(1).split("[=;]")[1]);
//           assert response.body() != null;
//           val jsonResponse = response.body().string();
//           token = mapper.readValue(jsonResponse, Token.class);
//           System.out.println(jsonResponse);
//       }
//        try(val response = clientR.newCall(new Request.Builder().headers(Headers.of(authHeaders)).url(BASE_URL.url.concat("login")).post(RequestBody.create("""
//           {
//           "email": "%s",
//           "password": "%s",
//           "%s": "",
//           "_kick_token_valid_from": "%s"
//           }
//           """.formatted(Info.getUsername(), Info.getPassword(), token.nameFieldName(), token.encryptedValidFrom()), MediaType.parse("application/json"))).build()).execute()){
//           System.out.println(response.code());
//        } catch (IOException e) {
//           log.severe("could not pass through authorization; not authentic :copynine: " + e.getMessage());
//        }
//        try(val respose = clientR.newCall(new Request.Builder().headers(Headers.of(authHeaders)).url(API_V1.url.concat("user")).get().build()).execute()){
//            assert respose.body() != null;
//            System.out.println(respose.body().string());
//        } catch (IOException e) {
//            log.severe("could not fetch self; " + e.getMessage());
//        }
//        System.exit(0);
//        clientR.newCall(new Request.Builder().header("Authorization", "Bearer " + token).url(API_V1.url.concat("")).post(RequestBody.create("""
//
//           {
//           "chatroom_id"
//           }
//           """, MediaType.parse("application/json"))).build());
//
//        try (val session = ClientManager.createClient().connectToServer(new KickClientWebsocket(new ArrayList<>()), URI.create("wss://ws-us2.pusher.com/app/eb1d5f283081a78b932c?protocol=7&client=js&version=7.4.0&flash=false"))) {
//        } catch (DeploymentException | IOException e) {
//            log.severe("could not establish connection; " + e.getMessage());
//        }

    }
}

