package carvajal.autenticador.android.adapter;

import java.util.ArrayList;

import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.dal.greendao.read.Municipios;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MunicipioSpinnerAdapter extends BaseAdapter {

	Context c;
	ArrayList<Municipios> municipios;

	public MunicipioSpinnerAdapter(Context c,
			ArrayList<Municipios> municipios) {
		super();
		this.c = c;
		this.municipios = municipios;
	}

	@Override
	public int getCount() {
		return municipios.size();
	}

	@Override
	public Object getItem(int position) {
		return municipios.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.parseLong(municipios.get(position).getCodMpio());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Municipios cur_mpio = municipios.get(position);
		LayoutInflater inflater = ((Activity) c).getLayoutInflater();
		View row = inflater.inflate(R.layout.spinner_row, parent, false);
		TextView label = (TextView) row.findViewById(R.id.item_row);
		label.setText(cur_mpio.getNomMpio());

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
                                       android.R.layout.simple_list_item_2,
                                       null);
        TextView tvText1 = (TextView)convertView.findViewById(android.R.id.text1);
        TextView tvText2 = (TextView)convertView.findViewById(android.R.id.text2);
        tvText1.setText(municipios.get(position).getNomMpio());
        tvText2.setText(municipios.get(position).getCodMpio());
        return convertView;
    }

}
