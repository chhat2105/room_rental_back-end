package com.chhatkh.dev.services;

import com.chhatkh.dev.dtos.DashboardStatsDto;
import com.chhatkh.dev.entities.BookingStatus;
import com.chhatkh.dev.entities.Role;
import com.chhatkh.dev.repositories.BookingRepository;
import com.chhatkh.dev.repositories.RoomRepository;
import com.chhatkh.dev.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public DashboardService(RoomRepository roomRepository,
                            UserRepository userRepository,
                            BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        long totalRooms = roomRepository.count();
        long availableRooms = roomRepository.countByAvailableTrue();
        long rentedRooms = roomRepository.countByAvailableFalse();

        stats.setTotalRooms(totalRooms);
        stats.setAvailableRooms(availableRooms);
        stats.setRentedRooms(rentedRooms);

        long totalUsers = userRepository.count();
        long totalTenants = userRepository.countByRole(Role.ROLE_TENANT);
        long totalLandlords = userRepository.countByRole(Role.ROLE_LANDLORD);

        stats.setTotalUsers(totalUsers);
        stats.setTotalTenants(totalTenants);
        stats.setTotalLandlords(totalLandlords);

        long totalBookings = bookingRepository.count();
        long pendingBookings = bookingRepository.countByStatus(BookingStatus.PENDING);
        long confirmedBookings = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        Double totalRevenue = bookingRepository.calculateTotalRevenue();

        stats.setTotalBookings(totalBookings);
        stats.setPendingBookings(pendingBookings);
        stats.setConfirmedBookings(confirmedBookings);
        stats.setTotalRevenue(totalRevenue != null ? Math.round(totalRevenue * 100.0) / 100.0 : 0.0);

        return stats;
    }
}
