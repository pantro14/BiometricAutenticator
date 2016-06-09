package carvajal.autenticador.android.adapter;

import java.util.ArrayList;

import carvajal.autenticador.android.activity.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServidorSpinnerAdapter extends BaseAdapter {

	Context c;
	ArrayList<String> servidores;

	public ServidorSpinnerAdapter(Context context, ArrayList<String> servidores) {
		super();
		this.c = context;
		this.servidores = servidores;
	}

	@Override
	public int getCount() {
		return servidores.size();
	}

	@Override
	public Object getItem(int position) {
		return servidores.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String cur_servidor = servidores.get(position);
		LayoutInflater inflater = ((Activity) c).getLayoutInflater();
		View row = inflater.inflate(R.layout.spinner_row, parent, false);
		TextView label = (TextView) row.findViewById(R.id.item_row);
		label.setText(cur_servidor);

		return row;
	}
	
	@Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return initView(position, convertView);
    }

    private View initView(int position, View convertView) {
        if(convertView == null)
            convertView = View.inflate(c,
                                       android.R.layout.simple_list_item_1,
                                       null);
        TextView tvText1 = (TextView)convertView.findViewById(android.R.id.text1);
        tvText1.setText(servidores.get(position));
        return convertView;
    }

}
