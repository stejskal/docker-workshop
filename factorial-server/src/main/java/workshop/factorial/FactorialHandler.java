package workshop.factorial;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FactorialHandler implements HttpHandler {
    NumberFormat formatter = new DecimalFormat("0.######E0", DecimalFormatSymbols.getInstance(Locale.ROOT));
    BigInteger biggestForStandardFormatOutput = BigInteger.valueOf(1000000L);

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        try (exchange) {
            String rawInput = path.replaceFirst("/factorial/", "");
            Range input = parseRange(rawInput);
            System.out.println("Serving input: " + input);
            BigInteger factorial = getFactorial(input);
            byte[] response;
            if (requestsRaw(exchange)) {
                exchange.getResponseHeaders().set("Content-Type", "application/raw");
                response = factorial.toString(10).getBytes("UTF-8");
            } else {
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                String readableResult = convertToReadableResult(factorial);
                String html = createHTLM(readableResult);
                response = html.getBytes("UTF-8");
            }

            exchange.sendResponseHeaders(200, response.length);
            OutputStream responseOut = exchange.getResponseBody();
            responseOut.write(response);
        } catch (ParseException e) {
            exchange.sendResponseHeaders(400, -1);
        } catch (Exception e) {
            exchange.sendResponseHeaders(500, -1);
        }
    }

    private boolean requestsRaw(HttpExchange exchange) {
        for (Map.Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {
            if (Objects.equals(header.getKey(), "Accept")) {
                if (header.getValue().contains("application/raw")) {
                    return true;
                }
            }
        }
        return false;
    }

    private Range parseRange(String inputParam) throws ParseException {
        String[] split = inputParam.toLowerCase().split("to");
        int start;
        int end;
        if (split.length == 1) {
            try {
                start = 1;
                end = Integer.parseInt(split[0]);

            } catch (NumberFormatException e) {
                throw new ParseException(e.getMessage(), -1);
            }
        } else if (split.length == 2) {
            try {
                start = Integer.parseInt(split[0]);
                end = Integer.parseInt(split[1]);
            } catch (NumberFormatException e) {
                throw new ParseException(e.getMessage(), -1);
            }
        } else {
            throw new ParseException("input contained multiple \"to\" delimiters", -1);
        }
        return new Range(start, end);
    }

    public BigInteger getFactorial(Range range) {
        BigInteger aggregate = BigInteger.ONE;
        for (int i = range.getStart(); i <= range.getEnd(); i++) {
            aggregate = aggregate.multiply(BigInteger.valueOf(i));
            if (i % 1000 == 0) {
                System.out.println("One thousand more iterations done.  Current index " + i + " " + range);
            }
        }
        return aggregate;
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
                    </body>
                </html>""".formatted(readableResult);
        return htmlTemplate;
    }

    private String convertToReadableResult(BigInteger aggregate) {
        if (aggregate.compareTo(biggestForStandardFormatOutput) >= 0) {
            return formatter.format(aggregate);
        }
        return aggregate.toString(10);
    }
}
