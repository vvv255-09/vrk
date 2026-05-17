public class Order {
    private int id;
    private int userId;
    private String status;
    private double totalPrice;
    private String orderDate;

    public Order(int id, int userId, String status, double totalPrice, String orderDate) {
        this.id = id;
        this.userId = userId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
    }

    public int getId()            { return id; }
    public int getUserId()        { return userId; }
    public String getStatus()     { return status; }
    public double getTotalPrice() { return totalPrice; }
    public String getOrderDate()  { return orderDate; }
    public void setStatus(String status) { this.status = status; }
}