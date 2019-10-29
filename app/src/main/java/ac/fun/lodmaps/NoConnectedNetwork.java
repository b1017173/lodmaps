package ac.fun.lodmaps;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

/* ネットワーク接続がないときのダイアログ */
public class NoConnectedNetwork extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // ダイアログ生成のためにBuilderを設定
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // テキストメッセージを設定
        builder.setMessage(R.string.not_connected_network)
                // 確認ボタンの設定
                .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 確認ボタンを押すと無線の設定画面を開く
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
                        startActivity(intent);
                    }
                });
        // ダイアログを生成して返り値として返す
        // .show()で表示
        return builder.create();
    }
}