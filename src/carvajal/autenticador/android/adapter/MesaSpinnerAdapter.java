package carvajal.autenticador.android.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import carvajal.autenticador.android.activity.R;
import carvajal.autenticador.android.dal.greendao.read.Mesas;

public class MesaSpinnerAdapter extends BaseAdapter {

	Context c;
	ArrayList<Mesas> mesas;

	public MesaSpinnerAdapter(Context context, ArrayList<Mesas> mesas) {
		super();
		this.c = context;
		this.mesas = mesas;
	}

	@Override
	public int getCount() {
		return mesas.size();
	}

	@Override
	public Object getItem(int position) {
		return mesas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mesas.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Mesas mesa = mesas.get(position);
		LayoutInflater inflater = ((Activity) c).getLayoutInflater();
		View row = inflater.inflate(R.layout.spinner_row, parent, false);
		TextView label = (TextView) row.findViewById(R.id.item_row);
		label.setText(mesa.getCodMesa());
		return row;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return initView(position, convertView);
	}

	private View initView(int position, View convertView) {
		if (convertView == null)
			convertView = View.inflate(c, android.R.layout.simple_list_item_1,
					null);
		TextView tvText1 = (TextView) convertView
				.findViewById(android.R.id.text1);
		tvText1.setText(String.valueOf(((Mesas) getItem(position)).getCodMesa()));
		return convertView;
	}
}
