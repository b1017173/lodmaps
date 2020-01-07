package ac.fun.lodmaps;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class CustomBottomSheet extends NestedScrollView {
    View mBottomSheet;
    BottomSheetBehavior bottomSheetBehavior;
    TextView spotName;  // スポット名
    TextView spotCategory;  // カテゴリー
    TextView spotText;  // 概要

    public CustomBottomSheet(@NonNull Context context, int mBottomSheet) {
        super(context);
        this.mBottomSheet = findViewById(mBottomSheet);
        assert this.mBottomSheet != null;
        this.bottomSheetBehavior = BottomSheetBehavior.from(this.mBottomSheet);

        this.spotName = findViewById(R.id.spot_name);
        this.spotCategory = findViewById(R.id.spot_category);
        this.spotText = findViewById(R.id.spot_text);
    }

    void setSpot(Spot spot) {
        spotName.setText(spot.getName());
        spotCategory.setText(spot.getCategory());
        spotText.setText(spot.getDescription());
    }
}
