package tripma.local.tripma.dto.Hotel;

import java.math.BigDecimal;

public record HotelRequest(
        String name,
        String address,
        String city,
        String country,
        Integer rating,
        BigDecimal price_per_night,
        String description) {
}
