package workshop.mr;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.IntBinaryOperator;

public class MapReduceHandler implements HttpHandler {
    private String[] workerURLs;
    private int workerCount;
    private HttpClient client;

    public MapReduceHandler(String[] workerURLs) {
        this.workerURLs = workerURLs;
        workerCount = workerURLs.length;
        client = HttpClient.newHttpClient();
    }

    NumberFormat formatter = new DecimalFormat("0.######E0", DecimalFormatSymbols.getInstance(Locale.ROOT));
    BigInteger biggestForStandardFormatOutput = BigInteger.valueOf(1000000L);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        try (exchange) {
            int input = Integer.parseInt(path.replaceFirst("/factorial/", ""));
            String factorial = getFactorial(input);
            byte[] response = factorial.getBytes("UTF-8");
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            OutputStream responseOut = exchange.getResponseBody();
            responseOut.write(response);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(400, -1);
        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, -1);
        }
    }

    public String getFactorial(int input) {
        Range[] ranges = breakIntoSubRanges(input);
        List<BigInteger> mapResults = submitToWorkers(ranges);
        BigInteger reducedResult = mapResults.stream().reduce(BigInteger::multiply).get();
        return createHTLM(convertToReadableResult(reducedResult));
    }

    private List<BigInteger> submitToWorkers(Range[] ranges) {
        List<CompletableFuture<BigInteger>> futures = new ArrayList<>();
        for (int i = 0; i < ranges.length; i++) {
            Range range = ranges[i];
            String workerURL = workerURLs[i % workerURLs.length];
            futures.add(submitToWorker(range, workerURL));
        }
        return futures.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    private CompletableFuture<BigInteger> submitToWorker(Range range, String workerURL) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("%s/factorial/%dto%d".formatted(workerURL, range.getStart(), range.getEnd())))
                .header("Accept", "application/raw")
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply((body) -> new BigInteger(body, 10));

    }

    private Range[] breakIntoSubRanges(int input) {
        Range[] ranges;
        if (input < workerCount * 2) {
            ranges = new Range[1];
            ranges[0] = new Range(1, input);
        } else {
            ranges = new Range[workerCount];
            int rangeSize = input / workerCount;
            int remainder = input % workerCount;
            int rangeEnd = rangeSize + remainder;
            Range first = new Range(1, rangeEnd);
            ranges[0] = first;
            for (int i = 1; i < workerCount; i++) {
                int rangeStart = rangeEnd + 1;
                rangeEnd = Math.min(input, rangeStart + rangeSize);
                Range range = new Range(rangeStart, rangeEnd);
                ranges[i] = range;
            }
        }
        for (Range range : ranges) {
            System.out.println(range);
        }
        return ranges;
    }

    private String createHTLM(String readableResult) {
        String htmlTemplate = """
                <!DOCTYPE html>
                <html lang="en">
                    <head>
                       <meta charset="utf-8">
                       <meta name="viewport" content="width=device-width, initial-scale=1">
                    </head>
                    <body>
                       <H1>Factorial</H1>
                       <H2>%s</H2>
                       <H1>Handled by</H1>
                       <H2>%s</H2>
                    </body>
                </html>""".formatted(readableResult, "the only server");
        return htmlTemplate;
    }

    private String convertToReadableResult(BigInteger aggregate) {
        if (aggregate.compareTo(biggestForStandardFormatOutput) >= 0) {
            return formatter.format(aggregate);
        }
        return aggregate.toString(10);
    }
}
