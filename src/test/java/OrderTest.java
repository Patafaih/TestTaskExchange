import exceptions.NotPositiveDataInOrderException;
import org.junit.Assert;
import org.junit.Test;
import static junit.framework.TestCase.fail;

public class OrderTest {
    @Test
    public  void testCreateNewOrderValidData() {
        Order order = null;
        try {
            order = new Order(true, 133.2F, 40, 1234);
        } catch (NotPositiveDataInOrderException e) {
            fail();
        }
        Assert.assertEquals(order.getIsBUYside(), true);
        Assert.assertEquals(order.getPrice(), 133.2F, 0.001F);
        Assert.assertEquals(order.getVolume(), 40);
        Assert.assertEquals(order.getOrderId(), 1234);
        Assert.assertEquals(order.getIsCancelOrder(), false);
        Assert.assertEquals(order.getOrderIDForCancel(), 0);
    }

    @Test
    public  void testCreateCancelOrderValidData() {
        Order order = null;
        try {
            order = new Order(1234);
        } catch (NotPositiveDataInOrderException e) {
            fail();
        }
        Assert.assertEquals(order.getIsCancelOrder(), true);
        Assert.assertEquals(order.getOrderIDForCancel(), 1234);
    }

    @Test
    public  void testCreateNewOrderInvalidDataPriceNegative() {
        Order order = null;
        try {
            order = new Order(false, -1.34F, 405, 9999999);
        } catch (NotPositiveDataInOrderException e) {
            return; // passed
        }
        fail();
    }

    @Test
    public  void testCreateNewOrderInvalidDataPriceZero() {
        Order order = null;
        try {
            order = new Order(false, 0F, 405, 9999999);
        } catch (NotPositiveDataInOrderException e) {
            return; // passed
        }
        fail();
    }

    @Test
    public  void testCreateNewOrderInvalidDataVolumeNegative() {
        Order order = null;
        try {
            order = new Order(false, 150.3F, -12347, 9999999);
        } catch (NotPositiveDataInOrderException e) {
            return; // passed
        }
        fail();
    }

    @Test
    public  void testCreateNewOrderInvalidDataVolumeZero() {
        Order order = null;
        try {
            order = new Order(false, 150.3F, 0, 9999999);
        } catch (NotPositiveDataInOrderException e) {
            return; // passed
        }
        fail();
    }

    @Test
    public  void testCreateNewOrderInvalidDataOrderIdNegative() {
        Order order = null;
        try {
            order = new Order(false, 150.3F, 59, -254);
        } catch (NotPositiveDataInOrderException e) {
            return; // passed
        }
        fail();
    }

    @Test
    public  void testCreateNewOrderInvalidDataOrderIdZero() {
        Order order = null;
        try {
            order = new Order(false, 150.3F, 59, 0);
        } catch (NotPositiveDataInOrderException e) {
            return; // passed
        }
        fail();
    }

    @Test
    public  void testCreateCancelOrderInvalidDataOrderIdNegative() {
        Order order = null;
        try {
            order = new Order(-34);
        } catch (NotPositiveDataInOrderException e) {
            return; // passed
        }
       fail();
    }

    @Test
    public  void testCreateCancelOrderInvalidDataOrderIdZero() {
        Order order = null;
        try {
            order = new Order(0);
        } catch (NotPositiveDataInOrderException e) {
            return; // passed
        }
        fail();
    }

}