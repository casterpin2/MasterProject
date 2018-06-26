package project.view.ProductInStore;

import java.util.ArrayList;

public class ProductInStore {
    private int productID;
    private String productName;
    private String productImage;
    private String categoryName;
    private String brandName;
    private long productPrice;
    private double promotionPercent;

    public ProductInStore (){}

    public ProductInStore( int productID, String productName, String productImage, String categoryName, String brandName, long productPrice, double promotionPercent) {
        this.productID = productID;
        this.productName = productName;
        this.productImage = productImage;
        this.categoryName = categoryName;
        this.brandName = brandName;
        this.productPrice = productPrice;
        this.promotionPercent = promotionPercent;
    }



    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }

    public double getPromotionPercent() {
        return promotionPercent;
    }

    public void setPromotionPercent(double promotionPercent) {
        this.promotionPercent = promotionPercent;
    }

    public static ArrayList<ProductInStore> getExample() {
        ArrayList<ProductInStore> productList = new ArrayList<>();
        productList.add(new ProductInStore(1,"Đồ ăn thức uống, rất nhiều thực phẩm chức năng 128Gb màu bạc xám nhưng lại rất là to con và địt mẹ, quan trong jalf ... cho bố m cái","haha.png","đồ ăn","hải hà",134000,1.2));
        productList.add(new ProductInStore(2,"ahahah","haha.png","đồ ăn","hải hà",134000,1.2));
        productList.add(new ProductInStore(3,"đâsdsadasdas","haha.png","đồ ăn","hải hà",134000,1.2));
        productList.add(new ProductInStore(4,"đâsdasdas","haha.png","đồ ăn","hải hà",134000,1.2));
        productList.add(new ProductInStore(5,"đâsdasdas","haha.png","đồ ăn","hải hà",134000,1.2));
        productList.add(new ProductInStore(6,"đâsdasdas","haha.png","đồ ăn","hải hà",134000,1.2));
        return productList;




    }
}
