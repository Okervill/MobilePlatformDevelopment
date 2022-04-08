//Patrick Lardner S1829335

package org.me.mpd;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button currentIncidentsButton = (Button) view.findViewById(R.id.currentIncidentsButton);
        Button roadworksButton = (Button) view.findViewById(R.id.roadworksButton);
        Button plannedRoadworksButton = (Button) view.findViewById(R.id.plannedRoadworksButton);

        currentIncidentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ItemListFragment("https://trafficscotland.org/rss/feeds/currentincidents.aspx", "incidents");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.displayFragment, fragment);
                transaction.commit();
            }
        });

        roadworksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ItemListFragment("https://trafficscotland.org/rss/feeds/roadworks.aspx", "roadworks");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.displayFragment, fragment);
                transaction.commit();
            }
        });

        plannedRoadworksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ItemListFragment("https://trafficscotland.org/rss/feeds/plannedroadworks.aspx", "planned");
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.displayFragment, fragment);
                transaction.commit();
            }
        });
    }
}
