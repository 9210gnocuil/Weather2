package com.liucong.weather2.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.liucong.weather2.R;
import com.liucong.weather2.utils.DBUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectLocActivity extends AppCompatActivity {
    private static final String TAG = "SelectLocActivity";

    @BindView(R.id.select_lv_loc)
    ListView seletcLvLoc;

    private List<String> allProvince;
    private List<String> allCity;
    private List<String> allCounty;
    private ShowDataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_loc);

        ButterKnife.bind(this);

        allProvince = DBUtils.queryAllProvince();
        adapter = new ShowDataAdapter(allProvince,this);
        seletcLvLoc.setAdapter(adapter);

        seletcLvLoc.setOnItemClickListener(listener);
    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Log.i(TAG, "onItemClick: position = "+position);

            String name = (String) adapter.getItem(position);
            Log.i(TAG, "onItemClick: name = "+name);

            List<String> citys = DBUtils.queryAll(name);

            if(citys==null){
                //数据已经到了County了不能再查了 应当记录点击的
                Intent intent = new Intent();

                intent.putExtra("select_loc_data",name);
                setResult(RESULT_OK,intent);
                finish();
            }
            Log.i(TAG, "onItemClick: List<String> citys = "+citys);
            //不为空，那么就去显示城市或者城镇
            adapter.setData(citys);
        }
    };


    class ShowDataAdapter extends BaseAdapter{

        private List<String> cityList;
        private Context ctx;

        public ShowDataAdapter(List<String> cityList, Context ctx) {
            this.cityList = cityList;
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return cityList == null?0:cityList.size();
        }

        @Override
        public Object getItem(int position) {
            return cityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if(convertView == null) {
                convertView = View.inflate(ctx, R.layout.selectloc_lv_item, null);
                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.select_tv_cityName);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(cityList.get(position));

            return convertView;
        }

        public void setData(List<String> cityList){
            this.cityList = cityList;
            notifyDataSetChanged();
        }
    }

    class ViewHolder{
        TextView tv;
    }

}
