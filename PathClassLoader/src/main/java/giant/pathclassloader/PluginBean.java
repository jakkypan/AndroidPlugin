package giant.pathclassloader;

/**
 * Created by 晓勇 on 2015/7/27 0027.
 * 插件的实体类
 */
public class PluginBean {
    //插件的名称
    private String label ;
    //插件的包名
    private String packageName;

    public PluginBean() {
    }

    public PluginBean(String label, String packageName) {
        this.label = label;
        this.packageName = packageName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
