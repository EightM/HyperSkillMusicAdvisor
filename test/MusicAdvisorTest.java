import advisor.Main;

import org.hyperskill.hstest.dynamic.output.SystemOutHandler;
import org.hyperskill.hstest.mocks.web.WebServerMock;
import org.hyperskill.hstest.stage.StageTest;
import org.hyperskill.hstest.testcase.CheckResult;
import org.hyperskill.hstest.testcase.TestCase;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.List;
import java.util.function.Function;

class RedirectUriFinder {
    
    private Thread thread;
    
    volatile CheckResult checkResult = CheckResult.correct();
    
    private String fictiveAuthCode;
    
    RedirectUriFinder(String fictiveAuthCode) {
        this.fictiveAuthCode = fictiveAuthCode;
    }
    
    void start() {
        // this message will be ignored, if user program hangs
        checkResult = CheckResult.wrong("Not found a link with redirect_uri.");
        thread = new Thread(() -> {
            String redirectUri = "";
            long searchTime = System.currentTimeMillis();
            
            while (!Thread.interrupted()) {
                if (System.currentTimeMillis() - searchTime > 1000 * 9) {
                    System.out.println("Tester: Not found a link with redirect_uri after 9 seconds. Stopping.");
                    return;
                }
                
                String out = SystemOutHandler.getDynamicOutput();
                if (out.contains("redirect_uri=")) {
                    redirectUri = out.split("redirect_uri=")[1];
                    if (redirectUri.contains("&")) {
                        redirectUri = redirectUri.split("&")[0];
                    }
                    if (redirectUri.contains("\n")) {
                        // \r \n or \r\n
                        redirectUri = redirectUri.split("\\R")[0];
                    }
                    break;
                }
                
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    return;
                }
            }
            try {
                HttpClient client = HttpClient.newBuilder().build();
                HttpRequest emptyRequest = HttpRequest.newBuilder()
                        .uri(URI.create(redirectUri))
                        .timeout(Duration.ofMillis(500))
                        .GET()
                        .build();
                HttpRequest errorRequest = HttpRequest.newBuilder()
                        .uri(URI.create(redirectUri + "?error=access_denied"))
                        .timeout(Duration.ofMillis(500))
                        .GET()
                        .build();
                HttpRequest codeRequest = HttpRequest.newBuilder()
                        .uri(URI.create(redirectUri + "?code=" + fictiveAuthCode))
                        .timeout(Duration.ofMillis(500))
                        .GET()
                        .build();
                
                checkResult = CheckResult.wrong("Making request to " + redirectUri + " was not finished.");
                System.out.println("Tester: making requests to redirect uri: " + redirectUri);
                HttpResponse<String> badResponse = client.send(emptyRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println("Tester: done request 1: " + badResponse.body());
                HttpResponse<String> badResponse2 = client.send(errorRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println("Tester: done request 2: " + badResponse2.body());
                HttpResponse<String> goodResponse = client.send(codeRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println("Tester: done request 3: " + goodResponse.body());
                
                if (!badResponse.body().contains("Not found authorization code. Try again.")
                        || !badResponse2.body().contains("Not found authorization code. Try again.")) {
                    checkResult = CheckResult.wrong("You should send to the browser: `Not found authorization code. Try again.` " +
                            "if there is no code.");
                    return;
                }
                
                if (!goodResponse.body().contains("Got the code. Return back to your program.")) {
                    checkResult = CheckResult.wrong("You should send `Got the code. Return back to your program.` " +
                            "if the query contains the code.");
                    return;
                }
                checkResult = CheckResult.correct();
            } catch (HttpTimeoutException e) {
                System.out.println("Tester: Timeout");
                // this checkResult will be ignored in most cases (if user program hangs)
                checkResult = CheckResult.wrong("Not received any response from the server, found in redirect_uri: "
                        + redirectUri);
            } catch (InterruptedException e) {
                // when the user printed token, but not answered the last request with code.
                checkResult = CheckResult.wrong("Request to " + redirectUri + " was interrupted. " +
                        "Make sure, that you give the right feedback in your browser.");
            } catch (Exception e) {
                System.out.println("Tester: Error: " + e.getMessage());
                e.printStackTrace();
            }
        });
        thread.start();
    }
    
    void stop() {
        if (thread != null) {
            thread.interrupt();
            try {
                // wait the thread to set a proper checkResult in case of interruption.
                thread.join();
            } catch(InterruptedException ignored) {
            }
        }
    }
}


public class MusicAdvisorTest extends StageTest<Void> {
    
    private static final String fictiveAuthCode = "123123";
    private static final String fictiveAccessToken = "456456";
    private static final String fictiveRefreshToken = "567567";
    
    
    private RedirectUriFinder redirectUriCatcher = new RedirectUriFinder(fictiveAuthCode);
    
    private int accessServerPort = 45678;
    private String accessServerUrl = "http://127.0.0.1:" + accessServerPort;
    
    private String[] arguments = new String[]{
            "-access",
            accessServerUrl
    };
    
    private String tokenResponse = "{" +
            "\"access_token\":\"" + fictiveAccessToken + "\"," +
            "\"token_type\":\"Bearer\"," +
            "\"expires_in\":3600," +
            "\"refresh_token\":" + "\"" + fictiveRefreshToken + "\"," +
            "\"scope\":\"\"" +
            "}";
    
    // TODO handle auth code argument to get the token.
    private WebServerMock accessServer = new WebServerMock(accessServerPort)
            .setPage("/api/token", tokenResponse);
    
    public MusicAdvisorTest() {
        super(Main.class);
    }
    
    @Override
    public List<TestCase<Void>> generate() {
        return List.of(
                new TestCase<Void>()
                        .addArguments(arguments)
                        .runWith(accessServer)
                        .addInput(1, out -> {
                            redirectUriCatcher.start();
                            return "auth\n";
                        })
                        .addInput(1, out -> {
                            redirectUriCatcher.stop();
                            if (redirectUriCatcher.checkResult != CheckResult.correct()) {
                                return redirectUriCatcher.checkResult;
                            }
                            if (!out.contains(fictiveAccessToken)) {
                                return CheckResult.wrong("Not found correct access token in the result. " +
                                        "Make sure, that you use the server from the command line arguments to access the token.");
                            }
                            return "featured";
                        })
                        .addInput(1, out -> {
                            if (!out.contains("---FEATURED---")) {
                                return new CheckResult(false,
                                        "When \"featured\" was inputted there should be \"---FEATURED---\" line");
                            }
                            return "exit";
                        }),
                
                new TestCase<Void>()
                        .setInput("new\nexit")
                        .setCheckFunc((reply, v) -> {
                            if (!reply.strip().startsWith("Please, provide access for application.")) {
                                return new CheckResult(false,
                                        "When no access provided you should output " +
                                                "\"Please, provide access for application.\"");
                            }
                            return CheckResult.correct();
                        }),
                
                new TestCase<Void>()
                        .setInput("featured\nexit")
                        .setCheckFunc((reply, v) -> {
                            if (!reply.strip().startsWith("Please, provide access for application.")) {
                                return new CheckResult(false,
                                        "When no access provided you should output " +
                                                "\"Please, provide access for application.\"");
                            }
                            return CheckResult.correct();
                        })
        
        );
        
    }
    
    @Override
    public CheckResult check(String reply, Void attach) {
        return CheckResult.correct();
    }
}
