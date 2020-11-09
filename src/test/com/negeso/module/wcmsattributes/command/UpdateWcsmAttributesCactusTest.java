/*
 * @(#)Id: UpdateWcsmAttributesCactusTest.java, 30.07.2007 17:16:24, Dmitry Fedotov
 *
 * Copyright (c) 2007 Negeso Ukraine
 *
 * This software is the confidential and proprietary information of Negeso
 * ("Confidential Information").  You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Negeso.
 */
package com.negeso.module.wcmsattributes.command;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;

import com.negeso.CommandTestCase;
import com.negeso.TestRequestContext;
import com.negeso.framework.command.AbstractCommand;
import com.negeso.framework.controller.RequestContext;
import com.negeso.framework.controller.ResponseContext;
import com.negeso.framework.domain.CriticalException;
import com.negeso.framework.domain.DBHelper;
import com.negeso.module.wcmsattributes.domain.Image;

/**
 * 
 * @TODO
 * 
 * @version	Revision: 
 * @author	Dmitry Fedotov
 *
 */
public class UpdateWcsmAttributesCactusTest extends CommandTestCase{
	
	private static final String IMG_ID = "9999999";
	private static final String IMG_SET_ID = "10000";
	
	private static final String IMG_SRC = "/src/old";
	private static final String IMG_SRC_NEW = "/src/new";
	
	private static final long IMG_MAX_WIDTH = 0;
	private static final long IMG_MAX_WIDTH_NEW = 1;

	private static final long IMG_MAX_HEIGHT = 0;
	private static final long IMG_MAX_HEIGHT_NEW = 1;
	
	private UpdateWcsmAttributes command;
	
	public UpdateWcsmAttributesCactusTest(){
		super();
	}
	
	@Override
	public void setUp() throws Exception{
		super.setUp();
		command = new UpdateWcsmAttributes();
		command.setRequestContext(requestContext);
	}
	
	/*
	 * we set ACTION parameter, but will throw new Critical Exception
	 */
	public void beginCriticalExceptionFailure(WebRequest request){
		request.addParameter("action_field", "up");
		request.addParameter("attribute_class_field", "some_value");
		request.addParameter("image_id_field", "some_image_id");
	}
	
	public void testCriticalExceptionFailure(){
		command = new UpdateWcsmAttributes(){
			@Override
			public void reorderImage(String imgId, int direction) {
				throw new CriticalException();
		    }
		};
		command.setRequestContext(requestContext);
		ResponseContext responseContext = command.execute();
		assertEquals(AbstractCommand.RESULT_FAILURE, responseContext.getResultName());
	}
	
	
	/*
	 * we set wrong ACTION parameter, wait for failure response
	 */
	public void beginNoActionFailure(WebRequest request){
		request.addParameter("action_field", "no parameter");
	}
	
	public void testNoActionFailure(){
		ResponseContext responseContext = command.execute();
		
		assertEquals(AbstractCommand.RESULT_FAILURE, responseContext.getResultName());
	}
	
	
	
	/*
	 * we set action='change' parameter, but without src ot imgId parameters -> wait for failure response
	 */
	public void beginJustActionChangeParameterFailure(WebRequest request){
		request.addParameter("action_field", "change");
	}
	
	public void testJustActionChangeParameterFailure(){
		ResponseContext responseContext = command.execute();
		
		assertEquals(AbstractCommand.RESULT_FAILURE, responseContext.getResultName());
	}
	
	
	/*
	 * we set action='changeFlash' parameter, but without src ot imgId parameters -> wait for failure response
	 */
	public void beginJustChangeFlashChangeParameterFailure(WebRequest request){
		request.addParameter("action_field", "changeFlash");
	}
	
	public void testJustChangeFlashChangeParameterFailure(){
		ResponseContext responseContext = command.execute();
		
		assertEquals(AbstractCommand.RESULT_FAILURE, responseContext.getResultName());
	}
	
	
	/*
	 * we set action='delete' parameter, but without src ot imgId parameters -> wait for failure response
	 */
	public void beginJustDeleteChangeParameterFailure(WebRequest request){
		request.addParameter("action_field", "delete");
	}
	
	public void testJustDeleteChangeParameterFailure(){
		ResponseContext responseContext = command.execute();
		
		assertEquals(AbstractCommand.RESULT_FAILURE, responseContext.getResultName());
	}
	
	
	
	/*
	 * we set action='add' parameter, but without src ot imgId parameters -> wait for failure response
	 */
	public void beginJustAddChangeParameterFailure(WebRequest request){
		request.addParameter("action_field", "add");
	}
	
	public void testJustAddChangeParameterFailure(){		
		ResponseContext responseContext = command.execute();
		
		assertEquals(AbstractCommand.RESULT_FAILURE, responseContext.getResultName());
	}
	
	
	
	/*
	 * we set action='addFlash' parameter, but without src ot imgId parameters -> wait for failure response
	 */
	public void beginJustAddFlashChangeParameterFailure(WebRequest request){
		request.addParameter("action_field", "addFlash");
	}
	
	public void testJustAddFlashChangeParameterFailure(){
		ResponseContext responseContext = command.execute();
		
		assertEquals(AbstractCommand.RESULT_FAILURE, responseContext.getResultName());
	}
	
	
	
	/*
	 * we set action='up' parameter, but without src ot imgId parameters -> wait for failure response
	 */
	public void beginJustUpDownFlashChangeParameterFailure(WebRequest request){
		request.addParameter("action_field", "up");
	}
	
	public void testJustUpDownFlashChangeParameterFailure(){
		ResponseContext responseContext = command.execute();
		
		assertEquals(AbstractCommand.RESULT_FAILURE, responseContext.getResultName());
	}
	
	
	
	/*
	 * we set action='change' parameter, but with wrong imgId parameter -> waiting for result failure
	 */
	public void beginChangeActionWrongImgId(WebRequest request){
		request.addParameter("action_field", "change"); 
		request.addParameter("src_field", "/src/"); 
		request.addParameter("image_id_field", "not number :)");
	}
	
	public void testChangeActionWrongImgId(){
		ResponseContext responseContext = command.execute();
		
		assertEquals(AbstractCommand.RESULT_FAILURE, responseContext.getResultName());
	}
	
	
	
	/*
	 * we set action='change' parameter, create, change and delete new record in DB
	 */
	public void beginChangeImgSrc(WebRequest request){
		request.addParameter("action_field", "change"); 
		request.addParameter("src_field", IMG_SRC_NEW); 
		request.addParameter("image_id_field", IMG_ID);
	}
	
	public void testChangeImgSrc() throws Exception{
		//create new image in DBTable 'image' with IMG_ID(then we will update this image)
		createDBImage();
		
		command.setRequestContext(requestContext);
		ResponseContext responseContext = command.execute();
		validateDBChange();
		
		//delete testimage in DB
		deleteTestDBImage();
	}
	
	private void createDBImage() throws Exception{
		Connection conn = DBHelper.getConnection();
		String query = "insert into image(" +
				"image_set_id, " +
				"image_id, " +
				"src, " +
				"max_width, " +
				"max_height) " +
				"values ( ?, ?, ?, ?, ?)";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setLong(1, 1L);
		ps.setLong(2, Long.valueOf(IMG_ID));
		ps.setString(3, IMG_SRC);
		ps.setLong(4, IMG_MAX_WIDTH);
		ps.setLong(5, IMG_MAX_HEIGHT);
		
		ps.execute();
		ps.close();
		conn.close();
	}
	
	private void validateDBChange() throws Exception{
		Connection conn = DBHelper.getConnection();
		String query = "select image.src from image where image.image_id = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setLong(1, Long.valueOf(IMG_ID));
		ResultSet rs = ps.executeQuery();
		String res = "";
		while(rs.next()){
			res = rs.getString("src");
		}

		assertEquals(IMG_SRC_NEW, res);
		
		rs.close();
		ps.close();
		conn.close();
	}
	
	private void deleteTestDBImage() throws Exception{
		Connection conn = DBHelper.getConnection();
		String query = "delete from image where image.image_id = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setLong(1, Long.valueOf(IMG_ID));
		ps.execute();
		
		ps.close();
		conn.close();
	}
	
	
	
	/*
	 * we set action='changeFlash' parameter, create, change and delete new record in DB
	 */
	public void beginChangeFlash(WebRequest request){
		request.addParameter("action_field", "changeFlash"); 
		request.addParameter("src_field", IMG_SRC_NEW); 
		request.addParameter("image_id_field", IMG_ID);
		request.addParameter("flash_width", String.valueOf(IMG_MAX_WIDTH_NEW));
		request.addParameter("flash_height", String.valueOf(IMG_MAX_HEIGHT_NEW));
	}
	
	public void testChangeFlash() throws Exception{
		//create new image in DBTable 'image' with IMG_ID
		createDBImage();
		
		command.setRequestContext(requestContext);
		ResponseContext responseContext = command.execute();
		validateDBChangeFlash();
		
		//delete testimage in DB
		deleteTestDBImage();
	}
	
	private void validateDBChangeFlash() throws Exception{
		Connection conn = DBHelper.getConnection();
		String query = "select image.src, image.max_width, image.max_height from image where image.image_id = ?";
		PreparedStatement ps = conn.prepareStatement(query);
		ps.setLong(1, Long.valueOf(IMG_ID));
		ResultSet rs = ps.executeQuery();
		String res = "";
		long wid = 0;
		long hei = 0;
		while(rs.next()){
			res = rs.getString("src");
			wid = rs.getLong("max_width");
			hei = rs.getLong("max_height");
		}

		assertEquals(IMG_SRC_NEW, res);
		assertEquals(IMG_MAX_WIDTH_NEW, wid);
		assertEquals(IMG_MAX_HEIGHT_NEW, hei);
		
		rs.close();
		ps.close();
		conn.close();
	}
	
	
	
	/*
	 * we set action='delete' parameter, create, excute command and check deleted record in DB
	 */
	public void beginDelete(WebRequest request){
		request.addParameter("action_field", "delete"); 
		request.addParameter("src_field", IMG_SRC_NEW); 
		request.addParameter("image_id_field", IMG_ID);
		request.addParameter("flash_width", String.valueOf(IMG_MAX_WIDTH_NEW));
		request.addParameter("flash_height", String.valueOf(IMG_MAX_HEIGHT_NEW));
	}
	
	public void testDelete() throws Exception{
		//create new image in DBTable 'image'
		createDBImage();
		
		command.setRequestContext(requestContext);
		ResponseContext responseContext = command.execute();
		validateDeletedDBImage();
	}
	
	private void validateDeletedDBImage() throws Exception{
		Connection conn = DBHelper.getConnection();
		String query = "select image.src from image where image.image_id = ?";
		PreparedStatement ps = conn.prepareStatement(
				query, 
				ResultSet.TYPE_SCROLL_INSENSITIVE, 
				ResultSet.CONCUR_UPDATABLE);
		ps.setLong(1, Long.valueOf(IMG_ID));
		ResultSet rs = ps.executeQuery();
		
		assertNotNull(rs.first());
		
		rs.close();
		ps.close();
		conn.close();
	}
	
	
	
	/*
	 * we set action='add' parameter, command create new record in DB, we check its existence and then delete test record
	 */
//	public void beginAdd(WebRequest request){
//		request.addParameter("action_field", "add"); 
//		request.addParameter("src_field", IMG_SRC); 
//		request.addParameter("image_set_id_field", "213");
//		request.addParameter("attribute_class_field", "max width");
//	}
//	
//	public void testAdd() throws Exception{
//		command.setRequestContext(requestContext);
//		ResponseContext responseContext = command.execute();
//		
//		validateCreatedDBImage();
//		
//		deleteRecordBySrc();
//		
//	}
//	
//	private void validateCreatedDBImage() throws Exception{
//		Connection conn = DBHelper.getConnection();
//		String query = "select image.src from image where image.src = ?";
//		PreparedStatement ps = conn.prepareStatement(query);
//		ps.setString (1, IMG_SRC);
//		ResultSet rs = ps.executeQuery();
//		String res = "";
//		while(rs.next()){
//			res = rs.getString("src");
//		}
//
//		assertEquals(IMG_SRC, res);
//		
//		rs.close();
//		ps.close();
//		conn.close();
//	}
//	
//	private void deleteRecordBySrc() throws Exception{
//		Connection conn = DBHelper.getConnection();
//		String query = "delete from image where image.src = ?";
//		PreparedStatement ps = conn.prepareStatement(query);
//		ps.setString (1, IMG_SRC);
//		ps.execute();
//		
//		ps.close();
//		conn.close();
//	}
}
