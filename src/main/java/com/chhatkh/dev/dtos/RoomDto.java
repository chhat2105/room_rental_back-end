package com.chhatkh.dev.dtos;

public class RoomDto {

    private Long id;
    private String title;
    private String description;
    private Double pricePerMonth;
    private String location;
    private boolean available;

    public RoomDto() {
    }

    public RoomDto(Long id, String title, String description, Double pricePerMonth, String location, boolean available) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pricePerMonth = pricePerMonth;
        this.location = location;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPricePerMonth() {
        return pricePerMonth;
    }

    public void setPricePerMonth(Double pricePerMonth) {
        this.pricePerMonth = pricePerMonth;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
