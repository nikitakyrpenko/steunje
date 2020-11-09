/*
 * @(#)User.java       @version	30.12.2003
 *
 * Copyright (c) 2003 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Negeso.
 */

package com.negeso.framework.domain;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import com.negeso.framework.io.xls.XlsColumnNumber;
import com.negeso.framework.util.Encryption;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;

import com.negeso.framework.Env;

/**
 * User domain object.
 *
 * @version 	30.12.2003
 * @author 	 	Olexiy Strashko
 */
public class User implements Serializable {
	
	public static final String WRONG = "WRONG";
	public static final String EXPIRED = "EXPIRED";
	public static final String NOT_ACTIVATED = "NOT_ACTIVATED";
	public static final String AUTHORIZED = "AUTHORIZED";
	public static final String NOT_APPROVED = "NOT_APPROVED";
	public static final String SYSTEM_USER = "SYSTEM_USER";
	public static final String ALREADY_LOGGED_IN = "ALREADY_LOGGED_IN";
	
	private static final long serialVersionUID = 1L;

    private static String tableId = "user_list";

    private static String sequenceId = "user_list_id_seq";

    public static final String ADMINISTRATOR_TYPE = "administrator";

    private final static String SQL_FIND_BY_ID =
        "SELECT * FROM user_list WHERE id = ?";

    private final static String SQL_FIND_ADMIN =
    	"SELECT * FROM user_list WHERE type = 'administrator' order by id ";
    
    private final static String SQL_SELECT_ALL_VISITORS =
        "SELECT * FROM user_list WHERE type='visitor' AND site_id = ? ORDER BY username";
        
    private final static String SQL_SELECT_CONTRIBUTORS =
        "SELECT * FROM user_list WHERE NOT(type='visitor') AND site_id = ? ORDER BY username";
    
    private final static String SQL_FIND_BY_LOGIN =
        "SELECT * FROM user_list WHERE login = ? AND site_id = ? ";
    
    private final static String SQL_FIND_BY_TYPE =
        "SELECT * FROM user_list WHERE type = ? AND site_id = ? ";


    private final static String SQL_UPDATE =
        " UPDATE user_list SET" +
        " id = ?," +
        " username = ?," +
        " login = ?," +
        " password = ?," +
        " type = ?," +
        " extra = ?," +
        " site_id = ?," +
        " publish_date = ?," +
        " expired_date = ?," +
        " last_action_date = ?," +
        " single_user = ?," +
        " guid = ?, " +
        " token = ?, " +
        " verification = ? " +
        " WHERE id = ? ";

    public final static String SQL_INSERT =
        "INSERT INTO user_list (" +
        " id," +
        " username," +
        " login," +
        " password," +
        " type," +
        " extra," +
        " site_id," +
        " publish_date," +
        " expired_date," +
        " last_action_date," +
        " single_user," +
        " guid, " +
        " token, " +
        " verification " +
        
        " ) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        
    private static Logger logger = Logger.getLogger( User.class );
    
    
    private Long id = null;
    

    /* user name */
    @XlsColumnNumber(4)
    private String name = null;

    /* user login */
    @XlsColumnNumber
    private String login = null;

    /* user password */
    @XlsColumnNumber(value = 11, encription = Encryption.MD5)
    private transient String password = null;

    /* user type */
    private String type = null;

    /* extra field in user */
    private String extraField = null;

    private Long siteId = null;

    private String cityTitle = null;

    private Date publishDate;
    private Date expiredDate;
    private Date lastActionDate;

    private boolean singleUser = false;
    
    private String guid;
    
    private boolean superuser = false;
    
    private String superuserLogin = null;

    private String token;

    private boolean verification;
    
    public User() {
    	this.siteId = 1L;
    	this.setType("visitor");
    }


    protected User( Long id, String userName, String login, String password,
                    String type, Date publishDate, Date expiredDate,
                    Date lastActionDate, boolean singleUser, String guid, String token,
                    boolean verification)
    {
        this.id = id;
        this.name = userName;
        this.login = login;
        this.password = password;
        this.type = type;
        this.publishDate = publishDate;
        this.expiredDate = expiredDate;
        this.lastActionDate = lastActionDate;
        this.singleUser = singleUser;
        this.guid = guid;
        this.token = token;
        this.verification = verification;
    }


    /**
     * Finds User by login.
     * If object is not found - throws ObjectNotFoundException
     *
     * @param login user login
     * @return User if object is found, never null
     * @throws SQLException
     */
    public static User findByLogin( String login )
        throws CriticalException, ObjectNotFoundException
    {
        logger.debug( "+" );
        PreparedStatement findStatement = null;
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            findStatement = conn.prepareStatement( SQL_FIND_BY_LOGIN );
            findStatement.setString( 1, login );
            findStatement.setLong(2, Env.getSiteId());
            ResultSet rs = findStatement.executeQuery();
            if ( rs.next() == false ) {
                throw new ObjectNotFoundException(
                    "User not found by login: " + login );
            }
            User result = load( rs );
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.debug( "-", ex );
            throw new CriticalException( ex );
        }
        finally {
            DBHelper.close( conn );
        }
    }


    /**
     * Finds User by id. If object is not found - throws ObjectNotFoundException
     *
     * @param id
     * @return User if object is found, null - if object not found
     * @throws SQLException
     */
    public static User findById( Long id )
        throws CriticalException, ObjectNotFoundException
    {
        logger.debug( "+" );
        User result = null;
        PreparedStatement findStatement = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            findStatement = conn.prepareStatement( SQL_FIND_BY_ID );
            findStatement.setLong( 1, id.longValue() );
            rs = findStatement.executeQuery();
            if ( rs.next() == false ) {
                // Object not found throw exception
                throw new ObjectNotFoundException( "Object: " + User.tableId +
                    " not found by id: " + id );
            }
            result = load( rs );
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.error( "-", ex );
            throw new CriticalException( ex );
        }
        finally {
            DBHelper.close( conn );
        }
    }
    
   
    /**
     * Finds the first available admin user
     * @return
     * @throws CriticalException
     * @throws ObjectNotFoundException
     */
    public static User findFirstAdmin()
    throws CriticalException, ObjectNotFoundException
    {
    	logger.debug( "+" );
    	User result = null;
    	PreparedStatement findStatement = null;
    	ResultSet rs = null;
    	Connection conn = null;
    	try {
    		conn = DBHelper.getConnection();
    		findStatement = conn.prepareStatement( SQL_FIND_ADMIN );
    		rs = findStatement.executeQuery();
    		if ( rs.next() == false ) {
    			// Object not found throw exception
    			throw new ObjectNotFoundException( "Admin from " + User.tableId +
    					" not found ");
    		}
    		result = load( rs );
    		logger.debug( "-" );
    		return result;
    	} catch ( SQLException ex ) {
    		logger.error( "-", ex );
    		throw new CriticalException( ex );
    	}
    	finally {
    		DBHelper.close( conn );
    	}
    }
    
    
    
    /**
     * Find user by id. If user not found - return null.
     * Included connection for transaction support.  
     * 
     * @param con
     * @param id
     * @return
     * @throws CriticalException
     */
    public static User findById( Connection con, Long id )
	    throws CriticalException
	{
	    logger.debug( "+" );
	    User result = null;
	    PreparedStatement stmt = null;
	    try {
	        stmt = con.prepareStatement( SQL_FIND_BY_ID );
	        stmt.setLong( 1, id.longValue() );
	        ResultSet rs = stmt.executeQuery();
	        if ( rs.next() == false ) {
	            // Object not found - return null
	        	return null;
	        }
	        result = load( rs );
	        rs.close();
		    stmt.close();
	    } 
	    catch ( SQLException ex ) {
	        logger.error( "-", ex );
	        throw new CriticalException( ex );
	    }
	    logger.debug( "-" );
        return result;
	}
    
    /**
     * Returns a list af all contributors (not visitors), who are
     * registered in the W/CMS.
     * 
     * @return list of users (maybe empty); never null
     * 
     * @throws CriticalException
     */
    public static Collection<User> getContributors() throws CriticalException
    {
        logger.debug("+");
        Collection<User> users = new LinkedList<User>();
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            PreparedStatement ps =
                conn.prepareStatement(SQL_SELECT_CONTRIBUTORS);
            ps.setLong(1, Env.getSiteId());
            ResultSet rs =ps.executeQuery();
            while(rs.next()) users.add(load(rs));
            rs.close();
            logger.debug( "-" );
            return users;
        } catch (SQLException ex) {
            logger.error( "-", ex );
            throw new CriticalException(ex);
        } finally {
            DBHelper.close( conn );
        }
    }
    
    
    /**
     * Returns a list af all users, both visitors (not contributors) who are
     * registered in the W/CMS.
     * 
     * @return list of visitors (maybe empty); never null
     * 
     * @throws CriticalException
     */
    public static Collection<User> getVisitors() throws CriticalException
    {
        logger.debug("+");
        Collection<User> users = new LinkedList<User>();
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            PreparedStatement ps =
                conn.prepareStatement(SQL_SELECT_ALL_VISITORS);
            ps.setLong(1, Env.getSiteId());
            ResultSet rs = ps.executeQuery();
            while(rs.next()) users.add(load(rs));
            rs.close();
            logger.debug( "-" );
            return users;
        } catch (SQLException ex) {
            logger.error( "-", ex );
            throw new CriticalException(ex);
        } finally {
            DBHelper.close( conn );
        }
    }
    
    
    public static Collection<String[]> getMembership(Long uid, boolean forVisitor)
        throws CriticalException
    {
        logger.debug("+");
        LinkedList<String[]> groups = new LinkedList<String[]>();
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            ResultSet rs;
            if(forVisitor){
               rs = conn.createStatement().executeQuery(
                        " SELECT groups.*, " +
                        "   (SELECT COUNT(*) > 0 FROM user_list, members " +
                        "       WHERE members.user_id = user_list.id " +
                        "       AND members.group_id = groups.id " +
                        "       AND user_list.id = " + uid +
                        "   ) AS member" +
                        " FROM groups, roles " +
                        " WHERE groups.role_id = roles.id " +
                        " AND roles.is_visitor = true " +
                        " AND groups.site_id = " + Env.getSiteId() +
                        " ORDER by groups.name "
                        );
            }
            else{
                rs = conn.createStatement().executeQuery(
                        " SELECT groups.*, " +
                        "   (SELECT COUNT(*) > 0 FROM user_list, members " +
                        "       WHERE members.user_id = user_list.id " +
                        "       AND members.group_id = groups.id " +
                        "       AND user_list.id = " + uid +
                        "   ) AS member" +
                        " FROM groups  "+
                        " WHERE groups.site_id = " + Env.getSiteId() +
                        " ORDER by groups.name "
                        );
            }
            
            while(rs.next()){
                try {
                    groups.add( new String[]{
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getBoolean("member") == true ? "true" : "false",
                        rs.getString("role_id"),
                        });
                } catch (Exception ex) {
                    logger.error( "CriticalEx private final UserDaoception", ex );
                }
            }
            rs.close();
            logger.debug( "-" );
            return groups;
        } catch (SQLException ex) {
            logger.error( "-", ex );
            throw new CriticalException(ex);
        } finally {
            DBHelper.close( conn );
        }
    }
    
    
    public void updateMembership(Long[] groupIds)
        throws CriticalException
    {
        logger.debug("+");
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            // clean membership
            PreparedStatement pstmt =
                conn.prepareStatement("DELETE FROM members WHERE user_id = ? ");
            pstmt.setObject(1, id);
            pstmt.executeUpdate();
            // establish membership
            pstmt = conn.prepareStatement(
                "INSERT INTO members (user_id, group_id) VALUES (?, ?)");
            for(int i = 0; i < groupIds.length; i++) {
                if(groupIds[i] != null){
                    pstmt.clearParameters();
                    pstmt.setObject(1, id);
                    pstmt.setObject(2, groupIds[i]);
                    pstmt.executeUpdate();
                }
            }        
            logger.debug( "-" );
            return;
        } catch (SQLException ex) {
            logger.error( "- SQLException", ex );
            throw new CriticalException(ex);
        } finally {
            DBHelper.close( conn );
        }
    }


    /**
     * Loads User by RecordSet. All blob fields from table are
     * passed throught Env.fixEncoding() method.
     *
     * @see Env
     * @param rs
     * @return
     * @throws SQLException
     */
    public static User load( ResultSet rs )
        throws CriticalException
    {
        logger.debug( "+" );
        try {
            User result = new User(
                new Long( rs.getLong( "id" ) ),
                rs.getString( "username" ),
                rs.getString( "login" ),
                rs.getString( "password" ),
                rs.getString( "type" ),
                rs.getTimestamp("publish_date") == null 
                	? null : new Date( rs.getTimestamp("publish_date").getTime() ),
                rs.getTimestamp("expired_date") == null                			
                	? null : new Date( rs.getTimestamp("expired_date").getTime() ),
    			rs.getTimestamp("last_action_date") == null                			
    				? null : new Date( rs.getTimestamp("last_action_date").getTime() ),
    			rs.getBoolean( "single_user" ),
    			rs.getString( "guid" ),
                rs.getString("token"),
                rs.getBoolean("verification"));


            result.setExtraField( rs.getString( "extra" ) );
            result.setSiteId( rs.getLong( "site_id" ) );
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.debug( "-", ex );
            throw new CriticalException( ex );
        }
    }

    public PreparedStatement saveIntoStatement( PreparedStatement stmt )
        throws SQLException
    {
        logger.debug( "+" );
        stmt.setLong( 1, this.getId().longValue() );
        stmt.setString( 2, this.getName() );
        stmt.setString( 3, this.getLogin() );
        stmt.setString( 4, this.getPassword() );
        stmt.setString( 5, this.getType() );
        stmt.setString( 6, this.getExtraField() );
        stmt.setLong( 7, Env.getSiteId() );
        stmt.setTimestamp( 8, 
        		this.getPublishDate() == null ? null : new Timestamp (this.getPublishDate().getTime()) );
        stmt.setTimestamp( 9, 
        		this.getExpiredDate() == null ? null : new Timestamp (this.getExpiredDate().getTime()) );
        stmt.setTimestamp( 10, 
        		this.getLastActionDate() == null ? null : new Timestamp (this.getLastActionDate().getTime()) );
        stmt.setBoolean(11, this.isSingleUser());
        stmt.setString(12, this.getGuid());
        stmt.setObject(13, this.getToken());
        stmt.setObject(14, this.isVerification());
        logger.debug( "-" );
        return stmt;
    }        


    public String getTableId() { return User.tableId; }

    public Long insert() {
        logger.debug( "+" );
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            insert(conn);
            return this.getId();
        } catch ( SQLException ex ) {
            throw new CriticalException( ex );
        } finally {
            DBHelper.close( conn );
        }
    }

    /**
     * Insert User with transaction support
     * 
     * @param con
     * @return
     * @throws CriticalException
     */
    public Long insert(Connection con) {
	    logger.debug( "+" );
	    PreparedStatement stmt = null;
	    try {
	        this.setId( DBHelper.getNextInsertId( con, User.sequenceId ) );
	        stmt = con.prepareStatement( SQL_INSERT );
	        stmt = this.saveIntoStatement( stmt );
	        stmt.execute();
		    logger.debug( "-" );
	        return this.getId();
	    } catch ( SQLException ex ) {
	        logger.debug("- SQLException", ex);
	        throw new CriticalException(ex);
	    } finally {
	        DBHelper.close( stmt );
	    }
	}

    
    public void update() {
        logger.debug( "+" );
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            update(conn);
        } catch ( SQLException ex ) {
            logger.debug( "-", ex );
            throw new CriticalException( ex );
        } finally {
            DBHelper.close( conn );
        }
    }

    
    /**
     * Update User with transaction support 
     * 
     * @param con
     * @throws CriticalException
     */
    public void update(Connection con) {
	    logger.debug( "+" );
	    PreparedStatement stmt = null;
	    try {
	        stmt = con.prepareStatement( SQL_UPDATE );
	        stmt.setObject( 13, this.id );
	        this.saveIntoStatement( stmt );
	        stmt.execute();
	    } catch ( SQLException ex ) {
	        logger.debug( "-", ex );
	        throw new CriticalException( ex );
	    } finally{
	    	DBHelper.close(stmt);
	    }
	}


    public void delete() {
        logger.debug( "+" );
        DBHelper.deleteObject( this.getTableId(), this.getId(), logger );
        this.setId( null );
        logger.debug( "-" );
    }
    
    public void delete(Connection connection) {
    	logger.debug( "+" );
    	DBHelper.deleteObject(connection, this.getTableId(), this.getId(), logger );
    	this.setId( null );
    	logger.debug( "-" );
    }
    
    
    public Long getId()
    {
        return this.id;
    }
    
    
    public void setId(Long id)
    {
        this.id = id;
    }
    
    
    /**
     * @return Returns the login.
     */
    public String getLogin()
    {
        return login;
    }


    /**
     * @param login The login to set.
     */
    public void setLogin( String login )
    {
        this.login = login;
    }


    /**
     * @return Returns the password.
     */
    public String getPassword()
    {
        return password;
    }


    /**
     * @param password The password to set.
     */
    public void setPassword( String password )
    {
        this.password = password;
    }


    /**
     * @return Returns the type.
     */
    public String getType()
    {
        return type;
    }


    /**
     * @param type The type to set.
     */
    public void setType( String type )
    {
        this.type = type;
    }


    /**
     * @return Returns the Name.
     */
    public String getName()
    {
        return name;
    }


    /**
     * @param Name The Name to set.
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * @param extraField The extraField to set.
     */
    public void setExtraField(String extraField) {
        this.extraField = extraField;
    }
    
    
    public String getExtraField() {
        return this.extraField;
    }

    public Long getSiteId() {
        return siteId;
    }    
    
    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
        
	public String toString () {
		return ReflectionToStringBuilder.reflectionToString(this);
	}
    
    public boolean equals(Object other) {
        if (this == other) return true;
        if ( !(other instanceof User) ) return false;
        return login.equals( ((User) other).login );
    }

    public int hashCode() {
        return login.hashCode();
    }


	public String getCityTitle() {
		return cityTitle;
	}


	public void setCityTitle(String cityTitle) {
		this.cityTitle = cityTitle;
	}


	public Date getExpiredDate() {
		return expiredDate;
	}


	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}


	public Date getPublishDate() {
		return publishDate;
	}


	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public boolean isExpired() {
        Timestamp now = new Timestamp( System.currentTimeMillis() );                
        return !( ( this.getPublishDate() == null 
        		|| this.getPublishDate().before(now) || this.getPublishDate().equals(now) ) 
        		&& ( this.getExpiredDate() == null || this.getExpiredDate().after(now) ) );

		 
	}


	public Date getLastActionDate() {
		return lastActionDate;
	}


	public void setLastActionDate(Date lastActionDate) {
		this.lastActionDate = lastActionDate;
	}


	public boolean isSingleUser() {
		return singleUser;
	}


	public void setSingleUser(boolean singleUser) {
		this.singleUser = singleUser;
	}

    public void updateLastActionDate(){
//    	if (user.isSingleUser())                 	
        logger.debug("+");            
        this.setLastActionDate(new Date( System.currentTimeMillis()));                

        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBHelper.getConnection();        	
			stmt = con.prepareStatement("UPDATE user_list SET last_action_date = ? WHERE id = " + this.getId());	
			stmt.setTimestamp(1, new Timestamp( System.currentTimeMillis() ));
            stmt.executeUpdate();
            logger.debug("update: new date=" + new Timestamp( System.currentTimeMillis() ));
        } catch(SQLException e) {
            logger.error("-error", e);
            throw new CriticalException(e);
        } finally {
        	DBHelper.close(stmt);
            DBHelper.close(con);
        }        	
        logger.debug("-");
    }

	public String getGuid() {
		return guid;
	}


	public void setGuid(String guid) {
		this.guid = guid;
	}

    public void clearLastActionDate(){
//      if (user.isSingleUser())                  
      logger.debug("+");            
      this.setLastActionDate(null);                
      
      Connection con = null;
      PreparedStatement stmt = null;
      try {
        con = DBHelper.getConnection();         
        stmt = con.prepareStatement("UPDATE user_list SET last_action_date =null WHERE id = " + this.getId());  
        stmt.executeUpdate();
        logger.debug("update-clear lastActionDate: new date=" + null);
      } catch(SQLException e) {
        logger.error("-error", e);
        throw new CriticalException(e);
      } finally {
        DBHelper.close(stmt);
        DBHelper.close(con);
      }         
      logger.debug("-");
    }


	public boolean isSuperUser() {
		return superuser;
	}


	public void setSuperUser(boolean superuser) {
		this.superuser = superuser;
	}

	private static final String GET_STATUS_QUERY = "SELECT is_activated, user_id FROM lm_user_info WHERE login = ?";
	
    public static String getUserStatus(String login) {
    	logger.debug("+");
    	logger.debug("login=" + login);
    	String status = WRONG;
    	
    	if (login != null) {
	    	Connection conn = null;
	    	try {
				conn = DBHelper.getConnection();
				PreparedStatement stmt = conn.prepareStatement(GET_STATUS_QUERY);
				stmt.setString(1, login);
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					if (rs.getLong(2) == 0) {
						status = NOT_APPROVED;  
					} else {
						status = (rs.getBoolean(1) == true? AUTHORIZED : NOT_ACTIVATED);
					}	
					logger.debug("userStatus=" + status);
				} else {// Wrong User or System User
					status = WRONG; 
					if (findByLogin(login) != null) {
						status= SYSTEM_USER;
					}
				}
				
			} catch (SQLException e) {
				logger.info("Exception - probably, lm_user_info is absent");
				status = WRONG;
				
			} catch (ObjectNotFoundException e) {
				//should be unreachable code
				logger.error("-error", e);				
				status = WRONG;
				
			} finally {
				DBHelper.close(conn);
			}
    	}
    	logger.debug("status=" + status.toString());
    	logger.debug("-");
    	return status;
    }


	public static User findByType(Connection con, String type) {
        logger.debug( "+" );
        PreparedStatement findStatement = null;
        Connection conn = null;
        try {
            conn = DBHelper.getConnection();
            findStatement = conn.prepareStatement( SQL_FIND_BY_TYPE );
            findStatement.setString( 1, type );
            findStatement.setLong( 2, Env.getSiteId() );
            ResultSet rs = findStatement.executeQuery();
            if ( !rs.next() ) {
                // Object not found throw exception
            	return null;
            }
            User result = load( rs );
            logger.debug( "-" );
            return result;
        } catch ( SQLException ex ) {
            logger.debug( "-", ex );
            throw new CriticalException( ex );
        }
        finally {
            DBHelper.close( conn );
        }
	}


	public String getSuperuserLogin() {
		return superuserLogin;
	}


	public void setSuperuserLogin(String superuserLogin) {
		this.superuserLogin = superuserLogin;
	}
	
	public static boolean isAdmin(User user) {
		return user != null && ADMINISTRATOR_TYPE.equals(user.getType());
	}
	
	public static boolean isNotAdmin(User user) {
		return !isAdmin(user);
	}

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public boolean isVerification() {
        return verification;
    }

    public void setVerification(boolean verification) {
        this.verification = verification;
    }
}
