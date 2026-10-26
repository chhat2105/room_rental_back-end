package com.chhatkh.dev.dtos;

public class DashboardStatsDto {

    private long totalRooms;
    private long availableRooms;
    private long rentedRooms;
    private long totalUsers;
    private long totalTenants;
    private long totalLandlords;
    private long totalBookings;
    private long pendingBookings;
    private long confirmedBookings;
    private double totalRevenue;

    public DashboardStatsDto() {
    }

    public long getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(long totalRooms) {
        this.totalRooms = totalRooms;
    }

    public long getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(long availableRooms) {
        this.availableRooms = availableRooms;
    }

    public long getRentedRooms() {
        return rentedRooms;
    }

    public void setRentedRooms(long rentedRooms) {
        this.rentedRooms = rentedRooms;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalTenants() {
        return totalTenants;
    }

    public void setTotalTenants(long totalTenants) {
        this.totalTenants = totalTenants;
    }

    public long getTotalLandlords() {
        return totalLandlords;
    }

    public void setTotalLandlords(long totalLandlords) {
        this.totalLandlords = totalLandlords;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public long getPendingBookings() {
        return pendingBookings;
    }

    public void setPendingBookings(long pendingBookings) {
        this.pendingBookings = pendingBookings;
    }

    public long getConfirmedBookings() {
        return confirmedBookings;
    }

    public void setConfirmedBookings(long confirmedBookings) {
        this.confirmedBookings = confirmedBookings;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
