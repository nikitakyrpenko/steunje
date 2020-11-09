package com.negeso.module.inquiry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.negeso.framework.domain.DBHelper;
import com.negeso.framework.util.StringUtil;
import com.negeso.module.inquiry.EmailNotUniqueException;
import com.negeso.module.inquiry.EmailNotValidException;
import com.negeso.module.inquiry.domain.User;


/**
 * Thread safe.
 * 
 * @author sdemchenko
 */
public class UserDao {
    
    private static Logger logger = Logger.getLogger(UserDao.class);
    
    private static final Pattern VALID_EMAIL_PATTERN =
        Pattern.compile(".+@.+\\.[a-z\\-]+");

    public User loadUser(int id) {
        logger.debug("+");
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("SELECT * FROM inq_user WHERE id=" + id);
            if(!rs.next()) {
                logger.warn("- cannot find row # " + id);
                return null;
            }
            logger.debug("-");
            return mapRow(rs);
        } catch (SQLException e) {
            logger.error("- throwing " + e.getMessage());
            throw new RuntimeException("cannot read a row", e);
        } finally {
            DBHelper.close(rs, st, conn);
        }
    }

    public User loadUser(String login, String password, int quizId) {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBHelper.getConnection();
            st = conn.prepareStatement(
                " SELECT * FROM inq_user " +
                " WHERE email = ? AND password = ? AND questionnaire_id = ? ");
            st.setString(1, login);
            st.setString(2, password);
            st.setInt(3, quizId);
            ResultSet rs = st.executeQuery();
            if(!rs.next()) {
                logger.warn("- user not found: " + login + " / " + password);
                return null;
            }
            logger.debug("-");
            return mapRow(rs);
        } catch (SQLException e) {
            logger.error("- throwing " + e.getMessage());
            throw new RuntimeException("cannot read a row", e);
        } finally {
            DBHelper.close(st);
            DBHelper.close(conn);
        }
    }

    public void deleteUser(int id) {
        logger.debug("+");
        Connection conn = null;
        Statement st = null;
        try {
            conn = DBHelper.getConnection();
            st = conn.createStatement();
            st.executeUpdate("DELETE FROM inq_user WHERE id = " + id);
            logger.debug("-");
        } catch (SQLException e) {
            throw new RuntimeException("cannot delete a row", e);
        } finally {
            DBHelper.close(st);
            DBHelper.close(conn);
        }
    }

    public void insertUser(User user) {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBHelper.getConnection();
            if (!VALID_EMAIL_PATTERN.matcher(user.getEmail()).matches())
                throw new EmailNotValidException(user.getEmail());
            if(emailExists(user.getEmail(), user.getQuestionnaireId()))
                throw new EmailNotUniqueException(user.getEmail());
            st = conn.prepareStatement(
                " INSERT INTO inq_user (id,questionnaire_id,email,password) " +
                " VALUES (?,?,?,?) ");
            Long id = DBHelper.getNextInsertId(conn, "inq_user_id_seq");
            st.setInt(1, id.intValue());
            int quizId = user.getQuestionnaireId();
            st.setInt(2, quizId);
            st.setString(3, user.getEmail());
            if(user.getPassword() == null) {
                user.setPassword(StringUtil.generatePassword(user.getEmail()));
            }
            st.setString(4, user.getPassword());
            st.execute();
            user.setId(id.intValue());
            logger.debug("-");
        } catch (SQLException e) {
            throw new RuntimeException("cannot store a row", e);
        } finally {
            DBHelper.close(st);
            DBHelper.close(conn);
        }
    }

    public void updateUser(User user) {
        logger.debug("+");
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBHelper.getConnection();
            st = conn.prepareStatement(
                " UPDATE inq_user SET password=? WHERE id=? ");
            if(user.getPassword() == null) {
                user.setPassword(StringUtil.generatePassword(user.getEmail()));
            }
            st.setString(1, user.getPassword());
            st.setInt(2, user.getId());
            st.execute();
            logger.debug("-");
        } catch (SQLException e) {
            throw new RuntimeException("cannot store a row", e);
        } finally {
            DBHelper.close(st);
            DBHelper.close(conn);
        }  
    }

    public List<User> getUsersByQuestionnaireId(int id) {
        logger.debug("+");
        List<User> result = new LinkedList<User>();
        Connection conn = null;
        Statement st = null;
        try {
            conn = DBHelper.getConnection();
            st = conn.createStatement();
            ResultSet rs = st.executeQuery(
                " SELECT * FROM inq_user WHERE questionnaire_id = " + id +
                " ORDER BY email ");
            while(rs.next()) result.add(mapRow(rs));
            logger.debug("-");
            return result;
        } catch (SQLException e) {
            logger.error("- throwing " + e.getMessage());
            throw new RuntimeException("cannot read rows", e);
        } finally {
            DBHelper.close(st);
            DBHelper.close(conn);
        }
    }

    private User mapRow(ResultSet rs) {
        logger.debug("+");
        try {
            User u = new User();
            u.setId(rs.getInt("id"));
            u.setQuestionnaireId(rs.getInt("questionnaire_id"));
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
            logger.debug("-");
            return u;
        } catch (SQLException e) {
            logger.error("- Exception", e);
            throw new RuntimeException("Cannot read a row", e);
        }
    }
    
    private static boolean emailExists(String email, int quizId) {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = DBHelper.getConnection();
            st = conn.prepareStatement(
                " SELECT count(*) FROM inq_user" +
                " WHERE email = ? AND questionnaire_id = " + quizId);
            st.setString(1, email);
            ResultSet rs = st.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Exception", e);
        } finally {
            DBHelper.close(st);
            DBHelper.close(conn);
        }
    }

    public List<User> getLazyUsers(int questionnaireId) {
        logger.debug("+");
        List<User> result = new LinkedList<User>();
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = DBHelper.getConnection();
            st = conn.createStatement();
            String laboriousUsers =
                " SELECT DISTINCT user_id FROM inq_respondent " +
                " WHERE questionnaire_id = " + questionnaireId;
            String lazyUsers =
                " SELECT * FROM inq_user " +
                " WHERE id NOT IN (" + laboriousUsers + ") " +
                " AND questionnaire_id = " + questionnaireId +
                " ORDER BY email ";
            rs = st.executeQuery(lazyUsers);
            while(rs.next()) result.add(mapRow(rs));
            logger.debug("-");
            return result;
        } catch (SQLException e) {
            logger.error("- throwing " + e.getMessage());
            throw new RuntimeException("cannot read rows", e);
        } finally {
            DBHelper.close(rs, st, conn);
        }
    }

}
