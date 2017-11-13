package com.bonc.demo.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.bonc.demo.SysCtlConst;
import com.bonc.demo.handler.ButtonClickHandler;
import com.bonc.demo.handler.SubAutoResHandler;
import com.bonc.demo.handler.TaskHandler;
import com.bonc.demo.matcher.TestMatcher;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxConfig;
import com.soecode.wxtools.api.WxMessageRouter;
import com.soecode.wxtools.api.WxService;
import com.soecode.wxtools.bean.WxXmlMessage;
import com.soecode.wxtools.bean.WxXmlOutMessage;
import com.soecode.wxtools.handler.DemoHandler;
import com.soecode.wxtools.interceptor.DemoInterceptor;
import com.soecode.wxtools.matcher.DemoMatcher;
import com.soecode.wxtools.util.xml.XStreamTransformer;

/**
 * 
 * @author 赵龙
 *
 */
public class HelloWorldController implements Controller {

	private IService iService = new WxService();

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String method = request.getMethod();
		String act = request.getParameter(SysCtlConst.PARAM_ACT);
		System.out.println("method:" + method);
		System.out.println("act" + act);

		if ("vertify".equals(act)) {// 服务器验证或者自动回复(无需返回ModelAndView)
			if ("GET".equals(request.getMethod())) {// 服务器验证
				System.out.println("服务器验证");
				vertify(request, response);
			} else {// 自动回复
				System.out.println("自动回复逻辑");
				autoResponse(request, response);
			}

		} else {
			ModelAndView mv = new ModelAndView("hello");
			// 添加模型数据 可以是任意的POJO对象
			mv.addObject("message", "Hellohello!");
			// 设置逻辑视图名，视图解析器会根据该名字解析到具体的视图页面
			// mv.setViewName("hello");
			return mv;
		}
		return null;

	}

	/**
	 * 自动回复
	 */
	private void autoResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String encrypt_type = request.getParameter("encrypt_type");
		WxMessageRouter router = new WxMessageRouter(iService);
		try {
			if (encrypt_type != null && "aes".equals(encrypt_type)) {// aes加密(代码跟明文基本一致)
//				temp(); 使用的时候将temp方法内的所有代码拷贝过来即可
			} else {// 如果是明文模式，执行以下语句(测试号明文模式)
				// 微信服务器推送过来的是XML格式。 https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140453
				WxXmlMessage wx = XStreamTransformer.fromXml(WxXmlMessage.class, request.getInputStream());
				String eventKey = wx.getEventKey();
				String msgType = wx.getMsgType();
				String status = wx.getStatus();
				String event = wx.getEvent();
				String fromUserName = wx.getFromUserName();
				System.out.println("event: " + wx.getEvent());
				System.out.println("eventKey: " + eventKey);
				System.out.println("msgType: " + msgType);
				System.out.println("status: " + status);
				System.out.println("content: " + wx.getContent());
				System.out.println("fromuser: " + fromUserName);
				System.out.println("touser: " + wx.getToUserName());

				// 首先判断msgType。text为文本，event为事件。
				if ("text".equals(msgType)) {// 接受的是文本，进行自动回复的逻辑
					System.out.println("这是一个回复文本");
					// end()规则终止。next()是指消息进入了一个规则后，如果满足其他规则也能进入，处理。
					router.rule().matcher(new TestMatcher()).interceptor(new DemoInterceptor()).handler(new TaskHandler()).end();
					// 把消息传递给路由器进行处理
					WxXmlOutMessage xmlOutMsg = router.route(wx);
					if (xmlOutMsg != null) {
						out.print(xmlOutMsg.toXml());
					}
				}else if ("event".equals(msgType)) {// 接受的是事件：点击，订阅，跳转网页等（CLICK,VIEW）
					// TEMPLATESENDJOBFINISH消息成功发送 unsubscribe 解除订阅 subscribe 订阅
					if ("CLICK".equals(event)) {// 点击事件
						if ("btn1_key".equals(eventKey)) {// 点击了第一个按钮
							//安琪项目中将发送模板的方法抽取出来,在此发送模板
							router.rule().handler(new ButtonClickHandler()).end();
							// 把消息传递给路由器进行处理
							WxXmlOutMessage xmlOutMsg = router.route(wx);
							if (xmlOutMsg != null)
								out.print(xmlOutMsg.toXml());
						} else {
							System.out.println("其他菜单按钮的点击事件");
						}
					} else if ("subscribe".equals(event)) {// 订阅事件
						router.rule().handler(new SubAutoResHandler()).end();
						// 把消息传递给路由器进行处理
						WxXmlOutMessage xmlOutMsg = router.route(wx);
						if (xmlOutMsg != null)
							out.print(xmlOutMsg.toXml());
					} else if ("TEMPLATESENDJOBFINISH".equals(event) && "success".equals(status)) {
						System.out.println("消息发送成功");
					} else {
						System.out.println("了不得了，你发现了一个新的event：" + event);
					}

				} else {// 接受的是图片 image、voice，video，music，news（图文）
					System.out.println("了不得了，你发现了一个新的msgType：" + msgType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			out.close();
		}

	}

	/**
	 * 微信公众号验证
	 */
	private void vertify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("【doGet】微信公众平台服务器地址测试");

		// 验证服务器有效性
		PrintWriter out = response.getWriter();
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		if (iService.checkSignature(signature, timestamp, nonce, echostr)) {
			out.print(echostr);
		}

	}
	
//	private void temp(){
//		// String signature = request.getParameter("signature");
//		String timestamp = request.getParameter("timestamp");
//		String nonce = request.getParameter("nonce");
//		String msg_signature = request.getParameter("msg_signature");
//
//		// 微信服务器推送过来的加密消息是XML格式。使用WxXmlMessage中的decryptMsg()解密得到明文。
//		WxXmlMessage wx = WxXmlMessage.decryptMsg(request.getInputStream(), WxConfig.getInstance(), timestamp, nonce, msg_signature);
//		System.out.println("密文消息：\n " + wx.toString());
//		String eventKey = wx.getEventKey();
//		String msgType = wx.getMsgType();
//		String status = wx.getStatus();
//		String event = wx.getEvent();
//		String fromUserName = wx.getFromUserName();
//
//		System.out.println("event: " + wx.getEvent());
//		System.out.println("eventKey: " + eventKey);
//		System.out.println("msgType: " + msgType);
//		System.out.println("status: " + status);
//		System.out.println("content: " + wx.getContent());
//		System.out.println("fromuser: " + wx.getFromUserName());
//		System.out.println("touser: " + wx.getToUserName());
//
//		// 首先判断msgType。text为文本，event为事件。
//		if ("text".equals(msgType)) {// 接受的是文本，进行自动回复的逻辑
//			// end()规则终止。next()是指消息进入了一个规则后，如果满足其他规则也能进入，处理。
//			router.rule().matcher(new TestMatcher()).interceptor(new DemoInterceptor()).handler(new TaskHandler()).end();
//			// 把消息传递给路由器进行处理
//			WxXmlOutMessage xmlOutMsg = router.route(wx);
//			if (xmlOutMsg != null) {// 加密后传给公众号后台
//				out.print(WxXmlOutMessage.encryptMsg(WxConfig.getInstance(), xmlOutMsg.toXml(), timestamp, nonce));
//			}
//		} else if ("event".equals(msgType)) {// 接受的是事件：点击，订阅，跳转网页等（CLICK,VIEW）
//			System.out.println("时间点击/订阅/跳转页面等");
//
//		} else {// 接受的是图片 image、voice，video，music，news（图文）
//			System.out.println("接受的是图片 image、voice，video，music，news" + msgType);
//		}
//	}

}
