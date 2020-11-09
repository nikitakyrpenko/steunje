package com.negeso.module.translator.service;

import com.negeso.framework.domain.Article;
import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.domain.Language;
import com.negeso.module.form_manager.FormAnalyzer;
import com.negeso.module.form_manager.domain.FormField;
import com.negeso.module.form_manager.domain.Forms;
import com.negeso.module.translator.exception.TranslationExeption;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;

import java.sql.*;

public class FormTranslator extends AbstractTranslator {
	

	private static final Logger logger = Logger.getLogger(FormTranslator.class);
	
	private static final String DELETE_FROM_FORMS = "DELETE FROM forms WHERE lang_id=?";
	private static final String SELECT_FROM_FORMS = "SELECT * FROM forms WHERE lang_id=? ORDER BY id";
	
	public FormTranslator(ITranslateService translateService) {
		super(translateService);
	}

	@Override
	public void copyAndTranslate(Connection con, Language from, Language to) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.prepareStatement(SELECT_FROM_FORMS);
			stmt.setLong(1, from.getId());
			rs = stmt.executeQuery();
			if (rs.next()) {
				Forms forms = new Forms();
				forms.load(rs);
				Long oldFormId = forms.getId();
				oldFormIdToNewFormObjectMap.put(oldFormId, forms);
				Long newFormId = DBHelper.getNextInsertId(con, "forms_id_seq");
				forms.setId(newFormId);
				forms.setFormId(newFormId.toString());
				forms.setLangId(to.getId());
				Article article = copyAndTranslateArticle(translateService, con, forms.getArticleId(), from, to);
				forms.setArticleId(article != null ? article.getId() : null);
				forms.setArticle(article);
				forms.setName(translateService.translate(forms.getName(), from.getCode(), to.getCode()));
				forms.setDescription(translateService.translate(forms.getDescription(), from.getCode(), to.getCode()));
				forms.setCreationDate(new Timestamp(System.currentTimeMillis()));
				forms.setLastModificationDate(new Timestamp(System.currentTimeMillis()));
				forms.setPageId(null);
				forms.insert(con);
				FormAnalyzer formAnalyzer = new FormAnalyzer();
				formAnalyzer.setForm(forms);
			    formAnalyzer.parseFormFields(forms.getArticle().getText());
			    for (FormField formField : forms.getFields()) {
					stmt = con.prepareStatement("INSERT INTO forms_field (form_id,name,order_number,visible,version) VALUES (?,?,?,?,?)");
					stmt.setLong(1, newFormId);
					stmt.setString(2, formField.getName());
					stmt.setInt(3, formField.getOrderNumber());
					stmt.setBoolean(4, formField.isVisible());
					stmt.setInt(5, 0);
					stmt.execute();
				}
			}
		} catch (Exception e) {
			logger.error("Error: ", e);
			throw new TranslationExeption("Unable to translate forms to lang: " + to.getCode());
		} finally {
			DBHelper.close(rs, stmt, null);
		}
	}

	@Override
	public void copyAndTranslate(SessionFactory factory) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clean(Connection con, Language to) {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(DELETE_FROM_FORMS);
			stmt.setLong(1, to.getId());
			stmt.execute();
		} catch (SQLException e) {
			throw new TranslationExeption("Unable to clear forms for lang: " + to.getCode());
		} finally {
			DBHelper.close(stmt);
		}
	}

	@Override
	public void clean(SessionFactory factory) {
		// TODO Auto-generated method stub
		
	}

}
