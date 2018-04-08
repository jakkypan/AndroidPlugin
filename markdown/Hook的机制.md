# 动态代理 & HOOK原则

动态代理是指运行时动态生成代理类，而不需要像静态代理那样手动的写一个个的代理类。

* Java动态代理
* CGLIB
* Javassist
* ASM

## Java动态代理

Java动态代理会涉及到如下几个方面的东西：

* InvocationHandler接口：Java动态代理中，每一个动态代理类都必须要实现InvocationHandler接口，当我们通过代理对象调用一个方法的时候，这个方法就会被转发给代理类的invoke方法来调用
* Proxy类：实现了InvocationHandler的类是动态代理类，而Proxy就是用来动态创建代理类对象的工具


**动态代理主要解决的是静态代理中每个代理类都实现了特定接口，针对每一个事情都需要去定义一个代理类，会迅速的使类变多。**

### 一个示例

接口定义：

```java
public interface Shopping {
    Object[] doShopping(long money);
}
```

handler：

```java
public class ShoppingHandler implements InvocationHandler {

     Object base;

    public ShoppingHandler(Object base) {
        this.base = base;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if ("doShopping".equals(method.getName())) {
        		return XXX
        }
        
		return method.invoke(base, args);
}
```

proxy：

```java
shopping = (Shopping) Proxy.newProxyInstance(Shopping.class.getClassLoader(),
			shopping.getClass().getInterfaces(), 
			new ShoppingHandler(shopping));
			
// 或者这么写
hopping = (Shopping) Proxy.newProxyInstance(Shopping.class.getClassLoader(),
			new Class[] { Class.forName("XXX.XXX.Shopping"), 
			new ShoppingHandler(shopping));
```

> 需要注意的一点：

Java动态代理只能针对接口创建代理，不能针对类创建代理。


### 实现的原理

无

## Hook的一般原则

寻找Hook点，原则是静态变量或者单例对象，尽量Hook pulic的对象和方法，非public不保证每个版本都一样，需要适配。

选择合适的代理方式，如果是接口可以用动态代理；如果是类可以手动写代理也可以使用cglib。

### 一个Hook的实例

hook住activity的跳转，在跳转之前先做一些自己要做的事情。

首先要分析下怎么做hook？看下activity的`startActivity`，它具体的跳转是：

```java
Instrumentation.ActivityResult ar =
                mInstrumentation.execStartActivity(
                    this, mMainThread.getApplicationThread(), mToken, this,
                    intent, requestCode, options);
```

可以看出是`mInstrumentation`完成的跳转，所以从这个属性进行hook。具体的代码如下：

```java
/**
     * activity的startActivity
     *
     * @param thisActivity
     * @throws Exception
     */
    public static void attachContext(Activity thisActivity) throws Exception {
        // mInstrumentation是在Activity里的私有属性，所以只能一直到Activity类才行
        Class<?> activityClass = thisActivity.getClass().getSuperclass();
        while (!(activityClass.getName().equals("android.app.Activity"))) {
            activityClass = activityClass.getSuperclass();
        }
        // 反射拿到Activity里面的mInstrumentation属性
        Field mInstrumentationField = activityClass.getDeclaredField("mInstrumentation");
        mInstrumentationField.setAccessible(true);
        Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(thisActivity);
        // 将mInstrumentation属性替换成重写的Instrumentation
        Instrumentation evilInstrumentation = new EvilInstrumentation(mInstrumentation);
        mInstrumentationField.set(thisActivity, evilInstrumentation);
        mInstrumentationField.setAccessible(false);
    }
```

这里我们将Instrumentation替换成了我们重写的EvilInstrumentation：

```java
public class EvilInstrumentation extends Instrumentation {

    private static final String TAG = "EvilInstrumentation";

    // ActivityThread中原始的对象, 保存起来
    Instrumentation mBase;

    public EvilInstrumentation(Instrumentation base) {
        mBase = base;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {

        // Hook之前, XXX到此一游!
        Log.e("111", "\n执行了startActivity, 参数如下: \n" + "who = [" + who + "], " +
                "\ncontextThread = [" + contextThread + "], \ntoken = [" + token + "], " +
                "\ntarget = [" + target + "], \nintent = [" + intent +
                "], \nrequestCode = [" + requestCode + "], \noptions = [" + options + "]");

        // 开始调用原始的方法, 调不调用随你,但是不调用的话, 所有的startActivity都失效了.
        // 由于这个方法是隐藏的,因此需要使用反射调用;首先找到这个方法
        try {
            Method execStartActivity = Instrumentation.class.getDeclaredMethod(
                    "execStartActivity",
                    Context.class, IBinder.class, IBinder.class, Activity.class,
                    Intent.class, int.class, Bundle.class);
            execStartActivity.setAccessible(true);
            return (ActivityResult) execStartActivity.invoke(mBase, who,
                    contextThread, token, target, intent, requestCode, options);
        } catch (Exception e) {
            // 某该死的rom修改了  需要手动适配
            throw new RuntimeException("do not support!!! pls adapt it");
        }
    }
}
```

Context里也有startActivity方法，他的逻辑与Activity其实有点不同，它的实现是在`ContextImpl`里：

```java
@Override
    public void startActivity(Intent intent, Bundle options) {
        warnIfCallingFromSystemProcess();
        if ((intent.getFlags()&Intent.FLAG_ACTIVITY_NEW_TASK) == 0) {
            throw new AndroidRuntimeException(
                    "Calling startActivity() from outside of an Activity "
                    + " context requires the FLAG_ACTIVITY_NEW_TASK flag."
                    + " Is this really what you want?");
        }
        mMainThread.getInstrumentation().execStartActivity(
                getOuterContext(), mMainThread.getApplicationThread(), null,
                (Activity) null, intent, -1, options);
    }
```

所以他的反射就需要进行修改：

```java
/**
     * context的startActivity
     *
     * @throws Exception
     */
    public static void attachContext() throws Exception{
        // 先获取到当前的ActivityThread对象
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
        currentActivityThreadMethod.setAccessible(true);
        Object currentActivityThread = currentActivityThreadMethod.invoke(null);
        // 拿到原始的 mInstrumentation字段
        Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
        mInstrumentationField.setAccessible(true);
        Instrumentation mInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);
        // 创建代理对象
        Instrumentation evilInstrumentation = new EvilInstrumentation(mInstrumentation);
        // 偷梁换柱
        mInstrumentationField.set(currentActivityThread, evilInstrumentation);
    }
```


