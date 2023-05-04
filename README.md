# bsx-EZrpc

基于spring，zookeeper，netty的轻量级rpc框架
## 使用

### 服务端

```java
//提供的服务的接口
public interface Hello {
    String talk(String msg);
}
//实现
@Component
@RpcService(value = Hello.class, version = "A")
public class AHello implements Hello {
    @Override
    public String talk(String msg) {
        return "A got the msg:" + msg + ", and A said hi!";
    }
}

@Component
@RpcService(value = Hello.class, version = "A")
public class AHello implements Hello {
    @Override
    public String talk(String msg) {
        return "A got the msg:" + msg + ", and A said hi!";
    }
}
```

```java
//将Rpc服务器注入容器
@Configuration
public class RpcConfig {
    @Bean
    public RpcServer getRpcServer() {
        //服务器地址和zookeeper地址
        return new RpcServer("127.0.0.1:6184", "127.0.0.1:2181");
    }
}
```
```java
//启动
@Configuration
@ComponentScan
public class ServerConfig {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ServerConfig.class);
    }
}
```

### 客户端
```java
//定义需要远程调用的接口
public interface Hello {
    String talk(String msg);
}
```
```java
//用RpcAutowired注入
@Component
public class ServiceRun {
    @RpcAutowired(version = "A")
    private Hello helloA;

    @RpcAutowired(version = "B")
    private Hello helloB;

    public void run() {
        new Thread(() -> {
            int c = 5;
            while (c-- > 0) {
                System.out.println(helloA.talk("client thread 1"));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("thread 1 ended");
        }).start();

        new Thread(() -> {
            int c = 5;
            while (c-- > 0) {
                System.out.println(helloB.talk("client thread 2"));
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("thread 2 ended");

        }).start();
    }
}
```
```java
//注入客户端服务器
@Configuration
public class RpcConfig {
    @Bean
    public RpcClient getClient() {
        //zookeeper地址
        return new RpcClient("127.0.0.1:2181");
    }
}
```
```java
//启动
@Configuration
@ComponentScan
public class ClientConfig {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ClientConfig.class);
        ServiceRun bean = context.getBean(ServiceRun.class);
        bean.run();
    }

}
```