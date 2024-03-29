package rs.novosti.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Class represents category with needed data and list of articles in that category
 * @author aleksandarvaricak
 *
 */
public class Category implements Serializable {

	private static final long serialVersionUID = 1L;
	private String title;
	private String path;
	private List<Article> articles;

	public Category() {
		articles = new ArrayList<Article>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	public void clear() {
		for(Article article:articles)
			article.clear();
	}
	
	@Override
	public String toString() {
		String returnString = title+"\n";
		for(Article article:articles){
			returnString+=article.toString()+"\n";
		}
		return returnString;
	}

}
