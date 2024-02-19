package pupPledge;

import jakarta.activation.FileDataSource;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import java.time.DayOfWeek;
import java.time.LocalDate;

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
        String message = generatePreText() + quote+"\"" + " Hope your day goes well!";
        sendSMS(message);


    }

    public static String generatePreText () {
        String day = getDay();
        String[] greetings = new String[]{"Good morning to you! I hope you have a wonderful day ahead filled with joy and success.",
                "Morning! Wishing you a bright and cheerful start to your day, with all the energy and positivity you need.",
                "Top of the morning to you! May your day be as radiant as the sunrise, and may you find happiness in every moment.",
                "Greetings! I hope this morning brings you a sense of peace and purpose, setting the tone for a fantastic day.",
                "Hello, morning! May the gentle light of dawn inspire you and fill your heart with optimism and enthusiasm.",
                "Bright and early! Here's to a fresh start and a productive day ahead, with opportunities waiting to be seized.",
                "Rise and shine! It's time to embrace the new day with a smile, ready to conquer challenges and savor successes.",
                "Good day to you! May this morning be the beginning of a truly great day, brimming with positivity and fulfillment.",
                "Happy morning! I hope you feel the warmth of the sun on your face and carry that happiness throughout the day.",
                "Good morning, my friend! I wish you a beautiful start to the day, setting the stage for wonderful experiences and achievements."};
        int rand = (int) (Math.random() * 9);
        String todayGreeting = greetings[rand];
        String[] dayText = new String[] {
                "Today is a Marvelous Monday, ",
                "Today is a Terrific Tuesday, ",
                "Today is a Wonderful Wednesday, ",
                "Today is a Thrilling Thursday, ",
                "Today is a Fantastic Friday, ",
                "Today is a Sensational Saturday, ",
                "Today is a Serene Sunday, "};
        String[] dogNames = {
                "Biscuit", "Daisy", "Fido", "Max", "Bella", "Charlie", "Luna", "Bailey", "Rosie", "Cooper",
                "Sadie", "Toby", "Molly", "Buddy", "Lucy", "Rocky", "Lola", "Jack", "Maggie", "Oliver",
                "Sophie", "Bear", "Chloe", "Duke", "Lily", "Harley", "Ruby", "Zeus", "Zoe", "Winston",
                "Gracie", "Rusty", "Ellie", "Oscar", "Millie", "Sam", "Penny", "Murphy", "Coco", "Gizmo",
                "Annie", "Teddy", "Holly", "Simba", "Sasha", "Riley", "Mia", "Louie", "Pepper", "Marley",
                "Nala", "Maximus", "Mocha", "Scout", "Phoebe", "Bruno", "Cinnamon", "Shadow", "Piper",
                "Ginger", "Hunter", "Misty", "Apollo", "Daisy Mae", "Jasper", "Muffin", "Rex", "Dolly",
                "Thor", "Lucky", "Ziggy", "Honey", "Baxter", "Snickers", "Ace", "Cupcake", "Bandit",
                "Cleo", "Ranger", "Sugar", "Otis", "Nova", "Brutus", "Candy", "Boomer", "Trixie",
                "Blue", "Pixie", "Hazel", "Frankie", "Sunny", "Lulu", "Copper"
        };
        int rand2 = (int) (Math.random() * 100);
        String puppyText = "and my great friend " + dogNames[rand2] + " has an inspiring quote for us: \"";
        String todayText = switch (day.toLowerCase()) {
            case "monday" -> dayText[0];
            case "tuesday" -> dayText[1];
            case "wednesday" -> dayText[2];
            case "thursday" -> dayText[3];
            case "friday" -> dayText[4];
            case "saturday" -> dayText[5];
            case "sunday" -> dayText[6];
            default -> "Invalid day";
        };

        return todayGreeting + " " + todayText + puppyText;
    }

    public static String getDay() {
        LocalDate date = LocalDate.now();
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek.toString();
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
                .from("youremail@email.com")
                .to("phone#1@recepient's carrier", "phone#2@recepient's carrier")
                .withSubject("")
                .withPlainText(message)
                .withEmbeddedImage("puppy.jpg", new FileDataSource("src/main/resources/puppy.jpg"))
                .buildEmail();

        MailerBuilder
                .withSMTPServer("smtp.your smtp sever", 587, "host email", "host password")
                .withTransportStrategy(TransportStrategy.SMTP_TLS)
                .buildMailer()
                .sendMail(email);
    }
}