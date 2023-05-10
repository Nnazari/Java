
public class Crawler {
    private String URL;
    private static int maxDepth;
    public static int CountThreads;
    public static int WaitingThreads = 0;
    public static int CountURLs = 0;

    public static int getMaxDepth() {
        return maxDepth;
    }

    public Crawler(String URL, int maxDepth, int countThreads) {
        this.URL = URL;
        Crawler.maxDepth = maxDepth;
        Crawler.CountThreads = countThreads;
    }
    public void run() {
        CrawlerTask task = new CrawlerTask(new URLDepthPair(URL, 0));
        task.start();
    }
    private static void printResult() {
        System.out.println();
        System.out.println("Кол во ссылко: " + CountURLs);
    }
    public static boolean checkDigit(String line) {
        boolean isDigit = true;
        for (int i = 0; i < line.length() && isDigit; i++)
            isDigit = Character.isDigit(line.charAt(i));
        return isDigit;
    }
    public static void main(String[] args) {
        args = new String[]{"https://wahapedia.ru/", "1", "5"};
        if (checkDigit(args[1])&&checkDigit(args[2])) {
        Crawler crawler = new Crawler(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        crawler.run();
        Runtime.getRuntime().addShutdownHook(new Thread(Crawler::printResult));
    } else {
        System.out.println("usage: java Crawler <URL> <максимальная глубина> <Кол-во потоков>");
    }
    }
}


