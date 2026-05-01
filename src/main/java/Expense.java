public class Expense {
    private int id;
    private int productId;
    private String productName;
    private int quantity;
    private String expenseDate;
    private String reason;

    public Expense(int id, int productId, String productName,
                   int quantity, String expenseDate, String reason) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.expenseDate = expenseDate;
        this.reason = reason;
    }

    public int getId()              { return id; }
    public int getProductId()       { return productId; }
    public String getProductName()  { return productName; }
    public int getQuantity()        { return quantity; }
    public String getExpenseDate()  { return expenseDate; }
    public String getReason()       { return reason; }
}