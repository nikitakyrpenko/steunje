package com.negeso.module.rich_snippet.bo;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.negeso.framework.Env;
import com.negeso.framework.jaxb.RoundDateAdapter;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "rs-review", namespace = Env.NEGESO_NAMESPACE)
public class Review extends RichSnippet {

	private static final long serialVersionUID = -5724083308601550294L;
	
	@XmlAttribute
	private String url;
	@XmlAttribute
	private String author;
	@XmlAttribute
	@XmlJavaTypeAdapter(RoundDateAdapter.class)
	private Date datePublished = new Date(System.currentTimeMillis());
	@XmlAttribute
	private String itemsReviewed;
	@XmlAttribute
	private String reviewBody;
	@XmlAttribute
	private Integer rating;
	@XmlAttribute
	private Integer ratingScaleMin = 1;
	@XmlAttribute
	private Integer ratingScaleMax = 5;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}

	public String getItemsReviewed() {
		return itemsReviewed;
	}

	public void setItemsReviewed(String itemsReviewed) {
		this.itemsReviewed = itemsReviewed;
	}

	public String getReviewBody() {
		return reviewBody;
	}

	public void setReviewBody(String reviewBody) {
		this.reviewBody = reviewBody;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Integer getRatingScaleMin() {
		return ratingScaleMin;
	}

	public void setRatingScaleMin(Integer ratingScaleMin) {
		this.ratingScaleMin = ratingScaleMin;
	}

	public Integer getRatingScaleMax() {
		return ratingScaleMax;
	}

	public void setRatingScaleMax(Integer ratingScaleMax) {
		this.ratingScaleMax = ratingScaleMax;
	}
	
	@XmlAttribute
	public String getFiveBasedRating() {
		return getFiveBasedRating(ratingScaleMax, ratingScaleMin, new Double(rating));
	}
	
	public static void main(String[] args) {
		Review r = new Review();
		r.setRating(55);
		r.setRatingScaleMin(44);
		r.setRatingScaleMax(60);
		System.out.println(r.getFiveBasedRating());
	}
}
