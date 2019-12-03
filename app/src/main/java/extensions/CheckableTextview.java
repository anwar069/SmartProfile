package extensions;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Checkable;
import android.widget.TextView;

import com.anwar069.smartprofile.R;

/**
 * Created by Ahmed on 24/10/2015.
 */
public class CheckableTextview extends TextView implements Checkable {
    private boolean mChecked;
    Context context;

    public CheckableTextview(Context context) {
        super(context);
        this.context=context;
    }

    public void setChecked(boolean checked) {
        mChecked = checked;
        setBackgroundResource(checked ?
                R.drawable.days_checked
                : R.drawable.days_unchecked);
        if(checked)
            this.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        else
            this.setTextColor(ContextCompat.getColor(context, R.color.divider));
    }


    public boolean isChecked() {
        return mChecked;
    }

    public void toggle() {
        setChecked(!mChecked);
    }

}

