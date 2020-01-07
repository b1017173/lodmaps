package ac.fun.lodmaps;

import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

public class Spot {
    private String course;  // コーススポットの場合はコース名
    private double number;  // コーススポットの場合は順路・避難所の場合は海抜を代入
    private String name;    // スポット名
    private String category;    // スポットのカテゴリー
    private LatLng location;    // スポットの座標
    private int id; // 主キー

    public Spot(String course, double number, String name, String category, LatLng location, int id) {
        this.course = course;
        this.number = number;
        this.name = name;
        this.category = category;
        this.location = location;
        this.id = id;
    }

    /* スポット詳細の基本情報 */
    private ImageView imageViews[]; // 画像の配列
    private String description;    // 概要
    private String area;    // エリア
    private int postalCode; // 郵便番号
    private String address;  // 住所
    private String access;  // アクセス
    private int phoneNumber;    // 電話番号
    private String businessHours;   // 営業時間
    private String holiday; // 定休日
    private String parking; // 駐車場
    private String eatIn;   // イートイン
    private String url; // はこぶらへのリンク

    /* 以下スイーツスポットの場合のおすすめ商品情報 */
    private String sweetsName;  // 商品名
    private ImageView sweetsImage;  // 画像
    private String sweetsDescription;   // 説明
    private int sweetsPrice;    // 値段
    private String sweetsShop;  // 店舗名
    private String shopUrl;    // 店舗サイトへのリンク

    /* 以下映画ロケ地の場合の情報 */
    private String movieName;   // 映画名
    private ImageView movieImage;    // 画像
    private String movieDescription;    // 説明
    private String movieDirector;   // 監督
    private String movieActor;  // 役者
    private String movieUrl;    // 函館フィルムへのリンク

    /* 以下土木スポットの場合の情報 */
    private String civilName;   // 建設物名
    private ImageView civilImage;   // 画像
    private String civilDescription;    // 説明
    private int civilYear; // 竣工年
    private String civilCreator; // 製作者
    private String civilUrl;    // 近代化遺産へのリンク

    public String getCourse() {
        return this.course;
    }

    public double getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public LatLng getLocation() {
        return location;
    }

    public int getId() {
        return id;
    }

    public ImageView[] getImageViews() {
        return imageViews;
    }

    public String getDescription() {
        return description;
    }

    public String getArea() {
        return area;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public String getAddress() {
        return address;
    }

    public String getAccess() {
        return access;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public String getBusinessHours() {
        return businessHours;
    }

    public String getHoliday() {
        return holiday;
    }

    public String getParking() {
        return parking;
    }

    public String getEatIn() {
        return eatIn;
    }

    public String getUrl() {
        return url;
    }

    public String getSweetsName() {
        return sweetsName;
    }

    public ImageView getSweetsImage() {
        return sweetsImage;
    }

    public String getSweetsDescription() {
        return sweetsDescription;
    }

    public int getSweetsPrice() {
        return sweetsPrice;
    }

    public String getSweetsShop() {
        return sweetsShop;
    }

    public String getShopUrl() {
        return shopUrl;
    }

    public String getMovieName() {
        return movieName;
    }

    public ImageView getMovieImage() {
        return movieImage;
    }

    public String getMovieDescription() {
        return movieDescription;
    }

    public String getMovieDirector() {
        return movieDirector;
    }

    public String getMovieActor() {
        return movieActor;
    }

    public String getMovieUrl() {
        return movieUrl;
    }

    public String getCivilName() {
        return civilName;
    }

    public ImageView getCivilImage() {
        return civilImage;
    }

    public String getCivilDescription() {
        return civilDescription;
    }

    public int getCivilYear() {
        return civilYear;
    }

    public String getCivilCreator() {
        return civilCreator;
    }

    public String getCivilUrl() {
        return civilUrl;
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

    public void setImageViews(ImageView[] imageViews) {
        this.imageViews = imageViews;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public void setEatIn(String eatIn) {
        this.eatIn = eatIn;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSweetsName(String sweetsName) {
        this.sweetsName = sweetsName;
    }

    public void setSweetsImage(ImageView sweetsImage) {
        this.sweetsImage = sweetsImage;
    }

    public void setSweetsDescription(String sweetsDescription) {
        this.sweetsDescription = sweetsDescription;
    }

    public void setSweetsPrice(int sweetsPrice) {
        this.sweetsPrice = sweetsPrice;
    }

    public void setSweetsShop(String sweetsShop) {
        this.sweetsShop = sweetsShop;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public void setMovieImage(ImageView movieImage) {
        this.movieImage = movieImage;
    }

    public void setMovieDescription(String movieDescription) {
        this.movieDescription = movieDescription;
    }

    public void setMovieDirector(String movieDirector) {
        this.movieDirector = movieDirector;
    }

    public void setMovieActor(String movieActor) {
        this.movieActor = movieActor;
    }

    public void setMovieUrl(String movieUrl) {
        this.movieUrl = movieUrl;
    }

    public void setCivilName(String civilName) {
        this.civilName = civilName;
    }

    public void setCivilImage(ImageView civilImage) {
        this.civilImage = civilImage;
    }

    public void setCivilDescription(String civilDescription) {
        this.civilDescription = civilDescription;
    }

    public void setCivilYear(int civilYear) {
        this.civilYear = civilYear;
    }

    public void setCivilCreator(String civilCreator) {
        this.civilCreator = civilCreator;
    }

    public void setCivilUrl(String civilUrl) {
        this.civilUrl = civilUrl;
    }
}
