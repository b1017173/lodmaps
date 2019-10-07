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

    private static final int PERMISSION_REQUEST_LOCATE = 0;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private FusedLocationProviderClient fusedLocationClient;    // 現在地取得のためのAPI
    protected Location lastLocation;    // 最後の観測現在地

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
            // TODO: オフラインの時にダイアログをだす
            // > NoConnectionDialogFragment()参照
            System.out.println("koo");
        }
    }

    /* 起動時処理 */
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (!checkPermissions()) {
//            requestPermissions();
//        }
//    }

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
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
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

    /* すでにパーミッションが許可されているか */
//    private boolean checkPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
//        } else {
//            return true;
//        }
//    }

    /* 初めて座標を取得する */
//    private void startLocationPermissionRequest() {
//        ActivityCompat.requestPermissions(MapsActivity.this,
//                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                REQUEST_PERMISSIONS_REQUEST_CODE);
//    }

//    private void requestPermissions() {
//        boolean shouldProvideRationale =
//                ActivityCompat.shouldShowRequestPermissionRationale(this,
//                        Manifest.permission.ACCESS_FINE_LOCATION);
//
//        if (shouldProvideRationale) {
//            Log.i("ろぐ", "位置情報を許可しないを選択した状態");
//
//            showSnackbar("位置情報の許可が必要です", "OK",
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Request permission
//                            startLocationPermissionRequest();
//                        }
//                    });
//        } else {
//            Log.i("ろぐ", "位置情報パーミッションのリクエストを開始");
//            startLocationPermissionRequest();
//        }
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
//            if (grantResults.length <= 0) {
//                // 許可する、許可しないのダイアログがキャンセルされた時
//                Log.i("ろぐ", "許可ダイアログでキャンセルボタンが押された");
//            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // 許可する、が押された場合
//                Log.i("ろぐ", "許可もらえたので位置情報を取得しにいく");
//                getLastLocation();
//            } else {
//                // 許可しない、を押された場合
//                showSnackbar("位置情報の許可が必要なので設定画面でお願いします", "設定画面にいく",
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                // Build intent that displays the App settings screen.
//                                Intent intent = new Intent();
//                                intent.setAction(
//                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package",
//                                        BuildConfig.APPLICATION_ID, null);
//                                intent.setData(uri);
//                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(intent);
//                            }
//                        });
//            }
//        }
//    }

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

//    /* シンプルなSnackBarを出すメソッド */
//    private void showSnackbar(final String text) {
//        View container = findViewById(R.id.map);    // mapのところをactivityのidに変える
//        if (container != null) {
//            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
//        }
//    }
//
//    /* アクションつきのSnackBarを出すメソッド */
//    private void showSnackbar(final String mainTextString, final String actionString,
//                              View.OnClickListener listener) {
//        Snackbar.make(findViewById(android.R.id.content),
//                mainTextString,
//                Snackbar.LENGTH_INDEFINITE)
//                .setAction(actionString, listener).show();
//    }

}
