import java.util.Date;

public class Booking {
    private int id;
    private String bookerName;
    private String status;
    private String cakeName;
    private int quantity;
    private Date dateBooked;
    private Date dateCleared;
    private double amountPaid;
    private double amountLeft;
    private String station;

    // Constructor
    public Booking(int id, String bookerName, String status, String cakeName, int quantity,
                   Date dateBooked, Date dateCleared, double amountPaid, double amountLeft, String station) {
        this.id = id;
        this.bookerName = bookerName;
        this.status = status;
        this.cakeName = cakeName;
        this.quantity = quantity;
        this.dateBooked = dateBooked;
        this.dateCleared = dateCleared;
        this.amountPaid = amountPaid;
        this.amountLeft = amountLeft;
        this.station = station;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookerName() {
        return bookerName;
    }

    public void setBookerName(String bookerName) {
        this.bookerName = bookerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCakeName() {
        return cakeName;
    }

    public void setCakeName(String cakeName) {
        this.cakeName = cakeName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getDateBooked() {
        return dateBooked;
    }

    public void setDateBooked(Date dateBooked) {
        this.dateBooked = dateBooked;
    }

    public Date getDateCleared() {
        return dateCleared;
    }

    public void setDateCleared(Date dateCleared) {
        this.dateCleared = dateCleared;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getAmountLeft() {
        return amountLeft;
    }

    public void setAmountLeft(double amountLeft) {
        this.amountLeft = amountLeft;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", bookerName='" + bookerName + '\'' +
                ", status='" + status + '\'' +
                ", cakeName='" + cakeName + '\'' +
                ", quantity=" + quantity +
                ", dateBooked=" + dateBooked +
                ", dateCleared=" + dateCleared +
                ", amountPaid=" + amountPaid +
                ", amountLeft=" + amountLeft +
                ", station='" + station + '\'' +
                '}';
    }
}
