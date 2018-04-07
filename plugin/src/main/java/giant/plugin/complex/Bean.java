/**
 * User.java
 * com.youku.pluginsdk.bean
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

import giant.iplugin.complex.IBean;

public class Bean implements IBean {

	/**
	 *
	 */
	private String name = "这是来自于插件工程中设置的初始化的名字";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
