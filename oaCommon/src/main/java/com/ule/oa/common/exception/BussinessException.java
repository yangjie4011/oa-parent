package com.ule.oa.common.exception;

/**
  * @ClassName: BussinessException
  * @Description: 业务异常类
  * @author minsheng
  * @date 2017年10月12日 上午9:07:25
 */
public class BussinessException extends RuntimeException {

	  /**
	  * @Fields serialVersionUID : TODO（用一句话描述这个变量表示什么）
	  */
	
	private static final long serialVersionUID = -6298172082388027872L;

	private int code;
    private String msg;
    
    public BussinessException(String msg) {
        super();
        this.msg = msg;
    }

    public BussinessException(int code, String msg) {
        super();
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
