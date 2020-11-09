package com.negeso.module.rich_snippet.bo;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.negeso.framework.Env;
import com.negeso.framework.jaxb.DecimalFormatAdapter;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "rs-aggregate-review", namespace = Env.NEGESO_NAMESPACE)
public class AggregateReview extends RichSnippet {

	private static final long serialVersionUID = -2088895818776053234L;
	@XmlAttribute
	private Integer bestRating = 5;
	@XmlAttribute
	@XmlJavaTypeAdapter(DecimalFormatAdapter.class)
	private BigDecimal ratingValue;
	@XmlAttribute
	private Integer worstRating = 1;
	@XmlAttribute
	private Integer ratingCount;
	@XmlAttribute
	private Integer reviewCount;
	
	@XmlAttribute
	public String getFiveBasedRating() {
		return getFiveBasedRating(bestRating, worstRating, ratingValue != null ? ratingValue.doubleValue() : null);
	}

	public Integer getBestRating() {
		return bestRating;
	}

	public void setBestRating(Integer bestRating) {
		this.bestRating = bestRating;
	}

	public BigDecimal getRatingValue() {
		return ratingValue;
	}

	public void setRatingValue(BigDecimal ratingValue) {
		this.ratingValue = ratingValue;
	}

	public Integer getWorstRating() {
		return worstRating;
	}

	public void setWorstRating(Integer worstRating) {
		this.worstRating = worstRating;
	}

	public Integer getRatingCount() {
		return ratingCount;
	}

	public void setRatingCount(Integer ratingCount) {
		this.ratingCount = ratingCount;
	}

	public Integer getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(Integer reviewCount) {
		this.reviewCount = reviewCount;
	}
}
