package com.rilixtech.shelfview;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.rilixtech.numberprogressbar.NumberProgressBar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.util.List;

class ShelfAdapter extends BaseAdapter {
  private static final String TAG = "ShelfAdapter";
  private Context mContext;
  private List<Shelf> mShelves;
  private String internalStorage;
  private String mRawPath;
  private int mTargetWidth;
  private int mTargetHeight;

  ShelfAdapter(Context ctx, List<Shelf> shelves) {
    mContext = ctx;
    mShelves = shelves;
    mTargetWidth = Utils.dpToPixels(ctx, ctx.getResources().getInteger(R.integer.book_width));
    mTargetHeight = Utils.dpToPixels(ctx, ctx.getResources().getInteger(R.integer.book_height));
  }

  @Override public int getCount() {
    return mShelves.size();
  }

  @Override public Object getItem(int position) {
    return mShelves.get(position);
  }

  @Override public long getItemId(int position) {
    return 0L;
  }

  private static class ViewHolder {
    TextView tvId;
    ImageView imvShelfBackground;
    ImageView imvBookCover;
    RelativeLayout rlyBookBackground;
    RelativeLayout rlyBookCover;
    TextView tvNoCover;
    ProgressBar pgbLoad;
    NumberProgressBar npbReadProgress;
    View vSpineWhite, vSpineGrey;
  }

  @Override public View getView(int position, View convertView, ViewGroup parent) {
    final ViewHolder holder;
    if (convertView == null) {
      LayoutInflater inflater = LayoutInflater.from(mContext);
      convertView = inflater.inflate(R.layout.book_shelf_grid_item, parent, false);
      holder = new ViewHolder();
      holder.tvId = convertView.findViewById(R.id.book_cover_id_tv);
      holder.imvShelfBackground = convertView.findViewById(R.id.shelf_background);
      holder.imvBookCover = convertView.findViewById(R.id.book_cover_imv);
      holder.rlyBookBackground = convertView.findViewById(R.id.book_background_rly);
      holder.rlyBookCover = convertView.findViewById(R.id.book_cover_rly);
      holder.tvNoCover = convertView.findViewById(R.id.book_cover_not_found_tv);
      holder.pgbLoad = convertView.findViewById(R.id.load_pgb);
      holder.npbReadProgress = convertView.findViewById(R.id.book_cover_read_progress_npb);
      holder.vSpineWhite = convertView.findViewById(R.id.spine_white_view);
      holder.vSpineGrey = convertView.findViewById(R.id.spine_grey_view);
      convertView.setTag(holder);
    } else {
      // Recycle view
      holder = (ViewHolder) convertView.getTag();
    }

    Shelf shelf = mShelves.get(position);
    switch (shelf.getType()) {
      case START:
        holder.imvShelfBackground.setImageResource(R.drawable.grid_item_background_left);
        break;
      case END:
        holder.imvShelfBackground.setImageResource(R.drawable.grid_item_background_right);
        break;
      default:
        holder.imvShelfBackground.setImageResource(R.drawable.grid_item_background_center);
        break;
    }

    ShelfBook book = shelf.getShelfBook();

    // Always assume there is no book and image found found so we can reuse the view
    if (book == null) {
      holder.tvId.setText(null);
      holder.rlyBookCover.setVisibility(View.GONE);
      holder.pgbLoad.setVisibility(View.GONE);
      holder.npbReadProgress.setVisibility(View.GONE);
      holder.tvNoCover.setText(Configuration.getNoImageFound());
    } else {
      holder.tvId.setText(book.getId());
      holder.pgbLoad.setVisibility(View.VISIBLE);
      holder.npbReadProgress.setProgress(book.getProgress());
      holder.npbReadProgress.setVisibility(book.getProgress() > 0 ? View.VISIBLE : View.INVISIBLE);
      loadImageWithPicasso(mContext, book, holder);

      if (Configuration.isTitleAsPlaceHolder()) {
        holder.tvNoCover.setText(book.getTitle());
      }
    }

    return convertView;
  }

  void setBookProgress(List<View> views, String bookId, int progress) {
    ShelfBook book = null;
    for (int i = 0; i < mShelves.size(); i++) {
      ShelfBook b = mShelves.get(i).getShelfBook();
      if (b.getId().equals(bookId)) {
        b.setProgress(progress);
        book = b;
        break;
      }
    }
    if (book == null) return;

    for (int i = 0; i < views.size(); i++) {
      View view = views.get(i);
      if (view == null) continue;
      ViewHolder viewHolder = (ViewHolder) view.getTag();
      if (viewHolder == null) continue;
      String id = viewHolder.tvId.getText().toString();
      if (id.equals(bookId)) {
        Log.d(TAG, "book progress updated form bookId = " + bookId);
        book.setProgress(progress);
        viewHolder.npbReadProgress.setProgress(progress);
        viewHolder.npbReadProgress.setVisibility(View.VISIBLE);
        break;
      }
    }
  }

  private void loadImageWithPicasso(Context ctx, final ShelfBook book, final ViewHolder holder) {
    Callback callback = new Callback() {
      @Override public void onSuccess() {
        holder.rlyBookCover.setVisibility(View.VISIBLE);
        holder.pgbLoad.setVisibility(View.GONE);
        holder.tvNoCover.setVisibility(View.GONE);
      }

      @Override
      public void onError(Exception e) {
        showNoImageFound(holder);
        Log.d(TAG, "Error when loading image from " + book.getCoverSource());
      }

//      @Override public void onError() {
//        showNoImageFound(holder);
//        Log.d(TAG, "Error when loading image from " + book.getCoverSource());
//      }
    };

    String bookCover = book.getCoverSource();
    if (bookCover == null || bookCover.isEmpty()) {
      showNoImageFound(holder);
      return;
    }

    switch (book.getBookSourceType()) {
      case FILE:
        Picasso.get()
            .load(new File(/*getInternalStorage() +*/ bookCover))
            .resize(mTargetWidth, mTargetHeight)
            .into(holder.imvBookCover, callback);
        break;
      case URL:
        Picasso.get()
            .load(bookCover)
            .resize(mTargetWidth, mTargetHeight)
            .into(holder.imvBookCover, callback);
        break;
      case ASSET_FOLDER:
        Picasso.get()
            .load("file:///android_asset/" + bookCover)
            .resize(mTargetWidth, mTargetHeight)
            .into(holder.imvBookCover, callback);
        break;
      case DRAWABLE_NAME:
        Picasso.get()
            .load(ctx.getResources().getIdentifier(bookCover, "drawable", ctx.getPackageName()))
            .resize(mTargetWidth, mTargetHeight)
            .into(holder.imvBookCover, callback);
        break;
      case DRAWABLE_ID:
        Picasso.get()
            .load(Integer.parseInt(bookCover))
            .resize(mTargetWidth, mTargetHeight)
            .into(holder.imvBookCover, callback);
        break;
      case RAW:
        Uri uri = Uri.parse(getRawPath() + bookCover);
        Picasso.get()
            .load(uri)
            .resize(mTargetWidth, mTargetHeight)
            .into(holder.imvBookCover, callback);
        break;
      case NONE:
        Picasso.get()
            .load(bookCover)
            .resize(mTargetWidth, mTargetHeight)
            .into(holder.imvBookCover, callback);
        break;
    }
  }

  private void showNoImageFound(ViewHolder holder) {
    holder.tvNoCover.setVisibility(View.VISIBLE);
    holder.pgbLoad.setVisibility(View.GONE);
    holder.rlyBookCover.setVisibility(View.VISIBLE);
  }

  private String getRawPath() {
    if (mRawPath == null) {
      mRawPath = "android.resource://" + mContext.getPackageName() + "/";
    }
    return mRawPath;
  }

  private String getInternalStorage() {
    if (internalStorage == null) {
      this.internalStorage = Environment.getExternalStorageDirectory().toString();
    }
    return internalStorage;
  }
}