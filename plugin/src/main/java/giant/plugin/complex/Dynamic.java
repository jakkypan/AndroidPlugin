/**
 * Dynamic1.java
 * com.youku.pluginsdk.imp
 *
 * Function�� TODO 
 *
 *   ver     date      		author
 * ��������������������������������������������������������������������
 *   		 2014-10-20 		Administrator
 *
 * Copyright (c) 2014, TNT All Rights Reserved.
*/

package giant.plugin.complex;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import giant.iplugin.complex.IDynamic;
import giant.iplugin.complex.YKCallBack;
import giant.plugin.R;

public class Dynamic implements IDynamic {
	public void methodWithCallBack(YKCallBack callback) {
		Bean bean = new Bean();
		bean.setName("PLUGIN_SDK_USER");
		callback.callback(bean);
	}

	public void showPluginWindow(Context context) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage("对话框");
//		builder.setTitle(context.getResources().getString(R.string.app_name));
		builder.setTitle("123");
		builder.setNegativeButton("取消", new Dialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		Dialog dialog = builder.create();
		dialog.show();
	}

	public void startPluginActivity(Context context,Class<?> cls){
		/**
		 *这里要注意几点:
		 *1、如果单纯的写一个MainActivity的话，在主工程中也有一个MainActivity，开启的Activity还是主工程中的MainActivity
		 *2、如果这里将MainActivity写成全名的话，还是有问题，会报找不到这个Activity的错误
		 */
		Intent intent = new Intent(context,cls);
		context.startActivity(intent);
	}

	public String getStringForResId(Context context){
		return context.getResources().getString(R.string.app_name);
	}

}

