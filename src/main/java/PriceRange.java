import java.util.LinkedList;

public class PriceRange {
    private float priceRange;
    private LinkedList<Order> priceRangeQueue;

    public PriceRange(Float price) {
        this.priceRange = price;
        priceRangeQueue = new LinkedList<Order>();
    }

    public LinkedList<Order> getPriceRangeQueue() {
        return priceRangeQueue;
    }

    public void addOrder(Order order) {
        priceRangeQueue.addLast(order);
    }

    public Order getFirstOrder() {
        return priceRangeQueue.peekFirst();
    }

    public void removeFirstOrder() {
        priceRangeQueue.removeFirst();
    }

    public int getSizePriceRange() {
        return priceRangeQueue.size();
    }

    public void deleteOrderViaID(int orderID) {
        int index = searchIndexOrderViaID(orderID);

        if (index == -1) {
            return;
        }

        priceRangeQueue.remove(index);

    }

    private int searchIndexOrderViaID(int orderID) {
        for (int i = 0; i < priceRangeQueue.size(); i++) {
            if (orderID == priceRangeQueue.get(i).getOrderId()) {
                return i;
            }
        }
        return -1;

    }

    private int getCommonRangeValue() {
        int result = 0;
        for (int i = 0; i < priceRangeQueue.size(); i++) {
            result += priceRangeQueue.get(i).getVolume();
        }
        return result;
    }

    @Override
    public String toString() {
        return getCommonRangeValue() + "@" + priceRange;
    }
}
