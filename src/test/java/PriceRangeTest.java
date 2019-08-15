import exceptions.NotPositiveDataInOrderException;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;

import static junit.framework.TestCase.fail;

public class PriceRangeTest {
    @Test
    public void testAddOrder() {
        PriceRange priceRange = new PriceRange(150.4F);
        try {
            priceRange.addOrder(new Order(124));
        } catch (NotPositiveDataInOrderException e) {
            fail();
        }

        Assert.assertEquals(1, priceRange.getPriceRangeQueue().size());
    }

    @Test
    public void testGetFirstOrder() {
        PriceRange priceRange = new PriceRange(150.4F);
        try {
            priceRange.getPriceRangeQueue().addLast(new Order(11));
            priceRange.getPriceRangeQueue().addLast(new Order(22));
        } catch (NotPositiveDataInOrderException e) {
            fail();
        }
        Assert.assertEquals(11, priceRange.getFirstOrder().getOrderIDForCancel());
    }

    @Test
    public void testRemoveFirstOrder() {
        PriceRange priceRange = new PriceRange(250.4F);
        try {
            priceRange.getPriceRangeQueue().addLast(new Order(33));
            priceRange.getPriceRangeQueue().addLast(new Order(44));
            priceRange.getPriceRangeQueue().addLast(new Order(55));
            priceRange.removeFirstOrder();
        } catch (NotPositiveDataInOrderException e) {
            fail();
        }
        Assert.assertEquals(2, priceRange.getPriceRangeQueue().size());
        Assert.assertEquals(44, priceRange.getPriceRangeQueue().getFirst().getOrderIDForCancel());
    }

    @Test
    public void testGetSizePriceRange() {
        PriceRange priceRange = new PriceRange(250.4F);
        int a = priceRange.getSizePriceRange();
        try {
            priceRange.getPriceRangeQueue().addLast(new Order(66));
        } catch (NotPositiveDataInOrderException e) {
            fail();
        }
        int b = priceRange.getSizePriceRange();
        Assert.assertEquals(a, 0);
        Assert.assertEquals(b, 1);
    }

    @Test
    public void testDeleteOrderViaID() {
        PriceRange priceRange = new PriceRange(350.4F);
        try {
            priceRange.getPriceRangeQueue().addLast(new Order(true, 133.2F, 40, 101));
            priceRange.getPriceRangeQueue().addLast(new Order(true, 133.2F, 40, 102));
            priceRange.getPriceRangeQueue().addLast(new Order(true, 133.2F, 40, 103));
        } catch (NotPositiveDataInOrderException e) {
            fail();
        }
        int a = priceRange.getPriceRangeQueue().size();
        priceRange.deleteOrderViaID(102);
        int b = priceRange.getPriceRangeQueue().size();

        Assert.assertEquals(a, 3);
        Assert.assertEquals(b, 2);
        Assert.assertEquals(101, priceRange.getPriceRangeQueue().getFirst().getOrderId());
        priceRange.getPriceRangeQueue().removeFirst();
        Assert.assertEquals(103, priceRange.getPriceRangeQueue().getFirst().getOrderId());

    }

    @Test
    public void testToString(){
        PriceRange priceRange = new PriceRange(777.7F);
        try {
            priceRange.getPriceRangeQueue().addLast(new Order(true, 134.2F, 40, 104));
            priceRange.getPriceRangeQueue().addLast(new Order(true, 134.2F, 40, 105));
            priceRange.getPriceRangeQueue().addLast(new Order(true, 134.2F, 40, 106));
        } catch (NotPositiveDataInOrderException e) {
            fail();
        }
        Assert.assertEquals("120@777.7", priceRange.toString());
    }

}