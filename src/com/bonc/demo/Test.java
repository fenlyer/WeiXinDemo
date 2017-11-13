package com.bonc.demo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.api.WxService;
import com.soecode.wxtools.bean.TemplateSender;
import com.soecode.wxtools.bean.WxMenu;
import com.soecode.wxtools.bean.WxMenu.WxMenuButton;
import com.soecode.wxtools.bean.WxMenu.WxMenuRule;
import com.soecode.wxtools.bean.result.TemplateSenderResult;
import com.soecode.wxtools.exception.WxErrorException;

public class Test {

	public static void main(String[] args) {
		createMenu();
//		sendTaskMessage();
	}

	private static void createMenu() {
		// 实例化 统一业务API入口
		IService iService = new WxService();
		WxMenu menu = new WxMenu();
		List<WxMenuButton> btnList = new ArrayList();

		// 设置CLICK类型的按钮1
		WxMenuButton btn1 = new WxMenuButton();
		btn1.setType(WxConsts.BUTTON_CLICK);
		btn1.setKey("btn1_key");
		btn1.setName("菜单1");
		
		// 设置VIEW类型的按钮2
		WxMenuButton btn2 = new WxMenuButton();
		btn2.setType(WxConsts.BUTTON_VIEW);
		btn2.setUrl("https://aqjmsbgl.natappvip.cc/WeiXinDemo/");
		btn2.setName("菜单2");

		// 设置含有子按钮的按钮3
		WxMenuButton btn3 = new WxMenuButton();
		btn3.setName("一级菜单");
		List<WxMenuButton> subList = new ArrayList();
		WxMenuButton btn3_1 = new WxMenuButton();
		btn3_1.setType(WxConsts.BUTTON_VIEW);
		btn3_1.setName("二级菜单1:跳往百度");
		btn3_1.setUrl("https://www.baidu.com/");
//		try {
//			String url2 = iService.oauth2buildAuthorizationUrl("https://aqjmsbgl.natappvip.cc/wxController.htm?param_act=userDetails", "snsapi_userinfo", "STATE");
//			btn3_1.setUrl(url2);
//		} catch (WxErrorException e2) {
//			e2.printStackTrace();
//		}
		WxMenuButton btn3_2 = new WxMenuButton();
		btn3_2.setType(WxConsts.BUTTON_VIEW);
		btn3_2.setName("二级菜单2:跳往主页");
		btn3_2.setUrl("https://aqjmsbgl.natappvip.cc/WeiXinDemo/");
//		try {
//			String url = iService.oauth2buildAuthorizationUrl("https://aqjmsbgl.natappvip.cc/wxController.htm?param_act=reload", "snsapi_userinfo", "STATE");
//			System.out.println("绑定" + url);
//			btn3_2.setUrl(url);
//		} catch (WxErrorException e1) {
//			e1.printStackTrace();
//		}
		subList.add(btn3_1);
		subList.add(btn3_2);
		btn3.setSub_button(subList);

		// 将三个按钮设置进btnList
		btnList.add(btn1);
		btnList.add(btn2);
		btnList.add(btn3);
		// 设置进菜单类
		menu.setButton(btnList);
		// 调用API即可
		try {
			// 参数1--menu ，参数2--是否是个性化定制。如果是个性化菜单栏，需要设置MenuRule
			iService.createMenu(menu, false);
		} catch (WxErrorException e) {
			e.printStackTrace();
		}

//		// 个性化菜单栏
//		WxMenuRule rule = new WxMenuRule();
//		rule.setCountry("中国");
//		rule.setProvince("广东");
//		menu.setMatchrule(rule);
//
//		try {
//			// 参数1--menu ，参数2--是否是个性化定制。如果是个性化菜单栏，需要设置MenuRule
//			iService.createMenu(menu, true);
//		} catch (WxErrorException e) {
//			e.printStackTrace();
//		}
	}
}
