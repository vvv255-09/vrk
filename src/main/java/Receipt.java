public class Receipt {
    private int id;
    private int productId;
    private String productName;
    private int supplierId;
    private String supplierName;
    private int quantity;
    private String receiptDate;

    public Receipt(int id, int productId, String productName,
                   int supplierId, String supplierName,
                   int quantity, String receiptDate) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.quantity = quantity;
        this.receiptDate = receiptDate;
    }

    public int getId()             { return id; }
    public int getProductId()      { return productId; }
    public String getProductName() { return productName; }
    public int getSupplierId()     { return supplierId; }
    public String getSupplierName(){ return supplierName; }
    public int getQuantity()       { return quantity; }
    public String getReceiptDate() { return receiptDate; }
}