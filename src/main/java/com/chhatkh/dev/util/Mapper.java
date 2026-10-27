package com.chhatkh.dev.util;

import com.chhatkh.dev.models.*;
import com.chhatkh.dev.payload.response.RentHubRes.*;

public final class Mapper {
    private Mapper() {}

    public static String userName(User u) { return u.getUsername(); }

    public static AmenityRes amenity(Amenity a) { return new AmenityRes(a.getId(), a.getName()); }

    public static RoomRes room(Room r) {
        return new RoomRes(r.getId(), r.getProperty().getId(), r.getRoomNumber(), r.getBedrooms(),
                r.getBathrooms(), r.getSize(), r.getPrice(), r.getStatus());
    }

    public static PropertyRes property(Property p) {
        return new PropertyRes(p.getId(), p.getLandlord().getId(), userName(p.getLandlord()), p.getTitle(),
                p.getDescription(), p.getPropertyType(), p.getAddress(), p.getLatitude(), p.getLongitude(),
                p.getPrice(), p.getStatus(), p.getCreatedAt(),
                p.getImages().stream().map(i -> new ImageRes(i.getId(), i.getImageUrl())).toList(),
                p.getAmenities().stream().map(Mapper::amenity).toList(),
                p.getRooms().stream().map(Mapper::room).toList());
    }

    public static ViewingRes viewing(ViewingRequest v) {
        return new ViewingRes(v.getId(), v.getTenant().getId(), userName(v.getTenant()), v.getProperty().getId(),
                v.getProperty().getTitle(), v.getDate(), v.getTime(), v.getStatus());
    }

    public static ApplicationRes application(RentalApplication a) {
        return new ApplicationRes(a.getId(), a.getTenant().getId(), userName(a.getTenant()), a.getProperty().getId(),
                a.getProperty().getTitle(), a.getRoom() == null ? null : a.getRoom().getId(),
                a.getStartDate(), a.getEndDate(), a.getStatus());
    }

    public static ContractRes contract(RentalContract c) {
        RentalApplication a = c.getApplication();
        return new ContractRes(c.getId(), a.getId(), a.getProperty().getId(), a.getProperty().getTitle(),
                a.getTenant().getId(), userName(a.getTenant()), c.getRent(), c.getDeposit(),
                c.getStartDate(), c.getEndDate(), c.getStatus());
    }

    public static PaymentRes payment(Payment p) {
        return new PaymentRes(p.getId(), p.getContract().getId(), p.getType(), p.getAmount(), p.getDueDate(),
                p.getPaymentDate(), p.getPaymentMethod(), p.getStatus());
    }

    public static ReviewRes review(Review r) {
        return new ReviewRes(r.getId(), r.getTenant().getId(), userName(r.getTenant()), r.getProperty().getId(),
                r.getRating(), r.getComment());
    }

    public static ChatMessageRes message(Message m) {
        return new ChatMessageRes(m.getId(), m.getSender().getId(), m.getReceiver().getId(), m.getMessage(), m.getCreatedAt());
    }

    public static ComplaintRes complaint(Complaint c) {
        return new ComplaintRes(c.getId(), c.getTenant().getId(), userName(c.getTenant()), c.getProperty().getId(),
                c.getProperty().getTitle(), c.getDescription(), c.getStatus());
    }
}
