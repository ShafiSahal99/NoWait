package MyObjects;

public class Hotel {
    private String hotelName;
    private int chairCount;

    public Hotel(){}

    public Hotel(String hotelName, int chaiCount){
        this.hotelName = hotelName;
        this.chairCount = chaiCount;

    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public int getChairCount() {
        return chairCount;
    }

    public void setChairCount(int chairCount) {
        this.chairCount = chairCount;
    }
}
