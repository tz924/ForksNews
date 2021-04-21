package com.example.forksnews;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

// Specify the custom ViewHolder which gives us access to our views
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

  public static class ViewHolder extends RecyclerView.ViewHolder {

    private final Context context;

    public TextView relatedTitleView;
    public TextView relatedContributorView;
    public TextView relatedTimeView;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public ViewHolder(Context context, @NonNull View itemView) {
      super(itemView);

      // capture views
      this.relatedTitleView = itemView.findViewById(R.id.title_related);
      this.relatedContributorView = itemView.findViewById(R.id.contributor_related);
      this.relatedTimeView = itemView.findViewById(R.id.time_related);

      // store context
      this.context = context;

      // store views
    }
  }

  // member variables and constructor
  private final List<News> newsList;

  public NewsAdapter(List<News> newsList) {
    this.newsList = newsList;
  }


  /**
   * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent an
   * item.
   * <p>
   * This new ViewHolder should be constructed with a new View that can represent the items of the
   * given type. You can either create a new View manually or inflate it from an XML layout file.
   * <p>
   * The new ViewHolder will be used to display items of the adapter using {@link
   * #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display different items
   * in the data set, it is a good idea to cache references to sub views of the View to avoid
   * unnecessary {@link View#findViewById(int)} calls.
   *
   * @param parent   The ViewGroup into which the new View will be added after it is bound to an
   *                 adapter position.
   * @param viewType The view type of the new View.
   * @return A new ViewHolder that holds a View of the given view type.
   * @see #getItemViewType(int)
   * @see #onBindViewHolder(ViewHolder, int)
   */
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    // Inflate news item layout and return the view holder
    View relatedNewsView = inflater.inflate(R.layout.item_related, parent, false);
    return new ViewHolder(context, relatedNewsView);
  }

  /**
   * Called by RecyclerView to display the data at the specified position. This method should update
   * the contents of the {@link ViewHolder#itemView} to reflect the item at the given position.
   * <p>
   * Note that unlike {@link ListView}, RecyclerView will not call this method again if the position
   * of the item changes in the data set unless the item itself is invalidated or the new position
   * cannot be determined. For this reason, you should only use the <code>position</code> parameter
   * while acquiring the related data item inside this method and should not keep a copy of it. If
   * you need the position of an item later on (e.g. in a click listener), use {@link
   * ViewHolder#getAdapterPosition()} which will have the updated adapter position.
   * <p>
   * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can handle
   * efficient partial bind.
   *
   * @param holder   The ViewHolder which should be updated to represent the contents of the item at
   *                 the given position in the data set.
   * @param position The position of the item within the adapter's data set.
   */
  @RequiresApi(api = VERSION_CODES.O)
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    // populate data into the item through holder
    News news = newsList.get(position);

    // Set up views
    holder.relatedTitleView.setText(news.getTitle());
    holder.relatedContributorView.setText(news.getContributor());
    holder.relatedTimeView.setText(news.getPast());
  }

  /**
   * Returns the total number of items in the data set held by the adapter.
   *
   * @return The total number of items in this adapter.
   */
  @Override
  public int getItemCount() {
    return newsList.size();
  }

  // clear out old data
  public void clear() {
    this.newsList.clear();
    notifyItemRangeRemoved(0, this.newsList.size());
  }

  // add new data
  public void addAll(List<News> newsList) {
    this.newsList.addAll(newsList);
    notifyDataSetChanged();
  }

  public News getItem(int position) {
    return this.newsList.get(position);
  }
}
