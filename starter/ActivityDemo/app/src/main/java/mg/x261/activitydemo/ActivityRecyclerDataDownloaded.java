package mg.x261.activitydemo;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class ActivityRecyclerDataDownloaded extends AppCompatActivity {
    private int currentPage = 1; // Current page of data to download
    private static final int ITEMS_PER_PAGE = 10; // Number of items to show per page
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recycler_view_data_downloaded);

        // Set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.my_recycler_view_for_downloaded_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the progress bar
        progressBar = findViewById(R.id.progress_bar);

        // Download the first page of data
        downloadData(recyclerView, currentPage);
    }


    // Download data from the server for a specific page
    private void downloadData(RecyclerView recyclerView, int page) {

        // Show the progress bar
        progressBar.setVisibility(View.VISIBLE);

        String url = "https://studio.mg/api-country/index.php?page=" + page;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Hide the progress bar
                        progressBar.setVisibility(View.GONE);
                        try {
                            JSONArray data = response.getJSONArray("data");
                            ArrayList<DataObject> dataObjects = parseJsonData(data);
                            recyclerView.setAdapter(new MyAdapter(dataObjects));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Hide the progress bar
                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(getApplicationContext(), "Error downloading data",
                                Toast.LENGTH_SHORT).show();
                        Log.d("TAG", "Error downloading data :" + error);
                    }
                });

        queue.add(jsonObjectRequest);
    }


    // Parse the JSON data and return an ArrayList of DataObject
    private ArrayList<DataObject> parseJsonData(JSONArray jsonArray) {
        ArrayList<DataObject> dataObjects = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String country = jsonObject.getString("country");
                String capital = jsonObject.getString("capital");
                dataObjects.add(new DataObject(country, capital));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataObjects;
    }

    // RecyclerView adapter
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private ArrayList<DataObject> dataObjects;

        public MyAdapter(ArrayList<DataObject> dataObjects) {
            this.dataObjects = dataObjects;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recycler_view_design_two, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            DataObject dataObject = dataObjects.get(position);
            holder.textViewUp.setText(dataObject.getCountry());
            holder.textViewDown.setText(dataObject.getCapital());
        }

        @Override
        public int getItemCount() {
            return dataObjects.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView textViewUp;
            public TextView textViewDown;

            public MyViewHolder(View itemView) {
                super(itemView);
                textViewUp = itemView.findViewById(R.id.item_recycler_textview_up);
                textViewDown = itemView.findViewById(R.id.item_recycler_textview_down);

                // Set click listener on the item view
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getBindingAdapterPosition();
                        DataObject dataObject = dataObjects.get(position);
                        Toast.makeText(itemView.getContext(), "Clicked on " + dataObject.getCountry(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    // Data object class
    private class DataObject {
        private String country;
        private String capital;

        public DataObject(String country, String capital) {
            this.country = country;
            this.capital = capital;
        }

        public String getCountry() {
            return country;
        }

        public String getCapital() {
            return capital;
        }
    }

}

