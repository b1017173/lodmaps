package ac.fun.lodmaps;

import com.google.android.gms.maps.model.LatLng;

public class Spot {
    private String course;
    private double number;  // コーススポットの場合は順路・避難所の場合は海抜を代入
    private String name;
    private String category;
    private LatLng location;
    private int id;

    public Spot(String course, double number, String name, String category, LatLng location, int id) {
        this.course = course;
        this.number = number;
        this.name = name;
        this.category = category;
        this.location = location;
        this.id = id;
    }

    public String getCourse() {
        return this.course;
    }
    public double getNumber() {
        return this.number;
    }
    public String getName() {
        return this.name;
    }
    public String getCategory() {
        return this.category;
    }
    public LatLng getLocation() {
        return this.location;
    }
    public int getId() {
        return this.id;
    }

    public void setCourse(String course) {
        this.course = course;
    }
    public void setNumber(double number) {
        this.number = number;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setLocation(LatLng location) {
        this.location = location;
    }
    public void setId(int id) {
        this.id = id;
    }
}
