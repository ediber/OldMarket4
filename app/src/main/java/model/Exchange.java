package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Exchange extends BaseEntity implements Serializable {
    private String customerNo1;
    private String customerNo2;
    private String productId_1;
    private String productId_2;
    private long date;
    private boolean done_1;
    private boolean done_2;

    public Exchange() {
    }

    public String getCustomerNo1() {
        return customerNo1;
    }

    public void setCustomerNo1(String customerNo1) {
        this.customerNo1 = customerNo1;
    }

    public String getCustomerNo2() {
        return customerNo2;
    }

    public void setCustomerNo2(String customerNo2) {
        this.customerNo2 = customerNo2;
    }

    public String getProductId_1() {
        return productId_1;
    }

    public void setProductId_1(String productId_1) {
        this.productId_1 = productId_1;
    }

    public String getProductId_2() {
        return productId_2;
    }

    public void setProductId_2(String productId_2) {
        this.productId_2 = productId_2;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isDone_1() {
        return done_1;
    }

    public void setDone_1(boolean done_1) {
        this.done_1 = done_1;
    }

    public boolean isDone_2() {
        return done_2;
    }

    public void setDone_2(boolean done_2) {
        this.done_2 = done_2;
    }
}
