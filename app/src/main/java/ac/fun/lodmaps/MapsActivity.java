package ac.fun.lodmaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private View mLayout;

    private LocationRequest locationRequest;    // 位置情報更新用の構成
    private LocationCallback locationCallback;  // 定期的な位置情報更新をするコールバック

    private static final int PERMISSION_REQUEST_LOCATE = 0; // 位置情報のパーミッションをリクエストする時のコード(任意で設定)
    private FusedLocationProviderClient fusedLocationClient;    // 現在地取得のためのGoogle API
    protected Location lastLocation;    // 最後の観測現在地

    /* activity生成時の処理 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLayout = findViewById(R.id.map);

        requestLocatePermission();  // パーミッションをリクエストして位置情報を取得
        createLocationRequest();    // 位置情報の更新設定を行う
        // コールバックを得た時の挙動
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    // 位置情報が得られなかった場合
                    return;
                }
                for (Location location: locationResult.getLocations()) {
                    // 得られた場合は更新する
                    lastLocation = location;
                }
            }
        };

        // オンラインの場合マップ処理
        if (isOnline(this.getApplicationContext())) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            // 現在地の取得準備
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        } else {
            // オフラインの場合はそれを伝えるダイアログを出す
            DialogFragment fragment = new NoConnectedNetwork();
            fragment.show(getSupportFragmentManager(), "noConnectedNetwork");
        }
    }

    /* マップが準備出来次第処理 */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setTrafficEnabled(false);  // 渋滞情報を非表示に設定
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String title = marker.getTitle();
                String snippet = marker.getSnippet();
                String id = marker.getId();
            }
        });
    }

    /* アプリがアクティブになった場合 */
    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    /* アプリがバックグランドになって停止した場合 */
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    /* スポット情報の取得 */

    public void loadSpots(String title, int course_id) {
        // 地図の読み込み中はダイアログをだす
        ProgressBar progressBar = findViewById(R.id.progressBar);

        if (course_id == -1) {
            SPARQLGetThread sgt = new SPARQLGetThread(mMap, "");
            Thread thread = new Thread(sgt);    // スレッド化
            thread.start();
        } else {
            String course_title = title.replaceAll("[ 　]", "");
            SPARQLGetThread sgt = new SPARQLGetThread(mMap, course_title);
            Thread thread = new Thread(sgt);
            thread.start();
        }
    }

    /* 現在地の取得
     * こいつを呼ぶと、現在位置がlastLocationに代入される */
    private void getLastLocation() {
        // fusedLocationClient内のgetLastLocation呼び出し
        fusedLocationClient.getLastLocation().addOnCompleteListener(
                new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        // 取得データがあるかを確認
                        // データがあってもnullの場合が稀にあるのでそれもチェック
                        if (task.isSuccessful() && task.getResult() != null) {
                            // 現在地の更新
                            lastLocation = task.getResult();
                        } else {
                            // 失敗した場合は失敗したことを伝える
                            Snackbar.make(mLayout, R.string.not_get_location,
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    /* 定期的な位置情報の取得の設定 */
    protected void createLocationRequest() {
        locationRequest = LocationRequest.create(); // 位置情報周りの構成を取得
        locationRequest.setInterval(10000); // 基本10秒ごとに更新
        locationRequest.setFastestInterval(5000);   // 最速でも5秒はインターバルをおく
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);    // 高精度で位置情報を取得する
    }

    /*
    * パーミッション関係
    * */
    /* 公式ドキュメント準拠 */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_LOCATE) {
            // 位置情報のリクエストを送った場合
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 正しくパーミッション付与がされており、許可されている場合
                Snackbar.make(mLayout, R.string.location_permission_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
                // 現在地の取得を行う
                getLastLocation();
            } else {
                // パーミッションの要求が拒否された場合
                Snackbar.make(mLayout, R.string.location_permission_denied,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    /* 公式ドキュメント準拠パーミッションに関する関数 */
    // {@link android.Manifest.permission#ACCESS_FINE_LOCATION} のパーミッションを取得する
    private void requestLocatePermission() {
        // 位置情報へのアクセスが許可されていないため要求する
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // 許可が得られなかった場合は、ユーザに許可が必要な根拠を示し2回目の確認
            // SnackBarを表示してパーミッションをリクエストする
            Snackbar.make(mLayout, R.string.location_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.approve, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // パーミッションのリクエスト
                    ActivityCompat.requestPermissions(MapsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_LOCATE);
                }
            }).show();
        } else {
            Snackbar.make(mLayout, R.string.location_access_required, Snackbar.LENGTH_SHORT).show();
            // パーミッションを要求し、onRequestPermissionResult()でユーザの選択を受け取る
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATE);
        }
    }

    /* インターネット接続を確認する */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // TODO: (API29以降)非推奨なコードを使っているから推奨コードを使う
        // > NetworkCapabilities とかが候補(？)
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // true: 接続あり
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }
}