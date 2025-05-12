import java.util.Date;

public class CakeDistributed {
    private int id;
    private String stationName;
    private String cakeName;
    private int quantity;
    private Date distributionDate;

    public CakeDistributed(int id, String stationName, String cakeName, int quantity, Date distributionDate) {
        this.id = id;
        this.stationName = stationName;
        this.cakeName = cakeName;
        this.quantity = quantity;
        this.distributionDate = distributionDate;
    }
    public CakeDistributed(String cakeName, int quantity, Date distributionDate) {
        this.cakeName = cakeName;
        this.quantity = quantity;
        this.distributionDate = distributionDate;
    }

    // Getters and setters

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

    public Date getDistributionDate() {
        return distributionDate;
    }
}

