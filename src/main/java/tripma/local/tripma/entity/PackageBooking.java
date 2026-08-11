package tripma.local.tripma.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Table(name = "package_booking")
public class PackageBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer packageBookingId;

    private Integer bookingId;
    private Integer packageId;

    public PackageBooking() {
    }

    public Integer getPackageBookingId() {
        return this.packageBookingId;
    }

    public void setPackageBookingId(Integer packageBookingId) {
        this.packageBookingId = packageBookingId;
    }

    public Integer getBookingId() {
        return this.bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getPackageId() {
        return this.packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }
}
