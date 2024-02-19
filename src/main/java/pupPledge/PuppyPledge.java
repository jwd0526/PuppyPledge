package pupPledge;

import jakarta.activation.FileDataSource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.io.*;
import java.net.*;


public class PuppyPledge {

    public static void main(String[] args) throws URISyntaxException {
        String pupURL = fetchPuppyPicture();
        System.out.println(pupURL);
        String imageUrl = fetchPuppyPicture();
        String destinationFile = "src/main/resources/puppy.jpg";
        try {
            saveImage(imageUrl, destinationFile);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Image downloaded successfully.");

        String quote = fetchInspirationalQuote();
        System.out.println("Inspirational quote: " + quote);

        sendSMS(quote);

    }

    public static void saveImage(String imageUrl, String destinationFile) throws IOException, URISyntaxException {
        URL url = new URI(imageUrl).toURL();
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(destinationFile);

        byte[] b = new byte[4096];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
    }
    private static String fetchPuppyPicture() {
        BufferedReader in = getBufferedReader();
        String inputLine;
        StringBuilder response = new StringBuilder();

        while (true) {
            try {
                if ((inputLine = in.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            response.append(inputLine);
        }
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String res = response.toString();
        int urlStartIndex = res.indexOf("\"message\":\"") + "\"message\":\"".length();
        int urlEndIndex = res.indexOf("\",\"status\":\"success\"", urlStartIndex);
        res = res.substring(urlStartIndex, urlEndIndex);

        return res.replaceAll("\\\\/", "/");
    }

    private static BufferedReader getBufferedReader() {
        URL url;
        try {
            url = new URI("https://dog.ceo/api/breeds/image/random").toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }

        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return in;
    }

    private static String fetchInspirationalQuote() throws URISyntaxException {
        try {
            URL url;
            url = new URI("https://zenquotes.io/api/random").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                return getQuote(connection);
            } else {
                System.out.println("Failed to retrieve data from the API. Response code: " + responseCode);
            }

            // Close the connection
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getQuote(HttpURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        // Extract the quote from the response string
        String quoteStartTag = "\"q\":\"";
        String quoteEndTag = "\",\"a\":";
        int startIndex = response.indexOf(quoteStartTag) + quoteStartTag.length();
        int endIndex = response.indexOf(quoteEndTag, startIndex);
        return response.substring(startIndex, endIndex);
    }

    private static void sendSMS(String message) {

        Email email = EmailBuilder.startingBlank()
                .from("sender email address")
                .to("phonenumber@examlplecarrier.com")
                .withSubject("")
                .withPlainText(message)
                .withEmbeddedImage("puppy.jpg", new FileDataSource("src/main/resources/puppy.jpg"))
                .buildEmail();

        MailerBuilder
                // here I use Google, and an app password for authentication. Google recommends OAUTH-2 authentication.
                .withSMTPServer("smtp.smtp server example.com", 587, "sender login", "authenticator")
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .buildMailer()
                .sendMail(email);
    }
}