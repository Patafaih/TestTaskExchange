import exceptions.NotPositiveDataInOrderException;
import org.junit.Assert;
import org.junit.Test;

import static junit.framework.TestCase.fail;


public class BookTest {
    @Test
    public void testRunAndToStringAddNewOrders() {
        Book book = new Book("ItBookName", true);
        try {
            book.inboundQueue.put(new Order(true, 148.4F, 89, 234));
            book.inboundQueue.put(new Order(false, 150.9F, 334, 235));
            book.inboundQueue.put(new Order(false, 151.9F, 32, 236));
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        } catch (NotPositiveDataInOrderException e) {
            e.printStackTrace();
            fail();
        }
        book.run();
        String exp = "Стакан: ItBookName" + "\n" + "Покупка - Продажа" + "\n" + "-----------------------" + "\n" + "Колво@Цена - Колво@Цена" + "\n" + "89@148.4 - 334@150.9" + "\n" + "-------- - 32@151.9" + "\n" + "\n" + "\n";
        Assert.assertEquals(exp, book.toString());
    }

    @Test
    public void testRunAndToStringMatchingOrdersNewOrderMore() {
        Book book = new Book("ItBookName", true);
        try {
            book.inboundQueue.put(new Order(true, 152.4F, 500, 234));
            book.inboundQueue.put(new Order(false, 150.9F, 334, 235));
            book.inboundQueue.put(new Order(false, 151.9F, 32, 236));
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        } catch (NotPositiveDataInOrderException e) {
            e.printStackTrace();
            fail();
        }
        book.run();
        String exp = "Стакан: ItBookName" + "\n" + "Покупка - Продажа" + "\n" + "-----------------------" + "\n" + "Колво@Цена - Колво@Цена" + "\n" + "134@152.4 - --------" + "\n" + "\n" + "\n";
        Assert.assertEquals(exp, book.toString());
    }

    @Test
    public void testRunAndToStringMatchingOrdersNewOrderLess() {
        Book book = new Book("ItBookName", true);
        try {
            book.inboundQueue.put(new Order(true, 152.4F, 12, 234));
            book.inboundQueue.put(new Order(false, 154.9F, 334, 235));
            book.inboundQueue.put(new Order(false, 151.9F, 32, 236));
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        } catch (NotPositiveDataInOrderException e) {
            e.printStackTrace();
            fail();
        }
        book.run();
        String exp = "Стакан: ItBookName" + "\n" + "Покупка - Продажа" + "\n" + "-----------------------" + "\n" + "Колво@Цена - Колво@Цена" + "\n" + "-------- - 20@151.9" + "\n" + "-------- - 334@154.9" + "\n" + "\n" + "\n";
        Assert.assertEquals(exp, book.toString());
    }

    @Test
    public void testRunAndToStringDeleteOrder() {
        Book book = new Book("ItBookName", true);
        try {
            book.inboundQueue.put(new Order(true, 148.4F, 89, 234));
            book.inboundQueue.put(new Order(false, 150.9F, 334, 235));
            book.inboundQueue.put(new Order(false, 151.9F, 32, 236));
            book.inboundQueue.put(new Order(235));
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        } catch (NotPositiveDataInOrderException e) {
            e.printStackTrace();
            fail();
        }
        book.run();
        String exp = "Стакан: ItBookName" + "\n" + "Покупка - Продажа" + "\n" + "-----------------------" + "\n" + "Колво@Цена - Колво@Цена" + "\n" + "89@148.4 - 32@151.9" + "\n" + "\n" + "\n";
        Assert.assertEquals(exp, book.toString());
    }

}