package com.negeso.module.rich_snippet.bo;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.negeso.framework.Env;
import com.negeso.framework.jaxb.RoundDateAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "rs-video", namespace = Env.NEGESO_NAMESPACE)
public class Video extends RichSnippet {

	private static final long serialVersionUID = 6448791409592235308L;
	
	@XmlAttribute
	private String thumbnailUrl;
	@XmlAttribute
	private Integer duration;
	@XmlAttribute
	private String contentUrl;
	@XmlAttribute
	private String embedUrl;
	@XmlAttribute
	@XmlJavaTypeAdapter(RoundDateAdapter.class)
	private Date uploadDate;
	@XmlAttribute
	@XmlJavaTypeAdapter(RoundDateAdapter.class)
	private Date expires;
	@XmlAttribute
	private String extraField;

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public String getEmbedUrl() {
		return embedUrl;
	}

	public void setEmbedUrl(String embedUrl) {
		this.embedUrl = embedUrl;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public String getExtraField() {
		return extraField;
	}

	public void setExtraField(String extraField) {
		this.extraField = extraField;
	}
}
