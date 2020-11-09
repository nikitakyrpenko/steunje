package com.negeso.module.rich_snippet.bo;

import java.text.DecimalFormat;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.negeso.framework.Env;
import com.negeso.framework.dao.Entity;

@XmlTransient
public class RichSnippet implements Entity {

	private static final long serialVersionUID = 6123274369094885945L;

	public enum RichSnippetType {
		review, product, video, aggregate_review
	}

	@XmlAttribute
	private Long id;
	@XmlAttribute
	private Long orderNumber;
	@XmlTransient
	private String discrim;
	@XmlAttribute
	private String name;
	@XmlElement(namespace = Env.NEGESO_NAMESPACE)
	private String description;

	public static String getFiveBasedRating(Integer ratingScaleMax, Integer ratingScaleMin, Double rating  ) {
		int minRating = 1;
		int maxRating = 5;
		if (ratingScaleMax != null) {
			maxRating = ratingScaleMax;
		}
		if (ratingScaleMin != null && ratingScaleMin > 1 && ratingScaleMin < maxRating) {
			minRating = ratingScaleMin;
		}
		if (rating == null || rating <= minRating) {
			return "1";
		} else if (rating >= maxRating) {
			return "5";
		} else {
			return new DecimalFormat("###.##").format((rating - minRating + 1) / ((maxRating - minRating + 1) / 5.0d));
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getDiscrim() {
		return discrim;
	}

	public void setDiscrim(String discrim) {
		this.discrim = discrim;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
