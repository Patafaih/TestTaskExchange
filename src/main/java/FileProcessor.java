import exceptions.NotPositiveDataInOrderException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.Properties;

public class FileProcessor implements Runnable {
    private BufferedReader bufReader;
    public HashMap<String, Book> allBooks = new HashMap<String, Book>();
    public static Thread threadFileProcessor;


    public FileProcessor(String path) throws IOException {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream("src/main/resources/config.properties");
        properties.load(fileInputStream);
        File file = new File(properties.getProperty(path));
        bufReader = new BufferedReader(new FileReader(file));
    }


    @Override
    public void run() {
        try {
            readFileAndParseOrder();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error with read and parse file! Thread is stopped!");
        }
    }

    private void readFileAndParseOrder() throws IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource inputSource;
        Document document = null;
        Element orderElement;

        String initialText;
        while ((initialText = bufReader.readLine()) != null) {
            if (initialText.contains("AddOrder")) {
                inputSource = new InputSource(new StringReader(initialText));
                try {
                    document = builder.parse(inputSource);
                } catch (SAXException eSAX) {
                    eSAX.printStackTrace();
                    System.out.println("Error with xml format! Please check this line - " + initialText);
                    continue;
                }
                orderElement = document.getDocumentElement();
                String bookName = orderElement.getAttribute("book");

                boolean isBUYside;
                if (orderElement.getAttribute("operation").equals("BUY")) {
                    isBUYside = true;
                } else if (orderElement.getAttribute("operation").equals("SELL")) {
                    isBUYside = false;
                } else {
                    System.out.println("Invalid Order! - " + initialText);
                    continue;
                }

                float price;
                int volume;
                int orderId;
                try {
                    price = Float.parseFloat(orderElement.getAttribute("price"));
                    volume = Integer.parseInt(orderElement.getAttribute("volume"));
                    orderId = Integer.parseInt(orderElement.getAttribute("orderId"));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Invalid Order! - " + initialText);
                    continue;
                }

                try {
                    addOrderToInboundQueueForBook(bookName, new Order(isBUYside, price, volume, orderId));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("Error with add new order to inbound Queue! Please check this order - " + initialText);
                    continue;
                } catch (NotPositiveDataInOrderException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    continue;
                }
                continue;
            }

            if (initialText.contains("DeleteOrder")) {
                inputSource = new InputSource(new StringReader(initialText));
                try {
                    document = builder.parse(inputSource);
                } catch (SAXException eSAX) {
                    eSAX.printStackTrace();
                    System.out.println("Error with xml format! Please check this line - " + initialText);
                    continue;
                }
                orderElement = document.getDocumentElement();
                String bookName = orderElement.getAttribute("book");
                int orderId;
                try {
                    orderId = Integer.parseInt(orderElement.getAttribute("orderId"));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Invalid Order! - " + initialText);
                    continue;
                }

                try {
                    addOrderToInboundQueueForBook(bookName, new Order(orderId));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("Error with add new order to inbound Queue! Please check this line - " + initialText);
                    continue;
                } catch (NotPositiveDataInOrderException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    continue;
                }
                continue;
            }

            if (initialText.contains("xml version")) {
                continue;
            }

            if (initialText.contains("<Orders>")) {
                continue;
            }

            if (initialText.contains("</Orders>")) {
                break;
            }

            if (initialText.equals("")) {
                continue;
            }

            System.out.println("Error! Invalid Data! Please check this line - " + initialText);

        }
        bufReader.close();
    }

    private void addOrderToInboundQueueForBook(String bookName, Order order) throws InterruptedException {
        if (!allBooks.containsKey(bookName)) {
            Book book = new Book(bookName, false);
            allBooks.put(bookName, book);
            book.bookThread = new Thread(book);
            book.bookThread.start();
        }
        allBooks.get(bookName).inboundQueue.put(order);

    }

}
