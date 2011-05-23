package rs.novosti.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import rs.novosti.NovostiCela;
import rs.novosti.R;
import rs.novosti.model.Article;
import rs.novosti.model.Main;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LatestNewsGalleryAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Article> articles;
	private Context context;


	public LatestNewsGalleryAdapter(Context context, List<Article> articles) {
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Article article = Main.getInstance().readArticle(articles.get(position));
		Holder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.first_article_style, null);
			holder = new Holder();
			holder.articleLayout = (LinearLayout) convertView
					.findViewById(R.id.firstStyleArticle);
			holder.latestArticleTitle = (TextView) convertView
					.findViewById(R.id.firstStyleArticle_title);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		// Article article = articles.get(position);
		// System.out.println(article.getName());
		// new BitmapFactory();
		// holder.latestArticleImage.setImageBitmap(BitmapFactory.decodeFile(article.getPhotoPath()));
		holder.latestArticleTitle.setText(article.getTitle());
		holder.latestArticleTitle
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent myIntent = new Intent(v.getContext(),
								NovostiCela.class);
						myIntent.putExtra("article", article);
						((Activity)context).startActivityForResult(myIntent, 0);
					}
				});
		// holder.latestArticleTitle.setText(article.getName());
		if (article.getBigDrawable() == null)
			article.setBigDrawable(getResizedDrawable(article.getPhotoPath()));
		holder.articleLayout.setBackgroundDrawable(article.getBigDrawable());
		return convertView;
	}

	private Drawable getResizedDrawable(String url) {
		
		InputStream is = null;
		url = url.replaceAll(" ", "%20");
		try {
			is = new URL(url).openStream();
			BitmapFactory.Options options = new BitmapFactory.Options();
			//options.inSampleSize = 2;
			Bitmap bitmap = BitmapFactory.decodeStream(is,null, options);

			// BitmapFactory.Options op = new BitmapFactory.Options();
			// op.inSampleSize = 8;
			// bitmap = BitmapFactory.decodeStream(is,null, op);
			// int width = bitmap.getWidth();
			// int height = bitmap.getHeight();
//			int screenWidth = ((Activity)context).getWindowManager().getDefaultDisplay()
//					.getWidth();
//			int screenHeight = ((Activity)context).getWindowManager().getDefaultDisplay()
//					.getHeight();
//			int maxHeight = (screenHeight - 100) / 2;
//			Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, screenWidth, maxHeight,
//					true);
//			bitmap.recycle();
			Drawable drawable = new BitmapDrawable(bitmap);
			// drawable.setBounds(0, 0, screenWidth, maxHeight);
			// bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width * ratio),
			// ((int) (height * ratio) > maxHeight ? maxHeight
			// : (int) (height * ratio)), true);
			return drawable;
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private class Holder {
		LinearLayout articleLayout;
		TextView latestArticleTitle;

	}
	
	public void clearAll(){
		for(Article article:articles){
			article.clear();
		}
	}
}