package ac.fun.lodmaps;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class CustomBottomSheet extends NestedScrollView {
    View mBottomSheet;
    BottomSheetBehavior bottomSheetBehavior;

    public CustomBottomSheet(@NonNull Context context, int mBottomSheet) {
        super(context);
        this.mBottomSheet = findViewById(mBottomSheet);
        assert this.mBottomSheet != null;
        this.bottomSheetBehavior = BottomSheetBehavior.from(this.mBottomSheet);
    }

    void setSpot(Spot spot) {
    }
}
