package Shared;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class HttpMinimalParser {

    public record HttpTextResponse(int statusCode, String body) {}

    private HttpMinimalParser() {}

    public static String readLineCrLf(InputStream in) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            if (b == '\n') {
                break;
            }
            if (b != '\r') {
                line.write(b);
            }
        }
        return line.toString(StandardCharsets.US_ASCII);
    }

    public static String readPostBodyUtf8(Socket socket, int maxBodyBytes) throws IOException {
        InputStream in = socket.getInputStream();

        String requestLine = readLineCrLf(in);
        if (requestLine == null || requestLine.isEmpty()) {
            return null;
        }

        int contentLength = 0;
        while (true) {
            String headerLine = readLineCrLf(in);
            if (headerLine.isEmpty()) {
                break;
            }
            int colon = headerLine.indexOf(':');
            if (colon > 0) {
                String name = headerLine.substring(0, colon).trim().toLowerCase(Locale.ROOT);
                String value = headerLine.substring(colon + 1).trim();
                if ("content-length".equals(name)) {
                    try {
                        contentLength = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) {
                        contentLength = 0;
                    }
                }
            }
        }

        if (contentLength <= 0) {
            return "";
        }
        if (contentLength > maxBodyBytes) {
            contentLength = maxBodyBytes;
        }
        byte[] raw = in.readNBytes(contentLength);
        return new String(raw, StandardCharsets.UTF_8).trim();
    }

    private static String statusPhrase(int statusCode) {
        return switch (statusCode) {
            case 200 -> "OK";
            case 204 -> "No Content";
            case 400 -> "Bad Request";
            case 405 -> "Method Not Allowed";
            case 422 -> "Unprocessable Entity";
            case 500 -> "Internal Server Error";
            case 502 -> "Bad Gateway";
            case 503 -> "Service Unavailable";
            case 504 -> "Gateway Timeout";
            default -> "Error";
        };
    }

    public static void writeTextHttpResponse(Socket socket, int statusCode, String serverName, String bodyUtf8)
            throws IOException {
        OutputStream out = socket.getOutputStream();

        if (statusCode == 204) {
            out.write(("HTTP/1.1 204 " + statusPhrase(204) + "\r\nConnection: close\r\n\r\n")
                    .getBytes(StandardCharsets.US_ASCII));
            out.flush();
            return;
        }

        byte[] bodyBytes = bodyUtf8 == null ? new byte[0] : bodyUtf8.getBytes(StandardCharsets.UTF_8);
        String statusLine = "HTTP/1.1 " + statusCode + " " + statusPhrase(statusCode) + "\r\n";
        String headers =
                "Server: " + serverName + "\r\n"
                        + "Content-Type: text/plain; charset=UTF-8\r\n"
                        + "Content-Length: " + bodyBytes.length + "\r\n"
                        + "Connection: close\r\n"
                        + "\r\n";

        out.write(statusLine.getBytes(StandardCharsets.US_ASCII));
        out.write(headers.getBytes(StandardCharsets.US_ASCII));
        out.write(bodyBytes);
        out.flush();
    }

    private static int parseStatusCodeFromStatusLine(String statusLine) {
        if (statusLine == null || statusLine.isEmpty()) {
            return 500;
        }
        String[] parts = statusLine.split("\\s+");
        for (String p : parts) {
            if (p.length() == 3 && p.chars().allMatch(Character::isDigit)) {
                try {
                    return Integer.parseInt(p);
                } catch (NumberFormatException ignored) {
                    return 500;
                }
            }
        }
        return 500;
    }

    public static HttpTextResponse readHttpResponseStatusAndBody(InputStream in, int maxBodyBytes) throws IOException {
        String statusLine = readLineCrLf(in);
        if (statusLine == null || statusLine.isEmpty()) {
            return new HttpTextResponse(500, "");
        }
        int statusCode = parseStatusCodeFromStatusLine(statusLine);

        int contentLength = -1;
        while (true) {
            String headerLine = readLineCrLf(in);
            if (headerLine.isEmpty()) {
                break;
            }
            int colon = headerLine.indexOf(':');
            if (colon > 0) {
                String name = headerLine.substring(0, colon).trim().toLowerCase(Locale.ROOT);
                if ("content-length".equals(name)) {
                    try {
                        contentLength = Integer.parseInt(headerLine.substring(colon + 1).trim());
                    } catch (NumberFormatException ignored) {
                        contentLength = -1;
                    }
                }
            }
        }

        if (contentLength <= 0) {
            return new HttpTextResponse(statusCode, "");
        }
        if (contentLength > maxBodyBytes) {
            contentLength = maxBodyBytes;
        }
        byte[] raw = in.readNBytes(contentLength);
        String body = new String(raw, StandardCharsets.UTF_8).trim();
        return new HttpTextResponse(statusCode, body);
    }

    public static String readResponseBodyUtf8(Socket socket, int maxBodyBytes) throws IOException {
        return readHttpResponseStatusAndBody(socket.getInputStream(), maxBodyBytes).body();
    }

    public static HttpTextResponse postPlainTextReadFullResponse(String host, int port, String bodyUtf8,
            int maxResponseBodyBytes, int timeoutMs) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            socket.setSoTimeout(timeoutMs);

            byte[] bodyBytes = bodyUtf8.getBytes(StandardCharsets.UTF_8);
            String head = "POST / HTTP/1.1\r\n"
                    + "Host: " + host + "\r\n"
                    + "Content-Type: text/plain; charset=UTF-8\r\n"
                    + "Content-Length: " + bodyBytes.length + "\r\n"
                    + "Connection: close\r\n\r\n";

            OutputStream out = socket.getOutputStream();
            out.write(head.getBytes(StandardCharsets.US_ASCII));
            out.write(bodyBytes);
            out.flush();

            return readHttpResponseStatusAndBody(socket.getInputStream(), maxResponseBodyBytes);
        }
    }

    public static String postPlainTextReadBody(String host, int port, String bodyUtf8,
            int maxResponseBodyBytes, int timeoutMs) throws IOException {
        return postPlainTextReadFullResponse(host, port, bodyUtf8, maxResponseBodyBytes, timeoutMs).body();
    }
}
