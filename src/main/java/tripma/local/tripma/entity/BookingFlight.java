package tripma.local.tripma.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "booking_flight")
public class BookingFlight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_flight_id")
    private Integer bookingFlightId;

    @Column(name = "booking_id")
    private Integer bookingId;

    @Column(name = "flight_id")
    private Integer flightId;

    @Column(name = "direction")
    private String direction;

    @Column(name = "cabin_class")
    private String cabinClass;

    @Column(name = "price")
    private BigDecimal price;

    public BookingFlight() {
    }

    public Integer getBookingFlightId() {
        return bookingFlightId;
    }

    public void setBookingFlightId(Integer bookingFlightId) {
        this.bookingFlightId = bookingFlightId;
    }

    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getFlightId() {
        return flightId;
    }

    public void setFlightId(Integer flightId) {
        this.flightId = flightId;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getCabinClass() {
        return cabinClass;
    }

    public void setCabinClass(String cabinClass) {
        this.cabinClass = cabinClass;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}