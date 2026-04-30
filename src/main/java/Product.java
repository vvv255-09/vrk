public class Product {
    private int id;
    private String name;
    private String category;
    private String unit;
    private int quantity;
    private int minQuantity;
    private double price;

    public Product(int id, String name, String category, String unit, int quantity, int minQuantity, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.price = price;
    }

    public Product(String name, String category, String unit, int quantity, int minQuantity, double price) {
        this.name = name;
        this.category = category;
        this.unit = unit;
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getUnit() {
        return unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public double getPrice() {
        return price;
    }
    public void setName(String name)           { this.name = name; }
    public void setCategory(String category)   { this.category = category; }
    public void setUnit(String unit)           { this.unit = unit; }
    public void setMinQuantity(int minQty)     { this.minQuantity = minQty; }
    public void setPrice(double price)         { this.price = price; }

    @Override
    public String toString() {
        return "ID: " + id +
                ", Название: " + name +
                ", Категория: " + category +
                ", Ед.изм: " + unit +
                ", Кол-во: " + quantity +
                ", Мин.остаток: " + minQuantity +
                ", Цена: " + price;
    }
}