package carvajal.autenticador.android.adapter;

import java.util.ArrayList;

import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.dal.greendao.read.ColegiosElectorales;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ColegioElectoralSpinnerAdapter extends BaseAdapter {

	Context c;
	ArrayList<ColegiosElectorales> puestos;

	public ColegioElectoralSpinnerAdapter(Context c,
			ArrayList<ColegiosElectorales> puestos) {
		super();
		this.c = c;
		this.puestos = puestos;

	}

	@Override
	public int getCount() {
		return puestos.size();
	}

	@Override
	public Object getItem(int position) {
		return puestos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.parseLong(puestos.get(position).getCodColElec());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ColegiosElectorales cur_pto = puestos.get(position);
		LayoutInflater inflater = ((Activity) c).getLayoutInflater();
		View row = inflater.inflate(R.layout.spinner_row, parent, false);
		TextView label = (TextView) row.findViewById(R.id.item_row);
		label.setText(cur_pto.getNomColElec());

		return row;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return initView(position, convertView);
	}

	private View initView(int position, View convertView) {
		if (convertView == null)
			convertView = View.inflate(c, android.R.layout.simple_list_item_2,
					null);
		TextView tvText1 = (TextView) convertView
				.findViewById(android.R.id.text1);
		TextView tvText2 = (TextView) convertView
				.findViewById(android.R.id.text2);
		tvText1.setText(puestos.get(position).getNomColElec());
		tvText2.setText(puestos.get(position).getCodColElec());
		return convertView;
	}

}
