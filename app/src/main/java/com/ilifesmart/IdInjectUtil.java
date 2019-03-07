package com.ilifesmart;

import android.app.Activity;
import android.util.Log;
import android.view.View;

import com.wuzh.lib.IdInject;

import java.lang.reflect.Field;

public class IdInjectUtil {

	public static final String TAG = "IdInjectUtil";
	public static void inject(Activity activity) {
		Class clz = activity.getClass();
		Field[] fls = clz.getFields(); // 一个包含某些 Field 对象的数组，该数组包含此 Class 对象所表示的类或接口的所有可访问公共字段

		if (fls != null && fls.length > 0) {
			for(Field field:fls) {
				if (field.isAnnotationPresent(IdInject.class)) {
					IdInject in = field.getAnnotation(IdInject.class); // 获取指定的注解内容
					if (in != null) {
						View v = activity.findViewById(in.value()); // in.value即:R.id.check_box
						if (v != null) {
							try {
								field.setAccessible(true);  // 可能是private
								field.set(activity, v); 		// 设置activity中的field为v,即mCheckBox=v;
							} catch(Exception ex) {
								ex.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
}
