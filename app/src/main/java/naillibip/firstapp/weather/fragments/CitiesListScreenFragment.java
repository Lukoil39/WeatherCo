package naillibip.firstapp.weather.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import naillibip.firstapp.weather.CityCard;
import naillibip.firstapp.weather.CityPreference;
import naillibip.firstapp.weather.R;
import naillibip.firstapp.weather.adapters.CitiesListRecyclerViewAdapter;
import naillibip.firstapp.weather.callBackInterfaces.IAdapterCallbacks;
import naillibip.firstapp.weather.callBackInterfaces.IAddCityCallback;

public class CitiesListScreenFragment extends Fragment implements IAdapterCallbacks, IAddCityCallback {

    private TextInputEditText enterCityEditText;
    private TextInputLayout enterCityInputLayout;
    private RecyclerView recyclerView;
    private CitiesListRecyclerViewAdapter adapter;
    private boolean isTempScreenExists;
    private String cityName;
    private ArrayList<CityCard> list;
    private CityPreference cityPreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cities_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initList(savedInstanceState);
        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);cityPreference = new CityPreference(Objects.requireNonNull(getActivity()));
        cityName = cityPreference.getCity();
//        if (savedInstanceState != null) {
//            cityName = savedInstanceState.getString("city", "Moscow");
//        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("city", cityName);
        outState.putParcelableArrayList("list", list);
//        cityPreference.setList(list);
        super.onSaveInstanceState(outState);
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);

    }

    private void initList(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            CityCard[] data = new CityCard[]{
                    new CityCard(getString(R.string.moscow)),
                    new CityCard(getString(R.string.spb))
            };
            list = new ArrayList<>(data.length);
            list.addAll(Arrays.asList(data));
        } else {
            list = savedInstanceState.getParcelableArrayList("list");
            //list = cityPreference.getList();
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        adapter = new CitiesListRecyclerViewAdapter(list, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    private void showTempScreen(String city) {
            TempScreenFragment detail = TempScreenFragment.newInstance(city);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(android.R.id.content, detail);
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.addToBackStack("Some_key");
            fragmentTransaction.commit();

    }


    @Override
    public void startTempScreenFragment(String city) {
        // cityName = city;
        cityPreference.setCity(city);
        showTempScreen(city);
    }

    @Override
    public boolean addCityToList() {


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_city_input, null);
        enterCityEditText = dialogView.findViewById(R.id.add_city_to_recycler);
        enterCityInputLayout = dialogView.findViewById(R.id.add_city_txt_layout);
        builder.setView(dialogView)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String city = enterCityEditText.getText().toString();
                        if (!city.isEmpty() && !adapter.checkIsItemInData(city)) {
                            CityCard cityCard = new CityCard(city);
                            adapter.addItem(cityCard);
                            recyclerView.scrollToPosition(0);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).create().show();


        return false;
    }


    @Override
    public void deleteCityFromList() {
        adapter.removeItem();
    }
}
