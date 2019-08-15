import exceptions.NotPositiveDataInOrderException;

public class Order {
    private boolean isCancelOrder;
    private boolean isBUYside;
    private float price;
    private int volume;
    private int orderId;
    private int orderIDForCancel;

    public Order(boolean isBUYside, float price, int volume, int orderId) throws NotPositiveDataInOrderException {
        if ((price <= 0) || (volume <=0) || (orderId <= 0)) {
            throw new NotPositiveDataInOrderException("price, volume and orderID should be positive! Price = " + price + " Volume = " + volume + " OrderID = " + orderId);
        }
        this.isBUYside = isBUYside;
        this.price = price;
        this.volume = volume;
        this.orderId = orderId;
    }

    public Order (int orderId) throws NotPositiveDataInOrderException {
        if (orderId <= 0) {
            throw new NotPositiveDataInOrderException("OrderID should be positive! OrderID = " + orderId);
        }
        this.isCancelOrder = true;
        this.orderIDForCancel = orderId;

    }

    public boolean getIsCancelOrder() {
        return isCancelOrder;
    }

    public boolean getIsBUYside() {
        return isBUYside;
    }

    public float getPrice() {
        return price;
    }

    public int getVolume() {
        return volume;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setVolume(int volume){
        this.volume = volume;
    }

    public int getOrderIDForCancel() {
        return orderIDForCancel;
    }

}
