package tripma.local.tripma.dto.Hotel;

import tripma.local.tripma.entity.Hotel;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record HotelResponse(
    Integer hotel_id,
    String name,
    String address,
    String city,
    String country,
    Integer rating,
    BigDecimal price_per_night,
    String description
) {
    public static HotelResponse fromEntity(Hotel hotel){
        return new HotelResponse(
            hotel.getHotel_id(),
            hotel.getName(),
            hotel.getAddress(),
            hotel.getCity(),
            hotel.getCountry(),
            hotel.getRating(),
            hotel.getPrice_per_night(),
            hotel.getDescription()
        );
    }
}