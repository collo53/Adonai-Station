import java.util.Date;
public class ItemsSold {
    private int id;
    private String stationName;
    private String cakeName;
    private int quantity;
    private Date updateTime;

    public ItemsSold(int id, String stationName, String cakeName, int quantity, Date updateTime) {
        this.id = id;
        this.stationName = stationName;
        this.cakeName = cakeName;
        this.quantity = quantity;
        this.updateTime = updateTime;
    }
    public ItemsSold(String cakeName, int quantity, Date updateTime) {
        this.cakeName = cakeName;
        this.quantity = quantity;
        this.updateTime = updateTime;
    }

    public int getId() {
        return id;
    }

    public String getStationName() {
        return stationName;
    }
    public String getCakeName() {
        return cakeName;
    }

    public int getQuantity() {
        return quantity;
    }

    public Date getUpdateTime() {
        return updateTime;
    }
}
