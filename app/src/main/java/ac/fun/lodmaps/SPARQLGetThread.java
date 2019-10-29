package ac.fun.lodmaps;

import android.widget.ProgressBar;

import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/* スポット情報を扱うクラス */
// TODO: 継承クラスが正しいか要検討
public class SPARQLGetThread extends MapsActivity implements Runnable {
    private GoogleMap mMap; // スポットを扱うマップ
    private String course_title;   // エンコードされるコース名
    private ProgressBar progressBar;

    SPARQLGetThread(GoogleMap mMap, String course_title, ProgressBar progressBar) {
        this.mMap = mMap;
        this.course_title = course_title;
        this.progressBar = progressBar;
    }

    // スポットデータの取得
    public void run() {
        // コース名をURLに埋め込むためにエンコードする
        String encoded_course = "";
        try {
            encoded_course = URLEncoder.encode(course_title, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // エラーの場合は標準出力
            e.printStackTrace();
        }

        /* SPARQLのクエリを用意 */
        // 検索用：http://lod.per.c.fun.ac.jp:8000/sparql/?query=PREFIX%20rdf%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f1999%2f02%2f22%2drdf%2dsyntax%2dns%23%3e%0d%0aPREFIX%20rdfs%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2000%2f01%2frdf%2dschema%23%3e%0d%0aPREFIX%20schema%3a%20%3chttp%3a%2f%2fschema%2eorg%2f%3e%0d%0aPREFIX%20dc%3a%20%3chttp%3a%2f%2fpurl%2eorg%2fdc%2felements%2f1%2e1%2f%3e%0d%0aPREFIX%20geo%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2003%2f01%2fgeo%2fwgs84_pos%23%3e%0d%0aPREFIX%20xsd%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2001%2fXMLSchema%23%3e%0d%0aPREFIX%20dcterms%3a%20%3chttp%3a%2f%2fpurl%2eorg%2fdc%2fterms%2f%3e%0d%0aPREFIX%20foaf%3a%20%3chttp%3a%2f%2fxmlns%2ecom%2ffoaf%2f0%2e1%2f%3e%0d%0a%0d%0aSELECT%20DISTINCT%20%3fcourseName%20%3frootNum%20%3fspotName%20%3fcategory%20%3flat%20%3flong%20%3furl%20%0d%0a%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fmachiaruki_akiba%2erdf%3e%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fhakobura_akiba%2erdf%3e%0d%0a%0d%0a%0d%0aWHERE%20%7b%0d%0a%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fhakobura_akiba%2erdf%3e%20%7b%0d%0a%20%3fhs%20rdfs%3alabel%20%3fspotName%3b%0d%0a%20rdfs%3acomment%20%3fcategory%3b%0d%0a%20geo%3alat%20%3flat%3b%0d%0a%20geo%3along%20%3flong%3b%0d%0a%7d%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fmachiaruki_akiba%2erdf%3e%20%7b%0d%0a%3fmss%20schema%3aname%20%3fspotName%3b%0d%0adc%3asubject%20%3frootNum%2e%0d%0a%3fms%20dc%3arelation%20%3fmss%3b%0d%0ardfs%3alabel%20%3fcourseName%2e%0d%0a%7d%0d%0aFILTER%28%3fms%20%3d%20%3curn%3a%3e%29%0d%0a%7d%0d%0a%0d%0a%7d&output=json
        String query_url = "http://lod.per.c.fun.ac.jp:8000/sparql/?query=PREFIX%20rdf%3a%20%3c" +
                "http%3a%2f%2fwww%2ew3%2eorg%2f1999%2f02%2f22%2drdf%2dsyntax%2dns%23%3e%0d%0aPREFIX%20rdfs" +
                "%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2000%2f01%2frdf%2dschema%23%3e%0d%0aPREFIX%20schem" +
                "a%3a%20%3chttp%3a%2f%2fschema%2eorg%2f%3e%0d%0aPREFIX%20dc%3a%20%3chttp%3a%2f%2fpurl%2eor" +
                "g%2fdc%2felements%2f1%2e1%2f%3e%0d%0aPREFIX%20geo%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f2" +
                "003%2f01%2fgeo%2fwgs84_pos%23%3e%0d%0aPREFIX%20xsd%3a%20%3chttp%3a%2f%2fwww%2ew3%2eorg%2f" +
                "2001%2fXMLSchema%23%3e%0d%0aPREFIX%20dcterms%3a%20%3chttp%3a%2f%2fpurl%2eorg%2fdc%2fterms" +
                "%2f%3e%0d%0aPREFIX%20foaf%3a%20%3chttp%3a%2f%2fxmlns%2ecom%2ffoaf%2f0%2e1%2f%3e%0d%0a%0d%" +
                "0aSELECT%20DISTINCT%20%3fcourseName%20%3frootNum%20%3fspotName%20%3fcategory%20%3flat%20%" +
                "3flong%20%3furl%20%0d%0a%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fmachiaruki_a" +
                "kiba%2erdf%3e%0d%0aFROM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fhakobura_akiba%2erdf%3e" +
                "%0d%0a%0d%0a%0d%0aWHERE%20%7b%0d%0a%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2f" +
                "hakobura_akiba%2erdf%3e%20%7b%0d%0a%20%3fhs%20rdfs%3alabel%20%3fspotName%3b%0d%0a%20rdfs%" +
                "3acomment%20%3fcategory%3b%0d%0a%20geo%3alat%20%3flat%3b%0d%0a%20geo%3along%20%3flong%3b%" +
                "0d%0a%7d%0d%0a%0d%0aOPTIONAL%20%7b%0d%0aGRAPH%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fm" +
                "achiaruki_akiba%2erdf%3e%20%7b%0d%0a%3fmss%20schema%3aname%20%3fspotName%3b%0d%0adc%3asub" +
                "ject%20%3frootNum%2e%0d%0a%3fms%20dc%3arelation%20%3fmss%3b%0d%0ardfs%3alabel%20%3fcourse" +
                "Name%2e%0d%0a%7d%0d%0aFILTER%28%3fms%20%3d%20%3curn%3a" +
                encoded_course +
                "%3e%29%0d%0a%7d%0d%0a%0d%0a%7d&output=json";

        /* 観光スポットの取得 */
        final ArrayList<Spot> spot_list = new ArrayList<>();
        if (setSparqlResultFromQuery(spot_list, query_url)) {
            // はこぶらのスポットリスト取得に成功
            if (encoded_course != null) {
                // まちあるきマップにしかないスポットの取得
                String town_work_url = "http://lod.per.c.fun.ac.jp:8000/sparql/?query=PREFIX%20rdfs%3a%20%3" +
                        "chttp%3a%2f%2fwww%2ew3%2eorg%2f2000%2f01%2frdf%2dschema%23%3e%0d%0aPREFIX%20schema" +
                        "%3a%20%3chttp%3a%2f%2fschema%2eorg%2f%3e%0d%0aPREFIX%20dc%3a%20%3chttp%3a%2f%2fpur" +
                        "l%2eorg%2fdc%2felements%2f1%2e1%2f%3e%0d%0aPREFIX%20geo%3a%20%3chttp%3a%2f%2fwww%2" +
                        "ew3%2eorg%2f2003%2f01%2fgeo%2fwgs84_pos%23%3e%0d%0a%0d%0aSELECT%20DISTINCT%20%3fco" +
                        "urseName%20%3frootNum%20%3fspotName%20%3fcategory%20%3flat%20%3flong%0d%0a%0d%0aFR" +
                        "OM%20%3cfile%3a%2f%2f%2fvar%2flib%2f4store%2fmachiaruki_akiba%2erdf%3e%0d%0a%0d%0a" +
                        "WHERE%20%7b%0d%0a%7b%0d%0a%20%20%20%20%3curn%3a" +
                        encoded_course +
                        "%3e%20rdfs%3alabel%20%3fcourseName%3b%0d%0a%20%20%20%20dc%3arelation%20%3fmspotURI" +
                        "%2e%0d%0a%20%20%20%20%3fmspotURI%20dc%3asubject%20%3frootNum%3b%0d%0a%20%20%20%20s" +
                        "chema%3aname%20%3fspotName%3b%0d%0a%20%20%20%20geo%3alat%20%3flat%3b%0d%0a%20%20%2" +
                        "0%20geo%3along%20%3flong%2e%0d%0a%7d%0d%0a%7d&output=json";
                setSparqlResultFromQuery(spot_list, town_work_url);

                // スイーツスポットの取得
                String sweets_url = "http://lod.per.c.fun.ac.jp:8080/sparql?default-graph-uri=&query=PREFIX" +
                        "+geo%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F2003%2F01%2Fgeo%2Fwgs84_pos%23%3E%0D%0APREFIX" +
                        "+sweets%3A+%3Chttp%3A%2F%2Flod.fun.ac.jp%2Fhakobura%2Fterms%2Fsweet%23%3E%0D%0APRE" +
                        "FIX+dc%3A+%3Chttp%3A%2F%2Fpurl.org%2Fdc%2Felements%2F1.1%2F%3E%0D%0APREFIX+schema%" +
                        "3A+%3Chttp%3A%2F%2Fschema.org%2F%3E%0D%0A%0D%0Aselect+%3Fid+%3Fshopname+%3Flat+%3F" +
                        "long+where+%7B%0D%0A%3Fs+sweets%3Aid+%3Fid.%0D%0A%3Fs+sweets%3Ashopname+%3Fshopnam" +
                        "e.%0D%0A%3Fs+geo%3Alat+%3Flat.%0D%0A%3Fs+geo%3Along+%3Flong.%0D%0A%7D&format=appli" +
                        "cation%2Fsparql-results%2Bjson&timeout=0&debug=on";
                setSparqlResultFromQuery(spot_list, sweets_url);
            }
            // 避難スポットの取得
            String shelter_url = "http://lod.per.c.fun.ac.jp:8000/sparql/?query=PREFIX%20rdf%3a%20%3Chttp%3" +
                    "a%2f%2fwww%2ew3%2eorg%2f1999%2f02%2f22-rdf-syntax-ns%23%3E%0d%0aPREFIX%20rdfs%3a%20%3C" +
                    "http%3a%2f%2fwww%2ew3%2eorg%2f2000%2f01%2frdf-schema%23%3E%0d%0aPREFIX%20geo%3a%20%3Ch" +
                    "ttp%3a%2f%2fwww%2ew3%2eorg%2f2003%2f01%2fgeo%2fwgs84_pos%23%3E%0d%0aPREFIX%20schema%3a" +
                    "%20%3Chttp%3a%2f%2fschema%2eorg%2f%3E%0d%0aPREFIX%20shelter%3a%20%3Chttp%3a%2f%2flod%2" +
                    "eper%2ec%2efun%2eac%2ejp%2fbosai%2fterms%2fshelter%23%3E%0d%0aPREFIX%20evcx%3a%20%3Cht" +
                    "tp%3a%2f%2fsmartercity%2ejp%2fevacuation%23%3E%0d%0a%0d%0aSELECT%20DISTINCT%20%3fspotN" +
                    "ame%20%3frootNum%20%3fcategory%20%3flat%20%3flong%0d%0a%0d%0aFROM%20%3Cfile%3a%2f%2f%2" +
                    "fvar%2flib%2f4store%2fshelter%2erdf%3E%0d%0a%0d%0aWHERE%20%7b%0d%0a%20%20%3fs%20rdfs%3" +
                    "alabel%20%3fspotName%3b%0d%0a%20%20%20%20geo%3aalt%20%3frootNum%3b%0d%0a%20%20%20%20sh" +
                    "elter%3atypeOfshelter%20%3fcategory%3b%0d%0a%20%20%20%20geo%3alat%20%3flat%3b%0d%0a%20" +
                    "%20%20%20geo%3along%20%3flong%3b%0d%0a%7d&output=json";
            setSparqlResultFromQuery(spot_list, shelter_url);

            // 取得した情報を地図へ反映
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setSpotPin(spot_list);
                }
            });
        } else {
            // データの取得に失敗した場合ダイアログを表示する
            DialogFragment fragment = new NoConnectedNetwork();
            fragment.show(getSupportFragmentManager(), "noConnectedNetwork");
        }
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

    // SPARQLのクエリを実行して結果をArrayList形式で取得する。成功するとtrueを、失敗するとfalseを返す
    private boolean setSparqlResultFromQuery(ArrayList<Spot> spot_list, String query_url) {
        try {
            URL url = new URL(query_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            String str = InputStreamToString(con.getInputStream());

            /* 受け取ったJSONを配列へパースする */
            JSONObject json_list = new JSONObject(str).getJSONObject("results");
            // LODデータをリストへ
            JSONArray bindings = json_list.getJSONArray("bindings");

            /* 各データの要素を取り出し格納 */
            for (int i = 0; i < bindings.length(); i++) {
                JSONObject binding = bindings.getJSONObject(i); // 1つのスポットデータを取得
                Spot spot = new Spot(null, -1, null, null, null, -1);    // 値を格納する変数

                // コース名の取得
                try {
                    // データがあれば格納
                    spot.setCourse(binding.getJSONObject("courseName").getString("value"));
                } catch (JSONException e) {
                    spot.setCourse("noCourse");
                }
                // コース順路または海抜の取得
                try {
                    spot.setNumber(binding.getJSONObject("rootNum").getDouble("value"));
                } catch (JSONException e) {
                    spot.setNumber(-1);
                }
                // スポット名の取得
                try {
                    // spotNameははこぶらのデータを参照
                    spot.setName(binding.getJSONObject("spotName").getString("value"));
                } catch (JSONException e1) {
                    try {
                        // spotnameはスイーツのデータを参照
                        spot.setName(binding.getJSONObject("shopname").getString("value"));
                    } catch (JSONException e2) {
                        spot.setName("noName");
                    }
                }
                // カテゴリの取得
                try {
                    // はこぶらのカテゴリデータを参照
                    spot.setCategory(binding.getJSONObject("category").getString("value"));
                    spot.setId(-1);
                } catch (JSONException e) {
                    // データがない場合はスイーツを指定
                    spot.setCategory("スイーツ");
                    // スイーツのスポットの場合idを取得
                    spot.setId(binding.getJSONObject("id").getInt("value"));
                }
                // 位置情報の取得
                LatLng location = new LatLng(binding.getJSONObject("lat").getDouble("value"), binding.getJSONObject("long").getDouble("value"));
                spot.setLocation(location);

                spot_list.add(spot);    // スポットリストに追加
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            
            return false;
        }
        return true;
    }

    /* スポット情報からピンを生成 */
    private void setSpotPin(ArrayList<Spot> spot_list) {
        /* 各スポットアイコン */
        BitmapDescriptor restaurant = BitmapDescriptorFactory.fromResource(R.drawable.restaurant);
        BitmapDescriptor photo = BitmapDescriptorFactory.fromResource(R.drawable.photo);
        BitmapDescriptor playground = BitmapDescriptorFactory.fromResource(R.drawable.playground);
        BitmapDescriptor shop = BitmapDescriptorFactory.fromResource(R.drawable.shop);
        BitmapDescriptor hot_spring = BitmapDescriptorFactory.fromResource(R.drawable.hot_spring);
        BitmapDescriptor event = BitmapDescriptorFactory.fromResource(R.drawable.event);
        BitmapDescriptor sweets = BitmapDescriptorFactory.fromResource(R.drawable.sweets);

        BitmapDescriptor shelter = BitmapDescriptorFactory.fromResource(R.drawable.shelter);
        BitmapDescriptor tsunami = BitmapDescriptorFactory.fromResource(R.drawable.tsunami);

        BitmapDescriptor[] course_pin = {
                BitmapDescriptorFactory.fromResource(R.drawable.pin01),
                BitmapDescriptorFactory.fromResource(R.drawable.pin02),
                BitmapDescriptorFactory.fromResource(R.drawable.pin03),
                BitmapDescriptorFactory.fromResource(R.drawable.pin04),
                BitmapDescriptorFactory.fromResource(R.drawable.pin05),
                BitmapDescriptorFactory.fromResource(R.drawable.pin06),
                BitmapDescriptorFactory.fromResource(R.drawable.pin07),
                BitmapDescriptorFactory.fromResource(R.drawable.pin08),
                BitmapDescriptorFactory.fromResource(R.drawable.pin09),
                BitmapDescriptorFactory.fromResource(R.drawable.pin10),
                BitmapDescriptorFactory.fromResource(R.drawable.pin11),
                BitmapDescriptorFactory.fromResource(R.drawable.pin12),
                BitmapDescriptorFactory.fromResource(R.drawable.pin13),
                BitmapDescriptorFactory.fromResource(R.drawable.pin14),
                BitmapDescriptorFactory.fromResource(R.drawable.pin15),
                BitmapDescriptorFactory.fromResource(R.drawable.pin16),
                BitmapDescriptorFactory.fromResource(R.drawable.pin17),
                BitmapDescriptorFactory.fromResource(R.drawable.pin18),
                BitmapDescriptorFactory.fromResource(R.drawable.pin19),
                BitmapDescriptorFactory.fromResource(R.drawable.pin20),
        };

        // スポットのピンを地図上に表示
        for (int i = 0; i < spot_list.size(); i++) {
            Spot target = spot_list.get(i);
            MarkerOptions mOptions = new MarkerOptions();   // ピンの設定
            mOptions.position(target.getLocation());  // 位置情報の設定
            mOptions.title(target.getName());   // スポット名を設定
            mOptions.snippet(target.getCategory()); // スニペット(サブタイトル)を設定

            // boolean is_show = true;
            // コーススポットかどうかでアイコンを変更する
            if (target.getCourse().equals("noCourse")) {
                switch (target.getCategory()) {
                    case "食べる":
                        mOptions.icon(restaurant);
                        break;
                    case  "見る":
                        mOptions.icon(photo);
                        break;
                    case "遊ぶ":
                        mOptions.icon(playground);
                        break;
                    case "買う":
                        mOptions.icon(shop);
                        break;
                    case "温泉":
                        mOptions.icon(hot_spring);
                        break;
                    case "観光カレンダー":
                        mOptions.icon(event);
                        mOptions.snippet("観光イベント"); // ”観光イベント”へ書き換える
                        break;
                    case "スイーツ":
                        mOptions.icon(sweets);
                        break;
                    case "津波避難所":
                        mOptions.icon(shelter);
                        mOptions.snippet("津波避難所 " + target.getNumber() + "m");
                        break;
                    case "津波避難ビル":
                        mOptions.icon(tsunami);
                        mOptions.snippet("津波避難ビル " + target.getNumber() + "m");
                        break;
                }
            } else {
                // コーススポットの場合は順路のアイコンを設定
                int iconNum = (int)target.getNumber() - 1; // コース順路は1から始まるため配列に合わせて-1
                if (iconNum < 0) {
                    mOptions.icon(course_pin[0]);
                } else {
                    mOptions.icon(course_pin[iconNum]);
                }
            }
            Marker marker = mMap.addMarker(mOptions);   // 設定に基づいてマーカーを作成
            // IDがあった場合はタグとして設定
            if (target.getId() == -1) {
                marker.setTag("");
            } else {
                marker.setTag(target.getId());
            }
            marker.setVisible(true); // マーカーの表示を設定
        }
        progressBar.setVisibility(android.widget.ProgressBar.INVISIBLE);    // ぐるぐるを非表示
    }
}
