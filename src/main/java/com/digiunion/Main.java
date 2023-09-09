package com.digiunion;

import com.digiunion.kick.KickClient;
import lombok.extern.java.Log;
import lombok.val;
import org.jetbrains.annotations.Nullable;

/**
 * Main is the class where the project runs javafx within the main method
 */
@Log

public final class Main {

    /**
     * examines concurrency implementation with CompletableFutures
     * @param args: String[]
     */
    public static void main(@Nullable String[] args) {
        val client = new KickClient();
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

