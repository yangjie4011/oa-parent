package com.ule.oa.admin.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class DocumentUtil {
	
    private Configuration configuration = null;  
    
    public DocumentUtil() {  
        configuration = new Configuration();  
        configuration.setDefaultEncoding("utf-8");  
    }  
   
    public File createDoc(Map<String,Object> dataMap,String fileName) throws UnsupportedEncodingException {  
        //dataMap 要填入模本的数据文件  
        //设置模本装置方法和路径,FreeMarker支持多种模板装载方法。可以重servlet，classpath，数据库装载，  
        //这里我们的模板是放在template包下面  
        configuration.setClassForTemplateLoading(this.getClass(), "/emailTemplet");  
        Template t= null;  
        File outFile = null;
        Writer out = null;  
        FileOutputStream fos=null;  
        OutputStreamWriter oWriter = null;
        try {  
            //test.ftl为要装载的模板  
            t = configuration.getTemplate("employee_record.ftl");  
            outFile = new File(fileName);  
            fos = new FileOutputStream(outFile);  
            oWriter = new OutputStreamWriter(fos,"UTF-8");  
            //这个地方对流的编码不可或缺，使用main（）单独调用时，应该可以，但是如果是web请求导出时导出后word文档就会打不开，并且包XML文件错误。主要是编码格式不正确，无法解析。  
            out = new BufferedWriter(oWriter);  
            if(t!=null) {
            	t.process(dataMap, out);  
            }
        } catch (IOException e) {  
             
        } catch (TemplateException e) {

		}  finally {
        	if(out!=null) {
       		 	try {
					out.close();
				} catch (IOException e) {
				}  
        	}
        	if(fos!=null) {
        		try {
					fos.close();
				} catch (IOException e) {
				}
       		}
        	if(oWriter!=null) {
        		try {
					oWriter.close();
				} catch (IOException e) {
				}
        	}
       }
	  return outFile;  
       
    } 

}
