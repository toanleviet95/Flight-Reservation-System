package tripma.local.tripma.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;

@Entity
@Table(name = "package_booking")
public class PackageBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_booking_id")
    private Integer packageBookingId;
    @Column(name = "booking_id")
    private Integer bookingId;
    @Column(name = "package_id")
    private Integer packageId;

    public PackageBooking() {
    }

    public Integer getPackage_booking_id() {
        return this.packageBookingId;
    }

    public void setPackage_booking_id(Integer packageBookingId) {
        this.packageBookingId = packageBookingId;
    }

    public Integer getBooking_id() {
        return this.bookingId;
    }

    public void setBooking_id(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getPackage_id() {
        return this.packageId;
    }

    public void setPackage_id(Integer packageId) {
        this.packageId = packageId;
    }
}
