package com.ggccnu.tinynote.util;

import org.json.JSONException;
import org.json.JSONObject;

public class BaiduLocationDecode {
	/**
	 * 解析服务器返回的地理位置json数据，百度返回的数据非JSON格式的数据，需要先处理下，转化为JSON格式的数据，再提取想要的位置信息。
	 *我需要的是city，district。他们在addressComponent这个键的值里面。先得到JSON对象result,再得到JSON对象addressComponet。
	 *最后从中取出city,district
	 */
	public static String locationFromResponse(String response) {
		try {
			// 转换为jsonObj数据
			// JSON数据中有分隔符，所以split加上了limit参数，2表示分割后的数组个数
			String jsonObj = response.split("\\(", 2)[1];
			// JSON数据中有分隔符，所以用substring方法
			String subJsonObj = jsonObj.substring(0, jsonObj.length() - 1);
			JSONObject jsonObject = new JSONObject(subJsonObj);
			JSONObject result = jsonObject.getJSONObject("result");
			JSONObject addressComponent = result.getJSONObject("addressComponent");
			String city = addressComponent.getString("city");
			String district = addressComponent.getString("district");
			return city+district;
		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}
}