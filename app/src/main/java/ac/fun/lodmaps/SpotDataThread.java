package ac.fun.lodmaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

/* スポットの詳細情報を得る */
public class SpotDataThread extends MapsActivity implements Runnable {
    private String spotName;
    private String spotCategory;
    private String spotTag;
    private Spot spot;

    SpotDataThread(String spotName, String spotCategory, String spotTag) {
        this.spotName = spotName;
        this.spotCategory = spotCategory;
        this.spotTag = spotTag;
    }

    public void run() {
        // スポット情報をクエリに埋め込むためにエンコードする
        String encoded_spot = "";   // エンコードされたスポットURL
        String query_url = "";  // ↑を組み込んだクエリ
        try {
            // スイーツ情報の場合はタグ，その他なら名前から情報を取得する
            encoded_spot = spotCategory.equals("函館スイーツ")? URLEncoder.encode(spotTag,"UTF-8"):URLEncoder.encode(spotName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /* SPARQLのクエリを準備 */
        if (spotCategory.equals("函館スイーツ")) {
            // 検索用：http://lod.per.c.fun.ac.jp:8080/sparql?default-graph-uri=&query=PREFIX+geo%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2003%2F01%2Fgeo%2Fwgs84_pos%23%3E%0D%0APREFIX+sweets%3A+%3Chttp%3A%2F%2Flod.fun.ac.jp%2Fhakobura%2Fterms%2Fsweet%23%3E%0D%0APREFIX+dc%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0D%0APREFIX+schema%3A+%3Chttp%3A%2F%2Fschema.org%2F%3E%0D%0A%0D%0Aselect+distinct+%3Fprimarykey+%3Fshopname+%3Fshopimage+%3Fshopdescription+%3Farea+%3Fmodified++%3Fclosed+%3Fparking+%3Featin+%3Flat+%3Flong+%3Fid+%3Ffeaturedproductname+%3Ffeaturedproductprice+%3Ffeaturedproductimage+%3Ffeaturedproductdescription+%3Fhakodatesweetsurl+%3Faddress+%3Fpostcode+%3Ftelephone+%3FfaxNumber+%3FopeningHoursSpecification+%3Furl+%3Femail+where+%7B%0D%0A%0D%0A%3Fs+sweets%3Aprimarykey+%3Fprimarykey.%0D%0A%3Fs+sweets%3Ashopname+%3Fshopname.%0D%0A%3Fs+sweets%3Ashopimage+%3Fshopimage.%0D%0A%3Fs+sweets%3Ashopdescription+%3Fshopdescription.%0D%0A%3Fs+sweets%3Aarea+%3Farea.%0D%0A%3Fs+dc%3Amodified+%3Fmodified.%0D%0A%3Fs+sweets%3Aclosed+%3Fclosed.%0D%0A%3Fs+sweets%3Aparking+%3Fparking.%0D%0A%3Fs+sweets%3Aeatin+%3Featin.%0D%0A%3Fs+geo%3Alat+%3Flat.%0D%0A%3Fs+geo%3Along+%3Flong.%0D%0A%3Fs+sweets%3Aid+%3Fid.%0D%0A%3Fs+sweets%3Afeaturedproductname+%3Ffeaturedproductname.%0D%0A%3Fs+sweets%3Afeaturedproductprice+%3Ffeaturedproductprice.%0D%0A%3Fs+sweets%3Afeaturedproductimage+%3Ffeaturedproductimage.%0D%0A%3Fs+sweets%3Afeaturedproductdescription+%3Ffeaturedproductdescription.%0D%0A%3Fs+sweets%3Ahakodatesweetsurl+%3Fhakodatesweetsurl.%0D%0A%3Fs+schema%3Aaddress+%3Faddress.%0D%0A%3Fs+sweets%3Apostcode+%3Fpostcode.%0D%0A%3Fs+schema%3Atelephone+%3Ftelephone.%0D%0A%3Fs+schema%3AfaxNumber+%3FfaxNumber.%0D%0A%3Fs+schema%3AopeningHoursSpecification+%3FopeningHoursSpecification.%0D%0A%3Fs+schema%3Aurl+%3Furl.%0D%0A%3Fs+schema%3Aemail+%3Femail.%0D%0A%0D%0A%7D&format=application%2Fsparql-results%2Bjson&timeout=0&debug=on
            query_url= "http://lod.per.c.fun.ac.jp:8080/sparql?default-graph-uri=&query=PREFIX+geo%3A" +
                    "+%3Chttp%3A%2F%2Fwww.w3.org%2F2003%2F01%2Fgeo%2Fwgs84_pos%23%3E%0D%0APREFIX+swee" +
                    "ts%3A+%3Chttp%3A%2F%2Flod.fun.ac.jp%2Fhakobura%2Fterms%2Fsweet%23%3E%0D%0APREFIX" +
                    "+dc%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0D%0APREFIX+schema%3" +
                    "A+%3Chttp%3A%2F%2Fschema.org%2F%3E%0D%0A%0D%0Aselect+distinct+%3Fprimarykey+%3Fs" +
                    "hopname+%3Fshopimage+%3Fshopdescription+%3Farea+%3Fmodified++%3Fclosed+%3Fparkin" +
                    "g+%3Featin+%3Flat+%3Flong+%3Fid+%3Ffeaturedproductname+%3Ffeaturedproductprice+%" +
                    "3Ffeaturedproductimage+%3Ffeaturedproductdescription+%3Fhakodatesweetsurl+%3Fadd" +
                    "ress+%3Fpostcode+%3Ftelephone+%3FfaxNumber+%3FopeningHoursSpecification+%3Furl+%" +
                    "3Femail+where+%7B%0D%0A%0D%0A%3Fs+sweets%3Aprimarykey+%3Fprimarykey.%0D%0A%3Fs+s" +
                    "weets%3Ashopname+%3Fshopname.%0D%0A%3Fs+sweets%3Ashopimage+%3Fshopimage.%0D%0A%3" +
                    "Fs+sweets%3Ashopdescription+%3Fshopdescription.%0D%0A%3Fs+sweets%3Aarea+%3Farea." +
                    "%0D%0A%3Fs+dc%3Amodified+%3Fmodified.%0D%0A%3Fs+sweets%3Aclosed+%3Fclosed.%0D%0A" +
                    "%3Fs+sweets%3Aparking+%3Fparking.%0D%0A%3Fs+sweets%3Aeatin+%3Featin.%0D%0A%3Fs+g" +
                    "eo%3Alat+%3Flat.%0D%0A%3Fs+geo%3Along+%3Flong.%0D%0A%3Fs+sweets%3Aid+%3Fid.%0D%0" +
                    "A%3Fs+sweets%3Afeaturedproductname+%3Ffeaturedproductname.%0D%0A%3Fs+sweets%3Afe" +
                    "aturedproductprice+%3Ffeaturedproductprice.%0D%0A%3Fs+sweets%3Afeaturedproductim" +
                    "age+%3Ffeaturedproductimage.%0D%0A%3Fs+sweets%3Afeaturedproductdescription+%3Ffe" +
                    "aturedproductdescription.%0D%0A%3Fs+sweets%3Ahakodatesweetsurl+%3Fhakodatesweets" +
                    "url.%0D%0A%3Fs+schema%3Aaddress+%3Faddress.%0D%0A%3Fs+sweets%3Apostcode+%3Fpostc" +
                    "ode.%0D%0A%3Fs+schema%3Atelephone+%3Ftelephone.%0D%0A%3Fs+schema%3AfaxNumber+%3F" +
                    "faxNumber.%0D%0A%3Fs+schema%3AopeningHoursSpecification+%3FopeningHoursSpecifica" +
                    "tion.%0D%0A%3Fs+schema%3Aurl+%3Furl.%0D%0A%3Fs+schema%3Aemail+%3Femail.%0D%0A%0D" +
                    "%0A%7D&format=application%2Fsparql-results%2Bjson&timeout=0&debug=on";
        } else {
            String queue_parts1 = "http://lod.per.c.fun.ac.jp:8000/sparql/?query=PREFIX%20rdf%3a%20%3" +
                    "chttp%3a%2f%2fwww%2ew3%2eorg%2f1999%2f02%2f22%2drdf%2dsyntax%2dns%23%3e%0d%0aPRE" +
                    "FIX%20rdfs%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2000%2f01%2frdf%2dschema%23%3e%" +
                    "0d%0aPREFIX%20schema%3a%20%3chttp%3a%2f%2fschema%2eorg%2f%3e%0d%0aPREFIX%20dc%3a" +
                    "%20%3chttp%3a%2f%2fpurl%2eorg%2fdc%2felements%2f1%2e1%2f%3e%0d%0aPREFIX%20geo%3a" +
                    "%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2003%2f01%2fgeo%2fwgs84_pos%23%3e%0d%0aPREFI" +
                    "X%20xsd%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2001%2fXMLSchema%23%3e%0d%0aPREFIX" +
                    "%20dcterms%3a%20%3chttp%3a%2f%2fpurl%2eorg%2fdc%2fterms%2f%3e%0d%0aPREFIX%20foaf" +
                    "%3a%20%3chttp%3a%2f%2fxmlns%2ecom%2ffoaf%2f0%2e1%2f%3e%0d%0a%0d%0aSELECT%20DISTI" +
                    "NCT%20%3fdobokuname%20%3fdescription%20%3faccess%20%3farea%20%3fimage%20%3faddre" +
                    "ss%20%3ftelephone%20%3furl%20%3ffilmName%20%3fdirector%20%3factor%20%3ffilmdescr" +
                    "iption%20%3ffilmurl%20%3fbornDate%20%3fdobokudescription%20%3fdobokuurl%20%3fcre" +
                    "ator%0d%0a%0d%0a%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fhakobura_ak" +
                    "iba%2erdf%3e%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2ffilm_akiba%2erd" +
                    "f%3e%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fdoboku_akiba%2erdf%3e%0" +
                    "d%0a%0d%0a%0d%0a%0d%0aWHERE%20%7b%0d%0a%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2fli" +
                    "b%2f4store%2fhakobura_akiba%2erdf%3e%20%7b%0d%0a%20%3fhs%20rdfs%3alabel%20%22";
            String queue_parts2 = "%22%3b%0d%0a%20dc%3adescription%20%3fdescription%3b%0d%0a%20schema" +
                    "%3adescription%20%3faccess%3b%0d%0a%20schema%3acontainedIn%20%3farea%3b%0d%0a%20" +
                    "schema%3aimage%20%3fimage%3b%0d%0a%20schema%3aaddress%20%3faddress%3b%0d%0a%20sc" +
                    "hema%3atelephone%20%3ftelephone%3b%0d%0a%20schema%3aurl%20%3furl%2e%0d%0a%7d%0d%" +
                    "0a%0d%0a%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4" +
                    "store%2ffilm_akiba%2erdf%3e%7b%0d%0a%3ffss%20schema%3aname%20%22";
            String queue_parts3 = "%22%3b%0d%0adcterms%3adescription%20%3ffilmdescription%2e%0d%0a%0d" +
                    "%0a%3ffs%20dc%3arelation%20%3ffss%3b%0d%0ardfs%3alabel%20%3ffilmName%3b%0d%0adc%" +
                    "3acreator%20%3fdirector%3b%0d%0afoaf%3aperson%20%3factor%3b%0d%0aschema%3aurl%20" +
                    "%3ffilmurl%0d%0a%7d%0d%0a%7d%0d%0a%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%20%3cfil" +
                    "e%3a%2f%2f%2fvar%2flib%2f4store%2fdoboku_akiba%2erdf%3e%7b%0d%0a%3fds%20rdfs%3acomment%20%22";
            String queue_parts4 = "%22%3b%0d%0adc%3adescription%20%3fdobokudescription%3b%0d%0aschema" +
                    "%3aurl%20%3fdobokuurl%2e%0d%0a%7d%0d%0a%7d%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%" +
                    "20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fdoboku_akiba%2erdf%3e%7b%0d%0a%3fds%20rdfs%3acomment%20%22";
            String queue_parts5 = "%22%3b%0d%0adc%3adate%20%3fbornDate%3b%0d%0adc%3acreator%20%3fcrea" +
                    "tor%3b%0d%0a%7d%0d%0a%7d%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%20%3cfile%3a%2f%2f" +
                    "%2fvar%2flib%2f4store%2fdoboku_akiba%2erdf%3e%7b%0d%0a%3fds%20rdfs%3acomment%20%22";
            String queue_parts6 = "%22%3b%0d%0ardfs%3alabel%20%3fdobokuname%3b%0d%0a%7d%0d%0a%7d%0d%0a%0d%0a%7d&output=json";
            query_url = queue_parts1 + encoded_spot + queue_parts2 + encoded_spot + queue_parts3 + encoded_spot + queue_parts4 + encoded_spot + queue_parts5 + encoded_spot + queue_parts6;
        }

        /* スポット詳細の取得 */
        try {
            /* JSONデータを受け取る */
            URL url = new URL(query_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String str = InputStreamToString(con.getInputStream());
            JSONObject json = new JSONObject(str);

            /* 受け取ったデータをパースする */
            final JSONArray bindings = json.getJSONObject("results").getJSONArray("bindings");
            JSONObject object = bindings.getJSONObject(0);
            if(spotCategory.equals("函館スイーツ")) {
                for (int i = 0; i < bindings.length(); i++) {
                    if (Objects.requireNonNull(bindings.getJSONObject(i).optJSONObject("id")).optString("value").equals(spotTag)) {
                        object = bindings.getJSONObject(i);
                    }
                }
            }
            final JSONObject binding = object;

            /* 映画ロケ地の場合 */
            List<String> film_spots = new ArrayList<>();
            Collection<String> film_spots_distinct = new LinkedHashSet<>();

            try {
                if (!binding.getJSONObject("filmName").isNull("value")) {
                    // 映画名リスト
                    for (int i = 0; i < bindings.length(); i++) {
                        String film_spot = bindings.getJSONObject(i).getJSONObject("filmName").getString("value");
                        film_spots.add(film_spot);
                    }
                    // 重複を排除する
                    film_spots_distinct.addAll(film_spots);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /* 土木スポットの場合 */
            List<String> civil_spots = new ArrayList<>();
            Collection<String> civil_spots_distinct = new LinkedHashSet<>();

            try {
                if (!binding.getJSONObject("dobokuname").isNull("value")) {
                    // 土木遺産名リスト
                    for (int i = 0; i < bindings.length(); i++) {
                        String civil_spot = bindings.getJSONObject(i).getJSONObject("dobokuname").getString("value");
                        civil_spots.add(civil_spot);
                    }
                    // 重複を排除する
                    civil_spots_distinct.addAll(civil_spots);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /* 諸々のデータを読み込み */
            final Collection<String> movie_collection = film_spots_distinct;
            final Collection<String> civil_collection = civil_spots_distinct;
            final String description = getDataFromJSON(binding,"description","shopdescription");

            final String sweetsName= getDataFromJSON(binding,"","featuredproductname");
            final String sweetsPrice= getDataFromJSON(binding,"","featuredproductprice");
            final String sweetsImage= getDataFromJSON(binding,"","featuredproductimage");
            final String sweetsDescription= getDataFromJSON(binding,"","featuredproductdescription");

            final String postalCode = getDataFromJSON(binding,"postcode","postcode");
            final String access = getDataFromJSON(binding,"access","");
            final String area = getDataFromJSON(binding,"area","");
            final String image = getDataFromJSON(binding,"image","shopimage");
            final String address = getDataFromJSON(binding,"address","address");
            final String phoneNumber = getDataFromJSON(binding,"telephone","telephone");
            final String businessHours = getDataFromJSON(binding,"","openingHoursSpecification");
            final String holiday = getDataFromJSON(binding,"","closed");
            final String parking = getDataFromJSON(binding,"","parking");
            final String eatIn = getDataFromJSON(binding,"","eatin");
            final String spotUrl = getDataFromJSON(binding,"url","url");
            
            /* 受け取ったデータをスポットクラスに格納 */
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /* スポットを作ってデータを入れていく
                    *  突貫工事でめっちゃわかりにくいコードでごめん */
                    spot = new Spot("noCorse", -1, spotName, spotCategory, null, spotTag);

                    ImageGetThread imageGetThread = new ImageGetThread(image, 0);   // 画像の取得
                    imageGetThread.start();
                    if (!sweetsImage.equals("")) {
                        ImageGetThread sweetsImageGetTread = new ImageGetThread(sweetsImage, 1);
                        sweetsImageGetTread.start();
                    }

                    spot.setDescription(description);
                    spot.setSweetsName(sweetsName);
                    spot.setSweetsPrice(sweetsPrice);
                    spot.setSweetsDescription(sweetsDescription);
                    spot.setPostalCode(postalCode);
                    spot.setAccess(access);
                    spot.setArea(area);
                    spot.setAddress(address);
                    spot.setPhoneNumber(phoneNumber);
                    spot.setBusinessHours(businessHours);
                    spot.setHoliday(holiday);
                    spot.setParking(parking);
                    spot.setEatIn(eatIn);
                    spot.setUrl(spotUrl);

                    if (movie_collection.size() > 0) {
                        int movieNo = 0;
                        for (String movie: movie_collection) {
                            /* ロケ地データをセット */
                            try {
                                if (!binding.getJSONObject("filmName").isNull("value")) {
                                    for (int i = 0; i < bindings.length(); i++) {
                                        String movieName = bindings.getJSONObject(i).getJSONObject("filmName").getString("value");
                                        if (movieName.equals(movie)) {
                                            final String movieImage = bindings.getJSONObject(i).getJSONObject("image").getString("value");
                                            final String movieDescription = bindings.getJSONObject(i).getJSONObject("filmdescription").getString("value");
                                            final String movieDirector = bindings.getJSONObject(i).getJSONObject("director").getString("value");
                                            final String movieActor = bindings.getJSONObject(i).getJSONObject("actor").getString("value");
                                            final String movieUrl = bindings.getJSONObject(i).getJSONObject("filmurl").getString("value");

                                            MovieSpot movieSpot = new MovieSpot(movieName, null, movieDescription, movieDirector, movieActor, movieUrl);
                                            spot.setMovieSpots(movieSpot);
                                            ImageGetThread movieImageGetThread = new ImageGetThread(movieImage, 2, movieNo);
                                            movieImageGetThread.start();
                                            break;
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            movieNo++;
                        }
                    }

                    if (civil_collection.size() > 0) {
                        int civilNo = 0;
                        for (String civil: civil_collection) {
                            /* 土木情報をセット */
                            try {
                                if (!binding.getJSONObject("dobokuname").isNull("value")) {
                                    for (int i = 0; i < bindings.length(); i++) {
                                        String civilName = bindings.getJSONObject(i).getJSONObject("dobokuname").getString("value");
                                        if (civilName.equals(civil)) {
                                            final String civilImage = bindings.getJSONObject(i).getJSONObject("image").getString("value");
                                            final String civilDescription = bindings.getJSONObject(i).getJSONObject("dobokudescription").getString("value");
                                            final String civilYear = bindings.getJSONObject(i).getJSONObject("bornDate").getString("value");
                                            final String civilCreator = bindings.getJSONObject(i).getJSONObject("creator").getString("value");
                                            final String civilUrl = bindings.getJSONObject(i).getJSONObject("dobokuurl").getString("value");

                                            CivilSpot civilSpot = new CivilSpot(civilName, null, civilDescription, civilYear, civilCreator, civilUrl);
                                            spot.setCivilSpots(civilSpot);
                                            ImageGetThread civilImageGetThread = new ImageGetThread(civilImage, 3, civilNo);
                                            civilImageGetThread.start();
                                            break;
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            civilNo++;
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("DEBUG POINT");
    }

    // InputStream型をString型へパース
    private String InputStreamToString(InputStream input_stream) throws IOException {
        BufferedReader buffered_builder = new BufferedReader(new InputStreamReader(input_stream));
        StringBuilder string_builder = new StringBuilder();
        String line;
        while ((line = buffered_builder.readLine()) != null) {
            string_builder.append(line);
        }
        buffered_builder.close();
        return string_builder.toString();
    }

    // JSONからデータの取得
    private String getDataFromJSON(JSONObject binding,String fromNormal,String fromSweets){
        if(binding.has(fromNormal)){
            return Objects.requireNonNull(binding.optJSONObject(fromNormal)).optString("value");
        }else if(binding.has(fromSweets)){
            return Objects.requireNonNull(binding.optJSONObject(fromSweets)).optString("value");
        }
        return "";
    }

    // 非同期にURLから画像を取得
    class ImageGetThread extends Thread {
        private String url; // 画像アクセス用のURL
        private int mode;   // 0: スポット画像, 1: スイーツ, 2: ロケ地
        private int spotNo; // ロケ地と土木のスポットを把握する用

        ImageGetThread(String url, int mode, int spotNo) {
            this.url = url;
            this.mode = mode;
            this.spotNo = spotNo;
        }

        ImageGetThread(String url, int mode) {
            this.url = url;
            this.mode = mode;
            this.spotNo = -1;
        }

        public void run() {
            URL image_url = null;
            Bitmap bitmap = null;
            InputStream inputStream;

            try {
                image_url = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                assert image_url != null;
                inputStream = image_url.openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            final Bitmap gotBitmap = bitmap;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (mode) {
                        case 0:
                            spot.setImageViews(gotBitmap);
                            break;
                        case 1:
                            spot.setSweetsImage(gotBitmap);
                            break;
                        case 2:
                            spot.setMovieImage(gotBitmap, spotNo);
                            break;
                        case 3:
                            spot.setCivilImage(gotBitmap, spotNo);
                            break;
                    }
                }
            });
        }
    }
}