package ac.fun.lodmaps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private View mLayout;

    private static final int PERMISSION_REQUEST_LOCATE = 0; // 位置情報のパーミッションをリクエストする時のコード(任意で設定)
    private FusedLocationProviderClient fusedLocationClient;    // 現在地取得のための外部パッケージ
    protected Location lastLocation;    // 最後の観測現在地

    /* activity生成時の処理 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLayout = findViewById(R.id.map);
        requestLocatePermission();

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
            DialogFragment newFragment = new NoConnectedNetwork();
            newFragment.show(getSupportFragmentManager(), "noConnectedNetwork");
        }
    }

    /* マップが準備出来次第処理 */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /* 現在地の取得
     * こいつを呼ぶと、現在位置がlastLocationに代入される */
//    private void getLastLocation() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions();
//                return;
//            }
//        }
//        fusedLocationClient.getLastLocation()
//                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Location> task) {
//                        if (task.isSuccessful() && task.getResult() != null) {
//                            lastLocation = task.getResult();
//                            System.out.println("Location =>" + lastLocation);
//                        } else {
//                            Log.w("ろぐ", "getLastLocation:exception", task.getException());
//                            showSnackbar("位置情報を取得できませんでした");
//                        }
//                    }
//                });
//    }

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
                // TODO: ここに許可されたときの正規の処理をかく(現在位置の取得処理)
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