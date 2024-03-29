package rs.novosti.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import rs.novosti.R;
import rs.novosti.model.Article;
import rs.novosti.model.Main;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
/**
 * Adapter class for loading data into solo image gallery view
 * @author aleksandarvaricak
 *
 */
public class SoloImageGalleryAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Article> articles;
	private Context context;

	public SoloImageGalleryAdapter(Context context, List<Article> articles) {
		inflater = LayoutInflater.from(context);
		this.articles = articles;
		this.context = context;
	}

	@Override
	public int getCount() {
		return articles.size();
	}

	@Override
	public Object getItem(int position) {
		return articles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	/**
	 * Method for creating a new ImageView for each item referenced by the Adapter
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Article article = (Article)getItem(position);
		Holder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.solo_image_gallery_element,null );
			holder = new Holder();
			holder.image = (ImageView) convertView
					.findViewById(R.id.solo_gallery_image);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.image.setImageResource(R.drawable.spinner);
		new LoadImageTask(holder.image, article).execute();
		return convertView;
	}

	private class Holder {
		ImageView image;
		
	}
	
	public void clearAll(){
		for(Article article:articles){
			article.clear();
		}
	}
	/**
	 * AsyncTask for downloading image
	 * @author aleksandarvaricak
	 *
	 */
	private class LoadImageTask extends AsyncTask<Void, Void, Drawable>{
		private ImageView view;
		private Article article;
		
		LoadImageTask(ImageView view, Article article){
			this.view = view;
			this.article = article;
		}
		

		@Override
		protected Drawable doInBackground(Void ... params) {
			InputStream is = null;
			if(article.getBigDrawable()!=null)
				return article.getBigDrawable();
			if(article.getPhotoPath() == null)
				article = Main.getInstance().readArticle(article);
			String url = article.getPhotoPath();
			if (url != null) {
				url = url.replaceAll(" ", "%20");
				try {
					is = new URL(url).openStream();
					BitmapFactory.Options options = new BitmapFactory.Options();
					Bitmap bitmap = BitmapFactory.decodeStream(is,null, options);

					Drawable drawable = new BitmapDrawable(bitmap);
					article.setBigDrawable(drawable);
					return drawable;
				} catch (Exception e) {
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				return new BitmapDrawable(BitmapFactory.decodeResource(
						context.getResources(), R.drawable.no_image));
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Drawable result) {
			view.setImageDrawable(result);
			view.setBackgroundColor(Color.BLACK);
		}
		
	}
}

