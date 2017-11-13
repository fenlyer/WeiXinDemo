package com.bonc.demo.matcher;

import com.soecode.wxtools.api.WxMessageMatcher;
import com.soecode.wxtools.bean.WxXmlMessage;

/**
 * 
 * @author 赵龙
 * 拦截器，拦截了“任务”
 */
public class TestMatcher implements WxMessageMatcher {

	public boolean match(WxXmlMessage message) {
		if ("任务".equals(message.getContent())) {
			return true;
		}else if(message.getContent() != null && message.getContent().indexOf("任务") != -1){
			return true;
		}
		return false;
	}

}
