package com.ule.oa.common.utils;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontStyle;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;

/**
  * @ClassName: PDFUtil
  * @Description: 生成pdf工具类
  * @author minsheng
  * @date 2017年10月18日 下午4:24:12
 */
public class PDFUtils {
	public static final Integer ALIGN_LEFT = 0;//居左显示
	public static final Integer ALIGN_CENTER = 1;//居中显示
	public static final Integer ALIGN_RIGHT = 2;//居右显示
	
	/**
	  * setChineseFont(设置中文字体---默认中文不显示)
	  * @Title: setChineseFont
	  * @Description: 设置中文字体---默认中文不显示
	  * @return Font
	  * @throws DocumentException
	  * @throws IOException    设定文件
	  * Font    返回类型
	  * @throws
	 */
	public static Font setChineseFont() throws DocumentException, IOException{   
		BaseFont bfChinese = BaseFont.createFont("STSong-Light","UniGB-UCS2-H",BaseFont.NOT_EMBEDDED);
		//BaseFont bfChinese = BaseFont.createFont("C:/Windows/Fonts/seguisym.ttf",BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED);
		Font font = new Font(bfChinese);
		
		return font;
	}
	
	/**
	  * setFontSize(设置字体大小，支持中文显示)
	  * @Title: setFontSize
	  * @Description: 设置字体大小，支持中文显示
	  * @param size
	  * @return
	  * @throws DocumentException
	  * @throws IOException    设定文件
	  * Font    返回类型
	  * @throws
	 */
	public static Font setFontSize(float size) throws DocumentException, IOException{
		Font font = setChineseFont();
		if(size >= 0){
			font.setSize(size);
		}
		font.setColor(BaseColor.BLACK);
		
		return font;
	}
	
	/**
	  * setParagraph(添加内容)
	  * @Title: setParagraph
	  * @Description: 添加内容
	  * @param content		内容
	  * @param fontSize		字体大小
	  * @param fontStyle	字体样式(例如：粗体，斜体)
	  * @param align		对齐方式(0:左对齐，1：居中对齐，3：右对齐)
	  * @return
	  * @throws DocumentException
	  * @throws IOException    设定文件
	  * Paragraph    返回类型
	  * @throws
	 */
	public static Paragraph setParagraph(String content,float fontSize,String fontStyle,Integer align) throws DocumentException, IOException{
		Font font = setFontSize(fontSize);
		if(!StringUtils.isBlank(fontStyle)){
			font.setStyle(fontStyle);
		}else{
			font.setStyle(FontStyle.NORMAL.getValue());
		}
		Paragraph paragraph = new Paragraph(content,font);
		paragraph.setAlignment(align);
		
		return paragraph;
	}
	
	/**
	  * createTable(这里用一句话描述这个方法的作用)
	  * @Title: createTable
	  * @Description: 建表
	  * @param columnSize
	  * @param width
	  * @param paddingTop
	  * @param paddingBottom
	  * @return    设定文件
	  * PdfPTable    返回类型
	  * @throws
	 */
	public static PdfPTable createTable(int columnSize,Integer width,float paddingTop,float paddingBottom){
		PdfPTable table = new PdfPTable(columnSize); 
		table.setWidthPercentage(width); 		// 宽度100%填充
		table.setSpacingBefore(paddingTop); 	// 前间距
		table.setSpacingAfter(paddingBottom); 	// 后间距
		
		return table;
	}
}
