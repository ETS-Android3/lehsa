package my.project.lhesa.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import my.project.lhesa.R;

// Reference: https://stackoverflow.com/questions/38417984/android-spinner-dropdown-checkbox/38418249
public class SpinnerChoiceBoxAdapter extends ArrayAdapter<ChoiceBox> {
    private Context mContext;
    private ArrayList<ChoiceBox> listState;
    private SpinnerChoiceBoxAdapter spinnerChoiceBoxAdapter;
    private boolean isFromView = false;

    public SpinnerChoiceBoxAdapter(Context context, int resource, List<ChoiceBox> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<ChoiceBox>) objects;
        this.spinnerChoiceBoxAdapter = this;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.spinner_item, null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView
                    .findViewById(R.id.checkbox_interest_name);
            holder.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.checkbox_selection);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(listState.get(position).getTitle());

        // To check weather checked event fire from getview() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(listState.get(position).isSelected());
        isFromView = false;

        if (position == 0) {
            holder.mCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        }
        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();

                if (!isFromView) {
                    listState.get(position).setSelected(isChecked);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        private TextView mTitle;
        private TextView mTextView;
        private CheckBox mCheckBox;
    }

    public ArrayList<ChoiceBox> getListState(){
        return listState;
    }
}