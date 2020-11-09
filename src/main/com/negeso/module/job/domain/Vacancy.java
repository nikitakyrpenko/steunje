package com.negeso.module.job.domain;

import com.negeso.framework.domain.*;
import com.negeso.module.job.JobModule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/*
 * @version 1.0
 * @author Alexander G. Shkabarnya
 * Mar 21, 2005
 */

public class Vacancy extends AbstractDbObject{
	
	private static final long serialVersionUID = 2470242337264513758L;

	private static Logger logger = Logger.getLogger( Vacancy.class );

    private static final String NEW_ARTICLE_TEXT = "New vacancy description";
    
    private static String tableId = "job_vacancies";

    private static final String selectFromSql =
        " SELECT id, title, position, article_id, salary, needed, " +
        " publish_date, expire_date, type, department_id, region_id " +
        " FROM " + tableId;

    private static final String findByIdSql =
        selectFromSql +
        " WHERE id = ? ";

    private static final String insertSql =
        " INSERT INTO " + tableId +
        " (id, title, position, article_id, salary, needed, " +
        " publish_date, expire_date, type, department_id, region_id ) "+
        " VALUES (?,?,?,?,?,?,?,?,?,?,?) ";

    private static final String updateSql =
        " UPDATE " + tableId +
        " SET id=?, title=?, position=?, article_id=?, salary=?, needed=?, " +
        " publish_date=?, expire_date=?, type=?, department_id=?, region_id=? " +
        " WHERE id=? ";

    private static final String SELECT_BY_SQL_PART_1 =
    	" SELECT job_vacancies.id, title, \"position\", article_id, salary, needed, " +
    	"   publish_date, expire_date, \"type\", article.text, department_id, region_id " +
    	" FROM job_vacancies " +
    	" LEFT JOIN article " +
    	"   ON job_vacancies.article_id=article.id " +
    	" LEFT JOIN job_vac2lang_presence " +
    	" ON (job_vac2lang_presence.j_vac_id = job_vacancies.id)";
    
    private static final String SELECT_BY_SQL_PART_2 =
    	" AND needed > 0 " +
    	" AND " +
    	" ((publish_date IS Null AND expire_date IS Null) OR " +
    	"  (publish_date<=now() AND expire_date IS Null) OR " +
    	"  (publish_date IS Null AND expire_date>=now()) OR " +
    	"  (publish_date<=now() AND expire_date>=now()) )" +
    	" AND job_vac2lang_presence.lang_id = ? " +
    	" AND job_vac2lang_presence.is_present = ?" +
    	" ORDER BY title ";
    
    private static final String selectByDepartmentIdSql =
    	SELECT_BY_SQL_PART_1 +
        " WHERE job_vacancies.department_id=? " +
        SELECT_BY_SQL_PART_2;
    
    public static final String selectByRegionIdSql =
    	SELECT_BY_SQL_PART_1 +
        " WHERE job_vacancies.region_id=? " +
        SELECT_BY_SQL_PART_2;
    

    private static int fieldCount = 11;
    
    
    public final static String getTemplateFieldsSql = 
        " SELECT * " +
        " FROM job_extra_fields " +
        " WHERE job_extra_fields.template_id = ?" +
        " ORDER BY order_number "
    ;

    public final static String getVacancyExtraFieldsSql = 
        " SELECT * " +
        " FROM job_extra_fields " +
        " WHERE (job_extra_fields.vacancy_id = ?) AND NOT is_removed " +
        " ORDER BY order_number "
    ;

    public final static String getGeneralVacancyExtraFieldsSql = 
        " SELECT * " +
        " FROM job_extra_fields " +
        " WHERE " +
        "   (NOT is_removed) " +
        "   AND (" +
        "   (job_extra_fields.template_id = ?) OR " + 
        "   (job_extra_fields.vacancy_id = ?)" +
        "   ) " +
        " ORDER BY order_number "
    ;

    
    private Long id = null;
    private String title = null;
    private String position = null;
    private Long articleId = null;
    private String salary = null;
    private Long needed = null;
    private Timestamp publishDate = null;
    private Timestamp expireDate = null;
    private String type = null;
    private Long departmentId = null;
    private Long regionId = null;

    public Long getId() {
        return id;
    }

    public void setId(Long newId) {
        id = newId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long article_id) {
        this.articleId = article_id;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public Long getNeeded() {
        return needed;
    }

    public void setNeeded(Long needed) {
        this.needed = needed;
    }

    public Timestamp getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Timestamp publish_date) {
        this.publishDate = publish_date;
    }

    public Timestamp getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Timestamp expire_date) {
        this.expireDate = expire_date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long load(ResultSet rs) throws CriticalException {
        logger.debug( "+" );
        try{
            this.id = (Long) DomainHelper.fromObject(rs.getObject("id"));
            this.title = (String) DomainHelper.fromObject(rs.getObject("title"));
            this.position = (String) DomainHelper.fromObject(rs.getObject("position"));
            this.articleId = (Long) DomainHelper.fromObject(rs.getObject("article_id"));
            this.salary = (String) DomainHelper.fromObject(rs.getObject("salary"));
            this.needed = (Long) DomainHelper.fromObject(rs.getObject("needed"));
            this.publishDate = (Timestamp) DomainHelper.fromObject(rs.getObject("publish_date"));
            this.expireDate = (Timestamp) DomainHelper.fromObject(rs.getObject("expire_date"));
            this.type = (String) DomainHelper.fromObject(rs.getObject("type"));
            this.departmentId = (Long) DomainHelper.fromObject(rs.getObject("department_id"));
            this.regionId = (Long) DomainHelper.fromObject(rs.getObject("region_id"));
            logger.debug( "-" );
            return this.id;
        }
        catch(SQLException e){
            throw new CriticalException(e);
        }
    }

    public PreparedStatement saveIntoStatement(PreparedStatement stmt) throws SQLException {
        logger.debug( "+" );
        try{
            DomainHelper.toStatement(id, stmt, 1);
            DomainHelper.toStatement(title, stmt, 2);
            DomainHelper.toStatement(position, stmt, 3);
            DomainHelper.toStatement(articleId, stmt, 4);
            DomainHelper.toStatement(salary, stmt, 5);
            DomainHelper.toStatement(needed, stmt, 6);
            DomainHelper.toStatement(publishDate, stmt, 7);
            DomainHelper.toStatement(expireDate, stmt, 8);
            DomainHelper.toStatement(type, stmt, 9);
            DomainHelper.toStatement(departmentId, stmt, 10);
            DomainHelper.toStatement(regionId, stmt, 11);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            throw new SQLException("Error while saving into statement");
        }
        logger.debug( "-" );
        return stmt;
    }

    public static Vacancy findById(Connection connection, Long id)
        throws CriticalException
    {
        try
        {
            PreparedStatement statement = connection.prepareStatement(findByIdSql);
            statement.setLong(1, id.longValue());
            ResultSet result = statement.executeQuery();
            if (result.next())
            {
                Vacancy vacancy = new Vacancy();
                vacancy.load(result);
                result.close();
                statement.close();
                return vacancy;
            }
            result.close();
            statement.close();
            return null;
        }
        catch(SQLException e)
        {
            throw new CriticalException(e);
        }
    }

    public String getTableId() {
        return tableId;
    }

    public String getFindByIdSql() {
        return findByIdSql;
    }

    public String getUpdateSql() {
        return updateSql;
    }

    public String getInsertSql() {
        return insertSql;
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public Vacancy clone(Connection conn) throws CriticalException
    {
        Article art = null;
        try{
            art = Article.findById( conn, this.getId() );
        }
        catch(ObjectNotFoundException e){
            logger.error("No article in general vacancy");
        }
        
        Article newArt = null;
        Vacancy newVac = new Vacancy();
        if (art != null) {
            newArt = new Article();
            newArt.setClass_(art.getClass_());
            newArt.setContainerId(art.getContainerId());
            newArt.setHead(art.getHead());
            newArt.setLangId(art.getLangId());
            newArt.setText(art.getText());
            newArt.insert(conn);
        }
        else{
            newArt = newVac.createArticle(conn);
        }

        newVac.setArticleId(newArt.getId());
        newVac.setType(this.getType());
        newVac.setDepartmentId(this.getDepartmentId());
        newVac.setTitle(this.getTitle());
        newVac.setPosition(this.getPosition());
        newVac.setSalary(this.getSalary());
        newVac.setNeeded(this.getNeeded());
        newVac.setPublishDate(this.getPublishDate());
        newVac.setExpireDate(this.getExpireDate());
        newVac.setRegionId(this.getRegionId());
        newVac.insert(conn);

        logger.info("add field by clone1");
        try{
            PreparedStatement stat = conn.prepareStatement(getVacancyExtraFieldsSql);
            stat.setLong(1, this.getId());
            ResultSet res = stat.executeQuery();
            ExtraField field = null;
            logger.info("add field by clone2:" + this.getId());
            while( res.next() ){
                logger.info("add field by clone3");
                field = new ExtraField();
                field.setRequired( res.getBoolean("is_required") );
                field.setTypeId( new Long(res.getLong("type_id")) );
                field.setOrderNumber( new Long(res.getLong("order_number")) );
                field.setSysName( res.getString("sys_name") );
                field.setVacancyId( newVac.getId() );
                field.insert( conn );
                logger.info("add field by clone");
            }
        }
        catch(SQLException e){
            logger.error("-errror", e);
            throw new CriticalException(e);
        }
        return newVac;
    }

    public static String getSelectByDepartmentIdSql(){
        return selectByDepartmentIdSql;
    }
    /**
     * @return Returns the departmentId.
     */
    public Long getDepartmentId() {
        return departmentId;
    }
    /**
     * @param departmentId The departmentId to set.
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * @param con
     * @return
     * @throws CriticalException 
     */
    public Article createArticle(Connection con) 
        throws CriticalException 
    {
        Article article = new Article();
        article.setText( NEW_ARTICLE_TEXT );
        article.insert( con );
        this.setArticleId( article.getId() );
        return article;
    }
    
    public List<ExtraField> getFields(Connection con) throws CriticalException{
        List<ExtraField> list = new ArrayList<ExtraField>();
        PreparedStatement stmt = null;
        try{
            stmt = con.prepareStatement(getGeneralVacancyExtraFieldsSql);
            stmt.setLong(1, JobModule.get().getFieldTepmlateId(con));
            stmt.setLong(2, this.getId());
            ResultSet rs = stmt.executeQuery();
            ExtraField ef = null;
            while (rs.next()){
                ef = new ExtraField();
                ef.load(rs);
                list.add(ef);
            }
            rs.close();
            stmt.close();
        }
        catch(SQLException e){
            logger.error("-error", e);
        }
        return list;
    }

	public Long getRegionId() {
		return regionId;
	}

	public void setRegionId(Long regionId) {
		this.regionId = regionId;
	}
}
