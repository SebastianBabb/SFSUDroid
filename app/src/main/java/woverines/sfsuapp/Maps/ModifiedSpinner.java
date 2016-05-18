package woverines.sfsuapp.Maps;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Created by Lowell Milliken on 5/17/2016.
 */
public class ModifiedSpinner extends Spinner {
    OnItemSelectedListener listener;
    int prevPos = -1;
    public ModifiedSpinner(Context context) {
        super(context);
    }

    public ModifiedSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ModifiedSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (position == getSelectedItemPosition() && prevPos == position) {

            getOnItemSelectedListener().onItemSelected(this, null, position, 0);
        }
        prevPos = position;
    }
}
