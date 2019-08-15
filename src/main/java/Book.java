import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Book implements Runnable {

    private String bookName;
    private TreeMap<Float, PriceRange> buySide;
    private TreeMap<Float, PriceRange> sellSide;
    public LinkedBlockingQueue<Order> inboundQueue;
    public Thread bookThread;
    private TreeMap<Integer, Float> allActiveOrder;
    public boolean testMode;

    public Book(String bookName, boolean testMode) {
        this.bookName = bookName;
        this.buySide = new TreeMap<Float, PriceRange>(Collections.reverseOrder());
        this.sellSide = new TreeMap<Float, PriceRange>();
        this.inboundQueue = new LinkedBlockingQueue<Order>();
        this.bookThread = null;
        this.allActiveOrder = new TreeMap<Integer, Float>();
        this.testMode = testMode;
    }

    private Float getBestPrice(boolean isBUYside) {
        try {
            if (isBUYside) {
                return buySide.firstKey();
            } else {
                return sellSide.firstKey();
            }
        } catch (NoSuchElementException e) {
            return null;
        }

    }

    private PriceRange getPriceRange(boolean isBUYside, Float price) {
        if (isBUYside) {
            return buySide.get(price);
        } else {
            return sellSide.get(price);
        }
    }

    private void addPriceRange(boolean isBUYside, Float price) {
        if (isBUYside) {
            buySide.put(price, new PriceRange(price));
        } else {
            sellSide.put(price, new PriceRange(price));
        }
    }

    private int maxLinesForPrint() {
        if (buySide.size() > sellSide.size()) {
            return buySide.size();
        } else {
            return sellSide.size();
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        String initialText = "Стакан: " + bookName + "\n" + "Покупка - Продажа" + "\n" + "-----------------------" + "\n" + "Колво@Цена - Колво@Цена" + "\n";

        ArrayList<Float> allPricesBUY = new ArrayList<>();
        ArrayList<Float> allPricesSELL = new ArrayList<>();

        allPricesBUY.addAll(buySide.keySet());
        allPricesSELL.addAll(sellSide.keySet());

        if (maxLinesForPrint() == 0) {
            result.append("-------- - --------");
        } else {
            for (int i = 0; i < maxLinesForPrint(); i++) {
                if ((allPricesBUY.size()) <= i) {
                    result.append("-------- - ");
                } else {
                    result.append(getPriceRange(true, allPricesBUY.get(i)).toString() + " - ");
                }

                if ((allPricesSELL.size()) <= i) {
                    result.append("--------" + "\n");
                } else {
                    result.append(getPriceRange(false, allPricesSELL.get(i)).toString() + "\n");
                }
            }
        }

        result.append("\n" + "\n");

        return initialText + result.toString();
    }


    @Override
    public void run() {
        if (!testMode) {
            while (true) {
                if ((inboundQueue.peek() == null) && (!FileProcessor.threadFileProcessor.isAlive())) {
                    break;
                }
                readInboundQueue();
            }
        } else {
            while (true) {
                if (inboundQueue.peek() == null) {
                    break;
                }
                readInboundQueue();
            }
        }
    }

    private void readInboundQueue() {
        Order tempOrder = inboundQueue.poll();
        if (tempOrder == null) {
            return;
        }

        if (tempOrder.getIsCancelOrder()) {
            if (!allActiveOrder.containsKey(tempOrder.getOrderIDForCancel())) {
                return;
            }

            Float priceOrderForCancel = allActiveOrder.get(tempOrder.getOrderIDForCancel()); // getPrice

            if (buySide.containsKey(priceOrderForCancel)) {
                // buy side
                buySide.get(priceOrderForCancel).deleteOrderViaID(tempOrder.getOrderIDForCancel());
                if (buySide.get(priceOrderForCancel).getSizePriceRange() == 0) {
                    buySide.remove(priceOrderForCancel);
                }
                allActiveOrder.remove(tempOrder.getOrderIDForCancel());

            } else {
                // sell side
                sellSide.get(priceOrderForCancel).deleteOrderViaID(tempOrder.getOrderIDForCancel());
                if (sellSide.get(priceOrderForCancel).getSizePriceRange() == 0) {
                    sellSide.remove(priceOrderForCancel);
                }
                allActiveOrder.remove(tempOrder.getOrderIDForCancel());
            }


            return;
        }

        if (tempOrder.getIsBUYside()) {
            if ((getBestPrice(false) == null) || (getBestPrice(false) > tempOrder.getPrice())) {
                addNewOrder(tempOrder);
            } else {
                matchingOrders(tempOrder);
            }
        } else {
            if ((getBestPrice(true) == null) || getBestPrice(true) < tempOrder.getPrice()) {
                addNewOrder(tempOrder);
            } else {
                matchingOrders(tempOrder);
            }
        }
    }

    private void addNewOrder(Order order) {

        if (order.getIsBUYside() == true) {
            if (!buySide.containsKey(order.getPrice())) {
                addPriceRange(true, order.getPrice());
            }
            buySide.get(order.getPrice()).addOrder(order);
            allActiveOrder.put(order.getOrderId(), order.getPrice());
        } else {
            if (!sellSide.containsKey(order.getPrice())) {
                addPriceRange(false, order.getPrice());
            }
            sellSide.get(order.getPrice()).addOrder(order);
            allActiveOrder.put(order.getOrderId(), order.getPrice());
        }
    }

    private void matchingOrders(Order order) {
        Float priceNewOrder = order.getPrice();
        int volumeNewOrder = order.getVolume();

        if (order.getIsBUYside() == true) {
            outerBUY:
            while (true) {
                if ((sellSide.size() == 0) || (sellSide.firstKey() > priceNewOrder)) {
                    if (volumeNewOrder > 0) {
                        addNewOrder(order);
                    }
                    break outerBUY;
                }
                innerBUY:
                while (true) {
                    Order oldOrder = sellSide.get(sellSide.firstKey()).getFirstOrder();
                    if (oldOrder == null) {
                        sellSide.remove(sellSide.firstKey());
                        continue outerBUY;
                    }
                    if (volumeNewOrder < oldOrder.getVolume()) {
                        oldOrder.setVolume(oldOrder.getVolume() - volumeNewOrder);
                        break outerBUY;
                    }
                    if (volumeNewOrder > oldOrder.getVolume()) {
                        volumeNewOrder = volumeNewOrder - oldOrder.getVolume();
                        order.setVolume(volumeNewOrder);
                        allActiveOrder.remove(oldOrder.getOrderId());
                        sellSide.get(sellSide.firstKey()).removeFirstOrder();
                        continue innerBUY;
                    }
                    if (volumeNewOrder == oldOrder.getVolume()) {
                        volumeNewOrder = 0;
                        allActiveOrder.remove(oldOrder.getOrderId());
                        sellSide.get(sellSide.firstKey()).removeFirstOrder();
                        continue innerBUY;
                    }
                }
            }
        } else {
            outerSELL:
            while (true) {
                if ((buySide.size() == 0) || (buySide.firstKey() < priceNewOrder)) {
                    if (volumeNewOrder > 0) {
                        addNewOrder(order);
                    }
                    break outerSELL;
                }
                innerSELL:
                while (true) {
                    Order oldOrder = buySide.get(buySide.firstKey()).getFirstOrder();
                    if (oldOrder == null) {
                        buySide.remove(buySide.firstKey());
                        continue outerSELL;
                    }
                    if (volumeNewOrder < oldOrder.getVolume()) {
                        oldOrder.setVolume(oldOrder.getVolume() - volumeNewOrder);
                        break outerSELL;
                    }
                    if (volumeNewOrder > oldOrder.getVolume()) {
                        volumeNewOrder = volumeNewOrder - oldOrder.getVolume();
                        order.setVolume(volumeNewOrder);
                        allActiveOrder.remove(oldOrder.getOrderId());
                        buySide.get(buySide.firstKey()).removeFirstOrder();
                        continue innerSELL;
                    }
                    if (volumeNewOrder == oldOrder.getVolume()) {
                        volumeNewOrder = 0;
                        allActiveOrder.remove(oldOrder.getOrderId());
                        buySide.get(buySide.firstKey()).removeFirstOrder();
                        continue innerSELL;
                    }
                }
            }
        }

    }
}
