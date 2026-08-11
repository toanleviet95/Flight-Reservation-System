package tripma.local.tripma.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_booking") 
public class HotelBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hotel_booking_id")
    private Integer hotel_booking_id;

    @Column(name = "booking_id")
    private Integer booking_id;

    @Column(name = "hotel_id")
    private Integer hotel_id;
    
    @Column(name = "check_in")
    private LocalDateTime check_in;

    @Column(name = "check_out")
    private LocalDateTime check_out;
    
    @Column(name = "guests")
    private Integer guests;

    @Column(name = "total_price")
    private BigDecimal total_price;

    public HotelBooking() {
    }

    public Integer getHotel_booking_id() {
        return hotel_booking_id;
    }

    public void setHotel_booking_id(Integer hotel_booking_id) {
        this.hotel_booking_id = hotel_booking_id;
    }

    public Integer getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(Integer booking_id) {
        this.booking_id = booking_id;
    }

    public Integer getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(Integer hotel_id) {
        this.hotel_id = hotel_id;
    }

    public LocalDateTime getCheck_in() {
        return check_in;
    }

    public void setCheck_in(LocalDateTime check_in) {
        this.check_in = check_in;
    }

    public LocalDateTime getCheck_out() {
        return check_out;
    }

    public void setCheck_out(LocalDateTime check_out) {
        this.check_out = check_out;
    }

    public Integer getGuests() {
        return guests;
    }

    public void setGuests(Integer guests) {
        this.guests = guests;
    }

    public BigDecimal getTotal_price() {
        return total_price;
    }

    public void setTotal_price(BigDecimal total_price) {
        this.total_price = total_price;
    }
}

