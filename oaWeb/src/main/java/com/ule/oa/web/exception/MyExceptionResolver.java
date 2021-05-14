package com.ule.oa.web.exception;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.ule.oa.common.exception.BussinessException;
import com.ule.oa.web.vo.ResultErrorVo;

/**
  * @ClassName: MyExceptionResolver
  * @Description: 全局异常处理器
  * @author minsheng
  * @date 2017年10月12日 上午8:46:29
 */
public class MyExceptionResolver implements HandlerExceptionResolver {
	private final static Integer BUSSINESS_EX_CODE = 100;
	private final static Integer BIND_EX_CODE = 100;
	private final static Integer OTHER_EX_CODE = 9999;

	/**
	  * resolveException(通过controller抛出不同的异常信息做不同的业务处理)
	  * @Title: resolveException
	  * @Description: 通过controller抛出不同的异常信息做不同的业务处理
	  * @param request
	  * @param response
	  * @param handler
	  * @param ex
	  * @return    设定文件
	  * @throws
	 */
	@Override
	public ModelAndView resolveException(HttpServletRequest request,HttpServletResponse response, Object handler, Exception ex) {
		ResultErrorVo result = new ResultErrorVo();
        StringBuilder sb = new StringBuilder();
		ModelAndView view = new ModelAndView();
		
        if(ex instanceof BussinessException) {//业务异常
        	//默认跳转的页面
        	resolverBussinessException(ex, sb, result);
        } else if (ex instanceof BindException) {//参数绑定异常
        	resolverBindException(ex, sb, result);
        } else {//其它异常
        	resolverOtherException(ex, sb, result);
        }
		
        result.setRemark(sb.toString());
        view.addObject("result", result);
        view.setViewName("error/error");
		return view;
	}
	
	/**
	  * resolverBussinessException(业务异常)
	  * @Title: resolverBussinessException
	  * @Description: 业务异常
	  * @param ex
	  * @param sb
	  * @param result    设定文件
	  * void    返回类型
	  * @throws
	 */
	private void resolverBussinessException(Exception ex, StringBuilder sb, ResultErrorVo result) {
        BussinessException businessException = (BussinessException) ex;
        sb.append(businessException.getMsg());
        
        result.setCode(BUSSINESS_EX_CODE);
        result.setMsg("业务异常");
    }
	
	/**
	  * resolverBindException(参数绑定错误异常)
	  * @Title: resolverBindException
	  * @Description: 参数绑定错误异常
	  * @param ex
	  * @param sb
	  * @param ResultErrorVo    设定文件
	  * void    返回类型
	  * @throws
	 */
    private void resolverBindException(Exception ex, StringBuilder sb, ResultErrorVo result) {
        BindException be = (BindException) ex;
        List<FieldError> errorList = be.getBindingResult().getFieldErrors();
        for (FieldError error : errorList) {
            sb.append(error.getObjectName());
            sb.append("对象的");
            sb.append(error.getField());
            sb.append("字段");
            sb.append(error.getDefaultMessage());
        }
        
        result.setCode(BIND_EX_CODE);
        result.setMsg("参数传递异常");
    }
    
    /**
      * resolverOtherException(其它异常)
      * @Title: resolverOtherException
      * @Description: 其它异常
      * @param ex
      * @param sb
      * @param result    设定文件
      * void    返回类型
      * @throws
     */
    private void resolverOtherException(Exception ex, StringBuilder sb, ResultErrorVo result) {
        sb.append(ex.getMessage());
        
        result.setCode(OTHER_EX_CODE);
        result.setMsg("其他异常");
    }
}

