//Patrick Lardner S1829335

package org.me.mpd;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ItemListFragment extends Fragment {
    private ArrayList<Item> items;
    private ArrayList<Item> filtered = new ArrayList<Item>();
    private String url = "";
    private String itemType = "";

    public ItemListFragment() {
        //Required empty constructor
    }

    public ItemListFragment(String source_url, String type) {
        url = source_url;
        itemType = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_list_fragment, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listview = view.findViewById(R.id.itemDisplay);
        EditText filterInput = view.findViewById(R.id.filter_input);
        DatePicker filterDate = view.findViewById(R.id.searchDate);

        filterDate.setMinDate(System.currentTimeMillis());

        if (itemType.equalsIgnoreCase("incidents")) {
            filterDate.setVisibility(View.GONE);
        }

        new Thread(new Runnable() {

            @Override
            public void run() {

                //Check network connection first
                ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                boolean connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();

                if (!connected) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CharSequence text = "Not connected";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getContext(), text, duration);
                            toast.show();
                        }
                    });

                    return;
                }

                XMLParser xmlparser = new XMLParser();
                xmlparser.startProgress(url, itemType);
                items = xmlparser.getItems();
                try {
                    if (items == null) {
                        //loading stuff
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                ArrayList<String> itemTitles = new ArrayList<String>();
                                for (Item i : items) {
                                    filtered.add(i);
                                    itemTitles.add(i.getTitle());
                                    if (i.getType() == "roadworks" | i.getType() == "planned") {
                                        i.setStartDate(i.getDescription().split("<br />")[0].substring(12, i.getDescription().split("<br />")[0].length() - 8));
                                        i.setEndDate(i.getDescription().split("<br />")[1].substring(10, i.getDescription().split("<br />")[1].length() - 8));
                                    }
                                }

                                ArrayAdapter adapter = new ArrayAdapter<String>(view.getContext(), R.layout.list_item, R.id.list_item, itemTitles);
                                listview.setAdapter(adapter);

                                for (int i = 0; i <= listview.getLastVisiblePosition() - listview.getFirstVisiblePosition(); i++) {
                                    if (listview.getChildAt(i) == null) {
                                        Log.e("Child", i + "Is null");
                                    } else {
                                        Log.e("Child", i + "Is not null");
                                    }
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("Thread:", e.toString());
                }
            }
        }).start();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item item = filtered.get(i);
                Intent intent = new Intent(getActivity(), ItemDisplayActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("description", item.getDescription());
                intent.putExtra("link", item.getLink());
                intent.putExtra("date", item.getDate().toString());
                intent.putExtra("latitude", item.getLatitude());
                intent.putExtra("longitude", item.getLongitude());
                intent.putExtra("type", item.getType());

                getContext().startActivity(intent);
            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        filterDate.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Date selectedDate = new Date();
                        try {
                            selectedDate = new SimpleDateFormat("yyyy M d").parse(year + " " + (month + 1) + " " + dayOfMonth);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        filtered.clear();
                        ArrayList<String> itemTitles = new ArrayList<String>();
                        if (items == null) {
                            return;
                        }
                        for (Item i : items) {
                            if (isBetweenDates(selectedDate, i.getStartDate(), i.getEndDate())) {
                                filtered.add(i);
                                itemTitles.add(i.getTitle());
                            }
                        }

                        ArrayAdapter adapter = new ArrayAdapter<String>(view.getContext(), R.layout.list_item, R.id.list_item, itemTitles);
                        listview.setAdapter(adapter);
                    }
                });
            }
        });

        filterInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (items == null) {
                            return;
                        }
                        filtered.clear();
                        ArrayList<String> itemTitles = new ArrayList<String>();
                        if (charSequence.length() == 0) {
                            for (Item i : items) {
                                filtered.add(i);
                                itemTitles.add(i.getTitle());
                            }
                        } else {
                            for (Item i : items) {
                                if (i.getTitle().toLowerCase().contains((CharSequence) charSequence.toString().toLowerCase())) {
                                    filtered.add(i);
                                    itemTitles.add(i.getTitle());
                                }
                            }
                        }

                        ArrayAdapter adapter = new ArrayAdapter<String>(view.getContext(), R.layout.list_item, R.id.list_item, itemTitles);
                        listview.setAdapter(adapter);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    public boolean isBetweenDates(Date d, Date start, Date end) {
        return start.compareTo(d) * d.compareTo(end) >= 0;
    }

}
