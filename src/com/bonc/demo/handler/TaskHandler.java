package com.bonc.demo.handler;

import java.util.Map;

import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxMessageHandler;
import com.soecode.wxtools.bean.WxXmlMessage;
import com.soecode.wxtools.bean.WxXmlOutMessage;
import com.soecode.wxtools.exception.WxErrorException;

/**
 * 返回任务信息
 * @author 赵龙
 * 
 */
public class TaskHandler implements WxMessageHandler{

	public WxXmlOutMessage handle(WxXmlMessage wxMessage, Map<String, Object> context, IService iService)
			throws WxErrorException {
		 //必须以build()作为结尾，否则不生效。
		WxXmlOutMessage xmlOutMsg = WxXmlOutMessage.TEXT().content("你好，您今日没有任务，谢谢").toUser(wxMessage.getFromUserName()).fromUser(wxMessage.getToUserName()).build();
		return xmlOutMsg;
	}

}
