import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;

public class CrawlerTask implements Runnable {
    URLPool urlPool;
    public static final String URL_PREFIX = "http://";

    public CrawlerTask(URLPool pool) {
        this.urlPool = pool;
    }

    public static void buildNewUrl(String str, int depth, URLPool pool) {
        try {
            int end_of_link = str.indexOf("\"", str.indexOf(URL_PREFIX));
            String currentLink = str.substring(str.indexOf(URL_PREFIX), end_of_link);
            pool.addPair(new URLDepthPair(currentLink, depth + 1));

        } catch (StringIndexOutOfBoundsException ignored) {
        }
    }

    @Override
    public void run() {
        while (true) {
            try{
            URLDepthPair currentPair = urlPool.getPair();
            URL lol = new URL(currentPair.getURL());
            BufferedReader in = new BufferedReader(new InputStreamReader(lol.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains(URL_PREFIX) && line.contains("<a href=")) {
                    buildNewUrl(line, currentPair.getDepth(), urlPool);
                }
            }
        }catch(IOException e){

            }
        }
    }
}