package com.wechat.demo.main;

import com.wechat.demo.wxpay.MyIWXPayConfig;
import com.wechat.demo.wxpay.WXPay;
import com.wechat.demo.wxpay.WXPayUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//统一下单与订单查询测试
public class TestWxPay {

    public static void main(String[] args) throws Exception {
        MyIWXPayConfig config = new MyIWXPayConfig();
        //传入config,自动报告true/false,使用沙盒测试true/false
        WXPay wxpay = new WXPay(config, true, true);


        /*===========================请注释掉一份密钥设定==========================*/
        //不使用沙盒测试时,适用商户平台API密钥
        String APIKey = "88888888888888888888888888888888"; //change this key 商户平台apikey
        config.setKey(APIKey);

        //当使用沙盒测试时,需使用沙盒密钥,而非商户平台API密钥
        String key = WXPayUtil.retrieveSandboxSignKey(config, wxpay);
        System.out.println(key);
        config.setKey(key);
        /*======================================================================*/

        //商户订单生成(请勿重复,推荐时间戳)
        String out_trade_no = "letstryaneWpayWay" + new Date().getTime();

        //可将以下统一下单参数封装为一个实体类进行创建
        Map<String, String> data = new HashMap<String, String>();
//        data.put("appid",config.getAppID()); 通过这样更加方便,首先更改MyIWXPayConfig内参数设定
        data.put("appid", "开通支付的公众服务号appid");//开通支付的公众服务号 appid
        data.put("mch_id", "绑定上面服务号appid的商户平台id"); //绑定上面服务号appid的 商户平台id
        data.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        data.put("body", "test-wechat-pay"); //商品名
        data.put("out_trade_no", out_trade_no); //微信订单号
        data.put("device_info", ""); //设备信息
        data.put("fee_type", "CNY"); //支付币种
        data.put("total_fee", "102");//支付总金额 (分计量)
        data.put("spbill_create_ip", "123.12.12.123"); //请求支付源IP地址
        data.put("notify_url", "http://ngrok.ykmimi.com/wxpay/notify");//回调域名,接受支付成功信息
        data.put("trade_type", "JSAPI");  // 此处指定为扫码支付
        data.put("product_id", "1341121231241000000"); //商品ID
        data.put("openid", "获取的用户openid,JSAPI必须设定"); //获取的用户openid,JSAPI必须设定
        String sign = WXPayUtil.generateSignatrue(data, APIKey); //获取签名
        data.put("sign", sign);

        System.out.println("发送前的total_fee:" + data.get("total_fee"));


        /**
         * 统一下单
         */
        try {
            Map<String, String> resp = wxpay.unifiedOrder(data);
            //验证签名合法性
            boolean payResultNotifySignatureValid = wxpay.isPayResultNotifySignatureValid(resp);
            System.err.println("SignatureValid unifiedOrder:" + payResultNotifySignatureValid);
            String return_code = resp.get("return_code");
            System.err.println("out_trade_no:" + out_trade_no);
            System.err.println("return_code : " + return_code);
            System.err.println("resp 统一下单: " + resp);
        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * 查询订单
         */
        Map<String, String> queryData = new HashMap<String, String>();
        queryData.put("out_trade_no", out_trade_no); //查询的微信订单号
        try {
            Map<String, String> resp1 = wxpay.orderQuery(queryData);
            String total_fee = resp1.get("total_fee");
            System.out.println("查询订单的total_fee:" + data.get("total_fee"));
            //验证签名合法性
            boolean payResultNotifySignatureValid = wxpay.isPayResultNotifySignatureValid(resp1);
            System.err.println("SignatureValid queryOrder:" + payResultNotifySignatureValid);
            System.err.println("out_trade_no:" + out_trade_no);
            System.err.println("resp1 查询订单:" + resp1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
