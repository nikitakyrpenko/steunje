package com.negeso.module.thr.web.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.itextpdf.text.pdf.PdfWriter;
import com.onbarcode.barcode.EAN13;
import com.onbarcode.barcode.IBarcode;

public class LabelsPdfGeteratingController extends AbstractController{

	
	private static final int BARCODE_IMAGE_WIDTH = 350;
	private static final int BARCODE_IMAGE_HEIGHT = 100;
	private static final int BARCODE_COLUMS = 3;
	private static final int BARCODE_ROWS_PER_PAGE = 14;
	private static Pattern p = Pattern.compile("^[0-9]{12,13}$");
	private static int scale = 2;
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String labelsStr = ServletRequestUtils.getStringParameter(request, "labels", StringUtils.EMPTY);
		List<String> labels = new ArrayList<String>();
		for (String label : labelsStr.split(",")) {
			if (p.matcher(label).find()) {
				labels.add(label);
			}
		}
		generatePDF(labels, response);
		return null;
	}
	private static final int BARCODE_PER_PAGE = BARCODE_COLUMS * BARCODE_ROWS_PER_PAGE;

	public void generatePDF(List<String> codes, HttpServletResponse response) throws Exception {
        Document document = new Document();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    PdfWriter.getInstance(document, baos);
	    
	    document.setPageSize(PageSize.A4);
	    document.setMargins(0, 0, 0, 0);
	    	
	    document.open();
	
	    PdfPTable table = new PdfPTable(BARCODE_COLUMS);
	    table.setWidthPercentage(100f);
	    table.setTableEvent(this.new DottedCells());
	    table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
	    
		int pages = codes.size() / BARCODE_PER_PAGE + (codes.size() % BARCODE_PER_PAGE > 0 ? 1 : 0 );
		if (pages == 0) {
			pages = 1;
		}
	    
	    for (int i = 0; i < pages * BARCODE_PER_PAGE; i++) {
	    	
	    	String code = i < codes.size() ? codes.get(i) : null;
	    	PdfPCell c = null;
	    	if ( code != null) {
	    		Image image2 = Image.getInstance(getEAN13Image(codes.get(i)));
	    		c = new PdfPCell(image2, true);
	    		c.setPadding(1);
	    	} else {
	    		c = new PdfPCell();
	    	}
	    	 
	    	c.setMinimumHeight(60.14f);
	    	c.setBorder(PdfPCell.NO_BORDER);
	    	table.addCell(c);
		}           
	
	    document.add(table);
	
	    document.close();
	    response.setHeader("Expires", "0");
        response.setHeader("Cache-Control",
            "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        // setting the content type
        response.setContentType("application/pdf");
        // the contentlength
        response.setContentLength(baos.size());
        // write ByteArrayOutputStream to the ServletOutputStream
        OutputStream os = response.getOutputStream();
        baos.writeTo(os);
        os.flush();
        os.close();
    }
	
	private static byte[] getEAN13Image(String barCode) throws Exception {
		EAN13 barcode = new EAN13();
        barcode.setData(barCode.substring(0, 12));
        barcode.setUom(IBarcode.UOM_PIXEL);
        // barcode bar module width (X) in pixel
        barcode.setX(2f * scale);
        // barcode bar module height (Y) in pixel
        barcode.setY(40f * scale);

        // barcode image margins
        barcode.setLeftMargin(30f * scale);
        barcode.setRightMargin(30f * scale);
        barcode.setTopMargin(20 * scale);
        barcode.setBottomMargin(15f * scale);

        // barcode image resolution in dpi
        barcode.setResolution(300);

        // disply barcode encoding data below the barcode
        barcode.setShowText(true);
        // barcode encoding data font style
        barcode.setTextFont(new Font("Arial", Font.PLAIN, 22 * scale));
        // space between barcode and barcode encoding data
        barcode.setTextMargin(3 * scale);
        barcode.setTextColor(Color.black);
        barcode.setBarcodeHeight(BARCODE_IMAGE_HEIGHT * scale);
        barcode.setBarcodeWidth(BARCODE_IMAGE_WIDTH * scale);

        
        // barcode bar color and background color in Android device
        barcode.setForeColor(Color.black);
        barcode.setBackColor(Color.white);

        BufferedImage bi = barcode.drawBarcode();
        for (int x = 0; x < bi.getWidth(); x++) {
        	for (int y = 0; y < bi.getHeight(); y++) {
        		int color = bi.getRGB(x, y);
        		if (color != Color.BLACK.getRGB() && color != Color.WHITE.getRGB()) {
        			bi.setRGB(x, y,  bi.getRGB(x, y -1));
        		}
        	}
		}
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "png", baos);
        return baos.toByteArray();
	}
	
	class DottedCells implements PdfPTableEvent {
        @Override
        public void tableLayout(PdfPTable table, float[][] widths,
            float[] heights, int headerRows, int rowStart,
            PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
            canvas.setLineDash(2f, 5f);
            canvas.setLineWidth(0.5f);
            float llx = widths[0][0];
            float urx = widths[0][widths[0].length-1];
            
            for (int i = 0; i < heights.length; i++) {
            	if (i != 0 && i != heights.length - 1) {
            		canvas.moveTo(llx, heights[i]);
            		canvas.lineTo(urx, heights[i]);
            	}
            }
            for (int i = 0; i < widths.length; i++) {
                for (int j = 0; j < widths[i].length; j++) {
                	if (j != 0 && j !=  widths[i].length - 1) {
                		canvas.moveTo(widths[i][j], heights[i]);
                		canvas.lineTo(widths[i][j], heights[i+1]);
                	}
                }
            }
            canvas.stroke();
        }
    }
 
    class DottedCell implements PdfPCellEvent {
        @Override
        public void cellLayout(PdfPCell cell, Rectangle position,
            PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
            canvas.setLineDash(3f, 3f);
            canvas.rectangle(position.getLeft(), position.getBottom(),
                position.getWidth(), position.getHeight());
            canvas.stroke();
        }
    }

}
